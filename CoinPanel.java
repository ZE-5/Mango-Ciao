import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.Color;
import java.awt.Font;


public class CoinPanel extends JPanel
{
    public void draw(int coins)
    {
        Graphics g = this.getGraphics();
        Graphics2D g2 = (Graphics2D) g;
        
        g2.setColor(Color.YELLOW);
        g2.fill(new Ellipse2D.Double(185 + 30, 10, 50, 50));
        
        g2.setColor(this.getBackground());
        g2.fill(new Ellipse2D.Double(185 + 30 + 6, 10 + 6, 25 + 13, 25 + 13));
        
        g2.setColor(Color.YELLOW);
        
        Font f = new Font ("Bookman Old Style", Font.BOLD, 40);
        g2.setFont (f);
        g2.drawString("x" + coins, 275, 50);
        
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