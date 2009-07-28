package org.caleydo.core.view.opengl.canvas.remote;

import java.util.List;

import javax.xml.bind.annotation.XmlElementWrapper;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.serialize.ASerializedView;

/**
 * Serialized form of the remote-rendering view (bucket). 
 * @author Werner Puff
 */
public class SerializedRemoteRenderingView 
	extends ASerializedView {

	/** @see org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering.pathwayTexturesEnabled} */
	private boolean pathwayTexturesEnabled;
	
	/** @see org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering.geneMappingEnabled} */
	private boolean geneMappingEnabled;
	
	/** @see org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering.neighborhoodEnabled} */
	private boolean neighborhoodEnabled;
	
	/** @see org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering.connectionLinesEnabled} */
	private boolean connectionLinesEnabled;

	/** list of view-ids contained in the focus-level */
	private List<ASerializedView> focusViews;
	
	/** list of view-ids contained in the stack-level */
	private List<ASerializedView> stackViews;
	
	@Override
	public ECommandType getCreationCommandType() {
		return ECommandType.CREATE_GL_BUCKET_3D;
	}

	@Override
	public ViewFrustum getViewFrustum() {
		return null;
	}

	public boolean isPathwayTexturesEnabled() {
		return pathwayTexturesEnabled;
	}

	public void setPathwayTexturesEnabled(boolean pathwayTexturesEnabled) {
		this.pathwayTexturesEnabled = pathwayTexturesEnabled;
	}

	public boolean isGeneMappingEnabled() {
		return geneMappingEnabled;
	}

	public void setGeneMappingEnabled(boolean geneMappingEnabled) {
		this.geneMappingEnabled = geneMappingEnabled;
	}

	public boolean isNeighborhoodEnabled() {
		return neighborhoodEnabled;
	}

	public void setNeighborhoodEnabled(boolean neighborhoodEnabled) {
		this.neighborhoodEnabled = neighborhoodEnabled;
	}

	public boolean isConnectionLinesEnabled() {
		return connectionLinesEnabled;
	}

	public void setConnectionLinesEnabled(boolean connectionLinesEnabled) {
		this.connectionLinesEnabled = connectionLinesEnabled;
	}

	@XmlElementWrapper
	public List<ASerializedView> getFocusViews() {
		return focusViews;
	}

	public void setFocusViews(List<ASerializedView> focusViews) {
		this.focusViews = focusViews;
	}

	@XmlElementWrapper
	public List<ASerializedView> getStackViews() {
		return stackViews;
	}

	public void setStackViews(List<ASerializedView> stackViews) {
		this.stackViews = stackViews;
	}

}
