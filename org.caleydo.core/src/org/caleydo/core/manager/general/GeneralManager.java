package org.caleydo.core.manager.general;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IMementoManager;
import org.caleydo.core.manager.ISWTGUIManager;
import org.caleydo.core.manager.IViewGLCanvasManager;
import org.caleydo.core.manager.IXmlParserManager;
import org.caleydo.core.manager.command.CommandManager;
import org.caleydo.core.manager.data.ISetManager;
import org.caleydo.core.manager.data.IStorageManager;
import org.caleydo.core.manager.data.set.SetManager;
import org.caleydo.core.manager.data.storage.StorageManager;
import org.caleydo.core.manager.event.EventPublisher;
import org.caleydo.core.manager.gui.SWTGUIManager;
import org.caleydo.core.manager.id.IDManager;
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
import org.caleydo.core.manager.view.ViewGLCanvasManager;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;
import org.eclipse.jface.preference.PreferenceStore;

/**
 * General manager that contains all module managers.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class GeneralManager
	implements IGeneralManager
{	
	/**
	 * General manager as a singleton
	 */
	private static IGeneralManager generalManager;
	
	/**
	 * Location where Caleydo stores preferences and caching data.
	 */
	private String sCaleydoHomePath;
	
	/**
	 * Preferences store enables storing and restoring of application specific preference data.
	 */
	private PreferenceStore preferenceStore;
	
	/**
	 * Determines whether Caleydo runs as standalone test GUI or in RCP mode.
	 */
	private boolean bIsStandalone = true;
	
	private boolean bAllManagersInitialized = false;

