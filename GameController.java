
/* GameController Class
 * Last Modified: 06 May 2015  
 * Author(s): Phillip Stewart, Lap Nguyen
 * Description:
 *   This is the Game Controller.
 *   It acts as a manager between the Game model and the GUI view.
 *   All cross-talk should go through this class.
 *   
 *   The buttons for the GUI are actually created in this class and assigned to
 *   the GUI panel so that their associated functions have easier access to
 *   the execution of their related actions.
 * 
 * 5/1/15 - Placed the AI on a timer scheduling object to
 * stop AI from freezing the GUI,or could just place the AI
 * on a separate thread as well,recursive calls for ai deleted.
 * 4/30/15 - Rough Implementation of basic AI, stubs for some clickers.
 */

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.Timer;

enum G_Button {AUCTION, BUY, END_TURN, INFO, MANAGE, MANAGE2, MENU, OK, ROLL, ROLL2}

public class GameController implements ActionListener {

    private static final boolean DEBUG = true;

    //private Launcher MainController;
    private Game game;
    public GUI gui; //TODO: make private, and fix link in Launcher
    private State gameState;
    private Random rng;
    private ArrayList<Box> button_boxes;
    private ArrayList<G_ClickBox> buttons;
    private int bid; //?? for getting input?
    private Timer timer;


    public GameController(Launcher l, GameSave gs, Dimension d) {
        //this.MainController = l;
        this.game = new Game(gs);
        this.gui = new GUI(l.gConfig, d);
        this.rng = new Random();
        setButtons();
    }

    public void start() {
        this.gui.start();
        gui.DrawInitialGame();
        for (Player p : game.GetPlayers()) {
            gui.DrawPlayerPanel(p);
        }
        gui.DrawTokens(game.GetPlayers());
        gui.DrawLog(game.GetLog());
        gameState = State.ROLL_MANAGE;
        gui.DrawButtonBox(gameState);
        timer = new Timer(1000, this);
        this.timer.start();
    }

    public void stop() {
        this.gui.stop();
    }

    //{PAY_MANAGE, BUY_MANAGE, AUCTION}
    private void AIAction(){
        switch(gameState){
            case ROLL_MANAGE:
                RollClicked();
                break;
            case JAIL_CHOICE:
                //TODO: Choices 
                JailRollClicked();
                //JailCard();
                //JailPay();
                break;
            case ROLL_OK:
            case GO_OK:
            case CARD_OK:
            case TO_JAIL_OK:
            case PAY_RENT_OK:
            case PAY_DOUBLERAIL_OK:
            case PAY_MAXUTIL_OK:
            case TAX_OK:
            case ASSESS_OK:
            case CARD_PAY_OK:
            case CARD_COLLECT_OK:
            case CARD_PAY_ALL_OK:
            case CARD_COLLECT_ALL_OK:
                OkClicked();
                break;
            case BUY_AUCTION://TODO: update for buy_manage
                if(game.GetCurrentPlayer().GetMoney() > game.GetDeedPrice())
                    BuyClicked();
                else
                    AuctionClicked();
                break;
            case MANAGE_END:
                EndTurnClicked();
                break;
            case GAMEOVER:
                timer.stop();
            default:
                break;
        }
        try{Thread.sleep(100);}catch(InterruptedException ignore){}
    }
    private void AIPayManageCheck(){
        ArrayList<Integer> property = game.SellAssests();
        while(!property.isEmpty()){
            int prop = game.LowestMortgage(property);
            gameState = game.ManageSelect(prop);
            MortgageClicked();
            property.remove(property.indexOf(prop));
            if(game.GetPayer().GetMoney()>=game.GetOwed()){
                break;
            }
            try{Thread.sleep(100);}catch(InterruptedException ignore){}
        }
        gameState = State.PAY_MANAGE;
        if(game.GetPayer().GetMoney()>game.GetOwed()){
            //	PayClicked(); OK...TODO
        }else{
            ResignClicked();
        }
        try{Thread.sleep(100);}catch(InterruptedException ignore){}
    }
    private void AIAuctionCheck(){	
        if(game.GetHighBid() >= game.GetAuctionPrice() || game.GetHighBid()>game.GetCurrentBidder().GetMoney()){
            PassClicked();
        }
        else{
            bid = rng.nextInt(game.GetAuctionPrice())+(game.GetHighBid());
            if(bid > game.GetCurrentBidder().GetMoney())
                PassClicked();
            else
                BidClicked();
        }
        try{Thread.sleep(100);}catch(InterruptedException ignore){}

    }

