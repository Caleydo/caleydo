package cerberus.command.memento;

import cerberus.util.exception.PrometheusMementoException;


/**
 * Memento has a public interface and a pseudo-private interface.
 * 
 * This pseudo-private interface is the "small" interface for the creator-object of the Memento only.
 * The creator-object stores its state in a derived calss of MementoState,
 * which contains all information needed to restore the current state.
 * 
 * @author Michael Kalkusch
 * 
 * @see cerberus.manager.MementoManager
 *
 */
public interface Memento {

	/**
	 * Get the type of this memento.
	 * 
	 * @return type of this memento
	 */
	public MementoType getMementoType();


	/**
	 * Each class sets it's state to be able to restore it's current status.
	 * 
	 * Throws PrometheusMementoException if an inappropriate MementoStateInterface-object
	 * is passed a parameter. 
	 * Derived class has to check, if MementoStateInterface-object fits.
	 * 
	 * @param setMementoCreator reference to the object, that created the memento
	 * @param setMemetoState details for restoring the state of the creator object setMementoCreator
	 * 
	 * @throws PrometheusMementoException are thrown in case of full storage or maximum number of mementos 
	 */
	public void setMementoState(final Object setMementoCreator,
			final MementoState setMemetoState)
			throws PrometheusMementoException;

	/**
	 * Each class is be able to restore it's previouse status using the information stored 
	 * in the MementoState object.
	 * 
	 * @return MementoState object to restore a previouse status.
	 */
	public MementoState getMementoState(final Object setMementoCreator);

	/**
	 * Overwrite toString() to get valueable debug information.
	 * 
	 * Note: This methode is used by MementoManager.toString()
	 */
	public String toString();

	/**
	 * Overwrite toString() to get valueable debug information.
	 * 
	 * Note: This methode is used by MementoManager.toString()
	 */
	public String toStringRecursive();

}