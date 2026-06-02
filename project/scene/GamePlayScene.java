package scene;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.graphics.TextImage;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import tools.Assets;
import tools.Util;
import tools.UIPositions;

import java.io.IOException;

public class GamePlayScene implements Scene {

    // --- scene state ---

    private enum TextAreaMode { LOG, CARD_INFO, SELECTION, TEST }
    
    // 조작 포커스 영역 정의 (내 패 vs 중앙 선택지)
    private enum FocusArea { MY_BOARD, TEXT_AREA, TARGET_BOARD }

    private String[] nicks = {"lkjo131", "asfwe123", "wefawef", "aa", "waefawgweag", "b", "dafawef"};   // 테스트용
    private int currentTargetIndex;
    private TextAreaMode currentTextMode;
    private int tickCount;
    
    private FocusArea currentFocus; 

    private int myBoardCurrentRow;
    private int myBoardCurrentCol;

    private int targetBoardCurrentRow;
    private int targetBoardCurrentCol;

    // 테스트용
    private int N_PASSIVE_CARDS = 4;
    private int N_HAND_CARDS = 4;
    private int N_MYBOARD_CARDS = N_PASSIVE_CARDS + N_HAND_CARDS + 3;


    // --- lifecycle ---

    @Override
    public void enter() {
        currentTargetIndex = 1;
        currentTextMode = TextAreaMode.LOG;
        currentFocus = FocusArea.MY_BOARD; // 기본 포커스는 내 패

        myBoardCurrentRow = 0;
        myBoardCurrentCol = 0;

        targetBoardCurrentRow = 0;
        targetBoardCurrentCol = 0;

        tickCount = 0;
    }

    @Override
    public void exit() {}

    // --- input ---

    @Override
    public void handleInput(KeyStroke key) throws IOException {
        KeyType type = key.getKeyType();

        // 1. Tab 키: 조작 포커스(영역) 전환
        if (type == KeyType.Tab) {
            FocusArea[] areas = FocusArea.values();
            currentFocus = areas[(currentFocus.ordinal() + 1) % areas.length];
            if (currentFocus == FocusArea.MY_BOARD) {
                myBoardCurrentRow = 0;
                myBoardCurrentCol = 0;
            } else if (currentFocus == FocusArea.TARGET_BOARD) {
                targetBoardCurrentRow = 0;
                targetBoardCurrentCol = 0;
            }
        }

        // 2. (테스트용) 1, 2, 3 키로 중앙 텍스트 영역 모드 변경
        else if (type == KeyType.Character) {
            char c = key.getCharacter();
            if (c == '1') currentTextMode = TextAreaMode.LOG;
            if (c == '2') currentTextMode = TextAreaMode.CARD_INFO;
            if (c == '3') currentTextMode = TextAreaMode.SELECTION;
            handleTargetBoardInput(key);
        }
        
        // 3. 방향키: 현재 포커스된 영역에 따라 다르게 작동
        else if (type == KeyType.ArrowLeft || type == KeyType.ArrowRight || type == KeyType.ArrowUp || type == KeyType.ArrowDown) {
            if (currentFocus == FocusArea.MY_BOARD) {
                handleMyBoardInput(type);
            } else if (currentFocus == FocusArea.TARGET_BOARD) {
                handleTargetBoardInput(key);
            } else if (currentFocus == FocusArea.TEXT_AREA) {
                handleTextAreaInput(type);
            }
        }
    }

