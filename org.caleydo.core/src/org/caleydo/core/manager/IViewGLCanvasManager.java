package org.caleydo.core.manager;

import java.util.Collection;
import org.caleydo.core.command.ECommandType;
import org.caleydo.core.data.view.camera.IViewFrustum;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.manager.view.ConnectedElementRepresentationManager;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
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
	public AGLEventListener createGLEventListener(ECommandType type, final int iGLCanvasID,
			String sLabel, IViewFrustum viewFrustum);

	public IView createGLView(final EManagedObjectType type, final int iParentContainerID,
			final String sLabel);

	public Collection<GLCaleydoCanvas> getAllGLCanvasUsers();

	public Collection<AGLEventListener> getAllGLEventListeners();

	public boolean registerGLCanvas(final GLCaleydoCanvas glCanvas);

	public boolean unregisterGLCanvas(final int iGLCanvasId);

	public void registerGLEventListenerByGLCanvasID(final int iGLCanvasID,
			final AGLEventListener gLEventListener);

	public void unregisterGLEventListener(final int iGLEventListenerID);

	public DataEntitySearcherViewRep getDataEntitySearcher();

	/**
	 * Get the PickingManager which is responsible for system wide picking
	 * 
	 * @return the PickingManager
	 */
	public PickingManager getPickingManager();

	public ConnectedElementRepresentationManager getConnectedElementRepresentationManager();

	public GLInfoAreaManager getInfoAreaManager();

	public void startAnimator();
	
	/**
	 * Removes all views, canvas and GL event listeners
	 */
	public void cleanup();

	public GLCaleydoCanvas getCanvas(int iItemID);

	public AGLEventListener getGLEventListener(int iItemID);
}
