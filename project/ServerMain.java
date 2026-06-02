import net.BangServer;

/**
 * Standalone server entry point.
 *
 * Usage:
 *   java ServerMain [port] [maxPlayers]
 *   java ServerMain 12345 4
 */
public class ServerMain {
    public static void main(String[] args) {
        int port       = args.length > 0 ? Integer.parseInt(args[0]) : 12345;
        int maxPlayers = args.length > 1 ? Integer.parseInt(args[1]) : 4;
        new BangServer(port, maxPlayers).start();
    }
}
