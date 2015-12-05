
/* Game Class
 * Last Modified: 06 May 2015  
 * Author(s): Lap Nguyen, Phillip Stewart
 * Description:
 *   This is the game model.
 *   It tracks all data of the game, including players and cards...
 *   It contains all methods for updating the game state,
 *   and has a public API that the GameController class calls.
 *   
 * 
 * 5/1/15 added logs to management functions
 * 4/30/15 Added helper functions/Getters to find possible actions for the AI.
 */

import java.util.ArrayList;

enum State {ROLL_MANAGE, MANAGE_END, BUY_AUCTION, JAIL_CHOICE, PAY_MANAGE, BUY_MANAGE, 
    ROLL_OK, CARD_OK, TO_JAIL_OK, PAY_RENT_OK, PAY_DOUBLERAIL_OK, PAY_MAXUTIL_OK, GO_OK, TAX_OK, ASSESS_OK,
    CARD_PAY_OK, CARD_COLLECT_OK, CARD_PAY_ALL_OK, CARD_COLLECT_ALL_OK, 
    AUCTION,AUCTION_OK, MANAGE, INFO, MORTGAGE, MANAGE_SELECT, GAMEOVER}

public class Game{

    private static final boolean TEST = false;

    /* ===== Private Members ==================================================
     * ======================================================================== */
    // Players
    private int numPlayers;
    private ArrayList<Player> playerList;
    private Player currentPlayer;

    // Board
    private Board board;
    private Dice dice;
    private int numDoubles; 
    private int houseCount;
    private int hotelCount;

    // Saveable members
    private Deeds deeds;
    private Chance chance;
    private CommChest commChest;
    private ArrayList<Integer> chanceOrder;
    private ArrayList<Integer> commChestOrder;
    private ArrayList<String> log;
    private boolean doneRolling;

    // Game state
    private State state;

    // Supplemental information
    int PaidAmount;
    Player Payee;
    Player Payer;
    Card card;
    String cardDeck;
    boolean doubleSalary;

    // If player cannot pay...
    private int amountOwed;
    private boolean oweEveryone;
    private Player lienholder;
    private ArrayList<Player> debtPlayers;

    // Auction
    private ArrayList<Player> Bidders;
    private int AuctionDeed;
    private Player CurrentBidder;
    private Player HighBidder;
    private int HighBid;
    private int managingDeed;

    /* ===== Constructor ======================================================
     * ======================================================================== */
    public Game(GameSave gs){
        LoadGame(gs);
        state = State.ROLL_MANAGE;
        this.chance = new Chance();
        this.commChest = new CommChest();
        // If new game:
        log.add("Welcome to monopoly!");
        log.add("It is " + currentPlayer.GetName() + "'s turn.");

        if (TEST) {
            ArrayList<String> prev_log = log;
            log = new ArrayList<String>();
            TestPayRent();
            TestBuyProperty();
            log = prev_log;
        }
    }

