package scene;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import java.io.IOException;

import net.BangClient;
import net.ClientGameState;

public class WaitingRoomScene implements Scene {

    private final BangClient client;

    public WaitingRoomScene(BangClient client) {
        this.client = client;
    }

    private static final int COLS = 270;
    private static final int ROWS = 70;

    private static final int COL_DIV1 = 80;
    private static final int COL_DIV2 = 180;

    private static final int ROW_SECT_DIV   = 2;
    private static final int ROW_SECT_HDR   = 3;
    private static final int ROW_CONTENT    = 4;
    private static final int ROW_CHAT_SEP   = 58;
    private static final int ROW_CHAT_INPUT = 59;
    private static final int ROW_BTM_DIV    = 60;
    private static final int ROW_BTM_BAR    = 61;
    private static final int ROW_BTM_BORDER = 62;

    private StringBuilder chatInput = new StringBuilder();
    private int           tickCount;

    // --- lifecycle ---

    @Override
    public void enter() {
        tickCount = 0;
        chatInput.setLength(0);
    }

    @Override
    public void exit() {}

    // --- input ---

    @Override
    public void handleInput(KeyStroke key) throws IOException {
        KeyType type = key.getKeyType();
        ClientGameState cs = client.getState();
        if (type == KeyType.F2 || type == KeyType.Escape) {
            boolean wasHost = cs.myPlayerIdx == 0;
            client.disconnect();
            SceneManager.getInstance().changeScene(new NicknameScene(wasHost));
        } else if (type == KeyType.F5) {
            if (cs.myPlayerIdx == 0 && cs.lobbyNames.size() >= 4) {
                client.sendAction("{\"type\":\"START_GAME\"}");
            }
        } else if (type == KeyType.Enter) {
            if (chatInput.length() > 0) sendChat();
        } else if (type == KeyType.Backspace) {
            if (chatInput.length() > 0) chatInput.deleteCharAt(chatInput.length() - 1);
        } else if (type == KeyType.Character) {
            if (chatInput.length() < 50) chatInput.append(key.getCharacter());
        }
    }

    private void sendChat() {
        ClientGameState cs = client.getState();
        String name = cs.myPlayerIdx < cs.lobbyNames.size() ? cs.lobbyNames.get(cs.myPlayerIdx) : "?";
        String msg = name + ": " + chatInput.toString();
        // Manually escape backslash and double-quote so the message is valid inside the JSON string
        String escaped = msg.replace("\\", "\\\\").replace("\"", "\\\"");
        client.sendAction("{\"type\":\"CHAT\",\"msg\":\"" + escaped + "\"}");
        chatInput.setLength(0);
    }

    // --- render ---

    @Override
    public void render(TextGraphics tg) {
        // Server pushes a non-LOBBY state update when the host starts the game;
        // the render loop detects this and transitions the client to GamePlayScene automatically.
        ClientGameState cs = client.getState();
        if (!cs.isLobby()) {
            SceneManager.getInstance().changeScene(new GamePlayScene(client));
            return;
        }

        tickCount++;
        tg.fillRectangle(new TerminalPosition(0, 0), new TerminalSize(COLS, ROWS), ' ');
        renderBorders(tg);
        renderTitle(tg);
        renderSectionHeaders(tg, cs);
        renderParticipants(tg, cs);
        renderChat(tg, cs);
        renderInfo(tg);
        renderBottomBar(tg, cs);
    }

    private void renderBorders(TextGraphics tg) {
        tg.putString(0, 0, "┌" + "─".repeat(COLS - 2) + "┐");
        tg.putString(0, ROW_BTM_BORDER, "└" + "─".repeat(COLS - 2) + "┘");
        for (int r = 1; r < ROW_BTM_BORDER; r++) {
            tg.putString(0,        r, "│");
            tg.putString(COLS - 1, r, "│");
        }

        // section divider with column joints
        tg.putString(0, ROW_SECT_DIV,
            "├" + "─".repeat(COL_DIV1 - 1) + "┬" +
            "─".repeat(COL_DIV2 - COL_DIV1 - 1) + "┬" +
            "─".repeat(COLS - COL_DIV2 - 2) + "┤");

        // bottom content divider (full width)
        tg.putString(0, ROW_BTM_DIV, "├" + "─".repeat(COLS - 2) + "┤");

        tg.putString(1, 0, " WaitingRoomScene ");

        // column dividers
        for (int r = ROW_SECT_DIV + 1; r < ROW_BTM_DIV; r++) {
            if (r == ROW_CHAT_SEP) {
                // separator only in center column (chat input box top)
                tg.putString(COL_DIV1, r,
                    "├" + "─".repeat(COL_DIV2 - COL_DIV1 - 1) + "┤");
            } else {
                tg.putString(COL_DIV1, r, "│");
                tg.putString(COL_DIV2, r, "│");
            }
        }
    }

