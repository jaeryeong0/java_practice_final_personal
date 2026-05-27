import com.googlecode.lanterna.graphics.TextImage;

public class Assets {
    private static final String[] BOARD_STRING = {
        " ▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄",
        " █                                                                               █",
        " █      ▄▀▄            ▄▀▄            ▄▀▄            ▄▀▄            ▄▀▄          █",
        " █    ▄▀   ▀▄        ▄▀   ▀▄        ▄▀   ▀▄        ▄▀   ▀▄        ▄▀   ▀▄        █",
        " █     ▀▄    ▀▄       ▀▄    ▀▄       ▀▄    ▀▄       ▀▄    ▀▄       ▀▄    ▀▄      █",
        " █       ▀▄    ▀▄       ▀▄    ▀▄       ▀▄    ▀▄       ▀▄    ▀▄       ▀▄    ▀▄    █",
        " █         ▀▄   █         ▀▄   █         ▀▄   █         ▀▄   █         ▀▄   █    █",
        " █           ▀▀▀            ▀▀▀            ▀▀▀            ▀▀▀            ▀▀▀     █",
        " █ ┌───────────────────────┐ ┌───────────────────────┐ ┌───────────────────────┐ █",
        " █ │                       │ │                       │ │                       │ █",
        " █ │                       │ │                       │ │        COLT.45        │ █",
        " █ │                       │ │                       │ │                       │ █",
        " █ │                       │ │                       │ │                       │ █",
        " █ │                       │ │                       │ │        ██████         │ █",
        " █ │                       │ │                       │ │        █              │ █",
        " █ │                       │ │                       │ │                       │ █",
        " █ │                       │ │                       │ │                       │ █",
        " █ │                       │ │                       │ │                       │ █",
        " █ │                       │ │                       │ │                       │ █",
        " █ │                       │ │                       │ │         ••|••         │ █",
        " █ │                       │ │                       │ │       •       •       │ █",
        " █ │                       │ │                       │ │      ――   1   ――      │ █",
        " █ │                       │ │                       │ │       •       •       │ █",
        " █ │                       │ │                       │ │         ••|••         │ █",
        " █ └───────────────────────┘ └───────────────────────┘ └───────────────────────┘ █",
        " ▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀",

    };

