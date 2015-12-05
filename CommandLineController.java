
/* CommandLineController Class
 * Modified: 06 May 2015  
 * Author(s): Phillip Stewart, Lap Nguyen
 * Description:
 *     Acts as a separate controller and main program for testing the game model.
 *     
 * This contains a main function, and can be run as the main application
 * The game can be played on the command line (or console) by inputting simple directions
 * 
 * The state is updated on each action. If the action is invalid, the state should remain the same.
 * That is, if input other than expected is entered, the loop in the start() function will simply
 * wrap back to the switch and prompt valid options again.
 * 
 * This is currently a rough draft, and is certainly not correct.
 * Not all states are represented, but may be easily added.
 * Actions should directly invoke public Game functions and catch the new state from the return values.
 * 
 * This should provide a general test kit for the Game, allowing a quick prototype for Game functions.
 * As new functionality is added to the game, the actions and states may be added here for testing.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.swing.SwingUtilities;


public class CommandLineController {

    BufferedReader br;
    Game game;

    public static void main(String[] args) 
    {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new CommandLineController();
            }
        });
    }


    public CommandLineController() {
        br = new BufferedReader(new InputStreamReader(System.in));
        ArrayList<Player> players = new ArrayList<Player>();
        players.add(new Player(false, "Player #1", 1, 0));
        players.add(new Player(false, "Player #2", 2, 0));
        GameSave gs = new GameSave(players); // 2 players, 0 AI
        game = new Game(gs);
        start();
    }
    
/* States: OUTDATED...
    ROLL_MANAGE, MANAGE_END, BUY_AUCTION, JAIL_CHOICE, PAY_MANAGE,
    ROLL_OK, CARD_OK, TO_JAIL_OK, PAY_RENT_OK, PAY_DOUBLE_OK, GO_OK, TAX_OK, ASSESS_OK,
    CARD_PAY_OK, CARD_COLLECT_OK, CARD_PAY_ALL_OK, CARD_COLLECT_ALL_OK, 
    AUCTION, LOSE, MANAGE, INFO, MORTGAGE, IMPROVE, GAMEOVER}
*/
    private void start() {
        String s = null;
        State state = State.ROLL_MANAGE;
        while (true) {//game.GetNumPlayers() > 1) {
            w(game.GetLastLog());
            switch(state) {
                case ROLL_MANAGE:
                    w(" options: manage, roll");
                    s = r();
                    if (s.equals("manage")) {
                        state = game.Manage();
                    } else if (s.equals("roll")) {
                        state = game.Roll(); 
                    }
                    break;
                case MANAGE_END:
                    w(" options: manage, end");
                    s = r();
                    if (s.equals("manage")) {
                        state = game.Manage();
                    } else if (s.equals("end")) {
                        state = game.EndTurn(); 
                    }
                    break;
                case BUY_AUCTION:
                    w(" options: buy, auction");
                    s = r();
                    if (s.equals("buy")) {
                        state = game.Buy();
                    } else if (s.equals("end")) {
                        state = game.Auction(); 
                    }
                    break;
                case JAIL_CHOICE:
                    w(" options: roll, pay, card");
                    s = r();
                    if (s.equals("roll")) {
                        state = game.JailRoll();
                    } else if (s.equals("pay")) {
                        state = game.JailPay(); 
                    } else if (s.equals("card")) {
                        state = game.JailCard(); 
                    }
                    break;
                case PAY_MANAGE:
                    w(" options: pay, manage, defeat,sell,mortgage");
                    s = r();
                    if (s.equals("pay")) {
                        state = game.RetryPay();
                    } else if (s.equals("manage")) {
                        w("Input: Managing Deed");
                        s = r();
                        state = game.PayManageSelect(Integer.parseInt(s));
                    } else if (s.equals("defeat")) {
                        state = game.AdmitDefeat(); 
                    }else if(s.equals("mortgage")){
                    	state = game.Mortgage();
                    }else if(s.equals("sell")){
                    	state = game.Sell();
                    }
                    break;
                case ROLL_OK:
                case CARD_OK:
                case TO_JAIL_OK:
                case PAY_RENT_OK:
                case PAY_DOUBLERAIL_OK:
                case PAY_MAXUTIL_OK:
                case GO_OK:
                case TAX_OK:
                case ASSESS_OK:
                case CARD_PAY_OK:
                case CARD_COLLECT_OK:
                case CARD_PAY_ALL_OK:
                case CARD_COLLECT_ALL_OK:
                    w(" options: ok");
                    s = r();
                    if (s.equals("ok")) {
                        state = game.Ok();
                    }
                    break;
                case AUCTION:
                    w(" options: bid, pass"); // Just an example...
                    s = r();
                    if (s.equals("bid")) {
                    	w("Input Bid");
                    	s = r();
                        state = game.Bid(Integer.parseInt(s));
                    }else if (s.equals("pass"))
                        state = game.Pass(); 
                    break;
                case MANAGE:
                    w(" options: select, return");
                    s = r();
                    if (s.equals("select")) {
                    	w("Input: Managing Deed");
                    	s = r();
                        state = game.ManageSelect(Integer.parseInt(s));
                    } else if (s.equals("end")) {
                        state = game.ReturnFromManage(); 
                    }
                    break;
                case MANAGE_SELECT:
                	w(" options: sell, mortgage,unmortgage,improve,end");
                	s = r();
                	if(s.equals("sell")){
                		state = game.Sell();
                	}else if(s.equals("mortgage")){
                		state = game.Mortgage();
                	}else if(s.equals("unmortgage")){
                		state = game.UnMortgage();
                	}else if(s.equals("improve")){
                		state = game.Improve();
                	}else if(s.equals("end")){
                		state = game.ReturnFromSelect();
                	}
                	break;
                case INFO:
                    //I don't know...
                    break;
//                case GAMEOVER:
                default:
                    //error...
            }


        }
    }

    private String r() {
        String s = null;
        try {
            s = br.readLine();
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }
        return s;
    }

    private void w(String s) {
        System.out.println(s);
    }

}
