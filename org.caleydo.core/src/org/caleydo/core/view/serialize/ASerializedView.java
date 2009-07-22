package org.caleydo.core.view.serialize;

import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.histogram.SerializedHistogramView;
import org.caleydo.core.view.opengl.canvas.radial.SerializedRadialHierarchyView;
import org.caleydo.core.view.opengl.canvas.remote.SerializedRemoteRenderingView;

/**
 * Basic abstract class for all serialized view representations.
 * A serialized view is used to store a view to disk or transmit it over network.  
 * @author Werner Puff
 */
@XmlType
@XmlSeeAlso({SerializedHistogramView.class, SerializedRadialHierarchyView.class, SerializedRemoteRenderingView.class})
public abstract class ASerializedView {

	protected int viewID;

	protected String viewGUIID;
	
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

	public String getViewGUIID() {
		return viewGUIID;
	}

	public void setViewGUIID(String viewGUIID) {
		this.viewGUIID = viewGUIID;
	}
}
