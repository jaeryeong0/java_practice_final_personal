import com.googlecode.lanterna.TerminalPosition;

public final class UIPositions {
    
    // 인스턴스화 방지
    private UIPositions() {}

    // 1. 내 보드 (화면 좌측)
    public static final class MyBoard {
        public static final TerminalPosition BOARD = new TerminalPosition(0, 0);
        // 배열로 선언하면 for문으로 1~5번 슬롯을 순회하며 그리기 십습니다.
        public static final TerminalPosition[] BULLETS = {
            new TerminalPosition(6,2),
            new TerminalPosition(21,2),
            new TerminalPosition(36,2),
            new TerminalPosition(41,2),
            new TerminalPosition(56,2),
        }; 
        
        // 카드의 역할이 명확하므로 직업, 인물, 무기로 이름을 짓는 것이 좋습니다.
        public static final TerminalPosition ROLE_CARD = new TerminalPosition(3, 8);
        public static final TerminalPosition CHARACTER_CARD = new TerminalPosition(29, 8);
        public static final TerminalPosition WEAPON_CARD = new TerminalPosition(55, 8);
        
        // 깔아둔 패시브 카드와 손패도 배열로 관리
        public static final TerminalPosition PASSIVE_CARDS = new TerminalPosition(1, 27);
        public static final TerminalPosition HAND_CARDS = new TerminalPosition(1, 59);
    }

    // 2. 다른 플레이어들 (화면 우측 상단)
    public static final class PlayerList {
        // 7명의 플레이어 상태 아이콘/닉네임 위치
        public static final TerminalPosition[] ICONS = {
            new TerminalPosition(105, 2),
            new TerminalPosition(127, 2),
            new TerminalPosition(149, 2),
            new TerminalPosition(171, 2),
            new TerminalPosition(193, 2),
            new TerminalPosition(215, 2),
            new TerminalPosition(237, 2)
        };
    }

    // 3. 선택지 및 로그 창 (화면 중앙)
    public static final class LogArea {
        // 설명 창의 기준점 (좌측 상단)
        public static final TerminalPosition TOP_LEFT = new TerminalPosition(97, 17);
        // 텍스트가 시작될 위치
        public static final TerminalPosition TEXT_START = new TerminalPosition(99, 18);
    }

    // 4. 공용 카드 덱 구역 (화면 중앙 우측)
    public static final class TableCenter {
        public static final TerminalPosition MAIN_DECK = new TerminalPosition(206, 18);
        public static final TerminalPosition DISCARD_PILE = new TerminalPosition(238, 18);
    }

    // 5. 상대방 보드 (화면 우측 하단 - 특정 플레이어 클릭/선택 시 표시)
    public static final class TargetBoard {
        public static final TerminalPosition BOARD = new TerminalPosition(99, 44);
        public static final TerminalPosition[] BULLETS = {
            new TerminalPosition(105, 46),
            new TerminalPosition(120, 46),
            new TerminalPosition(135, 46),
            new TerminalPosition(150, 46),
            new TerminalPosition(165, 46)
        };
        public static final TerminalPosition HAND_COUNT = new TerminalPosition(158, 43); // 남은 카드 장수 텍스트 위치
        
        public static final TerminalPosition ROLE_CARD = new TerminalPosition(102, 53);
        public static final TerminalPosition CHARACTER_CARD = new TerminalPosition(128, 53);
        public static final TerminalPosition WEAPON_CARD = new TerminalPosition(154, 53);
        
        public static final TerminalPosition PASSIVE_CARDS = new TerminalPosition(182, 45);
        
        // 페이지네이션 점의 중앙 기준점
        public static final TerminalPosition PAGE_DOTS_CENTER = new TerminalPosition(184, 70); 
    }
}