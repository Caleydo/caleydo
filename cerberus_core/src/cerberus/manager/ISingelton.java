package cerberus.manager;

import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.data.IGenomeIdManager;
import cerberus.manager.data.IPathwayElementManager;
import cerberus.manager.data.IPathwayManager;
import cerberus.manager.data.IVirtualArrayManager;
import cerberus.manager.data.ISetManager;
import cerberus.manager.data.IStorageManager;
import cerberus.manager.type.ManagerType;

public interface ISingelton {

	/**
	 * Initialize the singelton. 
	 * Call this method before using the singelton.
	 *
	 */
	public void initManager();
	
	public abstract IMementoManager getMementoManager();

	public abstract IStorageManager getStorageManager();

	public abstract IVirtualArrayManager getVirtualArrayManager();

	public abstract IMenuManager getMenuManager();

	public abstract ISetManager getSetManager();

//	public abstract IViewCanvasManager getViewCanvasManager();

	public abstract ICommandManager getCommandManager();

//	public abstract IDistComponentManager getDComponentManager();

	public abstract ILoggerManager getLoggerManager();

	public abstract ISWTGUIManager getSWTGUIManager();
	
	public abstract IGeneralManager getManagerByBaseType(ManagerType type);
	
	public abstract IViewGLCanvasManager getViewGLCanvasManager();
	
	public abstract IEventPublisher getEventPublisher();
	
	public abstract IXmlParserManager getXmlParserManager();
	
	public abstract IPathwayManager getPathwayManager();
	
	public abstract IPathwayElementManager getPathwayElementManager();

	public abstract IGenomeIdManager getGenomeIdManager();
	
	/**
	 * Identifies each application in the network with a unique Id form [1..99]
	 * issued by the network server.
	 * 
	 * @see cerberus.manager.IGeneralManager#iUniqueId_WorkspaceOffset
	 * 
	 * @return unique networkHostId of this host.
	 */
	public abstract int getNetworkPostfix();

	public abstract void setNetworkPostfix(int iSetNetworkPrefix);

	public abstract void setMenuManager(IMenuManager setMenuManager);

	public abstract void setMementoManager(IMementoManager setMementoManager);

	public abstract void setStorageManager(IStorageManager setStorageManager);

	public abstract void setVirtualArrayManager(
			IVirtualArrayManager setVirtualArrayManager);

	public abstract void setSetManager(ISetManager setSetManager);

//	public abstract void setViewCanvasManager(
//			IViewCanvasManager setViewCanvasManager);

	public abstract void setCommandManager(ICommandManager setCommandManager);

//	public abstract void setDComponentManager(
//			IDistComponentManager setDComponentManager);

	public abstract void setLoggerManager(ILoggerManager refLoggerManager);

	public abstract void setSWTGUIManager(ISWTGUIManager refSWTGUIManager);
	
	public abstract void setViewGLCanvasManager(IViewGLCanvasManager refViewManager);
	
	public abstract void setXmlParserManager(IXmlParserManager refXmlParserManager);
	
	public abstract void setGenomeIdManager( IGenomeIdManager refIGenomeIdManager);
	
	/**
	 * @see cerberus.manager.ILoggerManager#logMsg(String, LoggerType)
	 * 
	 * @param info log message
	 * @param logLevel type of message
	 */
	public abstract void logMsg( final String info, final LoggerType logLevel );
	
	/**
	 * Note: each message will be sent with LoggerType.FULL
	 * 
	 * @see cerberus.manager.ILoggerManager#logMsg( String )
	 * 
	 * @param info log message
	 */
	public abstract void logMsg( final String info );
	
	
}