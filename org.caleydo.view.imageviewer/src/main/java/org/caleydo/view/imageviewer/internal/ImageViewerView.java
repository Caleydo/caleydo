/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.imageviewer.internal;

import javax.media.opengl.GLAutoDrawable;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.layout2.AGLElementView;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementDecorator;
import org.caleydo.view.imageviewer.internal.serial.SerializedImageViewerView;
import org.caleydo.view.imageviewer.ui.ImageViewerElement;

/**
 *
 * @author Thomas Geymayer
 *
 */
public class ImageViewerView extends AGLElementView {
	public static final String VIEW_TYPE = "org.caleydo.view.imageviewer";
	public static final String VIEW_NAME = "ImageViewer";

	private static final Logger log = Logger.create(ImageViewerView.class);

	protected ImageViewerElement viewer;

	public ImageViewerView(IGLCanvas glCanvas) {
		super(glCanvas, VIEW_TYPE, VIEW_NAME);
	}

	public ImageViewerElement getImageViewer() {
		return viewer;
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		super.init(drawable);
		viewer = new ImageViewerElement();
		((GLElementDecorator) getRoot()).setContent(viewer);
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		return new SerializedImageViewerView(this);
	}

	@Override
	protected GLElement createRoot() {
		return new GLElementDecorator();
	}

}
