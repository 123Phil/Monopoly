
/* Player Class
 * Modified: 01 May 2015  
 * Author(s): Phillip Stewart, Lap Nguyen, Supharinna Chimm
 * Description:
 *     Represents a player's data and methods for the game.
 */

import java.util.ArrayList;
import java.util.Collections;


public class Player {
    /* ===== Private Members ==================================================
     * ======================================================================== */
    private int playerID;
	private Boolean AI;
	private int money;
	private int position;
	private boolean has_chance_jailcard;
    private boolean has_commch_jailcard;
	private ArrayList<Integer> deedList;
	private boolean inJail;
	private int jailbreakTries;
	private String name;
	private int token;
	
	
	/* ===== Constructor ======================================================
     * ======================================================================== */
	public Player(Boolean ai, String name, int num, int tok_num) {
	    this.playerID = num;
	    this.AI = ai;
	    this.money = 1500;
	    this.name = name;
	    this.position = 0;
	    this.inJail = false;
	    this.deedList = new ArrayList<Integer>();
	    this.token = tok_num;
	}
	
	
	/* ===== Private Members ==================================================
     * ======================================================================== */
	public int GetPnum() {
	    return this.playerID;
	}
	public String GetName() {
		return this.name;
	}
	public Boolean IsAI() {
		return this.AI;
	}
	public void SetAI(Boolean ai) {
		this.AI = ai;
	}
	public int GetMoney() {
		return this.money;
	}
	public void SetMoney(int money){
        this.money = money;
    }
	public int GetPosition() {
		return this.position;
	}
	public void SetPosition(int pos) {
		this.position = pos;
	}
	public boolean IsInJail() {
		return this.inJail;
	}
	public void SetJail(boolean jail) {
		this.inJail = jail;
	}
	public int GetJailbreakTries() {
		return this.jailbreakTries;
	}
	public void AddMoney(int money){
		this.money += money;
	}
	public void Sort() {
		Collections.sort(this.deedList);
	}
	public void SetJail(){
		this.inJail = true;
		this.jailbreakTries = 0;
		this.position = 10;
	}
	public void IncTries(){
	    this.jailbreakTries++;
	}
	public boolean HasChanceJailcard() {
		return this.has_chance_jailcard;
	}
	public void SetChanceJailcard(boolean has_chance_jailcard) {
		this.has_chance_jailcard = has_chance_jailcard;
	}
	public boolean HasCommChestJailcard() {
		return this.has_commch_jailcard;
	}
	public void SetCommChestJailcard(boolean has_commch_jailcard) {
		this.has_commch_jailcard = has_commch_jailcard;
	}
	public ArrayList<Integer> GetDeedList() {
		return this.deedList;
	}
	public void AddDeed(int deed) {
		deedList.add(deed);
		//sort deeds
	}
	public void RemoveDeed(int deed){
		int index = deedList.indexOf(deed);
		deedList.remove(index);
	}
	public void SetToken(int t) {
	    this.token = t;
	}
	public int GetToken() {
	    return this.token;
	}
}