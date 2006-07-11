package cerberus.manager;

import cerberus.manager.singelton.SingeltonManager;

public interface AbstractManager {

	/* (non-Javadoc)
	 * @see cerberus.manager.AbstractManagerImpl#getGeneralManager()
	 */
	public abstract GeneralManager getGeneralManager();

	public abstract SingeltonManager getSingelton();

	/* (non-Javadoc)
	 * @see cerberus.manager.AbstractManagerImpl#getGeneralManager()
	 */
	public abstract void setGeneralManager(GeneralManager setGeneralManager);

	public abstract void setSingelton(SingeltonManager setSingeltonManager);

}