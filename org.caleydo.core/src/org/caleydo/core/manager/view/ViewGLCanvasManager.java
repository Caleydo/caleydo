package org.caleydo.core.manager.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import javax.media.opengl.GLEventListener;
import javax.swing.JFrame;
import org.caleydo.core.command.CommandType;
import org.caleydo.core.data.view.camera.IViewFrustum;
import org.caleydo.core.data.view.rep.renderstyle.layout.ARemoteViewLayoutRenderStyle;
import org.caleydo.core.manager.AManager;
import org.caleydo.core.manager.IViewGLCanvasManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.ViewType;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.glyph.GLCanvasGlyph;
import org.caleydo.core.view.opengl.canvas.heatmap.GLCanvasHeatMap;
import org.caleydo.core.view.opengl.canvas.parcoords.GLCanvasParCoords3D;
import org.caleydo.core.view.opengl.canvas.pathway.GLCanvasPathway3D;
import org.caleydo.core.view.opengl.canvas.remote.GLCanvasRemoteRendering3D;
import org.caleydo.core.view.opengl.canvas.remote.glyph.GLCanvasRemoteGlyph;
import org.caleydo.core.view.opengl.canvas.wii.GLCanvasWiiTest;
import org.caleydo.core.view.opengl.util.infoarea.GLInfoAreaManager;
import org.caleydo.core.view.swt.browser.HTMLBrowserViewRep;
import org.caleydo.core.view.swt.data.search.DataEntitySearcherViewRep;
import org.caleydo.core.view.swt.glyph.GlyphMappingConfigurationViewRep;
import org.caleydo.core.view.swt.image.ImageViewRep;
import org.caleydo.core.view.swt.jogl.SwtJoglGLCanvasViewRep;
import org.caleydo.core.view.swt.mixer.MixerViewRep;
import org.caleydo.core.view.swt.undoredo.UndoRedoViewRep;
import com.sun.opengl.util.Animator;
import com.sun.opengl.util.FPSAnimator;

/**
 * Manage all canvas, view, ViewRep's and GLCanvas objects.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */
