package org.caleydo.core.manager.general;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IManager;
import org.caleydo.core.manager.IMementoManager;
import org.caleydo.core.manager.ISWTGUIManager;
import org.caleydo.core.manager.IViewGLCanvasManager;
import org.caleydo.core.manager.IXmlParserManager;
import org.caleydo.core.manager.command.CommandManager;
import org.caleydo.core.manager.data.IGenomeIdManager;
import org.caleydo.core.manager.data.IPathwayItemManager;
import org.caleydo.core.manager.data.IPathwayManager;
import org.caleydo.core.manager.data.ISetManager;
import org.caleydo.core.manager.data.IStorageManager;
import org.caleydo.core.manager.data.IVirtualArrayManager;
import org.caleydo.core.manager.data.genome.DynamicGenomeIdManager;
import org.caleydo.core.manager.data.pathway.PathwayItemManager;
import org.caleydo.core.manager.data.pathway.PathwayManager;
import org.caleydo.core.manager.data.set.SetManager;
import org.caleydo.core.manager.data.storage.StorageManager;
import org.caleydo.core.manager.data.virtualarray.VirtualArrayManager;
import org.caleydo.core.manager.event.EventPublisher;
import org.caleydo.core.manager.gui.SWTGUIManager;
import org.caleydo.core.manager.memento.MementoManager;
import org.caleydo.core.manager.parser.XmlParserManager;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.manager.type.ManagerType;
import org.caleydo.core.manager.view.ViewGLCanvasManager;
import org.caleydo.core.util.exception.CaleydoRuntimeException;


