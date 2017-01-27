package GUI;

import GameLogic.Stack;
import GameLogic.enums.Role;
import Network.HandlerClient;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class FormMain extends JFrame{
    private JPanel contentPanel;
    public JTextArea textArea;
    private JTextField txt_input;
    public JPanel panelGame;

    public DialogLogin dial = new DialogLogin();
    private HandlerClient handlerClient;

    public Stack cards;
    public Role role;

    public FormMain() {
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

    }

    protected void connectToServer() throws IOException {
        dial.showDialog();
        handlerClient = new HandlerClient(this, dial.txt_ip.getText(), 25565);
        handlerClient.start();
    }

    private void onEnterPressed() {
        if (txt_input.getText() != "")
            handlerClient.sendData("BETGIVEN", txt_input.getText());
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
