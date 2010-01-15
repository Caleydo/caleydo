package org.caleydo.core.view.opengl.canvas.remote.bucket.graphtype;

public class Tuple<T,V> {
	 private T object1;
	 private V object2;

	  public Tuple(T first, V second) {
	    object1 = first;
	    object2 = second;
	  }

	   public T getFirst() {
	     return object1;
	   }

	   public V getSecond() {
	     return object2;
	   }
	   
	   public boolean isEqual(T first, V second){
		   if ((object1.equals(first)) && (object2.equals(second)))
			   return true;
		   return false;
	   }
	}