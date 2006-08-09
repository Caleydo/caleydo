/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.data.collection.selection;

import java.lang.NullPointerException;

import cerberus.manager.GeneralManager;
import cerberus.manager.type.ManagerObjectType;

import cerberus.data.collection.ISelection;
import cerberus.data.collection.SelectionType;
import cerberus.xml.parser.DParseSaxHandler;
import cerberus.data.collection.parser.CollectionSelectionParseSaxHandler;
import cerberus.data.collection.selection.AbstractSelection;
import cerberus.data.collection.selection.iterator.SelectionIterator;
import cerberus.data.collection.selection.iterator.SelectionSingleBlockIterator;
import cerberus.data.collection.thread.lock.CollectionLock;
import cerberus.data.xml.MementoItemXML;

/**
 * @author Michael Kalkusch
 *
 */
public class SelectionThreadSingleBlock 
	extends AbstractSelection 
	implements ISelection, MementoItemXML, CollectionLock
{

	
	/**
	 * 
	 */
	public SelectionThreadSingleBlock( final int iCollectionId, 
			final GeneralManager refBaseManager,
			final CollectionLock setCollectionLock) {
		super(iCollectionId, refBaseManager,setCollectionLock);
		
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISelection#getSelectionType()
	 */
	public SelectionType getSelectionType() {		
		return SelectionType.SELECTION_SINGLE_BLOCK;
	}


	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISelection#getMultiRepeat()
	 */
	public int getMultiRepeat() {			
		//do nothing. this is not supported
		return 0;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISelection#getMultiOffset()
	 */
	public int getMultiOffset() {
		//do nothing. this is not supported
		return 0;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISelection#getIndexArray()
	 */
	public int[] getIndexArray() {
		int[] indexArray = new int[ this.iSelectionLength ];
		
		for ( int i=0; i < iSelectionLength; i++ ) {
			indexArray[i] = this.iSelectionOffset + i;
		}
		
		return indexArray;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISelection#setOffset(int)
	 */
	public void setOffset(int iSetOffset) {
		this.iSelectionOffset = iSetOffset;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISelection#setLength(int)
	 */
	public void setLength(int iSetLength) {
		this.iSelectionLength = iSetLength;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISelection#setMultiRepeat(int)
	 */
	public boolean setMultiRepeat(int iSetSize) {
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISelection#setMultiOffset(int)
	 */
	public boolean setMultiOffset(int iSetSize) {
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISelection#setIndexArray(int[])
	 */
	public void setIndexArray(int[] iSetIndexArray) {
		throw new RuntimeException("SelectionSingleBlock.setIndexArray() is not supported.");
	}


	/**
	 * Restore state of object by update data from SaxHandler
	 * 
	 * @param refSaxHandler reference to SaxHandler
	 * @return TRUE if the provided handler provided same Id as object.
	 */
	public boolean setMementoXML_usingHandler( final DParseSaxHandler refSaxHandler ) {
		
		try {
			CollectionSelectionParseSaxHandler parser = 
				(CollectionSelectionParseSaxHandler) refSaxHandler;
			
			setLength( parser.getXML_DataLength() );
			setOffset( parser.getXML_DataOffset() );
			
			getManager().unregisterItem( getId(), 
					ManagerObjectType.SELECTION_SINGLE_BLOCK );
			
			getManager().registerItem( this, 
					parser.getXML_DataComponent_Id(), 
					ManagerObjectType.SELECTION_SINGLE_BLOCK );
			
			setId( parser.getXML_DataComponent_Id() );			
			
			return true;
		}
		catch (NullPointerException npe) {
			return false;
		}
		
	}

	/**
	 * @see cerberus.data.xml.MementoXML#createMementoXML()
	 * @return String containing all information on the state 
	 * of the object in XML form with out a header.
	 */
	public String createMementoXML() {
		
		return createMementoXML_Intro(
				ManagerObjectType.SELECTION_SINGLE_BLOCK.name())
			+ "</DataComponentItem>\n";
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.collection.UniqueManagedInterface#getBaseType()
	 */
	public ManagerObjectType getBaseType() {
		return ManagerObjectType.SELECTION_SINGLE_BLOCK;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.collection.ICollection#getCacheId()
	 */
	public int getCacheId() {
		return this.iCacheId;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.collection.ISelection#iterator()
	 */
	public SelectionIterator iterator() {
		return new SelectionSingleBlockIterator(this);
	}

}
