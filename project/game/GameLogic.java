package game;

import java.util.*;

public class GameLogic {

    public enum Role { SHERIFF, DEPUTY, OUTLAW, RENEGADE }
    public enum Suit { SPADE, HEART, DIAMOND, CLUB }
    public enum CardType { BLUE, BROWN }
    public enum GameState { INIT, PLAY, SELECT_TARGET, GAME_OVER }

    // ==========================================
    // [2] Card system
    // ==========================================
    public static abstract class Card {
        public String name; public Suit suit; public int value; public CardType type;
        public Card(String name, Suit suit, int value, CardType type) {
            this.name = name; this.suit = suit; this.value = value; this.type = type;
        }
        public abstract void execute(Player user, Player target, Game game);
        public String getName() { return name; }
        public Suit getSuit() { return suit; }
        public int getValue() { return value; }
        public CardType getCardType() { return type; }
    }

    public static class BangCard extends Card {
        public BangCard() { super("Bang!", Suit.SPADE, 1, CardType.BROWN); }
        @Override public void execute(Player user, Player target, Game game) {
            if (target == null) return;
            user.hasPlayedBang = true;
            game.addLog("[!] " + user.name + " -> " + target.name + " 뱅!");
            if (!target.respondToBang(game)) game.loseHP(target, user, 1);
        }
    }

    public static class MissedCard extends Card {
        public MissedCard() { super("Missed!", Suit.CLUB, 1, CardType.BROWN); }
        @Override public void execute(Player user, Player target, Game game) {}
    }

    public static class DuelCard extends Card {
        public DuelCard() { super("Duel", Suit.CLUB, 7, CardType.BROWN); }
        @Override public void execute(Player user, Player target, Game game) {
            game.addLog("[!] " + user.name + " vs " + target.name + " 결투!");
            Player attacker = user, defender = target;
            while (true) {
                if (!defender.discardSpecificCard(BangCard.class, game)) {
                    game.loseHP(defender, attacker, 1); break;
                }
                Player tmp = attacker; attacker = defender; defender = tmp;
            }
        }
    }

    public static class IndiansCard extends Card {
        public IndiansCard() { super("Indians", Suit.DIAMOND, 1, CardType.BROWN); }
        @Override public void execute(Player user, Player target, Game game) {
            game.addLog("[!] 인디언 습격!");
            for (Player p : game.players)
                if (p != user && p.hp > 0 && !p.discardSpecificCard(BangCard.class, game))
                    game.loseHP(p, user, 1);
        }
    }

    public static class GatlingCard extends Card {
        public GatlingCard() { super("Gatling", Suit.HEART, 10, CardType.BROWN); }
        @Override public void execute(Player user, Player target, Game game) {
            game.addLog("[!] 개틀링!");
            for (Player p : game.players)
                if (p != user && p.hp > 0 && !p.respondToBang(game))
                    game.loseHP(p, user, 1);
        }
    }

    public static class BeerCard extends Card {
        public BeerCard() { super("Beer", Suit.HEART, 1, CardType.BROWN); }
        @Override public void execute(Player user, Player target, Game game) { game.getHP(user, 1); }
    }

    public static class WellsFargoCard extends Card {
        public WellsFargoCard() { super("Wells Fargo", Suit.HEART, 3, CardType.BROWN); }
        @Override public void execute(Player user, Player target, Game game) {
            for (int i = 0; i < 3; i++) user.drawCard(game.deck.popCard(), game);
        }
    }

    public static class PanicCard extends Card {
        public PanicCard() { super("Panic!", Suit.HEART, 8, CardType.BROWN); }
        @Override public void execute(Player user, Player target, Game game) {
            if (target == null || target.hand.isEmpty()) return;
            Card stolen = target.hand.remove(0);
            user.hand.add(stolen);
            game.addLog(user.name + "가 " + target.name + "의 [" + stolen.name + "] 강탈!");
        }
    }

    public static class CatBalouCard extends Card {
        public CatBalouCard() { super("Cat Balou", Suit.DIAMOND, 9, CardType.BROWN); }
        @Override public void execute(Player user, Player target, Game game) {
            if (target == null || target.hand.isEmpty()) return;
            game.deck.discard(target.hand.remove(0));
            game.addLog(target.name + "의 카드 한 장 버림.");
        }
    }

    public static class WeaponCard extends Card {
        public int range;
        public WeaponCard(String name, int range) {
            super(name, Suit.DIAMOND, 1, CardType.BLUE); this.range = range;
        }
        @Override public void execute(Player user, Player target, Game game) {
            if (user.weapon != null) game.deck.discard(user.weapon);
            user.weapon = this;
            game.addLog(user.name + " [" + name + "] 장착!");
        }
    }

