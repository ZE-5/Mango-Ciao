import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

// import java.awt.Color;
import javax.swing.JPanel;
// import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

public class Door //to go to next stage
{
    //int x, y, height, width, direction;
    
    private Rectangle2D.Double door;
    private JPanel panel;
    
    private Image doorFrameImage;
    private Image doorPortalImage;
    private boolean isVisible;

    private boolean playSound;


    private int alpha;
    private boolean brighten;
    private BufferedImage buffer;

    private int time;

    public Door(JPanel panel, int x, int y, int width, int height)
    {
        this.panel = panel;
        this.door = new Rectangle2D.Double (x, y, width, height);

        doorFrameImage = ImageManager.loadImage("Images/Door_Frame.png");
        doorPortalImage = ImageManager.loadImage("Images/Door_Portal.png");
        isVisible = false;
        playSound = true;
        
        alpha = 0;
        brighten = true;
        buffer = new BufferedImage(doorFrameImage.getWidth(null), doorPortalImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        time = 0;
    }


    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }
    
    
    public Door(JPanel panel, int x, int y)
    {
        this(panel, x, y, 40, 50);
    }
    
    
    public void draw(Graphics2D g2)
    {
        if (!isVisible)
            return;
        
        if (playSound)
        {
            playSound = false;
            SoundManager.getInstance().playClip("portal", 0.7f);
        }

        //g2.drawImage(doorFrameImage, (int) door.getX(), (int) door.getY(), (int) door.getWidth(), (int) door.getHeight(), null);
        g2.drawImage(doorGlow(), (int) door.getX(), (int) door.getY(), (int) door.getWidth(), (int) door.getHeight(), null);
        
        // Graphics g = panel.getGraphics();
        // Graphics2D g2 = (Graphics2D) g;
        /*
        g2.setColor(new Color(150, 84, 82));
        g2.fill(door);
        
        g2.setColor(new Color(60, 100, 200));
        int widthReduction = (int) door.getWidth()/6;
        int heightReduction = (int) door.getHeight()/6;
        g2.fill(new Rectangle2D.Double(door.getX() + widthReduction, door.getY() + heightReduction, 
                                       door.getWidth() - widthReduction * 2, door.getHeight() - heightReduction * 2));
        */
        // g.dispose();
    }


    private BufferedImage doorGlow()
    {
        int rate = 20;

        Graphics2D g2 = buffer.createGraphics();
        g2.drawImage(doorPortalImage, 0, 0, null);

        int imWidth = buffer.getWidth();
        int imHeight = buffer.getHeight();

        int [] pixels = new int[imWidth * imHeight];
        buffer.getRGB(0, 0, imWidth, imHeight, pixels, 0, imWidth);

        int red, green, blue, newValue;

        if (time != 1)
        {
            time++;
        }
        else if (brighten)
        {
            time = 0;
            alpha += 255/rate;
            if (alpha > 255)
            {
                alpha = 255;
                brighten = false;
            }
        }
        else
        {
            time = 0;
            alpha -= 255/rate;
            if (alpha <= 50)
            {
                alpha = 50;
                brighten = true;
            }
        }

        for (int i=0; i<pixels.length; i++) {

            // a = (pixels[i] >> 24);
            red = (pixels[i] >> 16) & 255;
            green = (pixels[i] >> 8) & 255;
            blue = pixels[i] & 255;

            newValue = blue | (green << 8) | (red << 16) | (alpha << 24);
            pixels[i] = newValue;
        }

        buffer.setRGB(0, 0, imWidth, imHeight, pixels, 0, imWidth);

        g2.drawImage(doorFrameImage, 0, 0, null);
        g2.dispose();

        return buffer;
    }
    
    
    public Rectangle2D.Double getBoundingRectangle()
    {
        return door;
    }
    
    
    public void erase(Graphics2D g2)
    {
        // Graphics g = panel.getGraphics();
        // Graphics2D g2 = (Graphics2D) g;
        
        g2.setColor(panel.getBackground());
        g2.fill(door);
        
        // g.dispose();
    }
}
