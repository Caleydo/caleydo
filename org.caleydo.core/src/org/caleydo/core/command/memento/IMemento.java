package org.caleydo.core.command.memento;

import org.caleydo.core.util.exception.CaleydoRuntimeException;


/**
 * IMemento has a public interface and a pseudo-private interface.
 * 
 * This pseudo-private interface is the "small" interface for the creator-object of the IMemento only.
 * The creator-object stores its state in a derived calss of IMementoState,
 * which contains all information needed to restore the current state.
 * 
 * @author Michael Kalkusch
 * 
 * @see org.caleydo.core.manager.IMementoManager
 *
 */
public interface IMemento {

	/**
	 * Get the type of this memento.
	 * 
	 * @return type of this memento
	 */
	public MementoType getMementoType();


	/**
	 * Each class sets it's state to be able to restore it's current status.
	 * 
	 * Throws CaleydoRuntimeException if an inappropriate MementoStateInterface-object
	 * is passed a parameter. 
	 * Derived class has to check, if MementoStateInterface-object fits.
	 * 
	 * @param setMementoCreator reference to the object, that created the memento
	 * @param setMemetoState details for restoring the state of the creator object setMementoCreator
	 * 
	 * @throws CaleydoRuntimeException are thrown in case of full storage or maximum number of mementos 
	 */
	public void setMementoState(final Object setMementoCreator,
			final IMementoState setMemetoState)
			throws CaleydoRuntimeException;

	/**
	 * Each class is be able to restore it's previouse status using the information stored 
	 * in the IMementoState object.
	 * 
	 * @return IMementoState object to restore a previouse status.
	 */
	public IMementoState getMementoState(final Object setMementoCreator);

	/**
	 * Overwrite toString() to get valueable debug information.
	 * 
	 * Note: This method is used by IMementoManager.toString()
	 */
	public String toString();

	/**
	 * Overwrite toString() to get valueable debug information.
	 * 
	 * Note: This method is used by IMementoManager.toString()
	 */
	public String toStringRecursive();

}