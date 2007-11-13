/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package org.geneview.core.manager.singleton;

import org.geneview.core.manager.ICommandManager;
import org.geneview.core.manager.IEventPublisher;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.ILoggerManager;
import org.geneview.core.manager.IMementoManager;
import org.geneview.core.manager.IMenuManager;
import org.geneview.core.manager.ISWTGUIManager;
import org.geneview.core.manager.ISingelton;
import org.geneview.core.manager.IViewGLCanvasManager;
import org.geneview.core.manager.IXmlParserManager;
import org.geneview.core.manager.ILoggerManager.LoggerType;
import org.geneview.core.manager.data.IGenomeIdManager;
import org.geneview.core.manager.data.IPathwayItemManager;
import org.geneview.core.manager.data.IPathwayManager;
import org.geneview.core.manager.data.ISetManager;
import org.geneview.core.manager.data.IStorageManager;
import org.geneview.core.manager.data.IVirtualArrayManager;
import org.geneview.core.manager.type.ManagerType;
import org.geneview.core.util.exception.GeneViewRuntimeException;

/**
 * Global object contining and handling several managers.
 * 
 * Design Pattern "ISingelton"
 * 
 * @author Michael Kalkusch
 *
 */
public class SingletonManager 
implements ISingelton {

//	private final IGeneralManager refGeneralManager;
		
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
	 * @see org.geneview.core.manager.IGeneralManager#iUniqueId_WorkspaceOffset
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
	 * @see org.geneview.core.manager.singelton.Singelton#getMementoManager()
	 */
	public IMementoManager getMementoManager() {
		return refMementoManager;
	}
	
	/* (non-Javadoc)	
	 * @see org.geneview.core.manager.singelton.Singelton#getStorageManager()
	 */
	public IStorageManager getStorageManager() {
		return refStorageManager;
	}
		
	/* (non-Javadoc)
	 * @see org.geneview.core.manager.singelton.Singelton#getVirtualArrayManager()
	 */
	public IVirtualArrayManager getVirtualArrayManager() {
		return refVirtualArrayManager;
	}
	
	/* (non-Javadoc)
	 * @see org.geneview.core.manager.singelton.Singelton#getMenuManager()
	 */
	public IMenuManager getMenuManager() {
		return this.refMenuManager;
	}
	
	/* (non-Javadoc)
	 * @see org.geneview.core.manager.singelton.Singelton#getSetManager()
	 */
	public ISetManager getSetManager() {
		return refSetManager;
	}
	
	/* (non-Javadoc)
	 * @see org.geneview.core.manager.singelton.Singelton#getViewGLCanvasManager()
	 */
	public IViewGLCanvasManager getViewGLCanvasManager() {
		return refViewManager;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.manager.ISingelton#getPathwayManager()
	 */
	public IPathwayManager getPathwayManager() {
		
		return refPathwayManager;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.manager.ISingelton#getPathwayItemManager()
	 */
	public IPathwayItemManager getPathwayItemManager() {
		
		return refPathwayItemManager;
	}
	
	/* (non-Javadoc)
	 * @see org.geneview.core.manager.singelton.Singelton#getSWTGUIManager()
	 */
	public ISWTGUIManager getSWTGUIManager() {
		return refSWTGUIManager;
	}
	
	/* (non-Javadoc)
	 * @see org.geneview.core.manager.singelton.Singelton#getEventManager()
	 */
	public IEventPublisher getEventPublisher() {
		return refEventPublisher;
	}
	
	/* (non-Javadoc)
	 * @see org.geneview.core.manager.singelton.Singelton#getLoggerManager()
	 */
	public ILoggerManager getLoggerManager() {
		return this.refLoggerManager;
	}
	
	/* (non-Javadoc)
	 * @see org.geneview.core.manager.singelton.Singelton#getXmlParserManager()
	 */
	public IXmlParserManager getXmlParserManager() {
		return this.refXmlParserManager;
	}
	
	/* (non-Javadoc)
	 * @see org.geneview.core.manager.singelton.Singelton#getCommandManager()
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
	 * @see org.geneview.core.manager.singelton.Singelton#getManager(org.geneview.core.manager.type.ManagerType)
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
	 * @see org.geneview.core.manager.singelton.Singelton#getNetworkPostfix()
	 */
	public int getNetworkPostfix() {
		return iNetworkApplicationIdPostfix;
	}
	
	/**
	 * @see org.geneview.core.manager.singelton.Singelton#setNetworkPostfix(int)
	 * 
	 * @see org.geneview.core.manager.IGeneralManager#iUniqueId_WorkspaceOffset
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
}
