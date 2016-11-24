package GUI;

import javax.swing.*;

/**
 * Created by Tim on 21.11.2016.
 */
public class MainWindow {
    private JPanel panel1;
    private JButton button1;
    private JTextField textField1;

    public MainWindow() {
        JFrame frame = new JFrame("Poker Client");
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
