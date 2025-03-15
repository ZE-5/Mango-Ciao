import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
// import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.Color;
import java.awt.Font;


public class CoinPanel extends JPanel
{
    private Image coinImage;

    CoinPanel()
    {
        coinImage = ImageManager.loadImage("Images/Coin.png");
    }

    
    public void draw(int coins)
    {
        Graphics g = this.getGraphics();
        Graphics2D g2 = (Graphics2D) g;
        
        g2.drawImage(coinImage, 185, 10, 50, 50, null);

        /* 
        g2.setColor(Color.YELLOW);
        g2.fill(new Ellipse2D.Double(185 + 30, 10, 50, 50));
        
        g2.setColor(this.getBackground());
        g2.fill(new Ellipse2D.Double(185 + 30 + 6, 10 + 6, 25 + 13, 25 + 13));
        */
        

        //text
        g2.setColor(Color.YELLOW);
        
        Font f = new Font ("Bookman Old Style", Font.BOLD, 40);
        g2.setFont (f);
        g2.drawString("x" + coins, 275 - 25, 50);
        
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