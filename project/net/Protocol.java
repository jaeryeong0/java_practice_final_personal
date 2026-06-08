package net;

import game.GameLogic.*;

import java.util.*;

/**
 * Newline-delimited JSON protocol.
 *
 * Server → Client  buildStateForPlayer() / buildLobby()
 * Client → Server  flat action objects, parsed by parseAction()
 * Client parses    parseStateUpdate()
 */
public class Protocol {

    // =========================================================================
    // SERVER → CLIENT  serialisation
    // =========================================================================

    /** Tailored state update: recipient sees their own hand; others see only handSize. */
    public static String buildStateForPlayer(Game game, int playerIdx) {
        StringBuilder sb = new StringBuilder("{");
        kStr(sb, "state",           game.getState().name(), true);
        kInt(sb, "currentPlayerIdx", game.getCurrentPlayerIdx());
        kInt(sb, "myPlayerIdx",      playerIdx);

        sb.append(",\"players\":[");
        for (int i = 0; i < game.players.size(); i++) {
            if (i > 0) sb.append(",");
            Player p = game.players.get(i);
            sb.append("{");
            kStr(sb, "name",     p.name, true);
            kInt(sb, "hp",       p.hp);
            kInt(sb, "maxHp",    p.maxHp);
            kStr(sb, "role",     p.role != null ? p.role.name() : "UNKNOWN");
            kStr(sb, "charName", p.character != null ? p.character.name : "");
            kInt(sb, "handSize", p.hand.size());
            // hand: full for recipient, empty for others
            sb.append(",\"hand\":").append(i == playerIdx ? cardsJson(p.hand) : "[]");
            sb.append(",\"field\":").append(cardsJson(p.field));
            sb.append(",\"weapon\":").append(weaponJson(p.weapon));
            sb.append(",\"alive\":").append(p.hp > 0);
            sb.append("}");
        }
        sb.append("]");

        sb.append(",\"targets\":[");
        List<Player> tc = game.getTargetCandidates();
        for (int i = 0; i < tc.size(); i++) {
            if (i > 0) sb.append(",");
            Player t = tc.get(i);
            sb.append("{\"name\":\"").append(esc(t.name))
              .append("\",\"idx\":").append(game.players.indexOf(t)).append("}");
        }
        sb.append("]");

        sb.append(",\"store\":").append(cardsJson(game.getGeneralStorePool()));
        kInt(sb, "storePickerIdx",    game.getGeneralStorePickerIdx());
        kInt(sb, "catBalouTargetIdx", game.getCatBalouTargetIdx());
        sb.append(",\"topDiscard\":").append(cardJson(game.getTopDiscard()));

        List<String> full = game.getLog();
        int from = Math.max(0, full.size() - 8);
        sb.append(",\"log\":[");
        for (int i = from; i < full.size(); i++) {
            if (i > from) sb.append(",");
            sb.append("\"").append(esc(full.get(i))).append("\"");
        }
        sb.append("]");
        sb.append("}");
        return sb.toString();
    }

    /** Lobby broadcast – includes myPlayerIdx so client knows who they are. */
    public static String buildLobby(List<String> names, int maxPlayers, int myPlayerIdx, List<String> chat) {
        StringBuilder sb = new StringBuilder("{");
        kStr(sb, "state",      "LOBBY", true);
        kInt(sb, "maxPlayers", maxPlayers);
        kInt(sb, "myPlayerIdx", myPlayerIdx);
        sb.append(",\"joined\":[");
        for (int i = 0; i < names.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(esc(names.get(i))).append("\"");
        }
        sb.append("]");
        sb.append(",\"chat\":[");
        for (int i = 0; i < chat.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(esc(chat.get(i))).append("\"");
        }
        sb.append("]}");
        return sb.toString();
    }

    // =========================================================================
    // CLIENT → SERVER  parse (flat one-level JSON)
    // =========================================================================

    public static Map<String, Object> parseAction(String json) {
        int[] p = {0};
        Object v = parseValue(json, p);
        if (v instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> m = (Map<String, Object>) v;
            return m;
        }
        return new LinkedHashMap<>();
    }

    // =========================================================================
    // SERVER → CLIENT  parse (nested JSON)
    // =========================================================================

