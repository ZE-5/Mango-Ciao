import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.Color;


public class HeartPanel extends JPanel
{
    public void draw(int hearts)
    {
        Graphics g = this.getGraphics();
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.GREEN);
        
        for (int x = 0; x != hearts; x++)
        {   
            //x * num is the offset for the hearts; alter the number to change the offset
            g2.fill(new Rectangle2D.Double((x * 80) + 10, 28, 50, 14));
            g2.fill(new Rectangle2D.Double((x * 80) + 28, 10, 14, 50));   
        }
        
        g.dispose();
    }
    
    
    public void erase()
    {
        Graphics g = this.getGraphics();
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(this.getBackground());
        
        g2.fill(new Rectangle2D.Double(0, 0, this.getSize().width, this.getSize().height));
        
        g.dispose();
    }
}