    private static final String[] BULLET_STRING = {
        "  ▄█▄      ",
        "▄█████▄    ",
        " ▀██████▄  ",
        "   ▀██████▄",
        "     ▀█████",
        "       ▀▀▀ "
    };
    // private static final String[] BULLET_STRING = {
    //     " ▄▄▄ ",
    //     "▄███▄",
    //     "█████",
    //     "█████",
    //     "█████",
    //     "█████"
    // };
    //************************************************************
    // GUNS
    //************************************************************
    private static final String[] GUN1_STRING = {

        "┌───────────────────────┐",
        "│                       │",
        "│       VOLCANIC        │",
        "│                       │",
        "│               ▄       │",
        "│       ▄▓▓███████      │",
        "│     ▄██▄▀▀▀▀▀▀▀       │",
        "│     ██▀               │",
        "│                       │",
        "│                       │",
        "│         ••|••         │",
        "│       •       •       │",
        "│      ――   1   ――      │",
        "│       •       •       │",
        "│         ••|••         │",
        "│                       │",
        "└───────────────────────┘",
    };
    private static final String[] GUN2_STRING = {

        "┌───────────────────────┐",
        "│                       │",
        "│       SCHOFIELD       │",
        "│                       │",
        "│                       │",
        "│        ███████        │",
        "│        █  ▓           │",
        "│                       │",
        "│                       │",
        "│                       │",
        "│         ••|••         │",
        "│       •       •       │",
        "│      ――   2   ――      │",
        "│       •       •       │",
        "│         ••|••         │",
        "│                       │",
        "└───────────────────────┘",
    };
    private static final String[] GUN3_STRING = {

        "┌───────────────────────┐",
        "│                       │",
        "│       REMINGTON       │",
        "│                       │",
        "│                       │",
        "│       █████████▀      │",
        "│      ▓▓               │",
        "│                       │",
        "│                       │",
        "│                       │",
        "│         ••|••         │",
        "│       •       •       │",
        "│      ――   3   ――      │",
        "│       •       •       │",
        "│         ••|••         │",
        "│                       │",
        "└───────────────────────┘",
    };
    private static final String[] GUN4_STRING = {

        "┌───────────────────────┐",
        "│                       │",
        "│       CARABINE        │",
        "│                       │",
        "│                       │",
        "│                    ▄  │",
        "│    ▀██▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀ │",
        "│ ▄▄███▀                │",
        "│ ▀▀▀                   │",
        "│                       │",
        "│         ••|••         │",
        "│       •       •       │",
        "│      ――   4   ――      │",
        "│       •       •       │",
        "│         ••|••         │",
        "│                       │",
        "└───────────────────────┘",
    };
    private static final String[] GUN5_STRING = {

        "┌───────────────────────┐",
        "│                       │",
        "│      WINCHESTER       │",
        "│▄▄                     │",
        "│  ▀▀▄▄                 │",
        "│      ▀▀▄▄             │",
        "│          ▀▀▄▄  ▄      │",
        "│              ▓▓▄      │",
        "│                █▄▄    │",
        "│                 ▀██▄  │",
        "│         ••|••     ▀██▄│",
        "│       •       •     ▀ │",
        "│      ――   5   ――      │",
        "│       •       •       │",
        "│         ••|••         │",
        "│                       │",
        "└───────────────────────┘",
    };
    //************************************************************
    // ROLE_CARDS
    //************************************************************
    private static final String[] SHERIFF_STRING = {

        "┌───────────────────────┐",
        "│                       │",
        "│          /‾\\          │",
        "│         /   \\         │",
        "│   _____/     \\_____   │",
        "│   \\               /   │",
        "│    \\   ―――――――   /    │",
        "│     )  SHERIFF  (     │",
        "│    /   ―――――――   \\    │",
        "│   /____       ____\\   │",
        "│        \\     /        │",
        "│         \\   /         │",
        "│          \\_/          │",
        "│                       │",
        "│  Kill all the Outlaws │",
        "│   and the Renegade!   │",
        "└───────────────────────┘",
    };
    private static final String[] VICE_STRING = {

        "┌───────────────────────┐",
        "│                       │",
        "│                       │",
        "│          /‾\\          │",
        "│         /   \\         │",
        "│     ___/     \\___     │",
        "│     \\  DEPUTY   /     │",
        "│      \\ SHERIFF /      │",
        "│      /    _    \\      │",
        "│     /_――‾‾ ‾‾――_\\     │",
        "│                       │",
        "│                       │",
        "│  Protect the Sheriff  │",
        "│  Kill all the Outlaws │",
        "│   and the Renegade!   │",
        "│                       │",
        "└───────────────────────┘",
    };
    //***********************************************
    // Play Cards
    //***********************************************
    private static final String[] FRAME_STRING = {
        "┌─────────────────────────────┐",
        "│                             │",
        "│                             │",
        "│                             │",
        "│                             │",
        "│                             │",
        "│                             │",
        "│                             │",
        "│                             │",
        "│                             │",
        "│                             │",
        "│                             │",
        "│                             │",
        "│                             │",
        "│                             │",
        "│                             │",
        "│                             │",
        "│                             │",
        "│                             │",
        "│                             │",
        "└─────────────────────────────┘"
    };
    //***********************************************
    public static final TextImage BOARD;
    public static final TextImage BULLET;
    
    public static final TextImage GUN1;
    public static final TextImage GUN2;
    public static final TextImage GUN3;
    public static final TextImage GUN4;
    public static final TextImage GUN5;
    
    public static final TextImage SHERIFF;
    public static final TextImage VICE;

    public static final TextImage FRAME;


    static {
        BOARD = Util.createTextImage(BOARD_STRING);
        BULLET = Util.createTextImage(BULLET_STRING);

        GUN1 = Util.createTextImage(GUN1_STRING);
        GUN2 = Util.createTextImage(GUN2_STRING);
        GUN3 = Util.createTextImage(GUN3_STRING);
        GUN4 = Util.createTextImage(GUN4_STRING);
        GUN5 = Util.createTextImage(GUN5_STRING);

        SHERIFF = Util.createTextImage(SHERIFF_STRING);
        VICE = Util.createTextImage(VICE_STRING);

        FRAME = Util.createTextImage(FRAME_STRING);

    }
}
