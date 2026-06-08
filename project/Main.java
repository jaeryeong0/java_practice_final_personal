import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;

import java.awt.Font;
import java.io.IOException;

import scene.NicknameScene;
import scene.SceneManager;

public class Main {

    public static final int SCREEN_COL = 270;
    public static final int SCREEN_ROW = 70;

    public static void main(String[] args) {
        Screen screen = null;

        try {
            // ----------------------------------------------------------------
            // 1. Initialize Lanterna terminal and screen
            // ----------------------------------------------------------------
            DefaultTerminalFactory factory = new DefaultTerminalFactory();
            factory.setPreferTerminalEmulator(true);

            Font myFont = new Font("Consolas", Font.PLAIN, 12);
            SwingTerminalFontConfiguration fontConfig = SwingTerminalFontConfiguration.newInstance(myFont);
            factory.setTerminalEmulatorFontConfiguration(fontConfig);

            TerminalSize initialSize = new TerminalSize(SCREEN_COL, SCREEN_ROW);
            factory.setInitialTerminalSize(initialSize);

            Terminal terminal = factory.createTerminal();
            if (terminal instanceof javax.swing.JFrame) {
                javax.swing.JFrame frame = (javax.swing.JFrame) terminal;
                frame.setResizable(false);
                frame.setTitle("BANG! HOST");
            }

            screen = new TerminalScreen(terminal);
            screen.startScreen();
            screen.setCursorPosition(null);

            TextGraphics tg = screen.newTextGraphics();

            // ----------------------------------------------------------------
            // 2. First screen: nickname input
            // ----------------------------------------------------------------
            SceneManager.getInstance().changeScene(new NicknameScene(true));

            // ----------------------------------------------------------------
            // 3. Main game loop
            // ----------------------------------------------------------------
            while (true) {
                KeyStroke key = screen.pollInput();

                if (key != null) {
                    if (key.getKeyType() == KeyType.EOF) {
                        // X button treated the same as F2 (disconnect and clean up scene)
                        SceneManager.getInstance().handleInput(
                            new KeyStroke(KeyType.F2));
                        break;
                    }
                    SceneManager.getInstance().handleInput(key);
                }

                screen.clear();
                SceneManager.getInstance().render(tg);
                screen.refresh();
                Thread.sleep(33);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (screen != null) {
                try {
                    screen.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}