    public static class BarrelCard extends Card {
        public BarrelCard() { super("Barrel", Suit.SPADE, 12, CardType.BLUE); }
        @Override public void execute(Player user, Player target, Game game) {
            user.field.add(this);
            game.addLog(user.name + " [술통] 장착!");
        }
    }

    public static class JailCard extends Card {
        public JailCard() { super("Jail", Suit.SPADE, 4, CardType.BLUE); }
        @Override public void execute(Player user, Player target, Game game) {
            if (target == null) return;
            if (target.role == Role.SHERIFF) { game.addLog("보안관은 감옥 불가."); user.hand.add(this); return; }
            target.field.add(this);
            game.addLog(target.name + " 감옥에 갇힘!");
        }
    }

    public static class DynamiteCard extends Card {
        public DynamiteCard() { super("Dynamite", Suit.HEART, 2, CardType.BLUE); }
        @Override public void execute(Player user, Player target, Game game) {
            user.field.add(this);
            game.addLog(user.name + " [다이너마이트] 설치!");
        }
    }

    // ==========================================
    // [3] Character abilities
    // (Named CharDef to avoid conflict with java.lang.Character)
    // ==========================================
    public static abstract class CharDef {
        public String name; public int hp;
        public CharDef(String name, int hp) { this.name = name; this.hp = hp; }
        public void onPhase1(Player self, Game game) {
            self.drawCard(game.deck.popCard(), game);
            self.drawCard(game.deck.popCard(), game);
        }
        public void onDamaged(Player self, Player attacker, Game game, int amount) {}
        public int getDistanceMod() { return 0; }
        public int getRangeMod() { return 0; }
        public boolean canInfiniteBang() { return false; }
        public String getName() { return name; }
    }

    public static class BartCassidy extends CharDef {
        public BartCassidy() { super("Bart Cassidy", 4); }
        @Override public void onDamaged(Player self, Player attacker, Game game, int amount) {
            for (int i = 0; i < amount; i++) self.drawCard(game.deck.popCard(), game);
        }
    }

    public static class WillyTheKid extends CharDef {
        public WillyTheKid() { super("Willy the Kid", 4); }
        @Override public boolean canInfiniteBang() { return true; }
    }

    public static class CalamityJanet extends CharDef {
        public CalamityJanet() { super("Calamity Janet", 4); }
        @Override public boolean canInfiniteBang() { return true; }
    }

    // ==========================================
    // [4] Player
    // ==========================================
    public static class Player {
        public String name; public int hp, maxHp;
        public Role role; public CharDef character;
        public List<Card> hand = new ArrayList<>();
        public List<Card> field = new ArrayList<>();
        public WeaponCard weapon = null;
        public boolean hasMustang = false, hasAppaloosa = false, hasPlayedBang = false, skipTurn = false;

        public Player(String name) { this.name = name; }
        public String getName() { return name; }
        public int getHp() { return hp; }
        public int getMaxHp() { return maxHp; }
        public Role getRole() { return role; }
        public CharDef getCharacter() { return character; }
        public List<Card> getHand() { return hand; }
        public List<Card> getField() { return field; }
        public WeaponCard getWeapon() { return weapon; }
        public boolean isAlive() { return hp > 0; }

        public void drawCard(Card c, Game game) {
            if (c != null) { hand.add(c); game.addLog(" > " + name + " [" + c.name + "] 드로우"); }
        }

        public boolean respondToBang(Game game) {
            for (Card c : field) {
                if (c instanceof BarrelCard) {
                    Card check = game.drawCheck();
                    if (check.suit == Suit.HEART) { game.addLog(name + " 술통 방어!"); return true; }
                }
            }
            for (int i = 0; i < hand.size(); i++) {
                if (hand.get(i) instanceof MissedCard) {
                    game.addLog(name + " [빗나감!] 사용.");
                    game.deck.discard(hand.remove(i));
                    return true;
                }
            }
            return false;
        }

        public boolean discardSpecificCard(Class<? extends Card> clazz, Game game) {
            for (int i = 0; i < hand.size(); i++) {
                if (clazz.isInstance(hand.get(i))) {
                    game.deck.discard(hand.remove(i)); return true;
                }
            }
            return false;
        }
    }

    // ==========================================
    // [5] Game engine (state machine, no Scanner)
    // ==========================================
    public static class Game {
        public List<Player> players;
        public Deck deck = new Deck();
        public int currentPlayerIdx = 0;
        private GameState state = GameState.INIT;
        private final List<String> log = new ArrayList<>();
        private int pendingCardIdx = -1;
        private final List<Player> targetCandidates = new ArrayList<>();

