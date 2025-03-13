import javax.swing.JPanel;
import java.util.Vector;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Color;


/**
   A component that displays all the game entities
*/

public class GamePanel extends JPanel {
    
   private class UpdateLoop extends Thread
   {
       private boolean isRunning;
       
       public void run()
       {
           isRunning = true;
           try{
               while (isRunning)
               {
                   //update Enitiies
                   updateGameEntities();
                   
                   //check barrier collision
                   resolveBarrierCollisions();
                   
                   //check coin collision
                   Coin collidedCoin = coinCollision();
                   if (collidedCoin != null)
                   {
                       collidedCoin.erase();
                       coins.remove(collidedCoin);
                       gameWindow.addCoin();
                   }
                   
                   //check if door was reached
                   if (doorCollision())
                   {
                       eraseAllGameEntities();
                       level++;
                       setLevel();
                       
                       if (isRunning)
                       {
                           resetMangoJumping();
                           drawAllGameEntities();   
                       }
                   }
                   //check hazard collision and draw accordingly
                   else if (spikeCollision() || laserCollision())
                   {
                       eraseAllGameEntities();
                       drawAllGameEntities();
                       sleep(100);
                       
                       mangoDied();
                       
                       if (isRunning)
                       {
                           eraseAllGameEntities();
                           drawAllGameEntities();   
                       }
                   }
                   else 
                   {
                       eraseMango();
                       
                       eraseLasers();
                       drawLasers();
                       
                       drawMango();
                       mangoPos = mango.getPosition();
                   }
                       
                   sleep(40);
               }
            }
           catch (InterruptedException e)
           {
               
           }
       }
       
       
       public void stopRunning()
       {
           isRunning = false;
       }
   }
   
   private GameWindow gameWindow;
   
   private int level;
   
   private Vector<Barrier> barriers;
   private Vector<Spike> spikes;
   private Vector<Coin> coins;
   private Vector<Laser> lasers;
   private Door door;
   private boolean[] keys;
   private UpdateLoop updateLoop;

   // Variables to govern Mango (the player)
   private Mango mango;
   private int[] mangoPos; // {x, y}
   private int [] defaultMangoPos;
   private boolean mangoIsJumping;
   private int mangoCurrentJumpHeight, mangoMaxJumpHeight;
   
   public GamePanel (GameWindow gameWindow) {
        this.gameWindow = gameWindow;
        
        mango = null;
        
        barriers = new Vector<>();
        spikes = new Vector<>();
        coins = new Vector<>();
        lasers = new Vector<>();
        
        this.keys = new boolean[3];
        
        updateLoop = new UpdateLoop();
   }
   
   
   public void start()
   {
       updateLoop.start();
   }


   public void setKey(int num, boolean val)
   {
       keys[num] = val;
   }
   
   
   public void grabPanelDimensions()
   {
       mango.grabPanelDimensions();
   }
   
   public void createGameEntities() {
       mango = new Mango (this, barriers, spikes); 
       
       if (mango != null) //initializing variables associated with Mango
       {
           mangoIsJumping = false;
           mangoCurrentJumpHeight = 0;
           mangoMaxJumpHeight = mango.getJumpHeight();
           defaultMangoPos = mangoPos = mango.getPosition();
       }
       
       //Setting up first level
       level = 1;
       setLevel();
   }


