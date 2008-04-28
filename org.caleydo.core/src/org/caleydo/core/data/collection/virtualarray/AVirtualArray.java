package org.caleydo.core.data.collection.virtualarray;

import org.caleydo.core.data.collection.IMetaData;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IVirtualArray;
import org.caleydo.core.data.collection.thread.impl.ACollectionThreadItem;
import org.caleydo.core.data.collection.thread.lock.ICollectionLock;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.event.EventPublisher;

/**
 * Abstract calls for all virtual arrays.
 * Implements several methods, that are suitable for all virtual arrays.
 * 
 * @author Michael Kalkusch
 */
public abstract class AVirtualArray 
extends ACollectionThreadItem 
implements IVirtualArray {

	/**
	 * Defines the offset of the virtual array.
	 * 
	 * Range [ 0.. (iLength-1) ]
	 */
	protected int iSelectionOffset = 0;

	/**
	 * Defines the length of the virtual array.
	 * 
	 * Range [0.. ]
	 */
	protected int iSelectionLength = 0;

	/**
	 * Link to Collection-Meta data.
	 */
	protected IMetaData refCollectionMetaData = null;

	/**
	 * Default Conctructor, sets the unique collectionId.
	 * 
	 * @param iSetCollectionId unique collectionId
	 */
	protected AVirtualArray(final int iSetCollectionId,
			final IGeneralManager setRefBaseManager,
			final ICollectionLock setCollectionLock)
	{

		super(iSetCollectionId, setRefBaseManager, setCollectionLock);

	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IVirtualArray#length()
	 */
	public final int length()
	{
		return iSelectionLength;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IVirtualArray#getOffset()
	 */
	public final int getOffset()
	{
		return iSelectionOffset;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IVirtualArray#setOffset(int)
	 */
	public void setOffset(int iSetOffset)
	{
		this.iSelectionOffset = iSetOffset;
		
		((EventPublisher)generalManager.
				getEventPublisher()).updateReceiver(this);
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IVirtualArray#setLength(int)
	 */
	public void setLength(int iSetLength)
	{
		this.iSelectionLength = iSetLength;
		
		((EventPublisher)generalManager.
				getEventPublisher()).updateReceiver(this);
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IMetaDataCollection#getMetaData()
	 */
	public final IMetaData getMetaData()
	{
		return refCollectionMetaData;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IMetaDataCollection#setMetaData(org.caleydo.core.data.collection.IMetaData)
	 */
	public final void setMetaData(IMetaData setMetaData)
	{
		refCollectionMetaData = setMetaData;
	}

	/**
	 * Create "Header" for all Selections.
	 * 
	 * @see org.caleydo.core.data.xml.IMementoXML#createMementoXML()
	 * @return String containign the XML-header for this selection
	 */
	final protected String createMementoXML_Intro(final String sSelectionType)
	{
		final String openDetail = "<DataComponentItemDetails type=\"";
		final String closeDetail = "</DataComponentItemDetails>\n";

		return "<DataComponentItem data_Id=\"" + getId() + "\" type=\""
				+ sSelectionType + "\">\n" + openDetail + "Offset_Length\" >"
				+ iSelectionOffset + " " + iSelectionLength + closeDetail;
	}

	public String toString()
	{
		String result = "[" + getId() + "#" + iSelectionOffset + ":"
				+ iSelectionLength + " ()]";

		return result;
	}
	

	/**
	 * Default implementation of IMediatorReceiver, must be overloaded by derived class if used.
	 * 
	 * @see org.caleydo.core.manager.event.mediator.IMediatorReceiver#updateReceiver(java.lang.Object)
	 */
	public void updateReceiver(Object eventTrigger) {
		assert false : "update() has no effect";
	}

	/**
	 * Default implementation of IMediatorReceiver, must be overloaded by derived class if used.
	 * 
	 * @see org.caleydo.core.manager.event.mediator.IMediatorReceiver#updateReceiver(java.lang.Object, org.caleydo.core.data.collection.ISet)
	 */
	public void updateReceiver(Object eventTrigger, ISet updatedSet) {
		assert false : "updateReceiver() has no effect";
	}

}
