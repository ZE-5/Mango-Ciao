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

   private boolean isRunning;
   
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
        lose = win = false;

        level = 1;

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

        SoundManager soundManager = SoundManager.getInstance();
        soundManager.playClip("background", true);
        soundManager.setVolume("background", 0.7f);

        Thread gameThread = new Thread(this);
        gameThread.start();
    }


    public void restartGame()
    {

        level = 1;
        setLevel();
        gameWindow.setCoins(0);
        gameWindow.setHearts(3);
        
        lose = win = false;
        
        SoundManager soundManager = SoundManager.getInstance();
        soundManager.stopAllClips();
        soundManager.loadAllClips();
        soundManager.playClip("background", true, 0.7f);
        resetMangoJumping();
        mango.place(mangoStartingPosition[0], mangoStartingPosition[1]);
        resetLasers();
        mango.setFacingRight(true);

        
        if (!isRunning)
        {
            isRunning = true;
            Thread gameThread = new Thread(this);
            gameThread.start();
        }
    }
   
   
    //Update loop
   public void run()
   {
       isRunning = true;
       while (isRunning) 
       {
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

       SoundManager soundManager = SoundManager.getInstance();
       soundManager.stopClip("background");
       soundManager.playClip("win", false);
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

       SoundManager soundManager = SoundManager.getInstance();
       soundManager.stopClip("background");
       soundManager.playClip("lose", false);
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
       SoundManager.getInstance().playClip("hurt", 0.7f);
       gameWindow.minusHeart();
       resetMangoJumping();
       mango.place(mangoStartingPosition[0], mangoStartingPosition[1]);
       resetLasers();

       for (int i = 0; i!= 3; i++)
            keys[i] = false;
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
       if (!coins.isEmpty())
           return;

       door.setVisible(true);
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
               
               mango.place(10, 400);
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
               
               lasers.add(new Laser(this, 450, 0, 50, 400, 10, 10, 10)); //right        
               lasers.add(new Laser(this, 350, 0, 50, 330, 40, 10, 10)); //middle
               lasers.add(new Laser(this, 200, 0, 50, 500, 10, 10, 10)); //left
            //    lasers.add(new Laser(this, 0, 50, 700, 20, 10, 10, 10));
               
               coins.add(new Coin(this, 275, 500));
               coins.add(new Coin(this, 670, 440));
               coins.add(new Coin(this, 640, 130));
               
               break;


           case 3:
               mango.place(10, 470);
               mangoStartingPosition = mango.getPosition();
               
               door = new Door(this, 200, 200);

               int x = 95;
               barriers.add(new Barrier(this, 0 + x,450,520 - x,30)); //first roof
               barriers.add(new Barrier(this, 520,120,10,360)); //wall
               barriers.add(new Barrier(this, 520,470,50,10)); //platform 1
               barriers.add(new Barrier(this, 630, 380, 30, 10)); // platform 2
               barriers.add(new Barrier(this, 670, 310, 30, 10)); //platform 3
               barriers.add(new Barrier(this, 530, 230, 30, 10)); //platform 4

               spikes.add(new Spike(this, x, 450, 0));
               spikes.add(new Spike(this, 630, 390, 30, 30, 3)); //spike under platform 2
               spikes.add(new Spike(this, 670, 320, 30, 30, 3)); //spike under platform 3
               spikes.add(new Spike(this, 530, 240, 30, 30, 3)); //spike under platform 3

               x = 15;
               int y = 0;

               lasers.add(new Laser(this, 100, 480, 20, 100, 5 - y, 10, 45 - x));
               lasers.add(new Laser(this, 100 + 50, 480, 20, 100 - y, 10, 10, 40 - x));
               lasers.add(new Laser(this, 100 + 100, 480, 20, 100 - y, 15, 10, 35 - x));
               lasers.add(new Laser(this, 100 + 150, 480, 20, 100 - y, 20, 10, 30 - x));
               lasers.add(new Laser(this, 100 + 200, 480, 20, 100 - y, 25, 10, 25 - x));
               lasers.add(new Laser(this, 100 + 250, 480, 20, 100 - y, 30, 10, 20 - x));
               lasers.add(new Laser(this, 100 + 300, 480, 20, 100 - y, 35, 10, 15 - x));
               lasers.add(new Laser(this, 100 + 350, 480, 20, 100 - y, 40, 10, 10 - x));
               lasers.add(new Laser(this, 100 + 400, 480, 20, 100 - y, 45, 10, 5 - x));
               
               coins.add(new Coin(this, 450, 400));
               break;
               
           default:
            //    isRunning = false;
               drawWinScreen();
       }    
   }
}