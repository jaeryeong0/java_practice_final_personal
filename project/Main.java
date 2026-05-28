import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextImage;
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


public class Main {

    // size of screen (number of characters)
    public static final int SCREEN_COL = 270;
    public static final int SCREEN_ROW = 70;
    
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
            Font myFont = new Font("Consolas", Font.PLAIN, 12);
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
            tg.drawLine(93, 0, 93, 69, '*');

            // 2. (97, 17) - (269, 17) : 가로선 (X 끝 좌표 270 -> 269)
            tg.drawLine(93, 16, 269, 16, '*');

            // 3. (97, 40) - (269, 40) : 가로선 (X 끝 좌표 270 -> 269)
            tg.drawLine(93, 38, 269, 38, '*');

            // 4. (204, 17) - (204, 40) : 세로선
            tg.drawLine(204, 17, 204, 38, '*');


            Util.placeImage(tg, UIPositions.MyBoard.BOARD, "board");
            Util.placeImage(tg, UIPositions.MyBoard.BULLETS[0], "bullet", "yellow_bright");
            TextImage[] cards = {Assets.FRAME, Assets.FRAME, Assets.FRAME, Assets.FRAME};
            Util.placeCards(tg, cards, UIPositions.MyBoard.PASSIVE_CARDS, 88);
            Util.placeCards(tg, cards, UIPositions.MyBoard.HAND_CARDS, 88);

            Util.placeImage(tg, UIPositions.TargetBoard.BOARD, "board");
            Util.placeImage(tg, UIPositions.TargetBoard.BULLETS[0], "bullet", "yellow_bright");
            Util.placeImage(tg, UIPositions.TargetBoard.HAND_COUNT, "numcards_display");
            Util.placeCards(tg, cards, UIPositions.TargetBoard.PASSIVE_CARDS, 88);
            
            
            String[] nicks = {"lkjo131", "asfwe123", "wefawef", "aa", "waefawgweag", "b", "dafawef"};
            int numPlayers = nicks.length;
            for (int i = 0; i < numPlayers; i++) {
                Util.placeImage(tg, UIPositions.PlayerList.ICONS[i], "user_icon");
                TerminalPosition pos = UIPositions.PlayerList.NICKNAMES[i];
                tg.putString(pos.withColumn(pos.getColumn() - (nicks[i].length())/2), nicks[i]);
            }

            Util.placeImage(tg, UIPositions.TableCenter.MAIN_DECK, "frame", "yellow");
            Util.placeImage(tg, UIPositions.TableCenter.DISCARD_PILE, "frame");

            
                       

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

                    // 키 입력 테스트
                    // if (key.getKeyType() == KeyType.ArrowDown) {
                    //     tg.drawImage(new TerminalPosition(3, 8), Assets.VICE);
                    //     tg.drawImage(new TerminalPosition(6, 2), Util.colorizeTextImage(Assets.BULLET, TextColor.ANSI.YELLOW_BRIGHT));
                    //     tg.drawImage(new TerminalPosition(100, 0), Util.colorizeTextImage(Assets.FRAME, TextColor.ANSI.RED));
                    //     tg.drawImage(new TerminalPosition(103, 2), Assets.GUN1);
                    //     Util.placeImage(tg, UIPositions.MyBoard.WEAPON_CARD, "GUN1", "WHITE");

                    //     screen.refresh();
                    // }
                    else if (key.getKeyType() == KeyType.Character) {
                        if (key.getCharacter() == '1') {
                            Util.placeImage(tg, UIPositions.MyBoard.WEAPON_CARD, "GUN1");
                        }
                        else if (key.getCharacter() == '2') {
                            Util.placeImage(tg, UIPositions.MyBoard.WEAPON_CARD, "GUN2");
                        }
                        else if (key.getCharacter() == '3') {
                            Util.placeImage(tg, UIPositions.MyBoard.WEAPON_CARD, "GUN3");
                        }
                        else if (key.getCharacter() == '4') {
                            Util.placeImage(tg, UIPositions.MyBoard.WEAPON_CARD, "GUN4");
                        }
                        else if (key.getCharacter() == '5') {
                            Util.placeImage(tg, UIPositions.MyBoard.WEAPON_CARD, "GUN5");
                        }
                        screen.refresh();
                    }
                }
                
                //-----------------------------------------------------------------
                // 소켓 통신 결과 업데이트
                //-----------------------------------------------------------------



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

