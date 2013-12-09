/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.idbrowser.internal;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.layout2.AGLElementView;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.view.idbrowser.internal.serial.SerializedIDBrowserView;
import org.caleydo.view.idbrowser.ui.IDBrowserElement;

/**
 *
 * @author Samuel Gratzl
 *
 */
public class IDBrowserView extends AGLElementView {
	public static final String VIEW_TYPE = "org.caleydo.view.idbrowser";
	public static final String VIEW_NAME = "ID Browser";

	public IDBrowserView(IGLCanvas glCanvas) {
		super(glCanvas, VIEW_TYPE, VIEW_NAME);
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		return new SerializedIDBrowserView();
	}

	@Override
	protected GLElement createRoot() {
		return new IDBrowserElement();
	}
}
