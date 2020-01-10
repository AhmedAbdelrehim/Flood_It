import java.awt.*;
import javax.swing.*;

/**
 * The class <b>GameView</b> provides the current view of the entire Game. It extends
 * <b>JFrame</b> and lays out the actual game and 
 * two instances of JButton. The action listener for the buttons is the controller.
 *
 * @author Guy-Vincent Jourdan, University of Ottawa
 */

// Ahmed Abdelrehim, 8394971
// Ahmed ElShafei, 7958212

public class GameView extends JFrame {

        public JButton reset;
        public JButton quit;
        private GameModel model;
        private GameController gameController;
        private JLabel numberOfSteps;
        private JPanel panel2;
        public DotButton[][] dotButtons;
        public JButton undo;
        public JButton redo;
        public JButton settings;
        

    /**
     * Constructor used for initializing the Frame
     * 
     * @param model
     *            the model of the game (already initialized)
     * @param gameController
     *            the controller
     */

    public GameView(GameModel model, GameController gameController) {

        super("Flood It");
        this.model = model;
        dotButtons = new DotButton[model.getSize()][model.getSize()];
        this.gameController = gameController;

        setSize(2000,2000);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel1 = new JPanel(); // drawing the panel that has the undo,redo and settings buttons
        panel1.setLayout(new GridLayout(1,3));
        add(panel1, BorderLayout.NORTH);

        
        undo = new JButton("Undo");
        undo.setBorder(BorderFactory.createRaisedBevelBorder());
        undo.addActionListener(gameController);
        panel1.add(undo);

        redo = new JButton("Redo");
        redo.addActionListener(gameController);
        redo.setBorder(BorderFactory.createRaisedBevelBorder());
        panel1.add(redo);

        settings = new JButton("Settings");
        settings.setBorder(BorderFactory.createRaisedBevelBorder());
        settings.addActionListener(gameController);
        panel1.add(settings);



        
        panel2 = new JPanel();//drawing the first panel that contains the dots
        add(panel2, BorderLayout.CENTER);
        panel2.setBorder(BorderFactory.createEmptyBorder (15,15,15,15));
        panel2.setLayout(new GridLayout(model.getSize(),model.getSize()));
        for (int i = 0; i<model.getSize(); i++){
            for (int j =0; j<model.getSize(); j++){
                dotButtons[i][j]=new DotButton(i,j,model.getColor(i,j),model.getSize());
                dotButtons[i][j].addActionListener(gameController);
                panel2.add(dotButtons[i][j]);
            }
        }

       

        JPanel panel3 = new JPanel(); // drawing the third panel that contains the quit and reset buttons as well as the number of steps
        add(panel3, BorderLayout.SOUTH);
        panel3.setBorder(BorderFactory.createEmptyBorder (10,10,10,10));
        panel3.setLayout(new GridLayout(1,3));

        numberOfSteps= new JLabel();
        numberOfSteps.setText("Select initial dot : ");
        numberOfSteps.setBorder(BorderFactory.createEmptyBorder (5,5,5,5));
        panel3.add(numberOfSteps);

        reset = new JButton("reset");
        reset.setBorder(BorderFactory.createRaisedBevelBorder());
        reset.addActionListener(gameController);
        panel3.add(reset);

        quit = new JButton("quit");
        quit.setBorder(BorderFactory.createRaisedBevelBorder());
        quit.addActionListener(gameController);
        panel3.add(quit);

        pack();
        setVisible(true);



    }

    /**
     * update the status of the board's DotButton instances based on the current game model
     */

    public void update(){
        // changing the color and updating UI

        if(!gameController.isRedoCaptured()){
            redo.setEnabled(true);
        }
        else{redo.setEnabled(false);}

        if(!gameController.isUndoCaptured()){
            undo.setEnabled(true);
        }
        else{undo.setEnabled(false);}

        model = gameController.getModel();

        for (int i = 0; i<model.getSize(); i++){
            for (int j =0; j<model.getSize(); j++){
                if(model.get(i,j).isCaptured()){
                    model.setDots(i,j,model.getCurrentSelectedColor());
                    dotButtons[i][j].setColor(model.get(i,j).getColor());
                }else{
                    dotButtons[i][j].setColor(model.get(i,j).getColor());
                }
            }
        }



        if(model.nonCaptured()){
            numberOfSteps.setText("Select initial dot ");
            pack(); 
        } else {
            numberOfSteps.setText("Number of steps : "+model.getNumberOfSteps());
            pack();
        }
        //gameController.addClone();


    }
}
