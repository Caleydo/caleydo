package org.caleydo.core.manager;

import java.util.Collection;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import org.caleydo.core.command.CommandType;
import org.caleydo.core.data.view.camera.IViewFrustum;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.manager.view.SelectionManager;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.opengl.util.infoarea.GLInfoAreaManager;
import org.caleydo.core.view.swt.data.search.DataEntitySearcherViewRep;
import com.sun.opengl.util.Animator;

/**
 * Make Jogl GLCanvas addressable by iD and provide ground for XML bootstrapping
 * of GLCanvas.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public interface IViewGLCanvasManager
	extends IViewManager, IManager<IView>
{

	public GLEventListener createGLCanvas(CommandType useViewType,
			final int iUniqueId, final int iGLCanvasID, String sLabel, IViewFrustum viewFrustum);

	public Collection<GLCanvas> getAllGLCanvasUsers();

	public Collection<GLEventListener> getAllGLEventListeners();

	public boolean registerGLCanvas(final GLCanvas canvas, final int iGLCanvasId);

	public boolean unregisterGLCanvas(final int iGLCanvasId);

	public void registerGLEventListenerByGLCanvasID(final int iGLCanvasID,
			final GLEventListener gLEventListener);

	public void unregisterGLEventListener(final int iGLEventListenerID);

	public DataEntitySearcherViewRep getDataEntitySearcher();

	/**
	 * Get the PickingManager which is responsible for system wide picking
	 * 
	 * @return the PickingManager
	 */
	public PickingManager getPickingManager();

	public SelectionManager getSelectionManager();

	public GLInfoAreaManager getInfoAreaManager();

	public void createAnimator();

	public Animator getAnimator();

	/**
	 * Removes all views, canvas and GL event listeners
	 */
	public void cleanup();
		
	public GLCanvas getCanvas(int iItemID);

	public GLEventListener getEventListener(int iItemID);
}
