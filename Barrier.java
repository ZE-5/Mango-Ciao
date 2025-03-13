import java.awt.geom.Rectangle2D;
import java.awt.Color;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Vector;

public class Barrier extends Thread
{
    Rectangle2D.Double barrier;
    JPanel panel;
    
    public Barrier(JPanel panel, int x, int y, int width, int height)
    {
        this.panel = panel;
        barrier = new Rectangle2D.Double(x, y, width, height);
    }
    
    
    public void draw()
    {
        Graphics g = panel.getGraphics();
        Graphics2D g2 = (Graphics2D) g;
        
        g2.setColor(Color.DARK_GRAY);
        g2.fill(barrier);
        
        g.dispose();
    }
    
    
    public Rectangle2D.Double getBoundingBox()
    {
        return barrier;
    }
    
    
    public void erase()
    {
        Graphics g = panel.getGraphics();
        Graphics2D g2 = (Graphics2D) g;
        
        g2.setColor(panel.getBackground());
        g2.fill(barrier);
        
        g.dispose();
    }
}