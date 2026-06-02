package scene;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.graphics.TextImage;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import net.BangClient;
import net.ClientGameState;
import net.ClientGameState.*;

import tools.Assets;
import tools.Util;
import tools.UIPositions;

import java.io.IOException;
import java.util.List;

public class GamePlayScene implements Scene {

    // --- scene state ---

    private enum TextAreaMode { LOG, CARD_INFO }
    private enum FocusArea    { MY_BOARD, TEXT_AREA, TARGET_BOARD }

    private final BangClient client;

    private int currentTargetIndex       = 0;
    private int selectedTargetCandidateIdx = 0;
    private int generalStoreSelectedIdx   = 0;

    private TextAreaMode currentTextMode;
    private FocusArea    currentFocus;

    private int myBoardCurrentRow;
    private int myBoardCurrentCol;
    private int targetBoardCurrentRow;
    private int targetBoardCurrentCol;

    public GamePlayScene(BangClient client) {
        this.client = client;
    }

    // --- lifecycle ---

    @Override
    public void enter() {
        int myIdx = client.getState().myPlayerIdx;
        int total = client.getState().players.size();
        currentTargetIndex = (total > 1) ? (myIdx + 1) % total : 0;
        currentTextMode             = TextAreaMode.LOG;
        currentFocus                = FocusArea.MY_BOARD;
        myBoardCurrentRow           = 2;
        myBoardCurrentCol           = 0;
        targetBoardCurrentRow       = 0;
        targetBoardCurrentCol       = 0;
        selectedTargetCandidateIdx  = 0;
        generalStoreSelectedIdx     = 0;
    }

    @Override public void exit() {}

    // --- input ---

    @Override
    public void handleInput(KeyStroke key) throws IOException {
        ClientGameState cs   = client.getState();
        KeyType         type = key.getKeyType();

        // Lobby: ignore all input
        if (cs.isLobby()) return;

        String state = cs.gameState;

        if ("SELECT_TARGET".equals(state)) { handleTargetSelectionInput(key, cs); return; }
        if ("GENERAL_STORE".equals(state)) { handleGeneralStoreInput(key, cs);    return; }
        if ("GAME_OVER".equals(state))     return;

        if (type == KeyType.Tab) {
            FocusArea[] areas = FocusArea.values();
            currentFocus = areas[(currentFocus.ordinal() + 1) % areas.length];
            if (currentFocus == FocusArea.MY_BOARD)     { myBoardCurrentRow = 2;    myBoardCurrentCol = 0; }
            else if (currentFocus == FocusArea.TARGET_BOARD) { targetBoardCurrentRow = 0; targetBoardCurrentCol = 0; }
        }
        else if (type == KeyType.Enter)  { handleEnterKey(cs); }
        else if (type == KeyType.Escape) { client.sendAction("{\"type\":\"CANCEL_TARGET\"}"); }
        else if (type == KeyType.Character) {
            char c = key.getCharacter();
            if (c == '1') currentTextMode = TextAreaMode.LOG;
            if (c == '2') currentTextMode = TextAreaMode.CARD_INFO;
            int n = Math.max(1, cs.players.size());
            if (c == 'q' || c == 'Q') {
                do { currentTargetIndex = (currentTargetIndex - 1 + n) % n; }
                while (n > 1 && currentTargetIndex == cs.myPlayerIdx);
            }
            if (c == 'e' || c == 'E') {
                do { currentTargetIndex = (currentTargetIndex + 1) % n; }
                while (n > 1 && currentTargetIndex == cs.myPlayerIdx);
            }
            if ((c == 'f' || c == 'F') && cs.isMyTurn())
                client.sendAction("{\"type\":\"END_TURN\"}");
            if ((c == 's' || c == 'S') && cs.isMyTurn())
                client.sendAction("{\"type\":\"SID_HEAL\"}");
        }
        else if (type == KeyType.ArrowLeft  || type == KeyType.ArrowRight ||
                 type == KeyType.ArrowUp    || type == KeyType.ArrowDown) {
            if      (currentFocus == FocusArea.MY_BOARD)     handleMyBoardInput(type, cs);
            else if (currentFocus == FocusArea.TARGET_BOARD) handleTargetBoardInput(type, cs);
            else if (currentFocus == FocusArea.TEXT_AREA)    handleTextAreaInput(type);
        }
    }

