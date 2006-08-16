package cerberus.manager;

import cerberus.manager.type.ManagerType;

public interface ISingelton {

	/**
	 * Initialize the singelton. 
	 * Call this methode before using the singelton.
	 *
	 */
	public void initManager();
	
	public abstract IMementoManager getMementoManager();

	public abstract IStorageManager getStorageManager();

	public abstract ISelectionManager getSelectionManager();

	public abstract IMenuManager getMenuManager();

	public abstract ISetManager getSetManager();

	public abstract IViewCanvasManager getViewCanvasManager();

	public abstract ICommandManager getCommandManager();

	public abstract IDistComponentManager getDComponentManager();

	public abstract ILoggerManager getLoggerManager();

	public abstract ISWTGUIManager getSWTGUIManager();
	
	public abstract IGeneralManager getManagerByBaseType(ManagerType type);
	
	public abstract IViewManager getViewManager(ManagerType type);
	
	/**
	 * Identifies each application in the network with a unique Id form [1..99]
	 * issued by the network server.
	 * 
	 * @return unique networkHostId of this host.
	 */
	public abstract int getNetworkPostfix();

	public abstract void setNetworkPostfix(int iSetNetworkPrefix);

	public abstract void setMenuManager(IMenuManager setMenuManager);

	public abstract void setMementoManager(IMementoManager setMementoManager);

	public abstract void setStorageManager(IStorageManager setStorageManager);

	public abstract void setSelectionManager(
			ISelectionManager setSelectionManager);

	public abstract void setSetManager(ISetManager setSetManager);

	public abstract void setViewCanvasManager(
			IViewCanvasManager setViewCanvasManager);

	public abstract void setCommandManager(ICommandManager setCommandManager);

	public abstract void setDComponentManager(
			IDistComponentManager setDComponentManager);

	public abstract void setLoggerManager(ILoggerManager refLoggerManager);

	public abstract void setSWTGUIManager(ISWTGUIManager refSWTGUIManager);
	
	public abstract void setViewManager(IViewManager refViewManager);
}