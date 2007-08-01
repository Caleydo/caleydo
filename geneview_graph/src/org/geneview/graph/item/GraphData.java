/**
 * 
 */
package org.geneview.graph.item;

import java.util.HashMap;

import org.geneview.graph.IGraphData;

/**
 * @author Michael Kalkusch
 *
 */
public class GraphData implements IGraphData {

	public static final HashMap <String,Integer> identifier_key_value = new HashMap <String, Integer> ();
	
	public static final HashMap <Integer,String> identifier_key_value_reverse = 
		new HashMap <Integer,String> ();
	
	public static final int getIdentifier( final String key ) {
		return identifier_key_value.get(key);
	}
	
	public static final boolean setIdentifier( final String key, final int value) {
		if ( identifier_key_value.containsKey(key)) {
			return false;
		}
		identifier_key_value.put(key, value);
		return true;
	}
	
	public static final boolean removeIdentifier( final String key ) {
		if ( identifier_key_value.containsKey(key)) {
			Integer buffer = identifier_key_value.remove(key);
			identifier_key_value_reverse.remove(buffer);
			return true;
		}
		return false;
	}
	
	public static final void removeAllIdentifier( ) {
		identifier_key_value.clear();
		identifier_key_value_reverse.clear();		
	}
	
	/**
	 * 
	 */
	public GraphData() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphData#getDate()
	 */
	public Object getData() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphData#getIdentifier()
	 */
	public String getIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphData#setData(java.lang.Object)
	 */
	public void setData(Object data) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphData#setIdentifier(java.lang.String)
	 */
	public void setIdentifier(String identifier) {
		// TODO Auto-generated method stub

	}

}