    @SuppressWarnings("unchecked")
    public static ClientGameState parseStateUpdate(String json) {
        ClientGameState cs = new ClientGameState();
        try {
            int[] p = {0};
            Map<String, Object> root = (Map<String, Object>) parseValue(json, p);

            cs.gameState          = str(root, "state");
            cs.currentPlayerIdx   = num(root, "currentPlayerIdx");
            cs.myPlayerIdx        = num(root, "myPlayerIdx");
            cs.storePickerIdx     = num(root, "storePickerIdx");
            cs.catBalouTargetIdx  = num(root, "catBalouTargetIdx");

            if ("LOBBY".equals(cs.gameState)) {
                cs.maxPlayers = num(root, "maxPlayers");
                for (Object n : list(root, "joined"))
                    cs.lobbyNames.add(n != null ? n.toString() : "");
                for (Object c : list(root, "chat"))
                    cs.chatMessages.add(c != null ? c.toString() : "");
                return cs;
            }

            for (Object po : list(root, "players")) {
                Map<String, Object> pm = (Map<String, Object>) po;
                ClientGameState.PlayerSnap ps = new ClientGameState.PlayerSnap();
                ps.name     = str(pm, "name");
                ps.hp       = num(pm, "hp");
                ps.maxHp    = num(pm, "maxHp");
                ps.role     = str(pm, "role");
                ps.charName = str(pm, "charName");
                ps.handSize = num(pm, "handSize");
                ps.alive    = bool(pm, "alive");
                for (Object co : list(pm, "hand"))  ps.hand.add(cardSnap((Map<String,Object>) co));
                for (Object co : list(pm, "field")) ps.field.add(cardSnap((Map<String,Object>) co));
                Object wm = pm.get("weapon");
                if (wm instanceof Map) {
                    Map<String,Object> w = (Map<String,Object>) wm;
                    ps.weapon       = new ClientGameState.WeaponSnap();
                    ps.weapon.name  = str(w, "n");
                    ps.weapon.range = num(w, "r");
                }
                cs.players.add(ps);
            }

            for (Object to : list(root, "targets")) {
                Map<String, Object> tm = (Map<String, Object>) to;
                ClientGameState.TargetSnap t = new ClientGameState.TargetSnap();
                t.name      = str(tm, "name");
                t.playerIdx = num(tm, "idx");
                cs.targets.add(t);
            }

            for (Object co : list(root, "store"))
                cs.storePool.add(cardSnap((Map<String,Object>) co));

            Object td = root.get("topDiscard");
            if (td instanceof Map) {
                Map<String,Object> tdm = (Map<String,Object>) td;
                cs.topDiscard = cardSnap(tdm);
            }

            for (Object lo : list(root, "log"))
                cs.log.add(lo != null ? lo.toString() : "");

        } catch (Exception e) {
            System.err.println("[Protocol] parseStateUpdate error: " + e.getMessage());
        }
        return cs;
    }

    // =========================================================================
    // Private helpers – serialisation
    // =========================================================================

    private static void kStr(StringBuilder sb, String k, String v, boolean first) {
        if (!first) sb.append(",");
        sb.append("\"").append(k).append("\":\"").append(esc(v)).append("\"");
    }
    private static void kStr(StringBuilder sb, String k, String v) { kStr(sb, k, v, false); }
    private static void kInt(StringBuilder sb, String k, int v) {
        sb.append(",\"").append(k).append("\":").append(v);
    }

