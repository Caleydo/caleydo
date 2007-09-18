package org.geneview.core.manager.singleton;

import org.geneview.core.manager.ICommandManager;
import org.geneview.core.manager.IGeneralManager;
//import org.geneview.core.manager.ViewCanvasManager;
import org.geneview.core.manager.type.ManagerObjectType;
import org.geneview.core.manager.type.ManagerType;
import org.geneview.core.data.xml.IMementoCallbackXML;

public interface IGeneralManagerSingleton 
extends IGeneralManager, IMementoCallbackXML {


//	/**
//	 * Creates a new unique Id with the type, that was set previouse.
//	 * This method returns and creates a unique Id.
//	 * 
//	 * @param define type of object ot be created.
//	 * 
//	 * @return int new unique Id
//	 * 
//	 * @see org.geneview.core.manager.singelton.OneForAllManager#setNewType(ManagerObjectType)
//	 * @see org.geneview.core.manager.singelton.OneForAllManager#createNewId(ManagerObjectType)
//	 */
//	public abstract int createNewId(ManagerObjectType setNewBaseType);

	/**
	 * Create a new item.
	 * 
	 * @param createNewType type of item. only used locally
	 * @param sNewTypeDetails optional details used to create new object
	 * @return new object
	 */
	public abstract Object createNewItem(final ManagerObjectType createNewType,
			final String sNewTypeDetails);

//	/**
//	 * Get the reference to the mangerer handling View's and Canvas.
//	 * 
//	 * @return manger for IViewCanvas objects
//	 */
//	public IViewCanvasManager getViewCanvasManager();
	
	/**
	 * Get the current ICommandManager.
	 * 
	 * @return current ICommandManager
	 */
	public ICommandManager getCommandManager();
	
	/**
	 * Get the reference to the managers using the ManagerType.
	 * Note: Instead of writing one get-method for all Managers this method
	 * handles all differnt types of managers.
	 * 
	 * @param managerType define type of manger that is requested
	 * @return manager for a certain type.
	 */
	public IGeneralManager getManagerByType(ManagerType managerType);
	
//	/**
//	 * Get the reference to the managers using the ManagerObjectType.
//	 * Note: Instead of writing one get-method for all Managers this method
//	 * handles all differnt types of managers.
//	 * 
//	 * @param managerType define type of manger that is requested
//	 * @return manager for a certain type.
//	 */
//	public IGeneralManager getManagerByBaseType(CommandQueueSaxType managerType);
	
	
	/**
	 * Enable SWT
	 * 
	 * @param enableSWT
	 */
	public void  setStateSWT( boolean enableSWT );
	
	
	/**
	 * Initialize all data structures.
	 *
	 */
	public void initAll();
	
	
	/**
	 * Cleanup all data structures, close all windows, stop all threads.
	 */
	public void destroyOnExit();
	
}