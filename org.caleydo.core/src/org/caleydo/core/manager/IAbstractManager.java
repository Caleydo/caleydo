package org.caleydo.core.manager;

/**
 * Base interface for all managers. 
 * 
 * @author Michael Kalkusch
 *
 */
public interface IAbstractManager {

	public abstract IGeneralManager getGeneralManager();

	public abstract ISingelton getSingelton();

	public abstract void setGeneralManager(IGeneralManager setGeneralManager);

	public abstract void setSingelton(ISingelton setSingeltonManager);
}