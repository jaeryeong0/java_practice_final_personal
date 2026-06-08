import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;

import scene.NicknameScene;
import scene.SceneManager;

import java.awt.Font;
import java.io.IOException;

/**
 * Client entry point.
 *
 * Remote server:
 *   java ClientMain <host> <port> <name>
 *   java ClientMain 192.168.1.5 12345 Alice
 *
 * Embedded local server (everyone plays on one machine with separate JVMs):
 *   java ClientMain local [port] [maxPlayers] [name]
 *   java ClientMain local 12345 4 You         ← also starts the server
 *   java ClientMain localhost 12345 P2         ← other players connect to it
 */
public class ClientMain {

    public static final int SCREEN_COL = 270;
    public static final int SCREEN_ROW = 70;

    public static void main(String[] args) throws Exception {
        Screen screen = null;
        try {
            DefaultTerminalFactory factory = new DefaultTerminalFactory();
            factory.setPreferTerminalEmulator(true);
            Font myFont = new Font("Consolas", Font.PLAIN, 12);
            factory.setTerminalEmulatorFontConfiguration(
                SwingTerminalFontConfiguration.newInstance(myFont));
            factory.setInitialTerminalSize(new TerminalSize(SCREEN_COL, SCREEN_ROW));

            Terminal terminal = factory.createTerminal();
            if (terminal instanceof javax.swing.JFrame) {
                javax.swing.JFrame frame = (javax.swing.JFrame) terminal;
                frame.setResizable(false);
                frame.setTitle("BANG! CLIENT");
            }

            screen = new TerminalScreen(terminal);
            screen.startScreen();
            screen.setCursorPosition(null);

            TextGraphics tg = screen.newTextGraphics();
            SceneManager.getInstance().changeScene(new NicknameScene(false));

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
        } finally {
            if (screen != null) try { screen.close(); } catch (IOException ignored) {}
        }
    }
}
