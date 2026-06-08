package scene;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.graphics.TextImage;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import net.BangClient;
import net.BangServer;
import tools.Assets;
import tools.Util;

import java.io.IOException;

public class TitleScene implements Scene {

    private static final int SCREEN_COLS = 270;
    private static final int SCREEN_ROWS = 70;

    // MAIN_MENU: title screen with HOST/JOIN button
    // PORT_INPUT: host types a port number to start the server on
    // IP_INPUT:   client types "host:port" to connect to an existing server
    private enum State { MAIN_MENU, PORT_INPUT, IP_INPUT }

    // --- constructor params ---

    private final boolean isHost;
    private final String nickname;

    public TitleScene(boolean isHost, String nickname) {
        this.isHost   = isHost;
        this.nickname = nickname;
    }

    // --- scene state ---

    private State state;
    private final StringBuilder ipInput   = new StringBuilder();
    private final StringBuilder portInput = new StringBuilder();
    private int tickCount;

    // --- lifecycle ---

    @Override
    public void enter() {
        state = State.MAIN_MENU;
        ipInput.setLength(0);
        portInput.setLength(0);
        tickCount = 0;
    }

    @Override
    public void exit() {}

    // --- input ---

    @Override
    public void handleInput(KeyStroke key) throws IOException {
        if (state == State.MAIN_MENU) {
            handleMainMenuInput(key);
        } else if (state == State.PORT_INPUT) {
            handlePortPopupInput(key);
        } else {
            handleIpPopupInput(key);
        }
    }

    private void handleMainMenuInput(KeyStroke key) {
        if (key.getKeyType() == KeyType.Enter) {
            if (isHost) {
                state = State.PORT_INPUT;
                portInput.setLength(0);
            } else {
                state = State.IP_INPUT;
                ipInput.setLength(0);
            }
        }
    }

    private void handlePortPopupInput(KeyStroke key) {
        if (key.getKeyType() == KeyType.Escape) {
            state = State.MAIN_MENU;
        } else if (key.getKeyType() == KeyType.Enter) {
            try {
                int port = portInput.length() > 0 ? Integer.parseInt(portInput.toString().trim()) : 12345;
                Thread st = new Thread(() -> new BangServer(port, 7).start(), "bang-server");
                st.setDaemon(true);
                st.start();
                // Brief sleep gives the ServerSocket time to bind before we connect as the host
                Thread.sleep(200);
                BangClient client = new BangClient();
                client.connect("localhost", port, nickname);
                SceneManager.getInstance().changeScene(new WaitingRoomScene(client));
            } catch (NumberFormatException e) {
                System.err.println("[TitleScene] Invalid port: " + portInput);
            } catch (Exception e) {
                System.err.println("[TitleScene] Host error: " + e.getMessage());
                state = State.MAIN_MENU;
            }
        } else if (key.getKeyType() == KeyType.Backspace) {
            if (portInput.length() > 0) portInput.deleteCharAt(portInput.length() - 1);
        } else if (key.getKeyType() == KeyType.Character) {
            if (portInput.length() < 5) portInput.append(key.getCharacter());
        }
    }

    private void handleIpPopupInput(KeyStroke key) {
        if (key.getKeyType() == KeyType.Escape) {
            state = State.MAIN_MENU;
        } else if (key.getKeyType() == KeyType.Enter) {
            // JOIN: connect to the typed host:port
            try {
                String raw = ipInput.toString().trim();
                String host;
                int port;
                // Parse "host:port" — lastIndexOf handles IPv6 addresses with colons in the host part
                int colonIdx = raw.lastIndexOf(':');
                if (colonIdx > 0) {
                    host = raw.substring(0, colonIdx);
                    port = Integer.parseInt(raw.substring(colonIdx + 1));
                } else {
                    // No port specified — fall back to default
                    host = raw;
                    port = 12345;
                }
                BangClient client = new BangClient();
                client.connect(host, port, nickname);
                SceneManager.getInstance().changeScene(new WaitingRoomScene(client));
            } catch (Exception e) {
                System.err.println("[TitleScene] Join error: " + e.getMessage());
                state = State.MAIN_MENU;
            }
        } else if (key.getKeyType() == KeyType.Backspace) {
            if (ipInput.length() > 0) {
                ipInput.deleteCharAt(ipInput.length() - 1);
            }
        } else if (key.getKeyType() == KeyType.Character) {
            if (ipInput.length() < 50) {
                ipInput.append(key.getCharacter());
            }
        }
    }

