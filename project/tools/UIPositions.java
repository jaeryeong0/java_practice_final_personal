package tools;
import com.googlecode.lanterna.TerminalPosition;

public final class UIPositions {
    
    // Prevent instantiation
    private UIPositions() {}

    // 1. My board (left side of screen)
    public static final class MyBoard {
        public static final TerminalPosition BOARD = new TerminalPosition(0, 0);
        public static final TerminalPosition NICKNAME = new TerminalPosition(3, 1);
        // Declared as an array so slots 1-5 can be iterated and drawn with a for loop.
        static int pos1 = 6;
        static int d = 15;
        public static final TerminalPosition[] BULLETS = {
            new TerminalPosition(pos1,2),
            new TerminalPosition(pos1 + d,2),
            new TerminalPosition(pos1 + 2*d,2),
            new TerminalPosition(pos1 + 3*d,2),
            new TerminalPosition(pos1 + 4*d,2),
        }; 
        
        // Named by card role since their purpose is clear: job, character, weapon.
        public static final TerminalPosition ROLE_CARD = new TerminalPosition(3, 8);
        public static final TerminalPosition CHARACTER_CARD = new TerminalPosition(29, 8);
        public static final TerminalPosition WEAPON_CARD = new TerminalPosition(55, 8);
        
        // Passive cards in play and hand cards are also managed as arrays
        public static final TerminalPosition PASSIVE_CARDS = new TerminalPosition(1, 27);
        public static final TerminalPosition HAND_CARDS = new TerminalPosition(1, 49);
    }

    // 2. Other players (top-right of screen)
    public static final class PlayerList {
        // Status icon/nickname positions for up to 7 players
        static int pos1 = 108;
        static int d = 22;
        public static final TerminalPosition[] ICONS = {
            new TerminalPosition(pos1, 1),
            new TerminalPosition(pos1 + 1*d, 1),
            new TerminalPosition(pos1 + 2*d, 1),
            new TerminalPosition(pos1 + 3*d, 1),
            new TerminalPosition(pos1 + 4*d, 1),
            new TerminalPosition(pos1 + 5*d, 1),
            new TerminalPosition(pos1 + 6*d, 1)
        };
        public static final TerminalPosition[] NICKNAMES = {
            new TerminalPosition(pos1 + 8, 14),
            new TerminalPosition(pos1 + 8 + 1*d, 14),
            new TerminalPosition(pos1 + 8 + 2*d, 14),
            new TerminalPosition(pos1 + 8 + 3*d, 14),
            new TerminalPosition(pos1 + 8 + 4*d, 14),
            new TerminalPosition(pos1 + 8 + 5*d, 14),
            new TerminalPosition(pos1 + 8 + 6*d, 14)
        };
    }

    // 3. Options and log panel (center of screen)
    public static final class TextArea {
        // Starting position for text
        public static final TerminalPosition TEXT_START = new TerminalPosition(96, 18);
        public static final TerminalPosition LOG = new TerminalPosition(96, 27);
        public static final TerminalPosition CARD_INFO = new TerminalPosition(96, 19);
        
    }

    // 4. Shared card deck area (center-right of screen)
    public static final class TableCenter {
        public static final TerminalPosition MAIN_DECK = new TerminalPosition(206, 17);
        public static final TerminalPosition DISCARD_PILE = new TerminalPosition(238, 17);
    }

    // 5. Opponent board (bottom-right of screen — shown when a player is selected)
    public static final class TargetBoard {
        public static final TerminalPosition BOARD = new TerminalPosition(97, 41);
        public static final TerminalPosition NICKNAME = new TerminalPosition(98, 39);
        static int pos1 = 103;
        static int d = 15;
        public static final TerminalPosition[] BULLETS = {
            new TerminalPosition(pos1,43),
            new TerminalPosition(pos1 + d,43),
            new TerminalPosition(pos1 + 2*d,43),
            new TerminalPosition(pos1 + 3*d,43),
            new TerminalPosition(pos1 + 4*d,43),
        }; 
        public static final TerminalPosition HAND_COUNT = new TerminalPosition(158, 39); // Position for displaying remaining card count
        
        public static final TerminalPosition ROLE_CARD = new TerminalPosition(100, 49);
        public static final TerminalPosition CHARACTER_CARD = new TerminalPosition(126, 49);
        public static final TerminalPosition WEAPON_CARD = new TerminalPosition(152, 49);
        
        public static final TerminalPosition PASSIVE_CARDS = new TerminalPosition(180, 45);
    }
}