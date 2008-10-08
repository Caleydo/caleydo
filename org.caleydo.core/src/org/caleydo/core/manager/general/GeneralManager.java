package org.caleydo.core.manager.general;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.caleydo.core.bridge.gui.IGUIBridge;
import org.caleydo.core.bridge.gui.standalone.SWTStandaloneBridge;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IIDMappingManager;
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
import org.caleydo.core.manager.mapping.IDMappingManager;
import org.caleydo.core.manager.memento.MementoManager;
import org.caleydo.core.manager.parser.XmlParserManager;
import org.caleydo.core.manager.specialized.genome.IPathwayItemManager;
import org.caleydo.core.manager.specialized.genome.IPathwayManager;
import org.caleydo.core.manager.specialized.genome.pathway.PathwayItemManager;
import org.caleydo.core.manager.specialized.genome.pathway.PathwayManager;
import org.caleydo.core.manager.specialized.glyph.GlyphManager;
import org.caleydo.core.manager.specialized.glyph.IGlyphManager;
import org.caleydo.core.manager.view.ViewGLCanvasManager;
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
	 * Preferences store enables storing and restoring of application specific
	 * preference data.
	 */
	private PreferenceStore preferenceStore;

	/**
	 * Determines whether Caleydo runs as standalone test GUI or in RCP mode.
	 */
	private boolean bIsStandalone = true;

	private boolean bAllManagersInitialized = false;

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
	private IIDMappingManager genomeIdManager;
	private IGlyphManager glyphManager;
	private IDManager IDManager;

	private Logger logger;

	private IGUIBridge guiBridge;

	@Override
	public void init(boolean bIsStandalone, IGUIBridge externalGUIBridge)
	{
		this.init(bIsStandalone);

		this.guiBridge = externalGUIBridge;
	}

	@Override
	public void init(boolean bIsStandalone)
	{
		this.bIsStandalone = bIsStandalone;

		if (bAllManagersInitialized)
		{
			throw new IllegalStateException(
					"Tried to initialize managers multiple times. Abort.");
		}

		bAllManagersInitialized = true;

		storageManager = new StorageManager();
		// virtualArrayManager = new VirtualArrayManager(this, 4);
		setManager = new SetManager();
		// connectedElementRepManager = new SelectionManager();
		mementoManager = new MementoManager();
		commandManager = new CommandManager();
		viewGLCanvasManager = new ViewGLCanvasManager();
		sWTGUIManager = new SWTGUIManager();
		eventPublisher = new EventPublisher();
		genomeIdManager = new IDMappingManager();
		pathwayManager = new PathwayManager();
		// serializationInputTest();
		pathwayItemManager = new PathwayItemManager();
		xmlParserManager = new XmlParserManager();
		glyphManager = new GlyphManager();
		IDManager = new IDManager();

		xmlParserManager.initHandlers();

		initLogger();
		initPreferences();

		// Init Standalone GUI Bridge if in standalone mode
		if (bIsStandalone)
		{
			guiBridge = new SWTStandaloneBridge();
		}
	}

	/**
	 * Returns the general method as a singleton object. When first called the
	 * general manager is created (lazy).
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
		preferenceStore = new PreferenceStore(IGeneralManager.CALEYDO_HOME_PATH
				+ PREFERENCE_FILE_NAME);

		try
		{
			preferenceStore.load();
		}
		catch (IOException e)
		{
			logger.log(Level.INFO, "Create new preference store at "
					+ IGeneralManager.CALEYDO_HOME_PATH + PREFERENCE_FILE_NAME);

			// Create .caleydo folder
			if (!(new File(IGeneralManager.CALEYDO_HOME_PATH).exists()))
			{
				if (!(new File(IGeneralManager.CALEYDO_HOME_PATH).mkdir()))
					throw new IllegalStateException(
							"Unable to create home folder .caleydo. Check user permissions!");
			}

			try
			{
				preferenceStore.setValue("firstStart", true);
				preferenceStore.save();
			}
			catch (IOException e1)
			{
				throw new IllegalStateException("Unable to save preference file.");
			}
		}

		// Create log folder in .caleydo
		new File(IGeneralManager.CALEYDO_HOME_PATH + "logs").mkdirs();
	}

	/**
	 * Initialize the Java internal logger
	 */
	private void initLogger()
	{
		logger = Logger.getLogger("Caleydo Log");
	}

	@Override
	public final Logger getLogger()
	{
		return logger;
	}

	@Override
	public IMementoManager getMementoManager()
	{
		return mementoManager;
	}

	@Override
	public IStorageManager getStorageManager()
	{
		return storageManager;
	}

	// public IVirtualArrayManager getVirtualArrayManager() {
	// return virtualArrayManager;
	// }
	// public ISelectionManager getSelectionManager()
	// {
	// return connectedElementRepManager;
	// }

	@Override
	public ISetManager getSetManager()
	{
		return setManager;
	}

	@Override
	public IViewGLCanvasManager getViewGLCanvasManager()
	{
		return viewGLCanvasManager;
	}

	@Override
	public IPathwayManager getPathwayManager()
	{
		return pathwayManager;
	}

	@Override
	public IPathwayItemManager getPathwayItemManager()
	{
		return pathwayItemManager;
	}

	@Override
	public ISWTGUIManager getSWTGUIManager()
	{
		return sWTGUIManager;
	}

	@Override
	public IEventPublisher getEventPublisher()
	{
		return eventPublisher;
	}

	@Override
	public IXmlParserManager getXmlParserManager()
	{
		return this.xmlParserManager;
	}

	@Override
	public IIDMappingManager getIDMappingManager()
	{
		return this.genomeIdManager;
	}

	@Override
	public ICommandManager getCommandManager()
	{
		return commandManager;
	}

	@Override
	public IGlyphManager getGlyphManager()
	{
		return glyphManager;
	}

	@Override
	public PreferenceStore getPreferenceStore()
	{
		return preferenceStore;
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

	@Override
	public IGUIBridge getGUIBridge()
	{
		return guiBridge;
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