    /* ===== Glass-box tests ==================================================
     * ======================================================================== */
    private void TestPayRent(){
        // Save initial state info:
        Player tester = playerList.get(0);
        int start_pos = tester.GetPosition();
        int[] moneys = new int[numPlayers];
        int i = 0;
        for (Player p : playerList) {
            moneys[i] = p.GetMoney();
            p.SetMoney(1500);
            i++;
        }
        int prev_houses = deeds.GetHouses(0);
        deeds.SetHouses(0, 0);
        State st = this.state;
        this.state = State.PAY_MANAGE;

        // Run tests:
        System.out.println("Running 'PayRent()' linearly independent path testing...");
        tester.SetPosition(0);
        PayRent();
        assert tester.GetMoney()==1500:"current should have not paid";
        System.out.println("PATH 1 SUCCESS");

        tester.SetPosition(1);
        PayRent();
        assert (Payer !=Payee) || (Payer==null):"current should have not payed";
        System.out.println("PATH 2 SUCCESS");

        deeds.SetOwner(0, tester);
        PayRent();
        assert (Payer != Payee) || (Payer!=tester):"current should have not payed themselves";
        System.out.println("PATH 3 SUCCESS");

        dice.Roll();
        tester.SetPosition(12);
        deeds.SetOwner(board.GetDeed(12), playerList.get(1));
        PayRent();
        assert tester.GetMoney()==(1500 - dice.GetTotal()*4):"4* dice";
        assert deeds.GetOwner(board.GetDeed(12)).GetMoney()==(1500 + dice.GetTotal()*4):"4* dice";
        System.out.println("PATH 4 SUCCESS");

        CleanMoney();
        tester.SetPosition(28);
        deeds.SetOwner(board.GetDeed(12), null);
        deeds.SetOwner(board.GetDeed(28), playerList.get(1));
        PayRent();
        assert tester.GetMoney()==(1500 - (dice.GetTotal()*4)):"4* dice";
        assert deeds.GetOwner(board.GetDeed(28)).GetMoney()==(1500 + (dice.GetTotal()*4)):"4* dice";
        System.out.println("PATH 5 SUCCESS");

        CleanMoney();
        deeds.SetOwner(board.GetDeed(12), playerList.get(1));
        tester.SetPosition(12);
        PayRent();
        assert tester.GetMoney()==(1500 - dice.GetTotal()*10):"10* dice";
        assert deeds.GetOwner(board.GetDeed(12)).GetMoney()==(1500 + dice.GetTotal()*10):"4* dice";
        System.out.println("PATH 6 SUCCESS");

        CleanMoney();
        tester.SetPosition(28);
        PayRent();
        assert tester.GetMoney()==(1500 - dice.GetTotal()*10):"10* dice";
        assert deeds.GetOwner(board.GetDeed(28)).GetMoney()==(1500 + dice.GetTotal()*10):"4* dice";
        System.out.println("PATH 7 SUCCESS");  

        CleanMoney();
        tester.SetPosition(15);
        deeds.SetOwner(board.GetDeed(15),playerList.get(1));
        PayRent();
        assert tester.GetMoney()==(1500 - 25):"railroad";
        assert deeds.GetOwner(board.GetDeed(15)).GetMoney()==1525:"railroad";       
        System.out.println("PATH 8 SUCCESS");

        CleanMoney();
        tester.SetPosition(1);
        deeds.SetOwner(0, playerList.get(1));
        PayRent();
        assert tester.GetMoney()==1498:"current should have paid";
        assert deeds.GetOwner(0).GetMoney()==1502:"owner should have collected";
        System.out.println("PATH 9 SUCCESS");

        CleanMoney();
        deeds.SetOwner(1, playerList.get(1));
        PayRent();
        assert tester.GetMoney()==1496:"current should have paid";
        assert deeds.GetOwner(0).GetMoney()==1504:"owner should have collected";
        System.out.println("PATH 10 SUCCESS");

        // Reset state info:
        tester.SetPosition(start_pos);
        i = 0;
        for (Player p : playerList) {
            p.SetMoney(moneys[i]);
            i++;
        }
        this.state = st;
        deeds.SetHouses(0, prev_houses);
    }

    private void CleanMoney(){
        for(Player p : playerList){
            p.SetMoney(1500);
        }
    }


    private void TestBuyProperty() {
        // Save initial state info:
        Player tester = playerList.get(0);
        Player prev_owner = deeds.GetOwner(0);
        int start_pos = tester.GetPosition();
        int prev_money = tester.GetMoney();
        tester.SetMoney(1500);
        State st = this.state;
        this.state = State.MANAGE_END;

        // Run tests:
        System.out.println("\nRunning 'Buy()' validation testing...");
        tester.SetPosition(1);
        deeds.SetOwner(0, null);
        Buy();
        assert (tester.GetMoney() == 1500 && deeds.GetOwner(0) == null):"Cannot buy unless state = BUY_MANAGE.";
        System.out.println("TEST 1 SUCCESS");

        this.state = State.BUY_MANAGE;
        deeds.SetOwner(0, tester);
        Buy();
        assert (tester.GetMoney() == 1500):"Cannot buy owned property. (owned by buyer)";
        System.out.println("TEST 2 SUCCESS");

        deeds.SetOwner(0, playerList.get(1));
        Buy();
        assert (tester.GetMoney() == 1500 && deeds.GetOwner(0) == playerList.get(1)):"Cannot buy owned property. (owned by other player)";
        System.out.println("TEST 3 SUCCESS");

        tester.SetPosition(10);
        Buy();
        assert (tester.GetMoney() == 1500):"Cannot buy non-property.";
        System.out.println("TEST 4 SUCCESS");

        this.state = State.BUY_MANAGE;
        tester.SetPosition(1);
        tester.SetMoney(0);
        Buy();
        assert (tester.GetMoney() == 1500 && deeds.GetOwner(0) == null):"Cannot buy with no money";
        System.out.println("TEST 5 SUCCESS");

        this.state = State.BUY_MANAGE;
        tester.SetMoney(1500);
        Buy();
        assert (tester.GetMoney() == 1440 && deeds.GetOwner(0) == tester):"Property should have been purchased";
        System.out.println("TEST 6 SUCCESS");

        // Reset state info:
        tester.SetPosition(start_pos);
        tester.SetMoney(prev_money);
        deeds.SetOwner(0, prev_owner);
        this.state = st;
    }

    /* ===== Loading GameSave =================================================
     * ======================================================================== */
    private void LoadGame(GameSave gs) {
        this.playerList = gs.playerList;
        this.numPlayers = playerList.size();
        this.currentPlayer = gs.currentPlayer;
        this.deeds = gs.deeds;
        this.chanceOrder = gs.chance_order;
        this.commChestOrder = gs.commCH_order;
        this.state = gs.state;
        this.board = gs.board;
        this.dice = gs.dice;
        this.log = gs.log;
    }

