/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.data.collection.selection;

import cerberus.manager.GeneralManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.data.collection.Selection;
import cerberus.data.collection.SelectionType;
import cerberus.data.collection.selection.iterator.SelectionIterator;
import cerberus.data.collection.selection.iterator.SelectionMultiBlockIterator;
import cerberus.data.collection.parser.CollectionSelectionParseSaxHandler;
import cerberus.data.collection.selection.iterator.SelectionSingleBlockIterator;
import cerberus.data.collection.thread.lock.CollectionLock;
import cerberus.data.xml.MementoItemXML;
import cerberus.xml.parser.DParseSaxHandler;

import java.lang.StringBuffer;

/**
 * @author Michael Kalkusch
 *
 */
public class SelectionMultiBlock 
extends AbstractSelection 
implements
		Selection, MementoItemXML {

	protected int iMultiOffset = 0;
	
	protected int iMultiRepeat = 0;
	
	/**
	 * @param iSetCollectionId
	 * @param setRefBaseManager
	 */
	public SelectionMultiBlock(int iSetCollectionId,
			final GeneralManager setRefBaseManager,
			final CollectionLock setCollectionLock) {
		super(iSetCollectionId, setRefBaseManager, setCollectionLock);
		
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.Selection#getSelectionType()
	 */
	public SelectionType getSelectionType() {
		return SelectionType.SELECTION_MULTI_BLOCK;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.Selection#getMultiRepeat()
	 */
	public int getMultiRepeat() {
		return iMultiRepeat;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.Selection#getMultiOffset()
	 */
	public int getMultiOffset() {
		return iMultiOffset;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.Selection#getIndexArray()
	 */
	public int[] getIndexArray() {
		int[] indexArray = new int[ iSelectionLength ];
		
		int iLastOffset = iSelectionOffset;
		int iCurrentIndex = iSelectionOffset;
		int iCounter = 0;
		
		for ( int i=0; i < iSelectionLength; i++ ) {
				
			if ( iCounter < iMultiRepeat ) {
				indexArray[i] = iCurrentIndex;
				
				iCurrentIndex++;
				iCounter++;
			}
			else {
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
	 * @see cerberus.data.collection.Selection#setOffset(int)
	 */
	public void setOffset(int iSetOffset) {
		this.iSelectionOffset = iSetOffset;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.Selection#setLength(int)
	 */
	public void setLength(int iSetLength) {
		this.iSelectionLength = iSetLength;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.Selection#setMultiRepeat(int)
	 */
	public boolean setMultiRepeat(int iSetSize) {
		if ( iSetSize <= this.iMultiOffset ) {
			this.iMultiRepeat = iSetSize;
			return true;
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.Selection#setMultiOffset(int)
	 */
	public boolean setMultiOffset(int iSetSize) {
		if ( iSetSize >= this.iMultiRepeat ) {
			this.iMultiOffset = iSetSize;
			return true;
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.Selection#setIndexArray(int[])
	 */
	public void setIndexArray(int[] iSetIndexArray) {
		// TODO Auto-generated method stub

	}

	public String toString() {
		StringBuffer result = new StringBuffer("Sel:");
		
		result.append( getId() );
		result.append( " (");
		
		result.append( this.iSelectionOffset );
		result.append( ")->");
		result.append( this.iSelectionLength );
		result.append( " [");
		result.append( this.iMultiOffset );
		result.append( "]->");
		result.append( this.iMultiRepeat );
		
		return result.toString();
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
			
			setMultiOffset( parser.getXML_DataMultiOffset() );
			setMultiRepeat( parser.getXML_DataMultiRepeat() );
			
			getManager().unregisterItem( getId(), 
					ManagerObjectType.SELECTION_MULTI_BLOCK );
			
			getManager().registerItem( this, 
					parser.getXML_DataComponent_Id(), 
					ManagerObjectType.SELECTION_MULTI_BLOCK );
			
			setId( parser.getXML_DataComponent_Id() );			
			
			return true;
		}
		catch (NullPointerException npe) {
			return false;
		}
		
	}
	
	/**
	 * Create XML Memento.
	 * 
	 * @see cerberus.data.xml.MementoXML#createMementoXML()
	 */
	public String createMementoXML() {
		final String openDetail = "<DataComponentItemDetails type=\"";
		final String closeDetail = "</DataComponentItemDetails>\n";
		
		String result = createMementoXML_Intro(
				ManagerObjectType.SELECTION_MULTI_BLOCK.name());
	
		result += openDetail + "MultiOffset\" >" +
			getMultiOffset() +	closeDetail;
		
		result += openDetail + "MultiRepeat\" >" +
			getMultiRepeat() +	closeDetail;
		
//		result += openDetail + "RandomLookup\" >";
					
		result += "</DataComponentItem>\n";
		
		return result;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.collection.UniqueManagedInterface#getBaseType()
	 */
	public ManagerObjectType getBaseType() {
		return ManagerObjectType.SELECTION_MULTI_BLOCK;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.collection.CollectionInterface#getCacheId()
	 */
	public int getCacheId() {
		return this.iCacheId;
	}

	public SelectionIterator iterator() {
		return new SelectionMultiBlockIterator(this);
	}
	

}
