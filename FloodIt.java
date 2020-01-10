

/**
 * The class <b>FloodIt</b> launches the game
 *
 * @author Guy-Vincent Jourdan, University of Ottawa
 */

// Ahmed Abdelrehim, 8394971
// Ahmed ElShafei, 7958212

public class FloodIt {


   /*
     * <b>main</b> of the application. Creates the instance of  GameController 
     * and starts the game. If a game size (<12) is passed as parameter, it is 
     * used as the board size. Otherwise, a default value is passed
     * 
     * @param args
     *            command line parameters
     */
     public static void main(String[] args) {

        if (args.length == 0 ){
        	GameController game = new GameController(5); // put back to 12 after examining
        } else {
	        try {
		        int a = Integer.parseInt(args[0]);
		        if (a<1){
		        	throw new IllegalStateException("size needs to be greater than 1");
		        }

		        if(a<10){
		            a=12;
		        }

		        GameController game = new GameController(a);
		     }

		     catch(NumberFormatException e){
		     	System.out.println("NumberFormatException "+e.getMessage());
		     }

		     catch(IllegalStateException e){
		     	System.out.println(e.getMessage());
		     }

	   		}
	   		StudentInfo.display();
	}


}
