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
import org.caleydo.core.manager.data.ISetManager;
import org.caleydo.core.manager.data.IStorageManager;
import org.caleydo.core.manager.data.IVirtualArrayManager;
import org.caleydo.core.manager.data.set.SetManager;
import org.caleydo.core.manager.data.storage.StorageManager;
import org.caleydo.core.manager.data.virtualarray.VirtualArrayManager;
import org.caleydo.core.manager.event.EventPublisher;
import org.caleydo.core.manager.gui.SWTGUIManager;
import org.caleydo.core.manager.memento.MementoManager;
import org.caleydo.core.manager.parser.XmlParserManager;
import org.caleydo.core.manager.specialized.genome.IGenomeIdManager;
import org.caleydo.core.manager.specialized.genome.IPathwayItemManager;
import org.caleydo.core.manager.specialized.genome.IPathwayManager;
import org.caleydo.core.manager.specialized.genome.id.GenomeIdManager;
import org.caleydo.core.manager.specialized.genome.pathway.PathwayItemManager;
import org.caleydo.core.manager.specialized.genome.pathway.PathwayManager;
import org.caleydo.core.manager.specialized.glyph.GlyphManager;
import org.caleydo.core.manager.specialized.glyph.IGlyphManager;
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

	protected IStorageManager storageManager;
	
	protected IMementoManager mementoManager;
	
	protected IVirtualArrayManager virtualArrayManager;
	
	protected ISetManager setManager;
	
	protected ICommandManager commandManager;

	protected ISWTGUIManager sWTGUIManager;
	
	protected IViewGLCanvasManager viewGLCanvasManager;
	
	protected IPathwayManager pathwayManager;
	
	protected IPathwayItemManager pathwayItemManager;
	
	protected IEventPublisher eventPublisher;
	
	protected IXmlParserManager xmlParserManager;
	
	protected IGenomeIdManager genomeIdManager;
	
	protected IGlyphManager glyphManager;

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

		storageManager = new StorageManager(this, 4);
		virtualArrayManager = new VirtualArrayManager(this, 4);
		setManager = new SetManager(this, 4);
		mementoManager = new MementoManager(this);
		commandManager = new CommandManager(this);
		viewGLCanvasManager = new ViewGLCanvasManager(this);
		sWTGUIManager = new SWTGUIManager(this);
		eventPublisher = new EventPublisher(this);
		genomeIdManager = new GenomeIdManager(this);
		pathwayManager = new PathwayManager(this);
//		serializationInputTest();
		pathwayItemManager = new PathwayItemManager(this);
		xmlParserManager = new XmlParserManager(this);
		glyphManager = new GlyphManager(this);
		
		/**
		 * Insert all Manager objects handling registered objects to 
		 * the LinkedList
		 */
		llAllManagerObjects.add(setManager);
		llAllManagerObjects.add(virtualArrayManager);
		llAllManagerObjects.add(storageManager);
		llAllManagerObjects.add(pathwayManager);
		llAllManagerObjects.add(pathwayItemManager);
		llAllManagerObjects.add(genomeIdManager);
		llAllManagerObjects.add(eventPublisher);
		llAllManagerObjects.add(viewGLCanvasManager);
		llAllManagerObjects.add(sWTGUIManager);
		llAllManagerObjects.add(commandManager);
		llAllManagerObjects.add(mementoManager);
		llAllManagerObjects.add(glyphManager);
		
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
			return mementoManager;
		case DATA_VIRTUAL_ARRAY:
			return virtualArrayManager;
		case DATA_SET:
			return setManager;
		case DATA_STORAGE:
			return storageManager;
		case VIEW:
			return viewGLCanvasManager;
		case COMMAND:
			return commandManager;
		case VIEW_GUI_SWT:
			return sWTGUIManager;
		case EVENT_PUBLISHER:
			return eventPublisher;
		case DATA_GENOME_ID:
			return genomeIdManager;
		case DATA_GLYPH:
			return glyphManager;
			
		default:
			throw new CaleydoRuntimeException(
					"Error in OneForAllManager.getManagerByObjectType() unsupported type "
							+ managerType.name());
		} // end switch ( type.getGroupType() )
	}
	
	public void destroyOnExit() {
		
//		generalManager.logMsg("OneForAllManager.destroyOnExit()", LoggerType.STATUS );
		
		this.viewGLCanvasManager.destroyOnExit();
		
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
//		IManager secialManager = getManagerByType( setNewBaseType );
//		
//		if ( ! secialManager.setCreateNewId(setNewBaseType, iCurrentId) ) {
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
		return mementoManager;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getStorageManager()
	 */
	public IStorageManager getStorageManager() {
		return storageManager;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getVirtualArrayManager()
	 */
	public IVirtualArrayManager getVirtualArrayManager() {
		return virtualArrayManager;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getSetManager()
	 */
	public ISetManager getSetManager() {
		return setManager;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getViewGLCanvasManager()
	 */
	public IViewGLCanvasManager getViewGLCanvasManager() {
		return viewGLCanvasManager;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getPathwayManager()
	 */
	public IPathwayManager getPathwayManager() {
		
		return pathwayManager;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getPathwayItemManager()
	 */
	public IPathwayItemManager getPathwayItemManager() {
		
		return pathwayItemManager;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getSWTGUIManager()
	 */
	public ISWTGUIManager getSWTGUIManager() {
		return sWTGUIManager;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getEventPublisher()
	 */
	public IEventPublisher getEventPublisher() {
		return eventPublisher;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getXmlParserManager()
	 */
	public IXmlParserManager getXmlParserManager() {
		return this.xmlParserManager;
	}
		
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getGenomeIdManager()
	 */
	public IGenomeIdManager getGenomeIdManager() {
		return this.genomeIdManager;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getCommandManager()
	 */
	public ICommandManager getCommandManager() {
		return commandManager;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getCommandManager()
	 */
	public IGlyphManager getGlyphManager() {
		return glyphManager;
	}
	
	
	
//	public void serializationOutputTest() {
//		
//		try
//		{
//			ObjectOutputStream out = new ObjectOutputStream(
//					new FileOutputStream("data/serialize_test.out"));
//			
//			out.writeObject(pathwayManager);
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
//			pathwayManager = (PathwayManager) in.readObject();
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

