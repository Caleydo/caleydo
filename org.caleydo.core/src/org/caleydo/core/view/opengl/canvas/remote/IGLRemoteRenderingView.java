package org.caleydo.core.view.opengl.canvas.remote;

import java.util.List;

import javax.media.opengl.awt.GLCanvas;

import org.caleydo.core.view.IView;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.eclipse.swt.widgets.Composite;

/**
 * Interface for accessing views that remotely render other views.
 * 
 * @author Marc Streit
 */
public interface IGLRemoteRenderingView
	extends IView {

	public GLCanvas getParentGLCanvas();

	public Composite getParentComposite();
	
	/**
	 * Retrieves all the contained view-types from a given view.
	 * 
	 * @return list of view-types contained in the given view
	 */
	public List<AGLView> getRemoteRenderedViews();
}
