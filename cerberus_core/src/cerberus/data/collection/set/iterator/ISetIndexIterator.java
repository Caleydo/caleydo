package cerberus.data.collection.set.iterator;

//import java.util.Iterator;

import cerberus.data.collection.IVirtualArray;

public interface ISetIndexIterator {

	public void begin();
	
	public boolean hasNext();
	
	public boolean hasNextInSelection();
	
	public int nextIndex();
	
	public IVirtualArray nextSelection();
	
}
