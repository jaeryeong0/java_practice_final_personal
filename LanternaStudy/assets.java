import com.googlecode.lanterna.graphics.TextImage;

public class assets {
    private static final String[] BACKGROUND_STRING = {
        "",
        " ********************************************************************************",
        " *         .-.           .-.           .-.           .-.           .-.          *",
        " *        /   \\         /   \\         /   \\         /   \\         /   \\         *",
        " *        │   │         │   │         │   │         │   │         │   │         *",
        " *        │   │         │   │         │   │         │   │         │   │         *",
        " *        └───┘         └───┘         └───┘         └───┘         └───┘         *",
        " *                                                                              *",
        " *                                                                              *",
        " *  ┌──────────────────────┐ ┌──────────────────────┐ ┌──────────────────────┐  *",
        " *  │                      │ │                      │ │                      │  *",
        " *  │                      │ │                      │ │                      │  *",
        " *  │                      │ │                      │ │                      │  *",
        " *  │                      │ │                      │ │                      │  *",
        " *  │                      │ │                      │ │                      │  *",
        " *  │                      │ │                      │ │                      │  *",
        " *  │                      │ │                      │ │                      │  *",
        " *  │                      │ │                      │ │                      │  *",
        " *  │                      │ │                      │ │                      │  *",
        " *  │                      │ │                      │ │                      │  *",
        " *  │                      │ │                      │ │                      │  *",
        " *  │                      │ │                      │ │                      │  *",
        " *  │                      │ │                      │ │                      │  *",
        " *  │                      │ │                      │ │                      │  *",
        " *  └──────────────────────┘ └──────────────────────┘ └──────────────────────┘  *",
        " *                                                                              *",
        " ********************************************************************************",

    };

    private static final String[] BULLET_STRING = {
        " .-. ",
        "/███\\",
        "│███│",
        "│███│",
        "└───┘"
    };

    public static final TextImage BACKGROUND;
    public static final TextImage BULLET;

    static {
        BACKGROUND = Util.createTextImage(BACKGROUND_STRING);
        BULLET = Util.createTextImage(BULLET_STRING);
    }
}
