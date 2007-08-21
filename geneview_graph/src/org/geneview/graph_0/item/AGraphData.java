/**
 * 
 */
package org.geneview.graph.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Abstract class handling data objects inside the graph.
 * 
 * @author Michael Kalkusch
 *
 */
public abstract class AGraphData <Data> {

	/**
	 * define initial size of internal data object. 
	 */
	private static final int iInitialSize = 3;

	/** 
	 * Lookup table for key/value = Id/data object.
	 */
	private HashMap<Integer, Data> hashId2GraphData;

	
	/* ------------------ */
	/* --- GRAPH DATA --- */
	/* ------------------ */
	
	/**
	 * Default constructor.  
	 */
	protected AGraphData () {
		this( iInitialSize );
    }
	
	/**
	 * Constructor defines size of initial Collection and internal data structures.
	 * 
	 * @param iInitialSize initial size of Collection
	 * 
	 * @see AGraphData#listData
	 * @see AGraphData#hashId2GraphData 
	 */
	protected AGraphData ( int iInitialSize ) {
		hashId2GraphData = new HashMap <Integer,Data> (iInitialSize);
	}
	
	
	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphObject#containsData(int)
	 */
	public final boolean containsData(int identifier) {
		return hashId2GraphData.containsKey(identifier);
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphObject#containsDataObject(java.lang.Object)
	 */
	public final boolean containsDataObject(Object data) {
		return this.hashId2GraphData.containsValue( data );
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphObject#getAllData()
	 */
	public final synchronized Collection<Data> getAllDataCloned() {
		/** create a copy of all data values as a Collection. */
		ArrayList <Data> clone = new ArrayList <Data> (hashId2GraphData.size());
		
		Iterator <Data> iter= hashId2GraphData.values().iterator();
		while ( iter.hasNext() ) {
			clone.add( iter.next() );
		}
	
		return clone;
	}
	
	/**
	 * Attention: expose internal data structure as Collection.
	 * 
	 * @see org.geneview.graph.IGraphObject#getAllData()
	 * @see java.util.HashMap#values()
	 */
	public final synchronized Collection<Data> getAllData() {
	
		return hashId2GraphData.values();
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphObject#getData(int)
	 */
	public final Data getData(int identifier) {
		return this.hashId2GraphData.get( identifier );
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphObject#removeAllData()
	 */
	public final synchronized void removeAllData() {
		this.hashId2GraphData.clear();
	}

	/**
	 * 
	 * @see java.util.HashMap#remove(Object)
	 * @see org.geneview.graph.IGraphObject#removeData(int)
	 */
	public final synchronized Data removeData(int identifier) {
		return this.hashId2GraphData.remove(identifier);
	}

	/**
	 * @return TURE if identifier was not registered previously, FLASE if identifier was already stored.
	 * 
	 * @see org.geneview.graph.IGraphObject#setData(int, java.lang.Object)
	 */
	public final boolean setData(int identifier, Data data) {
		
		if ( this.hashId2GraphData.put(identifier, data) != null ) {
			/** special case: identifier is already registered and will be replaced. */			
			assert false : "overwrite exisitng element";
			return false;
		}
		return true;
	}

}
