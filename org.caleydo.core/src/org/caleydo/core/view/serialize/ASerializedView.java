package org.caleydo.core.view.serialize;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * Basic abstract class for all serialized view representations.
 * A serialized view is used to store a view to disk or transmit it over network.  
 * @author Werner Puff
 */
public abstract class ASerializedView {

	protected int viewID;
	
	/**
	 * Gets the command-type for the command-factory to create that creates a according view
	 * @return command-type as used by command-factory
	 */
	public abstract ECommandType getCreationCommandType();

	/**
	 * Gets the according view frustum for the view
	 * @return ViewFrustum for open-gl rendering
	 */
	public abstract ViewFrustum getViewFrustum();
	
	/**
	 * Gets the view-id as used by IViewManager implementations
	 * @return view-id of the serialized view
	 */
	public int getViewID() {
		return viewID;
	}

	/**
	 * Sets the view-id as used by IViewManager implementations
	 * @param view-id of the serialized view
	 */
	public void setViewID(int viewID) {
		this.viewID = viewID;
	}
}
