package org.caleydo.core.data.collection.virtualarray;

import org.caleydo.core.data.collection.IVirtualArray;
import org.caleydo.core.data.collection.VirtualArrayType;
import org.caleydo.core.data.collection.parser.CollectionSelectionSaxParserHandler;
import org.caleydo.core.data.collection.thread.lock.ICollectionLock;
import org.caleydo.core.data.collection.virtualarray.iterator.IVirtualArrayIterator;
import org.caleydo.core.data.collection.virtualarray.iterator.VirtualArrayMultiBlockIterator;
import org.caleydo.core.data.xml.IMementoItemXML;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.event.mediator.IMediatorSender;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.parser.xml.sax.ISaxParserHandler;

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
			final IGeneralManager generalManager,
			final ICollectionLock setCollectionLock) {

		super(iSetCollectionId, generalManager, setCollectionLock);
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IVirtualArray#getSelectionType()
	 */
	public VirtualArrayType getSelectionType() {

		return VirtualArrayType.VIRTUAL_ARRAY_MULTI_BLOCK;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IVirtualArray#getMultiRepeat()
	 */
	public int getMultiRepeat() {

		return iMultiRepeat;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IVirtualArray#getMultiOffset()
	 */
	public int getMultiOffset() {

		return iMultiOffset;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IVirtualArray#getIndexArray()
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
	 * @see org.caleydo.core.data.collection.IVirtualArray#setMultiRepeat(int)
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
	 * @see org.caleydo.core.data.collection.IVirtualArray#setMultiOffset(int)
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
	 * @see org.caleydo.core.data.collection.IVirtualArray#setIndexArray(int[])
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

//			generalManager.getVirtualArrayManager().unregisterItem(getId(),
//					ManagerObjectType.VIRTUAL_ARRAY_MULTI_BLOCK);
//
//			getManager().registerItem(this, parser.getXML_DataComponent_Id(),
//					ManagerObjectType.VIRTUAL_ARRAY_MULTI_BLOCK);

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
	 * @see org.caleydo.core.data.xml.IMementoXML#createMementoXML()
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
	 * @see org.caleydo.core.data.collection.UniqueManagedInterface#getBaseType()
	 */
	public ManagerObjectType getBaseType() {

		return ManagerObjectType.VIRTUAL_ARRAY_MULTI_BLOCK;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ICollection#getCacheId()
	 */
	public int getCacheId() {

		return this.iCacheId;
	}

	public IVirtualArrayIterator iterator() {

		return new VirtualArrayMultiBlockIterator(this);
	}

}
