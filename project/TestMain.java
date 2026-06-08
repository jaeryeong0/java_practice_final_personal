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
import scene.Scene;
import scene.SceneManager;
import scene.WaitingRoomScene;

import java.awt.Font;
import java.io.IOException;

/**
 * 테스트 모드 진입점.
 * 하나의 창에서 N명의 플레이어 뷰를 실행하며, [ / ] 키로 전환합니다.
 *
 * 실행:  java TestMain P1 P2 P3 P4  (4~7명)
 */
public class TestMain {

    private static final int SCREEN_COL = 270;
    private static final int SCREEN_ROW = 70;
    private static final int TEST_PORT  = 19999;

    public static void main(String[] args) throws Exception {

        // ── 1. 플레이어 이름 결정 ─────────────────────────────────────────
        int n = (args.length >= 4 && args.length <= 7) ? args.length : 4;
        String[] names = new String[n];
        for (int i = 0; i < n; i++) {
            names[i] = (i < args.length) ? args[i] : "P" + (i + 1);
        }

        // ── 2. 내장 서버 시작 ─────────────────────────────────────────────
        Thread serverThread = new Thread(() -> new BangServer(TEST_PORT, n).start(), "test-server");
        serverThread.setDaemon(true);
        serverThread.start();
        Thread.sleep(300);

        // ── 3. 클라이언트 연결 및 씬 생성 ────────────────────────────────
        BangClient[] clients = new BangClient[n];
        Scene[]      scenes  = new Scene[n];
        for (int i = 0; i < n; i++) {
            clients[i] = new BangClient();
            clients[i].connect("localhost", TEST_PORT, names[i]);
            scenes[i]  = new WaitingRoomScene(clients[i]);
            scenes[i].enter();
            Thread.sleep(80); // JOIN 패킷 순서 보장
        }

        // ── 4. Lanterna 창 생성 ───────────────────────────────────────────
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

        // ── 5. 첫 번째 플레이어 뷰 활성화 ────────────────────────────────
        int active = 0;
        SceneManager.getInstance().forceScene(scenes[active]);
        updateTitle(frame, names, active, n);

        // ── 6. 메인 루프 ─────────────────────────────────────────────────
        try {
            while (true) {
                KeyStroke key = screen.pollInput();

                if (key != null) {
                    if (key.getKeyType() == KeyType.EOF) break;

                    char ch = (key.getKeyType() == KeyType.Character) ? key.getCharacter() : 0;

                    if (ch == '[' || ch == ']') {
                        // 현재 씬 저장 (이전 틱에서 전환이 있었을 수 있음)
                        scenes[active] = SceneManager.getInstance().getCurrentScene();
                        active = (ch == '[') ? (active - 1 + n) % n : (active + 1) % n;
                        SceneManager.getInstance().forceScene(scenes[active]);
                        updateTitle(frame, names, active, n);
                    } else {
                        SceneManager.getInstance().handleInput(key);
                        // 입력 처리 중 씬 전환(예: F2로 대기실 퇴장) 저장
                        scenes[active] = SceneManager.getInstance().getCurrentScene();
                    }
                }

                screen.clear();
                SceneManager.getInstance().render(tg);
                // 렌더 중 자동 전환(WaitingRoom → GamePlay 등) 저장
                scenes[active] = SceneManager.getInstance().getCurrentScene();

                drawHud(tg, names, active, n);
                screen.refresh();
                Thread.sleep(33);
            }
        } finally {
            try { screen.close(); } catch (IOException ignored) {}
            for (BangClient c : clients) c.disconnect();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────

    private static void updateTitle(javax.swing.JFrame frame, String[] names, int active, int n) {
        if (frame == null) return;
        frame.setTitle(String.format(
            "BANG! TEST  [%d/%d] %s  |  [ = prev player   ] = next player",
            active + 1, n, names[active]));
    }

    private static void drawHud(TextGraphics tg, String[] names, int active, int n) {
        String prev = active > 0   ? "< " + names[active - 1] : "";
        String cur  = String.format("[ %s  %d/%d ]", names[active], active + 1, n);
        String next = active < n-1 ? names[active + 1] + " >" : "";
        String line = String.format("  %-14s  %s  %-14s    [ < prev   next > ]",
            prev, cur, next);

        tg.setForegroundColor(TextColor.ANSI.BLACK);
        tg.setBackgroundColor(TextColor.ANSI.YELLOW_BRIGHT);
        tg.putString(0, SCREEN_ROW - 1, line);
        tg.setForegroundColor(TextColor.ANSI.DEFAULT);
        tg.setBackgroundColor(TextColor.ANSI.DEFAULT);
    }
}