    // --- render ---

    @Override
    public void render(TextGraphics graphics) {
        tickCount++;
        graphics.fillRectangle(
            new TerminalPosition(0, 0),
            new TerminalSize(SCREEN_COLS, SCREEN_ROWS),
            ' '
        );
        graphics.putString(0, 0, "[ TitleScene ]");
        renderTitle(graphics);
        renderButtons(graphics);
        renderInstructions(graphics);
        if (state == State.PORT_INPUT) {
            renderPortPopup(graphics);
        } else if (state == State.IP_INPUT) {
            renderIpPopup(graphics);
        }
    }

    private void renderTitle(TextGraphics graphics) {
        TerminalSize size = Assets.TITLE.getSize();
        int titleCol = (SCREEN_COLS - size.getColumns()) / 2;
        int titleRow = 8;

        Util.placeImage(graphics, new TerminalPosition(titleCol, titleRow), Assets.TITLE, "yellow_bright");

        int subtitleCol = (SCREEN_COLS - Assets.SUBTITLE.getSize().getColumns()) / 2;
        Util.placeImage(graphics, new TerminalPosition(subtitleCol, titleRow + size.getRows() + 2), Assets.SUBTITLE, "WHITE");
    }

    private void renderButtons(TextGraphics graphics) {
        int centerCol = SCREEN_COLS / 2;
        int startRow = 28;
        TextImage btnImage = isHost ? Assets.HOST_BTN : Assets.JOIN_BTN;
        int btnWidth = btnImage.getSize().getColumns();
        int col = centerCol - btnWidth / 2;
        Util.placeImage(graphics, new TerminalPosition(col - 3, startRow), Assets.ARROW_L, "YELLOW_BRIGHT");
        Util.placeImage(graphics, new TerminalPosition(col + btnWidth + 2, startRow), Assets.ARROW_R, "YELLOW_BRIGHT");
        Util.placeImage(graphics, new TerminalPosition(col, startRow), btnImage, "YELLOW_BRIGHT");
    }

    private void renderInstructions(TextGraphics graphics) {
        int col = (SCREEN_COLS - Assets.INSTRUCTIONS.getSize().getColumns()) / 2;
        Util.placeImage(graphics, new TerminalPosition(col, 50), Assets.INSTRUCTIONS, "WHITE");
    }

