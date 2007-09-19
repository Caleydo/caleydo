/**
 * 
 */
package org.geneview.util.graph.item;

import java.util.HashMap;

/**
 * @author Michael Kalkusch
 *
 */
public final class GraphItemDataSequenzer {
	
	private static final int iInitialSize = 3;
	
	private static HashMap <String,Integer> identifier_key_value = null;
	private static HashMap <Integer,String> identifier_key_value_reverse = null;
	
//	private static final HashMap <String,Integer> identifier_key_value = 
//		new HashMap <String, Integer> (iInitialSize);
//	private static final HashMap <Integer,String> identifier_key_value_reverse = 
//		new HashMap <Integer,String> (iInitialSize);

	
	/**
	 * Hide constructor.
	 */
	private GraphItemDataSequenzer() {
		/** hide constructor */
	}
	
	/**
	 * call this method once.
	 * 
	 * @return TRUE if method is called for the first time, FLASE else
	 */
	public static final boolean init( ) {
		
		boolean successfulInit = true;
		
		if ( identifier_key_value == null ) 
		{
			identifier_key_value = 
				new HashMap <String, Integer> (iInitialSize);
		} 
		else 
		{
			successfulInit = false;
		}
		
		if ( identifier_key_value_reverse == null ) 
		{
			identifier_key_value_reverse = 
				new HashMap <Integer,String> (iInitialSize);
		} 
		else 
		{
			successfulInit = false;
		}
		 
		 return successfulInit;
	}
	
	public static final int getIdentifier( final String key ) {
		return identifier_key_value.get(key).intValue();
	}
	
	public static final boolean setIdentifier( final String key, final int value) {
		if ( identifier_key_value.containsKey(key)) 
		{
			return false;
		}
		identifier_key_value.put(key, new Integer(value));
		return true;
	}
	
	public static final boolean removeIdentifier( final String key ) {
		if ( identifier_key_value.containsKey(key)) 
		{
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
}
