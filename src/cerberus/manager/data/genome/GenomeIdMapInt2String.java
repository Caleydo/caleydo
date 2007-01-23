/**
 * 
 */
package cerberus.manager.data.genome;

import cerberus.manager.data.genome.AGenomeIdMap;
import cerberus.manager.data.genome.IGenomeIdMap;

/**
 * @author Michael Kalkusch
 *
 */
public class GenomeIdMapInt2String 
extends AGenomeIdMap <Integer,String> 
implements IGenomeIdMap {

	/**
	 * 
	 */
	public GenomeIdMapInt2String() {
		super();
	}

	/**
	 * @param iSizeHashMap
	 */
	public GenomeIdMapInt2String(int iSizeHashMap) {
		super(iSizeHashMap);
		
	}
	
	public void put( final String key, 
			final String value) {
		hashGeneric.put( Integer.valueOf(key), value);
	}



	/* (non-Javadoc)
	 * @see cerberus.manager.event.IEventPublisherMap#getStringByInt(int)
	 */
	public String getStringByInt(int key) {

		return hashGeneric.get(key);
	}

}
