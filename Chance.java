
/* Chance Class
 * Modified: 20 April 2015  
 * Author(s): Phillip Stewart, Lap Nguyen, Supharinna Chimm
 * Description:
 *   Representation of the deck of chance cards.
 *   Deck remains in order once instantiated,
 *   Cards are accessed by index from a shuffled array of indices held by the game.
 */

import java.util.ArrayList;
import java.util.List;


public class Chance {
    private List<Card> deck;
    
    public Chance() {
        deck = new ArrayList<Card>();
        deck.add(new Card(0,  CardType.COLLECT,  50, "Collect 50"));
        deck.add(new Card(1,  CardType.MOVE_TO,   0, "Advance to Go"));
        deck.add(new Card(2,  CardType.MOVE,     -3, "Move Back 3 Spaces"));
        deck.add(new Card(3,  CardType.UTILITY,   0, "Move to the Nearest Utility"));
        deck.add(new Card(4,  CardType.TO_JAIL,   0, "Move to Jail"));
        deck.add(new Card(5,  CardType.PAY,      15, "Pay 15"));
        deck.add(new Card(6,  CardType.MOVE_TO,  11, "Move to St. Charles"));
        deck.add(new Card(7,  CardType.PAY_ALL,  50, "Pay All Players 50"));
        deck.add(new Card(8,  CardType.RAILROAD,  0, "Move to the Nearest Railroad"));
        deck.add(new Card(9,  CardType.MOVE_TO,   5, "Move to Reading Railroad"));
        deck.add(new Card(10, CardType.RAILROAD,  0, "Move to the Nearest Railroad"));
        deck.add(new Card(11, CardType.MOVE_TO,  39, "Move to Boardwalk"));
        deck.add(new Card(12, CardType.COLLECT,  50, "Collect 50"));
        deck.add(new Card(13, CardType.MOVE_TO,  24, "Move to Illinois Avenue")); 
        deck.add(new Card(14, CardType.JAIL_FREE, 0, "Get out of Jail Free Card"));
        deck.add(new Card(15, CardType.ASSESS,    0, "Pay Assess")); 
    }

    public Card GetCard(int index) {
        return this.deck.get(index);
    }
}
