
/* Menu Class
 * Modified: 20 April 2015  
 * Author(s): Phillip Stewart, Lap Nguyen
 * Description:
 *   The main menu, acts as its own model, view and controller.
 */

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JPanel;


enum M_State {MENU, NEW_GAME, LOAD_GAME, OPTIONS, HELP}
enum P_State {NONE, PLAYER, AI}

public class Menu extends JPanel {

    private Launcher MainController;
    private GraphicsConfiguration gConfig;
    private Graphics2D G2D;
    private Graphics G;
    private BufferedImage backBuffer;
    private BufferedImage background;
    private BufferedImage loading;
    
    private M_Button new_button;
    private M_Button load_button;
    private M_Button opts_button;
    private M_Button help_button;
    private M_Button quit_button;
    
    private M_Button p1,p2,p3,p4,t1,t2,t3,t4;
    
    private int[] pstates;
    private int[] token_ids;
    
    private BufferedImage[] tokens;
    
    
    private M_State state;
    
    public GameSave save;
    public Thread renderThread;
    public boolean rendering;

//    private Options... {sound, custom rules??}
    
    BufferedImage new_game_panel;
    BufferedImage help_panel;
    BufferedImage opts_panel;
    
    
    /* ===== Constructor ======================================================
     * ======================================================================== */
    public Menu(Launcher l, Dimension d) {
        super();
        this.MainController = l;
        this.gConfig = l.gConfig;
        this.backBuffer = this.gConfig.createCompatibleImage(d.width, d.height);
        
        this.setLayout(null);
        this.setIgnoreRepaint(true);
        this.setFocusable(true);
        this.setMaximumSize(d);
        this.setPreferredSize(d);
        
        this.background = loadImage("images/menu_background.jpg");
        this.loading = loadImage("images/loading.jpg");
        setButtons();
        loadPanels();
        
        this.renderThread = new Thread(new Renderer());
        this.state = M_State.MENU;
    }
    
    
    /* ===== Initialization ===================================================
     * ======================================================================== */
    public void setButtons() {
        BufferedImage img = loadImage("images/new_game.jpg");
        this.new_button = new M_Button(1, new Box(50, 150, 100, 50), img);
        this.add(this.new_button);
        img = loadImage("images/load_game.jpg");
        this.load_button = new M_Button(2, new Box(50, 250, 100, 50), img);
        this.add(this.load_button);
        img = loadImage("images/opts.jpg");
        this.opts_button = new M_Button(3, new Box(50, 350, 100, 50), img);
        this.add(this.opts_button);
        img = loadImage("images/help.jpg");
        this.help_button = new M_Button(4, new Box(50, 450, 100, 50), img);
        this.add(this.help_button);
        img = loadImage("images/quit.jpg");
        this.quit_button = new M_Button(5, new Box(50, 550, 100, 50), img);
        this.add(this.quit_button);
        
        //player buttons...
        img = loadImage("images/p1.png");
        p1 = new M_Button(6, new Box(224, 180, 75, 75), img);
        this.add(p1);
        img = loadImage("images/p2.png");
        p2 = new M_Button(7, new Box(224, 280, 75, 75), img);
        this.add(p2);
        img = loadImage("images/p3.png");
        p3 = new M_Button(8, new Box(224, 380, 75, 75), img);
        this.add(p3);
        img = loadImage("images/p4.png");
        p4 = new M_Button(9, new Box(224, 480, 74, 74), img);
        this.add(p4);
        
        //token buttons
        t1 = new M_Button(10, new Box(324, 180, 75, 75), null);
        this.add(t1);
        t2 = new M_Button(11, new Box(324, 280, 75, 75), null);
        this.add(t2);
        t3 = new M_Button(12, new Box(324, 380, 75, 75), null);
        this.add(t3);
        t4 = new M_Button(13, new Box(324, 480, 75, 75), null);
        this.add(t4);
        
        //start button
        this.add(new M_Button(14, new Box(774, 530, 100, 50), img));
        
        pstates = new int[]{1,2,0,0};
        token_ids = new int[]{0,1,-1,-1};
        
        tokens = new BufferedImage[8];
        for (int i = 1; i < 9; i++) {
            tokens[i-1] = loadImage("images/tokens/" + i + ".png");
        }

    }
    
    private void loadPanels() {
        new_game_panel = loadImage("images/new_game_panel.png");
        help_panel = loadImage("images/help_panel.jpg");
        opts_panel = loadImage("images/options_panel.png");
        
    }
    
    private BufferedImage loadImage(String path)
    {
        BufferedImage img = null;

        if(path == null) {
            return null;
        }
        try {
            img = ImageIO.read(new File(path));
        } catch(IOException e) {
            System.out.println("Error Loading Image: " + path);
            img = null;
        }
        return img;
    }

    
    /* ===== Click actions ====================================================
     * ======================================================================== */
    //{MENU, NEW_GAME, LOAD_GAME, OPTIONS, HELP}
    private void LoadGame() {
        if (state == M_State.LOAD_GAME) {
            state = M_State.MENU;
        } else {
            state = M_State.LOAD_GAME;
        }
    }
    
