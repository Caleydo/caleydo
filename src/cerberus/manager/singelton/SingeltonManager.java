/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.manager.singelton;

import cerberus.manager.ICommandManager;
import cerberus.manager.IDistComponentManager;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager;
import cerberus.manager.IMementoManager;
import cerberus.manager.IMenuManager;
import cerberus.manager.ISWTGUIManager;
import cerberus.manager.ISelectionManager;
import cerberus.manager.ISetManager;
import cerberus.manager.ISingelton;
import cerberus.manager.IStorageManager;
import cerberus.manager.IViewCanvasManager;
import cerberus.manager.IViewManager;
import cerberus.manager.logger.ConsoleSimpleLogger;
//import prometheus.net.dwt.swing.mdi.DDesktopPane;

import cerberus.manager.type.ManagerType;

import cerberus.util.exception.CerberusRuntimeException;

/**
 * Global object contining and handling several managers.
 * 
 * Desing Pattern "ISingelton"
 * 
 * @author Michael Kalkusch
 *
 */
public class SingeltonManager implements ISingelton {

	//private final IGeneralManager refGeneralManager;
	
	protected IDistComponentManager refDComponentManager = null;
	
	protected IViewCanvasManager refViewCanvasManager = null;
	
	protected IStorageManager refStorageManager = null;
	/**
	 * Store all undo& redo Mementos
	 */
	protected IMementoManager refMementoManager = null;
	
	protected ISelectionManager refSelectionManager = null;
	
	protected ISetManager refSetManager = null;
	
	//protected DDesktopPane refDDesktopPane = null;
	
	protected ICommandManager refCommandManager = null;
	
	protected IMenuManager refMenuManager = null;
	
	protected ILoggerManager refLoggerManager = null;
	
	protected ISWTGUIManager refSWTGUIManager = null;
	
	protected IViewManager refViewManager = null;
	
	private IGeneralManager refGeneralManager = null;
	
	/**
	 * Unique Id per each application over the network.
	 * Used to identify and create Id's unique for distributed applications. 
	 */
	private int iNetworkApplicationIdPostfix = 1;
	
	/**
	 * Constructor
	 */
	public SingeltonManager( final IGeneralManager refGeneralManager ) {
				
		this.refGeneralManager = refGeneralManager;			
		
	}
	
