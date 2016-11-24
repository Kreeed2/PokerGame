import GUI.LoginDialog;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class PokerClient {

    private Socket socket = null;
    private DataInputStream console = null;
    private DataOutputStream streamOut = null;

    public static void main(String[] args) {
        /*
        PokerClient client = null;
        if (args.length != 2)
            System.out.println("Usage: java PokerClient host port");
        else
            client = new PokerClient(args[0], Integer.parseInt(args[1]));
            */
        String test = LoginDialog.showForm();
        String test1 = test.substring(test.lastIndexOf(':'));
    }

    /*
    public PokerClient(String serverName, int serverPort)
    {  System.out.println("Establishing connection. Please wait ...");
        try
        {  socket = new Socket(serverName, serverPort);
            System.out.println("Connected: " + socket);
            start();
        }
        catch(UnknownHostException uhe)
        {  System.out.println("Host unknown: " + uhe.getMessage());
        }
        catch(IOException ioe)
        {  System.out.println("Unexpected exception: " + ioe.getMessage());
        }
        String line = "";
        while (!line.equals(".bye"))
        {  try
        {  line = console.readLine();
            streamOut.writeUTF(line);
            streamOut.flush();
        }
        catch(IOException ioe)
        {  System.out.println("Sending error: " + ioe.getMessage());
        }
        }
    }
    public void start() throws IOException
    {  console   = new DataInputStream(System.in);
        streamOut = new DataOutputStream(socket.getOutputStream());
    }
    public void stop()
    {  try
    {  if (console   != null)  console.close();
        if (streamOut != null)  streamOut.close();
        if (socket    != null)  socket.close();
    }
    catch(IOException ioe)
    {  System.out.println("Error closing ...");
    }
    }
    */
}
