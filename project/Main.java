import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.graphics.TextImage;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;
import java.awt.Font;

import java.io.IOException;


public class Main {

    // size of screen (number of characters)
    public static final int SCREEN_COL = 160;
    public static final int SCREEN_ROW = 60;
    
    public static void main(String[] args) {
        
        boolean running = true;
        Screen screen = null;

        try {
            //----------------------------------------------------------------
            // screen initialization
            //----------------------------------------------------------------
            
            // 외부 애뮬레이터 창에서 프로그램 실행하도록 설정.
            DefaultTerminalFactory factory = new DefaultTerminalFactory();
            factory.setPreferTerminalEmulator(true);
            
            // 폰트 지정
            Font myFont = new Font("Consolas", Font.PLAIN, 16);
            SwingTerminalFontConfiguration fontConfig = SwingTerminalFontConfiguration.newInstance(myFont);
            factory.setTerminalEmulatorFontConfiguration(fontConfig);


            // 화면 크기 세팅
            TerminalSize initialSize = new TerminalSize(SCREEN_COL, SCREEN_ROW);
            factory.setInitialTerminalSize(initialSize);

            // 터미널 실행하고, 화면 크기 조절 불가하도록 설정
            Terminal terminal = factory.createTerminal();
            if (terminal instanceof javax.swing.JFrame) {
                ((javax.swing.JFrame) terminal).setResizable(false);
            }

            // 스크린 실행
            screen = new TerminalScreen(terminal);
            screen.startScreen();
            
            // 커서 숨기기
            screen.setCursorPosition(null);

            // 화면에 그리기 위한 도구 가져오기
            TextGraphics tg = screen.newTextGraphics();
            
            // 화면 지우기
            screen.clear();

            // 배경 그리기
            tg.drawImage(new TerminalPosition(0,0), assets.BACKGROUND);

            screen.refresh();

            //-----------------------------------------------------------------
            while (running) {
                KeyStroke key = screen.pollInput();

                //-----------------------------------------------------------------
                // 플레이어로부터 키 입력 처리
                //-----------------------------------------------------------------
                if (key != null) {
                    // 창 닫는 버튼 눌렀을 때.
                    if (key.getKeyType() == KeyType.EOF) {
                        break;
                    }



                }
                //-----------------------------------------------------------------
                // 소켓 통신 결과 업데이트
                //-----------------------------------------------------------------

                // 딜레이 (약 30 tps로 설정)
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