//	private ArrayList<IManager> alManagers;

	private IStorageManager storageManager;

	private IMementoManager mementoManager;

	// protected IVirtualArrayManager virtualArrayManager;

	private ISetManager setManager;

	private ICommandManager commandManager;

	private ISWTGUIManager sWTGUIManager;

	private IViewGLCanvasManager viewGLCanvasManager;

	private IPathwayManager pathwayManager;

	private IPathwayItemManager pathwayItemManager;

	private IEventPublisher eventPublisher;

	private IXmlParserManager xmlParserManager;

	private IGenomeIdManager genomeIdManager;

	private IGlyphManager glyphManager;
	
	private IDManager IDManager;

	private Logger logger;

	/**
	 * Constructor.
	 */
	private GeneralManager()
	{
		// Retrieve platform independent home directory
		sCaleydoHomePath = System.getProperty(USER_HOME);
		sCaleydoHomePath +=  CALEYDO_HOME;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#initManager()
	 */
	public void init(boolean bIsStandalone)
	{
		this.bIsStandalone = bIsStandalone;
		
		if (bAllManagersInitialized)
		{
			throw new CaleydoRuntimeException("Tried to initialize managers multiple times. Abort.");
		}

		bAllManagersInitialized = true;

		storageManager = new StorageManager();
		// virtualArrayManager = new VirtualArrayManager(this, 4);
		setManager = new SetManager();
//		connectedElementRepManager = new SelectionManager();
		mementoManager = new MementoManager();
		commandManager = new CommandManager();
		viewGLCanvasManager = new ViewGLCanvasManager();
		sWTGUIManager = new SWTGUIManager();
		eventPublisher = new EventPublisher();
		genomeIdManager = new GenomeIdManager();
		pathwayManager = new PathwayManager();
		// serializationInputTest();
		pathwayItemManager = new PathwayItemManager();
		xmlParserManager = new XmlParserManager();
		glyphManager = new GlyphManager();
		IDManager = new IDManager();
		
		initLogger();
		initPreferences();
	}
	
	/**
	 * Returns the general method as a singleton object.
	 * When first called the general manager is created (lazy).
	 */
	public static IGeneralManager get()
	{
		if (generalManager == null)
		{
			generalManager = new GeneralManager();
		}
		return generalManager;
	}


	private void initPreferences() 
	{				
		preferenceStore = new PreferenceStore(sCaleydoHomePath + PREFERENCE_FILE_NAME);
		
		try
		{
			preferenceStore.load();
		}
		catch (IOException e)
		{
			logger.log(Level.INFO, "Create new preference store at "
					+sCaleydoHomePath + PREFERENCE_FILE_NAME);
			
			// Create .caleydo folder
			if (!(new File(sCaleydoHomePath).exists()))
			{
				if(!(new File(sCaleydoHomePath).mkdir()))
					throw new CaleydoRuntimeException("Unable to create home folder .caleydo. Check user permissions!", 
							CaleydoRuntimeExceptionType.DATAHANDLING);
			}
				
			try
			{				
				preferenceStore.setValue("firstStart", true);	
				preferenceStore.save();
			}
			catch (IOException e1)
			{
				throw new CaleydoRuntimeException("Unable to save preference file.", 
						CaleydoRuntimeExceptionType.DATAHANDLING);
			}
		}
		
		// Create log folder in .caleydo
		new File(sCaleydoHomePath + "logs").mkdirs();
	}
	
	/**
	 * Initialize the Java internal logger
	 */
	private void initLogger()
	{
		logger = Logger.getLogger("Caleydo Log");
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getLogger()
	 */
	public final Logger getLogger()
	{
		return logger;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getMementoManager()
	 */
	public IMementoManager getMementoManager()
	{
		return mementoManager;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getStorageManager()
	 */
	public IStorageManager getStorageManager()
	{
		return storageManager;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getVirtualArrayManager()
	 */
	// public IVirtualArrayManager getVirtualArrayManager() {
	// return virtualArrayManager;
	// }
//		public ISelectionManager getSelectionManager()
//		{
//			return connectedElementRepManager;
//		}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getSetManager()
	 */
	public ISetManager getSetManager()
	{
		return setManager;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getViewGLCanvasManager()
	 */
	public IViewGLCanvasManager getViewGLCanvasManager()
	{
		return viewGLCanvasManager;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getPathwayManager()
	 */
	public IPathwayManager getPathwayManager()
	{
		return pathwayManager;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getPathwayItemManager()
	 */
	public IPathwayItemManager getPathwayItemManager()
	{
		return pathwayItemManager;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getSWTGUIManager()
	 */
	public ISWTGUIManager getSWTGUIManager()
	{
		return sWTGUIManager;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getEventPublisher()
	 */
	public IEventPublisher getEventPublisher()
	{
		return eventPublisher;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getXmlParserManager()
	 */
	public IXmlParserManager getXmlParserManager()
	{
		return this.xmlParserManager;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getGenomeIdManager()
	 */
	public IGenomeIdManager getGenomeIdManager()
	{
		return this.genomeIdManager;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getCommandManager()
	 */
	public ICommandManager getCommandManager()
	{
		return commandManager;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getCommandManager()
	 */
	public IGlyphManager getGlyphManager()
	{
		return glyphManager;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getPreferenceStore()
	 */
	public PreferenceStore getPreferenceStore() 
	{
		return preferenceStore;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getCaleydoHomePath()
	 */
	public String getCaleydoHomePath() 
	{
		return sCaleydoHomePath;
	}

	@Override
	public IDManager getIDManager()
	{
		return IDManager;
	}

	@Override
	public boolean isStandalone()
	{
		return bIsStandalone;
	}
	
	// public void serializationOutputTest() {
	//		
	// try
	// {
	// ObjectOutputStream out = new ObjectOutputStream(
	// new FileOutputStream("data/serialize_test.out"));
	//			
	// out.writeObject(pathwayManager);
	// out.close();
	//			
	// } catch (FileNotFoundException e)
	// {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IOException e)
	// {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//	
	// public void serializationInputTest() {
	//		
	// try
	// {
	// ObjectInputStream in = new ObjectInputStream(
	// new FileInputStream("data/serialize_test.out"));
	//			
	// pathwayManager = (PathwayManager) in.readObject();
	// in.close();
	//			
	// } catch (FileNotFoundException e)
	// {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IOException e)
	// {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (ClassNotFoundException e)
	// {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
}
