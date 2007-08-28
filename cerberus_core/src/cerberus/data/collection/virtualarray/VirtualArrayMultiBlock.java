/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.data.collection.virtualarray;

import cerberus.manager.IGeneralManager;
import cerberus.manager.event.mediator.IMediatorSender;
import cerberus.manager.type.ManagerObjectType;
import cerberus.parser.ISaxParserHandler;
//import cerberus.data.collection.ISet;
import cerberus.data.collection.IVirtualArray;
import cerberus.data.collection.VirtualArrayType;
import cerberus.data.collection.parser.CollectionSelectionSaxParserHandler;
import cerberus.data.collection.thread.lock.ICollectionLock;
import cerberus.data.collection.virtualarray.iterator.IVirtualArrayIterator;
import cerberus.data.collection.virtualarray.iterator.VirtualArrayMultiBlockIterator;
import cerberus.data.xml.IMementoItemXML;

import java.lang.StringBuffer;

/**
 * @author Michael Kalkusch
 *
 */
public class VirtualArrayMultiBlock extends AVirtualArray
implements IVirtualArray, IMementoItemXML, IMediatorSender {

	protected int iMultiOffset = 0;

	protected int iMultiRepeat = 0;

	/**
	 * @param iSetCollectionId
	 * @param setRefBaseManager
	 */
	public VirtualArrayMultiBlock(int iSetCollectionId,
			final IGeneralManager setRefBaseManager,
			final ICollectionLock setCollectionLock) {

		super(iSetCollectionId, setRefBaseManager, setCollectionLock);
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.IVirtualArray#getSelectionType()
	 */
	public VirtualArrayType getSelectionType() {

		return VirtualArrayType.VIRTUAL_ARRAY_MULTI_BLOCK;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.IVirtualArray#getMultiRepeat()
	 */
	public int getMultiRepeat() {

		return iMultiRepeat;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.IVirtualArray#getMultiOffset()
	 */
	public int getMultiOffset() {

		return iMultiOffset;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.IVirtualArray#getIndexArray()
	 */
	public int[] getIndexArray() {

		int[] indexArray = new int[iSelectionLength];

		int iLastOffset = iSelectionOffset;
		int iCurrentIndex = iSelectionOffset;
		int iCounter = 0;

		for (int i = 0; i < iSelectionLength; i++)
		{

			if (iCounter < iMultiRepeat)
			{
				indexArray[i] = iCurrentIndex;

				iCurrentIndex++;
				iCounter++;
			} else
			{
				//current begin of multi block is 
				// last index of multi-block + iMultiOffset
				iLastOffset += iMultiOffset;

				//reset index to begin of next multi block...
				iCurrentIndex = iLastOffset;

				// set index inside array...
				indexArray[i] = iCurrentIndex;

				// increment counter...
				iCounter = 0;
			}

		}

		return indexArray;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.IVirtualArray#setMultiRepeat(int)
	 */
	public boolean setMultiRepeat(int iSetSize) {

		if (iSetSize <= this.iMultiOffset)
		{
			this.iMultiRepeat = iSetSize;
			return true;
		}

		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.IVirtualArray#setMultiOffset(int)
	 */
	public boolean setMultiOffset(int iSetSize) {

		if (iSetSize >= this.iMultiRepeat)
		{
			this.iMultiOffset = iSetSize;
			return true;
		}

		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.IVirtualArray#setIndexArray(int[])
	 */
	public void setIndexArray(int[] iSetIndexArray) {

		// TODO Auto-generated method stub

	}

	public String toString() {

		StringBuffer result = new StringBuffer("Sel:");

		result.append(getId());
		result.append(" (");

		result.append(this.iSelectionOffset);
		result.append(")->");
		result.append(this.iSelectionLength);
		result.append(" [");
		result.append(this.iMultiOffset);
		result.append("]->");
		result.append(this.iMultiRepeat);

		return result.toString();
	}

	/**
	 * Restore state of object by update data from SaxHandler
	 * 
	 * @param refSaxHandler reference to SaxHandler
	 * @return TRUE if the provided handler provided same Id as object.
	 */
	public boolean setMementoXML_usingHandler(
			final ISaxParserHandler refSaxHandler) {

		try
		{
			CollectionSelectionSaxParserHandler parser = (CollectionSelectionSaxParserHandler) refSaxHandler;

			setLength(parser.getXML_DataLength());
			setOffset(parser.getXML_DataOffset());

			setMultiOffset(parser.getXML_DataMultiOffset());
			setMultiRepeat(parser.getXML_DataMultiRepeat());

			getManager().unregisterItem(getId(),
					ManagerObjectType.VIRTUAL_ARRAY_MULTI_BLOCK);

			getManager().registerItem(this, parser.getXML_DataComponent_Id(),
					ManagerObjectType.VIRTUAL_ARRAY_MULTI_BLOCK);

			setId(parser.getXML_DataComponent_Id());

			return true;
		} catch (NullPointerException npe)
		{
			return false;
		}

	}

	/**
	 * Create XML IMemento.
	 * 
	 * @see cerberus.data.xml.IMementoXML#createMementoXML()
	 */
	public String createMementoXML() {

		final String openDetail = "<DataComponentItemDetails type=\"";
		final String closeDetail = "</DataComponentItemDetails>\n";

		String result = createMementoXML_Intro(ManagerObjectType.VIRTUAL_ARRAY_MULTI_BLOCK
				.name());

		result += openDetail + "MultiOffset\" >" + getMultiOffset()
				+ closeDetail;

		result += openDetail + "MultiRepeat\" >" + getMultiRepeat()
				+ closeDetail;

		//		result += openDetail + "RandomLookup\" >";

		result += "</DataComponentItem>\n";

		return result;
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.collection.UniqueManagedInterface#getBaseType()
	 */
	public ManagerObjectType getBaseType() {

		return ManagerObjectType.VIRTUAL_ARRAY_MULTI_BLOCK;
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.collection.ICollection#getCacheId()
	 */
	public int getCacheId() {

		return this.iCacheId;
	}

	public IVirtualArrayIterator iterator() {

		return new VirtualArrayMultiBlockIterator(this);
	}

}
