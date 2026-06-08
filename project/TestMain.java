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

import scene.NicknameScene;
import scene.Scene;
import scene.SceneManager;

import java.awt.Font;
import java.io.IOException;

/**
 * Single-window test launcher.
 *
 * Mirrors running one Main (host) + (N-1) ClientMain (client) windows,
 * but inside one Swing terminal. Use [ / ] to switch between player views.
 *
 * Player 0 = HOST  (NicknameScene -> TitleScene -> starts server, joins)
 * Player 1..N-1 = CLIENT  (NicknameScene -> TitleScene -> connects to host)
 *
 * Usage:  java TestMain <playerCount>   e.g.  java TestMain 4
 */
public class TestMain {

    private static final int SCREEN_COL = 270;
    private static final int SCREEN_ROW = 70;

    public static void main(String[] args) throws Exception {

        // ── 1. Player count ───────────────────────────────────────────────
        int n = 4;
        if (args.length > 0) {
            try { n = Integer.parseInt(args[0]); } catch (NumberFormatException ignored) {}
        }
        n = Math.max(4, Math.min(7, n));

        // ── 2. Create one NicknameScene per player (no pre-connections) ───
        //       Player 0 is the host; the rest are clients.
        Scene[] scenes = new Scene[n];
        for (int i = 0; i < n; i++) {
            scenes[i] = new NicknameScene(i == 0);
            scenes[i].enter();
        }

        // ── 3. Lanterna window ────────────────────────────────────────────
        DefaultTerminalFactory factory = new DefaultTerminalFactory();
        factory.setPreferTerminalEmulator(true);
        factory.setTerminalEmulatorFontConfiguration(
            SwingTerminalFontConfiguration.newInstance(new Font("Consolas", Font.PLAIN, 12)));
        factory.setInitialTerminalSize(new TerminalSize(SCREEN_COL, SCREEN_ROW));

        Terminal terminal = factory.createTerminal();
        javax.swing.JFrame frame = (terminal instanceof javax.swing.JFrame)
            ? (javax.swing.JFrame) terminal : null;
        if (frame != null) frame.setResizable(false);

        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();
        screen.setCursorPosition(null);
        TextGraphics tg = screen.newTextGraphics();

        // ── 4. Activate player 0's view ───────────────────────────────────
        int active = 0;
        SceneManager.getInstance().forceScene(scenes[active]);
        updateTitle(frame, active, n);

        // ── 5. Main loop ──────────────────────────────────────────────────
        try {
            while (true) {
                KeyStroke key = screen.pollInput();

                if (key != null) {
                    if (key.getKeyType() == KeyType.EOF) break;

                    char ch = (key.getKeyType() == KeyType.Character) ? key.getCharacter() : 0;

                    if (ch == '[' || ch == ']') {
                        // Save current player's scene (may have transitioned this tick)
                        scenes[active] = SceneManager.getInstance().getCurrentScene();
                        active = (ch == '[') ? (active - 1 + n) % n : (active + 1) % n;
                        SceneManager.getInstance().forceScene(scenes[active]);
                        updateTitle(frame, active, n);
                    } else {
                        SceneManager.getInstance().handleInput(key);
                        // Capture any scene transition triggered by input
                        scenes[active] = SceneManager.getInstance().getCurrentScene();
                    }
                }

                screen.clear();
                SceneManager.getInstance().render(tg);
                // Capture auto-transitions that fire during render (e.g. WaitingRoom -> GamePlay)
                scenes[active] = SceneManager.getInstance().getCurrentScene();

                drawHud(tg, active, n);
                screen.refresh();
                Thread.sleep(33);
            }
        } finally {
            try { screen.close(); } catch (IOException ignored) {}
            System.exit(0);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────

    private static void updateTitle(javax.swing.JFrame frame, int active, int n) {
        if (frame == null) return;
        String role = (active == 0) ? "HOST" : "CLIENT";
        frame.setTitle(String.format(
            "BANG! TEST  [%d/%d] %s  |  [ = prev player   ] = next player",
            active + 1, n, role));
    }

    private static void drawHud(TextGraphics tg, int active, int n) {
        String role = (active == 0) ? "HOST" : "CLIENT";
        String prev = (active > 0)   ? "< P" + active              : "";
        String cur  = String.format("[ P%d %s  %d/%d ]", active + 1, role, active + 1, n);
        String next = (active < n-1) ? "P" + (active + 2) + " >"   : "";
        String line = String.format("  %-16s  %s  %-16s  [ < prev   next > ]",
            prev, cur, next);

        tg.setForegroundColor(TextColor.ANSI.BLACK);
        tg.setBackgroundColor(TextColor.ANSI.YELLOW_BRIGHT);
        tg.putString(0, SCREEN_ROW - 1, line);
        tg.setForegroundColor(TextColor.ANSI.DEFAULT);
        tg.setBackgroundColor(TextColor.ANSI.DEFAULT);
    }
}
