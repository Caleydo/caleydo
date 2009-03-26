package org.caleydo.core.view.ser;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * Basic interface for all serialized view representations.
 * A serialized view is used to store a view to disk or transmit it over network.  
 * @author Werner Puff
 */
public interface ISerializedView {
	
	/**
	 * Gets the command-type for the command-factory to create that creates a according view
	 * @return command-type as used by command-factory
	 */
	public ECommandType getCreationCommandType();

	/**
	 * Gets the according view frustum for the view
	 * @return ViewFrustum for open-gl rendering
	 */
	public ViewFrustum getViewFrustum();
}
