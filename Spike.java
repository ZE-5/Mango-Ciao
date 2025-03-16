import java.awt.Polygon;
import java.awt.Color;
import javax.swing.JPanel;
// import java.awt.Graphics;
import java.awt.Graphics2D;


public class Spike 
{   
    Polygon spike;
    JPanel panel;
    
    public Spike(JPanel panel, int x, int y, int width, int height, int direction)
    {
        //for height and width, think of a rectangle and the spike itself, picture
        //drawing a rectangle around it
        //the rectangle is DRAWN:
        //when vertical - from the left corner that also lies on the base of the spike
        //when horizontal - from the top corner that also lies on the base of the spike
        
        this.panel = panel;
        int[] xpoints = new int[3];
        int [] ypoints = new int[3];
        
        switch (direction)
        {
            case 0: //up
                xpoints[0] = x;
                xpoints[1] = x + width/2;
                xpoints[2] = x + width;
                
                ypoints[0] = y;
                ypoints[1] = y - height;
                ypoints[2] = y;
                break;
                
            case 1: //right
                xpoints[0] = x;
                xpoints[1] = x + width;
                xpoints[2] = x;
                
                ypoints[0] = y;
                ypoints[1] = y + height/2;
                ypoints[2] = y + height;
                break;
                
            case 2: //left
                xpoints[0] = x;
                xpoints[1] = x - width;
                xpoints[2] = x ;
                
                ypoints[0] = y;
                ypoints[1] = y + height/2;
                ypoints[2] = y + height;
                break;
            
            case 3: //down
                xpoints[0] = x;
                xpoints[1] = x + width/2;
                xpoints[2] = x + width;
                
                ypoints[0] = y;
                ypoints[1] = y + height;
                ypoints[2] = y;
                break;
                
            default:
                System.out.println("ERROR: undefined SPIKE direction" + direction);
        }
        
        this.spike = new Polygon(xpoints, ypoints, 3);
    }
    
    
    public Spike(JPanel panel, int x, int y, int direction)
    {
        this(panel, x, y, 50, 50, direction);
    }
    
    
    public void draw(Graphics2D g2)
    {
        // Graphics g = panel.getGraphics();
        // Graphics2D g2 = (Graphics2D) g;
        
        g2.setColor(Color.WHITE);
        g2.fill(spike);
        
        // g.dispose();
    }
    
    
    public Polygon getBoundingTriangle()
    {
        return spike;
    }
    
    
    public void erase(Graphics2D g2)
    {
        // Graphics g = panel.getGraphics();
        // Graphics2D g2 = (Graphics2D) g;
        
        g2.setColor(panel.getBackground());
        g2.fill(spike);
        
        // g.dispose();
    }
}
