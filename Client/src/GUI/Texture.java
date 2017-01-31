package GUI;

import sun.awt.image.ToolkitImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
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

        imgFileName = "cardTexture/" + fileName;
        URL imgUrl = getClass().getClassLoader().getResource(imgFileName);


        if (imgUrl == null) {
            System.err.println("Couldn't find file: " + imgFileName);
            System.err.println(getClass().toString());
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

        if (img == null) {
            g2D.setColor(Color.CYAN);
            g2D.fillRect(0, 0, width - 5, height - 5);
        } else
            g2D.drawImage(img.getScaledInstance(width,height,Image.SCALE_SMOOTH),0,0,null);
    }
}