    /* ===== Public Getters ===================================================
     * ======================================================================== */
    public int GetNumPlayers() {
        return this.numPlayers;
    }
    public ArrayList<Player> GetPlayers() {
        return playerList;
    }
    public Player GetCurrentPlayer() {
        return this.currentPlayer;
    }
    public Dice GetDice() {
        return this.dice;
    }
    public ArrayList<String> GetLog() {
        return this.log;
    }
    public String GetLastLog() {
        String s = "";
        if (this.log != null && !this.log.isEmpty()) {
            s = this.log.get(this.log.size()-1);
        }
        return s;
    }
    public int GetBuyDeed() {
        int position = currentPlayer.GetPosition();
        return board.GetDeed(position);
    }
    public int GetCardID() {
        return this.card.GetIndex();
    }
    public String GetDeck() {
        return this.cardDeck;
    }
    public String GetPayees() {
        if (this.lienholder != null) {
            return lienholder.GetName() + " ";
        } else if (oweEveryone) {
            String s  = new String();
            for (Player p : this.playerList) {
                if (p != currentPlayer) {
                    s += p.GetName() + " ";
                }
            }
            return s;
        } else {
            return "The Bank ";
        }
    }
    public int GetOwed() {
        return this.amountOwed;
    }
    public Player GetCurrentBidder(){
        return CurrentBidder;
    }
    public Player GetPayer() {
        return Payer;
    }
    public int GetHighBid(){
        return HighBid;
    }
    public int GetDeedPrice(){
        return deeds.GetPrice(board.GetDeed(currentPlayer.GetPosition()));
    }
    public ArrayList<Integer> SellAssests(){
        ArrayList<Integer> dump = new ArrayList<Integer>();
        for(int i:Payer.GetDeedList()){
            if(deeds.CanMortgage(i)/*||deeds.CanSell(i)*/){
                dump.add(i);
            }
        }
        return dump;
    }
    public int LowestMortgage(ArrayList<Integer> property){
        if(property.isEmpty())
            return -1;
        int deed = property.get(0);
        int price = deeds.GetPrice(deed);
        for(int i:property){
            if(price > deeds.GetPrice(i)){
                deed = i;
                price = deeds.GetPrice(i);
            }
        }
        return deed;
    }
    public int GetAuctionDeed(){
        return this.AuctionDeed;
    }
    public int GetAuctionPrice(){
        return deeds.GetPrice(AuctionDeed);
    }

    /* ===== Ok ===============================================================
     * Manages updates when the player selects the OK action.
     * Basically used to continue after a state is reached in which the player
     * should see a result before allowing the game to continue.
     * Many follow-up actions are contained in their own functions.
     * ======================================================================== */
    public State Ok() {
        switch (state) {
            case ROLL_OK:
                FinishRoll();
                break;
            case CARD_OK:
                FinishCard();
                break;
            case AUCTION_OK:
                state = CheckDone();
                break;
            case TO_JAIL_OK:
                GoToJail();
                doneRolling = true;
                state = State.MANAGE_END;
                break;
            case PAY_RENT_OK:
                PayRent();
                break;
            case PAY_DOUBLERAIL_OK:
                PayDoubleRailRent();
                break;
            case PAY_MAXUTIL_OK:
                PayMaxUtilRent();
                break;
            case GO_OK:
                PlayerMoveTo(0);
                break;
            case TAX_OK:
                PayTax();
                break;
            case ASSESS_OK:
                Assess();
                break;
            case CARD_PAY_OK:
                CardPay();
                break;
            case CARD_COLLECT_OK:
                CardCollect();
                break;
            case CARD_PAY_ALL_OK:
                CardPayAll();
                break;
            case CARD_COLLECT_ALL_OK:
                CardCollectAll();
                break;
            default:
        }
        return state;
    }


    /* ===== Roll =============================================================
     * ======================================================================== */

    // Roll the dice, wait for player to say ok
    public State Roll() {
        if (state != State.ROLL_MANAGE) { // || doneRolling ?
            return state;
        }

        dice.Roll();
        log.add(currentPlayer.GetName() + " rolled " + dice.GetTotal());
        state = State.ROLL_OK;
        return state;
    }

    // Player saw dice, finish roll action
    public State FinishRoll() {
        if (currentPlayer.IsInJail()) {
            return JailRoll();
        }

        doneRolling = true;
        if (dice.IsDoubles()) {
            numDoubles++;
            if (numDoubles == 3) {
                state = State.TO_JAIL_OK;
                log.add(currentPlayer.GetName() + " rolled 3 Doubles, go to jail.");
                return state;
            }
            log.add(currentPlayer.GetName() + " rolled doubles! Roll again.");
            doneRolling = false;
        }

        PlayerMove(dice.GetTotal());

        return CheckSquare();
    }

