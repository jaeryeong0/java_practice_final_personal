package net;

import game.GameLogic.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Authoritative multiplayer server.
 *
 * Thread model
 * ┌────────────────────────────────────────────────────────────────┐
 * │  main thread       ServerSocket.accept() loop                  │
 * │  client-N thread   ClientHandler.run() – reads actions,        │
 * │                    calls synchronized handleAction()           │
 * │                    which mutates Game and broadcasts state      │
 * └────────────────────────────────────────────────────────────────┘
 */
public class BangServer {

    private final int  port;
    private final int  maxPlayers;

    // All game state mutations happen inside this lock (called from multiple client threads)
    private final Object gameLock = new Object();
    // CopyOnWriteArrayList lets broadcast loops iterate safely while handlers connect/disconnect
    private final List<ClientHandler> handlers   = new CopyOnWriteArrayList<>();
    private final List<String>        lobbyNames = new ArrayList<>();
    private final List<String>        lobbyChat  = new ArrayList<>();
    private Game game = null; // null until host sends START_GAME

    public BangServer(int port, int maxPlayers) {
        this.port       = port;
        this.maxPlayers = maxPlayers;
    }

    // -------------------------------------------------------------------------

    public void start() {
        System.out.println("[Server] Listening on port " + port +
                           " (need " + maxPlayers + " players)");
        try (ServerSocket ss = new ServerSocket(port)) {
            while (true) {
                Socket sock = ss.accept();
                sock.setTcpNoDelay(true);
                ClientHandler h = new ClientHandler(sock, this);
                handlers.add(h);
                new Thread(h, "client-" + handlers.size()).start();
                System.out.println("[Server] Connected: " + sock.getRemoteSocketAddress());
            }
        } catch (IOException e) {
            System.err.println("[Server] Fatal: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Action dispatch (all mutations happen inside gameLock)
    // -------------------------------------------------------------------------

    void handleAction(ClientHandler h, Map<String, Object> action) {
        synchronized (gameLock) {
            String type = str(action, "type");

            // ---- JOIN (lobby phase) ----------------------------------------
            if ("JOIN".equals(type)) {
                if (h.playerIdx >= 0) return; // already registered
                String name = str(action, "name");
                if (name.isEmpty()) name = "Player" + (lobbyNames.size() + 1);
                h.playerIdx = lobbyNames.size();
                lobbyNames.add(name);
                System.out.println("[Server] Joined: " + lobbyNames.get(h.playerIdx)
                    + " (" + lobbyNames.size() + "/" + maxPlayers + ")");
                broadcastLobby();
                return;
            }

            // ---- START_GAME (host only, 4~7 players) -----------------------
            if ("START_GAME".equals(type)) {
                if (h.playerIdx != 0) return;
                if (game == null && lobbyNames.size() >= 4) startGame();
                return;
            }

            // ---- CHAT (lobby phase) ----------------------------------------
            if ("CHAT".equals(type)) {
                if (h.playerIdx >= 0 && game == null) {
                    String msg = str(action, "msg");
                    if (!msg.isEmpty()) {
                        lobbyChat.add(msg);
                        broadcastLobby();
                    }
                }
                return;
            }

            if (game == null || h.playerIdx < 0) return;

            // ---- General Store (any alive player picks in order) ------------
            if (game.getState() == GameState.GENERAL_STORE) {
                if (h.playerIdx != game.getGeneralStorePickerIdx()) return;
                if ("PICK_STORE".equals(type))
                    game.pickGeneralStoreCard(num(action, "poolIdx"));
                broadcastState();
                return;
            }

            // Cat Balou discard-area choice (current player only, during sub-selection)
            if ("CAT_BALOU_PICK".equals(type)) {
                if (game != null
                        && game.getState() == GameState.SELECT_CAT_BALOU
                        && h.playerIdx == game.getCurrentPlayerIdx()) {
                    game.confirmCatBalou(num(action, "choiceIdx"));
                    broadcastState();
                }
                return;
            }

            // Sid Ketchum can heal at any time, even outside their own turn
            if ("SID_HEAL".equals(type)) {
                if (h.playerIdx >= 0 && h.playerIdx < game.players.size()) {
                    game.sidKetchumHeal(game.players.get(h.playerIdx));
                    broadcastState();
                }
                return;
            }

            // ---- Normal turn actions (only current player) ------------------
            if (h.playerIdx != game.getCurrentPlayerIdx()) return;
            switch (type) {
                case "PLAY_CARD":      game.tryPlayCard(num(action, "cardIdx")); break;
                case "CONFIRM_TARGET": game.confirmTarget(num(action, "targetIdx")); break;
                case "CANCEL_TARGET":  game.cancelTarget(); break;
                case "END_TURN":       game.endTurn(); break;
                default: return;
            }
            broadcastState();
        }
    }

    // -------------------------------------------------------------------------

    private void startGame() {
        List<Player> players = new ArrayList<>();
        for (String name : lobbyNames) players.add(new Player(name));
        game = new Game(players);
        game.startGame();
        System.out.println("[Server] Game started with " + players.size() + " players.");
        broadcastState();
    }

    private void broadcastState() {
        for (ClientHandler h : handlers) {
            if (h.playerIdx >= 0 && h.playerIdx < game.players.size())
                h.send(Protocol.buildStateForPlayer(game, h.playerIdx));
        }
    }

    private void broadcastLobby() {
        for (ClientHandler h : handlers) {
            if (h.playerIdx >= 0)
                h.send(Protocol.buildLobby(lobbyNames, maxPlayers, h.playerIdx, lobbyChat));
        }
    }

    void removeHandler(ClientHandler h) {
        handlers.remove(h);
        synchronized (gameLock) {
            int leavingIdx = h.playerIdx;
            // Mid-game disconnects are ignored to keep game state consistent.
            // Lobby departures compact the name list and shift indices for all later handlers.
            if (leavingIdx >= 0 && game == null && leavingIdx < lobbyNames.size()) {
                lobbyNames.remove(leavingIdx);
                for (ClientHandler other : handlers) {
                    if (other.playerIdx > leavingIdx) {
                        other.playerIdx--;
                    }
                }
                broadcastLobby();
            }
        }
        System.out.println("[Server] Disconnected player " + h.playerIdx);
    }

    // -------------------------------------------------------------------------

    private static String str(Map<String, Object> m, String k) {
        Object v = m.get(k); return v == null ? "" : v.toString();
    }
    private static int num(Map<String, Object> m, String k) {
        Object v = m.get(k); return (v instanceof Number) ? ((Number) v).intValue() : 0;
    }

    // =========================================================================
    // Inner class: one thread per connected client
    // =========================================================================

    static class ClientHandler implements Runnable {
        private final Socket     socket;
        private final BangServer server;
        private PrintWriter writer;
        volatile int playerIdx = -1;

        ClientHandler(Socket socket, BangServer server) {
            this.socket = socket;
            this.server = server;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), "UTF-8"))) {
                writer = new PrintWriter(
                    new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
                String line;
                while ((line = in.readLine()) != null) {
                    Map<String, Object> action = Protocol.parseAction(line);
                    server.handleAction(this, action);
                }
            } catch (IOException e) {
                System.err.println("[ClientHandler] " + e.getMessage());
            } finally {
                server.removeHandler(this);
                try { socket.close(); } catch (IOException ignored) {}
            }
        }

        void send(String json) {
            PrintWriter w = writer;
            if (w != null) w.println(json);
        }
    }
}
