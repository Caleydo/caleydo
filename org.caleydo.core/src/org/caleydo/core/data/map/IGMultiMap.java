/**
 * 
 */
package org.caleydo.core.data.map;

// import java.util.ArrayList;
// import java.util.Map;

/**
 * This is simply a Map with slightly different semantics. Instead of returning
 * an Object, it returns a Collection with Integer. So for example, you can put(
 * key, new Integer(1) ); and then a Object get( key ); will return you a
 * Collection instead of an Integer. Thus, this is simply a tag interface.
 * 
 * @author Michael Kalkusch
 */
public interface IGMultiMap<T>
{

	public T remove(T key, T item);

}
