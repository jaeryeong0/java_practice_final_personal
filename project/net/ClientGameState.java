package net;

import java.util.*;

/**
 * Immutable snapshot of game state received from the server.
 * The render thread reads this; the receiver thread replaces it atomically.
 */
public class ClientGameState {

    public String  gameState        = "LOBBY";
    public int     currentPlayerIdx = 0;
    public int     myPlayerIdx      = 0;
    public int     storePickerIdx     = 0;
    public int     catBalouTargetIdx  = -1;

    public final List<PlayerSnap> players   = new ArrayList<>();
    public final List<TargetSnap> targets   = new ArrayList<>();
    public final List<CardSnap>   storePool = new ArrayList<>();
    public final List<String>     log       = new ArrayList<>();
    public CardSnap topDiscard = null;

    // Lobby-only fields
    public final List<String> lobbyNames    = new ArrayList<>();
    public final List<String> chatMessages  = new ArrayList<>();
    public int maxPlayers = 4;

    // ---- convenience -------------------------------------------------------

    public boolean isLobby()  { return "LOBBY".equals(gameState); }
    public boolean isMyTurn() { return currentPlayerIdx == myPlayerIdx; }

    public PlayerSnap myPlayer() {
        if (myPlayerIdx >= 0 && myPlayerIdx < players.size()) return players.get(myPlayerIdx);
        return null;
    }

    // ---- nested POJOs ------------------------------------------------------

    public static class PlayerSnap {
        public String name    = "";
        public int    hp      = 0;
        public int    maxHp   = 0;
        public String role    = "";
        public String charName = "";
        public int    handSize = 0;
        public boolean alive  = false;
        public final List<CardSnap>   hand   = new ArrayList<>();
        public final List<CardSnap>   field  = new ArrayList<>();
        public WeaponSnap weapon = null;
    }

    public static class CardSnap {
        public String name  = "";
        public String suit  = "";
        public int    value = 0;
        public String type  = ""; // "BROWN" (action) or "BLUE" (equipment)
        public int    weaponRange = -1; // > 0 only for weapon cards; used to pick the correct gun image
    }

    public static class WeaponSnap {
        public String name  = "";
        public int    range = 1;
    }

    public static class TargetSnap {
        public String name      = "";
        public int    playerIdx = 0;
    }
}
