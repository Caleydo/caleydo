/**
 * 
 */
package cerberus.data.collection.set;

import cerberus.data.collection.ISet;
import cerberus.data.collection.SetType;
import cerberus.data.collection.thread.impl.ACollectionThreadItem;
import cerberus.data.collection.thread.lock.ICollectionLock;
import cerberus.manager.IGeneralManager;


/**
 * @author Michael Kalkusch
 *
 */
public abstract class ASetRawData 
extends ACollectionThreadItem 
implements ISet {

	/**
	 * @param iSetCollectionId
	 * @param setGeneralManager
	 * @param setCollectionLock
	 */
	public ASetRawData(int iSetCollectionId,
			IGeneralManager setGeneralManager,
			ICollectionLock setCollectionLock) {

		super(iSetCollectionId, setGeneralManager, setCollectionLock);
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISet#getSetType()
	 */
	public final SetType getSetType() {
		return SetType.SET_RAW_DATA;
	}

}