    private State CheckSquare() {
        int position = currentPlayer.GetPosition();
        switch (board.GetType(position)) { // Update state based on player position.
            case PROPERTY:
            case RAIL_PROP:
            case UTIL_PROP:
                int deed_num = board.GetDeed(position);
                Player owner = deeds.GetOwner(deed_num);
                // If property unowned, player may buy
                if (owner == null) {
                    state = State.BUY_AUCTION;
                    log.add(currentPlayer.GetName() + " landed on " + deeds.GetName(deed_num) + ". (unowned)");
                    return state;
                } else { // If owned -
                    if (currentPlayer == owner) { // by current player, continue
                        state = CheckDone();
                        log.add(currentPlayer.GetName() + " landed on " + deeds.GetName(deed_num) + ". (owner)");
                        return state;
                    } else { // by another player, pay rent.
                        this.lienholder = owner;

                        state = State.PAY_RENT_OK;
                        log.add(currentPlayer.GetName() + " landed on an owned property " + deeds.GetName(deed_num));
                        return state;
                    }
                }
            case CHANCE:
            case COMM_CHEST:
                PullCard();
                state = State.CARD_OK;
                log.add(currentPlayer.GetName() + " landed on card. " + card.GetText());
                return state;
            case JAIL:
                state = CheckDone();
                log.add(currentPlayer.GetName() + " landed on jail, just visiting.");
                return state;
            case FREE_PARK:
                state = CheckDone();
                log.add(currentPlayer.GetName() + " landed on Free Parking.");
                return state;
            case INCOME_TAX:
                state = State.TAX_OK;
                log.add(currentPlayer.GetName() + " landed on Income Tax.");
                return state;
            case LUX_TAX:
                state = State.TAX_OK;
                log.add(currentPlayer.GetName() + " landed on Luxury Tax.");
                return state;
            case GO:
                log.add(currentPlayer.GetName() + " landed on GO! Collect 200!");
                //Collect(200);  //TODO: check if double pay??
                state = CheckDone();
                return state;
            case GO_JAIL:
                state = State.TO_JAIL_OK;
                log.add(currentPlayer.GetName() + " go directly to jail.");
                return state;
            default:
                log.add("Not sure what happened here...");//TODO: verify by playing, this is never reached...
                return state;
        }
    }

    // Player is rolling to get out of jail
    public State JailRoll() {
        if (dice.IsDoubles()) {
            currentPlayer.SetJail(false);
            PlayerMove(dice.GetTotal());
            log.add(currentPlayer.GetName() + " rolled Doubles! and escaped from Jail.");
        } else {
            currentPlayer.IncTries();
            if (currentPlayer.GetJailbreakTries() < 3) {
                log.add(currentPlayer.GetName()+" could not roll a double, Tries Left: "
                        +(3-currentPlayer.GetJailbreakTries()));
            } else {
                if (currentPlayer.GetMoney() < 50) {
                    Payer = currentPlayer;
                    amountOwed = 50;
                    lienholder = null;
                    oweEveryone = false;
                    state = State.PAY_MANAGE; //TODO: JAIL_MANAGE?
                    return state;
                }
                Pay(50);
                currentPlayer.SetJail(false);
                log.add(currentPlayer.GetName() + " failed to get a triple in 3 tries, fine automatically paid.");
            }
        }
        doneRolling = true;
        state = State.MANAGE_END;
        return state;
    }

    // Common code for checking if player can roll again
    private State CheckDone() {
        if (doneRolling) {
            return State.MANAGE_END;
        } else {
            return State.ROLL_MANAGE;
        }
    }

    /* ===== Buy ==============================================================
     * ======================================================================== */
    public State Buy() {
        int position = currentPlayer.GetPosition();
        int deed_num = board.GetDeed(position);
        Player owner = deeds.GetOwner(deed_num);

        // Controller should already guard against this...
        if (state != State.BUY_AUCTION || owner != null) {
            log.add("Cannot execute buy...");
            return state;
        }

        if (currentPlayer.GetMoney() < deeds.GetPrice(deed_num)) {
            log.add("Not enough money to buy.");
            //TODO: set variables prior to pay/manage
            //  so that after pay/manage, we come back to the right spot
            state = State.BUY_MANAGE;
            return state;
        }

        Pay(deeds.GetPrice(deed_num));
        deeds.SetOwner(deed_num, currentPlayer);
        currentPlayer.AddDeed(deed_num);
        state = CheckDone();
        log.add(currentPlayer.GetName() + " bought " + deeds.GetName(deed_num));
        return state;
    }

    /* ===== Auction ==========================================================
     * ======================================================================== */
    public State Auction() {
        // Controller should already guard against this...
        if (state != State.BUY_AUCTION) {
            log.add("Cannot execute auction...");
            return state;
        }
        Bidders = new ArrayList<Player>(playerList);
        int position = currentPlayer.GetPosition();
        AuctionDeed = board.GetDeed(position);       
        state = State.AUCTION;
        HighBidder = currentPlayer;
        CurrentBidder = currentPlayer;
        NextBidder();
        HighBid = 0;
        return state;
    }

