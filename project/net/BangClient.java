package net;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * Non-blocking client.
 *
 * Thread model
 * ┌─────────────────────────────────────────────────────────────┐
 * │  Lanterna thread  → sendAction(json)  → outQueue            │
 * │                   ← getState()        ← stateRef (atomic)   │
 * │                                                             │
 * │  bang-sender  drains outQueue → socket                      │
 * │  bang-receiver  socket → Protocol.parseStateUpdate → stateRef│
 * └─────────────────────────────────────────────────────────────┘
 * The Lanterna render/input loop never touches a socket directly.
 */
public class BangClient {

    private Socket  socket;
    private PrintWriter writer;

    private final AtomicReference<ClientGameState> stateRef =
            new AtomicReference<>(new ClientGameState());

    private final BlockingQueue<String> outQueue = new LinkedBlockingQueue<>();
    private volatile boolean running = false;

    // -------------------------------------------------------------------------

    public void connect(String host, int port, String playerName) throws IOException {
        socket = new Socket(host, port);
        socket.setTcpNoDelay(true);
        writer = new PrintWriter(
            new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
        running = true;

        // --- Receiver thread: reads newline-delimited JSON from server ---
        Thread receiver = new Thread(() -> {
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), "UTF-8"))) {
                String line;
                while (running && (line = in.readLine()) != null) {
                    ClientGameState next = Protocol.parseStateUpdate(line);
                    stateRef.set(next);
                }
            } catch (IOException e) {
                if (running) System.err.println("[BangClient] receiver: " + e.getMessage());
            } finally {
                running = false;
            }
        }, "bang-receiver");
        receiver.setDaemon(true);
        receiver.start();

        // --- Sender thread: drains outQueue and writes to socket ---
        Thread sender = new Thread(() -> {
            try {
                while (running) {
                    String msg = outQueue.poll(1, TimeUnit.SECONDS);
                    if (msg != null) writer.println(msg);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "bang-sender");
        sender.setDaemon(true);
        sender.start();

        // Send JOIN immediately
        sendAction("{\"type\":\"JOIN\",\"name\":\"" + playerName + "\"}");
    }

    /** Queue a JSON action; returns immediately (never blocks the UI). */
    public void sendAction(String json) {
        outQueue.offer(json);
    }

    /** Snapshot of latest server state; always non-null. */
    public ClientGameState getState() {
        return stateRef.get();
    }

    public boolean isConnected() {
        return running && socket != null && !socket.isClosed();
    }

    public void disconnect() {
        running = false;
        try { if (socket != null) socket.close(); } catch (IOException ignored) {}
    }
}
