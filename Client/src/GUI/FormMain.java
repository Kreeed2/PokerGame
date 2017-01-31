package GUI;

import GameLogic.Stack;
import GameLogic.enums.Role;
import Network.HandlerClient;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Objects;

public class FormMain extends JFrame{
    private JPanel contentPanel;
    public JTextArea textArea;
    private JTextField txt_input;
    public JPanel panelGame;
    public JPanel panelHandcards;
    public JPanel panelOpencards;
    public JPanel panelOpponents;
    private JButton callButton;
    private JButton foldButton;
    private JButton sendButton;

    public DialogLogin dial = new DialogLogin();
    private HandlerClient handlerClient;

    public Stack cards = new Stack();
    public Stack openCards = new Stack();
    public Role role;
    public boolean betRequested = false;

    private FormMain() {
        super();
        setContentPane(contentPanel);
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setVisible(true);

        txt_input.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    onEnterPressed();
                }
            }
        });

        sendButton.addActionListener(e -> onEnterPressed());
        foldButton.addActionListener(e -> sendFold());
    }

    private void connectToServer() throws IOException {
        dial.showDialog();
        handlerClient = new HandlerClient(this, dial.txt_ip.getText(), 25565);
        handlerClient.start();
    }

    private void onEnterPressed() {
        if (betRequested) {
            if (!Objects.equals(txt_input.getText(), "")) {
                try {
                    Integer.parseInt(txt_input.getText());
                    handlerClient.sendData("BETGIVEN", txt_input.getText());
                    txt_input.setText("");
                    betRequested = false;
                } catch (Exception e) {
                    txt_input.setText("Das war keine Zahl!");
                }
            }
        }
    }

    private void sendFold() {
        if (betRequested) {
            handlerClient.sendData("BETGIVEN", "-1");
        }
    }

    private void sendCheck(){

    }

    public static void main(String[] args) {
        FormMain main = new FormMain();

        try {
            main.connectToServer();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(main,
                    "Kein Server an der angebenen Adresse gefunden.",
                    "Verbindungsfehler",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}
