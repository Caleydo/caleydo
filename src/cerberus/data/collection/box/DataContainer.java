/**
 * 
 */
package cerberus.data.collection.box;

import java.util.Vector;
import java.util.Iterator;

import cerberus.data.collection.ISelection;
import cerberus.data.collection.ISet;
import cerberus.data.collection.IStorage;

import cerberus.data.collection.thread.CollectionThreadElement;
import cerberus.data.collection.thread.impl.AbstractThreadElement;
import cerberus.data.collection.thread.lock.CollectionLock;


/**
 * Container or box holding several ISet's.
 * Has it's own unique id and a label.
 * 
 * @author kalkusch
 *
 */
public class DataContainer extends AbstractThreadElement implements
		CollectionThreadElement {

	/**
	 * Label for this DataContainer.
	 * Label must not be unique.
	 * Default label is it's unique ID
	 */
	protected String sMetaBoxLabel;
	
	/**
	 * Define a group of cerberus.data.collection.ISet objects.
	 * Each ISet may have a label stored in cerberus.data.collection.box.DataContainer#vecLabelOfSet
	 * 
	 * @see cerberus.data.collection.box.DataContainer#vecLabelOfSet
	 */
	protected Vector <ISet> vecSet;
	
	/**
	 * Define a label for each set
	 * 
	 * @see cerberus.data.collection.box.DataContainer#vecSet
	 */
	protected Vector <String> vecLabelOfSet;
		
	
	/**
	 * @param iSetCollectionId
	 */
	public DataContainer(int iSetCollectionId) {
		super(iSetCollectionId);
		
		sMetaBoxLabel = Integer.toString( iSetCollectionId );
	}

	/**
	 * @param iSetCollectionId
	 * @param setCollectionLock
	 */
	public DataContainer(int iSetCollectionId, CollectionLock setCollectionLock) {
		super(iSetCollectionId, setCollectionLock);
		
		sMetaBoxLabel = Integer.toString( iSetCollectionId );
	}

	/**
	 * Get Label.
	 * Label is not an unique name.
	 * 
	 * @return label of DataContainer
	 */
	public final String getMetaBoxLabel() {
		return sMetaBoxLabel;
	}
	
	/**
	 * ISet Label of DataContainer. 
	 * Must not be an unique name.
	 * 
	 * @param setMetaBoxLabel set new label name
	 */
	public final void getMetaBoxLabel( String setMetaBoxLabel ) {
		this.sMetaBoxLabel = setMetaBoxLabel;
	}
	
	public void addSet( ISet addSet, String label ) {
		
	}
	
	public void removeSet( ISet addSet ) {
		
	}
	
	/**
	 * @see cerberus.data.collection.box.DataContainer#addSet(ISet, String)
	 * 
	 * @param iUniqueSetId
	 * @param label
	 */
	public void addItem( int iUniqueSetId, String label ) {
		
	}
	
	/**
	 * 
	 * @see cerberus.data.collection.box.DataContainer#addItem(int, String)
	 * @see cerberus.data.collection.box.DataContainer#addSet(ISet, String)
	 * 
	 * @param iUniqueSetId
	 */
	public void removeItem( int iUniqueSetId ) {
		
	}
	
	/**
	 * Swap position of two ISet'S in a Box.
	 * 
	 * @param iFromUniqueSetId Addess first ISet
	 * @param iToUniqueSetId address second ISet
	 */
	public void swapItem( int iFromUniqueSetId, int iToUniqueSetId ) {
		
	}
	
	/**
	 * Remove all ISet objects from Box.
	 * 
	 * @see cerberus.data.collection.box.DataContainer#addItem(int, String)
	 * @see cerberus.data.collection.box.DataContainer#addSet(ISet, String)
	 * 
	 *
	 */
	public void clearBox( ) {
		
	}
	
}
