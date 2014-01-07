package org.caleydo.view.pathway.v2.internal;

import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout2.LayoutRendererAdapter;
import org.caleydo.data.loader.ResourceLocators;
import org.caleydo.datadomain.pathway.IPathwayRepresentation;
import org.caleydo.datadomain.pathway.embedding.IPathwayRepresentationCreator;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.view.pathway.v2.ui.PathwayElement;
import org.caleydo.view.pathway.v2.ui.PathwayTextureRepresentation;
import org.caleydo.view.pathway.v2.ui.augmentation.AverageColorMappingAugmentation;
import org.caleydo.view.pathway.v2.ui.augmentation.HighVarianceIndicatorAugmentation;
import org.caleydo.view.pathway.v2.ui.augmentation.MultiMappingIndicatorAugmentation;
import org.caleydo.view.pathway.v2.ui.augmentation.StdDevBarAugmentation;
import org.caleydo.view.pathway.v2.ui.augmentation.StdDevBarConsideringVertexHighlightAugmentation;
import org.caleydo.view.pathway.v2.ui.augmentation.path.SelectablePathsAugmentation;

public class EntouragePathwayLevel1Creator implements IPathwayRepresentationCreator {

	@Override
	public IPathwayRepresentation create(AGLView remoteRenderingView, PathwayGraph pathway,
			List<TablePerspective> tablePerspectives, TablePerspective mappingTablePerspective,
			String embeddingEventSpace) {

		PathwayElement pathwayElement = new PathwayElement(embeddingEventSpace);
		PathwayTextureRepresentation pathwayRepresentation = new PathwayTextureRepresentation(pathway);

		pathwayElement.setPathwayRepresentation(pathwayRepresentation);

		AverageColorMappingAugmentation colorMappingAugmentation = new AverageColorMappingAugmentation(
				pathwayElement.getPathwayRepresentation(), pathwayElement.getMappingHandler());

		pathwayElement.addBackgroundAugmentation(colorMappingAugmentation);
		pathwayElement.addBackgroundAugmentation(new MultiMappingIndicatorAugmentation(pathwayElement
				.getPathwayRepresentation()));

		pathwayElement.addForegroundAugmentation(new SelectablePathsAugmentation(pathwayElement
				.getPathwayRepresentation(), remoteRenderingView.getParentGLCanvas(), embeddingEventSpace));

		pathwayElement.addForegroundAugmentation(new StdDevBarAugmentation(pathwayElement.getPathwayRepresentation(),
				pathwayElement.getMappingHandler()));
		pathwayElement.addForegroundAugmentation(new HighVarianceIndicatorAugmentation(pathwayElement
				.getPathwayRepresentation(), pathwayElement.getMappingHandler()));
		pathwayElement.addForegroundAugmentation(new StdDevBarConsideringVertexHighlightAugmentation(pathwayElement));

		LayoutRendererAdapter wrappingLayoutRenderer = new LayoutRendererAdapter(remoteRenderingView,
				ResourceLocators.DATA_CLASSLOADER, pathwayElement, embeddingEventSpace);

		pathwayRepresentation.setWrappingLayoutRenderer(wrappingLayoutRenderer);

		return pathwayRepresentation;
	}

}
