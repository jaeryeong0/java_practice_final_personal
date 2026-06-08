package tools;

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

    private static final String[] USER_ICON_STRING = {
        "      ▄▄▄▄      ",
        "    █▀    ▀█    ",
        "   █        █   ",
        "   █        █   ",
        "    █▄    ▄█    ",
        "      █  █      ",
        "   ▄▀▀    ▀▀▄   ",
        "  █          █  ",
        "  █          █  ",
        "  █          █  ",
        "  █          █  ",
        "  █▄▄▄▄▄▄▄▄▄▄█  "
    };

    private static final String[] NUMCARDS_DISPLAY_STRING = {
        "█▀▀▀▀█",
        "█ X3 █",
        "█▄▄▄▄█"
    }; // ▄▀█

    
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

        private static final String[] OUTLAW_STRING = {
        "┌───────────────────────┐",
        "│         ▄███          │",
        "│      █████████        │",
        "│        █████▀         │",
        "│        ▄████▄         │",
        "│     ▄██████████▄      │",
        "│     ███OUTLAW███▄     │",
        "│    ▄██████████ ██▄    │",
        "│    ██ ████████  ██    │",
        "│   ██▀█████████▄ ██    │",
        "│   █  ████████████     │",
        "│  ▀█  ██████████▄ ▀▄   │",
        "│      ███████████   ▀▄ │",
        "│                       │",
        "│   Kill the Sheriff    │",
        "│                       │",
        "└───────────────────────┘",
    };

    private static final String[] RENEGADE_STRING = {
        "┌───────────────────────┐",
        "│                       │",  
        "│       RENEGADE        │",  
        "│                       │",  
        "│         ▄███▄         │",
        "│ ▄▄     ███████     ▄▄ │",
        "│ █████▄▄███████▄▄█████ │",
        "│ ▀▀█████████████████▀▀ │",
        "│     █▓▓▓▓▓▓▓▓▓▓▓█     │",  
        "│      █▓▓▓▓▓▓▓▓▓█      │",
        "│      █▓▓▓▓▓▓▓▓▓█      │",  
        "│       ▀▀▓▓▓▓▓▀▀       │",
        "│                       │",  
        "│    Be the last one    │",
        "│        in play        │",
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

    private static final String[] FRAME2_STRING = {
        "┌───────────────────────┐",
        "│                       │",  
        "│                       │",  
        "│                       │",  
        "│                       │",  
        "│                       │",  
        "│                       │",  
        "│                       │",  
        "│                       │",  
        "│                       │",  
        "│                       │",  
        "│                       │",  
        "│                       │",  
        "│                       │",  
        "│                       │",  
        "│                       │",  
        "└───────────────────────┘",
    };
    //***********************************************
    // Brown Cards
    //***********************************************
    private static final String[] BANG_STRING = {
        "┌─────────────────────────────┐",
        "│            Bang!            │",
        "│                             │",
        "│            ▄                │",
        "│    ▄▓▓███████  ======== ●   │",
        "│  ▄██▄▀▀▀▀▀▀▀                │",
        "│  ██▀                        │",
        "│                             │",
        "│   ▓▓▓▓   ▓▓▓  ▓   ▓  ▓▓▓    │",
        "│   ▓   ▓ ▓   ▓ ▓▓  ▓ ▓       │",
        "│   ▓▓▓▓  ▓▓▓▓▓ ▓ ▓ ▓ ▓  ▓▓   │",
        "│   ▓   ▓ ▓   ▓ ▓  ▓▓ ▓   ▓   │",
        "│   ▓▓▓▓  ▓   ▓ ▓   ▓  ▓▓▓    │",
        "│                             │",
        "│     ▄    ▄       ••|••      │",
        "│   ▄██▀ ▄▀      •  ▄▄▄  •    │",
        "│    ▀ ▄▀ ▄     ――――███――――   │",
        "│    ▄▀ ▄███     • ▀▀▀▀▀ •    │",
        "│   ▀    ▀▀▀       ••|••      │",
        "│                             │",
        "└─────────────────────────────┘"
    };
    private static final String[] BEER_STRING = {
        "┌─────────────────────────────┐",
        "│            Beer             │",
        "│                             │",
        "│                             │",
        "│            ▄▓▓▓▓▓▓▓▓▄       │",
        "│       ▄▄▄▄███▓▓▓▓▓▓▓██      │",
        "│      █    █  ▀▀▀▀▓▓  █      │",
        "│      █    █       ▓  █      │",
        "│      █    █          █      │",
        "│      █    █          █      │",
        "│      █    █          █      │",
        "│       ▀▀▀▀█▄▄      ▄▄█      │",
        "│              ▀▀▀▀▀▀         │",
        "│                             │",
        "│            ▄  ▄█▄           │",
        "│          ▄███▄ ▀            │",
        "│           ▀████▄            │",
        "│             ▀████           │",
        "│               ▀▀▀           │",
        "│                             │",
        "└─────────────────────────────┘"
    };
    private static final String[] CAT_BALOU_STRING = {
        "┌─────────────────────────────┐",
        "│          Cat Balou          │",
        "│                             │",
        "│             ▄███            │",
        "│        ▄▄▀▀▀████▀▀▀▄▄       │",
        "│          ▀▀#####█▀▀         │",
        "│           ##   ▄▀           │",
        "│       ▄▄▄## ██▀ ##▄▄▄       │",
        "│      █████████████████      │",
        "│       ▀▓▓▓███▓▓███▓▓▓       │",
        "│        █▓▓▓█▓▓▓████▓▓▓      │",
        "│         █▓▓▓▓▓████  ▓▓▓     │",
        "│         ██▓▓▓█████ ▓▓▓      │",
        "│                             │",
        "│       \\__/                  │",
        "│       |\\/|       ▄▄▄        │",
        "│       |/\\|       ███        │",
        "│       /‾‾\\      ▀▀▀▀▀       │",
        "│                             │",
        "│                             │",
        "└─────────────────────────────┘"
    };
    private static final String[] DUEL_STRING = {
        "┌─────────────────────────────┐",
        "│            Duel             │",
        "│               ▓▓▓▓▓▓▓▓      │",
        "│               ▓▓▓▓▓▓▓       │",
        "│              ▄▄█████▄▄      │",
        "│          ▄███████████████▄  │",
        "│         ███████████████████ │",
        "│        ███  ████████████ ███│",
        "│       ███   ███████████   ██│",
        "│        ███ ▄████████████   █│",
        "│        ██ \\ ▓▓▓▓▓▓▓▓▓▓▓▓    │",
        "│       ▓▓▓  \\████████████    │",
        "│            ████      ████   │",
        "│            ████       ████  │",
        "│           ████        ████  │",
        "│           ████    +    ████ │",
        "│          ████    /█\\   ████│",
        "│          ████     ▓    ████ │",
        "│          ████           ████│",
        "│         █████           ████│",
        "└─────────────────────────────┘"
    };
    private static final String[] GATLING_STRING = {
        "┌─────────────────────────────┐",
        "│           Gatling           │",
        "│                             │",
        "│                             │",
        "│                             │",
        "│                 ▄▄▄▄▄▄      │",
        "│ ████▄▄▄▄▄▄▄█████████████▄█▓ │",
        "│ █▓▓▓▓▓▓▓▓▓▓████████████   ▓ │",
        "│ ████▀▀▀▀▀▀▀█▓▓▓▓▓▓██████▄ ▓ │",
        "│                   █    ▀█▀▀ │",
        "│                  █ █▀▀▀▀▀   │",
        "│                 ▀  █        │",
        "│                    ▀        │",
        "│                             │",
        "│     ▄    ▄                  │",
        "│   ▄██▀ ▄▀          ██       │",
        "│    ▀ ▄▀ ▄      ██ ▀▀▀▀ ██   │",
        "│    ▄▀ ▄███    ▀▀▀▀    ▀▀▀▀  │",
        "│   ▀    ▀▀▀                  │",
        "│                             │",
        "└─────────────────────────────┘"
    };
    private static final String[] GENERAL_STORE_STRING = {
        "┌─────────────────────────────┐",
        "│        General Store        │",
        "│                             │",
        "│     ▓▓▓▓▓                   │",
        "│   ▓▓▓▓▓▓▓▓         ▓▓▓▓▓▓▓  │",
        "│                 ▓▓▓▓▓▓▓▓▓▓▓▓│",
        "│                             │",
        "│          ▄███████▄          │",
        "│     ▄▄███████████████▄▄     │",
        "│      █ @ @| @ @ |@ @ █      │",
        "│      █▄▄▄▄|▄▄▄▄▄|▄▄▄▄█      │",
        "│      █▓▓▓▓|▓▓▓▓▓|▓▓▓▓█      │",
        "│      █▓▓▓▓|▓▓▓▓▓|▓▓▓▓█      │",
        "│      █▓▓▓▓|▓▓▓▓▓|▓▓▓▓█      │",
        "│      █▓▓▓▓|▓▓▓▓▓|▓▓▓▓█      │",
        "│     ███████████████████     │",
        "│ ########################### │",
        "│ ########################### │",
        "│ ########################### │",
        "│ ########################### │",
        "└─────────────────────────────┘"
    };
    private static final String[] INDIANS_STRING = {
        "┌─────────────────────────────┐",
        "│          Indians!           │",
        "│                             │",
        "│     ▄████▄                  │",
        "│     |   ███                 │",
        "│     \\     █                 │",
        "│      ‾/‾‾‾  ▄████▄          │",
        "│      /      |   ███         │",
        "│      |      \\     █         │",
        "│      |       ‾‾▄███████▄    │",
        "│                ▓▓▓▓▓▓▓▓██   │",
        "│                |     █ ███  │",
        "│               /__     ▀████ │",
        "│                __/      ███ │",
        "│      █         \\_______/ ███│",
        "│     ██              \\       │",
        "│     ██             ___\\     │",
        "│     ██            /         │",
        "│     ███          /          │",
        "│    ▓▓▓▓▓▓\\      /           │",
        "└─────────────────────────────┘"
    };
    private static final String[] MISSED_STRING = {
        "┌─────────────────────────────┐",
        "│           Missed!           │",
        "│                             │",
        "│                             │",
        "│                             │",
        "│         ▄▄                  │",
        "│       ▄▀  ▀▀▄ ▄▄▀▀▀▀▀▄      │",
        "│     ▄▀     ▄▀▀        █     │",
        "│  ● =======================  │",
        "│       ▀▄▀  ▄█████▀   ▄▀     │",
        "│      ▄▀   ██████▀  ▄▀       │",
        "│      █     ▀▀▀   ▄▀         │",
        "│      █        ▄▄▀         ▄ │",
        "│       ▀▄▄▄▄▄▀▀    ▄██▄▄▄███ │",
        "│                ▄▄▄▄████████ │",
        "│                 ████████▀   │",
        "│               ▄█████  ▄▄    │",
        "│                ███ ▀ ▀ *  \\ │",
        "│                ████       _\\│",
        "│                 ████     ▄▄▄│",
        "└─────────────────────────────┘"
    };
    private static final String[] PANIC_STRING = {
        "┌─────────────────────────────┐",
        "│           Panic!            │",
        "│                             │",
        "│                             │",
        "│ c    ▄▄▄▄                   │",
        "│  c █▀████▀█                 │",
        "│   █  ▄  ▄  █                │",
        "│▄▄██ ▀    ▀ ██▄▄  ▀▀▀▀▀█████▄│",
        "│   █        █              ▀█│",
        "│    █ ▓▓▓▓ █                 │",
        "│     ▀▄▄▄▄▀                  │",
        "│ ▄▄███    ███▄▄              │",
        "│ ██████   █████              │",
        "│                             │",
        "│   ┌─────┐        ••|••      │",
        "│   │     │      •       •    │",
        "│   │  +  │     ――   1   ――   │",
        "│   │     │      •       •    │",
        "│   └─────┘        ••|••      │",
        "│                             │",
        "└─────────────────────────────┘"
    };
    private static final String[] SALOON_STRING = {
        "┌─────────────────────────────┐",
        "│           Saloon            │",
        "│                             │",
        "│                             │",
        "│     ▄▄▄▄▄▄██████▄▄▄▄▄▄      │",
        "│      █▓▓▓▓▓▓▓▓▓▓▓▓▓▓█       │",
        "│      █▓▓▓▓SALOON▓▓▓▓█       │",
        "│      █▓▓▓▓▓▓▓▓▓▓▓▓▓▓█       │",
        "│      █▓▓    ▓▓▓▓▓▓▓▓█       │",
        "│      █▓▓    ▓▓▓▓▓▓▓▓█       │",
        "│      █▓▓    ▓▓▓▓▓▓▓▓█       │",
        "│                             │",
        "│                             │",
        "│                             │",
        "│    ▄  ▄█▄                   │",
        "│  ▄███▄ ▀          ██        │",
        "│   ▀████▄      ██ ▀▀▀▀ ██    │",
        "│     ▀████    ▀▀▀▀    ▀▀▀▀   │",
        "│       ▀▀▀                   │",
        "│                             │",
        "└─────────────────────────────┘"
    };
    private static final String[] STAGECOACH_STRING = {
        "┌─────────────────────────────┐",
        "│         Stagecoach          │",
        "│                             │",
        "│                             │",
        "│                             │",
        "│                             │",
        "│   ▄████████▄                │",
        "│   ██████████                │",
        "│   ██████████__      ▄▄▄     │",
        "│   ██████████‾‾▓██████ ▀     │",
        "│   ▓▓      ▓▓   /|  |)       │",
        "│                             │",
        "│                             │",
        "│     ┌─────┐     ┌─────┐     │",
        "│     │     │     │     │     │",
        "│     │  +  │     │  +  │     │",
        "│     │     │     │     │     │",
        "│     └─────┘     └─────┘     │",
        "│                             │",
        "│                             │",
        "└─────────────────────────────┘"
    };
    private static final String[] WELLS_FARGO_STRING = {
        "┌─────────────────────────────┐",
        "│         Wells Fargo         │",
        "│                             │",
        "│                             │",
        "│                             │",
        "│              ▄██████████▄   │",
        "│   ▄▄▄      __████████████   │",
        "│   ▀ ██████▓‾‾████████████   │",
        "│   ▄▄(/   |\\__████████████   │",
        "│   ▀ ██████▓‾‾████████████   │",
        "│     )|  |/   ▓▓        ▓▓   │",
        "│                             │",
        "│                             │",
        "│  ┌─────┐  ┌─────┐  ┌─────┐  │",
        "│  │     │  │     │  │     │  │",
        "│  │  +  │  │  +  │  │  +  │  │",
        "│  │     │  │     │  │     │  │",
        "│  └─────┘  └─────┘  └─────┘  │",
        "│                             │",
        "│                             │",
        "└─────────────────────────────┘"
    };

    //***********************************************
    // Blue Equipment Cards
    //***********************************************
    private static final String[] BARREL_STRING = {
        "┌─────────────────────────────┐",
        "│           Barrel            │",
        "│                             │",
        "│         ⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀          │",
        "│     ⣤⣶⣿⣿⡿⠿⠿⢿⣿⣿⣿⣿⣿⣿⣿⣿⣶⣤      │",
        "│     ⠻⢿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠿⠛      │",
        "│     ⢰⣦⣤⣌⣉⣉⡉⠉⠙⠋⠉⢉⣉⣉⣡⣤⣴⡆      │",
        "│     ⢸⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡇      │",
        "│     ⢿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡿      │",
        "│     ⢠⣉⡙⠛⠛⠿⠿⠿⠿⠿⠿⠿⠿⠛⠛⢋⣁⡄      │",
        "│     ⢸⣿⣿⣿⣷⣶⣶⣶⣶⣶⣶⣶⣶⣾⣿⣿⣿⡇      │",
        "│     ⣸⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣇      │",
        "│     ⠻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠟      │",
        "│     ⢰⣤⣈⣉⡙⠛⠛⠛⠛⠛⠛⠛⠛⢉⣉⣁⣤⡆      │",
        "│     ⢸⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡇      │",
        "│     ⣸⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣇      │",
        "│     ⠛⠿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠿⠛      │",
        "│            ⠈⠉⠉⠉⠉⠉⠉⠉⠉⠉⠉      │",
        "│                             │",
        "│                             │",
        "└─────────────────────────────┘"
    };
    private static final String[] MUSTANG_STRING = {
        "┌─────────────────────────────┐",
        "│           Mustang           │",
        "│                             │",
        "│                             │",
        "│                             │",
        "│                             │",
        "│                             │",
        "│          .''                │",
        "│        ._.-.___.' (`\\       │",
        "│       //(        ( `'       │",
        "│      '/ )\\ ).__. )          │",
        "│      ' <' `\\ ._/'\\          │",
        "│         `   \\     \\         │",
        "│                             │",
        "│                             │",
        "│                             │",
        "│                             │",
        "│                             │",
        "│                             │",
        "│                             │",
        "└─────────────────────────────┘"
    };
    private static final String[] SCOPE_STRING = {
        "┌─────────────────────────────┐",
        "│            Scope            │",
        "│                             │",
        "│                             │",
        "│        ___                  │",
        "│          .:---:.         _  │",
        "│         // #   \\\\_...--'' \\ │",
        "│        || #     |_         |│",
        "│         \\\\     // ```--.._/ │",
        "│          ':===:'            │",
        "│            ```              │",
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
    private static final String[] JAIL_STRING = {
        "┌─────────────────────────────┐",
        "│            Jail             │",
        "│                             │",
        "│                             │",
        "│                             │",
        "│     ║  ║  ║  ║  ║  ║  ║     │",
        "│     ║  ║  ║  ║  ║  ║  ║     │",
        "│     ║  ║  ║  ║  ║  ║  ║     │",
        "│     ║  ║  ║  ║  ║  ║  ║     │",
        "│     ║  ║  ║  ║  ║  ║  ║     │",
        "│     ║  ║  ║  ║  ║  ║  ║     │",
        "│     ║  ║  ║  ║  ║  ║  ║     │",
        "│     ║  ║  ║  ║  ║  ║  ║     │",
        "│     ║  ║  ║  ║  ║  ║  ║     │",
        "│     ║  ║  ║  ║  ║  ║  ║     │",
        "│                             │",
        "│                             │",
        "│                             │",
        "│                             │",
        "│                             │",
        "└─────────────────────────────┘"
    };
    private static final String[] DYNAMITE_STRING = {
        "┌─────────────────────────────┐",
        "│          Dynamite           │",
        "│                             │",
        "│                             │",
        "│             /               │",
        "│        ____/                │",
        "│       |    |                │",
        "│       | TNT|       *        │",
        "│       |    |      /         │",
        "│       |____|     /          │",
        "│                 /           │",
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
    // Title Screen
    //***********************************************
    private static final String[] TITLE_STRING = {
        " ██████╗  █████╗ ███╗   ██╗  ██████╗  ██╗",
        " ██╔══██╗██╔══██╗████╗  ██║ ██╔════╝  ██║",
        " ██████╔╝███████║██╔██╗ ██║ ██║  ███╗ ██║",
        " ██╔══██╗██╔══██║██║╚██╗██║ ██║   ██║ ╚═╝",
        " ██████╔╝██║  ██║██║ ╚████║ ╚██████╔╝ ██╗",
        " ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═══╝  ╚═════╝  ╚═╝",
    };
    //***********************************************
    // Character Cards
    //***********************************************

    private static final String[] WILLY_THE_KID_STRING = {
        "┌───────────────────────┐",
        "│     Willy The Kid     │",
        "│                       │",  
        "│    ▄▀▄  ▄▀▀▀▄  ▄▀▄    │",
        "│   █   ▀▀█▄▄▄▄▀▀   █   │",
        "│  █    ▄▄██▀███     █  │",
        "│  █   ▄█▀▀   ▀▀▀█   █  │",
        "│   █  █         █  █   │",
        "│    ▀▄▄█       █▄▄▀    │",
        "│        ▀▄   ▄▀        │",
        "│     ▄▄▀█     █▀▄▄     │",
        "│   █▀▄▀  █   █  ▀▄▀█   │",
        "│  █  ▄▀▄ █   █   ▄▀ █  │",
        "│  █ █   █ █ █  ▄▀ █ █  │",
        "│ █  █    ▀█  ██   ▀▄▄█ │",
        "│ █▄▄▄▄▄▄▄▄█▄▄█▄▄▄▄▄▄▄█ │",
        "└───────────────────────┘",
    };
    
    private static final String[] CALAMITY_JANET_STRING = {
        "┌───────────────────────┐",
        "│    Calamity Jarnet    │",
        "│                       │",
        "│     ▄▄▄ ▄███ ▄▄▄      │",
        "│     ▀▄ ▀████▀ ▄▀      │",
        "│       ▀█▀  ▀█▀        │",
        "│       #▀▄  ▄▀#        │",
        "│▄     ### ▀▀ ###       │",
        "│▀█▄   /‾‾‾    ‾‾‾\\     │",
        "│▄█▀  │  ┌-_/_--┐  │    │",
        "│▀ \\  │ (        )  │   │",
        "│ \\ \\/  \\__   __/ │ │   │",
        "│  \\  /  \\ ‾‾‾ /  │ │   │",
        "│    ‾   │     │   ││   │",
        "│        /     \\   /│   │",
        "│      ▄█████████▄│ │   │",
        "└───────────────────────┘",
    };
    
    private static final String[] KIT_CARLSON_STRING = {
        "┌───────────────────────┐", //█▄▀▓
        "│      Kit Carlson      │",
        "│                       │",
        "│        ▄▄▄ ▄▄▄        │",
        "│    ▄▄▄ █  ▀▀ █  ▄▄▄▄  │",
        "│ ▄████████▄▄▄▄█▀▀    █ │",
        "│ ▀▀▀▀▀▀███▀▀▀▀▀▀█▀▀▀▀  │",
        "│      █ █       █      │",
        "│     ▄█▀█▄     ▄▀      │",
        "│       █▀ ▀▓▓▓▓ ▀      │",
        "│  ▄▄█████ \\   / ████▄▄ │",
        "│ ██▓██▓██▄ \\_/  ██▓██▓█│",
        "│▄███████▓█     ██▓█████│",
        "│█▓██▓█████     █████▓██│",
        "│███████▓██     ███▓████│",
        "│                       │",
        "└───────────────────────┘",
    };
    
    private static final String[] BART_CASSIDY_STRING = {
        "┌───────────────────────┐",
        "│     Bart Cassidy      │",//█▄▀▓
        "│       ▄██▀ ▄▄▄▄▄▄▄▄▄▄█│",
        "│  ▄▄█████████▀▀▀▀▀▀▀███│",
        "│███▀▀▀▀▓▓▓▓▄▄▄▄▄▄▄  ▄▄▄│",
        "│  ▓▓▓▓▓▓▓▓████▀▀█████▀▀│",
        "│ ▓▓▓▓▓▓▓   __▄▄▀▀▀  ▀▄ │",
        "│▓   ▓              ▄▀  │",
        "│▓   ▓              ▀▄  │",
        "│▓▓▓▓▓           ▄█▀▀   │",
        "│▓▓▓▓▓▓▓▓         ▀▄▄▄  │",
        "│▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓     ▀▀│",
        "│▓▓▓▓    ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓│",
        "│▓▓▓▓▀▄ ▓▓▓▓  ▄▄▄▄█▀▀▀▀▀│",
        "│▓▓▓▓▓▓▓▓▓▄       ▓▓▓▓▓▓│",
        "│▓▓▓▓      ▀▀▀▀▀▄▄▄▓▓▓▓▓│",
        "└───────────────────────┘",
    };

    private static final String[] SID_KETCHUM_STRING = {
        "┌───────────────────────┐",
        "│      Sid Ketchum      │",//█▄▓
        "│                       │",
        "│         ▄▀▀▀▀▄▄       │",
        "│ ▀████████▄▄▄▄▄█       │",
        "│    ▀▀▀▀█▀▀███████▄▄   │",
        "│       █    ▄▀ █▀▀▀▀▀▀ │",
        "│       ▓▓▓▓▄█▄██       │",
        "│        ▀▄▄▄▀▀ ▀▄      │",
        "│        ▄█  █▄         │",
        "│ ▓▓▓████▀\\__/█▓██▓▓▓▓  │",
        "│▓▓▓▓█▓██▄   ▄████▓▓▓▓▓ │",
        "│▓▓▓▓█████   ▓██▓█▓▓▓▓▓▓│",
        "│▓▓▓███▓███▄███████ ▓▓▓▓│",
        "│▓▓▓█▓█████@██▓████ ▓▓▓▓│",
        "│▓▓ █████▓██████▓██ ▓▓▓▓│",
        "└───────────────────────┘",
    };

    private static final String[] LUCKY_DUKE_STRING = {
        "┌───────────────────────┐",
        "│      Lucky Duke       │",//█▄▀▓
        "│                       │",
        "│       ▄█████▄         │",
        "│   ▄▄▄▄███████▄▄▀▀▀▄   │",
        "│   ▀▀████▄▄▄▄▄▄▄▄█▀    │",
        "│       █    \\ |        │",
        "│      (    ▀▀ |        │",
        "│      █\\__▓▓▓/         │",
        "│        |_ ‾‾_|        │",
        "│      ▄▄|_/‾\\_|▄▄      │",
        "│  ▄▄█▀ ▀▄      ████▄▄  │",
        "│▀█ ▀▄▄▄▄▄█\\_ _/▄▀▀█████│",
        "│  █ ▓   █ █ ˚  █  ██ ▀▓│",
        "│   ▓    █ █    █   ▓ ▓ │",
        "│   ▓   █  █     █   ▓  │",
        "└───────────────────────┘",
    };

    private static final String[] JOURDONNAIS_STRING = {
        "┌───────────────────────┐",
        "│      Jourdonnais      │",//█▄▀▓
        "│                       │",
        "│          ▄▄▄          │",
        "│    ▄█\\_▄▀   ▀▄_/█▄    │",
        "│   ███████▄▄▄███████   │",
        "│   ▀█████▀▀▀▀▀█████▀   │",
        "│ \\‾‾‾‾‾█--‾‾‾--█‾‾‾‾‾/ │",
        "│  \\    █\\▓▓▓▓▓/█    /  │",
        "│▄██\\__████▓▓▓████__/██▄│",
        "│███████████@███████████│",
        "│████████   ║   ████████│",
        "│█████      ║      █████│",
        "│███       /|        ███│",
        "│█  █               █  █│",
        "│  |▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓|  │",
        "└───────────────────────┘",
    };

    private static final String[] BLACK_JACK_STRING = {
        "┌───────────────────────┐", //█▄▀▓
        "│      Black Jack       │",
        "│                       │",
        "│        ▄▄▄ ▄▄▄        │",
        "│        █  ▀▀ █        │",
        "│        █     █        │",
        "│ ▀▀▀██▀▀▄▄   ▄▄▀▀██▀▀▀ │",
        "│      ██▓▓▄▄▄▓▓██      │",
        "│      ██   /   ██      │",
        "│     ██         ██     │",
        "│     █▀▀▄▓▓▓▓▓▄▀▀█     │",
        "│       ▄█ ▓▓▓ █▄       │",
        "│▄▀▀▀▀█▀▀█ /█\\ █▀▀█▀▀▀▀▄│",
        "│█   ▀▄  █/\\█/\\█  ▄▀   █│",
        "│█  ▄▀ \\  █ █ █  / ▀▄  █│",
        "│█   █  \\  ▀▀▀  /  █   █│",
        "└───────────────────────┘",
    };

    //***********************************************
    public static final TextImage BOARD;
    public static final TextImage BULLET;
    public static final TextImage USER_ICON;
    public static final TextImage NUMCARDS_DISPLAY;
    public static final TextImage GUN1;
    public static final TextImage GUN2;
    public static final TextImage GUN3;
    public static final TextImage GUN4;
    public static final TextImage GUN5;

    public static final TextImage SHERIFF;
    public static final TextImage VICE;

    public static final TextImage FRAME;
    public static final TextImage FRAME2;

    public static final TextImage TITLE;
    public static final TextImage SUBTITLE;
    public static final TextImage HOST_BTN;
    public static final TextImage JOIN_BTN;
    public static final TextImage ARROW_L;
    public static final TextImage ARROW_R;
    public static final TextImage INSTRUCTIONS;
    public static final TextImage POPUP_TITLE;
    public static final TextImage POPUP_PROMPT;
    public static final TextImage POPUP_HINT;
    public static final TextImage NICKNAME_TITLE;
    public static final TextImage NICKNAME_PROMPT;
    public static final TextImage NICKNAME_HINT;

    public static final TextImage HAND_GUN1;
    public static final TextImage HAND_GUN2;
    public static final TextImage HAND_GUN3;
    public static final TextImage HAND_GUN4;
    public static final TextImage HAND_GUN5;

    // Brown Cards
    public static final TextImage BANG;
    public static final TextImage MISSED;
    public static final TextImage BEER;
    public static final TextImage SALOON;
    public static final TextImage STAGECOACH;
    public static final TextImage WELLS_FARGO;
    public static final TextImage GENERAL_STORE;
    public static final TextImage DUEL;
    public static final TextImage INDIANS;
    public static final TextImage GATLING;
    public static final TextImage PANIC;
    public static final TextImage CAT_BALOU;

    // Blue Equipment Cards
    public static final TextImage BARREL;
    public static final TextImage MUSTANG;
    public static final TextImage SCOPE;
    public static final TextImage JAIL;
    public static final TextImage DYNAMITE;

    // Characters
    public static final TextImage WILLY_THE_KID;
    public static final TextImage CALAMITY_JANET;
    public static final TextImage KIT_CARLSON;
    public static final TextImage BART_CASSIDY;
    public static final TextImage SID_KETCHUM;
    public static final TextImage LUCKY_DUKE;
    public static final TextImage JOURDONNAIS;
    public static final TextImage BLACK_JACK;

    // Roles
    public static final TextImage OUTLAW;
    public static final TextImage RENEGADE;

    static {
        BOARD = Util.createTextImage(BOARD_STRING);
        BULLET = Util.createTextImage(BULLET_STRING);
        USER_ICON = Util.createTextImage(USER_ICON_STRING);
        NUMCARDS_DISPLAY = Util.createTextImage(NUMCARDS_DISPLAY_STRING);

        GUN1 = Util.createTextImage(GUN1_STRING);
        GUN2 = Util.createTextImage(GUN2_STRING);
        GUN3 = Util.createTextImage(GUN3_STRING);
        GUN4 = Util.createTextImage(GUN4_STRING);
        GUN5 = Util.createTextImage(GUN5_STRING);

        SHERIFF = Util.createTextImage(SHERIFF_STRING);
        VICE = Util.createTextImage(VICE_STRING);

        FRAME = Util.createTextImage(FRAME_STRING);
        FRAME2 = Util.createTextImage(FRAME2_STRING);

        BANG          = Util.createTextImage(BANG_STRING);
        MISSED        = Util.createTextImage(MISSED_STRING);
        BEER          = Util.createTextImage(BEER_STRING);
        SALOON        = Util.createTextImage(SALOON_STRING);
        STAGECOACH    = Util.createTextImage(STAGECOACH_STRING);
        WELLS_FARGO   = Util.createTextImage(WELLS_FARGO_STRING);
        GENERAL_STORE = Util.createTextImage(GENERAL_STORE_STRING);
        DUEL          = Util.createTextImage(DUEL_STRING);
        INDIANS       = Util.createTextImage(INDIANS_STRING);
        GATLING       = Util.createTextImage(GATLING_STRING);
        PANIC         = Util.createTextImage(PANIC_STRING);
        CAT_BALOU     = Util.createTextImage(CAT_BALOU_STRING);

        BARREL    = Util.createTextImage(BARREL_STRING);
        MUSTANG   = Util.createTextImage(MUSTANG_STRING);
        SCOPE = Util.createTextImage(SCOPE_STRING);
        JAIL      = Util.createTextImage(JAIL_STRING);
        DYNAMITE  = Util.createTextImage(DYNAMITE_STRING);

        HAND_GUN1 = Util.overlayCenter(FRAME, GUN1);
        HAND_GUN2 = Util.overlayCenter(FRAME, GUN2);
        HAND_GUN3 = Util.overlayCenter(FRAME, GUN3);
        HAND_GUN4 = Util.overlayCenter(FRAME, GUN4);
        HAND_GUN5 = Util.overlayCenter(FRAME, GUN5);

        TITLE        = Util.colorizeTextImage(Util.createTextImage(TITLE_STRING), "YELLOW_BRIGHT");
        SUBTITLE     = Util.createTextImage(new String[]{ "~ The Wild West Card Game ~" });
        HOST_BTN     = Util.createTextImage(new String[]{ "[ HOST SERVER ]" });
        JOIN_BTN     = Util.createTextImage(new String[]{ "[ JOIN SERVER ]" });
        ARROW_L      = Util.createTextImage(new String[]{ ">" });
        ARROW_R      = Util.createTextImage(new String[]{ "<" });
        INSTRUCTIONS = Util.createTextImage(new String[]{ "Use Arrow Keys to navigate, Enter to select" });
        POPUP_TITLE  = Util.createTextImage(new String[]{ "Join Server" });
        POPUP_PROMPT = Util.createTextImage(new String[]{ "Enter server IP address:" });
        POPUP_HINT   = Util.createTextImage(new String[]{ "Enter to connect  |  Esc to cancel" });

        NICKNAME_TITLE  = Util.createTextImage(new String[]{ "Enter Nickname" });
        NICKNAME_PROMPT = Util.createTextImage(new String[]{ "Your name:" });
        NICKNAME_HINT   = Util.createTextImage(new String[]{ "Enter to confirm  (max 20 chars)" });

        WILLY_THE_KID  = Util.createTextImage(WILLY_THE_KID_STRING);
        CALAMITY_JANET = Util.createTextImage(CALAMITY_JANET_STRING);
        KIT_CARLSON    = Util.createTextImage(KIT_CARLSON_STRING);
        BART_CASSIDY   = Util.createTextImage(BART_CASSIDY_STRING);
        SID_KETCHUM    = Util.createTextImage(SID_KETCHUM_STRING);
        LUCKY_DUKE     = Util.createTextImage(LUCKY_DUKE_STRING);
        JOURDONNAIS    = Util.createTextImage(JOURDONNAIS_STRING);
        BLACK_JACK     = Util.createTextImage(BLACK_JACK_STRING);

        OUTLAW   = Util.createTextImage(OUTLAW_STRING);
        RENEGADE = Util.createTextImage(RENEGADE_STRING);
    }
}
