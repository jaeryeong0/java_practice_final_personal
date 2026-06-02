package scene;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.graphics.TextImage;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import game.GameLogic.*;

import tools.Assets;
import tools.Util;
import tools.UIPositions;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class GamePlayScene implements Scene {

    // --- scene state ---

    private enum TextAreaMode { LOG, CARD_INFO, SELECTION }
    private enum FocusArea { MY_BOARD, TEXT_AREA, TARGET_BOARD }

    private Game game;
    private int myPlayerIdx = 0;

    private int currentTargetIndex;
    private int selectedTargetCandidateIdx = 0;

    private TextAreaMode currentTextMode;
    private FocusArea currentFocus;

    private int myBoardCurrentRow;
    private int myBoardCurrentCol;
    private int targetBoardCurrentRow;
    private int targetBoardCurrentCol;

    // --- lifecycle ---

    @Override
    public void enter() {
        List<Player> players = Arrays.asList(
            new Player("You"), new Player("P2"), new Player("P3"), new Player("P4")
        );
        game = new Game(players);
        game.startGame();

        currentTargetIndex = 1;
        currentTextMode = TextAreaMode.LOG;
        currentFocus = FocusArea.MY_BOARD;
        myBoardCurrentRow = 2;
        myBoardCurrentCol = 0;
        targetBoardCurrentRow = 0;
        targetBoardCurrentCol = 0;
        selectedTargetCandidateIdx = 0;
    }

    @Override
    public void exit() {}

    // --- input ---

    @Override
    public void handleInput(KeyStroke key) throws IOException {
        KeyType type = key.getKeyType();

        if (game.getState() == GameState.SELECT_TARGET) {
            handleTargetSelectionInput(key);
            return;
        }
        if (game.getState() == GameState.GENERAL_STORE) {
            handleGeneralStoreInput(key);
            return;
        }

        if (type == KeyType.Tab) {
            FocusArea[] areas = FocusArea.values();
            currentFocus = areas[(currentFocus.ordinal() + 1) % areas.length];
            if (currentFocus == FocusArea.MY_BOARD) { myBoardCurrentRow = 2; myBoardCurrentCol = 0; }
            else if (currentFocus == FocusArea.TARGET_BOARD) { targetBoardCurrentRow = 0; targetBoardCurrentCol = 0; }
        }
        else if (type == KeyType.Enter) {
            handleEnterKey();
        }
        else if (type == KeyType.Escape) {
            game.cancelTarget();
        }
        else if (type == KeyType.Character) {
            char c = key.getCharacter();
            if (c == '1') currentTextMode = TextAreaMode.LOG;
            if (c == '2') currentTextMode = TextAreaMode.CARD_INFO;
            if (c == 'q' || c == 'Q') currentTargetIndex = (currentTargetIndex - 1 + game.players.size()) % game.players.size();
            if (c == 'e' || c == 'E') currentTargetIndex = (currentTargetIndex + 1) % game.players.size();
            if (c == 'f' || c == 'F') game.endTurn();
        }
        else if (type == KeyType.ArrowLeft || type == KeyType.ArrowRight ||
                 type == KeyType.ArrowUp   || type == KeyType.ArrowDown) {
            if (currentFocus == FocusArea.MY_BOARD) handleMyBoardInput(type);
            else if (currentFocus == FocusArea.TARGET_BOARD) handleTargetBoardInput(type);
            else if (currentFocus == FocusArea.TEXT_AREA) handleTextAreaInput(type);
        }
    }

    private void handleEnterKey() {
        if (currentFocus == FocusArea.MY_BOARD && myBoardCurrentRow == 2) {
            if (game.getState() == GameState.PLAY && game.getCurrentPlayerIdx() == myPlayerIdx) {
                game.tryPlayCard(myBoardCurrentCol);
            }
        }
    }

    private void handleTargetSelectionInput(KeyStroke key) {
        KeyType type = key.getKeyType();
        List<Player> candidates = game.getTargetCandidates();
        if (candidates.isEmpty()) return;

        if (type == KeyType.ArrowLeft) {
            selectedTargetCandidateIdx = (selectedTargetCandidateIdx - 1 + candidates.size()) % candidates.size();
        } else if (type == KeyType.ArrowRight) {
            selectedTargetCandidateIdx = (selectedTargetCandidateIdx + 1) % candidates.size();
        } else if (type == KeyType.Enter) {
            game.confirmTarget(selectedTargetCandidateIdx);
            selectedTargetCandidateIdx = 0;
        } else if (type == KeyType.Escape) {
            game.cancelTarget();
        }
    }

    private int generalStoreSelectedIdx = 0;

    private void handleGeneralStoreInput(KeyStroke key) {
        KeyType type = key.getKeyType();
        List<Card> pool = game.getGeneralStorePool();
        if (pool.isEmpty()) return;
        if (type == KeyType.ArrowLeft) {
            generalStoreSelectedIdx = (generalStoreSelectedIdx - 1 + pool.size()) % pool.size();
        } else if (type == KeyType.ArrowRight) {
            generalStoreSelectedIdx = (generalStoreSelectedIdx + 1) % pool.size();
        } else if (type == KeyType.Enter) {
            game.pickGeneralStoreCard(generalStoreSelectedIdx);
            generalStoreSelectedIdx = 0;
        }
    }

    private void handleMyBoardInput(KeyType arrowKey) {
        Player me = game.players.get(myPlayerIdx);
        if (arrowKey == KeyType.ArrowUp) {
            int prev = myBoardCurrentRow;
            myBoardCurrentRow = Math.max(0, myBoardCurrentRow - 1);
            if (myBoardCurrentRow != prev) myBoardCurrentCol = 0;
            else { currentFocus = FocusArea.TARGET_BOARD; targetBoardCurrentRow = 0; targetBoardCurrentCol = 0; }
        } else if (arrowKey == KeyType.ArrowDown) {
            int prev = myBoardCurrentRow;
            myBoardCurrentRow = Math.min(2, myBoardCurrentRow + 1);
            if (myBoardCurrentRow != prev) myBoardCurrentCol = 0;
            else { currentFocus = FocusArea.TEXT_AREA; }
        } else if (arrowKey == KeyType.ArrowLeft) {
            myBoardCurrentCol = Math.max(0, myBoardCurrentCol - 1);
        } else if (arrowKey == KeyType.ArrowRight) {
            int maxCol;
            if (myBoardCurrentRow == 0) maxCol = 2;
            else if (myBoardCurrentRow == 1) maxCol = Math.max(0, me.getField().size() - 1);
            else maxCol = Math.max(0, me.getHand().size() - 1);
            myBoardCurrentCol = Math.min(maxCol, myBoardCurrentCol + 1);
        }
    }

    private void handleTargetBoardInput(KeyType arrowKey) {
        Player target = game.players.get(currentTargetIndex);
        if (arrowKey == KeyType.ArrowUp) {
            int prev = targetBoardCurrentRow;
            targetBoardCurrentRow = Math.max(0, targetBoardCurrentRow - 1);
            if (targetBoardCurrentRow != prev) targetBoardCurrentCol = 0;
            else currentFocus = FocusArea.TEXT_AREA;
        } else if (arrowKey == KeyType.ArrowDown) {
            int prev = targetBoardCurrentRow;
            targetBoardCurrentRow = Math.min(1, targetBoardCurrentRow + 1);
            if (targetBoardCurrentRow != prev) targetBoardCurrentCol = 0;
            else { currentFocus = FocusArea.MY_BOARD; myBoardCurrentRow = 0; myBoardCurrentCol = 0; }
        } else if (arrowKey == KeyType.ArrowLeft) {
            targetBoardCurrentCol = Math.max(0, targetBoardCurrentCol - 1);
        } else if (arrowKey == KeyType.ArrowRight) {
            int maxCol = (targetBoardCurrentRow == 0) ? 2 : Math.max(0, target.getField().size() - 1);
            targetBoardCurrentCol = Math.min(maxCol, targetBoardCurrentCol + 1);
        }
    }

    private void handleTextAreaInput(KeyType arrowKey) {
        if (arrowKey == KeyType.ArrowUp) {
            currentFocus = FocusArea.MY_BOARD; myBoardCurrentRow = 2; myBoardCurrentCol = 0;
        } else if (arrowKey == KeyType.ArrowDown) {
            currentFocus = FocusArea.TARGET_BOARD; targetBoardCurrentRow = 0; targetBoardCurrentCol = 0;
        }
    }

    // --- render ---

    @Override
    public void render(TextGraphics tg) {
        renderBackgroundFrames(tg);
        renderMyBoard(tg);
        renderTargetBoard(tg);
        renderPlayerList(tg);
        renderTableCenter(tg);
        renderTextArea(tg);
    }

    private void renderBackgroundFrames(TextGraphics tg) {
        tg.drawLine(93, 0, 93, 69, '*');
        tg.drawLine(93, 16, 269, 16, '*');
        tg.drawLine(93, 38, 269, 38, '*');
        tg.drawLine(204, 17, 204, 38, '*');
    }

    private void renderMyBoard(TextGraphics tg) {
        Player me = game.players.get(myPlayerIdx);

        Util.placeImage(tg, UIPositions.MyBoard.BOARD, Assets.BOARD);

        // HP bullets: yellow = alive, dark = lost
        for (int i = 0; i < me.getMaxHp() && i < UIPositions.MyBoard.BULLETS.length; i++) {
            String color = i < me.getHp() ? "yellow_bright" : "black_bright";
            Util.placeImage(tg, UIPositions.MyBoard.BULLETS[i], Assets.BULLET, color);
        }

        // Row 0: role, character, weapon cards
        Util.placeImage(tg, UIPositions.MyBoard.ROLE_CARD, getRoleImage(me.getRole()));
        Util.placeImage(tg, UIPositions.MyBoard.CHARACTER_CARD, getCharacterImage(me.getCharacter()));
        Util.placeImage(tg, UIPositions.MyBoard.WEAPON_CARD,
            me.getWeapon() != null ? getWeaponImage(me.getWeapon().range) : Assets.FRAME2);

        int passiveHighlight = -1, handHighlight = -1;
        if (currentFocus == FocusArea.MY_BOARD) {
            currentTextMode = TextAreaMode.CARD_INFO;
            if (myBoardCurrentRow == 0) {
                TerminalPosition[] slots = { UIPositions.MyBoard.ROLE_CARD,
                    UIPositions.MyBoard.CHARACTER_CARD, UIPositions.MyBoard.WEAPON_CARD };
                int col = Math.min(myBoardCurrentCol, slots.length - 1);
                Util.changeBackgroundColor(tg, slots[col], 25, 17, TextColor.ANSI.BLACK_BRIGHT);
            } else if (myBoardCurrentRow == 1) {
                passiveHighlight = myBoardCurrentCol;
            } else {
                handHighlight = myBoardCurrentCol;
            }
        }

        // Field cards
        List<Card> field = me.getField();
        TextImage[] fieldImgs = new TextImage[field.size()];
        for (int i = 0; i < field.size(); i++) fieldImgs[i] = getCardImage(field.get(i));
        if (fieldImgs.length > 0)
            Util.placeCards(tg, fieldImgs, UIPositions.MyBoard.PASSIVE_CARDS, 88, passiveHighlight);

        // Hand cards
        List<Card> hand = me.getHand();
        TextImage[] handImgs = new TextImage[hand.size()];
        for (int i = 0; i < hand.size(); i++) handImgs[i] = getCardImage(hand.get(i));
        if (handImgs.length > 0)
            Util.placeCards(tg, handImgs, UIPositions.MyBoard.HAND_CARDS, 88, handHighlight);
    }

    private void renderTargetBoard(TextGraphics tg) {
        Player target = game.players.get(currentTargetIndex);

        Util.placeImage(tg, UIPositions.TargetBoard.BOARD, Assets.BOARD);

        // HP bullets
        for (int i = 0; i < target.getMaxHp() && i < UIPositions.TargetBoard.BULLETS.length; i++) {
            String color = i < target.getHp() ? "yellow_bright" : "black_bright";
            Util.placeImage(tg, UIPositions.TargetBoard.BULLETS[i], Assets.BULLET, color);
        }

        // Hand count display
        Util.placeImage(tg, UIPositions.TargetBoard.HAND_COUNT, Assets.NUMCARDS_DISPLAY);
        tg.putString(UIPositions.TargetBoard.HAND_COUNT.withColumn(
            UIPositions.TargetBoard.HAND_COUNT.getColumn() + 2).withRow(
            UIPositions.TargetBoard.HAND_COUNT.getRow() + 1), "X" + target.getHand().size());

        // Row 0: role, character, weapon
        Util.placeImage(tg, UIPositions.TargetBoard.ROLE_CARD, getRoleImage(target.getRole()));
        Util.placeImage(tg, UIPositions.TargetBoard.CHARACTER_CARD, getCharacterImage(target.getCharacter()));
        Util.placeImage(tg, UIPositions.TargetBoard.WEAPON_CARD,
            target.getWeapon() != null ? getWeaponImage(target.getWeapon().range) : Assets.FRAME2);

        int passiveHighlight = -1;
        if (currentFocus == FocusArea.TARGET_BOARD) {
            currentTextMode = TextAreaMode.CARD_INFO;
            if (targetBoardCurrentRow == 0) {
                TerminalPosition[] slots = { UIPositions.TargetBoard.ROLE_CARD,
                    UIPositions.TargetBoard.CHARACTER_CARD, UIPositions.TargetBoard.WEAPON_CARD };
                int col = Math.min(targetBoardCurrentCol, slots.length - 1);
                Util.changeBackgroundColor(tg, slots[col], 25, 17, TextColor.ANSI.BLACK_BRIGHT);
            } else {
                passiveHighlight = targetBoardCurrentCol;
            }
        }

        // Field cards
        List<Card> field = target.getField();
        TextImage[] fieldImgs = new TextImage[field.size()];
        for (int i = 0; i < field.size(); i++) fieldImgs[i] = getCardImage(field.get(i));
        if (fieldImgs.length > 0)
            Util.placeCards(tg, fieldImgs, UIPositions.TargetBoard.PASSIVE_CARDS, 88, passiveHighlight);

        // Nickname with navigation hint
        String nick = "< " + target.getName() + " >";
        tg.putString(UIPositions.TargetBoard.NICKNAME, nick);
    }

    private void renderPlayerList(TextGraphics tg) {
        int n = Math.min(game.players.size(), UIPositions.PlayerList.ICONS.length);
        for (int i = 0; i < n; i++) {
            Player p = game.players.get(i);
            String color = p.isAlive() ? (i == game.getCurrentPlayerIdx() ? "yellow_bright" : "white") : "black_bright";
            Util.placeImage(tg, UIPositions.PlayerList.ICONS[i], Assets.USER_ICON, color);
            TerminalPosition pos = UIPositions.PlayerList.NICKNAMES[i];
            tg.putString(pos.withColumn(pos.getColumn() - p.getName().length() / 2), p.getName());
        }
    }

    private void renderTableCenter(TextGraphics tg) {
        Util.placeImage(tg, UIPositions.TableCenter.MAIN_DECK, Assets.FRAME, "yellow");
        Util.placeImage(tg, UIPositions.TableCenter.DISCARD_PILE, Assets.FRAME);
    }

    private void renderTextArea(TextGraphics tg) {
        TerminalPosition start = UIPositions.TextArea.TEXT_START;

        if (currentFocus == FocusArea.TEXT_AREA) {
            tg.putString(start.withRow(start.getRow() - 1), "< Pointing Text Area >   ");
        }

        GameState state = game.getState();

        if (state == GameState.SELECT_TARGET) {
            tg.putString(start, "[ Select Target ] (Arrow L/R, Enter confirm, Esc cancel)");
            List<Player> candidates = game.getTargetCandidates();
            for (int i = 0; i < candidates.size(); i++) {
                Player c = candidates.get(i);
                String prefix = (i == selectedTargetCandidateIdx) ? "> " : "  ";
                tg.putString(start.withRow(start.getRow() + 1 + i),
                    prefix + c.getName() + " HP:" + c.getHp() + "/" + c.getMaxHp());
            }
        } else if (state == GameState.GENERAL_STORE) {
            tg.putString(start, "[ General Store ] (Arrow L/R, Enter to pick)");
            Player picker = game.players.get(game.getGeneralStorePickerIdx());
            tg.putString(start.withRow(start.getRow() + 1), "Picking: " + picker.getName());
            List<Card> pool = game.getGeneralStorePool();
            for (int i = 0; i < pool.size(); i++) {
                String prefix = (i == generalStoreSelectedIdx) ? "> " : "  ";
                tg.putString(start.withRow(start.getRow() + 2 + i),
                    prefix + "[" + i + "] " + pool.get(i).getName() + " " + pool.get(i).getSuit());
            }
        } else if (state == GameState.GAME_OVER) {
            tg.putString(start, "[ GAME OVER ]");
            renderLogLines(tg, start.withRow(start.getRow() + 1), 15);
        } else if (currentTextMode == TextAreaMode.CARD_INFO) {
            tg.putString(start, "[ Card Info ]  F=End Turn  Enter=Play  Q/E=Browse");
            renderSelectedCardInfo(tg, start.withRow(start.getRow() + 1));
        } else {
            tg.putString(start, "[ Game Log ]   F=End Turn  Enter=Play  Q/E=Browse");
            renderLogLines(tg, start.withRow(start.getRow() + 1), 17);
        }

        // Turn indicator
        Player cur = game.getCurrentPlayer();
        String turnInfo = "Turn: " + cur.getName() + " | " + state;
        tg.putString(start.withRow(start.getRow() + 19), turnInfo);
    }

    private void renderLogLines(TextGraphics tg, TerminalPosition pos, int maxLines) {
        List<String> log = game.getLog();
        int start = Math.max(0, log.size() - maxLines);
        for (int i = start; i < log.size(); i++) {
            String line = log.get(i);
            if (line.length() > 100) line = line.substring(0, 100);
            tg.putString(pos.withRow(pos.getRow() + (i - start)), line);
        }
    }

    private void renderSelectedCardInfo(TextGraphics tg, TerminalPosition pos) {
        Player me = game.players.get(myPlayerIdx);
        if (currentFocus == FocusArea.MY_BOARD && myBoardCurrentRow == 2) {
            List<Card> hand = me.getHand();
            if (myBoardCurrentCol < hand.size()) {
                Card c = hand.get(myBoardCurrentCol);
                tg.putString(pos, "[" + myBoardCurrentCol + "] " + c.getName()
                    + "  " + c.getCardType() + "  " + c.getSuit() + "  " + c.getValue());
                tg.putString(pos.withRow(pos.getRow() + 1), "Press Enter to play this card.");
            }
        } else if (currentFocus == FocusArea.MY_BOARD && myBoardCurrentRow == 0) {
            tg.putString(pos, "Role: " + me.getRole() + "  Char: " + me.getCharacter().getName());
        }
    }

    // --- asset helpers ---

    private TextImage getCardImage(Card c) {
        if (c instanceof WeaponCard) return getWeaponImage(((WeaponCard) c).range);
        if (c instanceof BarrelCard)  return Assets.FRAME;
        if (c instanceof JailCard)    return Assets.FRAME;
        if (c instanceof DynamiteCard) return Assets.FRAME;
        return Assets.FRAME;
    }

    private TextImage getWeaponImage(int range) {
        switch (range) {
            case 1: return Assets.GUN1;
            case 2: return Assets.GUN2;
            case 3: return Assets.GUN3;
            case 4: return Assets.GUN4;
            case 5: return Assets.GUN5;
            default: return Assets.FRAME2;
        }
    }

    private TextImage getCharacterImage(CharDef ch) {
        if (ch instanceof WillyTheKid) return Assets.WILLY_THE_KID;
        if (ch instanceof CalamityJanet) return Assets.CALAMITY_JANET;
        return Assets.FRAME2;
    }

    private TextImage getRoleImage(Role role) {
        if (role == null) return Assets.FRAME2;
        switch (role) {
            case SHERIFF: return Assets.SHERIFF;
            case DEPUTY:  return Assets.VICE;
            default:      return Assets.FRAME2;
        }
    }
}