        public Game(List<Player> players) { this.players = players; deck.initDeck(); }

        public GameState getState() { return state; }
        public Player getCurrentPlayer() { return players.get(currentPlayerIdx); }
        public int getCurrentPlayerIdx() { return currentPlayerIdx; }
        public List<Player> getTargetCandidates() { return Collections.unmodifiableList(targetCandidates); }
        public List<String> getLog() { return Collections.unmodifiableList(log); }

        public void addLog(String msg) {
            log.add(msg);
            if (log.size() > 100) log.remove(0);
        }

        public void startGame() {
            CharDef[] charPool = { new BartCassidy(), new WillyTheKid(), new CalamityJanet(), new BartCassidy() };
            List<Role> roles = new ArrayList<>(Arrays.asList(Role.SHERIFF, Role.OUTLAW, Role.OUTLAW, Role.RENEGADE));
            Collections.shuffle(roles);
            for (int i = 0; i < players.size(); i++) {
                Player p = players.get(i);
                p.role = roles.get(i);
                p.character = charPool[i % charPool.length];
                p.maxHp = p.character.hp + (p.role == Role.SHERIFF ? 1 : 0);
                p.hp = p.maxHp;
                for (int j = 0; j < p.hp; j++) { Card c = deck.popCard(); if (c != null) p.hand.add(c); }
            }
            addLog("=== BANG! 게임 시작 ===");
            beginCurrentPlayerTurn();
        }

        private void beginCurrentPlayerTurn() {
            Player p = getCurrentPlayer();
            if (p.hp <= 0) { advanceToNextPlayer(); return; }
            addLog("▶ [" + p.name + "] HP:" + p.hp + "/" + p.maxHp + " | " + p.role);
            p.hasPlayedBang = false;
            if (!handleFieldEffects(p)) { advanceToNextPlayer(); return; }
            p.character.onPhase1(p, this);
            addLog(p.name + " 드로우 완료. 손패 " + p.hand.size() + "장");
            state = GameState.PLAY;
        }

        public void tryPlayCard(int cardIdx) {
            if (state != GameState.PLAY) return;
            Player p = getCurrentPlayer();
            if (cardIdx < 0 || cardIdx >= p.hand.size()) return;
            Card card = p.hand.get(cardIdx);
            if (card instanceof BangCard && p.hasPlayedBang && !p.character.canInfiniteBang()) {
                addLog("뱅은 한 번만!"); return;
            }
            boolean needsTarget = card instanceof BangCard || card instanceof PanicCard ||
                                  card instanceof CatBalouCard || card instanceof DuelCard ||
                                  card instanceof JailCard;
            if (needsTarget) {
                targetCandidates.clear();
                for (Player t : players) {
                    if (t == p || t.hp <= 0) continue;
                    if (card instanceof BangCard && !canHit(p, t)) continue;
                    if (card instanceof PanicCard && getDist(p, t) > 1) continue;
                    targetCandidates.add(t);
                }
                if (targetCandidates.isEmpty()) { addLog("유효한 타겟 없음."); return; }
                pendingCardIdx = cardIdx;
                state = GameState.SELECT_TARGET;
            } else {
                p.hand.remove(cardIdx);
                card.execute(p, null, this);
                if (card.type == CardType.BROWN) deck.discard(card);
                checkGameOver();
            }
        }

        public void confirmTarget(int targetIdx) {
            if (state != GameState.SELECT_TARGET) return;
            if (targetIdx < 0 || targetIdx >= targetCandidates.size()) return;
            Player p = getCurrentPlayer();
            Player target = targetCandidates.get(targetIdx);
            Card card = p.hand.remove(pendingCardIdx);
            pendingCardIdx = -1;
            state = GameState.PLAY;
            card.execute(p, target, this);
            if (card.type == CardType.BROWN) deck.discard(card);
            targetCandidates.clear();
            checkGameOver();
        }

        public void cancelTarget() {
            if (state != GameState.SELECT_TARGET) return;
            pendingCardIdx = -1;
            targetCandidates.clear();
            state = GameState.PLAY;
        }

        public void endTurn() {
            if (state != GameState.PLAY) return;
            Player p = getCurrentPlayer();
            int discarded = 0;
            while (p.hand.size() > p.hp) { deck.discard(p.hand.remove(0)); discarded++; }
            if (discarded > 0) addLog(p.name + " " + discarded + "장 버림.");
            addLog("[" + p.name + "] 턴 종료.");
            advanceToNextPlayer();
        }

        private void advanceToNextPlayer() {
            currentPlayerIdx = (currentPlayerIdx + 1) % players.size();
            if (!checkGameOver()) beginCurrentPlayerTurn();
        }

