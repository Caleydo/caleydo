package cerberus.manager.singelton;

import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
//import cerberus.manager.ViewCanvasManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.manager.type.ManagerType;
import cerberus.data.xml.IMementoCallbackXML;

public interface IGeneralManagerSingelton 
extends IGeneralManager, IMementoCallbackXML {


//	/**
//	 * Creates a new unique Id with the type, that was set previouse.
//	 * This methode returns and creates a unique Id.
//	 * 
//	 * @param define type of object ot be created.
//	 * 
//	 * @return int new unique Id
//	 * 
//	 * @see cerberus.manager.singelton.OneForAllManager#setNewType(ManagerObjectType)
//	 * @see cerberus.manager.singelton.OneForAllManager#createNewId(ManagerObjectType)
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
	 * Note: Instead of writing one get-methode for all Managers this methode
	 * handles all differnt types of managers.
	 * 
	 * @param managerType define type of manger that is requested
	 * @return manager for a certain type.
	 */
	public IGeneralManager getManagerByType(ManagerType managerType);
	
//	/**
//	 * Get the reference to the managers using the ManagerObjectType.
//	 * Note: Instead of writing one get-methode for all Managers this methode
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