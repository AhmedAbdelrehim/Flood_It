
/**
 * The class <b>GameController</b> is the controller of the game. It has a method
 * <b>selectColor</b> which is called by the  when the player selects the next
 * color. It then computes the next step of the game, and  updates model and view.
 *
 * @author Guy-Vincent Jourdan, University of Ottawa
 */

// Ahmed Abdelrehim, 8394971
// Ahmed ElShafei, 7958212

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import javax.swing.*;
import java.io.*;


public class GameController implements ActionListener {

        private GameModel model;
        private GameView view;
        Stack<DotInfo> stack;
        private JFrame frame;
        private int mode;
        private JRadioButton plane;
        private JRadioButton torus;
        private JRadioButton orth;
        private JRadioButton diag;
        LinkedStack<GameModel> clone;
        LinkedStack<GameModel> redoClone;
        private DataStore data;



    /**
     * Constructor used for initializing the controller. It creates the game's view 
     * and the game's model instances
     * 
     * @param size
     *            the size of the board on which the game will be played
     */
    public GameController(int size) {

        if(isFileFound() && (size == ( (model=(data=deSerial()).getUndo().pop()).getSize() ) ) ) {
            clone = data.getUndo();
            redoClone = data.getRedo();
            mode = data.getMode();
            File file = new File("savedGame.ser");
            file.delete();
        }else{
            model = new GameModel(size);
            clone = new LinkedStack<GameModel>();
            redoClone = new LinkedStack<GameModel>();
            mode = 1;
            if(isFileFound()){
                File file = new File("savedGame.ser");
                file.delete();
            }
        }

        view = new GameView(model,this);
        stack = new LinkedStack<DotInfo>();
        plane = new JRadioButton("Plane", true);
        torus = new JRadioButton("torus");
        orth = new JRadioButton("Orthogonal", true);
        diag = new JRadioButton("Diagonal");
        view.update();
        addClone();

    }

    /**
     * resets the game
     */
    public void reset(){

        while(! clone.isEmpty() ){
            clone.pop();
        }
        while(! redoClone.isEmpty() ){
            redoClone.pop();
        }
        model.reset();
        addClone();
        view.update();

    }

    public boolean isRedoCaptured(){

        return redoClone.isEmpty();
    }

    public boolean isUndoCaptured(){

        return clone.isEmpty();
    }

