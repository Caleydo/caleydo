package org.caleydo.core.manager;

import java.util.Collection;

import javax.media.opengl.GLCanvas;

import org.caleydo.core.manager.execution.DisplayLoopExecution;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.manager.view.ConnectedElementRepresentationManager;
import org.caleydo.core.manager.view.creator.IViewCreator;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.eclipse.swt.widgets.Composite;

/**
 * Make SWT Views and JOGL GLCanvas addressable by ID and provide ground for XML bootstrapping of GLCanvas.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public interface IViewManager
	extends IManager<IView> {

	public void init();

	public IView createView(String viewType, int parentContainerId, String sLabel);

	public AGLView createGLView(String viewID, GLCaleydoCanvas glCanvas, String label,
		IViewFrustum viewFrustum);

	public IView createGLView(final EManagedObjectType type, final int iParentContainerID, final String sLabel);

	public Collection<GLCaleydoCanvas> getAllGLCanvas();

	public Collection<AGLView> getAllGLViews();

	public boolean registerGLCanvas(final GLCaleydoCanvas glCanvas);

	public boolean unregisterGLCanvas(final GLCaleydoCanvas glCanvas);

	public void registerGLEventListenerByGLCanvas(final GLCaleydoCanvas glCanvas,
		final AGLView gLEventListener);

	public void unregisterGLView(final AGLView glEventListener);

	/**
	 * Remove canvas from animator. Therefore the canvas is not rendered anymore.
	 */
	public void registerGLCanvasToAnimator(final GLCanvas glCanvas);

	/**
	 * Add canvas to animator. Therefore the canvas is rendered by the animator loop.
	 */
	public void unregisterGLCanvasFromAnimator(final GLCaleydoCanvas glCanvas);

	/**
	 * Get the PickingManager which is responsible for system wide picking
	 * 
	 * @return the PickingManager
	 */
	public PickingManager getPickingManager();

	public ConnectedElementRepresentationManager getConnectedElementRepresentationManager();

	public GLInfoAreaManager getInfoAreaManager();

	public void startAnimator();

	public void stopAnimator();

	/**
	 * Removes all views, canvas and GL event listeners
	 */
	public void cleanup();

	public GLCaleydoCanvas getCanvas(int iItemID);

	public AGLView getGLView(int iItemID);

	public void setActiveSWTView(Composite composite);

	public Composite getActiveSWTView();

	/**
	 * Requests busy mode for the application. This method should be called whenever a process needs to stop
	 * any user interaction with the application, e.g. when starting up or when loading multiple pathways.
	 * Usually this should result disabling user events and showing a loading screen animation.
	 * 
	 * @param requestInstance
	 *            object that wants to request busy mode
	 */
	public void requestBusyMode(Object requestInstance);

	/**
	 * Releases a previously requested busy mode. Releases are only performed by passing the originally
	 * requesting object to this method.
	 * 
	 * @param requestInstance
	 *            the object that requested the busy mode
	 */
	public void releaseBusyMode(Object requestInstance);

	/**
	 * Retrieves the {@link DisplayLoopExecution} related to the {@link IViewManager}'s display loop.
	 * 
	 * @return {@link DisplayLoopExecution} for executing code in the display loop
	 */
	public DisplayLoopExecution getDisplayLoopExecution();

	/**
	 * TODO Document me
	 * 
	 * @param viewCreator
	 */
	public void addViewCreator(IViewCreator glViewCreator);

	/**
	 * TODO Document me
	 * 
	 * @return
	 */
	public IViewCreator getViewCreator(String viewID);

}
