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
import org.caleydo.core.util.color.ColorBrewer;
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

		List<PathSegment> alternativeSegments = handler.getAlternativeSegments();
		PathSegment selectedAltSegment = handler.getSelectedAlternativeSegment();

		List<Color> colors = ColorBrewer.Set3.getColors(alternativeSegments.size());

		int childIndex = 0;
		int colorIndex = 0;
		for (PathSegment altSegment : alternativeSegments) {
			if (colorIndex > colors.size())
				colorIndex = 0;
			if (altSegment != selectedAltSegment) {
				Color altColor = colors.get(colorIndex);
				Color color = new Color(altColor.r, altColor.g, altColor.b, 0.4f);
				addSegment(color, altSegment, childIndex);
				childIndex++;
			}
			colorIndex++;
		}

		List<PathSegment> pathSegments = handler.getSelectedPath().getSegmentsOfPathway(pathwayRepresentation.getPathway());
		Color selColor = SelectionType.SELECTION.getColor();
		Color color = new Color(selColor.r, selColor.g, selColor.b, 0.5f);

		for (PathSegment segment : pathSegments) {
			addSegment(color, segment, childIndex);
			childIndex++;
		}

		int diff = size() - pathSegments.size() - (alternativeSegments.size() - 1);
		for (int i = 0; i < diff; i++) {
			remove(size() - 1);
		}
	}

	private void addSegment(Color color, PathSegment segment, int childIndex) {
		if (size() > childIndex) {
			BubbleSetPathSegmentAugmentation seg = (BubbleSetPathSegmentAugmentation) get(childIndex);
			seg.setColor(color);
			seg.setPathSegment(segment);
		} else {
			BubbleSetPathSegmentAugmentation seg = new BubbleSetPathSegmentAugmentation(pathwayRepresentation);
			seg.setColor(color);
			seg.setPathSegment(segment);
			add(seg);
		}
	}

	@Override
	protected void takeDown() {
		pathHandler.takeDown();
		super.takeDown();
	}

}
