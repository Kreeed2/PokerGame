package GUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Texture extends JPanel{

    private void doDrawing(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        rh.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        //g2d.setRenderingHints(rh);

        try {
            System.out.print("Bild");
            BufferedImage bufferedImage = ImageIO.read(getClass().getResource("../cardTexture/ASS_CLUBS.png"));
            g2d.setPaint(new TexturePaint(bufferedImage, new Rectangle(0,0,100, 120)));
            //System.out.println( getClass().getResource("../cardTexture/ASS_CLUBS.png") );
        } catch (IOException e) {
            e.printStackTrace();
        }
        //g2d.dispose();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }
}
