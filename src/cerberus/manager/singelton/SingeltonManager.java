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
import cerberus.manager.LoggerManager;
import cerberus.manager.MementoManager;
import cerberus.manager.MenuManager;
import cerberus.manager.SelectionManager;
import cerberus.manager.SetManager;
import cerberus.manager.StorageManager;
import cerberus.manager.ViewCanvasManager;
//import prometheus.net.dwt.swing.mdi.DDesktopPane;

/**
 * Global object contining and handling several managers.
 * 
 * Desing Pattern "Singelton"
 * 
 * @author Michael Kalkusch
 *
 */
public class SingeltonManager {

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
	
	public MementoManager getMementoManager() {
		return refMementoManager;
	}
	
	public StorageManager getStorageManager() {
		return refStorageManager;
	}
		
	public SelectionManager getSelectionManager() {
		return refSelectionManager;
	}
	
	public void setMenuManager( MenuManager setMenuManager ) {
		this.refMenuManager = setMenuManager;
	}
	
	public MenuManager getMenuManager() {
		return this.refMenuManager;
	}
	
	public SetManager getSetManager() {
		return refSetManager;
	}
	
	public ViewCanvasManager getViewCanvasManager() {
		return refViewCanvasManager;
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
		assert setViewCanvasManager!=null: "ViewCanvasManager was null";
		
		refViewCanvasManager = setViewCanvasManager;
	}
	
	public CommandManager getCommandManager() {
		return refCommandManager;
	}
	
	public void setCommandManager( CommandManager setCommandManager ) {
		refCommandManager = setCommandManager;
	}
	
	public DComponentManager getDComponentManager() {
		return refDComponentManager;
	}
	
	public void setDComponentManager( DComponentManager setDComponentManager ) {
		assert setDComponentManager!=null: "DComponentManager was null";
		
		refDComponentManager = setDComponentManager;
		
	}
	
	public void setLoggerManager( LoggerManager refLoggerManager ) {
		this.refLoggerManager = refLoggerManager;
	}
	
	public LoggerManager getLoggerManager() {
		return this.refLoggerManager;
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
	
	/**
	 * Identifies each application in the network with a unique Id form [1..99]
	 * issued by the network server.
	 * 
	 * @return unique networkHostId of this host.
	 */
	public int getNetworkPostfix() {
		return iNetworkApplicationIdPostfix;
	}
	
	public void setNetworkPostfix( int iSetNetworkPrefix ) {
		if (( iSetNetworkPrefix < 100) && ( iSetNetworkPrefix > 0)) { 
			iNetworkApplicationIdPostfix = iSetNetworkPrefix;
		}
		throw new RuntimeException("SIngeltonManager.setNetworkPostfix() exceeded range [0..99] ");
	}
	


}
