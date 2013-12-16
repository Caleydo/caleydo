/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.pathway.v2.ui.augmentation.path;

import java.util.List;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.event.EventListenerManager.DeepScan;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.datadomain.pathway.IPathwayRepresentation;
import org.caleydo.datadomain.pathway.graph.PathSegment;
import org.caleydo.view.pathway.v2.ui.augmentation.path.PathwayPathHandler.IPathUpdateListener;

/**
 * @author Christian
 *
 */
public class BubbleSetPathsAugmentation extends GLElementContainer implements IPathUpdateListener {

	@DeepScan
	protected PathwayPathHandler pathHandler;
	protected IPathwayRepresentation pathwayRepresentation;

	public BubbleSetPathsAugmentation(IPathwayRepresentation pathwayRepresentation, IGLCanvas canvas) {
		this.pathHandler = new PathwayPathHandler(pathwayRepresentation, canvas);
		this.pathwayRepresentation = pathwayRepresentation;
		pathHandler.addPathUpdateListener(this);
		setLayout(GLLayouts.LAYERS);
	}

	@Override
	public void onPathsChanged(PathwayPathHandler handler) {
		List<PathSegment> segments = handler.getSelectedPath().getSegmentsOfPathway(pathwayRepresentation.getPathway());
		clear();
		Color selColor = SelectionType.SELECTION.getColor();
		Color color = new Color(selColor.r, selColor.g, selColor.b, 0.5f);
		for (PathSegment segment : segments) {
			BubbleSetPathSegmentAugmentation seg = new BubbleSetPathSegmentAugmentation(pathwayRepresentation);
			seg.setColor(color);
			seg.setPathSegment(segment);
			add(seg);
		}
		repaint();
	}

	@Override
	protected void takeDown() {
		pathHandler.takeDown();
		super.takeDown();
	}

}
