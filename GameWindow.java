import javax.swing.*;            // need this for GUI objects
import java.awt.*;            // need this for Layout Managers
import java.awt.event.*;        // need this to respond to GUI events
    
public class GameWindow extends JFrame  
                implements ActionListener,
                       KeyListener
{
    // declare instance variables for user interface objects
    // declare buttons

    private JButton startB;
    private JButton focusB;
    private JButton exitB;

    private Container c;

    //panels
    private JPanel mainPanel, infoPanel, buttonPanel;
    private GamePanel gamePanel;
    private HeartPanel heartPanel;
    private CoinPanel coinPanel;
    
    //variables
    private int hearts, coins;
    

    @SuppressWarnings({"unchecked"})
    public GameWindow() {
        
        hearts = 3;
        coins = 0;
        
        setTitle ("Mango Chao");
        setSize (800, 700);

        // create user interface objects
        // create buttons

        startB = new JButton ("Start");
        focusB = new JButton ("Focus on Key");
        exitB = new JButton ("Exit");

        // add listener to each button (same as the current object)

        startB.addActionListener(this);
        focusB.addActionListener(this);
        exitB.addActionListener(this);

        
        // create mainPanel

        mainPanel = new JPanel();
        FlowLayout flowLayout = new FlowLayout();
        mainPanel.setLayout(flowLayout);

        GridLayout gridLayout;

        // create the gamePanel for game entities

        gamePanel = new GamePanel(this);
        gamePanel.setPreferredSize(new Dimension(700, 550));
        gamePanel.setBackground(Color.BLACK);
        gamePanel.createGameEntities();


        // create infoPanel

        infoPanel = new JPanel();
        gridLayout = new GridLayout(1, 2);
        infoPanel.setLayout(gridLayout);
        infoPanel.setPreferredSize(new Dimension(700, 70));

        // add user interface objects to infoPanel
        infoPanel.add(heartPanel = new HeartPanel());
        heartPanel.setBackground(Color.BLACK);
        
        infoPanel.add(coinPanel = new CoinPanel());
        coinPanel.setBackground(Color.BLACK);
        
        // create buttonPanel

        buttonPanel = new JPanel();
        gridLayout = new GridLayout(1, 3);
        buttonPanel.setLayout(gridLayout);

        // add buttons to buttonPanel

        buttonPanel.add (startB);
        buttonPanel.add (focusB);
        buttonPanel.add (exitB);

        // add sub-panels with GUI objects to mainPanel and set its colour

        mainPanel.add(infoPanel);
        mainPanel.add(gamePanel);
        mainPanel.add(buttonPanel);
        mainPanel.setBackground(Color.DARK_GRAY);

        // set up mainPanel to respond to keyboard 
        mainPanel.addKeyListener(this);

        // add mainPanel to window surface

        c = getContentPane();
        c.add(mainPanel);

        // set properties of window

        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setVisible(true);
    }


    // implement single method in ActionListener interface

    public void actionPerformed(ActionEvent e) {

        String command = e.getActionCommand();
        
        
        if (command.equals(focusB.getText()))
            mainPanel.requestFocus();
        
        
        if (command.equals(startB.getText()))
        {
            heartPanel.draw(hearts);
            coinPanel.draw(coins);
            
            gamePanel.grabPanelDimensions();
            gamePanel.drawAllGameEntities();
            gamePanel.start();
        }
        
        
        if (command.equals(exitB.getText()))
            System.exit(0);

        mainPanel.requestFocus();
    }


    // implement methods in KeyListener interface

    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        
        switch (keyCode)
        {
            case (KeyEvent.VK_UP):
                gamePanel.setKey(0, true);
                break;
            
            case (KeyEvent.VK_RIGHT):
                gamePanel.setKey(1, true);
                break;
                
            case (KeyEvent.VK_LEFT):
                gamePanel.setKey(2, true);         
        }        
    }
    

    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();        
        switch (keyCode)
        {
            case (KeyEvent.VK_UP):
                gamePanel.setKey(0, false);
                break;
            
            case (KeyEvent.VK_RIGHT):
                gamePanel.setKey(1, false);
                break;
                
            case (KeyEvent.VK_LEFT):
                gamePanel.setKey(2, false);
        }
    }
    

    public void keyTyped(KeyEvent e) {

    }
    
    
    public void minusHeart()
    {
        heartPanel.erase();
        hearts --;
        heartPanel.draw(hearts);
        if (hearts == 0)
        {
            gamePanel.eraseAllGameEntities();
            gamePanel.drawLoseScreen();
        }
    }
    
    
    public void addCoin()
    {
        coinPanel.erase();
        coins++;
        coinPanel.draw(coins);
    }
}