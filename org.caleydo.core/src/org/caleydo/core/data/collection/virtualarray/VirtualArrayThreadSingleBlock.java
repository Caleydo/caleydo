/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package org.caleydo.core.data.collection.virtualarray;

import java.lang.NullPointerException;

import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.parser.xml.sax.ISaxParserHandler;

import org.caleydo.core.data.collection.IVirtualArray;
import org.caleydo.core.data.collection.VirtualArrayType;
import org.caleydo.core.data.collection.parser.CollectionSelectionSaxParserHandler;
import org.caleydo.core.data.collection.thread.lock.ICollectionLock;
import org.caleydo.core.data.collection.virtualarray.AVirtualArray;
import org.caleydo.core.data.collection.virtualarray.iterator.IVirtualArrayIterator;
import org.caleydo.core.data.collection.virtualarray.iterator.VirtualArraySingleBlockIterator;
import org.caleydo.core.data.xml.IMementoItemXML;

/**
 * @author Michael Kalkusch
 *
 */
public class VirtualArrayThreadSingleBlock 
extends AVirtualArray 
implements IVirtualArray, IMementoItemXML, ICollectionLock {
	
	/**
	 * 
	 */
	public VirtualArrayThreadSingleBlock( final int iCollectionId, 
			final IGeneralManager refBaseManager,
			final ICollectionLock setCollectionLock) {
		super(iCollectionId, refBaseManager,setCollectionLock);
		
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IVirtualArray#getSelectionType()
	 */
	public VirtualArrayType getSelectionType() {		
		return VirtualArrayType.VIRTUAL_ARRAY_SINGLE_BLOCK;
	}


	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IVirtualArray#getMultiRepeat()
	 */
	public int getMultiRepeat() {			
		//do nothing. this is not supported
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IVirtualArray#getMultiOffset()
	 */
	public int getMultiOffset() {
		//do nothing. this is not supported
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IVirtualArray#getIndexArray()
	 */
	public int[] getIndexArray() {
		int[] indexArray = new int[ this.iSelectionLength ];
		
		for ( int i=0; i < iSelectionLength; i++ ) {
			indexArray[i] = this.iSelectionOffset + i;
		}
		
		return indexArray;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IVirtualArray#setMultiRepeat(int)
	 */
	public boolean setMultiRepeat(int iSetSize) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IVirtualArray#setMultiOffset(int)
	 */
	public boolean setMultiOffset(int iSetSize) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IVirtualArray#setIndexArray(int[])
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
	 * @see org.caleydo.core.data.xml.IMementoXML#createMementoXML()
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
	 * @see org.caleydo.core.data.collection.UniqueManagedInterface#getBaseType()
	 */
	public ManagerObjectType getBaseType() {
		return ManagerObjectType.VIRTUAL_ARRAY_SINGLE_BLOCK;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ICollection#getCacheId()
	 */
	public int getCacheId() {
		return this.iCacheId;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IVirtualArray#iterator()
	 */
	public IVirtualArrayIterator iterator() {
		return new VirtualArraySingleBlockIterator(this);
	}

}