/**
 * General manager that contains all module managers.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class GeneralManager 
implements IGeneralManager
{
	private boolean bAllManagersInitialized = false;

	private ArrayList<IManager> llAllManagerObjects;

	protected IStorageManager refStorageManager;
	
	protected IMementoManager refMementoManager;
	
	protected IVirtualArrayManager refVirtualArrayManager;
	
	protected ISetManager refSetManager;
	
	protected ICommandManager refCommandManager;

	protected ISWTGUIManager refSWTGUIManager;
	
	protected IViewGLCanvasManager refViewGLCanvasManager;
	
	protected IPathwayManager refPathwayManager;
	
	protected IPathwayItemManager refPathwayItemManager;
	
	protected IEventPublisher refEventPublisher;
	
	protected IXmlParserManager refXmlParserManager;
	
	protected IGenomeIdManager refGenomeIdManager;
	
	private Logger logger;
	
	/**
	 * Unique Id per each application over the network.
	 * Used to identify and create Id's unique for distributed applications. 
	 * 
	 * @see org.caleydo.core.manager.IGeneralManager#iUniqueId_WorkspaceOffset
	 */
	private int iNetworkApplicationIdPostfix = 0;

	/**
	 * Constructor.
	 */
	public GeneralManager()
	{
		/**
		 * The Network postfix must be unique inside the network for 
		 * distributed Caleydo applications. 
		 * For stand alone Caleydo applications this id must match the XML file.
		 */
		setNetworkPostfix(0);
		
		llAllManagerObjects = new ArrayList<IManager>();
		
		initLogger();
		initManager();
	}

	/**
	 * Must be called right after the constructor before using this class.
	 * Initializes all manager objects.
	 *
	 */
	public void initManager()
	{
		if (bAllManagersInitialized)
		{
			throw new CaleydoRuntimeException(
					"initAll() was called at least twice!");
		}
		
		bAllManagersInitialized = true;

		refStorageManager = new StorageManager(this, 4);
		refVirtualArrayManager = new VirtualArrayManager(this, 4);
		refSetManager = new SetManager(this, 4);
		refMementoManager = new MementoManager(this);
		refCommandManager = new CommandManager(this);
		refViewGLCanvasManager = new ViewGLCanvasManager(this);
		refSWTGUIManager = new SWTGUIManager(this);
		refEventPublisher = new EventPublisher(this);
		refGenomeIdManager = new DynamicGenomeIdManager(this);
		refPathwayManager = new PathwayManager(this);
//		serializationInputTest();
		refPathwayItemManager = new PathwayItemManager(this);
		refXmlParserManager = new XmlParserManager(this);
		
		/**
		 * Insert all Manager objects handling registered objects to 
		 * the LinkedList
		 */
		llAllManagerObjects.add(refSetManager);
		llAllManagerObjects.add(refVirtualArrayManager);
		llAllManagerObjects.add(refStorageManager);
		llAllManagerObjects.add(refPathwayManager);
		llAllManagerObjects.add(refPathwayItemManager);
		llAllManagerObjects.add(refGenomeIdManager);
		llAllManagerObjects.add(refEventPublisher);
		llAllManagerObjects.add(refViewGLCanvasManager);
		llAllManagerObjects.add(refSWTGUIManager);
		llAllManagerObjects.add(refCommandManager);
		llAllManagerObjects.add(refMementoManager);
	}

	/**
	 * Initialize the Java internal logger
	 */
	private void initLogger() {
		
		logger = Logger.getLogger("Caleydo Log");
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getLogger()
	 */
	public final Logger getLogger() {
		return logger;
	}

	
	/* (non-Javadoc)
	 * @see org.caleydo.core.data.manager.GeneralManager#hasItem(int)
	 */
	public boolean hasItem(final int iItemId)
	{
		Iterator <IManager> iter = llAllManagerObjects.iterator();
		
		while ( iter.hasNext() ) 
		{
			if ( iter.next().hasItem(iItemId) ) 
				return true;
		} // while ( iter.hasNext() ) 

		return false;
	}

	/**
	 * @see org.caleydo.core.manager.IGeneralManager#hasItem(int)
	 * 
	 * @param iItemId unique Id used for lookup
	 * @return Object bound to Id or null, if id was not found.
	 */
	public Object getItem(final int iItemId)
	{
		Iterator <IManager> iter = llAllManagerObjects.iterator();
		
		while ( iter.hasNext() ) 
		{
			IManager buffer = iter.next();
			
			if ( buffer.hasItem(iItemId) ) 
			{
				return buffer.getItem(iItemId);
			}
			
		} // while ( iter.hasNext() ) 

		return null;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.manager.singelton.SingeltonManager#createNewId(org.caleydo.core.data.manager.BaseManagerType)
	 */
	public final int createId(final ManagerObjectType type)
	{
		assert type != null : "registerItem called with type == null!";
		
		IManager buffer = this.getManagerByObjectType( type );		
		assert buffer != null : "createNewId type does not address manager!";
		
		return buffer.createId(type);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getManagerByObjectType(org.caleydo.core.manager.type.ManagerObjectType)
	 */
	public IManager getManagerByObjectType(final ManagerObjectType managerType) 
	{
		return getManagerByType(managerType.getGroupType());
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getManagerByType(org.caleydo.core.manager.type.ManagerType)
	 */
	public IManager getManagerByType(final ManagerType managerType)
	{
		assert managerType != null : "type is null!";

		switch (managerType)
		{
		case MEMENTO:
			return refMementoManager;
		case DATA_VIRTUAL_ARRAY:
			return refVirtualArrayManager;
		case DATA_SET:
			return refSetManager;
		case DATA_STORAGE:
			return refStorageManager;
		case VIEW:
			return refViewGLCanvasManager;
		case COMMAND:
			return refCommandManager;
		case VIEW_GUI_SWT:
			return refSWTGUIManager;
		case EVENT_PUBLISHER:
			return refEventPublisher;
		case DATA_GENOME_ID:
			return refGenomeIdManager;
			
		default:
			throw new CaleydoRuntimeException(
					"Error in OneForAllManager.getManagerByObjectType() unsupported type "
							+ managerType.name());
		} // end switch ( type.getGroupType() )
	}
	
	public void destroyOnExit() {
		
//		generalManager.logMsg("OneForAllManager.destroyOnExit()", LoggerType.STATUS );
		
		this.refViewGLCanvasManager.destroyOnExit();
		
		Iterator <IManager> iter = llAllManagerObjects.iterator();
		
		while ( iter.hasNext() ) 
		{
			IManager buffer = iter.next();
			
			if ( buffer != null ) {
				buffer.destroyOnExit();
			}
			
		} // while ( iter.hasNext() ) 
		
		logger.log(Level.INFO, "OneForAllManager.destroyOnExit()  ...[DONE]");
		
//		serializationOutputTest();
	}

//	public boolean setCreateNewId(ManagerType setNewBaseType, int iCurrentId) {
//
//		IManager refSecialManager = getManagerByType( setNewBaseType );
//		
//		if ( ! refSecialManager.setCreateNewId(setNewBaseType, iCurrentId) ) {
//			generalManager.logMsg("setCreateNewId failed!", LoggerType.MINOR_ERROR );
//			return false;
//		}
//		
//		return true;
//	}

	public int getNetworkPostfix() {
		return iNetworkApplicationIdPostfix;
	}
	
	public void setNetworkPostfix( int iSetNetworkPrefix ) {
		if (( iSetNetworkPrefix < IGeneralManager.iUniqueId_WorkspaceOffset) && 
				( iSetNetworkPrefix >= 0)) { 
			iNetworkApplicationIdPostfix = iSetNetworkPrefix;
			return;
		}
		throw new RuntimeException("SIngeltonManager.setNetworkPostfix() exceeded range [0.." +
				IGeneralManager.iUniqueId_WorkspaceOffset + "] ");
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getMementoManager()
	 */
	public IMementoManager getMementoManager() {
		return refMementoManager;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getStorageManager()
	 */
	public IStorageManager getStorageManager() {
		return refStorageManager;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getVirtualArrayManager()
	 */
	public IVirtualArrayManager getVirtualArrayManager() {
		return refVirtualArrayManager;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getSetManager()
	 */
	public ISetManager getSetManager() {
		return refSetManager;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getViewGLCanvasManager()
	 */
	public IViewGLCanvasManager getViewGLCanvasManager() {
		return refViewGLCanvasManager;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getPathwayManager()
	 */
	public IPathwayManager getPathwayManager() {
		
		return refPathwayManager;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getPathwayItemManager()
	 */
	public IPathwayItemManager getPathwayItemManager() {
		
		return refPathwayItemManager;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getSWTGUIManager()
	 */
	public ISWTGUIManager getSWTGUIManager() {
		return refSWTGUIManager;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getEventPublisher()
	 */
	public IEventPublisher getEventPublisher() {
		return refEventPublisher;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getXmlParserManager()
	 */
	public IXmlParserManager getXmlParserManager() {
		return this.refXmlParserManager;
	}
		
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getGenomeIdManager()
	 */
	public IGenomeIdManager getGenomeIdManager() {
		return this.refGenomeIdManager;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getCommandManager()
	 */
	public ICommandManager getCommandManager() {
		return refCommandManager;
	}
	
//	public void serializationOutputTest() {
//		
//		try
//		{
//			ObjectOutputStream out = new ObjectOutputStream(
//					new FileOutputStream("data/serialize_test.out"));
//			
//			out.writeObject(refPathwayManager);
//			out.close();
//			
//		} catch (FileNotFoundException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//	public void serializationInputTest() {
//		
//		try
//		{
//			ObjectInputStream in = new ObjectInputStream(
//					new FileInputStream("data/serialize_test.out"));
//			
//			refPathwayManager = (PathwayManager) in.readObject();
//			in.close();
//			
//		} catch (FileNotFoundException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ClassNotFoundException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}

