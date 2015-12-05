
/* CommChest Class
 * Modified: 20 April 2015  
 * Author(s): Phillip Stewart, Lap Nguyen, Supharinna Chimm
 * Description:
 *   Representation of the deck of chance cards.
 *   Deck remains in order once instantiated,
 *   Cards are accessed by index from a shuffled array of indices held by the game.
 */

import java.util.ArrayList;
import java.util.List;

public class CommChest {
    private List<Card> deck;
    
    public CommChest() {
        deck = new ArrayList<Card>();
        deck.add(new Card(0,  CardType.PAY,         50, "Pay 50"));
        deck.add(new Card(1,  CardType.COLLECT,    100, "Collect 100"));
        deck.add(new Card(2,  CardType.JAIL_FREE,    0, "Get out of Jail Free"));
        deck.add(new Card(3,  CardType.COLLECT_ALL, 50, "Collect 50 from every player"));
        deck.add(new Card(4,  CardType.COLLECT,    100, "Collect 100"));
        deck.add(new Card(5,  CardType.COLLECT,     25, "Collect 25"));
        deck.add(new Card(6,  CardType.COLLECT,     25, "Collect 25"));
        deck.add(new Card(7,  CardType.COLLECT,     45, "Collect 45"));
        deck.add(new Card(8,  CardType.PAY,        150, "Pay 150"));
        deck.add(new Card(9,  CardType.ASSESS,       0, "Pay Assessment"));//pay 40/house & 115/hotel
        deck.add(new Card(10, CardType.COLLECT,    200, "Collect 200"));
        deck.add(new Card(11, CardType.MOVE_TO,      0, "Move to Go"));
        deck.add(new Card(12, CardType.COLLECT,    100, "Collect 100"));
        deck.add(new Card(13, CardType.PAY,        100, "Pay 100"));
        deck.add(new Card(14, CardType.COLLECT,     10, "Collect 10"));
        deck.add(new Card(15, CardType.TO_JAIL,      0, "Go directly to Jail"));
    }
    
    public Card GetCard(int index) {
        return deck.get(index);
    }
}
