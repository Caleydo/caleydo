/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.imageviewer.internal.serial;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.view.imageviewer.internal.ImageViewerView;

/**
 *
 * @author Thomas Geymayer
 *
 */
@XmlRootElement
@XmlType
public class SerializedImageViewerView extends ASerializedView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedImageViewerView() {
	}

	public SerializedImageViewerView(ImageViewerView view) {
		super(view);
	}

	@Override
	public String getViewType() {
		return ImageViewerView.VIEW_TYPE;
	}


}
