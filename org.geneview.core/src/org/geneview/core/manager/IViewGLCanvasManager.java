package org.geneview.core.manager;

import java.util.Collection;

import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.data.view.camera.IViewFrustum;
import org.geneview.core.manager.view.PickingManager;
import org.geneview.core.manager.view.SelectionManager;
import org.geneview.core.view.opengl.util.infoarea.GLInfoAreaManager;
import org.geneview.core.view.swt.data.search.DataEntitySearcherViewRep;

import com.sun.opengl.util.Animator;

/**
 * Make Jogl GLCanvas addressable by id and provide ground for XML bootstrapping of 
 * GLCanvas.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public interface IViewGLCanvasManager 
extends IViewManager {
	
	public GLEventListener createGLCanvas(CommandQueueSaxType useViewType,
			final int iUniqueId, 
			final int iGLCanvasID,
			String sLabel,
			IViewFrustum viewFrustum);

	public Collection<GLCanvas> getAllGLCanvasUsers();
	
	public Collection<GLEventListener> getAllGLEventListeners();
	
	public boolean registerGLCanvas( final GLCanvas canvas, final int iCanvasId );
	
	public boolean unregisterGLCanvas( final GLCanvas canvas );
	
	public void registerGLEventListenerByGLCanvasID(final int iGLCanvasID,
			final GLEventListener gLEventListener);
	
	public DataEntitySearcherViewRep getDataEntitySearcher();
	
	/**
	 * Get the PickingManager which is responsible for system wide picking
	 * @return the PickingManager
	 */
	public PickingManager getPickingManager();
	
	public SelectionManager getSelectionManager();
	
	public GLInfoAreaManager getInfoAreaManager();

	public void createAnimator();
	public Animator getAnimator();
}
