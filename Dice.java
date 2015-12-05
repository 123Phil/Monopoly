
/* Dice Class
 * Modified: 20 April 2015  
 * Author(s): Phillip Stewart, Lap Nguyen, Supharinna Chimm
 * Description:
 *   Representation of the dice.
 *   Stores 2 dice values and contains a RNG.
 */

import java.util.Random;

public class Dice {

    /* ===== Private Members ==================================================
     * ======================================================================== */
	private int d1;
	private int d2;
	private Random rng;
	
	
    /* ===== Constructor ======================================================
     * ======================================================================== */
	public Dice(){
		d1 = 1;
		d2 = 2;
		rng = new Random();
	}
	
    /* ===== Public API =======================================================
     * ======================================================================== */
	public boolean IsDoubles(){
		return (d1 == d2);
	}
	public int GetTotal(){
		return d1+d2;
	}
	public int GetD1() {
	    return d1;
	}
	public int GetD2() {
        return d2;
    }
	
	public void Roll(){
		d1 = rng.nextInt(6) + 1;
		d2 = rng.nextInt(6) + 1;
	}
}
