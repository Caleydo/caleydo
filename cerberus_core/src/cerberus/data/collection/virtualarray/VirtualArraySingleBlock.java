/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.data.collection.virtualarray;

import java.lang.NullPointerException;

import cerberus.manager.IGeneralManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.parser.xml.sax.ISaxParserHandler;

import cerberus.data.collection.IVirtualArray;
import cerberus.data.collection.VirtualArrayType;
import cerberus.data.collection.parser.CollectionSelectionSaxParserHandler;
import cerberus.data.collection.thread.lock.ICollectionLock;
import cerberus.data.collection.virtualarray.iterator.IVirtualArrayIterator;
import cerberus.data.collection.virtualarray.iterator.VirtualArraySingleBlockIterator;
import cerberus.data.xml.IMementoItemXML;

/**
 * @author Michael Kalkusch
 *
 */
public class VirtualArraySingleBlock 
extends AVirtualArray 
implements IVirtualArray, IMementoItemXML
{

	
	/**
	 * 
	 */
	public VirtualArraySingleBlock( final int iCollectionId, 
			final IGeneralManager refBaseManager,
			final ICollectionLock setCollectionLock) {
		super(iCollectionId, refBaseManager, setCollectionLock);
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.IVirtualArray#getSelectionType()
	 */
	public VirtualArrayType getSelectionType() {		
		return VirtualArrayType.VIRTUAL_ARRAY_SINGLE_BLOCK;
	}


	/* (non-Javadoc)
	 * @see cerberus.data.collection.IVirtualArray#getMultiRepeat()
	 */
	public int getMultiRepeat() {			
		//do nothing. this is not supported
		return 0;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.IVirtualArray#getMultiOffset()
	 */
	public int getMultiOffset() {
		//do nothing. this is not supported
		return 0;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.IVirtualArray#getIndexArray()
	 */
	public int[] getIndexArray() {
		int[] indexArray = new int[ this.iSelectionLength ];
		
		for ( int i=0; i < iSelectionLength; i++ ) {
			indexArray[i] = this.iSelectionOffset + i;
		}
		
		return indexArray;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.IVirtualArray#setMultiRepeat(int)
	 */
	public boolean setMultiRepeat(int iSetSize) {
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.IVirtualArray#setMultiOffset(int)
	 */
	public boolean setMultiOffset(int iSetSize) {
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.IVirtualArray#setIndexArray(int[])
	 */
	public void setIndexArray(int[] iSetIndexArray) {
		throw new RuntimeException("VirtualArraySingleBlock.setIndexArray() is not supported.");
	}


	/**
	 * Restore state of object by update data from SaxHandler
	 * 
	 * @param refSaxHandler reference to SaxHandler
	 * @return TRUE if the provided handler provided same Id as object.
	 */
	public boolean setMementoXML_usingHandler( final ISaxParserHandler refSaxHandler ) {
		
		
		try {
			CollectionSelectionSaxParserHandler parser = 
				(CollectionSelectionSaxParserHandler) refSaxHandler;
			
			setLength( parser.getXML_DataLength() );
			setOffset( parser.getXML_DataOffset() );
			
			getManager().unregisterItem( getId(), 
					ManagerObjectType.VIRTUAL_ARRAY_SINGLE_BLOCK );
			
			getManager().registerItem( this, 
					parser.getXML_DataComponent_Id(), 
					ManagerObjectType.VIRTUAL_ARRAY_SINGLE_BLOCK );
			
			setId( parser.getXML_DataComponent_Id() );			
			
			return true;
		}
		catch (NullPointerException npe) {
			return false;
		}
		
	}

	/**
	 * @see cerberus.data.xml.IMementoXML#createMementoXML()
	 * @return String containing all information on the state 
	 * of the object in XML form with out a header.
	 */
	public String createMementoXML() {
		
		return createMementoXML_Intro(
				ManagerObjectType.VIRTUAL_ARRAY_SINGLE_BLOCK.name())
			+ "</DataComponentItem>\n";
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.collection.UniqueManagedInterface#getBaseType()
	 */
	public ManagerObjectType getBaseType() {
		return ManagerObjectType.VIRTUAL_ARRAY_SINGLE_BLOCK;
	}
	
	/*
	 * (non-Javadoc)
	 * @see cerberus.data.collection.ICollection#getCacheId()
	 */
	public int getCacheId() {
		return this.iCacheId;
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.collection.IVirtualArray#iterator()
	 */
	public IVirtualArrayIterator iterator() {
		return new VirtualArraySingleBlockIterator(this);
	}
}
