package Network;

import GUI.FormMain;
import GameLogic.Card;
import GameLogic.enums.Role;
import handChecker.PokerCard;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HandlerClient extends Thread {

    private static final Logger log = Logger.getGlobal();
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket socket;

    private FormMain main;


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
                sendData("NAMEPASS", main.dial.txt_name.getText() + ":" + main.dial.txt_pass.getText());
                break;
            case "NAMEACCEPT":
               if ((boolean)message.getPayload()) {

               } else {
                   main.dial.showDialog("Name bereits vergeben", "");
                   while (!main.dial.isReady())
                       try {
                           currentThread().sleep(1000);
                       } catch (InterruptedException e) {
                           e.printStackTrace();
                       }
                   sendData("NAMEPASS", main.dial.txt_name.getText() + ":" + main.dial.txt_pass.getText());
               }
               break;
            case "PASSACCEPT":
                if ((boolean)message.getPayload());
                else {
                    main.dial.showDialog("", "falsches Passwort");
                    while (!main.dial.isReady())
                        try {
                            currentThread().sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    sendData("NAMEPASS", main.dial.txt_name.getText() + ":" + main.dial.txt_pass.getText());
                }
                break;
            case "REMOVE":
                close();
                System.exit(0);
                break;
            case "PLAYERCHIPS":
                Map<String, Integer> playerChips = (Map<String, Integer>) message.getPayload();

                String names[] = new String[playerChips.keySet().size()];
                playerChips.keySet().toArray(names);
                for (int i = 0; i < names.length; i++) {

                    if (main.panelOpponents.getComponents().length < names.length)
                        main.panelOpponents.add(createPlayerPanel(names[i]));
                    main.panelOpponents.revalidate();

                    ((JLabel) ((JPanel) main.panelOpponents.getComponent(i)).getComponent(1)).setText(playerChips.get(names[i]).toString());
                }
                main.panelOpponents.updateUI();
                break;
            case "POT":
                main.textArea.append("Der Pot liegt bei " + message.getPayload() + ".\n");
                break;
            case "ROLE":
                Map<String, Integer> playerRoles = (Map<String, Integer>) message.getPayload();

                playerRoles.forEach((s, integer) -> {
                    try {
                        ((JLabel) findPlayerPanel(s).getComponent(7)).setText(Role.values()[integer].name());
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                });
                main.panelOpponents.updateUI();
                break;
            case "HANDCARDS":
                    main.cards.add(((PokerCard) message.getPayload()));
                    main.panelHandcards.add(((Card) message.getPayload()).getTexture(125, 182));
                    main.panelHandcards.updateUI();
                break;
            case "OPENCARDS":
                PokerCard localCard = (PokerCard) message.getPayload();
                main.openCards.add(localCard);
                main.panelOpencards.add(((Card) localCard).getTexture(125,182));
                main.panelHandcards.updateUI();
                break;
            case "BET":
                Map<String, Integer> playerBets = (Map<String, Integer>) message.getPayload();

                playerBets.forEach((s, integer) -> {
                    try {
                        ((JLabel) findPlayerPanel(s).getComponent(3)).setText(integer.toString());
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                });
                break;
            case "INROUND":
                Map<String, Boolean> playerInRound = (Map<String, Boolean>) message.getPayload();

                playerInRound.forEach((s, aBoolean) -> {
                    try {
                        ((JLabel) findPlayerPanel(s).getComponent(5)).setText(aBoolean.toString());
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
                JOptionPane.showMessageDialog(main, "Du bist dran", "Zug erforderlich", JOptionPane.INFORMATION_MESSAGE);
                main.betRequested = true;
                break;
            case "NEXTROUND":
                main.cards.clearCards();
                main.openCards.clearCards();
                main.panelOpencards.removeAll();
                main.panelHandcards.removeAll();

                break;
            default:
                main.textArea.append("Fehlerhafte Message: " + message.getHeader());
                break;
        }
    }

    private JPanel createPlayerPanel(String name) {
        JPanel out = new JPanel(new GridLayout(4,2));
        Color color = (Objects.equals(name, main.dial.txt_name.getText())) ? Color.RED : Color.gray;

        out.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(color),name));
        out.setPreferredSize(new Dimension(300,100));

        out.add(new JLabel("Chips:"));
        out.add(new JLabel("1"));
        out.add(new JLabel("Bet:"));
        out.add(new JLabel("1"));
        out.add(new JLabel("InRound:"));
        out.add(new JLabel("1"));
        out.add(new JLabel("Role:"));
        out.add(new JLabel("1"));

        return out;
    }

    private JPanel findPlayerPanel(String name) throws Exception {
        Component[] components = main.panelOpponents.getComponents();
        for (Component component : components) {
            if (Objects.equals(((TitledBorder) ((JPanel) component).getBorder()).getTitle(), name))
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