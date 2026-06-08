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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GamePlayScene implements Scene {

    // --- scene state ---

    // Keyboard focus is split between the player's own board (left panel) and the target board (right panel).
    // Tab cycles between them; arrow keys then navigate within the focused area.
    private enum FocusArea { MY_BOARD, TARGET_BOARD }

    private final BangClient client;

    private int currentTargetIndex        = 0;
    private int selectedTargetCandidateIdx = 0;
    private int generalStoreSelectedIdx   = 0;
    private int catBalouSelectedIdx       = 0;

    private FocusArea currentFocus;

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
        currentFocus                = FocusArea.MY_BOARD;
        myBoardCurrentRow           = 2;
        myBoardCurrentCol           = 0;
        targetBoardCurrentRow       = 0;
        targetBoardCurrentCol       = 0;
        selectedTargetCandidateIdx  = 0;
        generalStoreSelectedIdx     = 0;
        catBalouSelectedIdx         = 0;
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

        if ("SELECT_TARGET".equals(state))    { handleTargetSelectionInput(key, cs); return; }
        if ("SELECT_CAT_BALOU".equals(state)) { handleCatBalouPickInput(key, cs);    return; }
        if ("GENERAL_STORE".equals(state))    { handleGeneralStoreInput(key, cs);    return; }
        if ("GAME_OVER".equals(state))        return;

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

    private void handleCatBalouPickInput(KeyStroke key, ClientGameState cs) {
        if (!cs.isMyTurn()) return;
        int idx = cs.catBalouTargetIdx;
        if (idx < 0 || idx >= cs.players.size()) return;
        PlayerSnap target = cs.players.get(idx);
        int n = catBalouChoiceCount(target);
        if (n == 0) return;

        KeyType type = key.getKeyType();
        if      (type == KeyType.ArrowLeft)  catBalouSelectedIdx = (catBalouSelectedIdx - 1 + n) % n;
        else if (type == KeyType.ArrowRight) catBalouSelectedIdx = (catBalouSelectedIdx + 1) % n;
        else if (type == KeyType.Enter) {
            client.sendAction("{\"type\":\"CAT_BALOU_PICK\",\"choiceIdx\":" + catBalouSelectedIdx + "}");
            catBalouSelectedIdx = 0;
        }
    }

    private int catBalouChoiceCount(PlayerSnap target) {
        return (target.handSize > 0 ? 1 : 0) + target.field.size() + (target.weapon != null ? 1 : 0);
    }

    // My board has 3 rows: row 0 = role/character/weapon cards, row 1 = field (passive) cards, row 2 = hand cards.
    // Pressing Up at row 0 or Down at row 2 transfers focus to the target board.
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
            else { currentFocus = FocusArea.TARGET_BOARD; targetBoardCurrentRow = 0; targetBoardCurrentCol = 0; }
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
            else { currentFocus = FocusArea.MY_BOARD; myBoardCurrentRow = 2; myBoardCurrentCol = 0; }
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

    // --- render ---

    @Override
    public void render(TextGraphics tg) {
        ClientGameState cs = client.getState();
        if (cs.isLobby()) { renderLobby(tg, cs); return; }
        renderBackgroundFrames(tg);
        renderMyBoard(tg, cs);
        renderTargetBoard(tg, cs);
        renderPlayerList(tg, cs);
        renderTableCenter(tg, cs);
        renderTextArea(tg, cs);
        renderGameLog(tg, cs);
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

        boolean roleVisible = "SHERIFF".equals(target.role) || !target.alive;
        Util.placeImage(tg, UIPositions.TargetBoard.ROLE_CARD,
            roleVisible ? roleImage(target.role) : Assets.FRAME2);
        Util.placeImage(tg, UIPositions.TargetBoard.CHARACTER_CARD, charImage(target.charName));
        if (target.weapon != null)
            Util.placeImage(tg, UIPositions.TargetBoard.WEAPON_CARD, weaponImage(target.weapon.range));

        int passiveHighlight = -1;
        if (currentFocus == FocusArea.TARGET_BOARD) {
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

    private void renderGameLog(TextGraphics tg, ClientGameState cs) {
        TerminalPosition pos = UIPositions.TextArea.LOG;
        for (int i = 0; i < cs.log.size(); i++) {
            String line = cs.log.get(i);
            if (line.length() > 105) line = line.substring(0, 105);
            tg.putString(pos.withRow(pos.getRow() + i), line);
        }
    }

    private void renderTableCenter(TextGraphics tg, ClientGameState cs) {
        Util.placeImage(tg, UIPositions.TableCenter.MAIN_DECK, Assets.FRAME, "yellow");
        if (cs.topDiscard != null)
            Util.placeImage(tg, UIPositions.TableCenter.DISCARD_PILE, cardSnapImage(cs.topDiscard));
    }

    private void renderTextArea(TextGraphics tg, ClientGameState cs) {
        TerminalPosition start = UIPositions.TextArea.TEXT_START;
        String           state = cs.gameState;

        if ("SELECT_TARGET".equals(state)) {
            tg.putString(start, "[ Select Target ]  L/R=browse  Enter=confirm  Esc=cancel");
            for (int i = 0; i < cs.targets.size(); i++) {
                TargetSnap t  = cs.targets.get(i);
                PlayerSnap tp = t.playerIdx < cs.players.size() ? cs.players.get(t.playerIdx) : null;
                String hp = tp != null ? " HP:" + tp.hp + "/" + tp.maxHp : "";
                tg.putString(start.withRow(start.getRow() + 1 + i),
                    (i == selectedTargetCandidateIdx ? "> " : "  ") + t.name + hp);
            }
        } else if ("SELECT_CAT_BALOU".equals(state)) {
            renderCatBalouPick(tg, start, cs);
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
        } else {
            tg.putString(start, "F=EndTurn  Enter=Play  Q/E=Browse");
            renderSelectedCardInfo(tg, UIPositions.TextArea.CARD_INFO, cs);
        }

        String curName = cs.currentPlayerIdx < cs.players.size()
            ? cs.players.get(cs.currentPlayerIdx).name : "?";
        tg.putString(start.withRow(start.getRow() + 19),
            "Turn: " + curName + (cs.isMyTurn() ? " | YOUR TURN" : ""));
    }

    // =========================================================================
    // Card & character description tables (sourced from bang_card_list_en.md)
    // =========================================================================

    /** Maps each play-card name to its 1-line description. */
    private static final Map<String, String> CARD_DESCRIPTIONS = new HashMap<>();

    /** Maps each character name to its ability description. */
    private static final Map<String, String> CHAR_DESCRIPTIONS = new HashMap<>();

    /** Maps each role name (enum .name()) to its description. */
    private static final Map<String, String> ROLE_DESCRIPTIONS = new HashMap<>();

    static {
        // --- Brown-bordered action cards ---
        // '\n' marks where the original bang_card_list_en.md placed a ↵ line-break indicator
        CARD_DESCRIPTIONS.put("Bang!",
            "The main method to reduce other players' life points. Play against a player within your weapon's range.\n" +
            "Limit 1 per turn.");
        CARD_DESCRIPTIONS.put("Missed!",
            "Play immediately when targeted by a BANG! - even out of turn - to cancel the shot.");
        CARD_DESCRIPTIONS.put("Beer",
            "Regain one life point. Cannot exceed your starting life point total. Can be played out of turn only when\n" +
            "receiving a lethal hit. No effect when only 2 players remain.");
        CARD_DESCRIPTIONS.put("Cat Balou",
            "Force any one player to discard a card (a random card from hand, or a chosen card in play), regardless\n" +
            "of the distance.");
        CARD_DESCRIPTIONS.put("Panic!",
            "Draw a card from a player at distance 1 (a random card from hand, or a chosen card in play). Distance is\n" +
            "not modified by weapons, only by Mustang/Scope.");
        CARD_DESCRIPTIONS.put("Duel",
            "Challenge any other player, regardless of distance. The challenged player may discard a BANG!; if he\n" +
            "does, you may discard a BANG!, and so on. The first player failing to discard a BANG! loses one life\n" +
            "point.");
        CARD_DESCRIPTIONS.put("Indians!",
            "Each other player may discard a BANG! card, or lose one life point.");
        CARD_DESCRIPTIONS.put("Stagecoach",
            "Draw two cards from the top of the deck.");
        CARD_DESCRIPTIONS.put("General Store",
            "Turn as many cards face up as the number of players still playing. Starting with you and proceeding\n" +
            "clockwise, each player chooses one card and adds it to his hand.");
        CARD_DESCRIPTIONS.put("Gatling",
            "Shoots a BANG! to all other players, regardless of the distance.");
        CARD_DESCRIPTIONS.put("Saloon",
            "All players in play regain one life point.");
        CARD_DESCRIPTIONS.put("Wells Fargo",
            "Draw three cards from the top of the deck.");

        // --- Blue-bordered equipment cards ---
        CARD_DESCRIPTIONS.put("Barrel",
            "When targeted by a BANG!, you may \"draw!\": on a Heart, the BANG! is cancelled (as if you played a\n" +
            "Missed!); otherwise, no effect.");
        CARD_DESCRIPTIONS.put("Dynamite",
            "At the start of your next turn, before phase 1, you must \"draw!\": on Spade 2-9 it explodes (lose 3 life\n" +
            "points, card discarded); otherwise, pass it to the player on your left.");
        CARD_DESCRIPTIONS.put("Jail",
            "Play in front of any player (not the Sheriff), regardless of distance. That player must \"draw!\" before\n" +
            "their turn: on a Heart, discard Jail and continue normally; otherwise, discard Jail and skip the turn.");
        CARD_DESCRIPTIONS.put("Mustang",
            "The distance between other players and you is increased by 1. You still see other players at normal\n" +
            "distance.");
        CARD_DESCRIPTIONS.put("Scope",
            "You see all other players at a distance decreased by 1 (minimum 1). Other players still see you at\n" +
            "normal distance.");

        // --- Weapon (gun) cards ---
        CARD_DESCRIPTIONS.put("Colt .45",
            "Default weapon. Can shoot players at distance 1. No card needed.");
        CARD_DESCRIPTIONS.put("Volcanic",
            "You may play any number of BANG! cards during your turn, but only at distance 1.");
        CARD_DESCRIPTIONS.put("Schofield",     "Extends your shooting range to distance 2.");
        CARD_DESCRIPTIONS.put("Remington",     "Extends your shooting range to distance 3.");
        CARD_DESCRIPTIONS.put("Rev. Carabine", "Extends your shooting range to distance 4.");
        CARD_DESCRIPTIONS.put("Winchester",    "Extends your shooting range to distance 5.");

        // --- Character ability descriptions ---
        CHAR_DESCRIPTIONS.put("Willy the Kid",
            "He can play any number of BANG! cards during his turn.");
        CHAR_DESCRIPTIONS.put("Calamity Janet",
            "She can use BANG! cards as Missed! cards and vice versa. If she plays a Missed! as a BANG!, she cannot\n" +
            "play another BANG! card that turn (unless she has a Volcanic in play).");
        CHAR_DESCRIPTIONS.put("Kit Carlson",
            "During phase 1 of his turn, he looks at the top three cards of the deck: he chooses 2 to draw, and puts\n" +
            "the other one back on the top of the deck, face down.");
        CHAR_DESCRIPTIONS.put("Bart Cassidy",
            "Each time he loses a life point, he immediately draws a card from the deck.");
        CHAR_DESCRIPTIONS.put("Sid Ketchum",
            "At any time, he may discard 2 cards from his hand to regain one life point. He cannot exceed his\n" +
            "starting life point total. [S key to activate]");
        CHAR_DESCRIPTIONS.put("Lucky Duke",
            "Each time he is required to \"draw!\", he flips the top two cards from the deck, and chooses the result he\n" +
            "prefers. Discard both cards afterwards.");
        CHAR_DESCRIPTIONS.put("Jourdonnais",
            "He is considered to have a Barrel in play at all times; he can \"draw!\" when he is the target of a BANG!,\n" +
            "and on a Heart he is missed.");
        CHAR_DESCRIPTIONS.put("Black Jack",
            "During phase 1 of his turn, he must show the second card he draws: if it's Heart or Diamonds (just like\n" +
            "a \"draw!\"), he draws one additional card (without revealing it).");

        // --- Role descriptions ---
        ROLE_DESCRIPTIONS.put("SHERIFF",
            "Must eliminate all the Outlaws and the Renegade, to protect law and order. Revealed at the start.");
        ROLE_DESCRIPTIONS.put("DEPUTY",
            "Helps and protects the Sheriff, and shares his same goal, at all costs. Role kept secret.");
        ROLE_DESCRIPTIONS.put("OUTLAW",
            "Wants to kill the Sheriff. Role kept secret. Any player who eliminates an Outlaw draws 3 cards as a\n" +
            "reward.");
        ROLE_DESCRIPTIONS.put("RENEGADE",
            "Wants to be the last character in play. Role kept secret. Wins only if he is the sole survivor when the\n" +
            "Sheriff dies.");
    }

    // =========================================================================

    /**
     * Renders a description string that may contain '\n' line-break markers.
     * Splits on '\n' and draws each segment on successive rows.
     * Returns the number of rows consumed, so callers can offset subsequent lines.
     */
    private int putDescription(TextGraphics tg, TerminalPosition pos, String desc) {
        if (desc == null) return 0;
        String[] lines = desc.split("\n");
        for (int i = 0; i < lines.length; i++)
            tg.putString(pos.withRow(pos.getRow() + i), lines[i]);
        return lines.length;
    }

    private void renderSelectedCardInfo(TextGraphics tg, TerminalPosition pos,
                                        ClientGameState cs) {
        PlayerSnap me = cs.myPlayer();
        if (me == null) return;

        if (currentFocus == FocusArea.MY_BOARD && myBoardCurrentRow == 2
                && myBoardCurrentCol < me.hand.size()) {
            // --- Hand card selected ---
            CardSnap c = me.hand.get(myBoardCurrentCol);
            // Line +0: card index, name, type, suit, value
            tg.putString(pos, "[" + myBoardCurrentCol + "] " + c.name
                + "  " + c.type + "  " + c.suit + "  " + c.value);
            // Lines +1…+N: description split at ↵ markers (suit-bearing card → description below name)
            int descLines = putDescription(tg, pos.withRow(pos.getRow() + 1), CARD_DESCRIPTIONS.get(c.name));
            // Play prompt follows immediately after the last description line
            if (cs.isMyTurn())
                tg.putString(pos.withRow(pos.getRow() + 1 + descLines), "Press Enter to play.");

        } else if (currentFocus == FocusArea.MY_BOARD && myBoardCurrentRow == 1
                && myBoardCurrentCol < me.field.size()) {
            // --- Field (passive/equipment) card selected ---
            CardSnap c = me.field.get(myBoardCurrentCol);
            // Line +0: card name, type, suit, value
            tg.putString(pos, c.name + "  " + c.type + "  " + c.suit + "  " + c.value);
            // Lines +1…+N: description split at ↵ markers
            putDescription(tg, pos.withRow(pos.getRow() + 1), CARD_DESCRIPTIONS.get(c.name));

        } else if (currentFocus == FocusArea.MY_BOARD && myBoardCurrentRow == 0) {
            if (myBoardCurrentCol == 0) {
                // --- Role card focused (col 0) ---
                tg.putString(pos, "Role: " + me.role);
                putDescription(tg, pos.withRow(pos.getRow() + 1), ROLE_DESCRIPTIONS.get(me.role));
            } else if (myBoardCurrentCol == 1) {
                // --- Character card focused (col 1) ---
                tg.putString(pos, "Char: " + me.charName);
                putDescription(tg, pos.withRow(pos.getRow() + 1), CHAR_DESCRIPTIONS.get(me.charName));
            } else if (myBoardCurrentCol == 2) {
                // --- Weapon card focused (col 2) ---
                if (me.weapon != null) {
                    tg.putString(pos, "Weapon: " + me.weapon.name + "  range " + me.weapon.range);
                    putDescription(tg, pos.withRow(pos.getRow() + 1), CARD_DESCRIPTIONS.get(me.weapon.name));
                } else {
                    tg.putString(pos, "Weapon: Colt .45 (default)  range 1");
                    putDescription(tg, pos.withRow(pos.getRow() + 1), CARD_DESCRIPTIONS.get("Colt .45"));
                }
            }

        // =====================================================================
        // TARGET BOARD
        // =====================================================================
        } else if (currentFocus == FocusArea.TARGET_BOARD) {
            int n = cs.players.size();
            if (n == 0) return;
            PlayerSnap t = cs.players.get(currentTargetIndex % n);

            if (targetBoardCurrentRow == 0) {
                if (targetBoardCurrentCol == 0) {
                    // --- Target role card (col 0) ---
                    // Role is secret unless the target is the Sheriff or already dead
                    boolean roleVisible = "SHERIFF".equals(t.role) || !t.alive;
                    if (roleVisible) {
                        tg.putString(pos, "Role: " + t.role);
                        putDescription(tg, pos.withRow(pos.getRow() + 1), ROLE_DESCRIPTIONS.get(t.role));
                    } else {
                        tg.putString(pos, "Role: ???  (hidden)");
                    }
                } else if (targetBoardCurrentCol == 1) {
                    // --- Target character card (col 1) ---
                    tg.putString(pos, "Char: " + t.charName);
                    putDescription(tg, pos.withRow(pos.getRow() + 1), CHAR_DESCRIPTIONS.get(t.charName));
                } else if (targetBoardCurrentCol == 2) {
                    // --- Target weapon card (col 2) ---
                    if (t.weapon != null) {
                        tg.putString(pos, "Weapon: " + t.weapon.name + "  range " + t.weapon.range);
                        putDescription(tg, pos.withRow(pos.getRow() + 1), CARD_DESCRIPTIONS.get(t.weapon.name));
                    } else {
                        tg.putString(pos, "Weapon: Colt .45 (default)  range 1");
                        putDescription(tg, pos.withRow(pos.getRow() + 1), CARD_DESCRIPTIONS.get("Colt .45"));
                    }
                }
            } else if (targetBoardCurrentRow == 1
                    && targetBoardCurrentCol < t.field.size()) {
                // --- Target field (passive/equipment) card ---
                CardSnap c = t.field.get(targetBoardCurrentCol);
                tg.putString(pos, c.name + "  " + c.type + "  " + c.suit + "  " + c.value);
                putDescription(tg, pos.withRow(pos.getRow() + 1), CARD_DESCRIPTIONS.get(c.name));
            }
        }
    }

    private void renderCatBalouPick(TextGraphics tg, TerminalPosition start, ClientGameState cs) {
        int idx = cs.catBalouTargetIdx;
        if (idx < 0 || idx >= cs.players.size()) return;
        PlayerSnap target = cs.players.get(idx);

        String actorName = cs.currentPlayerIdx < cs.players.size()
            ? cs.players.get(cs.currentPlayerIdx).name : "?";

        if (!cs.isMyTurn()) {
            tg.putString(start, "[ Cat Balou ]  " + actorName + " is choosing what to discard from " + target.name + "...");
            return;
        }

        tg.putString(start, "[ Cat Balou ]  Choose what to discard from " + target.name + ":");

        // Build choices: hand (random) → field cards → weapon
        java.util.List<String> choices = new java.util.ArrayList<>();
        if (target.handSize > 0)
            choices.add("Hand  (" + target.handSize + " cards — random pick)");
        for (CardSnap c : target.field)
            choices.add(c.name + "  [field card]");
        if (target.weapon != null)
            choices.add(target.weapon.name + "  [weapon]");

        for (int i = 0; i < choices.size(); i++) {
            tg.putString(start.withRow(start.getRow() + 1 + i),
                (i == catBalouSelectedIdx ? "> " : "  ") + "[" + i + "] " + choices.get(i));
        }
        tg.putString(start.withRow(start.getRow() + 1 + choices.size()),
            "L/R = browse   Enter = confirm");
    }

    // --- asset helpers (name-based, no GameLogic class references) ---

    private TextImage[] cardSnapImages(List<CardSnap> cards) {
        TextImage[] imgs = new TextImage[cards.size()];
        for (int i = 0; i < cards.size(); i++) imgs[i] = cardSnapImage(cards.get(i));
        return imgs;
    }

    // Build the visual card: weapon cards are selected by range, all others by name.
    // Border color distinguishes BROWN (action) from BLUE (equipment) cards.
    // Suit symbol + value are stamped at the bottom-left corner.
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
            case "Scope":          return Assets.SCOPE;
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
