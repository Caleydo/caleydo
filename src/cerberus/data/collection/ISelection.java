/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.data.collection;


//import cerberus.data.IUniqueManagedObject;
import cerberus.data.collection.IMetaDataHandler;
//import cerberus.data.collection.CollectionInterface;
import cerberus.data.collection.SelectionType;
import cerberus.data.collection.selection.iterator.ISelectionIterator;
import cerberus.data.collection.thread.ICollectionThreadObject;
import cerberus.data.xml.IMementoItemXML;

//import prometheus.util.exception.PrometheusVirtualArrayException;


/**
 * @author Michael Kalkusch
 *
 */
public interface ISelection  
	extends IMetaDataHandler, 
	IMementoItemXML,
	ICollectionThreadObject
{

	/**
	 * Get the type of the selection
	 * 
	 * @return type of the selection
	 * 
	 * @see prometheus.data.collection.SelectionType
	 */
	public SelectionType getSelectionType(); 
	
	
	/**
	 * Constructor sets SelectionId.
	 * 
	 * Note: The selecitonId con only be set by calling this constructor.
	 * 
	 * @param iSetSelectionId 
	 */
	//public ISelection(int iSetSelectionId);


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
	public ISelectionIterator iterator();
	
}
