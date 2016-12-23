package Network;

import GUI.FormMain;
import GameLogic.Stack;
import GameLogic.enums.Role;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HandlerClient extends Thread {

    private static final Logger log = Logger.getGlobal();
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket socket;

    FormMain main;
    Stack cards;
    Role role;

    public HandlerClient(FormMain main, String serverAddress, int port) throws IOException {
        this.main = main;
        socket = new Socket(serverAddress, port);

        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        try {
            while(true) {
                answerNetwork((Message) in.readObject());
            }
        } catch (ClassNotFoundException | IOException e) {
            Logger.getGlobal().log(Level.SEVERE, e.getMessage());
        } finally {
            close();
        }
    }

    public void answerNetwork(Message message) throws IOException {
        switch (message.getHeader()) {
            case "NAMEADD":
                sendData("NAME", main.dial.txt_name.getText());
                break;
            case "NAMEACCEPT":
               if ((boolean)message.getPayload())
                   main.textArea.append("Name wurde akzeptiert!\n");
               else {
                   main.textArea.append("Name wurde abgelehnt!\n");
                   System.exit(0);
               }
               break;
            case "REMOVE":
                //TODO make leave proper
                System.exit(0);
                break;
            case "PLAYERCHIPS":
                Map<String, Integer> playerChips = (Map<String, Integer>) message.getPayload();
                playerChips.forEach((s, integer) -> main.textArea.append("Der Spieler " + s + " besitzt " + integer + " Chips.\n"));
                break;
            case "POT":
                main.textArea.append("Der Pot liegt bei " + message.getPayload() + ".\n");
                break;
            case "ROLE":
                Map<String, Integer> playerRoles = (Map<String, Integer>) message.getPayload();
                role = Role.values()[playerRoles.get(main.dial.txt_name.getText())];
                playerRoles.forEach((s, integer) -> main.textArea.append("Der Spieler " + s + " ist " + Role.values()[integer] + ".\n"));
                break;
            case "HANDCARDS":
                cards = (Stack) message.getPayload();
                main.textAreaCards.setText(cards.toString());
                break;
            case "OPENCARDS":
                cards.addStack((Stack) message.getPayload());
                main.textAreaCards.setText(cards.toString());
                break;
            case "BET":
                Map<String, Integer> playerBets = (Map<String, Integer>) message.getPayload();
                playerBets.forEach((s, integer) -> main.textArea.append("Der Spieler " + s + " hat " + integer + " gesetzt.\n"));
                break;
            case "MESSAGE":
                main.textArea.append((String) message.getPayload() + "\n");
                break;
            case "BLINDS":
                if (role == Role.BIG)
                    main.textArea.append("Du hast den Blind (" + message.getPayload() + ") gesetzt\n");
                else if (role == Role.SMALL || role == Role.DEALERSPECIAL)
                    main.textArea.append("Du hast den SBlind (" + ((Integer) message.getPayload())/2 + ") gesetzt\n");
                else
                    main.textArea.append("Der Blind (" + message.getPayload() + ") wurde gesetzt\n");
                break;
            case "BETSET":
                main.textArea.append((String) message.getPayload());
                break;
            default:
                main.textArea.append("Fehlerhafte Message: " + message.getHeader());
                //sendData("UNKNOWN", null);
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
}