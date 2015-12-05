
/* Box Class
 * Modified: 20 April 2015  
 * Author(s): Phillip Stewart
 * Description:
 *   Box is a simple data class with four public members
 *   which denote the bounds of the box.
 *   Boxes are used to simplify setting button and image bounds.
 */

public class Box {
    public int x;
    public int y;
    public int w;
    public int h;
    
    public Box(int a, int b, int c, int d) {
        x = a;
        y = b;
        w = c;
        h = d;
    }
}
