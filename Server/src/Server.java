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
    static int PORT = 25565;

    public static void main(String[] args) throws IOException {
        log.info("Starte Server");
        ServerSocket listener = new ServerSocket(PORT);
        Table table = new Table();
        List<DatabaseObject>  database = new LinkedList<>();

        try {
            table.getCardStack().fillStack();
            //File textFile = new File("databse.dat");

            //while ()

            while(table.playerAmount() < 4) {
                //table.addPlayer(new Player(listener.accept()));
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

    static public void editTextFile(String name, String pass) {
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

        static public boolean namePassAccept(String namePass) {
            File file = new File("Userdata.txt");
                if (!file.canRead() || !file.isFile())
                    System.exit(0);
                BufferedReader in = null;
            try {
                in = new BufferedReader(new FileReader("Userdata.txt"));
                String line = null;
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
                }
                }
            }
            return true;
        }
}
