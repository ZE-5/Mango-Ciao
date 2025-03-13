import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.*;
import javax.swing.JPanel;
import java.util.Vector;


public class Mango  {

   private JPanel panel;
   private int x;
   private int y;
   private int width;
   private int height;

   private int dx;
   private int dy;
   
   private int defaultX, defaultY;
   
   
   private int jumpHeight, time;
   
   private boolean isJumping, isRunning, isFacingRight;
   

   private Color backgroundColour;
   private Dimension dimension;
   
   private Vector<Barrier> barriers;
   private Vector<Spike> spikes;
   
   

   public Mango (JPanel p, Vector<Barrier> barriers, Vector<Spike> spikes) {
      panel = p;

      backgroundColour = panel.getBackground ();

      dx = 10;    // make bigger (smaller) to increase (decrease) speed
      dy = 10;    // no movement along y-axis allowed (i.e., move left to right only)

      width = 40;
      height = 50;
      
      this.barriers = barriers;
      this.spikes = spikes;
      isJumping = false;
      time = 0;
      
      isFacingRight = true; //starting direction
      jumpHeight = 13; //self-explanatory
   }
   
   
   public void grabPanelDimensions()
   {
       this.dimension = panel.getSize();
   }
   
   public int getJumpHeight()
   {
       return jumpHeight;
   }
   
   
   public int[] getPosition()
   {
       int[] pos = {x, y};
       return pos;
   }
   
   public int getHeight()
   {
       return height;
   }
    
   
   public void draw()
   {
      int maxTime = 14;
      
      Graphics g = panel.getGraphics ();
      Graphics2D g2 = (Graphics2D) g;
      
      //troubeshooting code for drawing Mango
      //g2.setColor(Color.WHITE);
      //g2.draw(new Rectangle2D.Double(x, y, width-1, height-1));
      
      if (isFacingRight)
      {
          //leaf
          g2.setColor(Color.GREEN);
          g2.fill(new Rectangle2D.Double(x + width/2, y, 6, 6));
          
          //stem
          g2.drawArc(x + width/2, y + 5, width * 1/4, 10, 0, 90);
          
          //body
          g2.setColor(Color.ORANGE);
          g2.fill(new Rectangle2D.Double(x, y + 10, width, height - 20));
          
          //eye
          g2.setColor(Color.BLACK);
          g2.fill(new Rectangle2D.Double(x + width - 10, y + 14, 5, 5));
      }
      else
      {
          //leaf
          g2.setColor(Color.GREEN);
          g2.fill(new Rectangle2D.Double(x + width/2 - 4, y, 6, 6));
          
          //stem
          g2.drawArc(x + width/4, y + 5, width * 1/4, 10, 180, -90);
          
          //body
          g2.setColor(Color.ORANGE);
          g2.fill(new Rectangle2D.Double(x, y + 10, width, height - 20));
          
          //eye
          g2.setColor(Color.BLACK);
          g2.fill(new Rectangle2D.Double(x + 5, y + 14, 5, 5));
      }
      
      g2.setColor(Color.YELLOW);
      if (time++ < maxTime/2) //to make legs look like they are moving
      {
          //left legs
          g2.fill(new Rectangle2D.Double(x + 3, y + height - 10, 7, 10));
          g2.fill(new Rectangle2D.Double(x + 8, y + height - 10, 7, 5));
          //right legs
          g2.fill(new Rectangle2D.Double(x + width - 10, y + height - 10, 7, 5));
          g2.fill(new Rectangle2D.Double(x + width - 15, y + height - 10, 7, 10));
      }
      else
      {
          //left legs
          g2.fill(new Rectangle2D.Double(x + 3, y + height - 10, 7, 5));
          g2.fill(new Rectangle2D.Double(x + 8, y + height - 10, 7, 10));
          //right legs
          g2.fill(new Rectangle2D.Double(x + width - 10, y + height - 10, 7, 10));
          g2.fill(new Rectangle2D.Double(x + width - 15, y + height - 10, 7, 5));
      }
      
      if (time == maxTime)
          time = 0;
            
      g.dispose();
   }
   

    public void erase (int x, int y)
    {
      Graphics g = panel.getGraphics ();
      Graphics2D g2 = (Graphics2D) g;

      g2.setColor (backgroundColour);
      g2.fill (new Rectangle2D.Double (x, y, width, height));

      g.dispose();
   }
   
   
    public void move(int direction)
   {
      switch(direction)
      {
          case 0: //up
              if ((y -= dy) < 0)
                  y = 0;
              break;
          
          case 1: //right
              if ((x += dx) + width > dimension.width)
                  x = dimension.width - width;
              break;
              
          case 2: //left
              if ((x -= dx) < 0)
                  x = 0;
              break;
              
          case 3: //down
              if ((y += dy) + height > dimension.height)
                  y = dimension.height - height;
              break;
              
          default:
                  System.out.println("ERROR: Unavailable Movement: " + direction);
      }
   }
   
   
   public void setFacingRight(boolean val)
   {
       isFacingRight = val;
   }
   

   public Vector<Rectangle2D.Double> getBounds() 
   {
       Vector <Rectangle2D.Double> bounds = new Vector<>();
       
       bounds.add(new Rectangle2D.Double(x, y, width, height));
       
       return bounds;
   }
   
   
   public void place(int x, int y)
   {
       this.x = x;
       this.y = y;
   }
   
   
   public Line2D.Double getFloorLine() //gives line UNDER Mango (1 unit below)
   {
       return new Line2D.Double(x + 1, y + height + 1, x - 1 + width, y + height + 1);
   }
   
   
   //These functions give the line which correspond to Mango's top, left or riggt most sides with no offset off of the character's dimensions
   //These lines are shrunk a little to ensure they trigger when necessary
   public Line2D.Double getRightLine()
   {
       return new Line2D.Double(x + width -1, y+1, x + width -1, y + height - 1);
   }
   
   
   public Line2D.Double getLeftLine()
   {
       return new Line2D.Double(x + 1, y + 1, x + 1, y + height - 1);
   }
   
   
   public Line2D.Double getTopLine()
   {
       return new Line2D.Double(x + 1, y - 1, x + width - 1, y - 1);
   }
}