    private void NewGame() {
        if (state == M_State.NEW_GAME) {
            state = M_State.MENU;
        } else {
            state = M_State.NEW_GAME;
        }
    }
    
    private void ShowOptions() {
        if (state == M_State.OPTIONS) {
            state = M_State.MENU;
        } else {
            state = M_State.OPTIONS;
        }
        // TODO: open options panel
        // save info into gameSave object
        System.out.println("Options box clicked");
    }
    
    private void ShowHelp() {
        if (state == M_State.HELP) {
            state = M_State.MENU;
        } else {
            state = M_State.HELP;
        }
        // TODO: open panel to show help info
        System.out.println("Help box clicked");
    }
    
    private void QuitGame() {
        // kill render thread
        stop();
        
        //exit
        MainController.onClose();
    }

    
    /* ===== Start Game Buttons ===============================================
     * ======================================================================== */
    private void cycle_player(int i) {
        this.pstates[i]++;
        this.pstates[i] %= 3;
        if (this.pstates[i] == 0) {
            this.token_ids[i] = -1;
        } else if (this.pstates[i] == 1) {
            //increment token id until not taken...
            boolean taken = true;
            int tok_num = -1;
            while (taken) {
                taken = false;
                tok_num++;
                for (int j=0; j < 4; j++) {
                    if (this.token_ids[j] == tok_num) {taken = true;}
                }
            }
            this.token_ids[i] = tok_num;
        }
    }
    
    private void cycle_token(int i) {
        if (pstates[i] > 0) {
            boolean taken = true;
            int tok_num = this.token_ids[i];
            while (taken) {
                taken = false;
                tok_num++;
                tok_num %= 8;
                for (int j=0; j < 4; j++) {
                    if (i==j) {continue;}
                    if (this.token_ids[j] == tok_num) {taken = true;}
                }
            }
            this.token_ids[i] = tok_num;
        }
    }
    
    
    /* ===== Loading Game =====================================================
     * ======================================================================== */
    private void StartGame() {
        switch (state) {
            case NEW_GAME:
                ArrayList<Player> players = new ArrayList<Player>();
                for (int i=0; i < 4; i++) {
                    if (pstates[i] == 1) {
                        players.add(new Player(false, "Player #" + (i+1), i+1, token_ids[i]));
                    } else if (pstates[i] == 2) {
                        players.add(new Player(true, "AI Player #" + (i+1), i+1, token_ids[i]));
                    }
                }
                save = new GameSave(players);
                break;
            //case LOAD_GAME:
                //??
                //break;
            default:
                return;
        }
        

        // end render thread and switch to game
        stop();
        MainController.loadGame(save);
        System.out.println("done loading game");
    }
    
    
    /* ===== Class: ClickBox ==================================================
     * ======================================================================== */
    private class M_Button extends JComponent
    {
        BufferedImage img = null;
        int x, y;
        public M_Button(int id, Box b, BufferedImage image) {
            this.img = image;
            this.setIgnoreRepaint(true);
            this.x = b.x;
            this.y = b.y;
            setBounds(b.x, b.y, b.w, b.h);
            switch (id) {
                case 1: this.addMouseListener(new new_gamer()); break;
                case 2: this.addMouseListener(new load_gamer());break;
                case 3: this.addMouseListener(new optioner());  break;
                case 4: this.addMouseListener(new helper());    break;
                case 5: this.addMouseListener(new quiter());    break;

                case 6: this.addMouseListener(new cycler(0));   break;
                case 7: this.addMouseListener(new cycler(1));   break;
                case 8: this.addMouseListener(new cycler(2));   break;
                case 9: this.addMouseListener(new cycler(3));   break;
                
                case 10: this.addMouseListener(new tokener(0)); break;
                case 11: this.addMouseListener(new tokener(1)); break;
                case 12: this.addMouseListener(new tokener(2)); break;
                case 13: this.addMouseListener(new tokener(3)); break;
                
                case 14: this.addMouseListener(new starter());  break;
                default: break;
            }
        }