    private void renderPortPopup(TextGraphics graphics) {
        int popupWidth  = 50;
        int popupHeight = 9;
        int popupCol    = (SCREEN_COLS - popupWidth) / 2;
        int popupRow    = (SCREEN_ROWS - popupHeight) / 2;

        graphics.fillRectangle(
            new TerminalPosition(popupCol, popupRow),
            new TerminalSize(popupWidth, popupHeight), ' ');

        TextImage topBorder    = Util.createTextImage(new String[]{ "┌" + "─".repeat(popupWidth - 2) + "┐" });
        TextImage bottomBorder = Util.createTextImage(new String[]{ "└" + "─".repeat(popupWidth - 2) + "┘" });
        TextImage sideBar      = Util.createTextImage(new String[]{ "│" });
        Util.placeImage(graphics, new TerminalPosition(popupCol, popupRow),                   topBorder,    "WHITE");
        Util.placeImage(graphics, new TerminalPosition(popupCol, popupRow + popupHeight - 1), bottomBorder, "WHITE");
        for (int r = 1; r < popupHeight - 1; r++) {
            Util.placeImage(graphics, new TerminalPosition(popupCol,                  popupRow + r), sideBar, "WHITE");
            Util.placeImage(graphics, new TerminalPosition(popupCol + popupWidth - 1, popupRow + r), sideBar, "WHITE");
        }

        graphics.putString(popupCol + 2, popupRow + 1, "[ PORT ]");
        graphics.putString(popupCol + 2, popupRow + 3, "Port (default: 12345):");

        int inputInner = popupWidth - 4;
        TextImage inputBox = Util.createTextImage(new String[]{ "[" + " ".repeat(inputInner - 2) + "]" });
        Util.placeImage(graphics, new TerminalPosition(popupCol + 2, popupRow + 4), inputBox, "WHITE");

        if (portInput.length() > 0) {
            graphics.putString(popupCol + 3, popupRow + 4, portInput.toString());
        }
        if ((tickCount / 15) % 2 == 0) {
            graphics.putString(popupCol + 3 + portInput.length(), popupRow + 4, "█");
        }

        graphics.putString(popupCol + 2, popupRow + 6, "Enter: start  /  ESC: back");
    }

    private void renderIpPopup(TextGraphics graphics) {
        int popupWidth = 50;
        int popupHeight = 9;
        int popupCol = (SCREEN_COLS - popupWidth) / 2;
        int popupRow = (SCREEN_ROWS - popupHeight) / 2;

        // background + border
        graphics.fillRectangle(
            new TerminalPosition(popupCol, popupRow),
            new TerminalSize(popupWidth, popupHeight),
            ' '
        );
        TextImage topBorder    = Util.createTextImage(new String[]{ "┌" + "─".repeat(popupWidth - 2) + "┐" });
        TextImage bottomBorder = Util.createTextImage(new String[]{ "└" + "─".repeat(popupWidth - 2) + "┘" });
        TextImage sideBar      = Util.createTextImage(new String[]{ "│" });
        Util.placeImage(graphics, new TerminalPosition(popupCol, popupRow),                   topBorder,    "WHITE");
        Util.placeImage(graphics, new TerminalPosition(popupCol, popupRow + popupHeight - 1), bottomBorder, "WHITE");
        for (int r = 1; r < popupHeight - 1; r++) {
            Util.placeImage(graphics, new TerminalPosition(popupCol,                  popupRow + r), sideBar, "WHITE");
            Util.placeImage(graphics, new TerminalPosition(popupCol + popupWidth - 1, popupRow + r), sideBar, "WHITE");
        }

        // popup title, prompt, hint
        int titleCol = popupCol + (popupWidth - Assets.POPUP_TITLE.getSize().getColumns()) / 2;
        Util.placeImage(graphics, new TerminalPosition(titleCol, popupRow + 1), Assets.POPUP_TITLE, "YELLOW_BRIGHT");

        Util.placeImage(graphics, new TerminalPosition(popupCol + 2, popupRow + 3), Assets.POPUP_PROMPT, "WHITE");

        int hintCol = popupCol + (popupWidth - Assets.POPUP_HINT.getSize().getColumns()) / 2;
        Util.placeImage(graphics, new TerminalPosition(hintCol, popupRow + 6), Assets.POPUP_HINT, "WHITE");

        // input field (dynamic content — drawn directly)
        int inputInner = popupWidth - 4;
        TextImage inputBox = Util.createTextImage(new String[]{ "[" + " ".repeat(inputInner - 2) + "]" });
        Util.placeImage(graphics, new TerminalPosition(popupCol + 2, popupRow + 4), inputBox, "WHITE");

        if (ipInput.length() > 0) {
            graphics.putString(popupCol + 3, popupRow + 4, ipInput.toString());
        }
        if ((tickCount / 15) % 2 == 0) {
            graphics.putString(popupCol + 3 + ipInput.length(), popupRow + 4, "█");
        }
    }
}
