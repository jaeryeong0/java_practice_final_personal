import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;

import tools.Assets;
import tools.Util;

import java.awt.Font;
import java.io.IOException;

/**
 * Renders the 5 blue equipment card assets:
 * Barrel, Mustang, Appaloosa, Jail, Dynamite
 *
 * Press any key or close the window to exit.
 */
public class TestAssets {

    public static void main(String[] args) throws Exception {
        DefaultTerminalFactory factory = new DefaultTerminalFactory();
        factory.setPreferTerminalEmulator(true);
        factory.setTerminalEmulatorFontConfiguration(
            SwingTerminalFontConfiguration.newInstance(
                new Font("Consolas", Font.PLAIN, 14)));
        factory.setInitialTerminalSize(new TerminalSize(170, 30));

        Terminal terminal = factory.createTerminal();
        if (terminal instanceof javax.swing.JFrame) {
            ((javax.swing.JFrame) terminal).setTitle("Asset Test: Blue Equipment Cards");
        }

        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();
        screen.setCursorPosition(null);

        TextGraphics tg = screen.newTextGraphics();

        int x = 2;
        int y = 2;
        int gap = 2;

        Util.placeImage(tg, new TerminalPosition(x, y), Assets.BARREL);
        x += Assets.BARREL.getSize().getColumns() + gap;

        Util.placeImage(tg, new TerminalPosition(x, y), Assets.MUSTANG);
        x += Assets.MUSTANG.getSize().getColumns() + gap;

        Util.placeImage(tg, new TerminalPosition(x, y), Assets.SCOPE);
        x += Assets.SCOPE.getSize().getColumns() + gap;

        Util.placeImage(tg, new TerminalPosition(x, y), Assets.JAIL);
        x += Assets.JAIL.getSize().getColumns() + gap;

        Util.placeImage(tg, new TerminalPosition(x, y), Assets.DYNAMITE);

        screen.refresh();

        // wait for any key press to exit
        while (true) {
            KeyStroke key = screen.readInput();
            if (key.getKeyType() == KeyType.EOF
                    || key.getKeyType() == KeyType.Escape
                    || key.getKeyType() == KeyType.Enter
                    || key.getKeyType() == KeyType.Character) {
                break;
            }
        }

        try { screen.close(); } catch (IOException ignored) {}
    }
}