    private static String cardsJson(List<? extends Card> cards) {
        if (cards == null) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < cards.size(); i++) {
            if (i > 0) sb.append(",");
            Card c = cards.get(i);
            sb.append("{\"n\":\"").append(esc(c.name)).append("\"")
              .append(",\"s\":\"").append(c.suit.name()).append("\"")
              .append(",\"v\":").append(c.value)
              .append(",\"t\":\"").append(c.type.name()).append("\"");
            if (c instanceof WeaponCard) sb.append(",\"r\":").append(((WeaponCard) c).range);
            sb.append("}");
        }
        return sb.append("]").toString();
    }

    private static String cardJson(Card c) {
        if (c == null) return "null";
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"n\":\"").append(esc(c.name)).append("\"")
          .append(",\"s\":\"").append(c.suit.name()).append("\"")
          .append(",\"v\":").append(c.value)
          .append(",\"t\":\"").append(c.type.name()).append("\"");
        if (c instanceof WeaponCard) sb.append(",\"r\":").append(((WeaponCard) c).range);
        return sb.append("}").toString();
    }

    private static String weaponJson(WeaponCard w) {
        if (w == null) return "null";
        return "{\"n\":\"" + esc(w.name) + "\",\"r\":" + w.range + "}";
    }

    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", " ")
                .replace("\r", "");
    }

    // =========================================================================
    // Private helpers – deserialisation
    // =========================================================================

    private static ClientGameState.CardSnap cardSnap(Map<String, Object> m) {
        ClientGameState.CardSnap c = new ClientGameState.CardSnap();
        c.name  = str(m, "n");
        c.suit  = str(m, "s");
        c.value = num(m, "v");
        c.type  = str(m, "t");
        Object r = m.get("r");
        c.weaponRange = (r instanceof Number) ? ((Number) r).intValue() : -1;
        return c;
    }

    private static String str(Map<String,Object> m, String k) {
        Object v = m.get(k); return v == null ? "" : v.toString();
    }
    private static int num(Map<String,Object> m, String k) {
        Object v = m.get(k); return (v instanceof Number) ? ((Number) v).intValue() : 0;
    }
    private static boolean bool(Map<String,Object> m, String k) {
        return Boolean.TRUE.equals(m.get(k));
    }
    @SuppressWarnings("unchecked")
    private static List<Object> list(Map<String,Object> m, String k) {
        Object v = m.get(k);
        return (v instanceof List) ? (List<Object>) v : Collections.emptyList();
    }

    // =========================================================================
    // Recursive-descent JSON parser
    // =========================================================================

    private static Object parseValue(String s, int[] p) {
        skipWs(s, p);
        if (p[0] >= s.length()) return null;
        char c = s.charAt(p[0]);
        if (c == '{') return parseObj(s, p);
        if (c == '[') return parseArr(s, p);
        if (c == '"') return parseStr(s, p);
        if (s.startsWith("true",  p[0])) { p[0] += 4; return Boolean.TRUE; }
        if (s.startsWith("false", p[0])) { p[0] += 5; return Boolean.FALSE; }
        if (s.startsWith("null",  p[0])) { p[0] += 4; return null; }
        return parseNum(s, p);
    }

    private static Map<String, Object> parseObj(String s, int[] p) {
        Map<String, Object> m = new LinkedHashMap<>();
        p[0]++; // '{'
        skipWs(s, p);
        while (p[0] < s.length() && s.charAt(p[0]) != '}') {
            skipWs(s, p);
            if (p[0] >= s.length() || s.charAt(p[0]) == '}') break;
            if (s.charAt(p[0]) != '"') { p[0]++; continue; }
            String key = parseStr(s, p);
            skipWs(s, p);
            if (p[0] < s.length() && s.charAt(p[0]) == ':') p[0]++;
            m.put(key, parseValue(s, p));
            skipWs(s, p);
            if (p[0] < s.length() && s.charAt(p[0]) == ',') p[0]++;
        }
        if (p[0] < s.length()) p[0]++; // '}'
        return m;
    }

    private static List<Object> parseArr(String s, int[] p) {
        List<Object> list = new ArrayList<>();
        p[0]++; // '['
        skipWs(s, p);
        while (p[0] < s.length() && s.charAt(p[0]) != ']') {
            list.add(parseValue(s, p));
            skipWs(s, p);
            if (p[0] < s.length() && s.charAt(p[0]) == ',') p[0]++;
        }
        if (p[0] < s.length()) p[0]++; // ']'
        return list;
    }

    private static String parseStr(String s, int[] p) {
        p[0]++; // opening '"'
        StringBuilder sb = new StringBuilder();
        while (p[0] < s.length() && s.charAt(p[0]) != '"') {
            char c = s.charAt(p[0]);
            if (c == '\\' && p[0] + 1 < s.length()) {
                p[0]++;
                char e = s.charAt(p[0]);
                if      (e == '"')  sb.append('"');
                else if (e == '\\') sb.append('\\');
                else if (e == 'n')  sb.append('\n');
                else if (e == 't')  sb.append('\t');
                else                sb.append(e);
            } else sb.append(c);
            p[0]++;
        }
        if (p[0] < s.length()) p[0]++; // closing '"'
        return sb.toString();
    }

    private static Number parseNum(String s, int[] p) {
        int start = p[0];
        while (p[0] < s.length()) {
            char c = s.charAt(p[0]);
            if (c == ',' || c == '}' || c == ']' || Character.isWhitespace(c)) break;
            p[0]++;
        }
        String tok = s.substring(start, p[0]).trim();
        try   { return Integer.parseInt(tok); }
        catch (NumberFormatException ignored) {
            try   { return Double.parseDouble(tok); }
            catch (NumberFormatException e) { return 0; }
        }
    }

    private static void skipWs(String s, int[] p) {
        while (p[0] < s.length() && Character.isWhitespace(s.charAt(p[0]))) p[0]++;
    }
}