    private void handleEnterKey(ClientGameState cs) {
        if (currentFocus == FocusArea.MY_BOARD && myBoardCurrentRow == 2
                && "PLAY".equals(cs.gameState) && cs.isMyTurn()) {
            client.sendAction("{\"type\":\"PLAY_CARD\",\"cardIdx\":" + myBoardCurrentCol + "}");
        }
    }

    private void handleTargetSelectionInput(KeyStroke key, ClientGameState cs) {
        KeyType type = key.getKeyType();
        int     n    = cs.targets.size();
        if (n == 0) return;
        if      (type == KeyType.ArrowLeft)  selectedTargetCandidateIdx = (selectedTargetCandidateIdx - 1 + n) % n;
        else if (type == KeyType.ArrowRight) selectedTargetCandidateIdx = (selectedTargetCandidateIdx + 1) % n;
        else if (type == KeyType.Enter) {
            client.sendAction("{\"type\":\"CONFIRM_TARGET\",\"targetIdx\":" + selectedTargetCandidateIdx + "}");
            selectedTargetCandidateIdx = 0;
        }
        else if (type == KeyType.Escape)
            client.sendAction("{\"type\":\"CANCEL_TARGET\"}");
    }

    private void handleGeneralStoreInput(KeyStroke key, ClientGameState cs) {
        KeyType type = key.getKeyType();
        int     n    = cs.storePool.size();
        if (n == 0) return;
        if      (type == KeyType.ArrowLeft)  generalStoreSelectedIdx = (generalStoreSelectedIdx - 1 + n) % n;
        else if (type == KeyType.ArrowRight) generalStoreSelectedIdx = (generalStoreSelectedIdx + 1) % n;
        else if (type == KeyType.Enter && cs.storePickerIdx == cs.myPlayerIdx) {
            client.sendAction("{\"type\":\"PICK_STORE\",\"poolIdx\":" + generalStoreSelectedIdx + "}");
            generalStoreSelectedIdx = 0;
        }
    }

    private void handleMyBoardInput(KeyType arrowKey, ClientGameState cs) {
        PlayerSnap me = cs.myPlayer();
        if (arrowKey == KeyType.ArrowUp) {
            int prev = myBoardCurrentRow;
            myBoardCurrentRow = Math.max(0, myBoardCurrentRow - 1);
            if (myBoardCurrentRow != prev) myBoardCurrentCol = 0;
            else { currentFocus = FocusArea.TARGET_BOARD; targetBoardCurrentRow = 0; targetBoardCurrentCol = 0; }
        } else if (arrowKey == KeyType.ArrowDown) {
            int prev = myBoardCurrentRow;
            myBoardCurrentRow = Math.min(2, myBoardCurrentRow + 1);
            if (myBoardCurrentRow != prev) myBoardCurrentCol = 0;
            else currentFocus = FocusArea.TEXT_AREA;
        } else if (arrowKey == KeyType.ArrowLeft) {
            myBoardCurrentCol = Math.max(0, myBoardCurrentCol - 1);
        } else if (arrowKey == KeyType.ArrowRight) {
            int maxCol;
            if      (myBoardCurrentRow == 0) maxCol = 2;
            else if (myBoardCurrentRow == 1) maxCol = me != null ? Math.max(0, me.field.size() - 1) : 0;
            else                             maxCol = me != null ? Math.max(0, me.hand.size()  - 1) : 0;
            myBoardCurrentCol = Math.min(maxCol, myBoardCurrentCol + 1);
        }
    }

