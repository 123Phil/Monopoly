
/* Board Class
 * Modified: 06 May 2015  
 * Author(s): Phillip Stewart, Lap Nguyen
 * Description:
 *   Contains a representation of the game board.
 *   Each square is an instance of the contained Square class.
 *   Squares are added in order and accessed by index.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

enum SquareType {CHANCE, COMM_CHEST, FREE_PARK, GO, GO_JAIL, INCOME_TAX, 
                    JAIL, LUX_TAX, PROPERTY, RAIL_PROP, UTIL_PROP}

public class Board {
    
    /* ===== Square Class =====================================================
     * ======================================================================== */
    private class Square {
        protected SquareType type;
        protected String name;
        
        public Square(SquareType t, String n) {
            this.type = t;
            this.name = new String(n);
        }
    }
    
    /* ===== Board tiles ======================================================
     * ======================================================================== */
    private List<Square> tiles;
    public Board() {
        tiles = new ArrayList<Square>();
        tiles.add(new Square(SquareType.GO, "Go")); //Go
        tiles.add(new Square(SquareType.PROPERTY, "Mediterranean Avenue"));
        tiles.add(new Square(SquareType.COMM_CHEST,"Community Chest"));
        tiles.add(new Square(SquareType.PROPERTY,"Baltic Avenue"));
        tiles.add(new Square(SquareType.INCOME_TAX,"Income Tax"));
        tiles.add(new Square(SquareType.RAIL_PROP,"Reading Railroad"));
        tiles.add(new Square(SquareType.PROPERTY,"Oriental Avenue"));
        tiles.add(new Square(SquareType.CHANCE,"Chance"));
        tiles.add(new Square(SquareType.PROPERTY,"Vermont Avenue"));
        tiles.add(new Square(SquareType.PROPERTY,"Connecticut Avenue"));
        tiles.add(new Square(SquareType.JAIL,"Jail/JustVisiting"));
        tiles.add(new Square(SquareType.PROPERTY, "St. Charles Place"));
        tiles.add(new Square(SquareType.UTIL_PROP, "Electric Company"));
        tiles.add(new Square(SquareType.PROPERTY,"States Avenue"));
        tiles.add(new Square(SquareType.PROPERTY,"Virginia Avenue"));
        tiles.add(new Square(SquareType.RAIL_PROP,"Pennsylvania Railroad"));
        tiles.add(new Square(SquareType.PROPERTY,"St. James Place"));
        tiles.add(new Square(SquareType.COMM_CHEST,"Community Chest"));
        tiles.add(new Square(SquareType.PROPERTY,"Tennessee Avenue"));
        tiles.add(new Square(SquareType.PROPERTY,"New York Avenue"));
        tiles.add(new Square(SquareType.FREE_PARK,"Free Parking"));
        tiles.add(new Square(SquareType.PROPERTY,"Kentucky Avenue"));
        tiles.add(new Square(SquareType.CHANCE,"Chance"));
        tiles.add(new Square(SquareType.PROPERTY,"Indiana Avenue"));
        tiles.add(new Square(SquareType.PROPERTY,"Illinois Avenue"));
        tiles.add(new Square(SquareType.RAIL_PROP,"B&O Railroad"));
        tiles.add(new Square(SquareType.PROPERTY,"Atlanic Avenue"));
        tiles.add(new Square(SquareType.PROPERTY,"Ventnor Avenue"));
        tiles.add(new Square(SquareType.UTIL_PROP,"Waterworks"));
        tiles.add(new Square(SquareType.PROPERTY,"Marvin Gardens"));
        tiles.add(new Square(SquareType.GO_JAIL,"Go To Jail"));
        tiles.add(new Square(SquareType.PROPERTY,"Pacific Avenue"));
        tiles.add(new Square(SquareType.PROPERTY,"North Carolina Avenue"));
        tiles.add(new Square(SquareType.COMM_CHEST,"Community Chest"));
        tiles.add(new Square(SquareType.PROPERTY,"Pennsylvania Avenue"));
        tiles.add(new Square(SquareType.RAIL_PROP,"Short line Railroad"));
        tiles.add(new Square(SquareType.CHANCE,"Chance"));
        tiles.add(new Square(SquareType.PROPERTY,"Park Palce"));
        tiles.add(new Square(SquareType.LUX_TAX,"Luxury Tax"));
        tiles.add(new Square(SquareType.PROPERTY,"Boardwalk"));
    }
    
    /* ===== Board API ======================================================
     * ======================================================================== */
    
    // Returns the Deed index for the board square
    public int GetDeed(int pos) {
        Integer deeds[]= {1,3,6,8,9,11,13,14,16,18,19,21,23,24,26,27,29,31,32,34,37,39,12,28,5,15,25,35};
        return Arrays.asList(deeds).indexOf(pos);
    }
    
    public SquareType GetType(int pos){
    	return this.tiles.get(pos).type;
    }
   
    public String GetName(int pos) {
        return this.tiles.get(pos).name;
    }
}

