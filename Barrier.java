import java.awt.geom.Rectangle2D;
import java.awt.Color;
import javax.swing.JPanel;
// import java.awt.Graphics;
import java.awt.Graphics2D;

public class Barrier extends Thread
{
    Rectangle2D.Double barrier;
    JPanel panel;
    Color colour;
    
    public Barrier(JPanel panel, int x, int y, int width, int height)
    {
        this.panel = panel;
        barrier = new Rectangle2D.Double(x, y, width, height);
        colour = Color.GRAY;
    }


    public Barrier(JPanel panel, int x, int y, int width, int height, Color color)
    {
        this(panel, x, y, width, height);
        this.colour = color;
    }
    
    
    public int[] getPosition()
    {
        int[] pos = {(int) barrier.getX(), (int) barrier.getY()};
        return pos;
    }


    public int getX()
    {
        return (int) barrier.getX();
    }


    public int getY()
    {
        return (int) barrier.getY();
    }

    
    public int getWidth()
    {
        return (int) barrier.getWidth();
    }

    
    public int getHeight()
    {
        return (int) barrier.getHeight();
    }

    
    public void draw(Graphics2D g2)
    {
        // Graphics g = panel.getGraphics();
        // Graphics2D g2 = (Graphics2D) g;
        
        g2.setColor(colour);
        g2.fill(barrier);
        
        //g.dispose();
    }
    
    
    public Rectangle2D.Double getBoundingBox()
    {
        return barrier;
    }
    
    
    public void erase(Graphics2D g2)
    {
        // Graphics g = panel.getGraphics();
        // Graphics2D g2 = (Graphics2D) g;
        
        g2.setColor(panel.getBackground());
        g2.fill(barrier);
        
        // g.dispose();
    }
}