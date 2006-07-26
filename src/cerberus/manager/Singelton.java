package cerberus.manager;

import cerberus.manager.type.ManagerType;

public interface Singelton {

	public abstract MementoManager getMementoManager();

	public abstract StorageManager getStorageManager();

	public abstract SelectionManager getSelectionManager();

	public abstract MenuManager getMenuManager();

	public abstract SetManager getSetManager();

	public abstract ViewCanvasManager getViewCanvasManager();

	public abstract CommandManager getCommandManager();

	public abstract DComponentManager getDComponentManager();

	public abstract LoggerManager getLoggerManager();

	public abstract SWTGUIManager getSWTGUIManager();
	
	public abstract GeneralManager getManager(ManagerType type);
	
	/**
	 * Identifies each application in the network with a unique Id form [1..99]
	 * issued by the network server.
	 * 
	 * @return unique networkHostId of this host.
	 */
	public abstract int getNetworkPostfix();

	public abstract void setNetworkPostfix(int iSetNetworkPrefix);

	public abstract void setMenuManager(MenuManager setMenuManager);

	public abstract void setMementoManager(MementoManager setMementoManager);

	public abstract void setStorageManager(StorageManager setStorageManager);

	public abstract void setSelectionManager(
			SelectionManager setSelectionManager);

	public abstract void setSetManager(SetManager setSetManager);

	public abstract void setViewCanvasManager(
			ViewCanvasManager setViewCanvasManager);

	public abstract void setCommandManager(CommandManager setCommandManager);

	public abstract void setDComponentManager(
			DComponentManager setDComponentManager);

	public abstract void setLoggerManager(LoggerManager refLoggerManager);

	public abstract void setSWTGUIManager(SWTGUIManager refSWTGUIManager);
}