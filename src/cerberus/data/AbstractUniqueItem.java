/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.data;

import cerberus.data.UniqueInterface;

/**
 * Abstract class providing methodes defiend in UniqueManagedInterface.
 * Stores reference to creator of item in private variable.
 * 
 * @author Michael Kalkusch
 *
 * @see prometheus.data.xml.MementiItemXML
 */
public abstract class AbstractUniqueItem 
implements UniqueInterface {

	/**
	 * Unique Id
	 */
	private int iCollectionId;
	
	/**
	 * 
	 */
	protected AbstractUniqueItem( int iSetCollectionId ) {
		
		iCollectionId = iSetCollectionId;
	}
	
	/**
	 * Get Id by calling prometheus.data.collection.BaseManagerItem#getCollecionId().
	 * Part of prometheus.data.xml.MementiItemXML iterface.
	 * 
	 * @see prometheus.data.collection.BaseManagerItem#getCollecionId()
	 * @see prometheus.data.xml.MementiItemXML
	 * 
	 * @return
	 */
	public final int getId() {
		return this.iCollectionId;
	}
	
	/**
	 * Sets Id by calling prometheus.data.collection.BaseManagerItem#setCollecionId(GeneralManager, int)
	 * Part of prometheus.data.xml.MementiItemXML iterface.
	 * @param creator
	 * @param iSetDNetEventId
	 * 
	 * @see prometheus.data.collection.BaseManagerItem#setCollecionId(GeneralManager, int)
	 * @see prometheus.data.xml.MementiItemXML
	 */
	public final void setId( final int iSetDNetEventId ) {		
		this.iCollectionId = iSetDNetEventId;		
	}

}
