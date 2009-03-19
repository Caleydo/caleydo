package org.caleydo.core.command.memento;

/**
 * One base implementation of the IMemento interface. Desing pattern "IMemento"
 * 
 * @see org.caleydo.core.manager.IMementoManager
 * @author Michael Kalkusch
 */
public class IGeneralMemento
	implements IMemento {

	/**
	 * Stores all information needed to reset an object to a previous state. Must only be used by the object,
	 * that created the IMemento! Object which creates the IMemento must derive a class from
	 * MementoStateInterface and cast to the derived class once the object receives the
	 * MementoStateInterface-object.
	 */
	protected IMementoState mementoState = null;

	// TODO: write TEST for IMemento objects!

	/**
	 * Reference to creator of IMemento.
	 */
	final protected Object mementoCreator;

	/**
	 * Define type of memento
	 */
	protected MementoType mementoType = MementoType.GENERAL;

	/**
	 * Constructor.
	 * 
	 * @param setMementoCreator
	 *            creator of memento
	 */
	public IGeneralMemento(final Object setMementoCreator) {

		assert setMementoCreator != null : "IMemento(Object)Can not creat a memento with null-pointer as parent";

		mementoCreator = setMementoCreator;
	}

	/**
	 * Constructor.
	 * 
	 * @param setMementoCreator
	 *            creator of memento
	 * @param setMementoData
	 *            data from the creator object
	 */
	public IGeneralMemento(final Object setMementoCreator, final IMementoState setMementoData) {

		assert setMementoCreator != null : "IMemento(Object,MementoType) Can not creat a memento with null-pointer as parent";

		mementoCreator = setMementoCreator;
		mementoState = setMementoData;
	}

	/**
	 * Constructor.
	 * 
	 * @param setMementoCreator
	 *            creator of memento
	 * @param setMementoData
	 *            data from the creator object
	 * @param setMementoType
	 *            type of memento; used for memento manger
	 */
	public IGeneralMemento(final Object setMementoCreator, final IMementoState setMementoData,
		final MementoType setMementoType) {

		assert setMementoCreator != null : "IMemento(Object,MementoType,IMementoState)Can not creat a memento with null-pointer as parent";

		mementoCreator = setMementoCreator;
		mementoType = setMementoType;
		mementoState = setMementoData;
	}

	@Override
	public final MementoType getMementoType() {

		return mementoType;
	}

	@Override
	public void setMementoState(final Object setMementoCreator, final IMementoState setMemetoState) {

		if (mementoCreator != setMementoCreator)
			throw new IllegalStateException(
				"IGeneralMemento.setMementoState() failed due to setting data not from the creator of the IMemento.");

		this.mementoState = setMemetoState;
	}

	@Override
	public IMementoState getMementoState(final Object setMementoCreator) {

		assert setMementoCreator != mementoCreator : "getMemento() can only ba called by the creator of the memento!";

		return mementoState;
	}

	/**
	 * Get the estimated size of a IMemento
	 * 
	 * @return size in 4*Byte
	 */
	// public int getSizeMemento();
	@Override
	public String toString() {

		return mementoState.toString();
	}

	@Override
	public String toStringRecursive() {

		return mementoState.toStringRecursive();
	}

}
