/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package org.geneview.core.data.collection;


//import org.geneview.core.data.IUniqueManagedObject;
import org.geneview.core.data.collection.IMetaDataHandler;
//import org.geneview.core.data.collection.CollectionInterface;
import org.geneview.core.data.collection.VirtualArrayType;
import org.geneview.core.data.collection.thread.ICollectionThreadObject;
import org.geneview.core.data.collection.virtualarray.iterator.IVirtualArrayIterator;
import org.geneview.core.data.xml.IMementoItemXML;
import org.geneview.core.manager.event.mediator.IMediatorReceiver;
import org.geneview.core.manager.event.mediator.IMediatorSender;


/**
 * @author Michael Kalkusch
 *
 */
public interface IVirtualArray  
	extends IMetaDataHandler, 
	IMementoItemXML,
	ICollectionThreadObject,
	IMediatorSender,
	IMediatorReceiver
{

	/**
	 * Get the type of the selection
	 * 
	 * @return type of the selection
	 * 
	 * @see prometheus.data.collection.VirtualArrayType
	 */
	public VirtualArrayType getSelectionType(); 
	
	
	/**
	 * Constructor sets SelectionId.
	 * 
	 * Note: The selecitonId con only be set by calling this constructor.
	 * 
	 * @param iSetSelectionId 
	 */
	//public IVirtualArray(int iSetSelectionId);


	public int getOffset();

	/**
	 * Get number of items in selection.
	 * 
	 * @return number of items in selection
	 */
	public int length();
	
	public int getMultiRepeat();
	
	public int getMultiOffset();

	/**
	 * Creates an array from the selection.
	 * 
	 * @return array representing the selection
	 */
	public int[] getIndexArray();
	
	public void setOffset(final int iSetOffset);

	public void setLength(final int iSetLength);
	
	
	/**
	 * 
	 * @param iSetSize range [1.. (MultiOffset-1) ]
	 * @return TRUE if set was done successfully
	 */
	public boolean setMultiRepeat(final int iSetSize);
	
	/**
	 * 
	 * @param iSetSize range [ (MultiRepeat) .. ]
	 * @return TRUE if set was done successfully
	 */
	public boolean setMultiOffset(final int iSetSize);

	public void setIndexArray( int[] iSetIndexArray );
	
	/**
	 * Get an Iterator for this election.
	 * 
	 * @return Iterator
	 */
	public IVirtualArrayIterator iterator();
	
}
