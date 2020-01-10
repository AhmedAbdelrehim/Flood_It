import java.util.Random;
import java.io.*;
/**
 * The class <b>GameModel</b> holds the model, the state of the systems. 
 * It stores the followiung information:
 * - the state of all the ``dots'' on the board (color, captured or not)
 * - the size of the board
 * - the number of steps since the last reset
 * - the current color of selection
 *
 * The model provides all of this informations to the other classes trough 
 *  appropriate Getters. 
 * The controller can also update the model through Setters.
 * Finally, the model is also in charge of initializing the game
 *
 * @author Guy-Vincent Jourdan, University of Ottawa
 */

// Ahmed Abdelrehim, 8394971
// Ahmed ElShafei, 7958212

public class GameModel implements Cloneable, Serializable {


    /**
     * predefined values to capture the color of a DotInfo
     */
    public static final int COLOR_0           = 0;
    public static final int COLOR_1           = 1;
    public static final int COLOR_2           = 2;
    public static final int COLOR_3           = 3;
    public static final int COLOR_4           = 4;
    public static final int COLOR_5           = 5;
    public static final int NUMBER_OF_COLORS  = 6;

    private int size;
    private int c;
    private DotInfo[][] dots;
    private int numberOfSteps;
    private int currentSelectedColor;
    //GameModel gameModel;


// ADD YOUR INSTANCE VARIABLES HERE

    /**
     * Constructor to initialize the model to a given size of board.
     * 
     * @param size
     *            the size of the board
     */
    public GameModel(int size) {

        this.size=size;
        
        //int c;
        Random r = new Random();

        dots = new DotInfo[this.size][this.size];
        for (int i = 0; i<this.size; i++){
            for (int j = 0; j<this.size; j++){
                c=r.nextInt(NUMBER_OF_COLORS);
                dots [i][j]= new DotInfo(i,j,c);
            }
        }
        numberOfSteps=0;
    }


    /**
     * Resets the model to (re)start a game. The previous game (if there is one)
     * is cleared up . 
     */
    public void reset(){
        
        int c;
        Random r = new Random();

        //dots = new DotInfo[size][size];
        for (int i = 0; i<size; i++){
            for (int j = 0; j<size; j++){
                c=r.nextInt(NUMBER_OF_COLORS);
                dots[i][j].setColor(c);
            }
        }
        deCapture();
        numberOfSteps=0;
    }


    /**
     * Getter method for the size of the game
     * 
     * @return the value of the attribute sizeOfGame
     */   
    public int getSize(){

        return size;

    }

    /**
     * returns the current color  of a given dot in the game
     * 
     * @param i
     *            the x coordinate of the dot
     * @param j
     *            the y coordinate of the dot
     * @return the status of the dot at location (i,j)
     */   
    public int getColor(int i, int j){
        
        return dots[i][j].getColor();

    }

    /**
     * returns true is the dot is captured, false otherwise
    * 
     * @param i
     *            the x coordinate of the dot
     * @param j
     *            the y coordinate of the dot
     * @return the status of the dot at location (i,j)
     */   
    public boolean isCaptured(int i, int j){

        return dots[i][j].isCaptured();
    }

    /**
     * Sets the status of the dot at coordinate (i,j) to captured
     * 
     * @param i
     *            the x coordinate of the dot
     * @param j
     *            the y coordinate of the dot
     */   
    public void capture(int i, int j){
         boolean t = true;
         dots[i][j].setCaptured(t);

   }


    /**
     * Getter method for the current number of steps
     * 
     * @return the current number of steps
     */   
    public int getNumberOfSteps(){

        return numberOfSteps;

    }

    /**
    * increse the number of steps by 1
    *
    */
    public void incrNumberOfSteps(){
        numberOfSteps++;
    }

    /**
     * Setter method for currentSelectedColor
     * 
     * @param val
     *            the new value for currentSelectedColor
    */   
    public void setCurrentSelectedColor(int val) {

        currentSelectedColor = val;

    }

    /**
     * Getter method for currentSelectedColor
     * 
     * @return currentSelectedColor
     */   
    public int getCurrentSelectedColor() {

        return currentSelectedColor;

    }


    /**
     * Getter method for the model's dotInfo reference
     * at location (i,j)
     *
      * @param i
     *            the x coordinate of the dot
     * @param j
     *            the y coordinate of the dot
     *
     * @return model[i][j]
     */   
    public DotInfo get(int i, int j) {

        return dots[i][j];

    }


   /**
     * The metod <b>step</b> updates the number of steps. It must be called 
     * once the model has been updated after the payer selected a new color.
     */
     public void step(){

        numberOfSteps++;

    }
 
   /**
     * The metod <b>isFinished</b> returns true iff the game is finished, that
     * is, all the dats are captured.
     *
     * @return true if the game is finished, false otherwise
     */
    public boolean isFinished(){

        for (int i = 0; i<size; i++){
            for (int j = 0; j<size; j++){
               if(!dots[i][j].isCaptured()){
                return false;
               }
            }
        }

        return true;
        

    }


   /**
     * Builds a String representation of the model
     *
     * @return String representation of the model
     */
    public String toString(){
         String s="";

        for (int i = 0; i<size; i++){
            for (int j = 0; j<size; j++){
               s=s+"("+dots[i][j].getX()+" , "+dots[i][j].getY()+ ", "+dots[i][j].getColor()+"),"; 
            }
            s=s+"\n";
        }

        return s;
    }

    
    /**
    *this method returns true if no dots are captured 
    */
    public boolean nonCaptured(){
        for (int i = 0; i<size; i++){
            for (int j = 0; j<size; j++){
               if(dots[i][j].isCaptured()){
                return false;
               }
            }
        }
        return true;
    }


    /**
    *set the passed dot to the passed color
    */
    public void setDots(int i, int j, int color){
        get(i,j).setColor(color);
    }


    /**
    *this method decapture all dots 
    */
    public void deCapture(){
        for (int i = 0; i<size; i++){
            for (int j = 0; j<size; j++){
               dots[i][j].setCaptured(false);
           }
        }
    }

    /**
    *this method creates a deep copy of the model
    *@return 
    *           return the cloned object
    */
    public Object clone() throws CloneNotSupportedException {


        GameModel gameModel = new GameModel(getSize());

        try{
           gameModel = (GameModel) super.clone();

        }catch (CloneNotSupportedException e){
            System.out.println("There is something wrong with the cloning!");
        }

        DotInfo[][] cloonedDots = new DotInfo[size][size];
        gameModel.dots = cloonedDots;
        gameModel.size = size;
        gameModel.c = c;
        gameModel.numberOfSteps = numberOfSteps;
        gameModel.currentSelectedColor=currentSelectedColor;

        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                cloonedDots[i][j] = new DotInfo(i, j, dots[i][j].getColor());
                if(isCaptured(i,j)){
                    cloonedDots[i][j].setCaptured(true);
                }else{
                     cloonedDots[i][j].setCaptured(false);
                }
            }
        }

        return gameModel;
    }
}