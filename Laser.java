import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
// import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

public class Laser 
{
    //int x, y, height, width, direction;
    
    private Rectangle2D.Double laser;
    private JPanel panel;
    
    private int chargeTime, fireTime, chargeDelay;
    private int time;

    private Image laserImages[];
    
    private BufferedImage buffer;
    private int alpha;

    private boolean playSound;
    
    public Laser(JPanel panel, int x, int y, int width, int height, int chargeTime, int fireTime)
    {
        this.panel = panel;
        this.laser = new Rectangle2D.Double (x, y, width, height);
        this. chargeTime = chargeTime;
        this.fireTime = fireTime;
        this.chargeDelay = 0;

        laserImages = new Image[5];
        laserImages[0] = ImageManager.loadImage("Images/Laser/Laser0.png");
        laserImages[1] = ImageManager.loadImage("Images/Laser/Laser1.png");
        laserImages[2] = ImageManager.loadImage("Images/Laser/Laser2.png");
        laserImages[3] = ImageManager.loadImage("Images/Laser/Laser3.png");
        laserImages[4] = ImageManager.loadImage("Images/Laser/Laser4.png");

        buffer = new BufferedImage(laserImages[4].getWidth(null), laserImages[4].getHeight(null), BufferedImage.TYPE_INT_ARGB);

        alpha = 255;

        time = -1;
        playSound = true;
    }
    
    
    public Laser(JPanel panel, int x, int y, int width, int height, int chargeTime, int fireTime, int chargeDelay)
    {
        this(panel, x, y, width, height, chargeTime, fireTime);
        this.chargeDelay = chargeDelay;
    }
    
    
    public void draw(Graphics2D g2)
    {
        time++;
        // Graphics g = panel.getGraphics();
        // Graphics2D g2 = (Graphics2D) g;

        if (time <= chargeTime) //laser is charging
        {
            // g2.setColor(new Color(178/4,34/4,34/4));            
            // g2.fill(laser);
            if (time <= chargeTime/4)
                g2.drawImage(laserImages[0], (int) laser.getX(), (int) laser.getY(), (int) laser.getWidth(), (int) laser.getWidth()/2, null);

            else if (time <= chargeTime/2)
                g2.drawImage(laserImages[1], (int) laser.getX(), (int) laser.getY(), (int) laser.getWidth(), (int) laser.getWidth()/2, null);

            else if (time <= chargeTime*3/4)
                g2.drawImage(laserImages[2], (int) laser.getX(), (int) laser.getY(), (int) laser.getWidth(), (int) laser.getWidth()/2, null);

            else
                g2.drawImage(laserImages[3], (int) laser.getX(), (int) laser.getY(), (int) laser.getWidth(), (int) laser.getWidth()/2, null);
        }
            
        else if (time <= chargeTime + fireTime) //laser is firing
        {
            // g2.setColor(new Color(178,34,34));
            // g2.fill(laser);
            // int y = (int) laser.getY();
            // int imageHeight = laserImages[4].getHeight(null) * 500;
            // System.err.println(imageHeight);
            // while (y <= (int) laser.getY() + (int) laser.getHeight()) //draw laser until it hits the ground
            // {
            //     if (y + imageHeight > (int) laser.getY() + (int) laser.getHeight())
            //         imageHeight = (int) laser.getHeight() - y;
                
            //     g2.drawImage(laserImages[4], (int) laser.getX(), (int) y, (int) laser.getWidth(), imageHeight, null);

            //     y += laserImages[4].getHeight(null);
            //     System.err.println(y);
            // }
            g2.drawImage(laserImages[4], (int) laser.getX(), (int) laser.getY(), (int) laser.getWidth(), (int) laser.getHeight(), null);

            if (playSound)
            {
                SoundManager.getInstance().playClip("laser", 0.6f);
                playSound = false;
            }
            
            
        }

        else if (time <= chargeTime + fireTime + chargeDelay) //laser is cooling down
        {
            g2.drawImage(disappear(5), (int) laser.getX(), (int) laser.getY(), (int) laser.getWidth(), (int) laser.getHeight(), null);
            playSound = true;
        }

        else if (time > chargeTime + fireTime + chargeDelay) //reset laser's time
        {
            time = -1;
            alpha = 255;
            // playSound = true;
        }
        
        // g.dispose();
    }
    
    
    public Rectangle2D.Double getBoundingBox()
    {
        return laser;
    }
    
    
    public void erase(Graphics2D g2)
    {
        // Graphics g = panel.getGraphics();
        // Graphics2D g2 = (Graphics2D) g;
        
        g2.setColor(panel.getBackground());
        g2.fill(laser);
        
        // g.dispose();
    }
    
    
    public boolean isLethal()
    {
        return (time > chargeTime && time <= (fireTime + chargeTime));
    }
    
    
    public void reset()
    {
        time = 0;
    }

    private BufferedImage disappear(int rate)
    {
        Graphics2D g2 = buffer.createGraphics();
        g2.drawImage(laserImages[4], 0, 0, null);
        g2.dispose();

        int imWidth = buffer.getWidth();
        int imHeight = buffer.getHeight();

        int [] pixels = new int[imWidth * imHeight];
        buffer.getRGB(0, 0, imWidth, imHeight, pixels, 0, imWidth);

        int red, green, blue, newValue;

        alpha -= 255/rate;
        if (alpha < 0)
            alpha = 0;

        for (int i=0; i<pixels.length; i++) {

            // a = (pixels[i] >> 24);
            red = (pixels[i] >> 16) & 255;
            green = (pixels[i] >> 8) & 255;
            blue = pixels[i] & 255;

            newValue = blue | (green << 8) | (red << 16) | (alpha << 24);
            pixels[i] = newValue;
        }

        buffer.setRGB(0, 0, imWidth, imHeight, pixels, 0, imWidth);

        return buffer;
    }


}