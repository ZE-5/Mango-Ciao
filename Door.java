import java.awt.geom.Rectangle2D;
import java.awt.Color;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Vector;

public class Door //to go to next stage
{
    //int x, y, height, width, direction;
    
    Rectangle2D.Double door;
    JPanel panel;
    
    public Door(JPanel panel, int x, int y, int width, int height)
    {
        this.panel = panel;
        this.door = new Rectangle2D.Double (x, y, width, height);
    }
    
    
    public Door(JPanel panel, int x, int y)
    {
        this(panel, x, y, 40, 50);
    }
    
    
    public void draw()
    {
        Graphics g = panel.getGraphics();
        Graphics2D g2 = (Graphics2D) g;
        
        g2.setColor(new Color(150, 84, 82));
        g2.fill(door);
        
        g2.setColor(new Color(60, 100, 200));
        int widthReduction = (int) door.getWidth()/6;
        int heightReduction = (int) door.getHeight()/6;
        g2.fill(new Rectangle2D.Double(door.getX() + widthReduction, door.getY() + heightReduction, 
                                       door.getWidth() - widthReduction * 2, door.getHeight() - heightReduction * 2));
        
        g.dispose();
    }
    
    
    public Rectangle2D.Double getBoundingRectangle()
    {
        return door;
    }
    
    
    public void erase()
    {
        Graphics g = panel.getGraphics();
        Graphics2D g2 = (Graphics2D) g;
        
        g2.setColor(panel.getBackground());
        g2.fill(door);
        
        g.dispose();
    }
}
