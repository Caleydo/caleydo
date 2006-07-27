/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.manager.singelton;

import cerberus.manager.CommandManager;
import cerberus.manager.DComponentManager;
import cerberus.manager.GeneralManager;
import cerberus.manager.LoggerManager;
import cerberus.manager.MementoManager;
import cerberus.manager.MenuManager;
import cerberus.manager.SWTGUIManager;
import cerberus.manager.SelectionManager;
import cerberus.manager.SetManager;
import cerberus.manager.Singelton;
import cerberus.manager.StorageManager;
import cerberus.manager.ViewCanvasManager;
import cerberus.manager.ViewManager;
//import prometheus.net.dwt.swing.mdi.DDesktopPane;

import cerberus.manager.type.ManagerType;

import cerberus.util.exception.CerberusRuntimeException;

/**
 * Global object contining and handling several managers.
 * 
 * Desing Pattern "Singelton"
 * 
 * @author Michael Kalkusch
 *
 */
public class SingeltonManager implements Singelton {

	protected DComponentManager refDComponentManager = null;
	
	protected ViewCanvasManager refViewCanvasManager = null;
	
	protected StorageManager refStorageManager = null;
	/**
	 * Store all undo& redo Mementos
	 */
	protected MementoManager refMementoManager = null;
	
	protected SelectionManager refSelectionManager = null;
	
	protected SetManager refSetManager = null;
	
	//protected DDesktopPane refDDesktopPane = null;
	
	protected CommandManager refCommandManager = null;
	
	protected MenuManager refMenuManager = null;
	
	protected LoggerManager refLoggerManager = null;
	
	protected SWTGUIManager refSWTGUIManager = null;
	
	protected ViewManager refViewManager = null;
	
	
	/**
	 * Unique Id per each application over the network.
	 * Used to identify and create Id's unique for distributed applications. 
	 */
	private int iNetworkApplicationIdPostfix = 1;
	
	/**
	 * Constructor
	 */
	public SingeltonManager() {
				
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.singelton.Singelton#getMementoManager()
	 */
	public MementoManager getMementoManager() {
		return refMementoManager;
	}
	
	/* (non-Javadoc)	public void setMenuManager( MenuManager setMenuManager ) {
		this.refMenuManager = setMenuManager;
	}
	 * @see cerberus.manager.singelton.Singelton#getStorageManager()
	 */
	public StorageManager getStorageManager() {
		return refStorageManager;
	}
		
	/* (non-Javadoc)
	 * @see cerberus.manager.singelton.Singelton#getSelectionManager()
	 */
	public SelectionManager getSelectionManager() {
		return refSelectionManager;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.singelton.Singelton#getMenuManager()
	 */
	public MenuManager getMenuManager() {
		return this.refMenuManager;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.singelton.Singelton#getSetManager()
	 */
	public SetManager getSetManager() {
		return refSetManager;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.singelton.Singelton#getViewCanvasManager()
	 */
	public ViewCanvasManager getViewCanvasManager() {
		return refViewCanvasManager;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.singelton.Singelton#getViewManager()
	 */
	public ViewManager getViewManager(ManagerType type) {
		return refViewManager;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.singelton.Singelton#getCommandManager()
	 */
	public CommandManager getCommandManager() {
		return refCommandManager;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.singelton.Singelton#getSWTGUIManager()
	 */
	public SWTGUIManager getSWTGUIManager() {
		return refSWTGUIManager;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.singelton.Singelton#getDComponentManager()
	 */
	public DComponentManager getDComponentManager() {
		return refDComponentManager;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.singelton.Singelton#getLoggerManager()
	 */
	public LoggerManager getLoggerManager() {
		return this.refLoggerManager;
	}
	
	public void setMenuManager( MenuManager setMenuManager ) {
		this.refMenuManager = setMenuManager;
	}
	
	public void setMementoManager( MementoManager setMementoManager ) {
		assert setMementoManager!=null: "MementoManager was null";
		
		refMementoManager = setMementoManager;
	}
	
	public void setStorageManager( StorageManager setStorageManager ) {
		assert setStorageManager!=null: "StorageManager was null";
		
		refStorageManager = setStorageManager;
	}
		
	public void setSelectionManager( SelectionManager setSelectionManager ) {
		assert setSelectionManager!=null: "SelectionManager was null";
		
		refSelectionManager = setSelectionManager;
	}
	
	public void setSetManager( SetManager setSetManager ) {
		assert setSetManager!=null: "SetManager was null";
		
		refSetManager = setSetManager;
	}
	
	public void setViewCanvasManager( ViewCanvasManager setViewCanvasManager ) {
		assert setViewCanvasManager != null : "ViewCanvasManager was null";
		
		refViewCanvasManager = setViewCanvasManager;
	}
	
	public void setViewManager( ViewManager setViewManager ) {
		assert setViewManager != null : "ViewManager was null";
		
		refViewManager = setViewManager;
	}	

	public void setSWTGUIManager( SWTGUIManager setSWTGUIManager ) {
		assert setSWTGUIManager != null : "SWTGUIManager was null";
		
		refSWTGUIManager = setSWTGUIManager;
	}
	
	public void setCommandManager( CommandManager setCommandManager ) {
		refCommandManager = setCommandManager;
	}
	
	public void setDComponentManager( DComponentManager setDComponentManager ) {
		assert setDComponentManager!=null: "DComponentManager was null";
		
		refDComponentManager = setDComponentManager;
		
	}
	
	public void setLoggerManager( LoggerManager refLoggerManager ) {
		this.refLoggerManager = refLoggerManager;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.singelton.Singelton#getManager(cerberus.manager.type.ManagerType)
	 */
	public GeneralManager getManager( ManagerType type) {
		
		switch ( type ) 
		{
			case COMMAND: return this.refCommandManager;
		
			case SET: return this.refSetManager;
		
			case STORAGE: return this.refStorageManager;
		
			case SELECTION: return this.refSelectionManager;
		
			case MEMENTO: return this.refMementoManager;
				
			case VIEW: return this.refViewCanvasManager;
		
			case MENU: return this.refMenuManager;
			
			case GUI_SWT: return this.refSWTGUIManager;
		
			case NONE: 
				throw new CerberusRuntimeException("No Manager for type 'NONE' available!");
	
			case LOGGER:
				//TODO: fix this
				//return this.refLoggerManager;
				
			
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
//	 * Set a reference to the desktop pane. 
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
