/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.data.collection.selection;

import cerberus.manager.IGeneralManager;
import cerberus.manager.event.EventPublisher;
import cerberus.manager.type.ManagerObjectType;
import cerberus.data.collection.IMetaData;
import cerberus.data.collection.ISelection;
import cerberus.data.collection.thread.impl.ACollectionThreadItem;
import cerberus.data.collection.thread.lock.ICollectionLock;

/**
 * Abstract calss for all virtual arrays.
 * Implements several methodes, that are suitable for all virtual arrays.
 * 
 * @author Michael Kalkusch
 */
public abstract class ASelection extends ACollectionThreadItem implements
		ISelection
{

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
	protected ASelection(final int iSetCollectionId,
			final IGeneralManager setRefBaseManager,
			final ICollectionLock setCollectionLock)
	{

		super(iSetCollectionId, setRefBaseManager, setCollectionLock);

	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISelection#length()
	 */
	public final int length()
	{
		return iSelectionLength;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISelection#getOffset()
	 */
	public final int getOffset()
	{
		return iSelectionOffset;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISelection#setOffset(int)
	 */
	public void setOffset(int iSetOffset)
	{
		this.iSelectionOffset = iSetOffset;
		
//		((EventPublisher)refGeneralManager.
//				getManagerByBaseType(ManagerObjectType.EVENT_PUBLISHER)).update(this);
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISelection#setLength(int)
	 */
	public void setLength(int iSetLength)
	{
		this.iSelectionLength = iSetLength;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.IMetaDataCollection#getMetaData()
	 */
	public final IMetaData getMetaData()
	{
		return refCollectionMetaData;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.IMetaDataCollection#setMetaData(cerberus.data.collection.IMetaData)
	 */
	public final void setMetaData(IMetaData setMetaData)
	{
		refCollectionMetaData = setMetaData;
	}

	/**
	 * Create "Header" for all Selections.
	 * 
	 * @see cerberus.data.xml.IMementoXML#createMementoXML()
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

}
