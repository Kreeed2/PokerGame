import GameLogic.Player;
import GameLogic.Table;
import Network.DatabaseObject;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class Server {
    private static final Logger log = Logger.getGlobal();
    static int PORT = 25565;

    public static void main(String[] args) throws IOException {
        log.info("Starte Server");
        ServerSocket listener = new ServerSocket(PORT);
        Table table = new Table();
        List<DatabaseObject> database = new LinkedList<>();

        try {
            table.getCardStack().fillStack();
            //File textFile = new File("databse.dat");

            //while ()

            while(table.playerAmount() < 3) {
                table.addPlayer(new Player(listener.accept()));
            }

            while (table.allPlayersFinished()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            while (table.getRoundCounter() < 5) {
                table.nextTurn();
            }

        } finally {
            listener.close();
        }
    }
}
