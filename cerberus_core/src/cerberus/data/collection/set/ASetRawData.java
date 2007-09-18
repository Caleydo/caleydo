/**
 * 
 */
package cerberus.data.collection.set;

import cerberus.data.collection.ISet;
import cerberus.data.collection.SetDataType;
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

	private final SetType setType;
	
	protected SetDataType setDataType;
	
	/**
	 * @param iSetCollectionId
	 * @param setGeneralManager
	 * @param setCollectionLock
	 */
	protected ASetRawData(int iSetCollectionId,
			final IGeneralManager setGeneralManager,
			ICollectionLock setCollectionLock,
			final SetType setType) {

		super(iSetCollectionId, 
				setGeneralManager, 
				setCollectionLock);
		
		this.setType = setType;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISet#getSetType()
	 */
	public final SetType getSetType() {
		return setType;
	}
	
	public final SetDataType getSetDataType() {

		return setDataType;
	}

	
	protected final void setSetDataType(SetDataType SetDataType) {

		this.setDataType = SetDataType;
	}
}
