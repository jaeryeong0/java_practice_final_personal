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

// 새롭게 만든 씬 매니저와 타이틀 씬 임포트
import scene.SceneManager;
import scene.TitleScene;

public class Main {

    public static final int SCREEN_COL = 270;
    public static final int SCREEN_ROW = 70;

    public static void main(String[] args) {
        
        boolean running = true;
        Screen screen = null;

        try {
            // ----------------------------------------------------------------
            // 1. Lanterna 터미널 및 스크린 초기화
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
                ((javax.swing.JFrame) terminal).setResizable(false);
            }

            screen = new TerminalScreen(terminal);
            screen.startScreen();
            screen.setCursorPosition(null);

            TextGraphics tg = screen.newTextGraphics();

            // ----------------------------------------------------------------
            // 2. SceneManager 초기화 및 첫 화면 설정
            // ----------------------------------------------------------------
            SceneManager.getInstance().changeScene(new TitleScene());


            // ----------------------------------------------------------------
            // 3. 메인 게임 루프
            // ----------------------------------------------------------------
            while (running) {
                // 키 입력 감지
                KeyStroke key = screen.pollInput();

                if (key != null) {
                    // 창 닫기(X버튼) 처리
                    if (key.getKeyType() == KeyType.EOF) {
                        break;
                    }
                    
                    // 입력된 키를 SceneManager에게 넘김 -> 알아서 현재 씬의 handleInput 실행
                    SceneManager.getInstance().handleInput(key);
                }

                // 매 프레임마다 화면을 싹 지우고 새로 그림
                screen.clear();
                
                // SceneManager에게 그리라고 명령 -> 알아서 현재 씬의 render 실행
                SceneManager.getInstance().render(tg);
                
                // 버퍼에 그려진 내용을 실제 터미널에 반영
                screen.refresh();

                // 딜레이 (약 30 fps)
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