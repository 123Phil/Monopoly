
/* Launcher Class
 * Modified: 20 April 2015  
 * Author(s): Phillip Stewart
 * Description:
 *   Starts the main program and hands control to the menu.
 * 
 */

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;


public class Launcher {
    private static final int HEIGHT = 768;
    private static final int WIDTH = 1024;
    
    public static void main(String[] args) 
    {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Launcher();
            }
        });
    }// end of main

    // Launcher members:
    public static GraphicsConfiguration gConfig;
    private static JFrame frame;
    private Menu menu;
    private GameController gameControl;
    private Dimension d;
    
    // Launcher:
    public Launcher() {
        super();
        this.d = new Dimension(WIDTH, HEIGHT);
        initFrame();
        loadMenu();
        menu.start();
    }

    private void initFrame() {
        gConfig = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        
        frame = new JFrame();
        frame.setTitle("Monopoly!");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setVisible(true);
        
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                System.out.println("Window closed, exiting.");
                onClose();
            }
        });
    }
    
    public void loadMenu() {
        this.gameControl = null;
        this.menu = new Menu(this, d);
        frame.setContentPane(this.menu);
        frame.pack();
    }
    
    public void loadGame(GameSave gs) {
        gameControl = new GameController(this, gs, d);
        frame.setContentPane(this.gameControl.gui);
        frame.pack();
        while (menu.renderThread.isAlive()) {
            try {
                System.out.println("Waiting for menu to end");
                Thread.sleep(50);
            } catch (InterruptedException ignore) {}
        }
        this.menu = null;
        gameControl.start();
    }
    
    public void onClose() {
        if (menu != null) {
            menu.stop();
            while (menu.renderThread.isAlive()) {
                try {
                    System.out.println("Waiting for menu to end");
                    Thread.sleep(50);
                } catch (InterruptedException ignore) {}
            }
        }
        if (gameControl != null) {
            gameControl.stop();
            while (gameControl.gui.renderThread.isAlive()) {
                try {
                    System.out.println("Waiting for game to end");
                    Thread.sleep(50);
                } catch (InterruptedException ignore) {}
            }
        }

        frame.dispose();
        System.exit(0);
    }
    
}// end of Launcher





