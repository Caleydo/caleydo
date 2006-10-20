package cerberus.manager;

import cerberus.manager.data.IPathwayElementManager;
import cerberus.manager.data.IPathwayManager;
import cerberus.manager.data.ISelectionManager;
import cerberus.manager.data.ISetManager;
import cerberus.manager.data.IStorageManager;
import cerberus.manager.type.ManagerType;
import cerberus.xml.parser.manager.XmlParserManager;

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
	
	public abstract IViewGLCanvasManager getViewGLCanvasManager();
	
	public abstract IEventPublisher getEventPublisher();
	
	public abstract XmlParserManager getXmlParserManager();
	
	public abstract IPathwayManager getPathwayManager();
	
	public abstract IPathwayElementManager getPathwayElementManager();
	
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
	
	public abstract void setViewGLCanvasManager(IViewGLCanvasManager refViewManager);
	
	public abstract void setXmlParserManager(XmlParserManager refXmlParserManager);
}