    // 방향키 로직 헬퍼 메서드: 내 패 영역
    private void handleMyBoardInput(KeyType arrowKey) {
        if (arrowKey == KeyType.ArrowUp) {
            int prev = myBoardCurrentRow;
            myBoardCurrentRow = Math.max(0, myBoardCurrentRow - 1);
            if (myBoardCurrentRow != prev) myBoardCurrentCol = 0;
        } else if (arrowKey == KeyType.ArrowDown) {
            int prev = myBoardCurrentRow;
            myBoardCurrentRow = Math.min(2, myBoardCurrentRow + 1);
            if (myBoardCurrentRow != prev) myBoardCurrentCol = 0;
            else currentFocus = FocusArea.TEXT_AREA;
        }

        else if (arrowKey == KeyType.ArrowLeft) {
            myBoardCurrentCol = Math.max(0, myBoardCurrentCol - 1);
        }
        else if (arrowKey == KeyType.ArrowRight) {
            int[] cols = {2, N_PASSIVE_CARDS - 1, N_HAND_CARDS - 1};
            int maxCol = cols[myBoardCurrentRow];
            myBoardCurrentCol = Math.min(maxCol, myBoardCurrentCol + 1);
        }
    }

    
    private void handleTargetBoardInput(KeyStroke key) {
        KeyType arrowKey = key.getKeyType();
        if (arrowKey == KeyType.Character) {
            char c = key.getCharacter();
            if (c == 'q' || c == 'Q') {
                currentTargetIndex = (currentTargetIndex - 1 + nicks.length) % nicks.length;
            } else if (c == 'e' || c == 'E') {
                currentTargetIndex = (currentTargetIndex + 1) % nicks.length;
            }
        } else if (arrowKey == KeyType.ArrowUp) {
            int prev = targetBoardCurrentRow;
            targetBoardCurrentRow = Math.max(0, targetBoardCurrentRow - 1);
            if (targetBoardCurrentRow != prev) targetBoardCurrentCol = 0;
        } else if (arrowKey == KeyType.ArrowDown) {
            int prev = targetBoardCurrentRow;
            targetBoardCurrentRow = Math.min(1, targetBoardCurrentRow + 1);
            if (targetBoardCurrentRow != prev) targetBoardCurrentCol = 0;
            else {
                currentFocus = FocusArea.MY_BOARD;
                myBoardCurrentRow = 0;
                myBoardCurrentCol = 0;
            }
        } else if (arrowKey == KeyType.ArrowLeft) {
            targetBoardCurrentCol = Math.max(0, targetBoardCurrentCol - 1);
        } else if (arrowKey == KeyType.ArrowRight) {
            int[] cols = {2, N_PASSIVE_CARDS - 1};
            int maxCol = cols[targetBoardCurrentRow];
            targetBoardCurrentCol = Math.min(maxCol, targetBoardCurrentCol + 1);
        }
    }
    
    private void handleTextAreaInput(KeyType arrowKey) {
        if (arrowKey == KeyType.ArrowUp) {
            // 선택지 위로 이동 로직...
            
        } else if (arrowKey == KeyType.ArrowDown) {
            // 선택지 아래로 이동 로직...
            // TODO: ArrowDown at bottom → currentFocus = TARGET_BOARD
            // now just change it to target board when arrow down clicked
            currentFocus = FocusArea.TARGET_BOARD;
            targetBoardCurrentRow = 0;
            targetBoardCurrentCol = 0;
        }
    }
    
    // --- render ---
    
    @Override
    public void render(TextGraphics graphics) {
        tickCount++;

        renderBackgroundFrames(graphics);
        renderMyBoard(graphics);
        renderTargetBoard(graphics);
        renderPlayerList(graphics);
        renderTableCenter(graphics);
        renderTextArea(graphics);
    }

    private void renderBackgroundFrames(TextGraphics tg) {
        tg.drawLine(93, 0, 93, 69, '*');
        tg.drawLine(93, 16, 269, 16, '*'); 
        tg.drawLine(93, 38, 269, 38, '*'); 
        tg.drawLine(204, 17, 204, 38, '*'); 
    }