    public State Bid(int bid){
        if (bid < HighBid || bid > CurrentBidder.GetMoney())
            return state;
        HighBid = bid;
        HighBidder = CurrentBidder;
        NextBidder();
        return state;
    }

    public State Pass(){
        int index = Bidders.indexOf(CurrentBidder);
        Bidders.remove(CurrentBidder);
        if (Bidders.size()==1) {
            HighBidder.AddDeed(AuctionDeed);
            deeds.SetOwner(AuctionDeed, HighBidder);
            Pay(HighBidder,HighBid);
            log.add(HighBidder.GetName()+ " won "+deeds.GetName(AuctionDeed)+" by auction");
            state = CheckDone();
            return state;
        }
        if (index >Bidders.size())
            CurrentBidder = Bidders.get(0);
        else
            CurrentBidder = Bidders.get(index);
        return state;

    }

    private void NextBidder(){
        int index = Bidders.indexOf(CurrentBidder);
        while(CurrentBidder!=HighBidder){
            index++;
            CurrentBidder = Bidders.get(index%Bidders.size());
        }
    }

    /* ===== EndTurn ==========================================================
     * ======================================================================== */
    public State EndTurn(){
        if (!doneRolling) {
            log.add("You can't turn your turn yet.");
            return state;
        }
        NextPlayer();

        if (currentPlayer.IsInJail()) {
            state = State.JAIL_CHOICE;
        } else {
            state = State.ROLL_MANAGE;
        }
        return state;
    }

    private void NextPlayer() {
        doneRolling = false;
        numDoubles = 0;
        int i = 0;
        while (playerList.get(i) != currentPlayer) {
            i++;
            if (i == numPlayers) {
                // Major error...
                log.add("Couldn't find next player...");
            }
        }
        i++;
        currentPlayer = playerList.get(i % numPlayers);
        log.add("It is " + currentPlayer.GetName() + "'s turn.");
    }


    /* ===== Jail =============================================================
     * ======================================================================== */

    //JailRoll covered above in Roll section
    public State JailCard() {
        if (currentPlayer.HasChanceJailcard()) {
            currentPlayer.SetJail(false);
            log.add(currentPlayer.GetName() + " used Chance 'Get out of Jail Free' card.");
            chanceOrder.add(14);
            state = State.MANAGE_END;
        } else if (currentPlayer.HasChanceJailcard()) {
            currentPlayer.SetJail(false);
            log.add(currentPlayer.GetName() + " used Chance 'Get out of Jail Free' card.");
            commChestOrder.add(2);
            state = State.MANAGE_END;
        } else {
            log.add("You do not have a 'Get out of Jail Free' card.");
        }
        doneRolling = true;
        return state;
    }

    public State JailPay() {
        if (currentPlayer.GetMoney() >= 50) {
            Pay(50);
            currentPlayer.SetJail(false);
            log.add(currentPlayer.GetName()+" paid the Jail fine");
            state = State.MANAGE_END;
        } else {
            state = State.PAY_MANAGE;
        }
        doneRolling = true;
        return state;
    }


    /* ===== Card =============================================================
     * ======================================================================== */
    // Player landed on card square.
    private void PullCard() {
        int pos = currentPlayer.GetPosition();
        if (board.GetType(pos) == SquareType.CHANCE) {
            int card_num = chanceOrder.remove(0);
            cardDeck = "chance";
            card = chance.GetCard(card_num);
            if (card_num == 14) {
                currentPlayer.SetChanceJailcard(true);
            } else {
                chanceOrder.add(card_num);
            }
        } else if (board.GetType(pos) == SquareType.COMM_CHEST) {
            int card_num = commChestOrder.remove(0);
            cardDeck = "comm_chest";
            card = commChest.GetCard(card_num);
            if (card_num == 2) {
                currentPlayer.SetCommChestJailcard(true);
            } else {
                commChestOrder.add(card_num);
            }
        }
    }

