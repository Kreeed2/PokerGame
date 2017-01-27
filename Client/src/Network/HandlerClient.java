package Network;

import GUI.FormMain;
import GameLogic.Card;
import GameLogic.Stack;
import GameLogic.enums.Role;
import handChecker.PokerCard;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HandlerClient extends Thread {

    private static final Logger log = Logger.getGlobal();
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket socket;

    FormMain main;


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
        main.textArea.setText("");
        switch (message.getHeader()) {
            case "NAMEADD":
                /*Map<String, String> namePass = new HashMap<>();
                namePass.put(main.dial.txt_name.getText(), main.dial.txt_pass.getText());
                sendData("NAMEPASS", namePass);*/
                sendData("NAMEPASS", main.dial.txt_name.getText() + ":" + main.dial.txt_pass.getText());
                //sendData("NAME", main.dial.txt_name.getText());
                break;
            case "NAMEACCEPT":
               if ((boolean)message.getPayload())
                   main.textArea.append("Name wurde akzeptiert!\n");
               else {
                   //main.textArea.append("Name wurde abgelehnt!\n");
                   main.dial.showDialog("Name bereits vergeben", "");
                   while (!main.dial.isReady())
                       try {
                           currentThread().sleep(1000);
                       } catch (InterruptedException e) {
                           e.printStackTrace();
                       }
                   sendData("NAMEPASS", main.dial.txt_name.getText() + ":" + main.dial.txt_pass.getText());
                   //System.exit(0);
               }
               break;
            case "PASSACCEPT":
                if ((boolean)message.getPayload())
                    main.textArea.append("Passwort wurde akzeptiert!\n");
                else {
                    main.textArea.append("Falsches Passwort!\n");
                    main.dial.showDialog("", "falsches Passwort");
                    while (!main.dial.isReady())
                        try {
                            currentThread().sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    sendData("NAMEPASS", main.dial.txt_name.getText() + ":" + main.dial.txt_pass.getText());
                    //System.exit(0);
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
                main.role = Role.values()[playerRoles.get(main.dial.txt_name.getText())];
                playerRoles.forEach((s, integer) -> main.textArea.append("Der Spieler " + s + " ist " + Role.values()[integer] + ".\n"));
                break;
            case "HANDCARDS":
                copyStacks(((Stack) message.getPayload()));
                for (PokerCard card : main.cards.getCards()) {
                    main.panelHandcards.add(((Card) card).getTexture(125,182));
                }
                main.panelHandcards.updateUI();
                //main.textAreaCards.setText(cards.toString());
                break;
            case "OPENCARDS":
                main.openCards.addStack((Stack) message.getPayload());
                for (PokerCard card : main.openCards.getCards()) {
                    main.panelOpencards.add(((Card) card).getTexture(125,182));
                }
                //main.textAreaCards.setText(cards.toString());
                break;
            case "BET":
                Map<String, Integer> playerBets = (Map<String, Integer>) message.getPayload();
                playerBets.forEach((s, integer) -> main.textArea.append("Der Spieler " + s + " hat " + integer + " gesetzt.\n"));
                break;
            case "MESSAGE":
                main.textArea.append((String) message.getPayload() + "\n");
                break;
            case "BLINDS":
                if (main.role == Role.BIG)
                    main.textArea.append("Du hast den Blind (" + message.getPayload() + ") gesetzt\n");
                else if (main.role == Role.SMALL || main.role == Role.DEALERSPECIAL)
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

    private void copyStacks(Stack server) {
        main.cards = new Stack();
        for (PokerCard card : server.getCards()) {
            main.cards.add(new Card(card.getValue(), card.getColor()));
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