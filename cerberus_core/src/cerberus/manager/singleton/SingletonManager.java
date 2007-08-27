/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.manager.singleton;

import cerberus.manager.ICommandManager;
import cerberus.manager.IEventPublisher;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager;
import cerberus.manager.IMementoManager;
import cerberus.manager.IMenuManager;
import cerberus.manager.ISWTGUIManager;
import cerberus.manager.ISingelton;
import cerberus.manager.IViewGLCanvasManager;
import cerberus.manager.IXmlParserManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.data.IGenomeIdManager;
import cerberus.manager.data.IPathwayItemManager;
import cerberus.manager.data.IPathwayManager;
import cerberus.manager.data.ISetManager;
import cerberus.manager.data.IStorageManager;
import cerberus.manager.data.IVirtualArrayManager;
import cerberus.manager.type.ManagerType;
import cerberus.util.exception.GeneViewRuntimeException;

/**
 * Global object contining and handling several managers.
 * 
 * Desing Pattern "ISingelton"
 * 
 * @author Michael Kalkusch
 *
 */
public class SingletonManager 
implements ISingelton {

//	private final IGeneralManager refGeneralManager;
	
//	protected IViewCanvasManager refViewCanvasManager;
	
	protected IStorageManager refStorageManager;
	/**
	 * Store all undo& redo Mementos
	 */
	protected IMementoManager refMementoManager;
	
	protected IVirtualArrayManager refVirtualArrayManager;
	
	protected ISetManager refSetManager;
	
	protected ICommandManager refCommandManager;
	
	protected IMenuManager refMenuManager;
	
	protected ILoggerManager refLoggerManager;
	
	protected ISWTGUIManager refSWTGUIManager;
	
	protected IViewGLCanvasManager refViewManager;
	
	protected IPathwayManager refPathwayManager;
	
	protected IPathwayItemManager refPathwayItemManager;
	
	protected IEventPublisher refEventPublisher;
	
	protected IXmlParserManager refXmlParserManager;
	
	protected IGenomeIdManager refIGenomeIdManager;

	
	/**
	 * Unique Id per each application over the network.
	 * Used to identify and create Id's unique for distributed applications. 
	 * 
	 * @see cerberus.manager.IGeneralManager#iUniqueId_WorkspaceOffset
	 */
	private int iNetworkApplicationIdPostfix = 0;
	
	/**
	 * Constructor
	 */
	public SingletonManager( final IGeneralManager refGeneralManager ) {
				
//		this.refGeneralManager = refGeneralManager;			
		
	}
	
	/**
	 * Initialize the objects
	 *
	 */
	public void initManager() {
		//refLoggerManager = new ConsoleSimpleLogger( refGeneralManager );
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.singelton.Singelton#getMementoManager()
	 */
	public IMementoManager getMementoManager() {
		return refMementoManager;
	}
	
	/* (non-Javadoc)	
	 * @see cerberus.manager.singelton.Singelton#getStorageManager()
	 */
	public IStorageManager getStorageManager() {
		return refStorageManager;
	}
		
	/* (non-Javadoc)
	 * @see cerberus.manager.singelton.Singelton#getVirtualArrayManager()
	 */
	public IVirtualArrayManager getVirtualArrayManager() {
		return refVirtualArrayManager;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.singelton.Singelton#getMenuManager()
	 */
	public IMenuManager getMenuManager() {
		return this.refMenuManager;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.singelton.Singelton#getSetManager()
	 */
	public ISetManager getSetManager() {
		return refSetManager;
	}
	
//	/* (non-Javadoc)
//	 * @see cerberus.manager.singelton.Singelton#getViewCanvasManager()
//	 */
//	public IViewCanvasManager getViewCanvasManager() {
//		return refViewCanvasManager;
//	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.singelton.Singelton#getViewGLCanvasManager()
	 */
	public IViewGLCanvasManager getViewGLCanvasManager() {
		return refViewManager;
	}
	
	/*
	 * (non-Javadoc)
	 * @see cerberus.manager.ISingelton#getPathwayManager()
	 */
	public IPathwayManager getPathwayManager() {
		
		return refPathwayManager;
	}
	
	/*
	 * (non-Javadoc)
	 * @see cerberus.manager.ISingelton#getPathwayItemManager()
	 */
	public IPathwayItemManager getPathwayItemManager() {
		
		return refPathwayItemManager;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.singelton.Singelton#getSWTGUIManager()
	 */
	public ISWTGUIManager getSWTGUIManager() {
		return refSWTGUIManager;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.singelton.Singelton#getEventManager()
	 */
	public IEventPublisher getEventPublisher() {
		return refEventPublisher;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.singelton.Singelton#getLoggerManager()
	 */
	public ILoggerManager getLoggerManager() {
		return this.refLoggerManager;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.singelton.Singelton#getXmlParserManager()
	 */
	public IXmlParserManager getXmlParserManager() {
		return this.refXmlParserManager;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.singelton.Singelton#getCommandManager()
	 */
	public ICommandManager getCommandManager() {
		return refCommandManager;
	}
		
	public IGenomeIdManager getGenomeIdManager() {
		return this.refIGenomeIdManager;
	}
	
	public void setMenuManager( IMenuManager setMenuManager ) {
		this.refMenuManager = setMenuManager;
	}
	
	public void setMementoManager( IMementoManager setMementoManager ) {
		assert setMementoManager!=null: "IMementoManager was null";
		
		refMementoManager = setMementoManager;
	}
	
	public void setStorageManager( IStorageManager setStorageManager ) {
		assert setStorageManager!=null: "IStorageManager was null";
		
		refStorageManager = setStorageManager;
	}
		
	public void setVirtualArrayManager( IVirtualArrayManager setVirtualArrayManager ) {
		assert setVirtualArrayManager!=null: "IVirtualArrayManager was null";
		
		refVirtualArrayManager = setVirtualArrayManager;
	}
	
	public void setSetManager( ISetManager setSetManager ) {
		assert setSetManager!=null: "ISetManager was null";
		
		refSetManager = setSetManager;
	}
	
//	public void setViewCanvasManager( IViewCanvasManager setViewCanvasManager ) {
//		assert setViewCanvasManager != null : "IViewCanvasManager was null";
//		
//		refViewCanvasManager = setViewCanvasManager;
//	}
	
	public void setViewGLCanvasManager( IViewGLCanvasManager setViewManager ) {
		assert setViewManager != null : "IViewManager was null";
		
		refViewManager = setViewManager;
	}	

	public void setSWTGUIManager( ISWTGUIManager setSWTGUIManager ) {
		assert setSWTGUIManager != null : "ISWTGUIManager was null";
		
		refSWTGUIManager = setSWTGUIManager;
	}
	
	public void setCommandManager( ICommandManager setCommandManager ) {
		assert setCommandManager != null : "ICommandManager was null";
		
		refCommandManager = setCommandManager;
	}
	
	public void setLoggerManager( ILoggerManager refLoggerManager ) {
		assert refLoggerManager != null : "ILoggerManager was null";
		
		this.refLoggerManager = refLoggerManager;
	}

	public void setXmlParserManager( IXmlParserManager refXmlParserManager ) {
		assert refXmlParserManager != null : "XmlParserManager was null";
		
		this.refXmlParserManager = refXmlParserManager;
	}

	public void setPathwayManager(IPathwayManager refPathwayManager) {
		this.refPathwayManager = refPathwayManager;
	}
	
	public void setPathwayItemManager(IPathwayItemManager refPathwayItemManager) {
		this.refPathwayItemManager = refPathwayItemManager;
	}	
	
	public void setEventPublisher(IEventPublisher refEventPublisher)
	{
		this.refEventPublisher = refEventPublisher;
	}
	
	public void setGenomeIdManager( IGenomeIdManager refIGenomeIdManager) {
		this.refIGenomeIdManager = refIGenomeIdManager;
	}
	
	
	/* (non-Javadoc)
	 * @see cerberus.manager.singelton.Singelton#getManager(cerberus.manager.type.ManagerType)
	 */
	public IGeneralManager getManagerByBaseType( ManagerType type) {
		
		switch ( type ) 
		{
			case COMMAND: return this.refCommandManager;
		
			case DATA_SET: return this.refSetManager;
		
			case DATA_STORAGE: return this.refStorageManager;
		
			case DATA_VIRTUAL_ARRAY: return this.refVirtualArrayManager;
		
			case MEMENTO: return this.refMementoManager;
				
			case VIEW: return this.refViewManager;
		
			case MENU: return this.refMenuManager;
			
			case VIEW_GUI_SWT: return this.refSWTGUIManager;
		
			case NONE: 
				throw new GeneViewRuntimeException("No Manager for type 'NONE' available!");
	
			case LOGGER: return this.refLoggerManager;
				
			
			default: 
				throw new GeneViewRuntimeException("No Manager for type [" + 
						type.toString() + "] available!");
		}
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.singelton.Singelton#getNetworkPostfix()
	 */
	public int getNetworkPostfix() {
		return iNetworkApplicationIdPostfix;
	}
	
	/**
	 * @see cerberus.manager.singelton.Singelton#setNetworkPostfix(int)
	 * 
	 * @see cerberus.manager.IGeneralManager#iUniqueId_WorkspaceOffset
	 */
	public void setNetworkPostfix( int iSetNetworkPrefix ) {
		if (( iSetNetworkPrefix < IGeneralManager.iUniqueId_WorkspaceOffset) && 
				( iSetNetworkPrefix >= 0)) { 
			iNetworkApplicationIdPostfix = iSetNetworkPrefix;
			return;
		}
		throw new RuntimeException("SIngeltonManager.setNetworkPostfix() exceeded range [0.." +
				IGeneralManager.iUniqueId_WorkspaceOffset + "] ");
	}

	public final void logMsg( final String info, final LoggerType logLevel) {
		refLoggerManager.logMsg( info, logLevel );		
	}

	public final void logMsg(String info) {
		refLoggerManager.logMsg( info, LoggerType.FULL );	
	}
}
