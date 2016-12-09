import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.logging.Logger;

public class Server {
    private static final Logger log = Logger.getLogger( Logger.GLOBAL_LOGGER_NAME );
    public static int PORT = 25565;
    public static Table table = new Table();
    static HashSet<ObjectOutputStream> writers = new HashSet<>();

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
