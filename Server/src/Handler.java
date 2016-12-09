import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Handler extends Thread {

    private static final Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private Socket socket;
    private boolean playerConstructed = false;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public Handler(Socket accept) {
        log.info("Handling connection");
        socket = accept;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            login();
            while(true) {
                in.readObject();
            }
        } catch (ClassNotFoundException | IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        } finally {
            close();
        }
    }

    public synchronized void sendData(String header, String payload) throws IOException {
        out.writeObject(new Message(header, payload));
        out.flush();
    }


    private void login() throws IOException, ClassNotFoundException {
        log.info("Adding Player");
        while (true) {
            sendData("ADDPLAYER", null);
            Message data = (Message) in.readObject();
            if (data != null && data.getHeader().equals("NAME")) {
                Server.table.addPlayer(new Player(this, data.getPayload().toString()));
                playerConstructed = true;
                break;
            }
            log.log(Level.WARNING, "Player submitted invalid name");
        }
        sendData("NAMEACCEPTED", null);
        Server.writers.add(out);
    }


    private void close() {
        if (playerConstructed)
            Server.table.removePlayer();
        if (out != null)
            Server.writers.remove(out);
        try {
            socket.close();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
}
