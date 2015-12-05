
/* Deeds Class
 * Modified: 02 May 2015  
 * Author(s): Phillip Stewart, Lap Nguyen
 * Description:
 *     Representation of the properties for the game.
 *   Deeds maintains a collection of deed objects, defined inside the class.
 *   Each deed object tracks the owner and development on that property,
 *   and so the Deeds class is responsible for checking rent and monopoly.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Deeds {

    /* ===== Deed Class =======================================================
     * ======================================================================== */
    public class Deed {

        protected String name;
        protected ArrayList<Integer> rent;
        protected int price;
        protected int housePrice;
        protected int numHouses;
        protected Player owner;
        protected boolean mortgaged;

        /* ===== Deed Constructor =============================================
         * ==================================================================== */
        public Deed(String nm, List<Integer> rents, int price, int hp){
            this.name = nm;
            this.rent = new ArrayList<Integer>(rents);
            this.price = price;
            this.housePrice = hp;

            this.numHouses = 0;
            this.owner = null;
            this.mortgaged = false;
        }

        public int GetRent() {
            if (this.numHouses < 0 || this.numHouses > 5) {
                System.err.println("Error: invalid number of houses.");
                return 0;
            }
            return this.rent.get(this.numHouses);
        }
        
    }

    /* ===== Deeds list =======================================================
     * ======================================================================== */
    private List<Deed> properties;
    public Deeds() {
        properties = new ArrayList<Deed>();
        properties.add(new Deed("Mediterranean Avenue", Arrays.asList(2,10,30,90,160,250),        60,50));
        properties.add(new Deed("Baltic Avenue",        Arrays.asList(4,20,60,180,320,450),       60, 50));//0
        properties.add(new Deed("Oriental Avenue",      Arrays.asList(6,30,90,270,400,550),       100,5));
        properties.add(new Deed("Vermont Avenue",       Arrays.asList(6,30,90,270,400,550),       100,50));
        properties.add(new Deed("Connecticut Avenue",   Arrays.asList(8,40,100,300,450,600),      120,50));
        properties.add(new Deed("St. Charles Place",    Arrays.asList(10,50,150,450,625,750),     140,100));
        properties.add(new Deed("States Avenue",        Arrays.asList(10,50,150,450,625,750),     140,100));
        properties.add(new Deed("Virginia Avenue",      Arrays.asList(12,60,180,500,700,900),     160,100));
        properties.add(new Deed("St. James Place",      Arrays.asList(14,70,200,550,750,950),     180,100));
        properties.add(new Deed("Tennesse Avenue",      Arrays.asList(14,70,200,550,750,950),     180,100));
        properties.add(new Deed("New York Avenue",      Arrays.asList(16,80,220,600,800,1000),    200,100));
        properties.add(new Deed("Kentucky Avenue",      Arrays.asList(18,90,250,700,875,1050),    220,150));
        properties.add(new Deed("Indiana Avenue",       Arrays.asList(18,90,250,700,875,1050),    220,150));
        properties.add(new Deed("Illinois Avenue",      Arrays.asList(20,100,300,750,925,110),    240,150));
        properties.add(new Deed("Atlantic Avenue",      Arrays.asList(22,110,330,800,975,1150),   260,150));
        properties.add(new Deed("Ventnor Avenue",       Arrays.asList(22,110,330,800,975,1150),   260,150));
        properties.add(new Deed("Marvin Garden",        Arrays.asList(24,120,360,850,1025,1200),  280,150));
        properties.add(new Deed("Pacific Avenue",       Arrays.asList(26,130,390,900,1100,1275),  300,200));
        properties.add(new Deed("North Carolina Avenue",Arrays.asList(26,130,390,900,1100,1275),  300,200));
        properties.add(new Deed("Pennsylvania Avenue",  Arrays.asList(28,150,450,1000,1200,1400), 320,200));
        properties.add(new Deed("Park Place",           Arrays.asList(35,175,500,1100,1300,1500), 350,200));
        properties.add(new Deed("Boardwalk",            Arrays.asList(50,200,600,1400,1700,2000), 400,200));
        properties.add(new Deed("Electric Company",     Arrays.asList(0),   150,0));//22
        properties.add(new Deed("Waterworks",           Arrays.asList(0),   150,0));//23
        properties.add(new Deed("Reading Railroad",     Arrays.asList(25),  200,0));
        properties.add(new Deed("Pennsylvania Railroad",Arrays.asList(25),  200,0));
        properties.add(new Deed("B&O Railroad",         Arrays.asList(25),  200,0));
        properties.add(new Deed("Short Line",           Arrays.asList(25),  200,0));
    }


    /* ===== Deeds API ========================================================
     * ======================================================================== */

    // Returns the owner of a monopoly by color, or null if not monopoly
    public Player Monopoly(int deedNum) {
        if (deedNum < 0 || deedNum > 27) {return null;}
        ArrayList<Integer> group = new ArrayList<Integer>(GetGroup(deedNum));
        Player p = this.properties.get(group.get(0)).owner;
        for (Integer i : group) {
            if (p != this.properties.get(i).owner) {
                p = null;
                break;
            }
        }
        return p;
    }

    public ArrayList<Integer> GetProperties(Player owner) {
        ArrayList<Integer> deeds = new ArrayList<Integer>();
        int i = 0;
        for (Deed d : this.properties) {
            if (d.owner == owner) {
                deeds.add(i);
            }
            i++;
        }
        return deeds;
    }

    public int GetNumRails(Player p) {
        ArrayList<Integer> props = GetProperties(p);
        int count = 0;
        ArrayList<Integer> rails = new ArrayList<Integer>(Arrays.asList(24, 25, 26, 27));
        for(Integer i : rails) {
            if (props.contains(i)) {
                count++;
            }
        }
        return count;
    }
    
    public int GetRent(int deedNum) {
        if (deedNum < 0 || deedNum > 27) {return 0;}
        return this.properties.get(deedNum).GetRent();
    }
    
	public boolean CanSell(int deedNum) {
        if (deedNum < 0 || deedNum > 27) {return false;}
		ArrayList<Integer> group = new ArrayList<Integer>(GetGroup(deedNum));
		int num_houses = GetHouses(deedNum);
        if (num_houses == 0) {
            return false;
        }
		int Even = this.GetHouses(group.get(0));
		for(int i:group){
			Even = Math.max(Even,this.GetHouses(i));
		}
		if(this.GetHouses(deedNum)==Even){
			if(this.GetHouses(deedNum)==5){
			}
			else{
			}		
		}
		return true;
	}

	public boolean CanImprove(int deedNum) {
        if (deedNum < 0 || deedNum > 27) {return false;}
	    int num_houses = GetHouses(deedNum);
	    if (num_houses == 5 || Monopoly(deedNum) == null) {
	        return false;
	    }
	    ArrayList<Integer> group = new ArrayList<Integer>(GetGroup(deedNum));
	    for(int i:group) {
	        if (!(GetHouses(i) == num_houses || GetHouses(i) == num_houses+1)) {
	            return false;
	        }
	    }
	    return true;
	}
    
    public boolean CanMortgage(int deedNum){
        if (deedNum < 0 || deedNum > 27) {return false;}
    	return (!this.IsMortgaged(deedNum) && this.GetNumHouses(deedNum)==0);
    }

	/* ===== Class methods ====================================================
     * ======================================================================== */
    private List<Integer> GetGroup(int deedNum) {
        if (deedNum < 0) {
            return null;
        } else if (deedNum < 2) {
            return Arrays.asList(0,1);//Brown
        } else if (deedNum < 5) {
            return Arrays.asList(2,3,4);//Blue
        } else if (deedNum < 8) {
            return Arrays.asList(5,6,7);//Magenta
        } else if (deedNum < 11) {
            return Arrays.asList(8,9,10);//Orange
        } else if (deedNum < 14) {
            return Arrays.asList(11,12,13);//Red
        } else if (deedNum < 17) {
            return Arrays.asList(14,15,16);//Yellow
        } else if (deedNum < 20) {
            return Arrays.asList(17,18,19);//Green
        } else if (deedNum < 22) {
            return Arrays.asList(20,21);//Navy
        } else if (deedNum < 24) {
            return Arrays.asList(22,23);//Util
        } else if (deedNum < 28) {
            return Arrays.asList(24,25,26,27);//Rail
        } else {
            return null;
        }
    }

    /* ===== Getters/Setters ==================================================
     * ======================================================================== */
    public Deed GetDeed(int deedNum) {
        if (deedNum < 0 || deedNum > 27) {return null;}
        return this.properties.get(deedNum);
    }
    public String GetName(int deedNum) {
        if (deedNum < 0 || deedNum > 27) {return null;}
        return this.properties.get(deedNum).name;
    }
    public Player GetOwner(int deedNum) {
        if (deedNum < 0 || deedNum > 27) {return null;}
        return this.properties.get(deedNum).owner;
    }
    public void SetOwner(int deedNum, Player p) {
        if (deedNum < 0 || deedNum > 27) {return;}
        this.properties.get(deedNum).owner = p;
    }
    public int GetPrice(int deedNum) {
        if (deedNum < 0 || deedNum > 27) {return 0;}
        return this.properties.get(deedNum).price;
    }
    public int GetHousePrice(int deedNum){
        if (deedNum < 0 || deedNum > 27) {return 0;}
        return this.properties.get(deedNum).housePrice;
    }
    public int GetNumHouses(int deedNum) {
        if (deedNum < 0 || deedNum > 27) {return 0;}
        return this.properties.get(deedNum).numHouses;
    }
    public boolean IsMortgaged(int deedNum) {
        if (deedNum < 0 || deedNum > 27) {return false;}
        return this.properties.get(deedNum).mortgaged;
    }
    public void SetMortgaged(int deedNum, boolean b) {
        if (deedNum < 0 || deedNum > 27) {return;}
        this.properties.get(deedNum).mortgaged = b;
    }
    public void IncHouses(int deedNum) {
        if (deedNum < 0 || deedNum > 27) {return;}
        if (properties.get(deedNum).numHouses == 5) {
            return;
        }
		properties.get(deedNum).numHouses++;
	}
	public void DecHouses(int deedNum) {
        if (deedNum < 0 || deedNum > 27) {return;}
	    if (properties.get(deedNum).numHouses == 0) {
	        return;
	    }
		properties.get(deedNum).numHouses--;
	}
	public int GetHouses(int deedNum) {
        if (deedNum < 0 || deedNum > 27) {return 0;}
		return properties.get(deedNum).numHouses;	
	}

	public void SetHouses(int i, int j) {
        if (i < 0 || i > 27 || j < 0 || j > 5) {return;}
		this.properties.get(i).numHouses = j;
	}


}