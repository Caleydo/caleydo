package org.caleydo.core.view.vislink;

import java.util.HashMap;

import javax.media.opengl.GL2;

import org.caleydo.core.data.id.IDType;
import org.caleydo.core.event.IListenerOwner;

/**
 * Interface for utility objects to view's that help to transform and project points. The transformation and
 * projection itself are usually view specific. So each view that provides selections should have its own
 * implementation.
 * 
 * @author Werner Puff
 */
public interface ISelectionTransformer
	extends IListenerOwner {

	/**
	 * Projects all selection points related to this {@link ISelectionTransformer}'s view from the source-map
	 * into 2D selection points in the target-map. The resulting points have coordinates within the view
	 * canvas coordinate system.
	 * 
	 * @param gl
	 *            gl-object to do the transformation, it must be in the related view's state.
	 * @param deskoXID
	 *            deskotheque id (network name) of the caleydo application that displays the views the source
	 *            selection points are related to
	 * @param source
	 *            contains the selection points in the view's coordinate system
	 * @param target
	 *            is filled with connection points in the view's canvas coordinate system.
	 */
	public void project(GL2 gl, String deskoXID, HashMap<IDType, ConnectionMap> source,
		HashMap<IDType, CanvasConnectionMap> target);

	/**
	 * Transforms all selection points related to this {@link ISelectionTransformer}'s view from the
	 * source-map into the view's coordinate system. This is necessary to get the selection points in
	 * remote-rendered view's coordinate system into the remote-rendering view's coordinate system.
	 */
	public boolean transform(HashMap<IDType, ConnectionMap> source, HashMap<IDType, ConnectionMap> target);

	/**
	 * Handling method for connection-point updates
	 */
	public void handleNewConnections();

	/**
	 * Releases all obtained resources
	 */
	public void destroy();

}