    /* ==== Buttons ===========================================================
     * ======================================================================== */
    private void RollClicked() {
        if (DEBUG) System.out.println("Roll clicked");
        if (gameState != State.ROLL_MANAGE) {
            return;
        }

        gameState = game.Roll();
        gui.DrawButtonBox(gameState);
        if (DEBUG) {System.out.println(game.GetLastLog());}
        gui.DrawLog(game.GetLog());

        if (gameState != State.ROLL_OK) {
            System.err.println("Expected 'ROLL_OK' state.");
            System.exit(1);
        }

        Dice dice = game.GetDice();
        gui.DrawRoll(dice.GetD1(), dice.GetD2());
    }

    private void ManageClicked() {
        if (DEBUG) System.out.println("Clicked manage");
        if (!(gameState == State.ROLL_MANAGE || gameState == State.MANAGE_END)) {
            return;
        }
        gui.DrawManage();
        gameState = game.Manage();
    }

    private void MortgageClicked(){
        gameState = game.Mortgage();
        if (DEBUG) {System.out.println(game.GetLastLog());}
    }

    // TODO: finish selling in GUI and link up here
    //    private void SellClicked(){
    //    	gameState = game.Sell();
    //        if (DEBUG) {System.out.println(game.GetLastLog());}
    //    }
    //    private void UnMortgageClicked(){
    //    	gameState = game.UnMortgage();
    //        if (DEBUG) {System.out.println(game.GetLastLog());}
    //    }
    //    private void ImproveClicked(){
    //    	gameState = game.Improve();
    //        if (DEBUG) {System.out.println(game.GetLastLog());}
    //    }
    //    private void ReturnFromManage(){
    //    	gameState = game.ReturnFromManage();//this could just be an ok state...
    //    }
    //    private void ReturnFromSelect(){
    //    	gameState = game.ReturnFromSelect();//same with this possibly
    //    }

    private void AltOptionClicked() {
        //clicked on pane, Manage(pay) or UseJailCard...
        if (gameState == State.JAIL_CHOICE) {
            if (game.GetCurrentPlayer().HasCommChestJailcard()
                    || game.GetCurrentPlayer().HasChanceJailcard()) {
                gameState = game.JailCard();
                gui.HidePane();
            }
        } else if (gameState == State.PAY_MANAGE) {
            gui.DrawManagePay();
            //TODO: amnagement of properties... waiting on GUI implementation
        }
        if (DEBUG) {System.out.println(game.GetLastLog());}
        gui.DrawLog(game.GetLog());
        gui.DrawTokens(game.GetPlayers());
        gui.DrawButtonBox(gameState);
    }
    
    //===============Buy/ Buy Manage / Auction /Bid / Pass=======================================
    //===========================================================================================
    private void BuyClicked() {
        if (DEBUG) System.out.println("Buy clicked");
        if (gameState != State.BUY_AUCTION) {
            return;
        }
        gameState = game.Buy();
        gui.DrawButtonBox(gameState);
        gui.HideBuyProperty();
        if (DEBUG) {System.out.println(game.GetLastLog());}
        gui.DrawLog(game.GetLog());

        if (gameState == State.BUY_MANAGE) {
            gui.DrawManage();
        } else {
            gui.DrawPlayerPanel(game.GetCurrentPlayer());
        }
    }