    /**
     * Callback used when the user clicks a button
     *
     * @param e
     *            the ActionEvent
     */

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == view.reset ){
            reset();
        }else if (e.getSource() == view.quit){
        	try{
            GameModel temp = clone.pop();
            if(temp != clone.peek()){ 
                clone.push(temp);
                addClone();
            }
            data = new DataStore(clone, redoClone, mode);
            serial(data);
            System.exit(0);
        }
        catch(EmptyStackException ex){}
        }
        else if (e.getSource() == view.undo){

            if(!clone.isEmpty()){
                try{
                    redoClone.push(model);
                    model = clone.pop();
                }catch(EmptyStackException ex) {
                    System.out.println("Please select a new dot!");
                    model.deCapture();
                }
                view.update();
            }
            
        }
        else if (e.getSource() == view.redo){

            if(!redoClone.isEmpty()){

                try{
                    clone.push(model);
                    model = redoClone.pop();
                }catch(EmptyStackException ex) {
                    System.out.println("Empty Stack");
                }
               
                view.update();
            }
        }
        else if (e.getSource() == view.settings){
        	JPanel panel1 = new JPanel();
        	JLabel a = new JLabel("plane or torus");
        	panel1.setLayout(new GridLayout(6,1));
            ButtonGroup group1 = new ButtonGroup();
            group1.add(plane);
            group1.add(torus);
            JLabel b = new JLabel("Orthogonal or Diagonal");
            ButtonGroup group2 = new ButtonGroup();
            group2.add(orth);
            group2.add(diag);

            panel1.add(a);
            panel1.add(plane);
            panel1.add(torus);
            panel1.add(b);
            panel1.add(orth);
            panel1.add(diag);
            
            JOptionPane.showMessageDialog(null,panel1);
            if(plane.isSelected()&&orth.isSelected()){
            	mode=1;
            } else if (plane.isSelected()&&diag.isSelected()){
            	mode=2;
            } else if (torus.isSelected()&&orth.isSelected()){
            	mode=3;
            }else {
            	mode=4;
            }

        } else if (model.nonCaptured()){ // ie selecting the intial dot
        	for (int i = 0; i<model.getSize(); i++){
                for (int j = 0; j<model.getSize();j++){
                   if(e.getSource()== view.dotButtons[i][j]){
	                   model.capture(i,j);//capture the selected dot and it's neghbours (if any).
				       stack.push(model.get(i,j));
				       model.setCurrentSelectedColor(model.getColor(i,j));
				       view.update();
				       setMode(model.getColor(i,j),mode);
        				
                	}
                }
            }
        } else {

            for (int i = 0; i<model.getSize(); i++){
                for (int j = 0; j<model.getSize();j++){
                    if(e.getSource() == view.dotButtons[i][j]){
                        redoClone = new LinkedStack<GameModel>();

                        
                        if(view.dotButtons[i][j].getColor()!=model.getCurrentSelectedColor()){
                            addClone(); // cloning after each step
                        }
                        selectColor(view.dotButtons[i][j].getColor());
                    }
                }
            }
        }
        
    }

    /**
     * <b>selectColor</b> is the method called when the user selects a new color.
     * If that color is not the currently selected one, then it applies the logic
     * of the game to capture possible locations. It then checks if the game
     * is finished, and if so, congratulates the player, showing the number of
     * moves, and gives two options: start a new game, or exit
     * @param color
     *            the newly selected color
     *
     */
    public void selectColor(int color){
        
        if(color!=model.getCurrentSelectedColor()){
            model.step();// each time the user select a new color, the number of steps is incremented by 1.
            model.setCurrentSelectedColor(color); 
        }
        

        // pushing all captured dots into my stack.
        for(int k = 0; k<model.getSize(); k++){
            for(int j = 0; j < model.getSize(); j++){
                if(model.get(k,j).isCaptured()){
                   stack.push(model.get(k,j));
                }
            } 
        }
        
        
        setMode(color,mode);// specifying the mode from settings and capturing the dots accordingly

        view.update();

    	

    // when the game is done, tell the user his score and ask him if he wants to play again or no.
    if(model.isFinished()){
        Object[] options = {"Quit","Play Again"};
        int n = JOptionPane.showOptionDialog(view,"Congratulations, you won in "+model.getNumberOfSteps()+" steps,\n Would you like to play again?","Won!",
        JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,options, options[0]); 
            if (n==0){
                System.exit(0);
            }else{
                reset();
            }
     }

               
}
	/**
	* this method sets the mode of the game as the user chooses it from the setting. it captures the dots
	*according to the user preferences.
	*@param color
     *            the newly selected color
     *@param mode 
     *			  the mode adjusted from settings
	*/
	private void setMode(int color, int mode){
		if(mode==1){
        	while (!stack.isEmpty()){   
        	checkNeighboursPlaneOrtho(stack.pop(),color);
        	}
        } else if(mode==2){
        	while (!stack.isEmpty()){   
        	checkNeighboursPlaneDiagonal(stack.pop(),color);
        	}
        } else if(mode==3){
        	while (!stack.isEmpty()){   
        	checkNeighboursTorusOrtho(stack.pop(),color);
        	}
        } else{
        	while (!stack.isEmpty()){   
        	checkNeighboursTorusDiagonal(stack.pop(),color);
        	}
        }
	}

   /**
    * capturing all neighbours who are the same as selected color but are not captured 
    * 9 posibilities. 4 corners, 4 sides and 1 in the middle --> 9 if statements
    * @param dot 
    *               the dot to be compared with it's neighbours
    * @param color
    *              the color of the dot 
    *
    */
    private void checkNeighboursPlaneOrtho(DotInfo dot, int color){
            int boundary = model.getSize()-1;
            //corner 1
            if(dot.getX()==0&&dot.getY()==0){
                 if((!model.isCaptured(0,1))&&model.getColor(0,1)==color){
                    model.capture(0,1);
                    stack.push(model.get(0,1));
                 }
                 if((!model.isCaptured(1,0))&&model.getColor(1,0)==color){
                    model.capture(1,0);
                    stack.push(model.get(1,0));
                 }
            }

            //corner 2
            if(dot.getX()==0&&dot.getY()==boundary){
                if((! model.isCaptured(0,boundary-1))&&model.getColor(0,boundary-1)==color){
                    model.capture(0,boundary-1);
                    stack.push(model.get(0,boundary-1));
                 }
                 if((! model.isCaptured(1,boundary))&&model.getColor(1,boundary)==color){
                    model.capture(1,boundary);
                    stack.push(model.get(1,boundary));
                 }
            }

            //corner 3
            if(dot.getX()==boundary&&dot.getY()==0){
                if((! model.isCaptured(boundary-1,0))&&model.getColor(boundary-1,0)==color){
                    model.capture(boundary-1,0);
                    stack.push(model.get(boundary-1,0));
                 }
                 if((! model.isCaptured(boundary,1))&&model.getColor(boundary,1)==color){
                    model.capture(boundary,1);
                    stack.push(model.get(boundary,1));
                 }
                 

            }

            //corner 4
            if(dot.getX()==boundary&&dot.getY()==boundary){
                if((! model.isCaptured(boundary,boundary-1))&&model.getColor(boundary,boundary-1)==color){
                    model.capture(boundary,boundary-1);
                    stack.push(model.get(boundary,boundary-1));
                 }

                 if((! model.isCaptured(boundary-1,boundary))&&model.getColor(boundary-1,boundary)==color){
                    model.capture(boundary-1,boundary);
                    stack.push(model.get(boundary-1,boundary));
                 }
            }

            //side 1
            if(dot.getX()==0&&dot.getY()>0&&dot.getY()!=boundary){
                 if((! model.isCaptured(0,dot.getY()-1))&&model.getColor(0,dot.getY()-1)==color){
                    model.capture(0,dot.getY()-1);
                    stack.push(model.get(0,dot.getY()-1));
                 }
                 if((! model.isCaptured(0,dot.getY()+1))&&model.getColor(0,dot.getY()+1)==color){
                    model.capture(0,dot.getY()+1);
                    stack.push(model.get(0,dot.getY()+1));
                 }
                 if((! model.isCaptured(1,dot.getY()))&&model.getColor(1,dot.getY())==color){
                    model.capture(1,dot.getY());
                    stack.push(model.get(1,dot.getY()));

                 }

            }

            //side 2
            if(dot.getX()==boundary&&dot.getY()>0&&dot.getY()!=boundary){
                if((! model.isCaptured(boundary,dot.getY()-1))&&model.getColor(boundary,dot.getY()-1)==color){
                    model.capture(boundary,dot.getY()-1);
                    stack.push(model.get(boundary,dot.getY()-1));
                 }
                 if((! model.isCaptured(boundary,dot.getY()+1))&&model.getColor(boundary,dot.getY()+1)==color){
                    model.capture(boundary,dot.getY()+1);
                    stack.push(model.get(boundary,dot.getY()+1));
                 }
                 if((! model.isCaptured(boundary-1,dot.getY()))&&model.getColor(boundary-1,dot.getY())==color){
                    model.capture(boundary-1,dot.getY());
                    stack.push(model.get(boundary-1,dot.getY()));
                 }

            }


            //side 3
            if(dot.getX()>0&&dot.getX()!=boundary&&dot.getY()==0){
                 if((! model.isCaptured(dot.getX()-1,0))&&model.getColor(dot.getX()-1,0)==color){
                    model.capture(dot.getX()-1,0);
                    stack.push(model.get(dot.getX()-1,0));
                 }
                 if((! model.isCaptured(dot.getX()+1,0))&&model.getColor(dot.getX()+1,0)==color){
                    model.capture(dot.getX()+1,0);
                    stack.push(model.get(dot.getX()+1,0));
                 }
                 if((! model.isCaptured(dot.getX(),1))&&model.getColor(dot.getX(),1)==color){
                    model.capture(dot.getX(),1);
                    stack.push(model.get(dot.getX(),1));
                 }

            }

            //side 4
            if(dot.getX()>0&&dot.getX()!=boundary&&dot.getY()==boundary){
                 if((! model.isCaptured(dot.getX()-1,boundary))&&model.getColor(dot.getX()-1,boundary)==color){
                    model.capture(dot.getX()-1,boundary);
                    stack.push(model.get(dot.getX()-1,boundary));
                 }
                 if((! model.isCaptured(dot.getX()+1,boundary))&&model.getColor(dot.getX()+1,boundary)==color){
                    model.capture(dot.getX()+1,boundary);
                    stack.push(model.get(dot.getX()+1,boundary));
                 }
                 if((! model.isCaptured(dot.getX(),boundary-1))&&model.getColor(dot.getX(),boundary-1)==color){
                    model.capture(dot.getX(),boundary-1);
                    stack.push(model.get(dot.getX(),boundary-1));
                 }

            }

            // middle
            if(dot.getX()>0&&dot.getX()!=boundary&&dot.getY()>0&&dot.getY()!=boundary){
                if((! model.isCaptured(dot.getX()+1,dot.getY()))&&model.getColor(dot.getX()+1,dot.getY())==color){
                    model.capture(dot.getX()+1,dot.getY());
                    stack.push(model.get(dot.getX()+1,dot.getY()));
                }
                if((! model.isCaptured(dot.getX()-1,dot.getY()))&&model.getColor(dot.getX()-1,dot.getY())==color){
                    model.capture(dot.getX()-1,dot.getY());
                    stack.push(model.get(dot.getX()-1,dot.getY()));
                }
                if((! model.isCaptured(dot.getX(),dot.getY()+1))&&model.getColor(dot.getX(),dot.getY()+1)==color){
                    model.capture(dot.getX(),dot.getY()+1);
                    stack.push(model.get(dot.getX(),dot.getY()+1));
                }
                if((! model.isCaptured(dot.getX(),dot.getY()-1))&&model.getColor(dot.getX(),dot.getY()-1)==color){
                    model.capture(dot.getX(),dot.getY()-1);
                    stack.push(model.get(dot.getX(),dot.getY()-1));
                }
            }
            
    }

    
    private void checkNeighboursPlaneDiagonal(DotInfo dot, int color){
    	int boundary = model.getSize()-1;
    	checkNeighboursPlaneOrtho(dot,color);// Capture the normal plane ones first, then capture in the diagonal directions
    	 //corner 1
            if(dot.getX()==0&&dot.getY()==0){
                 if((!model.isCaptured(1,1))&&model.getColor(1,1)==color){
                    model.capture(1,1);
                    stack.push(model.get(1,1));
                 }
            }

            //corner 2
            if(dot.getX()==0&&dot.getY()==boundary){
                 if((! model.isCaptured(1,boundary-1))&&model.getColor(1,boundary-1)==color){
                    model.capture(1,boundary-1);
                    stack.push(model.get(1,boundary-1));
                 }

            }

            //corner 3
            if(dot.getX()==boundary&&dot.getY()==0){
                 if((! model.isCaptured(boundary-1,1))&&model.getColor(boundary-1,1)==color){
                    model.capture(boundary-1,1);
                    stack.push(model.get(boundary-1,1));
                 }
                 

            }

            //corner 4
            if(dot.getX()==boundary&&dot.getY()==boundary){
                 if((! model.isCaptured(boundary-1,boundary-1))&&model.getColor(boundary-1,boundary-1)==color){
                    model.capture(boundary-1,boundary-1);
                    stack.push(model.get(boundary-1,boundary-1));
                 }
            }

            //side 1
            if(dot.getX()==0&&dot.getY()>0&&dot.getY()!=boundary){
                 if((! model.isCaptured(1,dot.getY()-1))&&model.getColor(1,dot.getY()-1)==color){
                    model.capture(1,dot.getY()-1);
                    stack.push(model.get(1,dot.getY()-1));

                 }
                 if((! model.isCaptured(1,dot.getY()+1))&&model.getColor(1,dot.getY()+1)==color){
                    model.capture(1,dot.getY()+1);
                    stack.push(model.get(1,dot.getY()+1));

                 }


            }

            //side 2
            if(dot.getX()==boundary&&dot.getY()>0&&dot.getY()!=boundary){
                  if((! model.isCaptured(boundary-1,dot.getY()-1))&&model.getColor(boundary-1,dot.getY()-1)==color){
                    model.capture(boundary-1,dot.getY()-1);
                    stack.push(model.get(boundary-1,dot.getY()-1));
                 }
                 if((! model.isCaptured(boundary-1,dot.getY()+1))&&model.getColor(boundary-1,dot.getY()+1)==color){
                    model.capture(boundary-1,dot.getY()+1);
                    stack.push(model.get(boundary-1,dot.getY()+1));
                 }


            }


            //side 3
            if(dot.getX()>0&&dot.getX()!=boundary&&dot.getY()==0){
                 if((! model.isCaptured(dot.getX()-1,1))&&model.getColor(dot.getX()-1,1)==color){
                    model.capture(dot.getX()-1,1);
                    stack.push(model.get(dot.getX()-1,1));
                 }
                 if((! model.isCaptured(dot.getX()+1,1))&&model.getColor(dot.getX()+1,1)==color){
                    model.capture(dot.getX()+1,1);
                    stack.push(model.get(dot.getX()+1,1));
                 }

            }

            //side 4
            if(dot.getX()>0&&dot.getX()!=boundary&&dot.getY()==boundary){
                 if((! model.isCaptured(dot.getX()-1,boundary-1))&&model.getColor(dot.getX()-1,boundary-1)==color){
                    model.capture(dot.getX()-1,boundary-1);
                    stack.push(model.get(dot.getX()-1,boundary-1));
                 }
                 if((! model.isCaptured(dot.getX()+1,boundary-1))&&model.getColor(dot.getX()+1,boundary-1)==color){
                    model.capture(dot.getX()+1,boundary-1);
                    stack.push(model.get(dot.getX()+1,boundary-1));
                 }

            }

            // middle
            if(dot.getX()>0&&dot.getX()!=boundary&&dot.getY()>0&&dot.getY()!=boundary){
                if((! model.isCaptured(dot.getX()-1,dot.getY()-1))&&model.getColor(dot.getX()-1,dot.getY()-1)==color){
                    model.capture(dot.getX()-1,dot.getY()-1);
                    stack.push(model.get(dot.getX()-1,dot.getY()-1));
                }
                if((! model.isCaptured(dot.getX()-1,dot.getY()+1))&&model.getColor(dot.getX()-1,dot.getY()+1)==color){
                    model.capture(dot.getX()-1,dot.getY()+1);
                    stack.push(model.get(dot.getX()-1,dot.getY()+1));
                }
                if((! model.isCaptured(dot.getX()+1,dot.getY()+1))&&model.getColor(dot.getX()+1,dot.getY()+1)==color){
                    model.capture(dot.getX()+1,dot.getY()+1);
                    stack.push(model.get(dot.getX()+1,dot.getY()+1));
                }
                if((! model.isCaptured(dot.getX()+1,dot.getY()-1))&&model.getColor(dot.getX()+1,dot.getY()-1)==color){
                    model.capture(dot.getX()+1,dot.getY()-1);
                    stack.push(model.get(dot.getX()+1,dot.getY()-1));
                }
            }
    }

    
    private void checkNeighboursTorusOrtho(DotInfo dot, int color){
    	int boundary = model.getSize()-1;
    	checkNeighboursPlaneOrtho(dot,color); // capture all the normal plane dots, then the torus ones
            //corner 1
            if(dot.getX()==0&&dot.getY()==0){
                 
                 if((!model.isCaptured(0,boundary))&&model.getColor(0,boundary)==color){
                    model.capture(0,boundary);
                    stack.push(model.get(0,boundary));
                 }
                 if((!model.isCaptured(boundary,0))&&model.getColor(boundary,0)==color){
                    model.capture(boundary,0);
                    stack.push(model.get(boundary,0));
                 }

            }

            //corner 2
            if(dot.getX()==0&&dot.getY()==boundary){
                
                 if((! model.isCaptured(0,0))&&model.getColor(0,0)==color){
                    model.capture(0,0);
                    stack.push(model.get(0,0));
                 }
                 if((! model.isCaptured(boundary,boundary))&&model.getColor(boundary,boundary)==color) {
                    model.capture(boundary,boundary);
                    stack.push(model.get(boundary,boundary));
                 }
                 

            }

            //corner 3
            if(dot.getX()==boundary&&dot.getY()==0){
                
                 if((! model.isCaptured(0,0))&&model.getColor(0,0)==color){
                    model.capture(0,0);
                    stack.push(model.get(0,0));
                 }
                 if((! model.isCaptured(boundary,boundary))&&model.getColor(boundary,boundary)==color){
                    model.capture(boundary,boundary);
                    stack.push(model.get(boundary,boundary));
                 }
                 

            }

            //corner 4
            if(dot.getX()==boundary&&dot.getY()==boundary){
                
                  if((!model.isCaptured(0,boundary))&&model.getColor(0,boundary)==color){
                    model.capture(0,boundary);
                    stack.push(model.get(0,boundary));
                 }
                 if((!model.isCaptured(boundary,0))&&model.getColor(boundary,0)==color){
                    model.capture(boundary,0);
                    stack.push(model.get(boundary,0));
                 }

            }

            //side 1
            if(dot.getX()==0&&dot.getY()>0&&dot.getY()!=boundary){
                 
                 if((! model.isCaptured(boundary,dot.getY()))&&model.getColor(boundary,dot.getY())==color){
                    model.capture(boundary,dot.getY());
                    stack.push(model.get(boundary,dot.getY()));

                 }

            }

            //side 2
            if(dot.getX()==boundary&&dot.getY()>0&&dot.getY()!=boundary){
                
                 if((! model.isCaptured(0,dot.getY()))&&model.getColor(0,dot.getY())==color){
                    model.capture(0,dot.getY());
                    stack.push(model.get(0,dot.getY()));

                 }

            }


            //side 3
            if(dot.getX()>0&&dot.getX()!=boundary&&dot.getY()==0){
                
                 if((! model.isCaptured(dot.getX(),boundary))&&model.getColor(dot.getX(),boundary)==color){
                    model.capture(dot.getX(),boundary);
                    stack.push(model.get(dot.getX(),boundary));
                 }

            }

            //side 4
            if(dot.getX()>0&&dot.getX()!=boundary&&dot.getY()==boundary){
                 
                 if((! model.isCaptured(dot.getX(),0))&&model.getColor(dot.getX(),0)==color){
                    model.capture(dot.getX(),0);
                    stack.push(model.get(dot.getX(),0));
                 }

            }

    }

    public void checkNeighboursTorusDiagonal(DotInfo dot, int color){
    	int boundary = model.getSize()-1;
    	checkNeighboursTorusOrtho(dot,color); // capturing the dots from torus mode as it is already included in this mode
    	checkNeighboursPlaneDiagonal(dot,color); // capturing dots diagonally as it is already included in this mode

    	//corner 1
    	 if(dot.getX()==0 && dot.getY()==0){
                 
                 if((!model.isCaptured(boundary,boundary))&&model.getColor(boundary,boundary)==color){
                    model.capture(boundary,boundary);
                    stack.push(model.get(boundary,boundary));
                 }
                 if((!model.isCaptured(boundary,1))&&model.getColor(boundary,1)==color){
                    model.capture(boundary,1);
                    stack.push(model.get(boundary,1));
                 }
                 if((!model.isCaptured(1,boundary))&&model.getColor(1,boundary)==color){
                    model.capture(1,boundary);
                    stack.push(model.get(1,boundary));
                 }

            }

            //corner 2
            if(dot.getX()==0&&dot.getY()==boundary){
                
                 if((! model.isCaptured(boundary,0))&&model.getColor(boundary,0)==color){
                    model.capture(boundary,0);
                    stack.push(model.get(boundary,0));
                 }
                 if((! model.isCaptured(boundary,boundary-1))&&model.getColor(boundary,boundary-1)==color) {
                    model.capture(boundary,boundary-1);
                    stack.push(model.get(boundary,boundary-1));
                 }
                 if((! model.isCaptured(1,0))&&model.getColor(1,0)==color){
                    model.capture(1,0);
                    stack.push(model.get(1,0));
                 }

            }

            //corner 3
            if(dot.getX() == boundary&&dot.getY()==0){
                
                 if((! model.isCaptured(0,boundary))&&model.getColor(0,boundary)==color){
                    model.capture(0,boundary);
                    stack.push(model.get(0,boundary));
                 }
                 if((! model.isCaptured(boundary-1,boundary))&&model.getColor(boundary-1,boundary)==color){
                    model.capture(boundary-1,boundary);
                    stack.push(model.get(boundary-1,boundary));
                 }
                 if((! model.isCaptured(0,1))&&model.getColor(0,1)==color){
                    model.capture(0,1);
                    stack.push(model.get(0,1));
                 }
                 

            }

            //corner 4
            if(dot.getX()==boundary&&dot.getY()==boundary){
                
                 if((!model.isCaptured(0,0))&&model.getColor(0,0)==color){
                    model.capture(0,0);
                    stack.push(model.get(0,0));
                 }
                 if((!model.isCaptured(0,boundary-1))&&model.getColor(0,boundary-1)==color){
                    model.capture(0,boundary-1);
                    stack.push(model.get(0,boundary-1));
                 }
                 if((!model.isCaptured(boundary-1,0))&&model.getColor(boundary-1,0)==color){
                    model.capture(boundary-1,0);
                    stack.push(model.get(boundary-1,0));
                 }

            }

            //side 1
            if(dot.getX()==0&&dot.getY()>0&&dot.getY()!=boundary){
                 
                 if((! model.isCaptured(boundary,dot.getY()-1))&&model.getColor(boundary,dot.getY()-1)==color){
                    model.capture(boundary,dot.getY()-1);
                    stack.push(model.get(boundary,dot.getY()-1));

                 }
                 if((! model.isCaptured(boundary,dot.getY()+1))&&model.getColor(boundary,dot.getY()+1)==color){
                    model.capture(boundary,dot.getY()+1);
                    stack.push(model.get(boundary,dot.getY()+1));

                 }

            }

            //side 2
            if(dot.getX()==boundary&&dot.getY()>0&&dot.getY()!=boundary){
                
                 if((! model.isCaptured(0,dot.getY()-1))&&model.getColor(0,dot.getY()-1)==color){
                    model.capture(0,dot.getY()-1);
                    stack.push(model.get(0,dot.getY()-1));

                 }
                  if((! model.isCaptured(0,dot.getY()+1))&&model.getColor(0,dot.getY()+1)==color){
                    model.capture(0,dot.getY()+1);
                    stack.push(model.get(0,dot.getY()+1));

                 }

            }


            //side 3
            if(dot.getX()>0&&dot.getX()!=boundary&&dot.getY()==0){
                
                 if((! model.isCaptured(dot.getX()-1,boundary))&&model.getColor(dot.getX()-1,boundary)==color){
                    model.capture(dot.getX()-1,boundary);
                    stack.push(model.get(dot.getX()-1,boundary));
                 }
                 if((! model.isCaptured(dot.getX()+1,boundary))&&model.getColor(dot.getX()+1,boundary)==color){
                    model.capture(dot.getX()+1,boundary);
                    stack.push(model.get(dot.getX()+1,boundary));
                 }

            }

            //side 4
            if(dot.getX()>0&&dot.getX()!=boundary&&dot.getY()==boundary){
                 
                 if((! model.isCaptured(dot.getX()-1,0))&&model.getColor(dot.getX()-1,0)==color){
                    model.capture(dot.getX()-1,0);
                    stack.push(model.get(dot.getX()-1,0));
                 }
                 if((! model.isCaptured(dot.getX()+1,0))&&model.getColor(dot.getX()+1,0)==color){
                    model.capture(dot.getX()+1,0);
                    stack.push(model.get(dot.getX()+1,0));
                 }

            }

    }

    public void addClone(){
        try{
            clone.push((GameModel) model.clone());
        }catch (CloneNotSupportedException e){
            System.out.println("cloning PROBLEM!!");
        }
    }

    public GameModel getModel(){
        return model;
    }

    public void serial(DataStore data){

        try{

            FileOutputStream fileOut = new FileOutputStream("savedGame.ser");
            ObjectOutputStream outModel = new ObjectOutputStream(fileOut);
            outModel.writeObject(data);
            
            outModel.close();

            fileOut.close();

        }catch(IOException e){

            e.printStackTrace();    
        }
    }

    public DataStore deSerial(){

        DataStore dataStore = null;

        try {

            FileInputStream fileIn = new FileInputStream("savedGame.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            dataStore = (DataStore) in.readObject();

            in.close();
            fileIn.close();

            return dataStore;

      }catch(IOException e) {

            e.printStackTrace();

            return null;

      }catch(ClassNotFoundException e) {

            System.out.println("GameModel class not found");
            e.printStackTrace();

            return null;

      }

    }
    // This method  make sure that th serialising file exist
    public boolean isFileFound(){

        try {

            FileInputStream fileIn = new FileInputStream("savedGame.ser");

        }catch(Exception e) {
            
            return false;

        }
        return true;
    }
}
