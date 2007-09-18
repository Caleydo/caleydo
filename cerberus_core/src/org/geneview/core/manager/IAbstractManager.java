package org.geneview.core.manager;


public interface IAbstractManager {

	/* (non-Javadoc)
	 * @see org.geneview.core.manager.AbstractManagerImpl#getGeneralManager()
	 */
	public abstract IGeneralManager getGeneralManager();

	public abstract ISingelton getSingelton();

	/* (non-Javadoc)
	 * @see org.geneview.core.manager.AbstractManagerImpl#getGeneralManager()
	 */
	public abstract void setGeneralManager(IGeneralManager setGeneralManager);

	public abstract void setSingelton(ISingelton setSingeltonManager);

}