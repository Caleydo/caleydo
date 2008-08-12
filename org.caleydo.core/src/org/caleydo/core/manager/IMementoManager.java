package org.caleydo.core.manager;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.caleydo.core.command.memento.IMemento;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * Administration of IMemento's
 * 
 * @author Michael Kalkusch
 */
public interface IMementoManager
{

	/**
	 * Add a new IMemento to the IMementoManager.
	 * 
	 * @param addMemento
	 * @return unique mementoId
	 * @throws exception on errors
	 */
	public abstract int pushMemento(IMemento addMemento) throws CaleydoRuntimeException;

	/**
	 * Add a new IMemento to the IMementoManager and register a unique
	 * MementoName.
	 * 
	 * @param addMemento
	 * @param sMementoId
	 * @return unique mementoId
	 * @throws PrometheusMementoException if the String "sMementoId" is already
	 *             in use.
	 */
	// public abstract int pushMemento(IMemento addMemento, String sMementoId)
	// throws PrometheusMementoException;
	/**
	 * Get a IMemento by it's iMementoId. The iMementoId is created by the
	 * IMementoManager @see setMemento()
	 * 
	 * @param iMementoId unique Id
	 * @return memento bound to this unique id
	 * @throws PrometheusMementoException if no IMemento is bound to int
	 *             "iMementoId"
	 */
	public abstract IMemento getMemento(final int iMementoId);

	/**
	 * Get a IMemento by it's uniuqe sMementoId. The sMementoId is added to the
	 * IMementoManager @see setMemento()
	 * 
	 * @param iMementoId
	 * @return IMemento on sucsess, else returns null
	 * @throws PrometheusMementoException if no IMemento is bound to String
	 *             "sMementoId"
	 */
	public abstract IMemento pullMemento(final int iMementoId);

	/**
	 * Get a IMemento and removes it from the storage.
	 * 
	 * @param pullMemento IMemento, that shoud be pulled
	 * @return IMemento on sucsess, else returns null
	 */
	public abstract boolean pullMemento(IMemento pullMemento);

	/**
	 * Test if a IMemento with this int "iMementoId" does exist.
	 * 
	 * @param iMementoId
	 * @return TRUE if int "iMementoId" does exist.
	 */
	public abstract boolean isMementoId(int iMementoId);

	/**
	 * Create usefull debug information like number of stored Mementos.
	 * 
	 * @return details on storage
	 */
	public abstract String toString();

	/**
	 * Optimizes the mementos.
	 */
	public abstract void optimize();

	/**
	 * Writes all current stored mementos to the ObjectOutputStream.
	 * 
	 * @return true on success
	 */
	public abstract boolean writeToOutputStream(ObjectOutputStream outStream);

	/**
	 * Reads stored mementos from ObjectInputStream.
	 * 
	 * @return true on success
	 */
	public abstract boolean readFromInputStream(ObjectInputStream inStream);

	/**
	 * Removes all stored mementos.
	 */
	public abstract void clearAllMementos();

}
