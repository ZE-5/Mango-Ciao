import java.awt.geom.Ellipse2D;
// import java.awt.Color;
import javax.swing.JPanel;
// import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

public class Coin 
{
    //int x, y, height, width, direction;
    
    private Ellipse2D.Double coin;
    private JPanel panel;
    
    private Image coinImage;

    public Coin(JPanel panel, int x, int y)
    {
        this.panel = panel;
        this.coin = new Ellipse2D.Double (x, y, 20, 20);

        coinImage = ImageManager.loadImage("Images/Coin.png");
    }
    
    
    public void draw(Graphics2D g2)
    {
        // Graphics g = panel.getGraphics();
        // Graphics2D g2 = (Graphics2D) g;
        
        g2.drawImage(coinImage, (int) coin.getX(), (int) coin.getY(), (int) coin.getWidth(), (int) coin.getHeight(), null);

        /* 
        g2.setColor(Color.YELLOW);
        g2.fill(coin);
        
        //draw centre of coin
        g2.setColor(panel.getBackground());
        Ellipse2D.Double centre = new Ellipse2D.Double(coin.getX() + 4, coin.getY() + 4, 12, 12);
        g2.fill(centre);
        */
        //g.dispose();
    }
    
    
    public Ellipse2D.Double getBoundingEllipse()
    {
        return coin;
    }
    
    
    public void erase(Graphics2D g2)
    {
        // Graphics g = panel.getGraphics();
        // Graphics2D g2 = (Graphics2D) g;
        
        g2.setColor(panel.getBackground());
        g2.fill(coin);
        
        //g.dispose();
    }
}
