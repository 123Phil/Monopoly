
/* GameSave Class
 * Modified: 01 May 2015  
 * Author(s): Phillip Stewart, Lap Nguyen, Supharinna Chimm
 * Description:
 *   GameSave object.
 *   Holds pertinent game information for saving.
 *   
 *   TODO: implement Import() and Export() for saving and loading the game...
 * 
 */

import java.util.ArrayList;
import java.util.Collections;


public class GameSave {

    // Players
    public ArrayList<Player> playerList;
    public Player currentPlayer;
    
    // Board
    public ArrayList<Integer> chance_order;
    public ArrayList<Integer> commCH_order;
    public int houseCount;
    public int hotelCount;
    
    public int numDoubles;
    public State state;
    public ArrayList<String> log;
	public Deeds deeds;
	public Board board;
	public Dice dice;
    
    // New Game:
    public GameSave(ArrayList<Player> players) {
        this.playerList = new ArrayList<Player>(players);
        this.log = new ArrayList<String>();
        
        this.currentPlayer = playerList.get(0);
        this.board = new Board();
        this.deeds = new Deeds();
        this.chance_order = new ArrayList<Integer>();
        this.commCH_order = new ArrayList<Integer>();
        this.dice = new Dice();
        this.state = State.ROLL_MANAGE;
         
        for (int i = 0; i < 16; i++) {
            chance_order.add(i);
            commCH_order.add(i);
        }
        Collections.shuffle(chance_order);
        Collections.shuffle(commCH_order);
    }
    
    public GameSave(String filename) {
        //TODO: get a gamesave from file and load it here.
    }

}
