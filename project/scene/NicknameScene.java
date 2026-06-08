package scene;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import java.io.IOException;

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
        tg.putString(0, 0, "[ NicknameScene ]");

        String mode = isHost ? "[ HOST ]" : "[ CLIENT ]";
        tg.putString((COLS - mode.length()) / 2, ROWS / 2 - 4, mode);

        String prompt = "Enter your nickname:";
        tg.putString((COLS - prompt.length()) / 2, ROWS / 2 - 1, prompt);

        int inputCol = (COLS - 30) / 2;
        String inputDisplay = "> " + nickname;
        tg.putString(inputCol, ROWS / 2 + 1, inputDisplay);
        if ((tickCount / 15) % 2 == 0) {
            tg.putString(inputCol + inputDisplay.length(), ROWS / 2 + 1, "_");
        }

        String hint = "[ENTER] Confirm";
        tg.putString((COLS - hint.length()) / 2, ROWS / 2 + 3, hint);
    }
}
