
/* Deeds Test Class
 * Modified: 06 May 2015  
 * Author(s): Phillip Stewart, Lap Nguyen
 * Description:
 *   Black box tester for Deeds, using JUnit in Eclipse.
 */

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.ArrayList;


public class DeedsTest {
    private Deeds deeds;
    private Player p;

    @Test
    public void test() {
        this.deeds = new Deeds();
        this.p = new Player(false, "Player 1", 1, 0);
        
        testGetRent();
        testHouses();
        testOwner();
        testGettersSetters();
    }
    
    private void testGetRent() {
        assertEquals(deeds.GetRent(-1), 0);
        assertEquals(deeds.GetRent(0), 2);
        assertEquals(deeds.GetRent(21), 50);
        assertEquals(deeds.GetRent(27), 25);
        assertEquals(deeds.GetRent(28), 0);
        
        deeds.IncHouses(0);
        assertEquals(deeds.GetRent(0), 10);
        deeds.SetHouses(0, 5);
        assertEquals(deeds.GetRent(0), 250);
        
        deeds.SetOwner(0, null);
        deeds.SetOwner(1, null);
        deeds.SetHouses(0, 0);
    }
    
    private void testHouses() {
        // Ensure that numHouses cannot be set out of range:
        assertEquals(deeds.GetHouses(0), 0);
        deeds.SetHouses(0, -1);
        assertEquals(deeds.GetHouses(0), 0);
        deeds.SetHouses(0, 6);
        assertEquals(deeds.GetHouses(0), 0);
        deeds.DecHouses(0);
        assertEquals(deeds.GetHouses(0), 0);
        deeds.IncHouses(0);
        assertEquals(deeds.GetHouses(0), 1);
        deeds.DecHouses(0);
        assertEquals(deeds.GetHouses(0), 0);
        deeds.SetHouses(0, 5);
        assertEquals(deeds.GetHouses(0), 5);
        deeds.IncHouses(0);
        assertEquals(deeds.GetHouses(0), 5);
        
        // Test CanSell/Improve/Mortgage:
        deeds.SetHouses(0, 0);
        assertFalse(deeds.CanImprove(0));
        deeds.SetOwner(0, p);
        deeds.SetOwner(1, p);
        assertTrue(deeds.CanImprove(0));
        deeds.IncHouses(0);
        assertFalse(deeds.CanImprove(0));
        assertTrue(deeds.CanImprove(1));
        assertTrue(deeds.CanSell(0));
        deeds.DecHouses(0);
        assertFalse(deeds.CanSell(0));
        deeds.SetHouses(0, 5);
        assertFalse(deeds.CanImprove(0));
        assertFalse(deeds.CanMortgage(0));
        deeds.SetHouses(0, 0);
        assertTrue(deeds.CanMortgage(0));
        assertFalse(deeds.IsMortgaged(0));
        deeds.SetMortgaged(0, true);
        assertTrue(deeds.IsMortgaged(0));
        deeds.SetMortgaged(0, false);
        assertFalse(deeds.IsMortgaged(0));
        deeds.SetOwner(0, null);
        deeds.SetOwner(1, null);
    }
    
    private void testOwner() {
        ArrayList<Integer> props = deeds.GetProperties(p);
        assertTrue(props.isEmpty());
        assertNull(deeds.Monopoly(9));
        deeds.SetOwner(8, p);
        deeds.SetOwner(9, p);
        deeds.SetOwner(10, p);
        assertEquals(deeds.Monopoly(9), p);
        
        assertNull(deeds.Monopoly(25));
        assertEquals(deeds.GetNumRails(p), 0);
        deeds.SetOwner(24, p);
        assertEquals(deeds.GetNumRails(p), 1);
        deeds.SetOwner(25, p);
        assertEquals(deeds.GetNumRails(p), 2);
        deeds.SetOwner(26, p);
        assertEquals(deeds.GetNumRails(p), 3);
        deeds.SetOwner(27, p);
        assertEquals(deeds.GetNumRails(p), 4);
        assertEquals(deeds.Monopoly(25), p);
        
        assertNull(deeds.Monopoly(23));
        deeds.SetOwner(22, p);
        deeds.SetOwner(23, p);
        assertEquals(deeds.Monopoly(23), p);
        
        props = deeds.GetProperties(p);
        assertEquals(props.size(), 9);
        deeds.SetOwner(8, null);
        deeds.SetOwner(9, null);
        deeds.SetOwner(10, null);
        deeds.SetOwner(22, null);
        deeds.SetOwner(23, null);
        deeds.SetOwner(24, null);
        deeds.SetOwner(25, null);
        deeds.SetOwner(26, null);
        deeds.SetOwner(27, null);
        props = deeds.GetProperties(p);
        assertTrue(props.isEmpty());
        
    }
    
    private void testGettersSetters() {
        //GetName
        assertNull(deeds.GetName(-1));
        assertEquals(deeds.GetName(0), "Mediterranean Avenue");
        assertEquals(deeds.GetName(21), "Boardwalk");
        assertEquals(deeds.GetName(27), "Short Line");
        assertNull(deeds.GetName(28));
        
        //GetPrice
        assertEquals(deeds.GetPrice(-1), 0);
        assertEquals(deeds.GetPrice(0), 60);
        assertEquals(deeds.GetPrice(21), 400);
        assertEquals(deeds.GetPrice(27), 200);
        assertEquals(deeds.GetPrice(28), 0);
        
        //GetHousePrice
        assertEquals(deeds.GetHousePrice(-1), 0);
        assertEquals(deeds.GetHousePrice(0), 50);
        assertEquals(deeds.GetHousePrice(21), 200);
        assertEquals(deeds.GetHousePrice(27), 0);
        assertEquals(deeds.GetHousePrice(28), 0);
    }
}