        private class new_gamer extends MouseAdapter {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                NewGame();
            }
        }
        private class load_gamer extends MouseAdapter {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                LoadGame();
            }
        }
        private class optioner extends MouseAdapter {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                ShowOptions();
            }
        }
        private class helper extends MouseAdapter {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                ShowHelp();
            }
        }
        private class quiter extends MouseAdapter {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                QuitGame();
            }
        }
        private class cycler extends MouseAdapter {
            int val;
            public cycler(int v) {
                this.val = v;
            }
            @Override
            public void mouseClicked(MouseEvent arg0) {
                cycle_player(this.val);
            }
        }
        private class tokener extends MouseAdapter {
            int val;
            public tokener(int v) {
                this.val = v;
            }
            @Override
            public void mouseClicked(MouseEvent arg0) {
                cycle_token(this.val);
            }
        }
        private class starter extends MouseAdapter {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                StartGame();
            }
        }
    }// end of ClickBox
    
    
    /* ===== Rendering ========================================================
     * ======================================================================== */

    public void start() {
        this.rendering = true;
        this.renderThread.start();
    }

    public void stop() {
        rendering = false;
        return;
    }

    private void render() {
        try {
            this.G2D = this.backBuffer.createGraphics();
            this.G2D.drawImage(this.background, 0, 0, null);
            
            G2D.drawImage(new_button.img, new_button.x, new_button.y, null);
            G2D.drawImage(load_button.img, load_button.x, load_button.y, null);
            G2D.drawImage(opts_button.img, opts_button.x, opts_button.y, null);
            G2D.drawImage(help_button.img, help_button.x, help_button.y, null);
            G2D.drawImage(quit_button.img, quit_button.x, quit_button.y, null);

            //{MENU, NEW_GAME, LOAD_GAME, OPTIONS, HELP}
            switch (state) {
                case NEW_GAME:
                    G2D.drawImage(new_game_panel, 174, 130, null);

                    //show buttons, show tokens, show names...
                    G2D.drawImage(p1.img, p1.x, p1.y, null);
                    G2D.drawImage(p2.img, p2.x, p2.y, null);
                    G2D.drawImage(p3.img, p3.x, p3.y, null);
                    G2D.drawImage(p4.img, p4.x, p4.y, null);

                    //Too lazy to clean this up at this point...
                    G2D.setFont(new Font("Dialog", Font.BOLD, 50));
                    if (pstates[0] == 1) {
                        G2D.drawImage(tokens[token_ids[0]], t1.x, t1.y, 75, 75, null);
                        G2D.drawString("Player #1", 424, 230);
                        
                    } else if (pstates[0] == 2) {
                        G2D.drawImage(tokens[token_ids[0]], t1.x, t1.y, 75, 75, null);
                        G2D.drawString("AI Player #1", 424, 230);
                    }
                    else {
                        G2D.drawString("--", 424, 230);
                    }
                    if (pstates[1] == 1) {
                        G2D.drawImage(tokens[token_ids[1]], t2.x, t2.y, 75, 75, null);
                        G2D.drawString("Player #2", 424, 330);
                        
                    } else if (pstates[1] == 2) {
                        G2D.drawImage(tokens[token_ids[1]], t2.x, t2.y, 75, 75, null);
                        G2D.drawString("AI Player #2", 424, 330);
                    }
                    else {
                        G2D.drawString("--", 424, 330);
                    }
                    if (pstates[2] == 1) {
                        G2D.drawImage(tokens[token_ids[2]], t3.x, t3.y, 75, 75, null);
                        G2D.drawString("Player #3", 424, 430);
                        
                    } else if (pstates[2] == 2) {
                        G2D.drawImage(tokens[token_ids[2]], t3.x, t3.y, 75, 75, null);
                        G2D.drawString("AI Player #3", 424, 430);
                    }
                    else {
                        G2D.drawString("--", 424, 430);
                    }
                    if (pstates[3] == 1) {
                        G2D.drawImage(tokens[token_ids[3]], t4.x, t4.y, 75, 75, null);
                        G2D.drawString("Player #4", 424, 530);
                        
                    } else if (pstates[3] == 2) {
                        G2D.drawImage(tokens[token_ids[3]], t4.x, t4.y, 75, 75, null);
                        G2D.drawString("AI Player #4", 424, 530);
                    }
                    else {
                        G2D.drawString("--", 424, 530);
                    }

                    break;
                case LOAD_GAME:
                    G2D.drawImage(new_game_panel, 174, 130, null);
                    break;
                case OPTIONS:
                    G2D.drawImage(opts_panel, 174, 130, null);
                    break;
                case HELP:
                    G2D.drawImage(help_panel, 174, 130, null);
                    break;
                default:
                    break;
            }
            
            
            // Show Back Buffer
            G = this.getGraphics();
            if (this.backBuffer != null && G != null) {
                G.drawImage(this.backBuffer, 0, 0, null);
            } else {
                System.out.println("Menu Graphics null...");
            }
        } finally {
            if(this.G2D != null) {
                this.G2D.dispose();
            }
            if(this.G != null) {
                this.G.dispose();
            }
        }
    }// end of render
    
    private void renderBlack() {
        G = this.getGraphics();
        G.drawImage(this.loading, 0, 0, 1024, 768, null);
        
        if (G != null) {
            G.dispose();
        }
        System.out.println("done renderBlack");
    }


    private class Renderer implements Runnable
    {
        private long SLEEP_TIME = 50;

        @Override
        public void run() {
            while(rendering) {
                render();
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException ignore) {}
            }
            renderBlack();
            //System.out.println("Menu render thread: " + Thread.currentThread().getName() + " ended");
        }
    } // end of Renderer

}// end of Menu Class
