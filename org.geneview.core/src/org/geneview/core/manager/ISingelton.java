package org.geneview.core.manager;

import org.geneview.core.manager.ILoggerManager.LoggerType;
import org.geneview.core.manager.data.IGenomeIdManager;
import org.geneview.core.manager.data.IPathwayItemManager;
import org.geneview.core.manager.data.IPathwayManager;
import org.geneview.core.manager.data.ISetManager;
import org.geneview.core.manager.data.IStorageManager;
import org.geneview.core.manager.data.IVirtualArrayManager;
import org.geneview.core.manager.type.ManagerType;

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

	public abstract ILoggerManager getLoggerManager();

	public abstract ISWTGUIManager getSWTGUIManager();
	
	public abstract IGeneralManager getManagerByBaseType(ManagerType type);
	
	public abstract IViewGLCanvasManager getViewGLCanvasManager();
	
	public abstract IEventPublisher getEventPublisher();
	
	public abstract IXmlParserManager getXmlParserManager();
	
	public abstract IPathwayManager getPathwayManager();
	
	public abstract IPathwayItemManager getPathwayItemManager();

	public abstract IGenomeIdManager getGenomeIdManager();
	
	/**
	 * Identifies each application in the network with a unique Id form [1..99]
	 * issued by the network server.
	 * 
	 * @see org.geneview.core.manager.IGeneralManager#iUniqueId_WorkspaceOffset
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

	public abstract void setLoggerManager(ILoggerManager refLoggerManager);

	public abstract void setSWTGUIManager(ISWTGUIManager refSWTGUIManager);
	
	public abstract void setViewGLCanvasManager(IViewGLCanvasManager refViewManager);
	
	public abstract void setXmlParserManager(IXmlParserManager refXmlParserManager);
	
	public abstract void setGenomeIdManager( IGenomeIdManager refIGenomeIdManager);
	
	/**
	 * @see org.geneview.core.manager.ILoggerManager#logMsg(String, LoggerType)
	 * 
	 * @param info log message
	 * @param logLevel type of message
	 */
	public abstract void logMsg( final String info, final LoggerType logLevel );
	
}