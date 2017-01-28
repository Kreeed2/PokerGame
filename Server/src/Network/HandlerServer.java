package Network;

import GameLogic.Player;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HandlerServer extends Thread {

    private static final Logger log = Logger.getGlobal();
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket socket;
    private Player player;

    public Object betGiven = null;

    public HandlerServer(Socket socket, Player player) {
        this.socket = socket;
        this.player = player;

        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            while(true) {
                answerNetwork((Message) in.readObject());
            }
        } catch (ClassNotFoundException | IOException e) {

            Logger.getGlobal().log(Level.SEVERE, e.getMessage());
        }
    }

    public void answerNetwork(Message message) throws IOException {
        switch (message.getHeader()) {
            case "NAME":
                if (message.getPayload() instanceof String && message.getPayload() != "") {
                    sendData("NAMEACCEPT", true);
                    player.setName((String) message.getPayload());
                    log.info("Name geändert zu " + message.getPayload());
                }
                break;
            case "NAMEPASS":
                if (message.getPayload() instanceof String && message.getPayload() != "") {
                    String namePass = (String) message.getPayload();
                    String splitString[] = namePass.split(":");
                    String name = splitString[0];
                    String pass = splitString[1];
                    if (nameAccepted(name)) {
                        sendData("NAMEACCEPT", true);
                        if (passAccepted(pass, name)){
                            sendData("PASSACCEPT", true);
                            player.setNamePass(name, pass);
                            log.info("Name geändert zu " + name);
                        } else {
                            sendData("PASSACCEPT", false);
                        }
                    }  else {
                        sendData("NAMEACCEPT", false);
                        //sendData("PASSACCEPT", true);
                    }
                } else {
                    sendData("NAMEACCEPT", false);
                }
                break;
            case "BETGIVEN":
                    betGiven = message.getPayload();
                break;
            default:
                sendData("UNKNOWN", null);
                break;
        }
    }

    public boolean sendData(String header, Object payload) {
        try {
            out.writeObject(new Message(header, payload));
            out.flush();
            return true;
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
            return false;
        }
    }

    public void close() {
        try {
            log.info("Socket geschlossen");
            socket.close();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    public boolean nameAccepted(String name) {
        if (name != "") {
            for (Player p : player.getTable().getPlayers()) {
                System.out.println(p.getName());
                if (name.equals(p.getName())) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

    public boolean passAccepted(String pass, String name) {
        boolean rturn = true;
        if (pass != "") {
            File file = new File("Userdata.txt");

            if (!file.canRead() || !file.isFile())
                System.exit(0);

            BufferedReader in = null;
            try {
                in = new BufferedReader(new FileReader("Userdata.txt"));
                String line = null;
                while ((line = in.readLine()) != null) {
                    String splitString[] = line.split(":");
                    if (name.equals(splitString[0])) {
                        if (!pass.equals(splitString[1])) {
                            rturn = false;
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (in != null)
                    try {
                        in.close();
                    } catch (IOException e) {
                    }
            }
        } else {
            rturn = false;
        }
        return rturn;
    }
}