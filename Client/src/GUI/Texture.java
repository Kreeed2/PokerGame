package GUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class Texture extends JPanel{

    private String imgFileName;
    private Image img;
    private int width, height;

    public Texture(String fileName, int width, int height) {
        super();
        this.width = width;
        this.height = height;

        this.setPreferredSize(new Dimension(width, height));

        imgFileName = "../cardTexture/" + fileName;
        URL imgUrl = getClass().getResource(imgFileName);

        if (imgUrl == null) {
            System.err.println("Couldn't find file: " + imgFileName);
        } else {
            try {
                img = ImageIO.read(imgUrl);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;

        g2D.drawImage(img.getScaledInstance(width,height,Image.SCALE_SMOOTH),0,0,null);
    }
}
