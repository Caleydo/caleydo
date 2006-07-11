package cerberus.data.collection.set.iterator;

import java.util.Iterator;

import cerberus.data.collection.Selection;

public interface SetIndexIterator {

	public void begin();
	
	public boolean hasNext();
	
	public boolean hasNextInSelection();
	
	public int nextIndex();
	
	public Selection nextSelection();
	
}
