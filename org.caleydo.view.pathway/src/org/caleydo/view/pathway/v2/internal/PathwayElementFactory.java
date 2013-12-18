/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.pathway.v2.internal;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.layout.GLLayoutDatas;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext;
import org.caleydo.core.view.opengl.layout2.manage.IGLElementFactory;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.view.pathway.v2.ui.PathwayElement;
import org.caleydo.view.pathway.v2.ui.PathwayTextureRepresentation;
import org.caleydo.view.pathway.v2.ui.augmentation.AverageColorMappingAugmentation;
import org.caleydo.view.pathway.v2.ui.augmentation.HighVarianceIndicatorAugmentation;
import org.caleydo.view.pathway.v2.ui.augmentation.MultiMappingIndicatorAugmentation;
import org.caleydo.view.pathway.v2.ui.augmentation.StdDevBarAugmentation;
import org.caleydo.view.pathway.v2.ui.augmentation.StdDevBarConsideringVertexHighlightAugmentation;
import org.caleydo.view.pathway.v2.ui.augmentation.path.BubbleSetPathsAugmentation;

import com.google.common.base.Supplier;

/**
 * factory element for creating pathway elements
 *
 * @author Samuel Gratzl
 *
 */
public class PathwayElementFactory implements IGLElementFactory {

	@Override
	public String getId() {
		return "pathway";
	}

	@Override
	public GLElement create(GLElementFactoryContext context) {
		PathwayGraph pathway = context.get(PathwayGraph.class, GLLayoutDatas.<PathwayGraph> throwInvalidException());
		Supplier<String> supp = new Supplier<String>() {
			@Override
			public String get() {
				return EventPublisher.INSTANCE.createUniqueEventSpace();
			}
		};

		IGLCanvas canvas = context.get(IGLCanvas.class, GLLayoutDatas.<IGLCanvas> throwInvalidException());

		PathwayElement pathwayElement = new PathwayElement(context.get("eventSpace", String.class, supp));

		pathwayElement.setPathwayRepresentation(new PathwayTextureRepresentation(pathway));

		AverageColorMappingAugmentation colorMappingAugmentation = new AverageColorMappingAugmentation(
				pathwayElement.getPathwayRepresentation(), pathwayElement.getMappingHandler());

		pathwayElement.addBackgroundAugmentation(colorMappingAugmentation);
		pathwayElement.addBackgroundAugmentation(new MultiMappingIndicatorAugmentation(pathwayElement
				.getPathwayRepresentation()));

		pathwayElement.addForegroundAugmentation(new BubbleSetPathsAugmentation(pathwayElement
				.getPathwayRepresentation(), canvas, context.get("eventSpace", String.class, supp)));

		pathwayElement.addForegroundAugmentation(new StdDevBarAugmentation(pathwayElement.getPathwayRepresentation(),
				pathwayElement.getMappingHandler()));
		pathwayElement.addForegroundAugmentation(new HighVarianceIndicatorAugmentation(pathwayElement
				.getPathwayRepresentation(), pathwayElement.getMappingHandler()));
		pathwayElement.addForegroundAugmentation(new StdDevBarConsideringVertexHighlightAugmentation(pathwayElement));

		return pathwayElement;
	}

	@Override
	public boolean apply(GLElementFactoryContext context) {
		if (context.get(PathwayGraph.class, null) == null)
			return false; // no pathway

		return true;
	}

}
