/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.imageviewer.ui;

import java.util.Map.Entry;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLImageElement;
import org.caleydo.core.view.opengl.layout2.GLImageViewer;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.datadomain.image.LayeredImage;
import org.caleydo.datadomain.image.LayeredImage.Image;

/**
 * element of this view holding a {@link TablePerspective}
 *
 * @author Thomas Geymayer
 *
 */
public class ImageViewerElement extends GLImageViewer {

	protected APickingListener pickingListener;

	public ImageViewerElement() {
		pickingListener = new APickingListener() {
			@Override
			protected void mouseOver(Pick pick) {
				((GLImageElement) pick.getObject()).setColor(Color.RED);
			}

			@Override
			protected void mouseOut(Pick pick) {
				((GLImageElement) pick.getObject()).setColor(Color.WHITE);
			}
		};
	}

	public void setImage(LayeredImage img) {
		clear();
		setBaseImage(img.getBaseImage().image.getPath());

		for (Entry<String, LayeredImage.Layer> layer : img.getLayers().entrySet()) {
			Image highlight = layer.getValue().highlight;
			Image mask = layer.getValue().mask;

			String highlightPath = "", maskPath = "";

			if (highlight == null) {
				if (mask == null)
					continue;
				highlightPath = mask.image.getPath();
			} else {
				highlightPath = highlight.image.getPath();

				if (mask != null)
					maskPath = mask.image.getPath();
			}

			addLayer(highlightPath, maskPath).onPick(pickingListener);
		}
	}

}
