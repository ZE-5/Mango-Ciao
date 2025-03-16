import java.awt.geom.Rectangle2D;
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
    
    private Image doorImage;
    private boolean isVisible;

    private boolean playSound;

    public Door(JPanel panel, int x, int y, int width, int height)
    {
        this.panel = panel;
        this.door = new Rectangle2D.Double (x, y, width, height);

        doorImage = ImageManager.loadImage("Images/Door.png");
        isVisible = false;
        playSound = true;
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

        g2.drawImage(doorImage, (int) door.getX(), (int) door.getY(), (int) door.getWidth(), (int) door.getHeight(), null);
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
