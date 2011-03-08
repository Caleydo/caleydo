package org.caleydo.core.serialize;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.view.opengl.camera.ECameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * Basic abstract class for all serialized view representations. A serialized view is used to store a view to
 * disk or transmit it over network.
 * 
 * @author Werner Puff
 * @author Alexander Lex
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public abstract class ASerializedView {

	public ASerializedView() {
	}

	public ASerializedView(String dataDomainType) {
		this.dataDomainType = dataDomainType;
	}

	protected int viewID;

	protected String viewType;

	protected String dataDomainType;

	/**
	 * The full qualified view class name needed for the creation of views using reflections.
	 */
	protected String viewClassType;
	
	/**
	 * Sets the data domain associated with a view
	 * 
	 * @param dataDomain
	 */
	public void setDataDomainType(String dataDomainType) {
		this.dataDomainType = dataDomainType;
	}

	/**
	 * Returns the data domain a view is associated with
	 * 
	 * @return
	 */
	public String getDataDomainType() {
		return dataDomainType;
	}

	/**
	 * Gets the view-id as used by ViewManager implementations
	 * 
	 * @return view-id of the serialized view
	 */
	public int getViewID() {
		return viewID;
	}

	/**
	 * Sets the view-id as used by ViewManager implementations
	 * 
	 * @param view
	 *            -id of the serialized view
	 */
	public void setViewID(int viewID) {
		this.viewID = viewID;
	}

	/**
	 * Retrieves the id of the view as used within the GUI-framework.
	 * 
	 * @return GUI-related view-id.
	 */
	public abstract String getViewType();
	
	public void setViewType(String viewType) {
		this.viewType = viewType;
	}
	
	/**
	 * Gets the according view frustum for the view.
	 * Overwrite methode in subclass if a different frustum is needed.
	 * 
	 * @return ViewFrustum for open-gl rendering
	 */
	public ViewFrustum getViewFrustum() {
		return new ViewFrustum(ECameraProjectionMode.ORTHOGRAPHIC, 0, 8, 0, 8, -20, 20);
	}
	
	/**
	 * Determines the full qualified class name of the view.
	 */
	public String getViewClassType() {
		return null;
	}
}
