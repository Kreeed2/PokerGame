import Network.Message;

import java.io.*;
import java.net.Socket;

public class Client {

    ObjectInputStream in;
    ObjectOutputStream out;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Client client = new Client();
        client.run(args[0], Integer.parseInt(args[1]));
    }

    private void run(String serverAddress, int port) throws IOException, ClassNotFoundException {

        // Make connection and initialize streams
        Socket socket = new Socket(serverAddress, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        login();
        // Process all messages from server, according to the protocol.
        while (true) {
            in.readObject();
        }
    }

    private void login() throws IOException, ClassNotFoundException {
        while (true) {
            Message data = (Message) in.readObject();
            if (data != null && data.getHeader().equals("NAMEADD")) {
                sendData("NAME", getName());
            } else if (data != null && data.getHeader().equals("NAMEACCEPT") && (boolean) data.getPayload())
                break;
        }
    }

    private synchronized void sendData(String header, String payload) throws IOException {
        out.writeObject(new Message(header, payload));
        out.flush();
    }

    private String getName() {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);

        System.out.print("Name:");
        while (true) {
            try {
                return br.readLine();
            } catch (IOException e) {
            }
        }
    }
}
