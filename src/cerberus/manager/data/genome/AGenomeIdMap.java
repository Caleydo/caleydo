/**
 * 
 */
package cerberus.manager.data.genome;

import java.util.HashMap;


/**
 * @author Michael Kalkusch
 *
 */
public abstract class AGenomeIdMap <K,V> 
implements IGenomeIdMap {

	protected HashMap <K,V> hashGeneric;
	
	/**
	 * 
	 */
	public AGenomeIdMap() {
		hashGeneric = new HashMap <K,V> ();
	}
	
	/**
	 * 
	 * @param iSizeHashMap define size of hashmap
	 */
	protected AGenomeIdMap( final int iSizeHashMap) {
		hashGeneric = new HashMap <K,V> (iSizeHashMap);
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.event.IEventPublisherMap#getIntByInt(int)
	 */
	public int getIntByInt(int key) {
		
		assert false : "getIntByInt() is not overloaded and thus can not be used!";
		return 0;
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.event.IEventPublisherMap#getIntByString(java.lang.String)
	 */
	public int getIntByString(String key) {

		assert false : "getIntByInt() is not overloaded and thus can not be used!";
		return 0;
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.event.IEventPublisherMap#getStringByInt(int)
	 */
	public String getStringByInt(int key) {

		assert false : "getIntByInt() is not overloaded and thus can not be used!";
		return "";
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.event.IEventPublisherMap#getStringByString(java.lang.String)
	 */
	public String getStringByString(String key) {

		assert false : "getIntByInt() is not overloaded and thus can not be used!";
		return "";
	}

}