    // Player saw card, take appropriate action
    public State FinishCard() {
        int pos, deed_num;
        Player owner;
        switch (card.GetType()) {
            case COLLECT:
                log.add("Collect your reward!");
                state = State.CARD_COLLECT_OK;
                break;
            case PAY:
                log.add("Pay the penalty!");
                state = State.CARD_PAY_OK;
                break;
            case COLLECT_ALL:
                log.add("Collect your reward from each player!");
                state = State.CARD_COLLECT_ALL_OK;
                break;
            case PAY_ALL:
                log.add("Pay each player!");
                state = State.CARD_PAY_ALL_OK;
                break;
            case TO_JAIL:
                state = State.TO_JAIL_OK;
                break;
            case MOVE:
                PlayerMove(card.GetValue());
                log.add(currentPlayer.GetName() + " moved.");
                state = CheckSquare();
                break;
            case MOVE_TO:
                PlayerMoveTo(card.GetValue());
                log.add(currentPlayer.GetName() + " moved.");
                //TODO: state is not done... check where they landed...
                state = CheckSquare();
                break;
            case ASSESS:
                state = State.ASSESS_OK;
                break;
            case RAILROAD:
                PlayerMoveToRail();
                log.add(currentPlayer.GetName() + " moved to the nearest railroad.");
                pos = currentPlayer.GetPosition();
                deed_num = board.GetDeed(pos);
                owner = deeds.GetOwner(deed_num);
                if (owner == null) {
                    state = State.BUY_AUCTION;
                } else if (owner == currentPlayer) { 
                    state = CheckDone();
                } else {
                    state = State.PAY_DOUBLERAIL_OK;
                }
                break;
            case UTILITY:
                PlayerMoveToUtil();
                log.add(currentPlayer.GetName() + " moved to the nearest utility.");
                pos = currentPlayer.GetPosition();
                deed_num = board.GetDeed(pos);
                owner = deeds.GetOwner(deed_num);
                if (owner == null) {
                    state = State.BUY_AUCTION;
                } else if (owner == currentPlayer) { 
                    state = CheckDone();
                } else {
                    state = State.PAY_MAXUTIL_OK;
                }
                break;
            case JAIL_FREE:
                log.add("Get out of Jail Free card saved.");
                state = CheckDone();
                break;
            default:
                break;
        }
        return state;
    }

    private void Assess(){
        //        int worth = 0;
        //        if(!CurrentPlayer.getDeedList().isEmpty()){
        //            for(int i :CurrentPlayer.getDeedList()){
        //                worth+=(board.getDeedHouses(i)*25);
        //                worth+=(board.getDeedHotel(i)*100);
        //            }
        //            CurrentPlayer.AddMoney(worth*-1);
        //            log.add(CurrentPlayer.getName()+"pays"+worth+"in repairs!");
        //            PaySave(worth,CurrentPlayer,null,PayState.OTHER);
        //        }
        //        else{
        //            log.add(CurrentPlayer.getName()+"does not own any properties to repair");
        //            state = State.END;
        //        }
        log.add("Skipping assessment...");
        state = CheckDone();
    }   

    /* ===== Pay ==============================================================
     * ======================================================================== */
    public State PayRent() {
        int pos = currentPlayer.GetPosition();
        int deed_num = board.GetDeed(pos);
        int amount;
        if (deed_num ==-1)
            return state;

        Player owner = deeds.GetOwner(deed_num);

        if (deeds.GetOwner(deed_num) == null || deeds.GetOwner(deed_num) == currentPlayer)
            return state;
        if (pos == 12 || pos == 28) { // Utility
            if (deeds.GetOwner(22) == deeds.GetOwner(23)) {
                amount = 10 * dice.GetTotal();
            } else {
                amount = 4 * dice.GetTotal();
            }
        } else if ((pos+5)%10 == 0) { // Rail
            int count = deeds.GetNumRails(owner);
            amount = 25;
            for (int i = 1; i < count; i++) {
                amount *= 2;
            }
        } else { // Property
            amount = deeds.GetRent(deed_num);
            if (deeds.GetNumHouses(deed_num) == 0 && owner == deeds.Monopoly(deed_num)) {
                amount *= 2;
            }
        }

        return Pay(currentPlayer, owner, amount);
    }

    private State PayDoubleRailRent() {
        int pos = currentPlayer.GetPosition();
        int deed_num = board.GetDeed(pos);
        Player owner = deeds.GetOwner(deed_num);
        int count = deeds.GetNumRails(owner);
        int amount = 25;
        for (int i = 1; i < count; i++) {
            amount *= 2;
        }
        return Pay(currentPlayer, owner, amount);
    }

    private State PayMaxUtilRent() {
        int pos = currentPlayer.GetPosition();
        int deed_num = board.GetDeed(pos);
        Player owner = deeds.GetOwner(deed_num);
        return Pay(currentPlayer, owner, dice.GetTotal() * 10);
    }

    private void Pay(int amount) {
        Payer = currentPlayer;
        currentPlayer.AddMoney(amount * -1);
        log.add(currentPlayer.GetName()+" paid " + amount);
    }

    private void Pay(Player payer,int amount){
        Payer = payer;
        payer.AddMoney(amount * -1);
        log.add(payer.GetName()+" paid "+ amount);
    }

