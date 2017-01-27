package GUI;

import javax.swing.*;
import java.awt.event.*;
import java.util.Random;

public class DialogLogin extends JDialog {
    private boolean ready = false;
    private JPanel contentPane;
    private JButton connectButton;
    public JTextField txt_ip;
    public JTextField txt_name;
    public JTextField txt_pass;

    public DialogLogin() {
        setContentPane(contentPane);
        getRootPane().setDefaultButton(connectButton);
        setModal(true);
        pack();

        connectButton.addActionListener(e -> onOK());
    }

     public void showDialog() {
        Random rnd = new Random();
        txt_name.setText(String.valueOf(rnd.nextInt()));
        txt_pass.setText(String.valueOf(rnd.nextInt()));
        setVisible(true);
    }

    public void showDialog(String name, String pass) {
        //Random rnd = new Random();
        txt_name.setText(name);
        txt_pass.setText(pass);
        setVisible(true);
    }

    private void onOK() {
        if (!txt_ip.getText().isEmpty() && !txt_name.getText().isEmpty() && !txt_pass.getText().isEmpty()) {
            setVisible(false);
            dispose();
            ready = true;
        }
    }

    public boolean isReady() {
        return ready;
    }
}
