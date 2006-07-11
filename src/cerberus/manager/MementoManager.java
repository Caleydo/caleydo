/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.manager;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


import cerberus.command.memento.Memento;
import cerberus.util.exception.PrometheusMementoException;
//import prometheus.util.exception.PrometheusRuntimeException;

/**
 * Administration of Memento's
 * 
 * @author Michael Kalkusch
 *
 */
public interface MementoManager 
extends GeneralManager
{

	/**
	 * Add a new Memento to the MementoManager.
	 * 
	 * @param addMemento
	 * @return unique mementoId
	 * 
	 * @throws exception on errors
	 */
	public abstract int pushMemento(Memento addMemento)
		throws PrometheusMementoException;
	
	
	/**
	 * Add a new Memento to the MementoManager and register a unique MementoName.
	 * 
	 * @param addMemento
	 * @param sMementoId
	 * @return unique mementoId
	 * 
	 * @throws PrometheusMementoException if the String "sMementoId" is already in use.
	 */
	//public abstract int pushMemento(Memento addMemento, String sMementoId)
	//	throws PrometheusMementoException;
	

	/**
	 * Get a Memento by it's iMementoId. 
	 * The iMementoId is created by the MementoManager @see setMemento()
	 * 
	 * @param iMementoId unique Id
	 * @return memento bound to this unique id
	 * @throws PrometheusMementoException if no Memento is bound to int "iMementoId"
	 */
	public abstract Memento getMemento( final int iMementoId );
	
	
	/**
	 * Get a Memento by it's uniuqe sMementoId. 
	 * The sMementoId is added to the MementoManager @see setMemento()
	 * 
	 * @param iMementoId
	 * @return Memento on sucsess, else returns null
	 * @throws PrometheusMementoException if no Memento is bound to String "sMementoId"
	 */
	public abstract Memento pullMemento( final int iMementoId );
	
	
	/**
	 * Get a Memento and removes it from the storage.
	 * 
	 * @param pullMemento Memento, that shoud be pulled
	 * @return Memento on sucsess, else returns null
	 */
	public abstract boolean pullMemento( Memento pullMemento );
	
	
	/**
	 * Test if a Memento with this int "iMementoId" does exist.
	 * 
	 * @param iMementoId
	 * @return TRUE if int "iMementoId" does exist.
	 */
	public abstract boolean isMementoId( int iMementoId );
	
	/**
	 * Create usefull debug information like number of stored Mementos.
	 * 
	 * @return details on storage
	 */
	public abstract String toString();
	
	/**
	 * Optimizes the mementos.
	 *
	 */
	public abstract void optimize();
	
	/**
	 * Writes all current stored mementos to the ObjectOutputStream.
	 * 
	 * @return true on success
	 */
	public abstract boolean writeToOutputStream( ObjectOutputStream outStream);
	
	/**
	 *  Reads stored mementos from ObjectInputStream.
	 * 
	 * @return true on success
	 */
	public abstract boolean readFromInputStream( ObjectInputStream inStream );
	
	/**
	 * Removes all stored mementos.
	 *
	 */
	public abstract void clearAllMementos();
	

	
}
