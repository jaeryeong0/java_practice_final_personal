import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;

import net.BangClient;
import net.BangServer;
import scene.GamePlayScene;

import java.awt.Font;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Single-window test launcher.
 * Starts an embedded server and connects N clients in one JVM.
 * Use [ / ] to switch between player views.
 *
 * Usage:
 *   java TestMain <name1> <name2> ... <nameN>
 *   java TestMain Alice Bob Carol Dave
 */
public class TestMain {

    static final int SCREEN_COL = 270;
    static final int SCREEN_ROW = 70;

    public static void main(String[] args) throws Exception {
        int n    = args.length > 0 ? args.length : 2;
        int port = 12345;

        // start embedded server (daemon so it dies with the JVM)
        Thread serverThread = new Thread(() -> new BangServer(port, n).start(), "test-server");
        serverThread.setDaemon(true);
        serverThread.start();
        Thread.sleep(300);

        // connect all clients
        List<BangClient>    clients = new ArrayList<>();
        List<GamePlayScene> scenes  = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            String name = i < args.length ? args[i] : "P" + (i + 1);
            BangClient c = new BangClient();
            c.connect("localhost", port, name);
            clients.add(c);
            scenes.add(new GamePlayScene(c));
            Thread.sleep(100);
        }

        for (GamePlayScene s : scenes) s.enter();
        int active = 0;

        // open single window
        Screen screen = null;
        try {
            DefaultTerminalFactory factory = new DefaultTerminalFactory();
            factory.setPreferTerminalEmulator(true);
            factory.setTerminalEmulatorFontConfiguration(
                SwingTerminalFontConfiguration.newInstance(
                    new Font("Consolas", Font.PLAIN, 12)));
            factory.setInitialTerminalSize(new TerminalSize(SCREEN_COL, SCREEN_ROW));

            Terminal terminal = factory.createTerminal();
            if (terminal instanceof javax.swing.JFrame) {
                javax.swing.JFrame frame = (javax.swing.JFrame) terminal;
                frame.setResizable(false);
                frame.setTitle("BANG! Test");
            }

            screen = new TerminalScreen(terminal);
            screen.startScreen();
            screen.setCursorPosition(null);

            TextGraphics tg = screen.newTextGraphics();

            while (true) {
                KeyStroke key = screen.pollInput();
                if (key != null) {
                    if (key.getKeyType() == KeyType.EOF) break;

                    if (key.getKeyType() == KeyType.Character) {
                        char c = key.getCharacter();
                        if (c == ']') {
                            active = (active + 1) % n;
                            scenes.get(active).enter();
                            continue;
                        }
                        if (c == '[') {
                            active = (active - 1 + n) % n;
                            scenes.get(active).enter();
                            continue;
                        }
                    }
                    scenes.get(active).handleInput(key);
                }

                screen.clear();
                scenes.get(active).render(tg);
                drawHud(tg, clients.get(active), active, n);
                screen.refresh();
                Thread.sleep(33);
            }
        } finally {
            if (screen != null) try { screen.close(); } catch (IOException ignored) {}
            for (BangClient c : clients) c.disconnect();
            System.exit(0);
        }
    }

    private static void drawHud(TextGraphics tg, BangClient client, int active, int n) {
        String name = client.getState().myPlayer() != null
            ? client.getState().myPlayer().name
            : "P" + (active + 1);
        String hud = String.format(
            "  VIEWING: %-10s (%d/%d)   [ = prev player     ] = next player  ",
            name, active + 1, n);

        tg.setForegroundColor(TextColor.ANSI.BLACK);
        tg.setBackgroundColor(TextColor.ANSI.YELLOW_BRIGHT);
        tg.putString(0, SCREEN_ROW - 1, hud);
        tg.setForegroundColor(TextColor.ANSI.DEFAULT);
        tg.setBackgroundColor(TextColor.ANSI.DEFAULT);
    }
}