    private State Pay(Player payer, Player payee, int amount) {
        if (payer == null || payee == null) {return state;}
        if (payer.GetMoney() < amount) {
            Payer = payer;
            lienholder = payee;
            amountOwed = amount;
            oweEveryone = false;
            state = State.PAY_MANAGE;
            //log?
        } else {
            payer.AddMoney(amount * -1);
            payee.AddMoney(amount);
            log.add(payer.GetName() + " paid " + amount + " to " + payee.GetName());
            state = CheckDone();
        }
        return state;
    }
    //PAY MANAGE
    //-----------------------------------------------------------------------
    public State PayManageSelect(int deed){
        if(!Payer.GetDeedList().contains(deed))
            return state;
        managingDeed = deed;
        state = State.PAY_MANAGE;
        return state;
    }
    public State RetryPay() {
        if (oweEveryone) {
            CardPayAll();
            return state;
        }
        if (Payer.GetMoney() < amountOwed) {
            state = State.PAY_MANAGE;
            log.add("You still do not have enough money to pay your debts.");
            return state;
        }
        if(Payer != currentPlayer && Payer !=null){
            Pay(Payer,currentPlayer,amountOwed);
            debtPlayers.remove(Payer);
            state = CheckDebt();
            return state;
        }else if (lienholder != null) {//currentplayer
            Pay(currentPlayer, lienholder, amountOwed);
            state = CheckDone();
            return state;
        }
        managingDeed = -1;
        Pay(amountOwed);
        state = CheckDone();
        return state;
    }
    public State AdmitDefeat() {
        numPlayers--;
        managingDeed = -1;
        if(numPlayers == 1){
            state = State.GAMEOVER;
            return state;
        }

        for(int i:Payer.GetDeedList()){
            deeds.SetOwner(i, lienholder);//for now...
            if(lienholder !=null){
                lienholder.AddDeed(i);
                if(deeds.GetHouses(i)==5){
                    lienholder.AddMoney((int) (deeds.GetHousePrice(i)*.5));
                }
                else
                    lienholder.AddMoney((int) (deeds.GetHousePrice(i)*.5*deeds.GetNumHouses(i)));
            }
            deeds.SetHouses(i,0);
        }
        int index = playerList.indexOf(Payer);
        playerList.remove(Payer);
        if(Payer == currentPlayer){
            if(index > playerList.size()){
                currentPlayer = playerList.get(0);
            }else
                currentPlayer = playerList.get(index);
            state = State.ROLL_MANAGE;
            return state;
        }else{
            debtPlayers.remove(Payer);
            state = CheckDebt();
            return state;
        } 
    }
    public State CheckDebt(){
        if(debtPlayers.isEmpty()){
            return CheckDone();
        }
        else{
            Payer = debtPlayers.get(0);
            return State.PAY_MANAGE;
        }
    }
    private void Collect(int amount) {
        currentPlayer.AddMoney(amount);
    }
    private void Collect(Player play,int amount){
        Payer = play;
        Payer.AddMoney(amount);
    }
    private void CardPay() {
        int amount = card.GetValue();

        if (currentPlayer.GetMoney() < amount) {
            Payer = currentPlayer;
            lienholder = null;
            amountOwed = amount;
            oweEveryone = false;
            state = State.PAY_MANAGE;
        } else {
            Pay(amount);
            state = CheckDone();
        }
    }

    private void CardCollect() {
        Collect(card.GetValue());
        state = CheckDone();
        log.add(currentPlayer.GetName() + " collected $" + card.GetValue());
    }

    private void CardPayAll() {
        // There is a chance the payer could go bankrupt on this.
        // Who should inherit the estate?
        int count = 0;
        for (Player p : playerList) {
            if (p != currentPlayer) {
                count++;
            }
        }
        int total = card.GetValue() * count; // 50 * count
        if (currentPlayer.GetMoney() < total) {
            Payer = currentPlayer;
            amountOwed = card.GetValue();
            oweEveryone = true;
            lienholder = null;
            state = State.PAY_MANAGE;
            log.add("Not enough money. Manage assets to pay, or admit defeat.");
        } else {
            for (Player p : playerList) {
                if (p != currentPlayer) {
                    Pay(currentPlayer, p, card.GetValue());
                }
            }
            state = CheckDone();
            log.add(currentPlayer.GetName() + " paid $" + card.GetValue() + " to each player.");
        }
    }

    private void CardCollectAll() {
        //TODO: verify others have $50...
        //  make list of indebted players??
        //  PAY_MANAGE_OTHERS??

        // For now, just assume they have the money...
        debtPlayers = new ArrayList<Player>();
        for (Player p : playerList) {
            if (p != currentPlayer) {
                if(p.GetMoney()<50){
                    debtPlayers.add(p);
                }else{
                    Pay(p,currentPlayer,50);
                }
            }
        }
        if(debtPlayers.isEmpty()){
            state = CheckDone();
        }
        else{
            lienholder = currentPlayer;
            oweEveryone = false;
            Payer = debtPlayers.get(0);
            state = State.PAY_MANAGE;
        }
        log.add(currentPlayer.GetName() + " collected $" + card.GetValue() + " from each player.");
    }


    /* ===== PayTax ===========================================================
     * ======================================================================== */
    private void PayTax() {
        int pos = currentPlayer.GetPosition();
        int tax = 0;
        if (pos == 4) {
            tax = CalcIncomeTax();
            if (tax > 200) { // Luxury tax
                tax = 200;
            }
        } else if (pos == 38) { // Income tax
            tax = 75;
        } else {
            System.err.println("Tax error, player not on taxable square.");
            System.exit(1);
        }

        if (currentPlayer.GetMoney() < tax) {
            lienholder = null;
            amountOwed = tax;
            oweEveryone = false;
            Payer = currentPlayer;
            state = State.PAY_MANAGE;
        } else {
            Pay(tax);
            state = CheckDone();
        }
    }

