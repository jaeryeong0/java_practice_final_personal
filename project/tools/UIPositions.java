package tools;
import com.googlecode.lanterna.TerminalPosition;

public final class UIPositions {
    
    // 인스턴스화 방지
    private UIPositions() {}

    // 1. 내 보드 (화면 좌측)
    public static final class MyBoard {
        public static final TerminalPosition BOARD = new TerminalPosition(0, 0);
        public static final TerminalPosition NICKNAME = new TerminalPosition(3, 1);
        // 배열로 선언하면 for문으로 1~5번 슬롯을 순회하며 그리기 십습니다.
        static int pos1 = 6;
        static int d = 15;
        public static final TerminalPosition[] BULLETS = {
            new TerminalPosition(pos1,2),
            new TerminalPosition(pos1 + d,2),
            new TerminalPosition(pos1 + 2*d,2),
            new TerminalPosition(pos1 + 3*d,2),
            new TerminalPosition(pos1 + 4*d,2),
        }; 
        
        // 카드의 역할이 명확하므로 직업, 인물, 무기로 이름을 짓는 것이 좋습니다.
        public static final TerminalPosition ROLE_CARD = new TerminalPosition(3, 8);
        public static final TerminalPosition CHARACTER_CARD = new TerminalPosition(29, 8);
        public static final TerminalPosition WEAPON_CARD = new TerminalPosition(55, 8);
        
        // 깔아둔 패시브 카드와 손패도 배열로 관리
        public static final TerminalPosition PASSIVE_CARDS = new TerminalPosition(1, 27);
        public static final TerminalPosition HAND_CARDS = new TerminalPosition(1, 49);
    }

    // 2. 다른 플레이어들 (화면 우측 상단)
    public static final class PlayerList {
        // 7명의 플레이어 상태 아이콘/닉네임 위치
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

    // 3. 선택지 및 로그 창 (화면 중앙)
    public static final class TextArea {
        // 텍스트가 시작될 위치
        public static final TerminalPosition TEXT_START = new TerminalPosition(96, 18);
        public static final TerminalPosition LOG = new TerminalPosition(96, 27);
        public static final TerminalPosition CARD_INFO = new TerminalPosition(96, 19);
        
    }

    // 4. 공용 카드 덱 구역 (화면 중앙 우측)
    public static final class TableCenter {
        public static final TerminalPosition MAIN_DECK = new TerminalPosition(206, 17);
        public static final TerminalPosition DISCARD_PILE = new TerminalPosition(238, 17);
    }

    // 5. 상대방 보드 (화면 우측 하단 - 특정 플레이어 클릭/선택 시 표시)
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
        public static final TerminalPosition HAND_COUNT = new TerminalPosition(158, 39); // 남은 카드 장수 표시 위치
        
        public static final TerminalPosition ROLE_CARD = new TerminalPosition(100, 49);
        public static final TerminalPosition CHARACTER_CARD = new TerminalPosition(126, 49);
        public static final TerminalPosition WEAPON_CARD = new TerminalPosition(152, 49);
        
        public static final TerminalPosition PASSIVE_CARDS = new TerminalPosition(180, 45);
        
        // 페이지네이션 점의 중앙 기준점
        public static final TerminalPosition PAGE_DOTS_CENTER = new TerminalPosition(182, 68); 
    }
}