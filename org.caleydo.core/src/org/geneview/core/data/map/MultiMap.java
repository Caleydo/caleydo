/**
 * 
 */
package org.geneview.core.data.map;

import java.util.Map;


/** 
 * This is simply a Map with slightly different semantics.
 * Instead of returning an Object, it returns a Collection.
 * So for example, you can put( key, new Integer(1) ); 
 * and then a Object get( key ); will return you a Collection 
 * instead of an Integer.
 * Thus, this is simply a tag interface.
 *
 * @author Michael Kalkusch
 */
public interface MultiMap extends Map {
    
    public Object remove( Object key, Object item );
   
}
