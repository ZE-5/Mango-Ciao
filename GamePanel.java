// import javax.swing.JComponent;
import javax.swing.JPanel;
import java.util.Vector;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Font;
import java.awt.Color;


/**
   A component that displays all the game entities
*/

public class GamePanel extends JPanel implements Runnable 
{
   private GameWindow gameWindow;

   private boolean isRunning, isPaused;
   
   private int level;
   
   private Vector<Barrier> barriers;
   private Vector<Spike> spikes;
   private Vector<Coin> coins;
   private Vector<Laser> lasers;
   private Door door;
   private boolean[] keys;

   // Variables to govern Mango (the player)
   private Mango mango;
   private int [] mangoStartingPosition;
   private boolean mangoIsJumping;
   private int mangoCurrentJumpHeight, mangoMaxJumpHeight;
   private BufferedImage bufferedImage;
   boolean lose, win;

   private Image backgroundImage;
   

   public GamePanel (GameWindow gameWindow) {
        this.gameWindow = gameWindow;
        
        mango = null;
        
        barriers = new Vector<>();
        spikes = new Vector<>();
        coins = new Vector<>();
        lasers = new Vector<>();
        
        this.keys = new boolean[3];

        isRunning = false;
        isPaused = false;

        lose = win = false;

        createGameEntities();
        backgroundImage = ImageManager.loadImage("Images/Background.png");
    }


    public void initialization()
    {
        this.bufferedImage = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
        mango.grabPanelDimensions();
    }


    public void startGame()
    {
        if (isRunning)
            return;

        isPaused = false;

        SoundManager soundManager = SoundManager.getInstance();
        soundManager.playClip("background", true);
        soundManager.setVolume("background", 0.7f);

        Thread gameThread = new Thread(this);
        gameThread.start();
    }
   
   
    //Update loop
   public void run()
   {
       isRunning = true;
       while (isRunning) 
       {
           if (!isPaused)
                updateGame();
           renderGame();
           try
           {
               Thread.sleep(40);
           }
           catch (Exception e)
           {
               
           }
       }
   }


   public void setKey(int num, boolean val)
   {
       keys[num] = val;
   }
   
   public void createGameEntities() {
       mango = new Mango (this); 
       
       if (mango != null) //initializing variables associated with Mango
       {
           mangoIsJumping = false;
           mangoCurrentJumpHeight = 0;
           mangoMaxJumpHeight = mango.getJumpHeight();
           mangoStartingPosition = mango.getPosition();
       }
       
       //Setting up first level
       level = 1;
       setLevel();
   }


   public void drawWinScreen()
   {
       win = true;
       isRunning = false;
       eraseGame();

       Graphics g = this.getGraphics();
       Graphics2D g2 = (Graphics2D) g;
       
       g2.setColor(Color.GREEN);
       Font f = new Font ("Bookman Old Style", Font.BOLD, 40);
       g2.setFont (f);
       g2.drawString("You Win!", 200, 200);
       
       g2.dispose();
   }
   
   
   public void drawLoseScreen()
   {
       lose = true;
       isRunning = false;
       eraseGame();

       Graphics g = this.getGraphics();
       Graphics2D g2 = (Graphics2D) g;    
       
       g2.setColor(Color.RED);
       Font f = new Font ("Bookman Old Style", Font.BOLD, 40);
       g2.setFont (f);
       g2.drawString("Loser!", 200, 200);
              
       g2.dispose();
   }


   public void updateGame()
   {
       //update Entities
       updateMango();        
       //check barrier collision
       resolveBarrierCollisions();
       
       //check coin collision
       resolveCoinCollisions();
       
       //check if door was reached
       resolveDoorCollisions();
       

       //check hazard collision and draw accordingly
       if (spikeCollision() || laserCollision())
       {   
           mangoDied();
       }
   }
   
   
   public void renderGame()
   {
       Graphics2D buffer = (Graphics2D) bufferedImage.getGraphics();
       

       //Erase previous image
       //eraseGame(buffer);
       buffer.drawImage(backgroundImage, 0, 0, this.getSize().width, this.getSize().height, null);


       mango.draw(buffer);;
       
       for (Barrier b: barriers)
           b.draw(buffer);
       
       for (Spike s: spikes)
           s.draw(buffer);
       
       for (Laser l: lasers)
           l.draw(buffer);
           
       for (Coin c: coins)
           c.draw(buffer);
           
       door.draw(buffer);

       //Placed here incase the loss or win occurs while the buffer image is being generated.
       //It will simply not output the image.
       if (lose || win)
            return;

       Graphics2D g2 = (Graphics2D) this.getGraphics();
       g2.drawImage(bufferedImage, 0, 0, this.getSize().width, this.getSize().height, null);
       g2.dispose();
       buffer.dispose();
   }
   
   
   public void eraseGame() 
   {
       Graphics g = (Graphics) this.getGraphics();
       Graphics2D g2 = (Graphics2D) g;
       g2.setColor(this.getBackground());
       g2.fill(new Rectangle2D.Double(0, 0, this.getSize().width, this.getSize().height));
       g.dispose();
   }


   public void eraseGame(Graphics2D g2) 
   {
        g2.setColor(this.getBackground());
        g2.fill(new Rectangle2D.Double(0, 0, this.getSize().width, this.getSize().height));
   }


   public void updateMango() {
        
        //UPDATING AMON START

        if (mango == null)
            return;
            
        boolean mangoOnFloor = isMangoOnFloor();
        mango.setIsOnGround(mangoOnFloor);
        
        //Handling JUMPING and FALLING
        if (mangoIsJumping)
        {
            mango.move(0);
            if (++mangoCurrentJumpHeight == mangoMaxJumpHeight || (mango.getPosition())[1] == 0) //hit an object above Mango
            {
                resetMangoJumping();
            }
        }
        else if (!mangoOnFloor) //Mango is neither jumping nor standing on a floor, i.e. falling
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
           
           if (keys[2] && barrierBoundingBox.intersectsLine(mango.getLeftLine())) //left side of mango intersects
               mango.setX(b.getX() + b.getWidth());
               
           
           
           if (keys[1] && barrierBoundingBox.intersectsLine(mango.getRightLine())) //right side intersects
               mango.setX(b.getX() - mango.getWidth());

               
           if (mangoIsJumping && barrierBoundingBox.intersectsLine(mango.getTopLine())) //head intersects
            {
               resetMangoJumping();
               mango.setY(b.getY() + b.getHeight());
            }
       }
   }
   
   
   public void mangoDied()
   {
       gameWindow.minusHeart();
       resetMangoJumping();
       mango.place(mangoStartingPosition[0], mangoStartingPosition[1]);
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
   
   
   public void resolveCoinCollisions() //detects if a coin has been touched and returns it for deletion
   {
       for (Rectangle2D.Double mangoHitBox: mango.getBounds())
       {
           for (Coin c: coins)
           {
               if ((c.getBoundingEllipse()).intersects(mangoHitBox))
               {
                    //collidedCoin.erase();
                    coins.remove(c);
                    gameWindow.addCoin();
                    break;
               }
           }
       }
   }
   
   
   public void resolveDoorCollisions()
   {
       for (Rectangle2D.Double mangoHitBox: mango.getBounds())
       {
           if (door.getBoundingRectangle().intersects(mangoHitBox))
           {
               level++;
               setLevel();
               resetMangoJumping();
           }
       }
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
               mangoStartingPosition = mango.getPosition();
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
               mangoStartingPosition = mango.getPosition();
               
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
            //    isRunning = false;
               drawWinScreen();
       }    
   }
}