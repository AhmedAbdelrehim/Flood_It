// Ahmed Abdelrehim, 8394971
// Ahmed ElShafei, 7958212

import java.io.*;
public class LinkedStack<T> implements Stack<T>, Serializable{
	
	private static class Elem<T> implements Serializable{
		private T value;
		private Elem<T> next;

		private Elem(T value , Elem<T> next){
			this.value = value;
			this.next = next;
		}
	}

	private Elem<T> top;

	public boolean isEmpty(){
		return top == null;
	}

	public void push(T element){
		if(element == null){
			throw new NullPointerException ("can not pass a null as a pointer");
		}
		if(isEmpty()){
			top = new Elem<T> (element,null);
		} else  {
			Elem<T> newGuy = new Elem<T> (element,top);
			top = newGuy;

		}

	}

	public T pop(){
		if(isEmpty()){
			throw new EmptyStackException();
		} 
		Elem<T> toBePoped = top;
		top = top.next;
		
		return toBePoped.value;
	}

	public T peek(){
		if (isEmpty()){
			throw new EmptyStackException();
		}
		return top.value;
	}


}