    private void AuctionClicked() {
        if (DEBUG) System.out.println("Clicked auction");
        if (gameState != State.BUY_AUCTION) {
            return;
        }
        gui.HidePane();
        bid = 0;
        gameState = game.Auction();
        gui.DrawAuction(game.GetAuctionDeed());
        if (DEBUG) {System.out.println(game.GetLastLog());}
        gui.DrawLog(game.GetLog());
    }

    private void BidClicked(){
        if (DEBUG) System.out.println("Clicked Bid");
        if (gameState != State.AUCTION) {
            return;
        }
        //get player input somehow.
        if(bid < game.GetHighBid())
            return;

        gameState = game.Bid(bid);
        //draw auction bid?
        bid = 0;
    }
    private void PassClicked(){
        if (DEBUG) System.out.println("Clicked Passed");
        if (gameState != State.AUCTION) {
            return;
        }
        gameState = game.Pass();
    }
    private void EndTurnClicked() {
        if (DEBUG) System.out.println("Clicked end turn");
        if (gameState != State.MANAGE_END) {
            return;
        }

        gameState = game.EndTurn();
        gui.DrawButtonBox(gameState);
        if (DEBUG) {System.out.println(game.GetLastLog());}
        gui.DrawLog(game.GetLog());

        if (gameState == State.JAIL_CHOICE) {
            gui.DrawJailChoice();
        }
        //TODO: highlight the current player somehow.
    }

    private void OkClicked() {
        if (DEBUG) System.out.println("OK clicked");
        switch (gameState) {
            case ROLL_OK:
                //TODO: show player moving
                AfterRollOk();
                break;
            case GO_OK:
                gameState = game.Ok();
                // TODO: verify GO func...
                // add option for double money on GO...
                break;
            case CARD_OK:
                AfterCardOk();
                break;
            case TO_JAIL_OK:
                gameState = game.Ok();
                gui.HidePane();
                break;
            case JAIL_CHOICE:
                gameState = game.JailPay();
                gui.HidePane();
                if (gameState == State.PAY_MANAGE) {
                    gui.DrawManagePay();
                }
                break;
            case PAY_RENT_OK:
            case PAY_DOUBLERAIL_OK:
            case PAY_MAXUTIL_OK:
                AfterPayOk();
                break;
            case TAX_OK:
                AfterPayOk();
                break;
            case ASSESS_OK:
                gameState = game.Ok();
                gui.HidePane();
                break;
            case CARD_PAY_OK:
            case CARD_PAY_ALL_OK:
                gameState = game.Ok();
                gui.HidePane();
                break;
            case CARD_COLLECT_OK:
            case CARD_COLLECT_ALL_OK:
                gameState = game.Ok();
                gui.HidePane();
                break;
            case MANAGE:
                gui.HideTopPane();
                gameState = game.ReturnFromManage();
                break;
            case AUCTION:
                //////////////////////////
                //TODO: fix this, not correct at all...
                gui.HideTopPane();
                gameState = game.Pass();
                break;
                //////////////////////////
            default:
                return;
        }
        if (DEBUG) {System.out.println(game.GetLastLog());}
        gui.DrawLog(game.GetLog());
        gui.DrawTokens(game.GetPlayers());
        gui.DrawButtonBox(gameState);
        for (Player p : game.GetPlayers()) {
            gui.DrawPlayerPanel(p);
        }
    }

