package cerberus.manager;


public interface AbstractManager {

	/* (non-Javadoc)
	 * @see cerberus.manager.AbstractManagerImpl#getGeneralManager()
	 */
	public abstract GeneralManager getGeneralManager();

	public abstract Singelton getSingelton();

	/* (non-Javadoc)
	 * @see cerberus.manager.AbstractManagerImpl#getGeneralManager()
	 */
	public abstract void setGeneralManager(GeneralManager setGeneralManager);

	public abstract void setSingelton(Singelton setSingeltonManager);

}