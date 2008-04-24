package org.caleydo.core.manager;

/**
 * Base interface for all managers. 
 * 
 * @author Michael Kalkusch
 *
 */
public interface IAbstractManager {

	public abstract IGeneralManager getGeneralManager();

	public abstract ISingleton getSingleton();

	public abstract void setGeneralManager(IGeneralManager setGeneralManager);

	public abstract void setSingleton(ISingleton setSingeltonManager);
}