    private void renderTitle(TextGraphics tg) {
        String title = "=== THE SALOON (Waiting Room) ===";
        tg.putString((COLS - title.length()) / 2, 1, title);
    }

    private void renderSectionHeaders(TextGraphics tg, ClientGameState cs) {
        int joined = cs.lobbyNames.size();
        int max    = cs.maxPlayers;
        tg.putString(2,            ROW_SECT_HDR, "[Players " + joined + "/" + max + "]");
        tg.putString(COL_DIV1 + 2, ROW_SECT_HDR, "[Chat]");
        tg.putString(COL_DIV2 + 2, ROW_SECT_HDR, "[Game Info / Tips]");
    }

    private void renderParticipants(TextGraphics tg, ClientGameState cs) {
        int slots = cs.maxPlayers;
        for (int i = 0; i < slots; i++) {
            int    r    = ROW_CONTENT + i * 2;
            String line;
            if (i < cs.lobbyNames.size()) {
                String name = cs.lobbyNames.get(i);
                String icon = (i == 0) ? "[H]" : "[ ]";
                String me   = (i == cs.myPlayerIdx) ? " <me>" : "";
                line = "  " + icon + " " + name + " (Waiting)" + me;
            } else {
                line = "  - Empty Slot -";
            }
            tg.putString(1, r, line);
        }
    }

    private void renderChat(TextGraphics tg, ClientGameState cs) {
        int innerWidth = COL_DIV2 - COL_DIV1 - 2;
        int maxVisible = ROW_CHAT_SEP - ROW_CONTENT;
        int msgCount = cs.chatMessages.size();
        int start = Math.max(0, msgCount - maxVisible);
        for (int i = start; i < msgCount; i++) {
            String msg = cs.chatMessages.get(i);
            if (msg.length() > innerWidth) msg = msg.substring(0, innerWidth);
            tg.putString(COL_DIV1 + 1, ROW_CONTENT + (i - start), msg);
        }

        String prompt = "> " + chatInput;
        tg.putString(COL_DIV1 + 1, ROW_CHAT_INPUT, prompt);
        if ((tickCount / 15) % 2 == 0) {
            tg.putString(COL_DIV1 + 1 + prompt.length(), ROW_CHAT_INPUT, "_");
        }
    }

    private void renderInfo(TextGraphics tg) {
        String[] lines = {
            "* Role Cards",
            "- Sheriff  : eliminate outlaws",
            "- Deputy   : protect the sheriff",
            "- Outlaw   : eliminate the sheriff",
            "- Renegade : be the last one standing",
            "",
            "* Controls",
            "- [Arrow]  : select card",
            "- [Space]  : use card",
            "- [Tab]    : change target",
        };
        for (int i = 0; i < lines.length; i++) {
            tg.putString(COL_DIV2 + 2, ROW_CONTENT + i, lines[i]);
        }
    }

    private void renderBottomBar(TextGraphics tg, ClientGameState cs) {
        String bar;
        if (cs.myPlayerIdx == 0) {
            int count = cs.lobbyNames.size();
            bar = count >= 4
                ? "[F2] Leave      [F5] Start Game      [ENTER] Chat"
                : "[F2] Leave      Waiting for players... (" + count + "/4 minimum)      [ENTER] Chat";
        } else {
            bar = "[F2] Leave      [ENTER] Chat";
        }
        tg.putString((COLS - bar.length()) / 2, ROW_BTM_BAR, bar);
    }
}
