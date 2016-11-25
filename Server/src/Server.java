import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.UUID;
import java.util.logging.Logger;

public class Server {
    public static int PORT = 25565;

    static final HashSet<String> names = new HashSet<String>();
    static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();
    private static final Logger log = Logger.getLogger( Logger.GLOBAL_LOGGER_NAME );

    public static void main(String[] args) throws IOException {
        log.info("Starte Server");
        ServerSocket listener = new ServerSocket(PORT);
        try {
            while (true) {
                new Handler(listener.accept()).start();
            }
        } finally {
            listener.close();
        }
    }
}
