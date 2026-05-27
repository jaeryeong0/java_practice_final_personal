import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

public class LanternaColorExample {
    public static void main(String[] args) throws Exception {
        // 1. 터미널 및 스크린 초기화
        DefaultTerminalFactory factory = new DefaultTerminalFactory();
        factory.setPreferTerminalEmulator(true);
        Terminal terminal = factory.createTerminal();
        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();

        // 2. TextGraphics 객체 생성
        TextGraphics textGraphics = screen.newTextGraphics();

        // 3. ANSI 색상 적용 (글자색: 빨강, 배경색: 검정)
        textGraphics.setForegroundColor(TextColor.ANSI.RED);
        textGraphics.setBackgroundColor(TextColor.ANSI.BLUE);
        textGraphics.putString(2, 2, "This is ANSI Red Text!");

        // 4. RGB 색상 적용 (글자색: 커스텀 녹색)
        textGraphics.setForegroundColor(new TextColor.RGB(0, 255, 128));
        textGraphics.putString(2, 4, "This is RGB TrueColor Text!");

        // 5. Indexed 색상 적용
        textGraphics.setForegroundColor(new TextColor.Indexed(226)); // 노란색 계열
        textGraphics.putString(2, 6, "This is Indexed 256 Color Text!");

        // 6. 화면 새로고침 (실제 화면에 반영)
        screen.refresh();

        // 3초 대기 후 종료
        // Thread.sleep(3000);
        // screen.stopScreen();
    }
}