        private boolean handleFieldEffects(Player p) {
            Iterator<Card> it = p.field.iterator();
            while (it.hasNext()) {
                Card c = it.next();
                if (c instanceof JailCard) {
                    Card check = drawCheck(); it.remove(); deck.discard(c);
                    if (check.suit != Suit.HEART) { addLog(p.name + " 탈옥 실패!"); return false; }
                    addLog(p.name + " 탈옥 성공!");
                } else if (c instanceof DynamiteCard) {
                    Card check = drawCheck();
                    if (check.suit == Suit.SPADE && check.value >= 2 && check.value <= 9) {
                        addLog("다이너마이트 폭발! " + p.name + " -3HP");
                        loseHP(p, null, 3); it.remove(); deck.discard(c);
                    } else {
                        addLog("다이너마이트 전달...");
                        it.remove();
                        players.get((players.indexOf(p) + 1) % players.size()).field.add(c);
                    }
                }
            }
            return true;
        }

        public Card drawCheck() {
            Card c = deck.popCard();
            if (c != null) addLog("[판정] " + c.name + " " + c.suit + " " + c.value);
            deck.discard(c);
            return c;
        }

        public int getDist(Player a, Player t) {
            List<Player> alive = new ArrayList<>();
            for (Player p : players) if (p.hp > 0) alive.add(p);
            int ai = alive.indexOf(a), ti = alive.indexOf(t), n = alive.size();
            if (n == 0) return 999;
            int base = Math.min(Math.abs(ai - ti), n - Math.abs(ai - ti));
            return base + t.character.getDistanceMod() + (t.hasMustang ? 1 : 0)
                       - a.character.getRangeMod() - (a.hasAppaloosa ? 1 : 0);
        }

        public boolean canHit(Player a, Player t) {
            return getDist(a, t) <= (a.weapon == null ? 1 : a.weapon.range);
        }

        public void getHP(Player p, int amt) {
            p.hp = Math.min(p.hp + amt, p.maxHp);
            addLog(p.name + " 회복! HP:" + p.hp + "/" + p.maxHp);
        }

        public void loseHP(Player t, Player a, int amt) {
            t.hp -= amt;
            addLog(t.name + " -" + amt + "HP! 현재:" + t.hp + "/" + t.maxHp);
            if (t.hp <= 0) addLog("[사망] " + t.name);
            t.character.onDamaged(t, a, this, amt);
        }

        private boolean checkGameOver() {
            int outlaws = 0; boolean sheriff = false;
            for (Player p : players) {
                if (p.hp > 0) { if (p.role == Role.SHERIFF) sheriff = true; else outlaws++; }
            }
            if (!sheriff) { addLog("★ 악당 승리!"); state = GameState.GAME_OVER; return true; }
            if (outlaws == 0) { addLog("★ 정의 구현!"); state = GameState.GAME_OVER; return true; }
            return false;
        }
    }

    // ==========================================
    // [6] Deck
    // ==========================================
    public static class Deck {
        public List<Card> draw = new ArrayList<>(), discard = new ArrayList<>();

        public Card popCard() {
            if (draw.isEmpty()) { draw.addAll(discard); discard.clear(); Collections.shuffle(draw); }
            return draw.isEmpty() ? null : draw.remove(draw.size() - 1);
        }

        public void discard(Card c) { if (c != null) discard.add(c); }

        public void initDeck() {
            for (int i = 0; i < 30; i++) draw.add(new BangCard());
            for (int i = 0; i < 15; i++) draw.add(new MissedCard());
            for (int i = 0; i < 8; i++) draw.add(new BeerCard());
            for (int i = 0; i < 3; i++) draw.add(new IndiansCard());
            for (int i = 0; i < 2; i++) draw.add(new GatlingCard());
            for (int i = 0; i < 3; i++) draw.add(new DuelCard());
            draw.add(new DynamiteCard()); draw.add(new JailCard()); draw.add(new BarrelCard());
            draw.add(new WeaponCard("Volcanic", 1));
            draw.add(new WeaponCard("Schofield", 2));
            draw.add(new WeaponCard("Remington", 3));
            draw.add(new WeaponCard("Carabine", 4));
            draw.add(new WeaponCard("Winchester", 5));
            Collections.shuffle(draw);
        }
    }

    public static void main(String[] args) {
        List<Player> players = Arrays.asList(
            new Player("P1"), new Player("P2"), new Player("P3"), new Player("P4")
        );
        Game game = new Game(players);
        game.startGame();
        for (String line : game.getLog()) System.out.println(line);
    }
}
