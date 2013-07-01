/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.serialize;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * Basic abstract class for all serialized view representations. A serialized
 * view is used to store a view to disk or transmit it over network.
 * 
 * @author Werner Puff
 * @author Alexander Lex
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public abstract class ASerializedView {

	/**
	 * DO NOT CALL THIS CONSTRUCTOR! ONLY USED FOR DESERIALIZATION.
	 */
	public ASerializedView() {
	}

	protected int viewID;

	protected String viewType;

	protected String viewLabel = "NOT SET viewLabel";

	/**
	 * The full qualified view class name needed for the creation of views using
	 * reflections.
	 */
	protected String viewClassType;

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
	 * Gets the according view frustum for the view. Overwrite method in
	 * subclass if a different frustum is needed.
	 * 
	 * @return ViewFrustum for open-gl rendering
	 */
	public ViewFrustum getViewFrustum() {
		return new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 1, 0, 1, -20, 20);
	}

	/**
	 * Determines the full qualified class name of the view.
	 */
	public String getViewClassType() {
		return null;
	}

	/**
	 * @return the viewLabel, see {@link #viewLabel}
	 */
	public String getViewLabel() {
		return viewLabel;
	}
}