    private int CalcIncomeTax(){
        int worth = currentPlayer.GetMoney();
        if(!currentPlayer.GetDeedList().isEmpty()){
            for(int i : currentPlayer.GetDeedList()){
                if (deeds.IsMortgaged(i)) {
                    worth += deeds.GetPrice(i) / 2; // * 0.55
                }
                else {
                    worth += deeds.GetPrice(i);
                    worth += deeds.GetHousePrice(i)*deeds.GetNumHouses(i);
                }
            }
        }
        return worth / 10;
    }

    /* ===== Move =============================================================
     * ======================================================================== */
    private void PlayerMove(int num) {
        int pos = currentPlayer.GetPosition() + num;
        if (pos >= 40) {
            pos %= 40;
            log.add(currentPlayer.GetName() + " passed GO, collect $200.");
            Collect(200);
        }
        currentPlayer.SetPosition(pos);
    }

    private void PlayerMoveTo(int pos) {
        int num = pos - currentPlayer.GetPosition();
        if (num < 0) { num += 40; }
        PlayerMove(num);
    }

    private void PlayerMoveToRail(){
        int pos = currentPlayer.GetPosition();
        if (pos < 5 || pos >= 35) {
            PlayerMoveTo(5);
        } else if (pos < 15) {
            PlayerMoveTo(15);
        } else if (pos < 25) {
            PlayerMoveTo(25);
        } else if (pos < 35) {
            PlayerMoveTo(35);
        } 
    }

    private void PlayerMoveToUtil() {
        int pos = currentPlayer.GetPosition();
        if (pos >= 28 || pos < 12)
            PlayerMoveTo(12);//electricity
        else
            PlayerMoveTo(28);//waterworks
    }

    private void GoToJail() {
        currentPlayer.SetJail();
    }

    /* ===== Manage ===========================================================
     * ======================================================================== */
    public State Manage() {
        Payer = currentPlayer;
        state = State.MANAGE;
        return state;
    }
    public State ManageSelect(int property){
        if(!Payer.GetDeedList().contains(property))
            return state;
        managingDeed = property;
        state = State.MANAGE_SELECT;
        return state;
    }
    public State Sell() {
        if (managingDeed == -1)
            return state;    	
        if (deeds.GetHousePrice(managingDeed)>Payer.GetMoney())
            return state;
        if (deeds.CanSell(managingDeed)) {
            deeds.DecHouses(managingDeed);
            houseCount++;
            Pay(Payer,deeds.GetHousePrice(managingDeed));
            log.add(Payer.GetName()+" has sold an improvement from " + deeds.GetName(managingDeed));
        } else {
            log.add("cannot be sold");
        }
        return state;
    }
    public State Improve(){
        if (managingDeed == -1)
            return state; 
        if (deeds.GetHousePrice(managingDeed) > Payer.GetMoney())
            log.add("Not enough money for improvement");
        if (deeds.CanImprove(managingDeed)) {
            if  (deeds.GetHouses(managingDeed) < 4 && houseCount>0) {
                log.add(currentPlayer.GetName()+ " has improved " +deeds.GetName(managingDeed));
                deeds.IncHouses(managingDeed);
                houseCount--;
                Pay(deeds.GetHousePrice(managingDeed));
            } else if (deeds.GetHouses(managingDeed) == 4 && hotelCount>0){
                deeds.IncHouses(managingDeed);
                hotelCount--;
                houseCount+=4;
                Pay(deeds.GetHousePrice(managingDeed));
            } else {
                log.add("Not enough buildings to improve");
            }
        }
        return state;
    }
    public State Mortgage(){
        if(managingDeed == -1)
            return state; 
        if(deeds.CanMortgage(managingDeed)){
            log.add(Payer.GetName()+" mortgaged " +deeds.GetName(managingDeed));
            deeds.SetMortgaged(managingDeed, true);
            Collect(Payer,(int) (deeds.GetPrice(managingDeed)*.5));
        }
        return state;
    }
    public State UnMortgage(){
        if(managingDeed == -1)
            return state; 
        if(!deeds.IsMortgaged(managingDeed) && deeds.GetPrice(managingDeed)*.55<Payer.GetMoney()){
            log.add(Payer.GetName()+" unmortgaged " +deeds.GetName(managingDeed));
            deeds.SetMortgaged(managingDeed, false);
            Pay((int) (deeds.GetPrice(managingDeed)*.55));
        }
        return state;
    }
    public State ReturnFromManage() {
        state = CheckDone();
        return state;
    }
    public State ReturnFromSelect(){
        managingDeed = -1;
        state = State.MANAGE;
        return state;
    }

}
