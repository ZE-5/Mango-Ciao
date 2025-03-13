import java.awt.geom.Rectangle2D;
import java.awt.Color;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Vector;

public class Laser 
{
    //int x, y, height, width, direction;
    
    Rectangle2D.Double laser;
    JPanel panel;
    
    int chargeTime, fireTime, chargeDelay;
    int time;
    
    public Laser(JPanel panel, int x, int y, int width, int height, int chargeTime, int fireTime)
    {
        this.panel = panel;
        this.laser = new Rectangle2D.Double (x, y, width, height);
        this. chargeTime = chargeTime;
        this.fireTime = fireTime;
        this.chargeDelay = 0;
    }
    
    
    public Laser(JPanel panel, int x, int y, int width, int height, int chargeTime, int fireTime, int chargeDelay)
    {
        this.panel = panel;
        this.laser = new Rectangle2D.Double (x, y, width, height);
        this. chargeTime = chargeTime;
        this.fireTime = fireTime;this.chargeDelay = chargeDelay;
        
        time = 0;
    }
    
    
    public void draw()
    {
        Graphics g = panel.getGraphics();
        Graphics2D g2 = (Graphics2D) g;
        
        if (time <= chargeTime)
            g2.setColor(new Color(178/4,34/4,34/4));
            
        else if (time <= chargeTime + fireTime)
            g2.setColor(new Color(178,34,34));
        
        else if (time > chargeTime + fireTime + chargeDelay) //reset laser's time
            time = -1;
            
        time++;
            
        g2.fill(laser);
        
        g.dispose();
    }
    
    
    public Rectangle2D.Double getBoundingBox()
    {
        return laser;
    }
    
    
    public void erase()
    {
        Graphics g = panel.getGraphics();
        Graphics2D g2 = (Graphics2D) g;
        
        g2.setColor(panel.getBackground());
        g2.fill(laser);
        
        g.dispose();
    }
    
    
    public boolean isLethal()
    {
        return (time > chargeTime && time <= (fireTime + chargeTime));
    }
    
    
    public void reset()
    {
        time = 0;
    }
}
