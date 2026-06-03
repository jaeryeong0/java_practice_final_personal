package game;

import java.util.*;

public class GameLogic {

    public enum Role { SHERIFF, DEPUTY, OUTLAW, RENEGADE }
    public enum Suit { SPADE, HEART, DIAMOND, CLUB }
    public enum CardType { BLUE, BROWN }
    public enum GameState { INIT, PLAY, SELECT_TARGET, GENERAL_STORE, GAME_OVER }

    // ==========================================
    // [2] Cards — Brown (action)
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
            if (!target.respondToBang(game, user)) game.loseHP(target, user, 1);
        }
    }

    public static class MissedCard extends Card {
        public MissedCard() { super("Missed!", Suit.CLUB, 1, CardType.BROWN); }
        @Override public void execute(Player user, Player target, Game game) {}
    }

    public static class BeerCard extends Card {
        public BeerCard() { super("Beer", Suit.HEART, 1, CardType.BROWN); }
        @Override public void execute(Player user, Player target, Game game) {
            long alive = game.players.stream().filter(p -> p.hp > 0).count();
            if (alive <= 2) { game.addLog("맥주: 2인 이하 무효."); return; }
            game.getHP(user, 1);
        }
    }

    public static class SaloonCard extends Card {
        public SaloonCard() { super("Saloon", Suit.HEART, 1, CardType.BROWN); }
        @Override public void execute(Player user, Player target, Game game) {
            game.addLog("[!] 살롱! 모두 HP+1");
            for (Player p : game.players) if (p.hp > 0) game.getHP(p, 1);
        }
    }

    public static class StagecoachCard extends Card {
        public StagecoachCard() { super("Stagecoach", Suit.SPADE, 9, CardType.BROWN); }
        @Override public void execute(Player user, Player target, Game game) {
            user.drawCard(game.deck.popCard(), game); user.drawCard(game.deck.popCard(), game);
        }
    }

    public static class WellsFargoCard extends Card {
        public WellsFargoCard() { super("Wells Fargo", Suit.HEART, 3, CardType.BROWN); }
        @Override public void execute(Player user, Player target, Game game) {
            user.drawCard(game.deck.popCard(), game);
            user.drawCard(game.deck.popCard(), game);
            user.drawCard(game.deck.popCard(), game);
        }
    }

    public static class GeneralStoreCard extends Card {
        public GeneralStoreCard() { super("General Store", Suit.SPADE, 9, CardType.BROWN); }
        @Override public void execute(Player user, Player target, Game game) {
            game.addLog("[!] 잡화점!");
            game.startGeneralStore(user);
        }
    }

    public static class DuelCard extends Card {
        public DuelCard() { super("Duel", Suit.CLUB, 7, CardType.BROWN); }
        @Override public void execute(Player user, Player target, Game game) {
            game.addLog("[!] " + user.name + " vs " + target.name + " 결투!");
            Player attacker = user, defender = target;
            while (true) {
                if (!defender.discardForBang(game)) { game.loseHP(defender, attacker, 1); break; }
                Player tmp = attacker; attacker = defender; defender = tmp;
            }
        }
    }

    public static class IndiansCard extends Card {
        public IndiansCard() { super("Indians!", Suit.DIAMOND, 1, CardType.BROWN); }
        @Override public void execute(Player user, Player target, Game game) {
            game.addLog("[!] 인디언 습격!");
            for (Player p : game.players)
                if (p != user && p.hp > 0 && !p.discardForBang(game)) game.loseHP(p, user, 1);
        }
    }

    public static class GatlingCard extends Card {
        public GatlingCard() { super("Gatling", Suit.HEART, 10, CardType.BROWN); }
        @Override public void execute(Player user, Player target, Game game) {
            game.addLog("[!] 개틀링!");
            for (Player p : game.players)
                if (p != user && p.hp > 0 && !p.respondToBang(game, user)) game.loseHP(p, user, 1);
        }
    }

    public static class PanicCard extends Card {
        public PanicCard() { super("Panic!", Suit.HEART, 8, CardType.BROWN); }
        @Override public void execute(Player user, Player target, Game game) {
            if (target == null) return;
            // Can steal from hand, field, or weapon
            List<Card> pool = new ArrayList<>(target.hand);
            pool.addAll(target.field);
            if (target.weapon != null) pool.add(target.weapon);
            if (pool.isEmpty()) { game.addLog(target.name + " 카드 없음."); return; }
            Card stolen = pool.get(new Random().nextInt(pool.size()));
            if (target.hand.remove(stolen)) {}
            else if (target.field.remove(stolen)) {}
            else if (stolen == target.weapon) target.weapon = null;
            user.hand.add(stolen);
            game.addLog(user.name + "가 " + target.name + "의 [" + stolen.name + "] 강탈!");
        }
    }

    public static class CatBalouCard extends Card {
        public CatBalouCard() { super("Cat Balou", Suit.DIAMOND, 9, CardType.BROWN); }
        @Override public void execute(Player user, Player target, Game game) {
            if (target == null) return;
            List<Card> pool = new ArrayList<>(target.hand);
            pool.addAll(target.field);
            if (target.weapon != null) pool.add(target.weapon);
            if (pool.isEmpty()) { game.addLog(target.name + " 카드 없음."); return; }
            Card c = pool.get(new Random().nextInt(pool.size()));
            if (target.hand.remove(c)) {}
            else if (target.field.remove(c)) {}
            else if (c == target.weapon) target.weapon = null;
            game.deck.discard(c);
            game.addLog(target.name + "의 [" + c.name + "] 버림.");
        }
    }

    // ==========================================
    // Blue (equipment)
    // ==========================================
    public static class WeaponCard extends Card {
        public int range;
        public WeaponCard(String name, int range) { super(name, Suit.DIAMOND, 1, CardType.BLUE); this.range = range; }
        @Override public void execute(Player user, Player target, Game game) {
            if (user.weapon != null) game.deck.discard(user.weapon);
            user.weapon = this;
            game.addLog(user.name + " [" + name + "] 장착!");
        }
    }

    public static class VolcanicCard extends WeaponCard {
        public VolcanicCard() { super("Volcanic", 1); }
    }

    public static class BarrelCard extends Card {
        public BarrelCard() { super("Barrel", Suit.SPADE, 12, CardType.BLUE); }
        @Override public void execute(Player user, Player target, Game game) {
            user.field.add(this); game.addLog(user.name + " [술통] 장착!");
        }
    }

    public static class MustangCard extends Card {
        public MustangCard() { super("Mustang", Suit.HEART, 6, CardType.BLUE); }
        @Override public void execute(Player user, Player target, Game game) {
            user.field.add(this); game.addLog(user.name + " [무스탕] 장착!");
        }
    }

    public static class AppaloosaCard extends Card {
        public AppaloosaCard() { super("Appaloosa", Suit.SPADE, 9, CardType.BLUE); }
        @Override public void execute(Player user, Player target, Game game) {
            user.field.add(this); game.addLog(user.name + " [아팔루사] 장착!");
        }
    }

    public static class JailCard extends Card {
        public JailCard() { super("Jail", Suit.SPADE, 4, CardType.BLUE); }
        @Override public void execute(Player user, Player target, Game game) {
            if (target == null) return;
            if (target.role == Role.SHERIFF) { game.addLog("보안관은 감옥 불가."); user.hand.add(this); return; }
            target.field.add(this); game.addLog(target.name + " 감옥!");
        }
    }

    public static class DynamiteCard extends Card {
        public DynamiteCard() { super("Dynamite", Suit.HEART, 2, CardType.BLUE); }
        @Override public void execute(Player user, Player target, Game game) {
            user.field.add(this); game.addLog(user.name + " [다이너마이트] 설치!");
        }
    }

    // ==========================================
    // [3] Characters
    // ==========================================
    public static abstract class CharDef {
        public String name; public int hp;
        public CharDef(String name, int hp) { this.name = name; this.hp = hp; }
        public void onPhase1(Player self, Game game) {
            self.drawCard(game.deck.popCard(), game); self.drawCard(game.deck.popCard(), game);
        }
        public void onDamaged(Player self, Player attacker, Game game, int amount) {}
        public void onKill(Player self, Player killed, Game game) {}
        public void onCardPlayed(Player self, Game game) {}
        public int getDistanceMod() { return 0; }
        public int getRangeMod()    { return 0; }
        public boolean canInfiniteBang()    { return false; }
        public boolean canUseMissedAsBang() { return false; }
        public boolean canUseBangAsMissed() { return false; }
        public String getName() { return name; }
    }

    public static class BartCassidy extends CharDef {
        public BartCassidy() { super("Bart Cassidy", 4); }
        @Override public void onDamaged(Player self, Player attacker, Game game, int amount) {
            for (int i = 0; i < amount; i++) self.drawCard(game.deck.popCard(), game);
        }
    }

    public static class BlackJack extends CharDef {
        public BlackJack() { super("Black Jack", 4); }
        @Override public void onPhase1(Player self, Game game) {
            self.drawCard(game.deck.popCard(), game);
            Card second = game.deck.popCard();
            self.drawCard(second, game);
            if (second != null && (second.suit == Suit.HEART || second.suit == Suit.DIAMOND)) {
                game.addLog("(블랙잭) 추가 드로우!");
                self.drawCard(game.deck.popCard(), game);
            }
        }
    }

    public static class CalamityJanet extends CharDef {
        public CalamityJanet() { super("Calamity Janet", 4); }
        @Override public boolean canUseMissedAsBang() { return true; }
        @Override public boolean canUseBangAsMissed() { return true; }
    }

    public static class Jourdonnais extends CharDef {
        public Jourdonnais() { super("Jourdonnais", 4); }
        // Built-in barrel handled in Player.respondToBang
    }

    public static class KitCarlson extends CharDef {
        public KitCarlson() { super("Kit Carlson", 4); }
        @Override public void onPhase1(Player self, Game game) {
            Card a = game.deck.popCard(), b = game.deck.popCard(), c = game.deck.popCard();
            self.drawCard(a, game); self.drawCard(b, game);
            if (c != null) { game.deck.draw.add(0, c); game.addLog("(킷 칼슨) 카드 1장 덱 위 반환."); }
        }
    }

    public static class LuckyDuke extends CharDef {
        public LuckyDuke() { super("Lucky Duke", 4); }
        // drawCheckFor(player) handles flipping 2 cards in Game
    }

    public static class SidKetchum extends CharDef {
        public SidKetchum() { super("Sid Ketchum", 4); }
        // game.sidKetchumHeal() exposes this ability
    }

    public static class WillyTheKid extends CharDef {
        public WillyTheKid() { super("Willy the Kid", 4); }
        @Override public boolean canInfiniteBang() { return true; }
    }

    // ==========================================
    // [4] Player
    // ==========================================
    public static class Player {
        public String name; public int hp, maxHp;
        public Role role; public CharDef character;
        public List<Card> hand  = new ArrayList<>();
        public List<Card> field = new ArrayList<>();
        public WeaponCard weapon = null;
        public boolean hasPlayedBang = false;

        public Player(String name) { this.name = name; }
        public String getName()       { return name; }
        public int getHp()            { return hp; }
        public int getMaxHp()         { return maxHp; }
        public Role getRole()         { return role; }
        public CharDef getCharacter() { return character; }
        public List<Card> getHand()   { return hand; }
        public List<Card> getField()  { return field; }
        public WeaponCard getWeapon() { return weapon; }
        public boolean isAlive()      { return hp > 0; }

        public boolean hasMustang()   { for (Card c : field) if (c instanceof MustangCard)   return true; return false; }
        public boolean hasAppaloosa() { for (Card c : field) if (c instanceof AppaloosaCard) return true; return false; }

        public void drawCard(Card c, Game game) {
            if (c != null) { hand.add(c); game.addLog(" > " + name + " [" + c.name + "] 드로우"); }
        }

        public boolean respondToBang(Game game, Player attacker) {
            int required = 1;

            // Barrel(s) in field
            for (Card c : field) {
                if (c instanceof BarrelCard) {
                    Card check = game.drawCheckFor(this);
                    if (check != null && check.suit == Suit.HEART) { game.addLog(name + " 술통 방어!"); return true; }
                }
            }
            // Jourdonnais built-in barrel
            if (character instanceof Jourdonnais) {
                Card check = game.drawCheckFor(this);
                if (check != null && check.suit == Suit.HEART) { game.addLog(name + " (조르당네) 방어!"); return true; }
            }

            // Collect defence cards: Missed!, or Bang! for CalamityJanet
            List<Integer> defIdx = new ArrayList<>();
            for (int i = 0; i < hand.size() && defIdx.size() < required; i++) {
                Card c = hand.get(i);
                if (c instanceof MissedCard || (character instanceof CalamityJanet && c instanceof BangCard))
                    defIdx.add(i);
            }
            if (defIdx.size() >= required) {
                defIdx.sort(Collections.reverseOrder());
                for (int idx : defIdx) game.deck.discard(hand.remove(idx));
                game.addLog(name + " [빗나감!]" + (required > 1 ? " x2" : "") + " 사용.");
                return true;
            }
            return false;
        }

        // Discard a Bang! (or Missed! for CalamityJanet) for Duel / Indians
        public boolean discardForBang(Game game) {
            for (int i = 0; i < hand.size(); i++) {
                Card c = hand.get(i);
                if (c instanceof BangCard || (character instanceof CalamityJanet && c instanceof MissedCard)) {
                    game.deck.discard(hand.remove(i)); return true;
                }
            }
            return false;
        }
    }

    // ==========================================
    // [5] Game engine
    // ==========================================
    public static class Game {
        public List<Player> players;
        public Deck deck = new Deck();
        public int currentPlayerIdx = 0;
        private GameState state = GameState.INIT;
        private final List<String> log = new ArrayList<>();
        private int pendingCardIdx = -1;
        private final List<Player> targetCandidates = new ArrayList<>();

        // General Store state
        private final List<Card> generalStorePool = new ArrayList<>();
        private int generalStorePickerIdx = 0;

        public Game(List<Player> players) { this.players = players; deck.initDeck(); }

        public GameState getState()    { return state; }
        public Player getCurrentPlayer() { return players.get(currentPlayerIdx); }
        public int getCurrentPlayerIdx() { return currentPlayerIdx; }
        public List<Player> getTargetCandidates() { return Collections.unmodifiableList(targetCandidates); }
        public List<Card>   getGeneralStorePool() { return Collections.unmodifiableList(generalStorePool); }
        public int          getGeneralStorePickerIdx() { return generalStorePickerIdx; }
        public List<String> getLog() { return Collections.unmodifiableList(log); }

        public void addLog(String msg) { log.add(msg); if (log.size() > 100) log.remove(0); }

        private static final CharDef[] ALL_CHARS = {
            new BartCassidy(), new BlackJack(),  new CalamityJanet(), new Jourdonnais(),
            new KitCarlson(),  new LuckyDuke(), new SidKetchum(),     new WillyTheKid()
        };

        public void startGame() {
            List<CharDef> charPool = new ArrayList<>(Arrays.asList(ALL_CHARS));
            Collections.shuffle(charPool);
            List<Role> roles = buildRoles(players.size());
            Collections.shuffle(roles);
            for (int i = 0; i < players.size(); i++) {
                Player p = players.get(i);
                p.role      = roles.get(i);
                p.character = charPool.get(i);
                p.maxHp     = p.character.hp + (p.role == Role.SHERIFF ? 1 : 0);
                p.hp        = p.maxHp;
                for (int j = 0; j < p.hp; j++) { Card c = deck.popCard(); if (c != null) p.hand.add(c); }
            }
            addLog("=== BANG! 게임 시작 ===");
            beginCurrentPlayerTurn();
        }

        private List<Role> buildRoles(int n) {
            List<Role> r = new ArrayList<>(Arrays.asList(Role.SHERIFF, Role.OUTLAW, Role.OUTLAW, Role.RENEGADE));
            if (n >= 5) r.add(Role.DEPUTY);
            if (n >= 6) r.add(Role.OUTLAW);
            if (n >= 7) { r.add(Role.DEPUTY); }
            return r.subList(0, Math.min(n, r.size()));
        }

        private void beginCurrentPlayerTurn() {
            Player p = getCurrentPlayer();
            if (p.hp <= 0) { advanceToNextPlayer(); return; }
            addLog("▶ [" + p.name + "] " + p.character.name + "  HP:" + p.hp + "/" + p.maxHp + " | " + p.role);
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

            boolean actingAsBang = card instanceof BangCard ||
                (p.character instanceof CalamityJanet && card instanceof MissedCard);
            boolean infiniteBang = p.character.canInfiniteBang() || (p.weapon instanceof VolcanicCard);

            if (actingAsBang && p.hasPlayedBang && !infiniteBang) { addLog("뱅은 한 번만!"); return; }

            boolean needsTarget = actingAsBang || card instanceof PanicCard
                || card instanceof CatBalouCard || card instanceof DuelCard || card instanceof JailCard;

            if (needsTarget) {
                targetCandidates.clear();
                for (Player t : players) {
                    if (t == p || t.hp <= 0) continue;
                    if (actingAsBang && !canHit(p, t)) continue;
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
                p.character.onCardPlayed(p, this);
                checkGameOver();
            }
        }

        public void confirmTarget(int targetIdx) {
            if (state != GameState.SELECT_TARGET) return;
            if (targetIdx < 0 || targetIdx >= targetCandidates.size()) return;
            Player p      = getCurrentPlayer();
            Player target = targetCandidates.get(targetIdx);
            Card card     = p.hand.remove(pendingCardIdx);
            pendingCardIdx = -1;
            state = GameState.PLAY;

            // CalamityJanet using Missed! as Bang!
            if (p.character instanceof CalamityJanet && card instanceof MissedCard) {
                p.hasPlayedBang = true;
                addLog("[!] " + p.name + " (Janet) -> " + target.name + " 뱅! (Missed 사용)");
                if (!target.respondToBang(this, p)) loseHP(target, p, 1);
            } else {
                card.execute(p, target, this);
            }
            if (card.type == CardType.BROWN) deck.discard(card);
            targetCandidates.clear();
            p.character.onCardPlayed(p, this);
            checkGameOver();
        }

        public void cancelTarget() {
            if (state != GameState.SELECT_TARGET) return;
            pendingCardIdx = -1; targetCandidates.clear(); state = GameState.PLAY;
        }

        public void endTurn() {
            if (state != GameState.PLAY) return;
            Player p = getCurrentPlayer();
            int d = 0;
            while (p.hand.size() > p.hp) { deck.discard(p.hand.remove(0)); d++; }
            if (d > 0) addLog(p.name + " " + d + "장 버림.");
            addLog("[" + p.name + "] 턴 종료.");
            advanceToNextPlayer();
        }

        // Sid Ketchum: discard 2 for +1 HP (call anytime during PLAY)
        public boolean sidKetchumHeal() {
            if (state != GameState.PLAY) return false;
            Player p = getCurrentPlayer();
            if (!(p.character instanceof SidKetchum) || p.hand.size() < 2) {
                addLog("시드 케첨 능력 사용 불가."); return false;
            }
            deck.discard(p.hand.remove(0)); deck.discard(p.hand.remove(0));
            getHP(p, 1);
            addLog("(시드 케첨) 카드 2장 → HP+1");
            return true;
        }

        // General Store
        void startGeneralStore(Player starter) {
            generalStorePool.clear();
            long alive = players.stream().filter(pl -> pl.hp > 0).count();
            for (int i = 0; i < alive; i++) { Card c = deck.popCard(); if (c != null) generalStorePool.add(c); }
            generalStorePickerIdx = players.indexOf(starter);
            while (players.get(generalStorePickerIdx).hp <= 0)
                generalStorePickerIdx = (generalStorePickerIdx + 1) % players.size();
            state = GameState.GENERAL_STORE;
            addLog(players.get(generalStorePickerIdx).name + " 먼저 선택!");
        }

        public void pickGeneralStoreCard(int poolIdx) {
            if (state != GameState.GENERAL_STORE) return;
            if (poolIdx < 0 || poolIdx >= generalStorePool.size()) return;
            Player picker = players.get(generalStorePickerIdx);
            Card chosen = generalStorePool.remove(poolIdx);
            picker.hand.add(chosen);
            addLog(picker.name + " [" + chosen.name + "] 선택!");
            if (generalStorePool.isEmpty()) { state = GameState.PLAY; addLog("잡화점 종료."); checkGameOver(); return; }
            do { generalStorePickerIdx = (generalStorePickerIdx + 1) % players.size(); }
            while (players.get(generalStorePickerIdx).hp <= 0);
            addLog(players.get(generalStorePickerIdx).name + " 차례.");
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
                    Card check = drawCheckFor(p); it.remove(); deck.discard(c);
                    if (check == null || check.suit != Suit.HEART) { addLog(p.name + " 탈옥 실패!"); return false; }
                    addLog(p.name + " 탈옥 성공!");
                } else if (c instanceof DynamiteCard) {
                    Card check = drawCheckFor(p);
                    if (check != null && check.suit == Suit.SPADE && check.value >= 2 && check.value <= 9) {
                        addLog("다이너마이트 폭발! " + p.name + " -3HP");
                        loseHP(p, null, 3); it.remove(); deck.discard(c);
                    } else {
                        addLog("다이너마이트 다음 사람에게..");
                        it.remove(); players.get((players.indexOf(p) + 1) % players.size()).field.add(c);
                    }
                }
            }
            return true;
        }

        // Normal draw check
        public Card drawCheck() { return drawCheckFor(null); }

        // LuckyDuke flips 2 and picks the better one
        public Card drawCheckFor(Player p) {
            if (p != null && p.character instanceof LuckyDuke) {
                Card a = deck.popCard(), b = deck.popCard();
                Card chosen = (suitRank(a) >= suitRank(b)) ? a : b;
                Card other  = (chosen == a) ? b : a;
                deck.discard(other);
                if (chosen != null) addLog("[판정x2] " + chosen.name + " " + chosen.suit + " (럭키 듀크)");
                deck.discard(chosen); return chosen;
            }
            Card c = deck.popCard();
            if (c != null) addLog("[판정] " + c.name + " " + c.suit + " " + c.value);
            deck.discard(c); return c;
        }

        private int suitRank(Card c) {
            if (c == null) return 0;
            switch (c.suit) { case HEART: return 4; case DIAMOND: return 3; case CLUB: return 2; default: return 1; }
        }

        public int getDist(Player a, Player t) {
            List<Player> alive = new ArrayList<>();
            for (Player p : players) if (p.hp > 0) alive.add(p);
            int ai = alive.indexOf(a), ti = alive.indexOf(t), n = alive.size();
            if (n == 0) return 999;
            int base = Math.min(Math.abs(ai - ti), n - Math.abs(ai - ti));
            return base + t.character.getDistanceMod() + (t.hasMustang() ? 1 : 0)
                       - a.character.getRangeMod() - (a.hasAppaloosa() ? 1 : 0);
        }

        public boolean canHit(Player a, Player t) {
            return getDist(a, t) <= (a.weapon == null ? 1 : a.weapon.range);
        }

        public void getHP(Player p, int amt) {
            p.hp = Math.min(p.hp + amt, p.maxHp);
            addLog(p.name + " 회복! HP:" + p.hp + "/" + p.maxHp);
        }

        public void loseHP(Player t, Player attacker, int amt) {
            t.hp -= amt;
            addLog(t.name + " -" + amt + "HP! 현재:" + t.hp + "/" + t.maxHp);
            t.character.onDamaged(t, attacker, this, amt); // BartCassidy draws here
            if (t.hp <= 0) {
                addLog("[사망] " + t.name);
            }
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
            for (int i = 0; i < 25; i++) draw.add(new BangCard());
            for (int i = 0; i < 12; i++) draw.add(new MissedCard());
            for (int i = 0; i < 6;  i++) draw.add(new BeerCard());
            for (int i = 0; i < 2;  i++) draw.add(new SaloonCard());
            for (int i = 0; i < 2;  i++) draw.add(new StagecoachCard());
            for (int i = 0; i < 3;  i++) draw.add(new WellsFargoCard());
            for (int i = 0; i < 2;  i++) draw.add(new GeneralStoreCard());
            for (int i = 0; i < 4;  i++) draw.add(new PanicCard());
            for (int i = 0; i < 4;  i++) draw.add(new CatBalouCard());
            for (int i = 0; i < 3;  i++) draw.add(new IndiansCard());
            for (int i = 0; i < 2;  i++) draw.add(new GatlingCard());
            for (int i = 0; i < 3;  i++) draw.add(new DuelCard());
            draw.add(new DynamiteCard());
            for (int i = 0; i < 2;  i++) draw.add(new JailCard());
            for (int i = 0; i < 2;  i++) draw.add(new BarrelCard());
            for (int i = 0; i < 2;  i++) draw.add(new MustangCard());
            draw.add(new AppaloosaCard());
            draw.add(new VolcanicCard());
            draw.add(new WeaponCard("Schofield",  2));
            draw.add(new WeaponCard("Remington",  3));
            draw.add(new WeaponCard("Carabine",   4));
            draw.add(new WeaponCard("Winchester", 5));
            Collections.shuffle(draw);
        }
    }

    public static void main(String[] args) {
        List<Player> ps = Arrays.asList(
            new Player("P1"), new Player("P2"), new Player("P3"), new Player("P4")
        );
        Game g = new Game(ps); g.startGame();
        for (String l : g.getLog()) System.out.println(l);
    }
}
