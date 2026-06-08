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
            game.addLog("[!] " + user.name + " -> " + target.name + " Bang!");
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
            game.getHP(user, 1);
        }
    }

    public static class SaloonCard extends Card {
        public SaloonCard() { super("Saloon", Suit.HEART, 1, CardType.BROWN); }
        @Override public void execute(Player user, Player target, Game game) {
            game.addLog("[!] Saloon! All players HP+1");
            for (Player p : game.players) if (p.hp > 0) game.getHP(p, 1);
        }
    }

    public static class StagecoachCard extends Card {
        public StagecoachCard() { super("Stagecoach", Suit.SPADE, 9, CardType.BROWN); }
        @Override public void execute(Player user, Player target, Game game) {
            game.addLog("[!] " + user.name + " (Stagecoach) drew 2 cards.");
            user.drawCard(game.deck.popCard(), game); user.drawCard(game.deck.popCard(), game);
        }
    }

    public static class WellsFargoCard extends Card {
        public WellsFargoCard() { super("Wells Fargo", Suit.HEART, 3, CardType.BROWN); }
        @Override public void execute(Player user, Player target, Game game) {
            game.addLog("[!] " + user.name + " (Wells Fargo) drew 3 cards.");
            user.drawCard(game.deck.popCard(), game);
            user.drawCard(game.deck.popCard(), game);
            user.drawCard(game.deck.popCard(), game);
        }
    }

    public static class GeneralStoreCard extends Card {
        public GeneralStoreCard() { super("General Store", Suit.SPADE, 9, CardType.BROWN); }
        @Override public void execute(Player user, Player target, Game game) {
            game.addLog("[!] General Store!");
            game.startGeneralStore(user);
        }
    }

    public static class DuelCard extends Card {
        public DuelCard() { super("Duel", Suit.CLUB, 7, CardType.BROWN); }
        @Override public void execute(Player user, Player target, Game game) {
            game.addLog("[!] " + user.name + " vs " + target.name + " Duel!");
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
            game.addLog("[!] Indians!");
            for (Player p : game.players)
                if (p != user && p.hp > 0 && !p.discardForBang(game)) game.loseHP(p, user, 1);
        }
    }

    public static class GatlingCard extends Card {
        public GatlingCard() { super("Gatling", Suit.HEART, 10, CardType.BROWN); }
        @Override public void execute(Player user, Player target, Game game) {
            game.addLog("[!] Gatling!");
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
            if (pool.isEmpty()) { game.addLog(target.name + " has no cards."); return; }
            Card stolen = pool.get(new Random().nextInt(pool.size()));
            boolean fromHand = target.hand.remove(stolen);
            if (!fromHand) { if (!target.field.remove(stolen)) if (stolen == target.weapon) target.weapon = null; }
            user.hand.add(stolen);
            if (fromHand) game.addLog(user.name + " stole a card from " + target.name + "'s hand!");
            else game.addLog(user.name + " stole [" + stolen.name + "] from " + target.name + "!");
        }
    }

    public static class CatBalouCard extends Card {
        public CatBalouCard() { super("Cat Balou", Suit.DIAMOND, 9, CardType.BROWN); }
        @Override public void execute(Player user, Player target, Game game) {
            if (target == null) return;
            List<Card> pool = new ArrayList<>(target.hand);
            pool.addAll(target.field);
            if (target.weapon != null) pool.add(target.weapon);
            if (pool.isEmpty()) { game.addLog(target.name + " has no cards."); return; }
            Card c = pool.get(new Random().nextInt(pool.size()));
            if (target.hand.remove(c)) {}
            else if (target.field.remove(c)) {}
            else if (c == target.weapon) target.weapon = null;
            game.deck.discard(c);
            game.addLog("[" + c.name + "] discarded from " + target.name + ".");
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
            game.addLog(user.name + " equipped [" + name + "]!");
        }
    }

    public static class VolcanicCard extends WeaponCard {
        public VolcanicCard() { super("Volcanic", 1); }
    }

    public static class BarrelCard extends Card {
        public BarrelCard() { super("Barrel", Suit.SPADE, 12, CardType.BLUE); }
        @Override public void execute(Player user, Player target, Game game) {
            user.field.add(this); game.addLog(user.name + " equipped [Barrel]!");
        }
    }

    public static class MustangCard extends Card {
        public MustangCard() { super("Mustang", Suit.HEART, 6, CardType.BLUE); }
        @Override public void execute(Player user, Player target, Game game) {
            user.field.add(this); game.addLog(user.name + " equipped [Mustang]!");
        }
    }

    public static class ScopeCard extends Card {
        public ScopeCard() { super("Scope", Suit.SPADE, 9, CardType.BLUE); }
        @Override public void execute(Player user, Player target, Game game) {
            user.field.add(this); game.addLog(user.name + " equipped [Scope]!");
        }
    }

    public static class JailCard extends Card {
        public JailCard() { super("Jail", Suit.SPADE, 4, CardType.BLUE); }
        @Override public void execute(Player user, Player target, Game game) {
            if (target == null) return;
            if (target.role == Role.SHERIFF) { game.addLog("Sheriff cannot be jailed."); user.hand.add(this); return; }
            target.field.add(this); game.addLog(target.name + " jailed!");
        }
    }

    public static class DynamiteCard extends Card {
        public DynamiteCard() { super("Dynamite", Suit.HEART, 2, CardType.BLUE); }
        @Override public void execute(Player user, Player target, Game game) {
            user.field.add(this); game.addLog(user.name + " planted [Dynamite]!");
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
            game.addLog("(Bart Cassidy) " + self.name + " drew " + amount + " card(s).");
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
                game.addLog("(Black Jack) Extra draw!");
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
            if (c != null) { game.deck.draw.add(0, c); game.addLog("(Kit Carlson) 1 card returned to top of deck."); }
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

        public boolean hasMustang() { for (Card c : field) if (c instanceof MustangCard) return true; return false; }
        public boolean hasScope()   { for (Card c : field) if (c instanceof ScopeCard)   return true; return false; }

        public void drawCard(Card c, Game game) {
            if (c != null) hand.add(c);
        }

        public boolean respondToBang(Game game, Player attacker) {
            int required = 1;

            // Barrel(s) in field
            for (Card c : field) {
                if (c instanceof BarrelCard) {
                    Card check = game.drawCheckFor(this);
                    if (check != null && check.suit == Suit.HEART) { game.addLog(name + " Barrel defense!"); return true; }
                }
            }
            // Jourdonnais built-in barrel
            if (character instanceof Jourdonnais) {
                Card check = game.drawCheckFor(this);
                if (check != null && check.suit == Suit.HEART) { game.addLog(name + " (Jourdonnais) defense!"); return true; }
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
                game.addLog(name + " used [Missed!]" + (required > 1 ? " x2" : "") + ".");
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
        public Card         getTopDiscard() { return deck.discard.isEmpty() ? null : deck.discard.get(deck.discard.size() - 1); }

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
            for (int i = 0; i < players.size(); i++)
                if (players.get(i).role == Role.SHERIFF) { currentPlayerIdx = i; break; }
            addLog("=== BANG! Game Start ===");
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
            addLog("[" + p.name + "] " + p.character.name + "  HP:" + p.hp + "/" + p.maxHp + " | " + p.role);
            p.hasPlayedBang = false;
            if (!handleFieldEffects(p)) { advanceToNextPlayer(); return; }
            p.character.onPhase1(p, this);
            addLog(p.name + " drew cards. Hand: " + p.hand.size());
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

            if (actingAsBang && p.hasPlayedBang && !infiniteBang) { addLog("Bang! can only be used once per turn!"); return; }

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
                if (targetCandidates.isEmpty()) { addLog("No valid targets."); return; }
                pendingCardIdx = cardIdx;
                state = GameState.SELECT_TARGET;
            } else {
                // Missed! is a reaction card — cannot be played actively (except by CalamityJanet as Bang!)
                if (card instanceof MissedCard) { addLog("[!] Missed! is a reaction card."); return; }
                // Beer is invalid when only 2 or fewer players remain
                if (card instanceof BeerCard) {
                    long alive = players.stream().filter(pl -> pl.hp > 0).count();
                    if (alive <= 2) { addLog("[!] Beer: cannot use with 2 or fewer players alive."); return; }
                }
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
                addLog("[!] " + p.name + " (Janet) -> " + target.name + " Bang! (using Missed!)");
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
            while (p.hand.size() > p.maxHp) { deck.discard(p.hand.remove(0)); d++; }
            if (d > 0) addLog(p.name + " discarded " + d + " card(s).");
            addLog("[" + p.name + "] End of turn.");
            // Pass Dynamite to the next alive player so it appears on their board
            // and is checked at the start of their turn by handleFieldEffects().
            Iterator<Card> it = p.field.iterator();
            while (it.hasNext()) {
                Card c = it.next();
                if (c instanceof DynamiteCard) {
                    int nextIdx = (players.indexOf(p) + 1) % players.size();
                    while (players.get(nextIdx).hp <= 0)
                        nextIdx = (nextIdx + 1) % players.size();
                    it.remove();
                    players.get(nextIdx).field.add(c);
                    addLog("[Dynamite] Moved to " + players.get(nextIdx).name + "'s board.");
                }
            }
            advanceToNextPlayer();
        }

        // Sid Ketchum: discard 2 cards for +1 HP — usable at ANY time, even outside own turn
        public boolean sidKetchumHeal() { return sidKetchumHeal(getCurrentPlayer()); }

        public boolean sidKetchumHeal(Player p) {
            if (!(p.character instanceof SidKetchum)) {
                addLog("Only Sid Ketchum can use this."); return false;
            }
            if (p.hand.size() < 2) {
                addLog(p.name + " not enough cards."); return false;
            }
            if (p.hp >= p.maxHp) {
                addLog(p.name + " already at max HP."); return false;
            }
            deck.discard(p.hand.remove(0)); deck.discard(p.hand.remove(0));
            getHP(p, 1);
            addLog("(Sid Ketchum) " + p.name + " discards 2 cards -> HP+1");
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
            addLog(players.get(generalStorePickerIdx).name + " picks first!");
        }

        public void pickGeneralStoreCard(int poolIdx) {
            if (state != GameState.GENERAL_STORE) return;
            if (poolIdx < 0 || poolIdx >= generalStorePool.size()) return;
            Player picker = players.get(generalStorePickerIdx);
            Card chosen = generalStorePool.remove(poolIdx);
            picker.hand.add(chosen);
            addLog(picker.name + " picked [" + chosen.name + "]!");
            if (generalStorePool.isEmpty()) { state = GameState.PLAY; addLog("General Store ended."); checkGameOver(); return; }
            do { generalStorePickerIdx = (generalStorePickerIdx + 1) % players.size(); }
            while (players.get(generalStorePickerIdx).hp <= 0);
            addLog(players.get(generalStorePickerIdx).name + "'s turn.");
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
                    if (check == null || check.suit != Suit.HEART) { addLog(p.name + " failed jail check!"); return false; }
                    addLog(p.name + " escaped jail!");
                } else if (c instanceof DynamiteCard) {
                    addLog("[Dynamite] Draw check for " + p.name + "...");
                    Card check = drawCheckFor(p);
                    String checkResult = check != null
                        ? check.suit.name() + " " + check.value
                        : "none";
                    addLog("[Dynamite] Flipped: " + checkResult);
                    if (check != null && check.suit == Suit.SPADE && check.value >= 2 && check.value <= 9) {
                        addLog("Dynamite explodes! " + p.name + " -3HP");
                        loseHP(p, null, 3); it.remove(); deck.discard(c);
                    } else {
                        addLog("Dynamite did not explode. It stays with " + p.name + " until end of turn.");
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
                if (chosen != null) addLog("[check x2] " + chosen.name + " " + chosen.suit + " (Lucky Duke)");
                deck.discard(chosen); return chosen;
            }
            Card c = deck.popCard();
            if (c != null) addLog("[check] " + c.name + " " + c.suit + " " + c.value);
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
                       - a.character.getRangeMod() - (a.hasScope() ? 1 : 0);
        }

        public boolean canHit(Player a, Player t) {
            return getDist(a, t) <= (a.weapon == null ? 1 : a.weapon.range);
        }

        public void getHP(Player p, int amt) {
            p.hp = Math.min(p.hp + amt, p.maxHp);
            addLog(p.name + " healed! HP:" + p.hp + "/" + p.maxHp);
        }

        public void loseHP(Player t, Player attacker, int amt) {
            t.hp -= amt;
            addLog(t.name + " -" + amt + "HP! Current:" + t.hp + "/" + t.maxHp);
            t.character.onDamaged(t, attacker, this, amt);
            if (tryBeerRescue(t)) return; // saved by Beer — treat as if death never happened
            if (t.hp > 0) return;         // took damage but still alive
            // --- death ---
            addLog("[DEAD] " + t.name);
            if (t.role == Role.OUTLAW && attacker != null) {
                addLog("[REWARD] " + attacker.name + " draws 3 bounty cards!");
                attacker.drawCard(deck.popCard(), this);
                attacker.drawCard(deck.popCard(), this);
                attacker.drawCard(deck.popCard(), this);
            }
            if (t.role == Role.DEPUTY && attacker != null && attacker.role == Role.SHERIFF) {
                addLog("[PENALTY] Sheriff killed Deputy! All hand/equipment discarded!");
                new ArrayList<>(attacker.hand).forEach(deck::discard);
                attacker.hand.clear();
                if (attacker.weapon != null) { deck.discard(attacker.weapon); attacker.weapon = null; }
                new ArrayList<>(attacker.field).forEach(deck::discard);
                attacker.field.clear();
            }
        }

        // Beer rescue: auto-use Beer when hp reaches exactly 0.
        // hp == 0 guarantees 1 Beer restores to 1 HP (covers Dynamite only when pre-damage hp was 3).
        // Beer has no effect when only 2 or fewer players remain.
        private boolean tryBeerRescue(Player t) {
            if (t.hp != 0) return false;
            long otherAlive = players.stream().filter(pl -> pl.hp > 0).count();
            if (otherAlive + 1 <= 2) return false; // t counts as alive (not yet dead)
            for (int i = 0; i < t.hand.size(); i++) {
                if (t.hand.get(i) instanceof BeerCard) {
                    deck.discard(t.hand.remove(i));
                    t.hp = 1;
                    addLog("(Beer) " + t.name + " survives! HP: 1/" + t.maxHp);
                    return true;
                }
            }
            return false;
        }

        private boolean checkGameOver() {
            boolean sheriffAlive = false;
            int aliveCount = 0;
            Player lastAlive = null;
            boolean anyEnemy = false;

            for (Player p : players) {
                if (p.hp > 0) {
                    aliveCount++;
                    lastAlive = p;
                    if (p.role == Role.SHERIFF) sheriffAlive = true;
                    else anyEnemy = true;
                }
            }

            if (!sheriffAlive) {
                // Renegade wins only if they are the sole survivor and killed the Sheriff last
                if (aliveCount == 1 && lastAlive != null && lastAlive.role == Role.RENEGADE) {
                    addLog("★ Renegade wins!");
                } else {
                    addLog("★ Outlaws win!");
                }
                state = GameState.GAME_OVER;
                return true;
            }
            if (!anyEnemy) { addLog("★ Justice prevails!"); state = GameState.GAME_OVER; return true; }
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
            draw.add(new ScopeCard());
            draw.add(new VolcanicCard());
            draw.add(new WeaponCard("Schofield",  2));
            draw.add(new WeaponCard("Remington",  3));
            draw.add(new WeaponCard("Rev. Carabine", 4));
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
