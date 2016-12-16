import GameLogic.Player;
import GameLogic.Table;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Logger;

public class Server {
    private static final Logger log = Logger.getGlobal();
    static int PORT = 25565;

    public static void main(String[] args) throws IOException {
        log.info("Starte Server");
        ServerSocket listener = new ServerSocket(PORT);
        Table table = new Table();

        try {
            table.getCardStack().fillStack();

            while(table.playerAmount() < 4) {
                table.addPlayer(new Player(listener.accept()));
            }

            while (table.getRoundCounter() < 5) {
                table.nextTurn();
            }
        } finally {
            listener.close();
        }
    }
}
