import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

public class LanternaExample {
    public static void main(String[] args) {
        try {
            // 1. 터미널 및 스크린 설정
            DefaultTerminalFactory factory = new DefaultTerminalFactory();

            // 이 코드가 핵심입니다! VS Code 터미널 대신 별도의 게임 전용 창을 띄웁니다.
            factory.setPreferTerminalEmulator(true); 

            Screen screen = factory.createScreen();
            screen.startScreen();
            
            // 게임 화면에서 깜빡이는 텍스트 커서(밑줄)를 숨깁니다.
            screen.setCursorPosition(null); 

            // TextGraphics: 화면에 색상을 입히고 글자를 그리는 도구
            TextGraphics tg = screen.newTextGraphics();

            // 플레이어 초기 위치
            int playerX = 10;
            int playerY = 5;
            
            boolean keepRunning = true;

            // 메인 게임 루프
            while (keepRunning) {
                // 현재 터미널 창의 크기를 가져옵니다. (사용자가 창 크기를 조절할 수 있으므로 매번 확인)
                TerminalSize terminalSize = screen.getTerminalSize();

                // 2. 화면 전체 초기화 (이전 프레임 지우기)
                screen.clear();

                // 3. 배경 그리기
                // 이중 for문을 쓰지 않고 Lanterna의 fillRectangle을 쓰면 배경을 한 번에 채울 수 있습니다.
                tg.setBackgroundColor(TextColor.ANSI.BLUE);    // 배경색: 파란색
                tg.setForegroundColor(TextColor.ANSI.CYAN);    // 글자색: 청록색
                tg.fillRectangle(
                    new TerminalPosition(0, 0), // 시작 좌표 (0, 0)
                    terminalSize,               // 끝 좌표 (화면 전체 크기)
                    '~'                         // 채울 문자 (바다 느낌)
                );

                // 4. 플레이어 캐릭터 그리기
                tg.setBackgroundColor(TextColor.ANSI.BLUE);    // 배경색과 맞춰서 투명한 것처럼 보이게 함
                tg.setForegroundColor(TextColor.ANSI.YELLOW);  // 캐릭터 색상: 노란색
                tg.putString(playerX, playerY, "★");

                // 5. 상단에 안내 메시지 그리기 (UI)
                tg.setBackgroundColor(TextColor.ANSI.DEFAULT); // 기본 배경색
                tg.setForegroundColor(TextColor.ANSI.WHITE);   // 흰색 글씨
                tg.putString(0, 0, "방향키: 이동 | ESC: 종료");

                // 6. 화면 적용 (버퍼에 그려진 내용을 실제 화면에 한 번에 띄워 깜빡임 방지)
                screen.refresh();

                // 7. 사용자 키 입력 대기 (readInput()은 키를 누를 때까지 프로그램을 멈추고 기다립니다)
                KeyStroke keyStroke = screen.readInput();
                KeyType keyType = keyStroke.getKeyType();

                // 8. 입력된 키에 따른 동작 및 화면 이탈 방지(경계선 체크)
                if (keyType == KeyType.Escape) {
                    keepRunning = false; // ESC 누르면 루프 종료
                } else if (keyType == KeyType.ArrowUp && playerY > 1) {
                    playerY--; // Y좌표 1(안내 메시지 줄) 밑으로만 이동 가능
                } else if (keyType == KeyType.ArrowDown && playerY < terminalSize.getRows() - 1) {
                    playerY++; // 화면 세로 길이 밖으로 못 나감
                } else if (keyType == KeyType.ArrowLeft && playerX > 0) {
                    playerX--; // 화면 왼쪽 밖으로 못 나감
                } else if (keyType == KeyType.ArrowRight && playerX < terminalSize.getColumns() - 1) {
                    playerX++; // 화면 오른쪽 밖으로 못 나감
                }
            }

            // 루프를 빠져나오면 스크린을 종료하여 콘솔을 원래 상태로 되돌립니다.
            screen.stopScreen();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}