// Ahmed Abdelrehim, 8394971
// Ahmed ElShafei, 7958212

import java.io.*;

public class DataStore implements Serializable{
	private LinkedStack<GameModel> undo;
	private LinkedStack<GameModel> redo;
	private int mode;

	public DataStore(LinkedStack<GameModel> undo, LinkedStack<GameModel> redo, int mode){
		this.undo = undo;
		this.redo = redo;
		this.mode = mode;
	}
	public LinkedStack<GameModel> getUndo(){
		return undo;
	}
	public LinkedStack<GameModel> getRedo(){
		return redo;
	}
	public int getMode(){
		return mode;
	}
}