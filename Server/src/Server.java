import GameLogic.Player;
import GameLogic.Table;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Logger;

public class Server {
    private static final Logger log = Logger.getGlobal();
    static int PORT = 25565;
    Table table = new Table();

    public static void main(String[] args) throws IOException {
        log.info("Starte Server");
        ServerSocket listener = new ServerSocket(PORT);

        try {
            while (true) {
                new Player(listener.accept());
            }
        } finally {
            listener.close();
        }
    }
}
