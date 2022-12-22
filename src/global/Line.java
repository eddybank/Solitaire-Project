package global;

import java.awt.Graphics;

import javax.swing.JPanel;

public class Line extends JPanel{



    public void drawLine(int x1, int y1, int x2, int y2, Graphics g) {

        double m = (y2 - y1) / (double)(x2-x1);
        double y = y1;
        for (int x =x1; x < x2; x++) {

            drawPoint(x,(int)y,g);
            y +=m;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        drawLine(20, 10, 300, 700, g); //has spaces between the dots 
        g.drawLine(20, 10, 300, 700); //this is perfect


    }

    private void drawPoint(int x, int y, Graphics g) {

        g.drawLine(x, y, x, y);

    }
} 
