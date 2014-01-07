/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.pathway.v2.ui.augmentation.path;

import java.util.List;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.datadomain.pathway.IPathwayRepresentation;
import org.caleydo.datadomain.pathway.graph.PathSegment;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.PathwayPath;

/**
 * @author Christian
 *
 */
public class MergedPathSegmentsAugmentation extends GLElementContainer {

	protected IPathwayRepresentation pathwayRepresentation;
	protected PathSegmentAugmentation segmentRenderer;

	/**
	 * @param pathwayRepresentation
	 */
	public MergedPathSegmentsAugmentation(IPathwayRepresentation pathwayRepresentation) {
		setLayout(GLLayouts.LAYERS);
		this.pathwayRepresentation = pathwayRepresentation;
		segmentRenderer = new PathSegmentAugmentation(pathwayRepresentation);
		add(segmentRenderer);
	}

	public void setPath(PathwayPath path) {
		PathwayPath segmentsToMerge = new PathwayPath();
		List<PathwayGraph> pathways = pathwayRepresentation.getPathways();
		for (PathSegment segment : path) {
			if (pathways.contains(segment.getPathway())) {
				segmentsToMerge.add(segment);
			}
		}

		PathSegment mergedSegment = new PathSegment(PathwayPath.flattenSegments(segmentsToMerge));
		segmentRenderer.setPathSegment(mergedSegment);
	}

	public void setColor(Color color) {
		segmentRenderer.setColor(color);
	}

}
