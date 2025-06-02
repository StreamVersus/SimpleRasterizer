package pr.stream.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Window extends JPanel {
    private final int WIDTH;
    private final int HEIGHT;
    private final double SCALE;
    public final BufferedImage image;

    public Window(int width, int height, double scale) {
        this.WIDTH = width;
        this.HEIGHT = height;
        this.SCALE = scale;

        setPreferredSize(new Dimension((int) (WIDTH * SCALE), (int) (HEIGHT * SCALE)));
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (SCALE != 1) {
            g.drawImage(image, 0, 0, (int) (WIDTH * SCALE), (int) (HEIGHT * SCALE), null);
        } else {
            g.drawImage(image, 0, 0, null);
        }
    }
}