    private void AfterRollOk() {
        gameState = game.Ok();
        gui.HidePane();
        switch(gameState) {
            case ROLL_MANAGE: // Rolled doubles
            case MANAGE_END: // Standard roll
                break;
            case PAY_MANAGE:
                //rolling to get out of jail, after 3 tries...
                gui.DrawManagePay();
                break;
            case TO_JAIL_OK:
                //Rolled 3 doubles
                gui.DrawToJail();
                break;
            case BUY_AUCTION:
                gui.DrawBuyProperty(game.GetBuyDeed());
                break;
            case PAY_RENT_OK:
                gui.DrawPayRent(game.GetPayees());
                break;
            case CARD_OK:
                gui.DrawCard(game.GetCardID(), game.GetDeck());
                break;
            case TAX_OK:
                gui.DrawPayTax();
                break;
            default:
                break;
        }
    }

    private void AfterCardOk() {
        gameState = game.Ok();
        gui.HidePane();
        switch(gameState) {
            case ROLL_MANAGE: // Rolled doubles
            case MANAGE_END: // Standard roll
                gui.DrawTokens(game.GetPlayers());
                break;
            case BUY_AUCTION:
                gui.DrawBuyProperty(game.GetBuyDeed());
                break;
            case PAY_DOUBLERAIL_OK:
            case PAY_MAXUTIL_OK:
                gui.DrawPayRent(game.GetPayees());
                break;
            case TO_JAIL_OK:
                //Rolled 3 doubles
                gui.DrawToJail();
                break;
            case PAY_MANAGE:
                gui.DrawManagePay();
                break;
            case CARD_COLLECT_OK:
            case CARD_COLLECT_ALL_OK:
                gui.DrawCardCollect();
                break;
            case CARD_PAY_OK:
            case CARD_PAY_ALL_OK:
                gui.DrawCardPay();
                break;
            case ASSESS_OK:
                gui.DrawAssessed(0);
                break;
            default:
                break;
        }
    }

    private void AfterPayOk() {
        gameState = game.Ok();
        gui.HidePane();
        switch (gameState) {
            case ROLL_MANAGE:
            case MANAGE_END:
                gui.HidePane();
                break;
            case PAY_MANAGE:
                gui.DrawManagePay();
                break;
            default:
                break;
        }
        if (DEBUG) {System.out.println(game.GetLastLog());}
        gui.DrawLog(game.GetLog());
        gui.DrawTokens(game.GetPlayers());
        gui.DrawButtonBox(gameState);
    }

    private void MenuClicked() {
        if (DEBUG) System.out.println("Clicked menu");
        //TODO: open menu regardless of state...
        //waiting on GUI
    }

    private void InfoClicked() {
        if (DEBUG) System.out.println("Clicked info");
        // allow info to be displayed??
        // not sure about this one...
        // TODO: design GUI info option...
    }

    private void ResignClicked(){
        if(gameState != State.PAY_MANAGE)
            return;
        gameState = game.AdmitDefeat();
        if(gameState == State.GAMEOVER){
            System.out.println("Gameover");//game over draw?
        }else if(gameState == State.PAY_MANAGE){
            //redraw for next person
        }else{

        }
    }
    private void JailRollClicked() {
        if (gameState != State.JAIL_CHOICE) {
            return;
        }
        gameState = game.JailRoll();
        if (gameState == State.MANAGE_END) {// successful roll
            if (DEBUG) {System.out.println(game.GetLastLog());}
            gui.DrawLog(game.GetLog());
            gui.HidePane();
            gui.DrawTokens(game.GetPlayers());
        } else if (gameState == State.PAY_MANAGE) {
            if (DEBUG) {System.out.println(game.GetLastLog());}
            gui.DrawLog(game.GetLog());
            gui.DrawManagePay();
        }
        gui.DrawButtonBox(gameState);
        for (Player p : game.GetPlayers()) {
            gui.DrawPlayerPanel(p);
        }
    }