    private void handleTargetBoardInput(KeyType arrowKey, ClientGameState cs) {
        int n = cs.players.size();
        PlayerSnap target = n > 0 ? cs.players.get(currentTargetIndex % n) : null;
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
            int maxCol = (targetBoardCurrentRow == 0) ? 2
                : (target != null ? Math.max(0, target.field.size() - 1) : 0);
            targetBoardCurrentCol = Math.min(maxCol, targetBoardCurrentCol + 1);
        }
    }

    private void handleTextAreaInput(KeyType arrowKey) {
        if      (arrowKey == KeyType.ArrowUp)   { currentFocus = FocusArea.MY_BOARD;     myBoardCurrentRow = 2;    myBoardCurrentCol = 0; }
        else if (arrowKey == KeyType.ArrowDown) { currentFocus = FocusArea.TARGET_BOARD; targetBoardCurrentRow = 0; targetBoardCurrentCol = 0; }
    }

    // --- render ---

    @Override
    public void render(TextGraphics tg) {
        ClientGameState cs = client.getState();
        if (cs.isLobby()) { renderLobby(tg, cs); return; }
        renderBackgroundFrames(tg);
        renderMyBoard(tg, cs);
        renderTargetBoard(tg, cs);
        renderPlayerList(tg, cs);
        renderTableCenter(tg);
        renderTextArea(tg, cs);
    }

    // --- lobby screen ---

    private void renderLobby(TextGraphics tg, ClientGameState cs) {
        TerminalPosition p = new TerminalPosition(10, 10);
        tg.putString(p, "=== BANG! - Waiting for players ===");
        tg.putString(p.withRow(11), "Joined " + cs.lobbyNames.size() + " / " + cs.maxPlayers + ":");
        for (int i = 0; i < cs.lobbyNames.size(); i++)
            tg.putString(p.withRow(12 + i), "  [" + i + "] " + cs.lobbyNames.get(i));
    }

    // --- in-game render ---

    private void renderBackgroundFrames(TextGraphics tg) {
        tg.drawLine(93, 0,  93, 69, '*');
        tg.drawLine(93, 16, 269, 16, '*');
        tg.drawLine(93, 38, 269, 38, '*');
        tg.drawLine(204, 17, 204, 38, '*');
    }

    private void renderMyBoard(TextGraphics tg, ClientGameState cs) {
        PlayerSnap me = cs.myPlayer();
        if (me == null) return;

        Util.placeImage(tg, UIPositions.MyBoard.BOARD, Assets.BOARD);
        tg.putString(UIPositions.MyBoard.NICKNAME, "< " + me.name + " >");

        for (int i = 0; i < me.maxHp && i < UIPositions.MyBoard.BULLETS.length; i++)
            Util.placeImage(tg, UIPositions.MyBoard.BULLETS[i], Assets.BULLET,
                i < me.hp ? "yellow_bright" : "black_bright");

        Util.placeImage(tg, UIPositions.MyBoard.ROLE_CARD,      roleImage(me.role));
        Util.placeImage(tg, UIPositions.MyBoard.CHARACTER_CARD, charImage(me.charName));
        if (me.weapon != null)
            Util.placeImage(tg, UIPositions.MyBoard.WEAPON_CARD, weaponImage(me.weapon.range));

        int passiveHighlight = -1, handHighlight = -1;
        if (currentFocus == FocusArea.MY_BOARD) {
            currentTextMode = TextAreaMode.CARD_INFO;
            if (myBoardCurrentRow == 0) {
                TerminalPosition[] slots = { UIPositions.MyBoard.ROLE_CARD,
                    UIPositions.MyBoard.CHARACTER_CARD, UIPositions.MyBoard.WEAPON_CARD };
                Util.changeBackgroundColor(tg, slots[Math.min(myBoardCurrentCol, 2)],
                    25, 17, TextColor.ANSI.BLACK_BRIGHT);
            } else if (myBoardCurrentRow == 1) passiveHighlight = myBoardCurrentCol;
            else                               handHighlight    = myBoardCurrentCol;
        }

        TextImage[] fieldImgs = cardSnapImages(me.field);
        if (fieldImgs.length > 0)
            Util.placeCards(tg, fieldImgs, UIPositions.MyBoard.PASSIVE_CARDS, 88, passiveHighlight);

        TextImage[] handImgs = cardSnapImages(me.hand);
        if (handImgs.length > 0)
            Util.placeCards(tg, handImgs, UIPositions.MyBoard.HAND_CARDS, 88, handHighlight);
    }

    private void renderTargetBoard(TextGraphics tg, ClientGameState cs) {
        if (cs.players.isEmpty()) return;
        int n = cs.players.size();
        if (currentTargetIndex % n == cs.myPlayerIdx)
            currentTargetIndex = (cs.myPlayerIdx + 1) % n;
        int        idx    = currentTargetIndex % n;
        if (idx == cs.myPlayerIdx) return;
        PlayerSnap target = cs.players.get(idx);

        Util.placeImage(tg, UIPositions.TargetBoard.BOARD, Assets.BOARD);

        for (int i = 0; i < target.maxHp && i < UIPositions.TargetBoard.BULLETS.length; i++)
            Util.placeImage(tg, UIPositions.TargetBoard.BULLETS[i], Assets.BULLET,
                i < target.hp ? "yellow_bright" : "black_bright");

        Util.placeImage(tg, UIPositions.TargetBoard.HAND_COUNT, Assets.NUMCARDS_DISPLAY);
        tg.putString(UIPositions.TargetBoard.HAND_COUNT
            .withColumn(UIPositions.TargetBoard.HAND_COUNT.getColumn() + 2)
            .withRow   (UIPositions.TargetBoard.HAND_COUNT.getRow()    + 1),
            "X" + target.handSize);

        Util.placeImage(tg, UIPositions.TargetBoard.ROLE_CARD,      roleImage(target.role));
        Util.placeImage(tg, UIPositions.TargetBoard.CHARACTER_CARD, charImage(target.charName));
        if (target.weapon != null)
            Util.placeImage(tg, UIPositions.TargetBoard.WEAPON_CARD, weaponImage(target.weapon.range));

        int passiveHighlight = -1;
        if (currentFocus == FocusArea.TARGET_BOARD) {
            currentTextMode = TextAreaMode.CARD_INFO;
            if (targetBoardCurrentRow == 0) {
                TerminalPosition[] slots = { UIPositions.TargetBoard.ROLE_CARD,
                    UIPositions.TargetBoard.CHARACTER_CARD, UIPositions.TargetBoard.WEAPON_CARD };
                Util.changeBackgroundColor(tg, slots[Math.min(targetBoardCurrentCol, 2)],
                    25, 17, TextColor.ANSI.BLACK_BRIGHT);
            } else passiveHighlight = targetBoardCurrentCol;
        }

        TextImage[] fieldImgs = cardSnapImages(target.field);
        if (fieldImgs.length > 0)
            Util.placeCards(tg, fieldImgs, UIPositions.TargetBoard.PASSIVE_CARDS, 88, passiveHighlight);

        tg.putString(UIPositions.TargetBoard.NICKNAME, "< " + target.name + " >");
    }

    private void renderPlayerList(TextGraphics tg, ClientGameState cs) {
        int n = Math.min(cs.players.size(), UIPositions.PlayerList.ICONS.length);
        for (int i = 0; i < n; i++) {
            PlayerSnap p = cs.players.get(i);
            String color = !p.alive ? "black_bright"
                : (i == cs.currentPlayerIdx ? "yellow_bright" : "white");
            Util.placeImage(tg, UIPositions.PlayerList.ICONS[i], Assets.USER_ICON, color);
            TerminalPosition pos = UIPositions.PlayerList.NICKNAMES[i];
            tg.putString(pos.withColumn(pos.getColumn() - p.name.length() / 2), p.name);
        }
    }

    private void renderTableCenter(TextGraphics tg) {
        Util.placeImage(tg, UIPositions.TableCenter.MAIN_DECK,    Assets.FRAME, "yellow");
        Util.placeImage(tg, UIPositions.TableCenter.DISCARD_PILE, Assets.FRAME);
    }

    private void renderTextArea(TextGraphics tg, ClientGameState cs) {
        TerminalPosition start = UIPositions.TextArea.TEXT_START;
        String           state = cs.gameState;

        if (currentFocus == FocusArea.TEXT_AREA)
            tg.putString(start.withRow(start.getRow() - 1), "< Text Area >            ");

        if ("SELECT_TARGET".equals(state)) {
            tg.putString(start, "[ Select Target ]  L/R=browse  Enter=confirm  Esc=cancel");
            for (int i = 0; i < cs.targets.size(); i++) {
                TargetSnap t  = cs.targets.get(i);
                PlayerSnap tp = t.playerIdx < cs.players.size() ? cs.players.get(t.playerIdx) : null;
                String hp = tp != null ? " HP:" + tp.hp + "/" + tp.maxHp : "";
                tg.putString(start.withRow(start.getRow() + 1 + i),
                    (i == selectedTargetCandidateIdx ? "> " : "  ") + t.name + hp);
            }
        } else if ("GENERAL_STORE".equals(state)) {
            tg.putString(start, "[ General Store ]  L/R=browse  Enter=pick");
            String pickerName = cs.storePickerIdx < cs.players.size()
                ? cs.players.get(cs.storePickerIdx).name : "?";
            tg.putString(start.withRow(start.getRow() + 1), "Picking: " + pickerName);
            for (int i = 0; i < cs.storePool.size(); i++) {
                CardSnap c = cs.storePool.get(i);
                tg.putString(start.withRow(start.getRow() + 2 + i),
                    (i == generalStoreSelectedIdx ? "> " : "  ")
                    + "[" + i + "] " + c.name + " " + c.suit);
            }
        } else if ("GAME_OVER".equals(state)) {
            tg.putString(start, "[ GAME OVER ]");
            renderLogLines(tg, start.withRow(start.getRow() + 1), cs, 15);
        } else if (currentTextMode == TextAreaMode.CARD_INFO) {
            tg.putString(start, "[ Card Info ]  F=EndTurn  Enter=Play  Q/E=Browse  S=SidHeal");
            renderSelectedCardInfo(tg, start.withRow(start.getRow() + 1), cs);
        } else {
            tg.putString(start, "[ Game Log ]   F=EndTurn  Enter=Play  Q/E=Browse  S=SidHeal");
            renderLogLines(tg, start.withRow(start.getRow() + 1), cs, 17);
        }

        String curName = cs.currentPlayerIdx < cs.players.size()
            ? cs.players.get(cs.currentPlayerIdx).name : "?";
        tg.putString(start.withRow(start.getRow() + 19),
            "Turn: " + curName + " | " + state + (cs.isMyTurn() ? " ★ YOUR TURN" : ""));
    }

    private void renderLogLines(TextGraphics tg, TerminalPosition pos,
                                ClientGameState cs, int maxLines) {
        List<String> log = cs.log;
        int from = Math.max(0, log.size() - maxLines);
        for (int i = from; i < log.size(); i++) {
            String line = log.get(i);
            if (line.length() > 108) line = line.substring(0, 108);
            tg.putString(pos.withRow(pos.getRow() + (i - from)), line);
        }
    }

    private void renderSelectedCardInfo(TextGraphics tg, TerminalPosition pos,
                                        ClientGameState cs) {
        PlayerSnap me = cs.myPlayer();
        if (me == null) return;
        if (currentFocus == FocusArea.MY_BOARD && myBoardCurrentRow == 2
                && myBoardCurrentCol < me.hand.size()) {
            CardSnap c = me.hand.get(myBoardCurrentCol);
            tg.putString(pos, "[" + myBoardCurrentCol + "] " + c.name
                + "  " + c.type + "  " + c.suit + "  " + c.value);
            if (cs.isMyTurn())
                tg.putString(pos.withRow(pos.getRow() + 1), "Press Enter to play.");
        } else if (currentFocus == FocusArea.MY_BOARD && myBoardCurrentRow == 0) {
            tg.putString(pos, "Role: " + me.role + "   Char: " + me.charName);
        }
    }

    // --- asset helpers (name-based, no GameLogic class references) ---

    private TextImage[] cardSnapImages(List<CardSnap> cards) {
        TextImage[] imgs = new TextImage[cards.size()];
        for (int i = 0; i < cards.size(); i++) imgs[i] = cardSnapImage(cards.get(i));
        return imgs;
    }

    private TextImage cardSnapImage(CardSnap c) {
        TextImage base = c.weaponRange > 0 ? handWeaponImage(c.weaponRange) : playCardImage(c.name);
        if ("BROWN".equals(c.type)) base = Util.colorizeBorder(base, new TextColor.RGB(139, 90, 43));
        else if ("BLUE".equals(c.type)) base = Util.colorizeBorder(base, TextColor.ANSI.BLUE);
        return Util.stampSuitValue(base, c.suit, c.value);
    }

    private TextImage playCardImage(String name) {
        switch (name) {
            case "Bang!":          return Assets.BANG;
            case "Missed!":        return Assets.MISSED;
            case "Beer":           return Assets.BEER;
            case "Saloon":         return Assets.SALOON;
            case "Stagecoach":     return Assets.STAGECOACH;
            case "Wells Fargo":    return Assets.WELLS_FARGO;
            case "General Store":  return Assets.GENERAL_STORE;
            case "Duel":           return Assets.DUEL;
            case "Indians!":       return Assets.INDIANS;
            case "Gatling":        return Assets.GATLING;
            case "Panic!":         return Assets.PANIC;
            case "Cat Balou":      return Assets.CAT_BALOU;
            case "Barrel":         return Assets.BARREL;
            case "Mustang":        return Assets.MUSTANG;
            case "Appaloosa":      return Assets.APPALOOSA;
            case "Jail":           return Assets.JAIL;
            case "Dynamite":       return Assets.DYNAMITE;
            default:               return Assets.FRAME;
        }
    }

    private TextImage handWeaponImage(int range) {
        switch (range) {
            case 1:  return Assets.HAND_GUN1;
            case 2:  return Assets.HAND_GUN2;
            case 3:  return Assets.HAND_GUN3;
            case 4:  return Assets.HAND_GUN4;
            case 5:  return Assets.HAND_GUN5;
            default: return Assets.FRAME;
        }
    }

    private TextImage weaponImage(int range) {
        switch (range) {
            case 1:  return Assets.GUN1;
            case 2:  return Assets.GUN2;
            case 3:  return Assets.GUN3;
            case 4:  return Assets.GUN4;
            case 5:  return Assets.GUN5;
            default: return Assets.FRAME;
        }
    }

    private TextImage charImage(String charName) {
        if ("Bart Cassidy".equals(charName))   return Assets.BART_CASSIDY;
        if ("Black Jack".equals(charName))     return Assets.BLACK_JACK;
        if ("Calamity Janet".equals(charName)) return Assets.CALAMITY_JANET;
        if ("Jourdonnais".equals(charName))    return Assets.JOURDONNAIS;
        if ("Kit Carlson".equals(charName))    return Assets.KIT_CARLSON;
        if ("Lucky Duke".equals(charName))     return Assets.LUCKY_DUKE;
        if ("Sid Ketchum".equals(charName))    return Assets.SID_KETCHUM;
        if ("Willy the Kid".equals(charName))  return Assets.WILLY_THE_KID;
        return Assets.FRAME2;
    }

    private TextImage roleImage(String role) {
        if ("SHERIFF".equals(role))  return Assets.SHERIFF;
        if ("DEPUTY".equals(role))   return Assets.VICE;
        if ("OUTLAW".equals(role))   return Assets.OUTLAW;
        if ("RENEGADE".equals(role)) return Assets.RENEGADE;
        return Assets.FRAME2;
    }
}
