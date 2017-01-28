package Network;

import GUI.FormMain;
import GameLogic.Card;
import GameLogic.Stack;
import GameLogic.enums.Role;
import handChecker.PokerCard;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
        switch (message.getHeader()) {
            case "NAMEADD":
                /*Map<String, String> namePass = new HashMap<>();
                namePass.put(main.dial.txt_name.getText(), main.dial.txt_pass.getText());
                sendData("NAMEPASS", namePass);*/
                sendData("NAMEPASS", main.dial.txt_name.getText() + ":" + main.dial.txt_pass.getText());
                //sendData("NAME", main.dial.txt_name.getText());
                break;
            case "NAMEACCEPT":
               if ((boolean)message.getPayload()) {
                   //main.textArea.append("Name wurde akzeptiert!\n");

               } else {
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
                if ((boolean)message.getPayload());
                    //main.textArea.append("Passwort wurde akzeptiert!\n");
                else {
                    //main.textArea.append("Falsches Passwort!\n");
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
                close();
                System.exit(0);
                break;
            case "PLAYERCHIPS":
                Map<String, Integer> playerChips = (Map<String, Integer>) message.getPayload();

                String names[] = new String[playerChips.keySet().size()];
                playerChips.keySet().toArray(names);
                for (int i = 0; i < names.length; i++) {
                        ((JLabel) ((JPanel) main.panelOpponents.getComponents()[i]).getComponents()[0]).setText(names[i]);
                        ((JLabel) ((JPanel) ((JPanel) main.panelOpponents.getComponents()[i]).getComponents()[1]).getComponent(4)).setText(playerChips.get(names[i]).toString());
                }

                //COLOR OWN PANEL
                try {
                    findPlayerPanel(main.dial.txt_name.getText()).setBorder(BorderFactory.createLineBorder(Color.RED));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                break;
            case "POT":
                main.textArea.append("Der Pot liegt bei " + message.getPayload() + ".\n");
                break;
            case "ROLE":
                Map<String, Integer> playerRoles = (Map<String, Integer>) message.getPayload();

                String names1[] = new String[playerRoles.keySet().size()];
                playerRoles.keySet().toArray(names1);
                for (int i = 0; i < names1.length; i++) {
                    ((JLabel) ((JPanel) ((JPanel) main.panelOpponents.getComponents()[i]).getComponents()[1]).getComponent(5)).setText(Role.values()[playerRoles.get(names1[i])].name());
                }
                break;
            case "HANDCARDS":
                if (main.cards == null) {
                    main.cards = ((Stack) message.getPayload());
                    for (PokerCard card : main.cards.getCards()) {
                        main.panelHandcards.add(((Card) card).getTexture(125, 182));
                    }
                    main.panelHandcards.updateUI();
                }
                break;
            case "OPENCARDS":
                Stack localStack = (Stack) message.getPayload();
                main.openCards.addStack(localStack);
                for (PokerCard card : localStack.getCards()) {
                    main.panelOpencards.add(((Card) card).getTexture(125,182));
                }
                main.panelHandcards.updateUI();
                break;
            case "BET":
                Map<String, Integer> playerBets = (Map<String, Integer>) message.getPayload();

                playerBets.forEach((s, integer) -> {
                    try {
                        ((JLabel) ((JPanel) findPlayerPanel(s).getComponent(1)).getComponent(7)).setText(integer.toString());
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                });
                break;
            case "INROUND":
                Map<String, Boolean> playerInRound = (Map<String, Boolean>) message.getPayload();

                playerInRound.forEach((s, aBoolean) -> {
                    try {
                        ((JLabel) ((JPanel) findPlayerPanel(s).getComponent(1)).getComponent(6)).setText(aBoolean.toString());
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                });
                break;
            case "MESSAGE":
                main.textArea.append("MESSAGE: " + message.getPayload() + "\n");
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

    private JPanel findPlayerPanel(String name) throws Exception {
        Component[] components = main.panelOpponents.getComponents();
        for (Component component : components) {
            if (Objects.equals(((JLabel) ((JPanel) component).getComponent(0)).getText(), name))
                return ((JPanel) component);
        }
        throw new Exception("Element Not Found");
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