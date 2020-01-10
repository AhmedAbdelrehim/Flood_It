import java.io.*;
/**
 * The class <b>DotInfo</b> is a simple helper class to store the initial color and state
 * (captured or not) at the dot position (x,y)
 *
 * @author Guy-Vincent Jourdan, University of Ottawa
 */

// Ahmed Abdelrehim, 8394971
// Ahmed ElShafei, 7958212


public class DotInfo implements Serializable {


// ADD YOUR INSTANCE VARIABLES HERE
    private int x;
    private int y;
    private int color;
    private boolean captured;


    /**
     * Constructor 
     * 
     * @param x
     *            the x coordinate
     * @param y
     *            the y coordinate
     * @param color
     *            the initial color
     */
    public DotInfo(int x, int y, int color){
        this.x=x;
        this.y=y;
        this.color=color;
        captured=false;

// ADD YOUR CODE HERE

    }

    /**
     * Getter method for the attribute x.
     * 
     * @return the value of the attribute x
     */
    public int getX(){

        return x;
    }
    
    /**
     * Getter method for the attribute y.
     * 
     * @return the value of the attribute y
     */
    public int getY(){

        return y;
    }
    
 
    /**
     * Setter for captured
     * @param captured
     *            the new value for captured
     */
    public void setCaptured(boolean captured) {
        this.captured=captured;

// ADD YOUR CODE HERE

    }

    /**
     * Get for captured
     *
     * @return captured
     */
    public boolean isCaptured(){
        return captured;

// ADD YOUR CODE HERE

    }

    /**
     * Get for color
     *
     * @return color
     */
    public int getColor() {

        return color;
    }

    public void setColor(int color){
        this.color = color;
    }
    
 }
