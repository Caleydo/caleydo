package org.caleydo.core.command.memento;


/**
 * Interface for creator-object's of IMemento's. This interface must be
 * implemented by all objects, that create Mementos and restore a previous state
 * using the information stored in the IMemento. Each creator-object must
 * implement it's own state-storage class derived from
 * org.caleydo.command.memento.MementoState . Part of DesignPattern "IMemento"
 * 
 * @author Michael Kalkusch
 */
public interface IMementoCreator
{

	/**
	 * Tell's the object to create it's memento, storing all information needed,
	 * to reset the object to the current state in future.
	 * 
	 * @return the created org.caleydo.command.memento.Memento
	 */
	public IMemento createMemento();

	/**
	 * Restore a previous state using data stored in the IMemento. Throws
	 * PrometheusMementoException if an inappropriate
	 * MementoStateInterface-object is passed inside the memento, or if the
	 * IMemento-object does not fit. Derived class has to check, if
	 * IMemento-object fits.
	 * 
	 * @param setMemento org.caleydo.command.memento.Memento to reset the object
	 *            in a previous state
	 */
	public void setMemento(IMemento setMemento);
}