   public void drawWinScreen()
   {
       Graphics g = this.getGraphics();
       Graphics2D g2 = (Graphics2D) g;
       
       updateLoop.stopRunning();
       
       g2.setColor(Color.GREEN);
       Font f = new Font ("Bookman Old Style", Font.BOLD, 40);
       g2.setFont (f);
       g2.drawString("You Win!", 200, 200);
       
       g2.dispose();
   }
   
   
   public void drawLoseScreen()
   {
       Graphics g = this.getGraphics();
       Graphics2D g2 = (Graphics2D) g;
       
       updateLoop.stopRunning();       
       
       g2.setColor(Color.RED);
       Font f = new Font ("Bookman Old Style", Font.BOLD, 40);
       g2.setFont (f);
       g2.drawString("Loser!", 200, 200);
              
       g2.dispose();
   }
   
   
   public void drawAllGameEntities() {

       drawMango();
       
       for (Barrier b: barriers)
           b.draw();
       
       for (Spike s: spikes)
           s.draw();
       
       drawLasers();
           
       for (Coin c: coins)
           c.draw();
           
       door.draw();
   }
   
   
   public void eraseAllGameEntities() {
       Graphics g = (Graphics) this.getGraphics();
       Graphics2D g2 = (Graphics2D) g;
       g2.fill(new Rectangle2D.Double(0, 0, this.getSize().width, this.getSize().height));
   }
   
   
   public void drawMango()
   {
       if (mango != null)
           mango.draw();
   }
   
   
   public void eraseMango()
   {
       if (mango != null)
           mango.erase(mangoPos[0], mangoPos[1]);
   }


