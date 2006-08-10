/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.data;

import cerberus.data.IUniqueObject;

/**
 * Abstract class providing methodes defined in IUniqueManagedObject.
 * Stores reference to creator of item in private variable.
 * 
 * @author Michael Kalkusch
 *
 * @see prometheus.data.xml.MementiItemXML
 */
public abstract class AbstractUniqueItem implements IUniqueObject
{

	/**
	 * Unique Id
	 */
	private int iUniqueId;

	/**
	 * 
	 */
	protected AbstractUniqueItem(int iSetCollectionId)
	{

		iUniqueId = iSetCollectionId;
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
	public final int getId()
	{
		return this.iUniqueId;
	}

	/**
	 * Sets Id by calling prometheus.data.collection.BaseManagerItem#setCollecionId(GeneralManager, int)
	 * Part of prometheus.data.xml.MementiItemXML iterface.
	 * @param iSetDNetEventId
	 * 
	 * @see prometheus.data.collection.BaseManagerItem#setCollecionId(GeneralManager, int)
	 * @see prometheus.data.xml.MementiItemXML
	 */
	public final void setId(final int iSetDNetEventId)
	{
		this.iUniqueId = iSetDNetEventId;
	}

}
