
/* Card Class
 * Modified: 20 April 2015  
 * Author(s): Phillip Stewart, Lap Nguyen
 * Description:
 *   Card is a super-class for the Community Chest and Chance cards.
 */

enum CardType {COLLECT, PAY, COLLECT_ALL, PAY_ALL, JAIL_FREE, TO_JAIL, MOVE, MOVE_TO, ASSESS, RAILROAD, UTILITY}

public class Card {
	private int index;
	private CardType type;
	private int value; //money amount, #squares to move, or square #to move to
	private String text;
	
	public Card(int id, CardType ct, int val, String txt) {
		this.index = id;
		this.value = val;
		this.type = ct;
		this.text = txt;
	}
	
	public String GetText(){
		return this.text;
	}
	public int GetIndex() {
	    return this.index;
	}
	public int GetValue() {
	    return this.value;
	}
	public CardType GetType() {
	    return this.type;
	}
}