   public void updateGameEntities() {
        
        //UPDATING AMON START

        if (mango == null)
            return;
            
        boolean mangoOnFloor = isMangoOnFloor();
        
        //Handling JUMPING and FALLING
        if (mangoIsJumping)
        {
            mango.move(0);
            if (++mangoCurrentJumpHeight == mangoMaxJumpHeight || (mango.getPosition())[1] == 0)
            {
                resetMangoJumping();
            }
        }
        else if (!mangoOnFloor) //Mango is neither jumping nor standing on a floor
        {
            mango.move(3);
        }
        
        //Handling key INPUTS
        if (keys[1])    //right
        {
            mango.move(1);
            mango.setFacingRight(true);
        }
        
        if (keys[2]) //left
        {
            mango.move(2);
            mango.setFacingRight(false);
        }
            
        if (keys[0] && mangoOnFloor) //up key pressed
            mangoIsJumping = true;
            
        //UPDATING AMON END
   }
   
   
   private void resetMangoJumping()  //resets Mango's jump data
   {
       mangoIsJumping = false;
       mangoCurrentJumpHeight = 0;
   }
   
   
   private boolean isMangoOnFloor()
   {
       if (mango.getPosition() [1] == this.getHeight() - mango.getHeight())
           return true;
       
       Line2D.Double mangoFloorLine = mango.getFloorLine();
       for (Barrier b : barriers)
       {
           if ((b.getBoundingBox()).intersectsLine(mangoFloorLine))
               return true;
       }
       return false;
   }
   
   
   private void resolveBarrierCollisions()
   {
       for (Barrier b : barriers)
       {
           Rectangle2D barrierBoundingBox = b.getBoundingBox();
           
           if (keys[2] && barrierBoundingBox.intersectsLine(mango.getLeftLine())) //left side intersects
               mango.move(1);
           
           
           if (keys[1] && barrierBoundingBox.intersectsLine(mango.getRightLine())) //right side intersects
               mango.move(2);
               
           if (mangoIsJumping && barrierBoundingBox.intersectsLine(mango.getTopLine())) //head intersects
               resetMangoJumping();
       }
       
       //ISSUE: this only worked for the FIRST if statement; the other direction let it phase through the wall
               //This only occured when the character was under a certain width
       //REASON: the width was <= the movement amount (dx). This allowed both left and sides to enter the barrier and trigger,
               //which resulted in no displacement to move the character out of the barrier.
       //SOLUTION: While adjusting the character size and movement displacement will fix this, to allow for freedom of character design,
               //the keys were inspected to determine the direction the character was moving and only check for collisions in that direction.
   }
   
   
   public void mangoDied()
   {
       gameWindow.minusHeart();
       resetMangoJumping();
       mango.place(defaultMangoPos[0], defaultMangoPos[1]);
       mangoPos = mango.getPosition();
       resetLasers();
   }
   
   
   public boolean spikeCollision()
   {
       for (Rectangle2D.Double mangoHitBox: mango.getBounds())
       {
           for (Spike s:spikes)
           {
               if ((s.getBoundingTriangle()).intersects(mangoHitBox))
                   return true;
           }
       }
       return false;
   }
   
   
   public Coin coinCollision() //detects if a coin has been touched and returns it for deletion
   {
       for (Rectangle2D.Double mangoHitBox: mango.getBounds())
       {
           for (Coin c: coins)
           {
               if ((c.getBoundingEllipse()).intersects(mangoHitBox))
                   return c;
           }
       }
       return null;
   }
   
   
   public boolean doorCollision()
   {
       for (Rectangle2D.Double mangoHitBox: mango.getBounds())
       {
           if (door.getBoundingRectangle().intersects(mangoHitBox))
               return true;
       }
       return false;
   }
   
   
   public boolean laserCollision()
   {
       for (Rectangle2D.Double mangoHitBox: mango.getBounds())
       {
           for (Laser l : lasers)
           {
               if (l.isLethal() && l.getBoundingBox().intersects(mangoHitBox))
                   return true;
           }
       }
       return false;
   }
   
   
   public void resetLasers()
   {
       for (Laser l: lasers)
           l.reset();
   }
   
   
   public void drawLasers()
   {
       for (Laser l: lasers)
           l.draw();
   }
   
   
   public void eraseLasers()
   {
       for (Laser l: lasers)
           l.erase();
   }
   
   
   //level design
   public void setLevel()
   {
       barriers.removeAllElements();
       spikes.removeAllElements();
       coins.removeAllElements();
       lasers.removeAllElements();
       
       switch(level)
       {
           case 1:
               mango.place(50, 350);
               defaultMangoPos = mangoPos = mango.getPosition();
               door = new Door(this, 100, 100);
               
               barriers.add(new Barrier(this, 100,350,400,30));
               barriers.add(new Barrier(this, 0,400,30,30));
               barriers.add(new Barrier(this,0,500,800,50));
               barriers.add(new Barrier(this,190,240,60,50));
               barriers.add(new Barrier(this,300,150,60,50));
               
               spikes.add(new Spike(this, 50, 50, 50, 50, 1));
               spikes.add(new Spike(this, 50, 50, 50, 50, 2));
               spikes.add(new Spike(this, 400, 500, 50, 50, 0));
               
               coins.add(new Coin(this, 400,400));
               coins.add(new Coin(this, 500,200));
               coins.add(new Coin(this, 400,100)); 
               
               lasers.add(new Laser(this, 450, 0, 50, 350, 10, 10, 10));

               break;
               
           case 2:
               
               mango.place(100, 400);
               defaultMangoPos = mangoPos = mango.getPosition();
               
               door = new Door(this, 0, 0);
               
               barriers.add(new Barrier(this, 300,330,100,30));
               barriers.add(new Barrier(this, 150, 180, 200, 30));
               barriers.add(new Barrier(this, 500, 250, 100, 50));
               barriers.add(new Barrier(this, 0, 500, 250, 50));
               barriers.add(new Barrier(this, 450, 400, 100, 40));
               barriers.add(new Barrier(this, 650, 510, 50, 40));
               barriers.add(new Barrier(this, 0, 140, 130, 20));
               
               spikes.add(new Spike(this, 300, 550, 0));
               spikes.add(new Spike(this, 700, 250, 2));
               
               lasers.add(new Laser(this, 450, 0, 50, 400, 10, 10, 10));
               lasers.add(new Laser(this, 0, 50, 700, 20, 10, 10, 10));
               
               coins.add(new Coin(this, 275, 500));
               coins.add(new Coin(this, 670, 440));
               coins.add(new Coin(this, 640, 130));
               
               break;
               
           default:
               updateLoop.stopRunning();
               drawWinScreen();
       }    
   }
}