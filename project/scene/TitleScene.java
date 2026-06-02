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

    private enum State { MAIN_MENU, IP_INPUT }

    // --- scene state ---

    private State state;
    private int selectedButton;  // 0 = Host, 1 = Join
    private final StringBuilder ipInput = new StringBuilder();
    private int tickCount;

    // --- lifecycle ---

    @Override
    public void enter() {
        state = State.MAIN_MENU;
        selectedButton = 0;
        ipInput.setLength(0);
        tickCount = 0;
    }

    @Override
    public void exit() {}

    // --- input ---

    @Override
    public void handleInput(KeyStroke key) throws IOException {
        if (state == State.MAIN_MENU) {
            handleMainMenuInput(key);
        } else {
            handleIpPopupInput(key);
        }
    }

    private void handleMainMenuInput(KeyStroke key) {
        if (key.getKeyType() == KeyType.ArrowUp || key.getKeyType() == KeyType.ArrowDown) {
            selectedButton = 1 - selectedButton;
        } else if (key.getKeyType() == KeyType.Enter) {
            if (selectedButton == 0) {
                // HOST: start embedded server then connect as first player
                try {
                    new Thread(() -> new BangServer(12345, 4).start(), "bang-server").start();
                    Thread.sleep(200);
                    BangClient client = new BangClient();
                    client.connect("localhost", 12345, "Host");
                    SceneManager.getInstance().changeScene(new WaitingRoomScene(client));
                } catch (Exception e) {
                    System.err.println("[TitleScene] Host error: " + e.getMessage());
                }
            } else {
                state = State.IP_INPUT;
                ipInput.setLength(0);
            }
        }
    }

    private void handleIpPopupInput(KeyStroke key) {
        if (key.getKeyType() == KeyType.Escape) {
            state = State.MAIN_MENU;
        } else if (key.getKeyType() == KeyType.Enter) {
            // JOIN: connect to the typed IP
            try {
                BangClient client = new BangClient();
                client.connect(ipInput.toString().trim(), 12345, "Player");
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
            char c = key.getCharacter();
            if ((c >= '0' && c <= '9') || c == '.') {
                if (ipInput.length() < 15) {
                    ipInput.append(c);
                }
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
        renderTitle(graphics);
        renderButtons(graphics);
        renderInstructions(graphics);
        if (state == State.IP_INPUT) {
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
        TextImage[] btnImages = { Assets.HOST_BTN, Assets.JOIN_BTN };

        for (int i = 0; i < btnImages.length; i++) {
            int btnWidth = btnImages[i].getSize().getColumns();
            int col = centerCol - btnWidth / 2;
            int row = startRow + i * 4;
            String color = (i == selectedButton) ? "YELLOW_BRIGHT" : "WHITE";

            if (i == selectedButton) {
                Util.placeImage(graphics, new TerminalPosition(col - 3, row), Assets.ARROW_L, "YELLOW_BRIGHT");
                Util.placeImage(graphics, new TerminalPosition(col + btnWidth + 2, row), Assets.ARROW_R, "YELLOW_BRIGHT");
            }
            Util.placeImage(graphics, new TerminalPosition(col, row), btnImages[i], color);
        }
    }

    private void renderInstructions(TextGraphics graphics) {
        int col = (SCREEN_COLS - Assets.INSTRUCTIONS.getSize().getColumns()) / 2;
        Util.placeImage(graphics, new TerminalPosition(col, 50), Assets.INSTRUCTIONS, "WHITE");
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

        TextImage typedText = Util.createTextImage(new String[]{ ipInput.toString() });
        Util.placeImage(graphics, new TerminalPosition(popupCol + 3, popupRow + 4), typedText, "YELLOW_BRIGHT");

        // blinking block cursor (toggles every 15 frames ≈ 0.5 s at 30 fps)
        if ((tickCount / 15) % 2 == 0) {
            TextImage cursor = Util.createTextImage(new String[]{ "█" });
            Util.placeImage(graphics, new TerminalPosition(popupCol + 3 + ipInput.length(), popupRow + 4), cursor, "YELLOW_BRIGHT");
        }
    }
}
