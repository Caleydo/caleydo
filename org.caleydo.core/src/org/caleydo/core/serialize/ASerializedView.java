package org.caleydo.core.serialize;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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
	 * Gets the according view frustum for the view
	 * 
	 * @return ViewFrustum for open-gl rendering
	 */
	public abstract ViewFrustum getViewFrustum();

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
}
