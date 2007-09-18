package org.geneview.core.data.collection.set.iterator;

//import java.util.Iterator;

public interface ISetDataIterator_Generic<E extends Number> {

	public void begin();
	
	public boolean hasNext();
	
	public boolean hasNextInSelection();
	
	public int nextIndex();
	
	public int nextSelection();
	
}