    private void renderMyBoard(TextGraphics tg) {
        Util.placeImage(tg, UIPositions.MyBoard.BOARD, Assets.BOARD);
        Util.placeImage(tg, UIPositions.MyBoard.BULLETS[0], Assets.BULLET, "yellow_bright");
        
        TextImage[] cards = {Assets.FRAME, Assets.FRAME, Assets.FRAME, Assets.FRAME}; // 테스트용
        
        int passiveCardHighlightIndex = -1;
        int handCardHighlightIndex = -1;

        Util.placeImage(tg, UIPositions.MyBoard.CHARACTER_CARD, Assets.CALAMITY_JANET);

        // 포커스가 내 패에 있다면 시각적 표시기 띄우기
        if (currentFocus == FocusArea.MY_BOARD) {
            currentTextMode = TextAreaMode.CARD_INFO;

            if (myBoardCurrentRow == 0) {
                TerminalPosition cardSlots[] = {UIPositions.MyBoard.ROLE_CARD, UIPositions.MyBoard.CHARACTER_CARD,
                     UIPositions.MyBoard.WEAPON_CARD};
                Util.changeBackgroundColor(tg, cardSlots[myBoardCurrentCol], 25, 17, TextColor.ANSI.BLACK_BRIGHT);
            }
            if (myBoardCurrentRow == 1) {
                passiveCardHighlightIndex = myBoardCurrentCol;
            } else if (myBoardCurrentRow == 2) {
                handCardHighlightIndex = myBoardCurrentCol;
            }
        }
        Util.placeCards(tg, cards, UIPositions.MyBoard.PASSIVE_CARDS, 88, passiveCardHighlightIndex);
        Util.placeCards(tg, cards, UIPositions.MyBoard.HAND_CARDS, 88, handCardHighlightIndex);
        
    }

    private void renderTargetBoard(TextGraphics tg) {
        Util.placeImage(tg, UIPositions.TargetBoard.BOARD, Assets.BOARD);
        Util.placeImage(tg, UIPositions.TargetBoard.BULLETS[0], Assets.BULLET, "yellow_bright");
        Util.placeImage(tg, UIPositions.TargetBoard.HAND_COUNT, Assets.NUMCARDS_DISPLAY);
        
        TextImage[] cards = {Assets.FRAME, Assets.FRAME, Assets.FRAME, Assets.FRAME};
        
        int passiveCardHighlightIndex = -1;

        if (currentFocus == FocusArea.TARGET_BOARD) {
            currentTextMode = TextAreaMode.CARD_INFO;

            if (targetBoardCurrentRow == 0) {
                TerminalPosition cardSlots[] = {UIPositions.TargetBoard.ROLE_CARD, UIPositions.TargetBoard.CHARACTER_CARD,
                     UIPositions.TargetBoard.WEAPON_CARD};
                Util.changeBackgroundColor(tg, cardSlots[targetBoardCurrentCol], 25, 17, TextColor.ANSI.BLACK_BRIGHT);
            } else if (targetBoardCurrentRow == 1) {
                passiveCardHighlightIndex = targetBoardCurrentCol;
            }
        }
        
        Util.placeCards(tg, cards, UIPositions.TargetBoard.PASSIVE_CARDS, 88, passiveCardHighlightIndex);

        tg.putString(UIPositions.TargetBoard.NICKNAME, "< " + nicks[currentTargetIndex] + " >");
    }

    private void renderPlayerList(TextGraphics tg) {
        int numPlayers = nicks.length;
        for (int i = 0; i < numPlayers; i++) {
            Util.placeImage(tg, UIPositions.PlayerList.ICONS[i], Assets.USER_ICON);

            TerminalPosition pos = UIPositions.PlayerList.NICKNAMES[i];
            tg.putString(pos.withColumn(pos.getColumn() - (nicks[i].length()) / 2), nicks[i]);
        }
    }

    private void renderTableCenter(TextGraphics tg) {
        Util.placeImage(tg, UIPositions.TableCenter.MAIN_DECK, Assets.FRAME, "yellow");
        Util.placeImage(tg, UIPositions.TableCenter.DISCARD_PILE, Assets.FRAME);
    }

    private void renderTextArea(TextGraphics tg) {

        // 포커스가 중앙 영역에 있다면 시각적 표시기 띄우기
        if (currentFocus == FocusArea.TEXT_AREA) {
            tg.putString(UIPositions.TextArea.TEXT_START.withRow(UIPositions.TextArea.TEXT_START.getRow() - 1),
             "< Pointing Text Area >   ");
        }

        if (currentTextMode == TextAreaMode.LOG) {
            tg.putString(UIPositions.TextArea.TEXT_START, "< Game Log >");
        } 
        else if (currentTextMode == TextAreaMode.CARD_INFO) {
            tg.putString(UIPositions.TextArea.TEXT_START, "< Card Information >");
        } 
        else if (currentTextMode == TextAreaMode.SELECTION) {
            tg.putString(UIPositions.TextArea.TEXT_START, "< Select >");
        }
    }
}