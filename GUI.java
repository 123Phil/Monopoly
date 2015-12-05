
/* GUI Class
 * Modified: 05 May 2015  
 * Author(s): Phillip Stewart, Lap Nguyen
 * Description:
 *   The GUI for the game portion of the program.
 *   Does all drawing to screen, retains all images and screen coordinates.
 *   
 *   The public Draw...() functions act as an API for the GameController.
 */


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.color.ColorSpace;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class GUI extends JPanel
{
    /* ===== Members ===========================================================================
     * ========================================================================================= */

    // Graphics
    private GraphicsConfiguration gConfig;
    private BufferedImage backBuffer;
    private BufferedImage background;
    private Graphics G;
    private Graphics2D G2D;

    // Rendering
    private ArrayList<ImageInfo> images;
    private ArrayList<ImageInfo> topImages;
    private ArrayList<ImageInfo> top2Images;
    public Thread renderThread;
    private boolean keepRendering;
    
    private boolean extendedLog;
    ArrayList<String> log;

    // Images...
    int board_img;
    int logID, log_longID;
    BufferedImage log_img;
    int[] deedFronts;
    int[] deedBacks;
    int[] chanceIDs;
    int[] commChIDs;
    int[] dice;
    int[] tokens;

    int playerBgID;
    int paneBgID;
    int manageBgID;
    int okID;
    int buyID;
    int payID;
    int roll2ID;
    int manage2ID;
    int jail_freeID;
    int resignID;
    int auctionID;
    int rolldiceID;
    int manageID;
    int endID;
    int blankID;

    // dice/prop/player/card panels
    int p1Pane, p2Pane, p3Pane, p4Pane; // ?? make array[4]?
    int dicePane;
    int cardPane;
    int propPane;
    int jailChoicePane;
    int managePane;
    int payPane;
    int auctionPane;
    int buttonPane;
    int blankPane;

    long t; //used for FPS


    /* ===== Constructor =======================================================================
     * ========================================================================================= */
    public GUI(GraphicsConfiguration g, Dimension d) {
        super();
        this.setLayout(null);
        this.setIgnoreRepaint(true);
        this.setFocusable(true);
        this.setMaximumSize(d);
        this.setPreferredSize(d);

        this.backBuffer = g.createCompatibleImage(d.width, d.height);
        this.gConfig = g;
        this.background = loadImage("images/background.jpg");
        this.images = new ArrayList<ImageInfo>();
        this.topImages = new ArrayList<ImageInfo>();
        this.top2Images = new ArrayList<ImageInfo>();
        this.renderThread = new Thread(new Renderer());
        loadImages();
        this.extendedLog = false;
        this.add(new LogToggle(882, 45, 0));
        this.add(new LogToggle(882, 470, 1));
    }

    private void loadImages() {
        //load everything, save the ID's into arrays
        board_img = addImage("images/board.jpg", 262, 168);
        
        logID = addImage("images/log.jpg", 112, 0);
        log_longID = addImage("images/log_full.jpg", 112, 0);
        
        deedFronts = new int[28];
        deedBacks = new int[28];
        for (int i = 1; i < 29; i++) {
            deedFronts[i-1] = addTopImage("images/deeds/deed" + i + "front.jpg", 0, 0);
            deedBacks[i-1] = addTopImage("images/deeds/deed" + i + "back.jpg", 0, 0);
        }
        chanceIDs = new int[16];
        commChIDs = new int[16];
        for (int i = 1; i < 17; i++) {
            commChIDs[i-1] = addTopImage("images/comm/comm" + i + ".jpg", 0, 0);
            chanceIDs[i-1] = addTopImage("images/chance/chance" + i + ".jpg", 0, 0);
        }
        dice = new int[6];
        for (int i = 1; i < 7; i++) {
            dice[i-1] = addTopImage("images/dice/d" + i + ".jpg");
        }
        tokens = new int[8];
        for (int i = 1; i < 9; i++) {
            tokens[i-1] = addTopImage("images/tokens/" + i + ".png");
        }
        
        playerBgID = addImage("images/p_pane.jpg");
        paneBgID = addImage("images/pane.jpg");
        manageBgID = addImage("images/manage_pane.png");
        
        okID = addImage("images/ok.jpg");
        buyID = addImage("images/buy.jpg");
        auctionID = addImage("images/auction.jpg");
        payID = addImage("images/pay.png");
        roll2ID = addImage("images/roll2.png");
        jail_freeID = addImage("images/jail_free.png");
        manage2ID = addImage("images/manage2.png");
        resignID = addImage("images/resign.png");
        blankID = addImage("images/blank.png");
        
        rolldiceID = addImage("images/roll.jpg");
        manageID = addImage("images/manage.jpg");
        endID = addImage("images/end.jpg");
        
        
        dicePane = makePane();
        cardPane = makePane();
        propPane = makePane();
        payPane = makePane();
        jailChoicePane = makePane();
        managePane = makeBigPane();
        auctionPane = makeBigPane();
        
        // button pane bg...
        ImageInfo imginf = new ImageInfo(gConfig.createCompatibleImage(225, 50), 400, 75);
        topImages.add(imginf);
        buttonPane = imginf.getImgID();
        
        // player pane stuff
        imginf = new ImageInfo(gConfig.createCompatibleImage(220, 220), 21, 150);
        topImages.add(imginf);
        p1Pane = imginf.getImgID();
        imginf = new ImageInfo(gConfig.createCompatibleImage(220, 220), 783, 150);
        topImages.add(imginf);
        p2Pane = imginf.getImgID();
        imginf = new ImageInfo(gConfig.createCompatibleImage(220, 220), 21, 391);
        topImages.add(imginf);
        p3Pane = imginf.getImgID();
        imginf = new ImageInfo(gConfig.createCompatibleImage(220, 220), 783, 391);
        topImages.add(imginf);
        p4Pane = imginf.getImgID();
        imginf = new ImageInfo(gConfig.createCompatibleImage(128, 160), 0, 0);
        topImages.add(imginf);
        blankPane = imginf.getImgID();

    }




    /* ===== Panel Drawing =====================================================================
     * I am sure a lot of these functions repeat code.
     * Much of this needs to be refactored, but it works well enough for the time being.
     * ========================================================================================= */
    public void DrawInitialGame() {
        Show(board_img);
    }

    public void DrawButtonBox(State state) {
        //load buttons and show,
        BufferedImage buttonBox = getImageInfo(buttonPane).getImg();
        Graphics button_g = buttonBox.getGraphics();

        BufferedImage b1 = getImageInfo(rolldiceID).getImg(); 
        BufferedImage b2 = getImageInfo(manageID).getImg(); 
        BufferedImage b3 = getImageInfo(endID).getImg();
        
        if (state != State.ROLL_MANAGE) {
            BufferedImage temp = b1;
            b1 = new BufferedImage(temp.getWidth(), temp.getHeight(), temp.getType());
            Graphics b1_g = b1.getGraphics();
            b1_g.drawImage(temp, 0, 0, null);
            b1_g.dispose();
            ColorConvertOp colorConvert = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
            colorConvert.filter(b1, b1);
        } else if (state != State.MANAGE_END) {
            BufferedImage temp = b3;
            b3 = new BufferedImage(temp.getWidth(), temp.getHeight(), temp.getType());
            Graphics b3_g = b3.getGraphics();
            b3_g.drawImage(temp, 0, 0, null);
            b3_g.dispose();
            ColorConvertOp colorConvert = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
            colorConvert.filter(b3, b3);
        }
        
        button_g.drawImage(b1, 0, 0, null);
        button_g.drawImage(b2, 75, 0, null);
        button_g.drawImage(b3, 150, 0, null);
        button_g.dispose();
        
        Show(buttonPane);
    }

    public void DrawTokens(ArrayList<Player> players) {
        Box b = null;
        for (Player p : players) {
            b = GetBoardXY(p.GetPosition());
            setImageXY(tokens[p.GetToken()], b.x, b.y);
            Show(tokens[p.GetToken()]);
        }
    }

    public void DrawBuyProperty(int propID) {
        //get property image
        BufferedImage propBox = getImageInfo(propPane).getImg();
        Graphics prop_g = propBox.getGraphics();

        BufferedImage pane = getImageInfo(paneBgID).getImg(); 
        BufferedImage prop = getImageInfo(deedFronts[propID]).getImg(); 
        BufferedImage buy = getImageInfo(buyID).getImg();
        BufferedImage auc = getImageInfo(auctionID).getImg();
        
        prop_g.drawImage(pane, 0, 0, null);
        prop_g.drawImage(prop, 75, 10, 250, 325, null);
        prop_g.drawImage(buy, 77, 340, null);
        prop_g.drawImage(auc, 225, 340, null);
        prop_g.dispose();
        
        Show(propPane);
    }

    public void HideBuyProperty() {
        Hide(propPane);
    }

    public void DrawCard(int cardID, String deck) {
        //show the card...
        BufferedImage diceBox = getImageInfo(cardPane).getImg();
        Graphics card_g = diceBox.getGraphics();
        BufferedImage pane = getImageInfo(paneBgID).getImg();
        BufferedImage card = null; 
        if (deck == "comm_chest") {
            card = getImageInfo(commChIDs[cardID]).getImg();
        } else if (deck == "chance") {
            card = getImageInfo(chanceIDs[cardID]).getImg();
        }
        
        BufferedImage ok = getImageInfo(okID).getImg();
        card_g.drawImage(pane, 0, 0, null);
        card_g.drawImage(card, 50, 75, 300, 150, null);
        card_g.drawImage(ok, 150, 250, null);
        card_g.dispose();
        
        Show(cardPane);
    }

    public void DrawRoll(int d1, int d2) {
        //for now show those dice...
        BufferedImage diceBox = getImageInfo(dicePane).getImg();
        Graphics dice_g = diceBox.getGraphics();
        BufferedImage pane = getImageInfo(paneBgID).getImg(); 
        dice_g.drawImage(pane, 0, 0, null);
        BufferedImage d_img = getImageInfo(dice[d1-1]).getImg();
        dice_g.drawImage(d_img, 100, 100, null);
        d_img = getImageInfo(dice[d2-1]).getImg();
        dice_g.drawImage(d_img, 250, 100, null);
        dice_g.drawImage(getImageInfo(okID).getImg(), 150, 250, null);
        dice_g.dispose();
        Show(dicePane);

        //then move player??
        //later TODO... animate dice, animate move
    }


    // Drawing the log is a messy hack right now...
    // There's two buttons to expand/shrink,
    // the log image is recreated from gConfig...
    // the log strings are copied in... it works though
    public void DrawLog(ArrayList<String> log) {
        if (this.extendedLog) {
            Hide(log_longID);
            this.extendedLog = false;
        }
        this.log = log;
        BufferedImage img = gConfig.createCompatibleImage(800, 75);
        Graphics log_g = img.createGraphics();
        log_g.drawImage(getImageInfo(this.logID).getImg(), 0, 0, null);
        if (this.log != null) {
            log_g.setFont(new Font("Dialog", Font.BOLD, 15));
            if (this.log.size() > 2) {
                log_g.drawString(log.get(log.size()-3), 20, 18);
                log_g.drawString(log.get(log.size()-2), 20, 38);
                log_g.drawString(log.get(log.size()-1), 20, 58);
            } else if (this.log.size() == 2) {
                log_g.drawString(log.get(log.size()-2), 20, 18);
                log_g.drawString(log.get(log.size()-1), 20, 38);
            } else if (this.log.size() == 1) {
                log_g.drawString(log.get(log.size()-1), 20, 18);
            }
        }
        this.log_img = img;
        log_g.dispose();
    }
    
    public void DrawLogLong(ArrayList<String> log) {
        if (!this.extendedLog) {
            Hide(logID);
            this.extendedLog = true;
        }
        this.log = log;
        BufferedImage img = gConfig.createCompatibleImage(800, 500);
        Graphics log_g = img.createGraphics();
        log_g.drawImage(getImageInfo(this.log_longID).getImg(), 0, 0, null);
        if (this.log != null) {
            log_g.setFont(new Font("Dialog", Font.BOLD, 15));
            int i;
            int max = log.size();
            if (max > 25) {max = 25;}
            for (i = 0; i < max; i++) {
                log_g.drawString(log.get(log.size()-(max-i)), 20, (i+1)*20 - 5);
            }
        }
        this.log_img = img;
        log_g.dispose();
        //TODO: make scrollable?
    }
    
    private void ToggleLog(int id) {
        if (id == 0 && this.extendedLog == false) {//plus
            DrawLogLong(this.log);
        } else if (id == 1 && this.extendedLog == true) {//minus
            DrawLog(this.log);
        }
    }
    
    public void DrawCardCollect() {
        BufferedImage PayBox = getImageInfo(payPane).getImg();
        Graphics pay_g = PayBox.getGraphics();
        pay_g.drawImage(getImageInfo(paneBgID).getImg(), 0, 0, null);
        pay_g.setFont(new Font("Dialog", Font.BOLD, 25));
        pay_g.drawString("Collect your reward.", 20, 100);
        pay_g.drawImage(getImageInfo(okID).getImg(), 150, 250, null);
        pay_g.dispose();
        Show(payPane);
    }
    
    public void DrawCardPay() {
        BufferedImage PayBox = getImageInfo(payPane).getImg();
        Graphics pay_g = PayBox.getGraphics();
        pay_g.drawImage(getImageInfo(paneBgID).getImg(), 0, 0, null);
        pay_g.setFont(new Font("Dialog", Font.BOLD, 25));
        pay_g.drawString("Pay the penalty.", 20, 100);
        pay_g.drawImage(getImageInfo(okID).getImg(), 150, 250, null);
        pay_g.drawImage(getImageInfo(manage2ID).getImg(), 275, 300, null);
        pay_g.dispose();
        Show(payPane);
    }
    
    public void DrawPayRent(String name) {
        BufferedImage PayBox = getImageInfo(payPane).getImg();
        Graphics pay_g = PayBox.getGraphics();
        pay_g.drawImage(getImageInfo(paneBgID).getImg(), 0, 0, null);
        pay_g.setFont(new Font("Dialog", Font.BOLD, 25));
        pay_g.drawString("Property owned by " + name, 20, 100);
        pay_g.drawImage(getImageInfo(okID).getImg(), 150, 250, null);
        pay_g.drawImage(getImageInfo(manage2ID).getImg(), 275, 300, null);
        pay_g.dispose();
        Show(payPane);
    }
    
    public void DrawPayTax() {
        BufferedImage PayBox = getImageInfo(payPane).getImg();
        Graphics pay_g = PayBox.getGraphics();
        pay_g.drawImage(getImageInfo(paneBgID).getImg(), 0, 0, null);
        pay_g.setFont(new Font("Dialog", Font.BOLD, 25));
        pay_g.drawString("You must pay tax.", 20, 100);
        pay_g.drawImage(getImageInfo(okID).getImg(), 150, 250, null);
        pay_g.drawImage(getImageInfo(manage2ID).getImg(), 275, 300, null);
        pay_g.dispose();
        Show(payPane);
    }
    
    public void DrawAssessed(int amount) {
        BufferedImage PayBox = getImageInfo(payPane).getImg();
        Graphics pay_g = PayBox.getGraphics();
        pay_g.drawImage(getImageInfo(paneBgID).getImg(), 0, 0, null);
        pay_g.setFont(new Font("Dialog", Font.BOLD, 25));
        pay_g.drawString("You have been assessed", 20, 80);
        pay_g.drawString("for $" + amount, 60, 110);
        pay_g.drawImage(getImageInfo(okID).getImg(), 150, 250, null);
        pay_g.drawImage(getImageInfo(manage2ID).getImg(), 275, 300, null);
        pay_g.dispose();
        Show(payPane);
    }
    
    public void DrawToJail() {
        BufferedImage PayBox = getImageInfo(payPane).getImg();
        Graphics pay_g = PayBox.getGraphics();
        pay_g.drawImage(getImageInfo(paneBgID).getImg(), 0, 0, null);
        pay_g.setFont(new Font("Dialog", Font.BOLD, 25));
        pay_g.drawString("Go To Jail", 100, 70);
        pay_g.drawString("Do not pass go.", 80, 100);
        pay_g.drawString("Do not collect $200.", 70, 130);
        pay_g.drawImage(getImageInfo(okID).getImg(), 150, 250, null);
        pay_g.dispose();
        Show(payPane);
    }
    
    public void DrawManage() {
        BufferedImage ManageBox = getImageInfo(managePane).getImg();
        Graphics manage_g = ManageBox.getGraphics();
        
        // TODO: draw properties...
        
        manage_g.drawImage(getImageInfo(manageBgID).getImg(), 0, 0, null);
        manage_g.drawImage(getImageInfo(okID).getImg(), 350, 350, null);
        manage_g.dispose();
        Show(managePane);
    }
    
    public void DrawManagePay() {
        BufferedImage ManageBox = getImageInfo(managePane).getImg();
        Graphics manage_g = ManageBox.getGraphics();
        manage_g.drawImage(getImageInfo(manageBgID).getImg(), 0, 0, null);
        manage_g.drawImage(getImageInfo(okID).getImg(), 350, 350, null);
        manage_g.dispose();
        Show(managePane);
    }
    
    public void HidePane() {
        Hide(jailChoicePane);
        Hide(payPane);
        Hide(cardPane);
        Hide(dicePane);
        Hide(propPane);
    }
    
    public void HideTopPane() {
        Hide(managePane);
        Hide(auctionPane);
    }
    
    public void DrawAuction(int deed_num) {
        BufferedImage ManageBox = getImageInfo(auctionPane).getImg();
        Graphics manage_g = ManageBox.getGraphics();
        manage_g.drawImage(getImageInfo(manageBgID).getImg(), 0, 0, null);
        BufferedImage prop = getImageInfo(deedFronts[deed_num]).getImg();
        manage_g.drawImage(prop, 300, 25, 250, 325, null);
        manage_g.drawImage(getImageInfo(okID).getImg(), 350, 350, null);
        manage_g.dispose();
        Show(auctionPane);
    }
    
    public void DrawJailChoice() {
        BufferedImage JailBox = getImageInfo(jailChoicePane).getImg();
        Graphics jail_g = JailBox.getGraphics();
        jail_g.drawImage(getImageInfo(paneBgID).getImg(), 0, 0, null);
        jail_g.setFont(new Font("Dialog", Font.BOLD, 25));
        jail_g.drawString("You are in jail:", 20, 100);
        jail_g.drawImage(getImageInfo(payID).getImg(), 150, 250, null);
        jail_g.drawImage(getImageInfo(roll2ID).getImg(), 150, 300, null);
        jail_g.drawImage(getImageInfo(jail_freeID).getImg(), 275, 300, null);
        jail_g.dispose();
        Show(jailChoicePane);
    }
    
    public void DrawPlayerPanel(Player p) {
        int paneBgID = 0;
        switch(p.GetPnum()) {
            case 1:
                paneBgID = p1Pane;
                break;
            case 2:
                paneBgID = p2Pane;
                break;
            case 3:
                paneBgID = p3Pane;
                break;
            case 4:
                paneBgID = p4Pane;
                break;
        }
        boolean jail_card1 = p.HasCommChestJailcard();
        boolean jail_card2 = p.HasChanceJailcard();
        BufferedImage pane = getImageInfo(paneBgID).getImg(); 
        Graphics pane_g = pane.getGraphics();
        BufferedImage pane_BG = getImageInfo(playerBgID).getImg();
        pane_g.drawImage(pane_BG, 0, 0, null);
        pane_g.setFont(new Font("Dialog", Font.BOLD, 14));
        pane_g.drawString(p.GetName(), 20, 25);
        pane_g.drawString("$" + p.GetMoney(), 20, 45);
        
        //cards
        if (jail_card1) {
            pane_g.drawImage(getImageInfo(commChIDs[2]).getImg(), 120, 25, 40, 20, null);
        }
        if (jail_card2) {
            pane_g.drawImage(getImageInfo(chanceIDs[14]).getImg(), 160, 25, 40, 20, null);
        }
        
        //for deed image, draw in player pane...
        //    draw deed groups overlayed...
        //BufferedImage img = getImageInfo(id).getImg();
        //pane_g.drawImage(img, x, y, null);
        if (p.GetDeedList().size() > 0) {
            p.Sort();
            ArrayList<Integer> deeds = new ArrayList<Integer>(p.GetDeedList());
            int prop, y2, y3;
            ArrayList<Integer> group;
            
            Box[] spots = { new Box(20,60,0,0), new Box(57,60,0,0), new Box(94,60,0,0),
                    new Box(131,60,0,0), new Box(168,60,0,0), new Box(20,135,0,0), new Box(57,135,0,0),
                    new Box(94,135,0,0), new Box(131,135,0,0), new Box(168,135,0,0) };

            int spotter = 0;
            while (deeds.size() > 0) {
                prop = deeds.get(0);
                deeds.remove(0);

                BufferedImage blank = getImageInfo(blankPane).getImg(); 
                Graphics blank_g = blank.getGraphics();
                //TODO: if mortgaged, show back...
                blank_g.drawImage(getImageInfo(deedFronts[prop]).getImg(), 0, 0, 128, 160, null);
                pane_g.drawImage(blank, spots[spotter].x, spots[spotter].y, 32, 40, null);
                
                group = new ArrayList<Integer>(GetGroup(prop));
                if (group.size() == 4) {
                    y2 = 10;
                } else {
                    y2 = 15;
                }
                y3 = y2;
                for (Integer i : group) {
                    if (deeds.contains(i)) {
                        deeds.remove(new Integer(i));//ensure remove(Integer) and not remove(int)...
                        blank_g.drawImage(getImageInfo(deedFronts[prop]).getImg(), 0, 0, 128, 160, null);
                        pane_g.drawImage(blank, spots[spotter].x, spots[spotter].y + y3, 32, 40, null);
                        y3 += y2;
                    }
                }
                spotter++;
            }
        }
        
        pane_g.dispose();
        Show(paneBgID);
    }
    
    private List<Integer> GetGroup(int deedNum) {
        if (deedNum < 0) {
            return null;
        } else if (deedNum < 2) {
            return Arrays.asList(0,1);//Brown
        } else if (deedNum < 5) {
            return Arrays.asList(2,3,4);//Blue
        } else if (deedNum < 8) {
            return Arrays.asList(5,6,7);//Magenta
        } else if (deedNum < 11) {
            return Arrays.asList(8,9,10);//Orange
        } else if (deedNum < 14) {
            return Arrays.asList(11,12,13);//Red
        } else if (deedNum < 17) {
            return Arrays.asList(14,15,16);//Yellow
        } else if (deedNum < 20) {
            return Arrays.asList(17,18,19);//Green
        } else if (deedNum < 22) {
            return Arrays.asList(20,21,22);//Navy
        } else if (deedNum < 24) {
            return Arrays.asList(23,24);//Util
        } else if (deedNum < 28) {
            return Arrays.asList(25,26,27,28);//Rail
        } else {
            return null;
        }
    }


    private Box GetBoardXY(int pos) { //TODO: numbers...
        switch (pos) {
            case 0: return new Box(712,618,0,0);
            case 1: return new Box(662,618,0,0);
            case 2: return new Box(621,618,0,0);
            case 3: return new Box(580,618,0,0);
            case 4: return new Box(538,618,0,0);
            case 5: return new Box(497,618,0,0);
            case 6: return new Box(456,618,0,0);
            case 7: return new Box(414,618,0,0);
            case 8: return new Box(373,618,0,0);
            case 9: return new Box(332,618,0,0);
            case 10: return new Box(282,618,0,0);
            case 11: return new Box(282,568,0,0);
            case 12: return new Box(282,528,0,0);
            case 13: return new Box(282,486,0,0);
            case 14: return new Box(282,444,0,0);
            case 15: return new Box(282,403,0,0);
            case 16: return new Box(282,362,0,0);
            case 17: return new Box(282,320,0,0);
            case 18: return new Box(282,280,0,0);
            case 19: return new Box(282,238,0,0);
            case 20: return new Box(282,188,0,0);
            case 21: return new Box(332,188,0,0);
            case 22: return new Box(373,188,0,0);
            case 23: return new Box(414,188,0,0);
            case 24: return new Box(456,188,0,0);
            case 25: return new Box(497,188,0,0);
            case 26: return new Box(538,188,0,0);
            case 27: return new Box(580,188,0,0);
            case 28: return new Box(621,188,0,0);
            case 29: return new Box(662,188,0,0);
            case 30: return new Box(712,188,0,0);
            case 31: return new Box(712,238,0,0);
            case 32: return new Box(712,280,0,0);
            case 33: return new Box(712,320,0,0);
            case 34: return new Box(712,362,0,0);
            case 35: return new Box(712,404,0,0);
            case 36: return new Box(712,444,0,0);
            case 37: return new Box(712,486,0,0);
            case 38: return new Box(712,528,0,0);
            case 39: return new Box(712,568,0,0);
            default:
                return null;
        }
    }

    /* ===== Class: Images =====================================================================
     * ========================================================================================= */
    public int makePane() {
        BufferedImage img = gConfig.createCompatibleImage(400, 400);
        ImageInfo i = new ImageInfo(img, 312, 200);
        topImages.add(i);
        return i.getImgID();
    }
    
    public int makeBigPane() {
        BufferedImage img = gConfig.createCompatibleImage(800, 500);
        ImageInfo i = new ImageInfo(img, 112, 100);
        top2Images.add(i);
        return i.getImgID();
    }
    
    public int addImage(String path) {
        ImageInfo i = new ImageInfo(loadImage(path), 0, 0);
        images.add(i);
        return i.getImgID();
    }

    public int addImage(String path, int x, int y) {
        ImageInfo i = new ImageInfo(loadImage(path), x, y);
        images.add(i);
        //System.out.format("%d\n", i.getImgID());
        return i.getImgID();
    }

    public int addTopImage(String path) {
        ImageInfo i = new ImageInfo(loadImage(path), 0, 0);
        topImages.add(i);
        return i.getImgID();
    }

    public int addTopImage(String path, int x, int y) {
        ImageInfo i = new ImageInfo(loadImage(path), x, y);
        topImages.add(i);
        //System.out.format("%d\n", i.getImgID());
        return i.getImgID();
    }

    public void setImageXY(int imgID, int x, int y) {
        ImageInfo i = getImageInfo(imgID);
        i.setx(x);
        i.sety(y);
    }

    public void Show(int imgID) {
        ImageInfo i = getImageInfo(imgID);
        i.setVisible(true);
    }

    public void Hide(int imgID) {
        ImageInfo i = getImageInfo(imgID);
        i.setVisible(false);
    }


    private ImageInfo getImageInfo(int id) {
        for (ImageInfo imgInfo : this.images) {
            if (imgInfo.getImgID() == id) {
                return imgInfo;
            }
        }
        for (ImageInfo imgInfo : this.topImages) {
            if (imgInfo.getImgID() == id) {
                return imgInfo;
            }
        }
        for (ImageInfo imgInfo : this.top2Images) {
            if (imgInfo.getImgID() == id) {
                return imgInfo;
            }
        }
        return null;
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


    /* ===== Rendering =========================================================================
     * 
     * ========================================================================================= */

    public void start() {
        this.keepRendering = true;
        this.t = System.currentTimeMillis();
        this.renderThread.start();

        System.out.println("gui start");
    }

    public void stop() {
        keepRendering = false;
    }
    
    private void renderBlack() {
        G = this.getGraphics();

        G.setColor(Color.BLACK);
        G.fillRect(0, 0, 1024, 768);

        if (this.G != null) {
            this.G.dispose();
        }
    }

    private void render()
    {
        try
        {
            //System.out.println(".");
            G2D = this.backBuffer.createGraphics();
            G2D.drawImage(this.background, 0, 0, null);

            for (ImageInfo i : this.images) {
                if (i.visible()) {
                    G2D.drawImage(i.getImg(), i.getx(), i.gety(), null);
                }
            }

            for (ImageInfo i : this.topImages) {
                if (i.visible()) {
                    G2D.drawImage(i.getImg(), i.getx(), i.gety(), null);
                }
            }
            
            for (ImageInfo i : this.top2Images) {
                if (i.visible()) {
                    G2D.drawImage(i.getImg(), i.getx(), i.gety(), null);
                }
            }
            
            G2D.drawImage(log_img, 112, 0, null);

            //Draw FPS:
            long newt = System.currentTimeMillis();
            String fps = Long.toString(1000 / (newt-t));
            t = newt;
            G2D.drawString("FPS: " + fps, 20, 30);

            // Show Back Buffer
            G = this.getGraphics();
            if (this.backBuffer != null) {
                G.drawImage(this.backBuffer, 0, 0, 1024, 768, null);
            } else {
                System.out.println("backBuffer is null...");
            }
        }
        finally
        {
            if(G2D != null)
            {
                G2D.dispose();
            }
            if(G != null)
            {
                G.dispose();
            }
        }
    }


    private class Renderer implements Runnable
    {
        private long system_time;
        private final long TARGET_LOOP_TIME = 50; //20 FPS
        private long SLEEP_TIME = 25;
        private List<Long> loop_times;
        private final int num_loops_for_avg = 5;

        @Override
        public void run()
        {
            system_time = System.currentTimeMillis();
            loop_times = new ArrayList<Long>();
            for (int i = 0; i < num_loops_for_avg; i++) {
                loop_times.add(SLEEP_TIME);
            }

            System.out.println("game render start");
            while(keepRendering)
            {
                render();
                adjust_sleep();
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException ignore) {}
            }
            renderBlack();
            System.out.println(Thread.currentThread().getName() + "Ended");
        }

        private void adjust_sleep() {
            long new_time = System.currentTimeMillis();
            long loop_time = new_time - system_time;
            system_time = new_time;
            loop_times.remove(0);
            loop_times.add(loop_time);
            long avg_loop_time = 0;
            for (int i = 0; i < num_loops_for_avg; i++) {
                avg_loop_time += loop_times.get(i);
            }
            avg_loop_time /= num_loops_for_avg;
            if (avg_loop_time > TARGET_LOOP_TIME + 1 && SLEEP_TIME > 0) {
                SLEEP_TIME--;
            } else if (avg_loop_time < TARGET_LOOP_TIME - 1) {
                SLEEP_TIME++;
            }
        }
    }
    
    private class LogToggle extends JComponent
    {
        public LogToggle(int x, int y, int id) {
            this.setIgnoreRepaint(true);
            this.setBounds(x, y, 30, 30);
            this.addMouseListener(new roller(id));
            
        }

        private class roller extends MouseAdapter {
            int id;
            public roller(int id) {
                super();
                this.id = id;
            }
            @Override
            public void mouseClicked(MouseEvent arg0) {
                ToggleLog(this.id);
            }
        }
    }
}
