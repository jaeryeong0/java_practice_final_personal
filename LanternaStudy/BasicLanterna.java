import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;


public class BasicLanterna {
    public static final int SCREEN_COL = 150;
    public static final int SCREEN_ROW = 50;

    public static void main(String[] args) {

        try {
            // 1. 터미널 설정 및 스크린 생성 (에뮬레이터 창 사용)
            DefaultTerminalFactory factory = new DefaultTerminalFactory();
            factory.setPreferTerminalEmulator(true);
            
            TerminalSize initialSize = new TerminalSize(SCREEN_COL, SCREEN_ROW);
            factory.setInitialTerminalSize(initialSize);

            // 1. 스크린 대신 '터미널'을 먼저 생성합니다.
            Terminal terminal = factory.createTerminal();

            // 2. 생성된 터미널이 윈도우 창(JFrame)인지 확인하고 크기 조절을 막습니다.
            if (terminal instanceof javax.swing.JFrame) {
                ((javax.swing.JFrame) terminal).setResizable(false);
            }

            // 3. 크기가 고정된 터미널을 가지고 스크린을 만듭니다.
            Screen screen = new TerminalScreen(terminal);
            screen.startScreen();

            // 2. 화면에 그리기 위한 도구(TextGraphics) 가져오기
            TextGraphics tg = screen.newTextGraphics();

            // 3. 화면 깨끗하게 지우기
            screen.clear();

            // 4. 원하는 위치(x: 10, y: 5)에 텍스트 그리기
            tg.putString(10, 5, "Hello, Lanterna!");
            
            // 5. 그린 내용을 실제 화면에 적용 (매우 중요!)
            screen.refresh();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}