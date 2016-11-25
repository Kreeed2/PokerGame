import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    BufferedReader in;
    PrintWriter out;

    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.run(args[0], Integer.parseInt(args[1]));
    }

    private void run(String serverAdress, int port) throws IOException {

        // Make connection and initialize streams
        String serverAddress = serverAdress;
        Socket socket = new Socket(serverAddress, port);
        in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Process all messages from server, according to the protocol.
        while (true) {
            String line = in.readLine();
            if (line.startsWith("/SUBMITNAME")) {
                out.println(getName());
            } else if (line.startsWith("NAMEACCEPTED")) {
                System.out.print("Name akzeptiert");
            }
        }
    }

    private String getName() {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);

        while (true) {
            try {
                return br.readLine();
            } catch (IOException e) {
            }
        }
    }
}
