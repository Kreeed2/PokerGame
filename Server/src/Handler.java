import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

class Handler extends Thread {

    private String name;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private static final Logger log = Logger.getLogger( Logger.GLOBAL_LOGGER_NAME );

    public Handler(Socket accept) {
        socket = accept;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            log.info("Adding Player");

            while (true) {
                out.println("/SUBMITNAME");
                name = in.readLine();
                if (name == null) {
                    return;
                }
                synchronized (Server.names) {
                    if (!Server.names.contains(name)) {
                        Server.names.add(name);
                        break;
                    }
                }
                log.log(Level.WARNING, "Player submitted invalid name");
            }

            out.println("/NAMEACCEPTED");
            Server.writers.add(out);

        }
        catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
        finally {

            log.info("Removing Player " + name);

            if (name != null) {
                Server.names.remove(name);
            }
            if (out != null) {
                Server.writers.remove(out);
            }
            try {
                socket.close();
            } catch (IOException e) {
                log.log(Level.SEVERE, e.getMessage());
            }
        }
    }
}
