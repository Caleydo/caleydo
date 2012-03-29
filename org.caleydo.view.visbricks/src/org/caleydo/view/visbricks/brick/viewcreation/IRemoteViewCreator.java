package org.caleydo.view.visbricks.brick.viewcreation;

import javax.media.opengl.GL2;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.view.visbricks.brick.GLBrick;

/**
 * Base class whose subclasses are intended to create remote views for a brick.
 * 
 * @author Christian Partl
 * 
 */
public interface IRemoteViewCreator {

	/**
	 * Creates a remote view specified by the concrete subclass of
	 * {@link IRemoteViewCreator}.
	 * 
	 * @param remoteRenderingView
	 *            View that remote renders the view created by this method.
	 * @param gl
	 * @param glMouseListener
	 * @return remote view.
	 */
	public AGLView createRemoteView(GLBrick remoteRenderingView, GL2 gl,
			GLMouseListener glMouseListener);

}
