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
 * Interface for creator-object's of IMemento's.
 * 
 * This interface must be implemented by all objects, that create Mementos 
 * and restore a prevoius state using the inforamtion stored in the IMemento.
 * Each creator-object must implement it's own state-storage class derived from prometheus.command.memento.MementoState .
 *   
 * Part of DesignPattern "IMemento"
 *   
 * @author Michael Kalkusch
 *
 * @see prometheus.command.memento.IMemento
 * @see prometheus.command.memento.IMementoState
 */
public interface IMementoCreator {

	/**
	 * Tell's the object ot create it's memento, stoing all information needed,
	 * to reset the object to the current state in future.
	 * 
	 * @return the created prometheus.command.memento.Memento
	 */
	public IMemento createMemento();
	
	/**
	 * Restore a prevoius state using data stored in the IMemento.
	 * 
	 * Throws PrometheusMementoException if an inappropriate MementoStateInterface-object
	 * is passed inside the memento, or if the IMemento-object does not fit.
	 * 
	 * Derived class has to check, if IMemento-object fits.
	 * 
	 * @param setMemento prometheus.command.memento.Memento to reset the obejct in a previouse state
	 */
	public void setMemento( IMemento setMemento )
		throws CerberusRuntimeException;
}