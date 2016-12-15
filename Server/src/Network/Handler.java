package Network;

import GameLogic.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Handler extends Thread {

    private static final Logger log = Logger.getGlobal();
    private static ObjectOutputStream out;
    private static ObjectInputStream in;
    private Socket socket;
    private Player player;

    public Handler(Socket socket, Player player) {
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
                    player.setName((String) message.getPayload());
                    sendData("NAMEACCEPT", true);
                    log.info("Name geändert");
                } else {
                    sendData("NAMEACCEPT", false);
                }
                break;
            default:
                sendData("UNKNOWN", null);
                break;
        }
    }

    public synchronized boolean sendData(String header, Object payload) {
        try {
            out.writeObject(new Message(header, payload));
            return true;
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
            return false;
        }
    }

    public ObjectOutputStream getOutputStream() {
        return out;
    }

    public void close() {
        try {
            log.info("Socket geschlossen");
            socket.close();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
}