public class ViewGLCanvasManager
	extends AManager<IView>
	implements IViewGLCanvasManager
{
	protected HashMap<Integer, GLCaleydoCanvas> hashGLCanvasID2GLCanvas;

	protected HashMap<Integer, ArrayList<AGLEventListener>> hashGLCanvasID2GLEventListeners;

	protected HashMap<Integer, AGLEventListener> hashGLEventListenerID2GLEventListener;

	/**
	 * List of data explorer view reps (needed for mediator registration when
	 * data is created).
	 */
	protected ArrayList<IView> arDataExplorerViewRep;

	/**
	 * List of HTML browser view reps
	 */
	protected ArrayList<IView> arHTMLBrowserViewRep;

	protected ArrayList<JFrame> arWorkspaceJFrame;

	private Animator fpsAnimator;

	private DataEntitySearcherViewRep dataEntitySearcher;

	private PickingManager pickingManager;

	private ConnectedElementRepresentationManager selectionManager;

	private GLInfoAreaManager infoAreaManager;

	/**
	 * Constructor.
	 * 
	 */
	public ViewGLCanvasManager()
	{
		pickingManager = new PickingManager();
		selectionManager = new ConnectedElementRepresentationManager();
		infoAreaManager = new GLInfoAreaManager();

		hashGLCanvasID2GLCanvas = new HashMap<Integer, GLCaleydoCanvas>();
		hashGLCanvasID2GLEventListeners = new HashMap<Integer, ArrayList<AGLEventListener>>();
		hashGLEventListenerID2GLEventListener = new HashMap<Integer, AGLEventListener>();

		arDataExplorerViewRep = new ArrayList<IView>();
		arHTMLBrowserViewRep = new ArrayList<IView>();
		arWorkspaceJFrame = new ArrayList<JFrame>();
	}

	public boolean hasItem(int iItemId)
	{

		if (hashItems.containsKey(iItemId))
			return true;

		if (hashGLCanvasID2GLCanvas.containsKey(iItemId))
			return true;

		if (hashGLEventListenerID2GLEventListener.containsKey(iItemId))
			return true;

		return false;
	}
	
	public GLCaleydoCanvas getCanvas(int iItemID)
	{
		return hashGLCanvasID2GLCanvas.get(iItemID);
	}
	
	public AGLEventListener getEventListener(int iItemID)
	{
		return hashGLEventListenerID2GLEventListener.get(iItemID);
	}	

	public void registerItem(IView view)
	{
		super.registerItem(view);
	
		switch (view.getViewType())
		{
			case SWT_DATA_EXPLORER:
			case SWT_HTML_BROWSER:
				this.addViewRep(view);
				break;

			// default: // do nothing
		} 
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IViewManager#createView(org.caleydo.core.manager.id.EManagedObjectTypes, int, int, java.lang.String)
	 */
	public IView createView(final EManagedObjectType type, 
			final int iParentContainerID, final String sLabel)
	{
		IView view = null;
		
		switch (type)
		{
			case VIEW:
				break;
			case VIEW_SWT_PATHWAY:
				// return new Pathway2DViewRep(generalManager, iViewID,
				// iParentContainerID, sLabel);
				break;
			case VIEW_SWT_DATA_EXPLORER:
				// return new DataExplorerViewRep(generalManager, iViewID,
				// iParentContainerID, sLabel);
				break;
			case VIEW_SWT_DATA_EXCHANGER:
				// return new DataExchangerViewRep(generalManager, iViewID,
				// iParentContainerID, sLabel);
				break;
			case VIEW_SWT_DATA_SET_EDITOR:
				// return new NewSetEditorViewRep(generalManager, iViewID,
				// iParentContainerID, sLabel);
				break;
			case VIEW_SWT_MIXER:
				view = new MixerViewRep(iParentContainerID, sLabel);
				break;
			case VIEW_SWT_BROWSER:
				view = new HTMLBrowserViewRep(iParentContainerID, sLabel);
				break;
			case VIEW_SWT_IMAGE:
				view = new ImageViewRep(iParentContainerID, sLabel);
				break;
			case VIEW_SWT_UNDO_REDO:
				view = new UndoRedoViewRep(iParentContainerID, sLabel);
				break;
			case VIEW_SWT_DATA_ENTITY_SEARCHER:
				view = new DataEntitySearcherViewRep(iParentContainerID, sLabel);
				break;
			case VIEW_SWT_GLYPH_MAPPINGCONFIGURATION:
				view = new GlyphMappingConfigurationViewRep(iParentContainerID, sLabel);
				break;
			default:
				throw new CaleydoRuntimeException(
						"StorageManagerSimple.createView() failed due to unhandled type ["
								+ type.toString() + "]");
		}

		registerItem(view);
		
		return view;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IViewManager#createGLView(org.caleydo.core.manager.id.EManagedObjectType, int, int, int, java.lang.String)
	 */
	public IView createGLView(final EManagedObjectType useViewType,
			final int iParentContainerID, final String sLabel)
	{
		IView view = null;
		
		switch (useViewType)
		{
			case VIEW_GL_CANVAS:
				view = new SwtJoglGLCanvasViewRep(iParentContainerID, sLabel);
				break;

			default:
				throw new CaleydoRuntimeException(
						"Unhandled view type ["
								+ useViewType.toString() + "]");
		}
		
		registerItem(view);
		
		return view;
	}

	public ArrayList<IView> getViewRepByType(ViewType viewType)
	{

		switch (viewType)
		{
			case SWT_DATA_EXPLORER:
				return arDataExplorerViewRep;

			case SWT_HTML_BROWSER:
				return arHTMLBrowserViewRep;

			default:
				assert false : "unsupportet view Type";
				return null;
		} // switch (viewType)

	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IViewGLCanvasManager#createGLCanvas(org.caleydo.core.command.CommandType, int, int, java.lang.String, org.caleydo.core.data.view.camera.IViewFrustum)
	 */
	public AGLEventListener createGLEventListener(CommandType type,
			final int iGLCanvasID, final String sLabel,
			final IViewFrustum viewFrustum)
	{
		GeneralManager.get().getLogger().log(
				Level.INFO,
				"Creating GL canvas view from type: [" + type + "] and label: [" + sLabel + "]");

		AGLEventListener glEventListener = null;
		
		switch (type)
		{
			case CREATE_GL_HEAT_MAP_3D:
				glEventListener = new GLCanvasHeatMap(iGLCanvasID, sLabel,
						viewFrustum);
				break;

			case CREATE_GL_PATHWAY_3D:
				glEventListener = new GLCanvasPathway3D(iGLCanvasID,
						sLabel, viewFrustum);
				break;
				
			case CREATE_GL_PARALLEL_COORDINATES_3D:
				glEventListener = new GLCanvasParCoords3D(iGLCanvasID,
						sLabel, viewFrustum);
				break;
				
			case CREATE_GL_GLYPH:
				glEventListener = new GLCanvasGlyph(iGLCanvasID, sLabel,
						viewFrustum);
				break;
				
			case CREATE_GL_BUCKET_3D:
				glEventListener = new GLCanvasRemoteRendering3D(iGLCanvasID, 
						sLabel, viewFrustum,
						ARemoteViewLayoutRenderStyle.LayoutMode.BUCKET);
				break;
				
			case CREATE_GL_JUKEBOX_3D:
				glEventListener = new GLCanvasRemoteRendering3D(iGLCanvasID,
						sLabel, viewFrustum,
						ARemoteViewLayoutRenderStyle.LayoutMode.JUKEBOX);
				break;
				
			case CREATE_GL_WII_TEST:
				glEventListener = new GLCanvasWiiTest(iGLCanvasID, sLabel,
						viewFrustum);
				break;
				
			case CREATE_GL_REMOTE_GLYPH:
				glEventListener = new GLCanvasRemoteGlyph(iGLCanvasID,
						sLabel, viewFrustum);
				break;
				
			default:
				throw new CaleydoRuntimeException(
						"ViewJoglManager.createGLCanvasUser() failed due to unhandled type ["
								+ type.toString() + "]");
		}
		
		registerGLEventListenerByGLCanvasID(iGLCanvasID, glEventListener);
		
		return glEventListener;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IViewGLCanvasManager#registerGLCanvas(org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas)
	 */
	@Override
	public boolean registerGLCanvas(final GLCaleydoCanvas glCanvas)
	{
		int iGLCanvasID = glCanvas.getID();
		
		if (hashGLCanvasID2GLCanvas.containsKey(iGLCanvasID))
		{
			generalManager.getLogger().log(Level.WARNING, "GL Canvas with ID " 
					+iGLCanvasID + " is already registered! Do nothing.");
			
			return false;
		}

		hashGLCanvasID2GLCanvas.put(iGLCanvasID, glCanvas);

		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.manager.IViewGLCanvasManager#unregisterGLCanvas(javax
	 * .media.opengl.GLCanvas)
	 */
	public boolean unregisterGLCanvas(final int iGLCanvasId)
	{

		if (hashGLCanvasID2GLCanvas.containsKey(iGLCanvasId))
		{
			hashGLCanvasID2GLCanvas.remove(iGLCanvasId);
			hashGLCanvasID2GLEventListeners.remove(iGLCanvasId);
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IViewGLCanvasManager#registerGLEventListenerByGLCanvasID(int, javax.media.opengl.GLEventListener)
	 */
	public void registerGLEventListenerByGLCanvasID(final int iGLCanvasID,
			final AGLEventListener gLEventListener)
	{
		hashGLEventListenerID2GLEventListener.put(((AGLEventListener) gLEventListener).getID(),
				gLEventListener);

		if (iGLCanvasID == -1)
			return;

		if (!hashGLCanvasID2GLEventListeners.containsKey(iGLCanvasID))
			hashGLCanvasID2GLEventListeners.put(iGLCanvasID, new ArrayList<AGLEventListener>());

		hashGLCanvasID2GLEventListeners.get(iGLCanvasID).add(gLEventListener);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IViewGLCanvasManager#cleanup()
	 */
	public void cleanup()
	{

		hashGLCanvasID2GLCanvas.clear();
		hashGLCanvasID2GLEventListeners.clear();
		hashGLEventListenerID2GLEventListener.clear();
		hashItems.clear();
		arDataExplorerViewRep.clear();
		arHTMLBrowserViewRep.clear();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.manager.IViewGLCanvasManager#unregisterGLEventListener()
	 */
	public void unregisterGLEventListener(final int iGLEventListenerID)
	{

		GLEventListener gLEventListenerToRemove = hashGLEventListenerID2GLEventListener
				.get(iGLEventListenerID);

		GLCaleydoCanvas parentGLCanvas = ((AGLEventListener) gLEventListenerToRemove)
				.getParentGLCanvas();

		if (parentGLCanvas != null)
		{
			parentGLCanvas.removeGLEventListener(gLEventListenerToRemove);
			hashGLCanvasID2GLEventListeners.get(parentGLCanvas.getID()).remove(
					gLEventListenerToRemove);
		}

		hashGLEventListenerID2GLEventListener.remove(iGLEventListenerID);
	}

	public void addViewRep(final IView view)
	{

		try
		{
			switch (view.getViewType())
			{

				case SWT_SET_EDITOR:
				case SWT_DATA_EXCHANGER:
					System.err.println("Ignore: addViewRep(" + view.getViewType()
							+ ") type SWT_DATA_EXCHANGER!");
					return;

				case SWT_DATA_EXPLORER:
					// arDataExplorerViewRep.add( (DataExplorerViewRep) view);
					return;
				case SWT_HTML_BROWSER:
					arHTMLBrowserViewRep.add((HTMLBrowserViewRep) view);
					return;
				case SWT_DATA_ENTITY_SEARCHER:
					dataEntitySearcher = (DataEntitySearcherViewRep) view;

				default:
					assert false : "unsupported ViewType " + view.getViewType();
			} // switch ( view.getViewType() )
		}
		catch (NullPointerException npe)
		{
			System.err.println("addViewRep(IView) getViewType() returned unexpected (class)!");

			assert false : "Error,  getViewType() returned unexpected (class)";
		} // try .. catch ( NullPointerException npe)
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.manager.IViewManager#removeViewRep(org.caleydo.core.
	 * view.IView)
	 */
	public void removeViewRep(final IView view)
	{
		try
		{
			switch (view.getViewType())
			{

				case SWT_DATA_EXPLORER:
					// arDataExplorerViewRep.remove( (DataExplorerViewRep)
					// view);
					return;
				case SWT_HTML_BROWSER:
					// arDataExplorerViewRep.remove( (DataExplorerViewRep)
					// view);
					return;

				default:
					assert false : "unsupported ViewType " + view.getViewType();
			} // switch ( view.getViewType() )

		}
		catch (NullPointerException npe)
		{
			System.err
					.println("removedViewRep(IView) getViewType() returned unexpected (class)!");

			assert false : "Error,  getViewType() returned unexpected (class)";
		} // try .. catch ( NullPointerException npe)
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IViewManager#getAllGLCanvasUsers()
	 */
	public Collection<GLCaleydoCanvas> getAllGLCanvasUsers()
	{
		return hashGLCanvasID2GLCanvas.values();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.manager.IViewGLCanvasManager#getAllGLEventListeners()
	 */
	public Collection<AGLEventListener> getAllGLEventListeners()
	{
		return hashGLEventListenerID2GLEventListener.values();
	}

	public DataEntitySearcherViewRep getDataEntitySearcher()
	{

		return dataEntitySearcher;
	}

	public PickingManager getPickingManager()
	{

		return pickingManager;
	}

	public ConnectedElementRepresentationManager getConnectedElementRepresentationManager()
	{

		return selectionManager;
	}

	public GLInfoAreaManager getInfoAreaManager()
	{

		return infoAreaManager;
	}

	public void createAnimator()
	{

		fpsAnimator = new FPSAnimator(null, 60);

		Iterator<GLCaleydoCanvas> iterGLCanvas = hashGLCanvasID2GLCanvas.values().iterator();
		while (iterGLCanvas.hasNext())
		{
			fpsAnimator.add(iterGLCanvas.next());
		}

		fpsAnimator.start();
	}

	public Animator getAnimator()
	{

		return fpsAnimator;
	}
}
