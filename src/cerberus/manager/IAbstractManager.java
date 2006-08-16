package cerberus.manager;


public interface IAbstractManager {

	/* (non-Javadoc)
	 * @see cerberus.manager.AbstractManagerImpl#getGeneralManager()
	 */
	public abstract IGeneralManager getGeneralManager();

	public abstract ISingelton getSingelton();

	/* (non-Javadoc)
	 * @see cerberus.manager.AbstractManagerImpl#getGeneralManager()
	 */
	public abstract void setGeneralManager(IGeneralManager setGeneralManager);

	public abstract void setSingelton(ISingelton setSingeltonManager);

}