	/**
	 * Initialize the objects
	 *
	 */
	public void initManager() {
		refLoggerManager = new ConsoleSimpleLogger( refGeneralManager );
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.singelton.Singelton#getMementoManager()
	 */
	public IMementoManager getMementoManager() {
		return refMementoManager;
	}
	
	/* (non-Javadoc)	public void setMenuManager( MenuManager setMenuManager ) {
		this.refMenuManager = setMenuManager;
	}
	 * @see cerberus.manager.singelton.Singelton#getStorageManager()
	 */
	public IStorageManager getStorageManager() {
		return refStorageManager;
	}
		
	/* (non-Javadoc)
	 * @see cerberus.manager.singelton.Singelton#getSelectionManager()
	 */
	public ISelectionManager getSelectionManager() {
		return refSelectionManager;
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
	
	/* (non-Javadoc)
	 * @see cerberus.manager.singelton.Singelton#getViewCanvasManager()
	 */
	public IViewCanvasManager getViewCanvasManager() {
		return refViewCanvasManager;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.singelton.Singelton#getViewManager()
	 */
	public IViewManager getViewManager(ManagerType type) {
		return refViewManager;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.singelton.Singelton#getCommandManager()
	 */
	public ICommandManager getCommandManager() {
		return refCommandManager;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.singelton.Singelton#getSWTGUIManager()
	 */
	public ISWTGUIManager getSWTGUIManager() {
		return refSWTGUIManager;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.singelton.Singelton#getDComponentManager()
	 */
	public IDistComponentManager getDComponentManager() {
		return refDComponentManager;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.singelton.Singelton#getLoggerManager()
	 */
	public ILoggerManager getLoggerManager() {
		return this.refLoggerManager;
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
		
	public void setSelectionManager( ISelectionManager setSelectionManager ) {
		assert setSelectionManager!=null: "ISelectionManager was null";
		
		refSelectionManager = setSelectionManager;
	}
	
	public void setSetManager( ISetManager setSetManager ) {
		assert setSetManager!=null: "ISetManager was null";
		
		refSetManager = setSetManager;
	}
	
	public void setViewCanvasManager( IViewCanvasManager setViewCanvasManager ) {
		assert setViewCanvasManager != null : "IViewCanvasManager was null";
		
		refViewCanvasManager = setViewCanvasManager;
	}
	
	public void setViewManager( IViewManager setViewManager ) {
		assert setViewManager != null : "IViewManager was null";
		
		refViewManager = setViewManager;
	}	

	public void setSWTGUIManager( ISWTGUIManager setSWTGUIManager ) {
		assert setSWTGUIManager != null : "SWTGUIManager was null";
		
		refSWTGUIManager = setSWTGUIManager;
	}
	
	public void setCommandManager( ICommandManager setCommandManager ) {
		refCommandManager = setCommandManager;
	}
	
	public void setDComponentManager( IDistComponentManager setDComponentManager ) {
		assert setDComponentManager!=null: "IDistComponentManager was null";
		
		refDComponentManager = setDComponentManager;
		
	}
	
	public void setLoggerManager( ILoggerManager refLoggerManager ) {
		this.refLoggerManager = refLoggerManager;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.singelton.Singelton#getManager(cerberus.manager.type.ManagerType)
	 */
	public IGeneralManager getManagerByBaseType( ManagerType type) {
		
		switch ( type ) 
		{
			case COMMAND: return this.refCommandManager;
		
			case SET: return this.refSetManager;
		
			case STORAGE: return this.refStorageManager;
		
			case SELECTION: return this.refSelectionManager;
		
			case MEMENTO: return this.refMementoManager;
				
			case VIEW: return this.refViewManager;
		
			case MENU: return this.refMenuManager;
			
			case GUI_SWT: return this.refSWTGUIManager;
		
			case NONE: 
				throw new CerberusRuntimeException("No Manager for type 'NONE' available!");
	
			case LOGGER: return this.refLoggerManager;
				
			
			default: 
				throw new CerberusRuntimeException("No Manager for type [" + 
						type.toString() + "] available!");
		}
	}
	
//	/**
//	 * Get a reference to the desktop pane. 
//	 * Needed to create new windows in the multi document environment.
//	 *  
//	 * @see javax.swing.JDeskopPane
//	 * @see javax.swing.JInternalFrame
//	 * 
//	 * @return reference to JDesktopPane
//	 */
//	public DDesktopPane getDDesktopPane() {	
//		assert refDDesktopPane != null: "ASSERT because JDesktopPane has not been set! (null-pointer)";
//		
//		return refDDesktopPane;
//	}
//	
//	/**
//	 * ISet a reference to the desktop pane. 
//	 * Needed to create new windows in the multi document environment.
//	 * 
//	 * @see javax.swing.JDeskopPane
//	 * @see javax.swing.JInternalFrame
//	 * 
//	 * @param setDDesktopPane reference to JDesktopPane
//	 */
//	public void setDDesktopPane( final DDesktopPane setDDesktopPane ) {
//		refDDesktopPane = setDDesktopPane;
//	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.singelton.Singelton#getNetworkPostfix()
	 */
	public int getNetworkPostfix() {
		return iNetworkApplicationIdPostfix;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.singelton.Singelton#setNetworkPostfix(int)
	 */
	public void setNetworkPostfix( int iSetNetworkPrefix ) {
		if (( iSetNetworkPrefix < 100) && ( iSetNetworkPrefix > 0)) { 
			iNetworkApplicationIdPostfix = iSetNetworkPrefix;
		}
		throw new RuntimeException("SIngeltonManager.setNetworkPostfix() exceeded range [0..99] ");
	}
}
