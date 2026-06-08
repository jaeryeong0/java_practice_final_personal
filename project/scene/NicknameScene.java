package scene;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.graphics.TextImage;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import java.io.IOException;

import tools.Assets;
import tools.Util;

public class NicknameScene implements Scene {

    private static final int COLS = 270;
    private static final int ROWS = 70;

    private final boolean isHost;
    private final StringBuilder nickname = new StringBuilder();
    private int tickCount;

    public NicknameScene(boolean isHost) {
        this.isHost = isHost;
    }

    @Override
    public void enter() {
        nickname.setLength(0);
        tickCount = 0;
    }

    @Override
    public void exit() {}

    @Override
    public void handleInput(KeyStroke key) throws IOException {
        if (key.getKeyType() == KeyType.Enter) {
            if (nickname.length() > 0) {
                SceneManager.getInstance().changeScene(new TitleScene(isHost, nickname.toString()));
            }
        } else if (key.getKeyType() == KeyType.Backspace) {
            if (nickname.length() > 0) nickname.deleteCharAt(nickname.length() - 1);
        } else if (key.getKeyType() == KeyType.Character) {
            if (nickname.length() < 20) nickname.append(key.getCharacter());
        }
    }

    @Override
    public void render(TextGraphics tg) {
        tickCount++;
        tg.fillRectangle(new TerminalPosition(0, 0), new TerminalSize(COLS, ROWS), ' ');

        // title art (centered)
        int titleCol = (COLS - Assets.TITLE.getSize().getColumns()) / 2;
        Util.placeImage(tg, new TerminalPosition(titleCol, 8), Assets.TITLE, "YELLOW_BRIGHT");
        int subtitleCol = (COLS - Assets.SUBTITLE.getSize().getColumns()) / 2;
        Util.placeImage(tg, new TerminalPosition(subtitleCol, 8 + Assets.TITLE.getSize().getRows() + 2), Assets.SUBTITLE, "WHITE");

        // popup box
        int popupWidth  = 50;
        int popupHeight = 9;
        int popupCol    = (COLS - popupWidth) / 2;
        int popupRow    = (ROWS - popupHeight) / 2;

        tg.fillRectangle(new TerminalPosition(popupCol, popupRow),
                         new TerminalSize(popupWidth, popupHeight), ' ');

        TextImage topBorder    = Util.createTextImage(new String[]{ "┌" + "─".repeat(popupWidth - 2) + "┐" });
        TextImage bottomBorder = Util.createTextImage(new String[]{ "└" + "─".repeat(popupWidth - 2) + "┘" });
        TextImage sideBar      = Util.createTextImage(new String[]{ "│" });
        Util.placeImage(tg, new TerminalPosition(popupCol, popupRow),                   topBorder,    "WHITE");
        Util.placeImage(tg, new TerminalPosition(popupCol, popupRow + popupHeight - 1), bottomBorder, "WHITE");
        for (int r = 1; r < popupHeight - 1; r++) {
            Util.placeImage(tg, new TerminalPosition(popupCol,                  popupRow + r), sideBar, "WHITE");
            Util.placeImage(tg, new TerminalPosition(popupCol + popupWidth - 1, popupRow + r), sideBar, "WHITE");
        }

        // title, prompt, hint (from Assets)
        int nicknameTitle = popupCol + (popupWidth - Assets.NICKNAME_TITLE.getSize().getColumns()) / 2;
        Util.placeImage(tg, new TerminalPosition(nicknameTitle, popupRow + 1), Assets.NICKNAME_TITLE, "YELLOW_BRIGHT");

        Util.placeImage(tg, new TerminalPosition(popupCol + 2, popupRow + 3), Assets.NICKNAME_PROMPT, "WHITE");

        int hintCol = popupCol + (popupWidth - Assets.NICKNAME_HINT.getSize().getColumns()) / 2;
        Util.placeImage(tg, new TerminalPosition(hintCol, popupRow + 6), Assets.NICKNAME_HINT, "WHITE");

        // input field
        int inputInner = popupWidth - 4;
        TextImage inputBox = Util.createTextImage(new String[]{ "[" + " ".repeat(inputInner - 2) + "]" });
        Util.placeImage(tg, new TerminalPosition(popupCol + 2, popupRow + 4), inputBox, "WHITE");

        TextImage typedText = Util.createTextImage(new String[]{ nickname.toString() });
        Util.placeImage(tg, new TerminalPosition(popupCol + 3, popupRow + 4), typedText, "YELLOW_BRIGHT");

        // blinking block cursor
        if ((tickCount / 15) % 2 == 0) {
            TextImage cursor = Util.createTextImage(new String[]{ "█" });
            Util.placeImage(tg, new TerminalPosition(popupCol + 3 + nickname.length(), popupRow + 4), cursor, "YELLOW_BRIGHT");
        }
    }
}
