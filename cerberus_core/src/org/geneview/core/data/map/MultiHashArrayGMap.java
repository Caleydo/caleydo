/**
 * 
 */
package org.geneview.core.data.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;



/**
 * @author Michael Kalkusch
 *
 */
public class MultiHashArrayGMap <Type, Value extends ArrayList<Type> >
extends HashMap <Type,Value>
implements Map <Type,Value > {
//implements IGMultiMap <Type>  {

	static final long serialVersionUID = 80806677;
	
	final int iDefaultLengthInternalArrayList = 3;
	
	/**
	 * @param arg0
	 * @param arg1
	 */
	public MultiHashArrayGMap(int arg0, float arg1) {

		super(arg0, arg1);
		
	}

	/**
	 * @param arg0
	 */
	public MultiHashArrayGMap(int arg0) {

		super(arg0);
		
	}

	/**
	 * 
	 */
	public MultiHashArrayGMap() {

		super();
		// TODO Auto-generated constructor stub
	}

//	/**
//	 * @param arg0
//	 */
//	public MultiHashArrayGMap(Map <Type,ArrayList<Type>>  arg0) {
//
//		super(arg0);
//		// TODO Auto-generated constructor stub
//	}

	/* (non-Javadoc)
	 * @see org.apache.commons.collections.MultiMap#remove(java.lang.Object, java.lang.Object)
	 */
	public Type removeItem(Type key, Type item) {

		ArrayList<Type> bufferArrayList = super.get( key );
	        
	    if ( bufferArrayList == null )
	       return null;
	        
	    bufferArrayList.remove( item );
	    
		return item;
	}
	

	 public void putAll( Map mapToPut )
	    {
//	        super.putAll( (GenericMultiMap <Type>) mapToPut );
	    }
	 
	 
	 public void putItem( Type key, Type value )
	    {
		 
			ArrayList<Type> bufferArrayList = super.get( key );
			 	
	        if ( bufferArrayList == null ) {
	        	/**
	        	 * new value!
	        	 */
	        	bufferArrayList = 
	        		new ArrayList<Type> (iDefaultLengthInternalArrayList);	        	
	        }
	        
	        bufferArrayList.add( value );
	        super.put( key, (Value) bufferArrayList );
		 
			//super.put(key, null);
		 
	    }
	 
//	 public Value put( Type key, Value value )
//	    {
//	        // NOTE:: put might be called during deserialization !!!!!!
//	        //        so we must provide a hook to handle this case
//	        //        This means that we cannot make MultiMaps of ArrayLists !!!
//	        
//		 	ArrayList<Type> bufferArrayList = super.get( key );
//		 			 	
//	        if ( bufferArrayList == null ) {
//	        	/**
//	        	 * new value!
//	        	 */
//	        	bufferArrayList = 
//	        		new ArrayList<Type> (iDefaultLengthInternalArrayList);	        	
//	        }
//	        
//	        bufferArrayList.add( value );
//	        return super.put( key, bufferArrayList );
//		 
//		 super.put(key, null);
//		 return value;
//	    }
	 
	 /**
	  * Checkes if value is already registerd to the key.
	  * 
	  * @param key
	  * @param value
	  * @return
	  */
	 public Object putChecked( Type key, Type value )
	    {
	        // NOTE:: put might be called during deserialization !!!!!!
	        //        so we must provide a hook to handle this case
	        //        This means that we cannot make MultiMaps of ArrayLists !!!
	        
		 	Value bufferArrayList = super.get( key );
		 			 	
	        if ( bufferArrayList == null ) {
	        	/**
	        	 * new value!
	        	 */
	        	bufferArrayList = (Value) new ArrayList<Type> (iDefaultLengthInternalArrayList);
	        	bufferArrayList.add( value );
	            return ( super.put( key, bufferArrayList ) );
	        }
	        
	        if  ( ! bufferArrayList.contains( value )) 
	        {
		        bufferArrayList.add( value );
		        return super.put( key, bufferArrayList );
	        }
	        return null;
	    }
	 
	 public boolean containsValue( Object value )
	    {
	        //Set<Entry <Type,ArrayList<Type>>> pairs =
	        Set<Entry <Type,Value> > pairs = 
	        	super.entrySet();
	        
	        if ( pairs == null )
	            return false;
	        
	        Iterator <Entry <Type,Value>> pairsIterator = 
	        	pairs.iterator();
	        
	        while ( pairsIterator.hasNext() ) {
	            Map.Entry <Type,Value> keyValuePair = 
	            	pairsIterator.next();
	            
	            ArrayList<Type> list = keyValuePair.getValue();
	            
	            if( list.contains( value ) )
	                return true;
	        }
	        return false;
	    }
	 
	 public void clear()
	    {
		 Set<Entry <Type,Value> > pairs = 
	        	super.entrySet();
	        
		 Iterator <Entry <Type,Value>> pairsIterator = 
	        	pairs.iterator();
		 
	        while ( pairsIterator.hasNext() ) {
	        	 Map.Entry <Type,Value> keyValuePair = 
		            	pairsIterator.next();
	        	 
	        	 ArrayList<Type> list = keyValuePair.getValue();
	            list.clear();
	        }
	        
	        super.clear();
	    }
	 
	 //public Collection <ArrayList<Type>> values()
	 public Collection <Value> values()
	    {
		 assert false : "values() in not implemented yet!";
	 
	       return null;
	    }
	 
	 
//	 public static ArrayList<Type> mergeRemoveCopies( 
//			 ArrayList<Type> first, 
//			 final ArrayList<Type> second) {
//		 
//		 first.ensureCapacity( first.size() + second.size() );		 		 
//		 Iterator <Type> iter = second.iterator();
//		 
//		 int iBuffer;
//		 while ( iter.hasNext() ) {			 
//			 if ( ! first.contains( iBuffer = iter.next() ) ) {
//				 first.add( iBuffer );
//			 }
//		 }
//		 
//		 return first;
//	 }

}