    /* ===== Setting Buttons ==================================================
     * ======================================================================== */
    private void setButtons() {
        // There may be a much better way to do this, but it works...
        // TODO: Fix x, y, w, h magic numbers...

        button_boxes = new ArrayList<Box>(); // Box(x,y,w,h)
        //{AUCTION, BUY, END_TURN, INFO, MANAGE, MANAGE2, MENU, OK, ROLL, ROLL2}
        button_boxes.add(new Box(537, 540, 100, 50)); //AUCTION button
        button_boxes.add(new Box(389, 540, 100, 50)); //BUY button
        button_boxes.add(new Box(550, 75, 75, 100));  //END_TURN button
        button_boxes.add(new Box(924, 0, 100, 50));   //INFO button
        button_boxes.add(new Box(475, 75, 75, 50));   //MANAGE button
        button_boxes.add(new Box(587, 500, 100, 50)); //MANAGE2 & JAIL_CARD...
        button_boxes.add(new Box(0, 0, 100, 50));     //MENU button
        button_boxes.add(new Box(462, 450, 100, 50)); //OKAY button
        button_boxes.add(new Box(400, 75, 75, 50));   //ROLL button
        button_boxes.add(new Box(462, 500, 100, 50)); //ROLL2 button

        //add click listeners to each button box
        buttons = new ArrayList<G_ClickBox>();
        for (G_Button gb : G_Button.values()) {
            buttons.add(new G_ClickBox(gb, button_boxes.get(gb.ordinal())));
        }

        for (G_ClickBox cb : buttons) {
            this.gui.add(cb); 
        }
    }

    public void removeButtons() {
        for (G_ClickBox cb : buttons) {
            this.gui.remove(cb);
        }
        buttons = null;
    }

    /* ===== Class: G_ClickBox =================================================================== */
    @SuppressWarnings("serial")
    private class G_ClickBox extends JComponent
    {
        //{AUCTION, BUY, END_TURN, INFO, MANAGE, MANAGE2, MENU, OK, ROLL, ROLL2}
        public G_ClickBox(G_Button type, Box b) {
            this.setIgnoreRepaint(true);
            setBounds(b.x, b.y, b.w, b.h);
            switch (type) {
                case ROLL:      this.addMouseListener(new roller());    break;
                case BUY:       this.addMouseListener(new buyer());     break;
                case AUCTION:   this.addMouseListener(new auctioner()); break;
                case MANAGE:    this.addMouseListener(new manager());   break;
                case OK:        this.addMouseListener(new okayer());    break;
                case END_TURN:  this.addMouseListener(new ender());     break;
                case MENU:      this.addMouseListener(new menuer());    break;
                case INFO:      this.addMouseListener(new infoer());    break;
                case MANAGE2:   this.addMouseListener(new alt_opt());   break;
                case ROLL2:     this.addMouseListener(new roll2er());   break;
                default: break;
            }
        }

        private class roller extends MouseAdapter {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                RollClicked();
            }
        }
        private class buyer extends MouseAdapter {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                BuyClicked();
            }
        }
        private class auctioner extends MouseAdapter {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                AuctionClicked();
            }
        }
        private class manager extends MouseAdapter {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                ManageClicked();
            }
        }
        private class okayer extends MouseAdapter {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                OkClicked();
            }
        }
        private class ender extends MouseAdapter {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                EndTurnClicked();
            }
        }
        private class menuer extends MouseAdapter {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                MenuClicked();
            }
        }
        private class infoer extends MouseAdapter {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                InfoClicked();
            }
        }
        private class alt_opt extends MouseAdapter {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                AltOptionClicked();
            }
        }
        private class roll2er extends MouseAdapter {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                JailRollClicked();
            }
        }
    }// end of ClickBox

    @Override
    public void actionPerformed(ActionEvent e) {
        if(gameState == State.PAY_MANAGE && game.GetPayer()!=null){
            if(game.GetPayer().IsAI())
                AIPayManageCheck();
        }else if(gameState == State.AUCTION && game.GetCurrentBidder()!=null){
            if(game.GetCurrentBidder().IsAI())
                AIAuctionCheck();
        }else if(game.GetCurrentPlayer().IsAI())
            AIAction();
    }

}
