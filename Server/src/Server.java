import GameLogic.Player;
import GameLogic.Table;
import Network.DatabaseObject;

import java.io.*;
import java.net.ServerSocket;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class Server {
    private static final Logger log = Logger.getGlobal();
    private static int PORT = 25565;

    public static void main(String[] args) throws IOException {
        log.info("Starte Server");
        ServerSocket listener = new ServerSocket(PORT);

        try {
            Table table = new Table();
            table.getCardStack().fillStack();

            while(table.playerAmount() < 2) {
                table.addPlayer(new Player(listener.accept(), table));
            }

            while (table.allPlayersFinished()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            for (Player p : table.getPlayers()) {
                editTextFile(p.getName(), p.getPass());
            }

            while (table.getRoundCounter() < 5) {
                table.nextTurn();
            }
        } finally {
            listener.close();
        }
    }

    private static void editTextFile(String name, String pass) {
        if (namePassAccept(name + ":" + pass)) {
            PrintWriter pWriter = null;
            try {
                pWriter = new PrintWriter(new FileWriter("userdata.txt", true), true);
                pWriter.println(name + ":" + pass);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } finally {
                if (pWriter != null) {
                    pWriter.flush();
                    pWriter.close();
                }
            }
        }
    }

        private static boolean namePassAccept(String namePass) {
            BufferedReader in = null;
            try {
                in = new BufferedReader(new FileReader("userdata.txt"));
                String line;
                while ((line = in.readLine()) != null) {
                    if (namePass.equals(line)) {
                        return false;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (in != null){
                    try {
                    in.close();
                } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
            return true;
        }
}
