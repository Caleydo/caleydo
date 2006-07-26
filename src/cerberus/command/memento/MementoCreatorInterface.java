/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.command.memento;

import cerberus.util.exception.CerberusRuntimeException;

/**
 * Interface for creator-object's of Memento's.
 * 
 * This interface must be implemented by all objects, that create Mementos 
 * and restore a prevoius state using the inforamtion stored in the Memento.
 * Each creator-object must implement it's own state-storage class derived from prometheus.command.memento.MementoState .
 *   
 * Part of DesignPattern "Memento"
 *   
 * @author Michael Kalkusch
 *
 * @see prometheus.command.memento.Memento
 * @see prometheus.command.memento.MementoState
 */
public interface MementoCreatorInterface {

	/**
	 * Tell's the object ot create it's memento, stoing all information needed,
	 * to reset the object to the current state in future.
	 * 
	 * @return the created prometheus.command.memento.Memento
	 */
	public Memento createMemento();
	
	/**
	 * Restore a prevoius state using data stored in the Memento.
	 * 
	 * Throws PrometheusMementoException if an inappropriate MementoStateInterface-object
	 * is passed inside the memento, or if the Memento-object does not fit.
	 * 
	 * Derived class has to check, if Memento-object fits.
	 * 
	 * @param setMemento prometheus.command.memento.Memento to reset the obejct in a previouse state
	 */
	public void setMemento( Memento setMemento )
		throws CerberusRuntimeException;
}