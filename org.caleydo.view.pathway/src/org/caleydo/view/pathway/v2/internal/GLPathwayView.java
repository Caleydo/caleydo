/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.pathway.v2.internal;

import java.util.List;

import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.datadomain.IDataSupportDefinition;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.layout2.GLElementDecorator;
import org.caleydo.core.view.opengl.layout2.view.AMultiTablePerspectiveElementView;
import org.caleydo.datadomain.pathway.manager.EPathwayDatabaseType;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.view.pathway.v2.internal.serial.SerializedPathwayView;
import org.caleydo.view.pathway.v2.ui.PathwayElement;
import org.caleydo.view.pathway.v2.ui.PathwayTextureRepresentation;
import org.caleydo.view.pathway.v2.ui.augmentation.AverageColorMappingAugmentation;
import org.caleydo.view.pathway.v2.ui.augmentation.CompoundAugmentation;
import org.caleydo.view.pathway.v2.ui.augmentation.HighVarianceIndicatorAugmentation;
import org.caleydo.view.pathway.v2.ui.augmentation.MultiMappingIndicatorAugmentation;
import org.caleydo.view.pathway.v2.ui.augmentation.StdDevBarAugmentation;
import org.caleydo.view.pathway.v2.ui.augmentation.StdDevBarConsideringVertexHighlightAugmentation;
import org.caleydo.view.pathway.v2.ui.augmentation.path.SelectablePathsAugmentation;

/**
 *
 * @author Christian
 *
 */
public class GLPathwayView extends AMultiTablePerspectiveElementView {
	public static final String VIEW_TYPE = "org.caleydo.view.pathway.v2";
	public static final String VIEW_NAME = "Pathway";

	public static final Logger log = Logger.create(GLPathwayView.class);

	private PathwayElement pathwayElement;

	private String eventSpace = EventPublisher.INSTANCE.createUniqueEventSpace();

	public GLPathwayView(IGLCanvas glCanvas) {
		super(glCanvas, VIEW_TYPE, VIEW_NAME);
		pathwayElement = new PathwayElement(eventSpace);
		// pathwayElement.setPathwayRepresentation(new PathwayTextureRepresentation(PathwayManager.get()
		// .getPathwayByTitle("ErbB signaling pathway", EPathwayDatabaseType.WIKIPATHWAYS)));
		pathwayElement.setPathwayRepresentation(new PathwayTextureRepresentation(PathwayManager.get()
				.getPathwayByTitle("Glioma", EPathwayDatabaseType.KEGG)));
		// PathwayDataMappingHandler pathwayMappingHandler = new PathwayDataMappingHandler();
		// pathwayMappingHandler.setEventSpace(eventSpace);

		// pathwayElement.addBackgroundAugmentation(new PathwayTextureBackgroundColorAugmentation(pathwayElement
		// .getPathwayRepresentation()));

		AverageColorMappingAugmentation colorMappingAugmentation = new AverageColorMappingAugmentation(
				pathwayElement.getPathwayRepresentation(), pathwayElement.getMappingHandler());

		pathwayElement.addBackgroundAugmentation(colorMappingAugmentation);
		pathwayElement.addBackgroundAugmentation(new MultiMappingIndicatorAugmentation(pathwayElement
				.getPathwayRepresentation()));

		pathwayElement.addForegroundAugmentation(new SelectablePathsAugmentation(pathwayElement
				.getPathwayRepresentation(), canvas, eventSpace));

		pathwayElement.addForegroundAugmentation(new StdDevBarAugmentation(pathwayElement.getPathwayRepresentation(),
				pathwayElement.getMappingHandler()));
		pathwayElement.addForegroundAugmentation(new HighVarianceIndicatorAugmentation(pathwayElement
				.getPathwayRepresentation(), pathwayElement.getMappingHandler()));
		pathwayElement.addForegroundAugmentation(new StdDevBarConsideringVertexHighlightAugmentation(pathwayElement));
		pathwayElement.addForegroundAugmentation(new CompoundAugmentation(pathwayElement.getPathwayRepresentation()));


		// IDMappingDescription desc = new IDMappingDescription();
		// desc.setParsingStartLine(0);
		// desc.setParsingStopLine(-1);
		// desc.setFileName("C:/Users/Christian/.caleydo_3.1/cache/caleydo/download/3.1/mappings/homo_sapiens/INTERACTION_ID2COMPOUND_ID.txt");
		// desc.setDelimiter("\t");
		// desc.setFromIDType(EGeneIDTypes.INTERACTION_ID.name());
		// desc.setToIDType(EGeneIDTypes.COMPOUND_ID.name());
		// desc.setIdCategory(EGeneIDTypes.GENE.name());
		// desc.setMultiMapping(true);
		// desc.setCreateReverseMapping(true);
		// desc.setResolveCodeMappingUsingCodeToId_LUT(false);
		// desc.setFromDataType(EDataType.INTEGER);
		// desc.setToDataType(EDataType.INTEGER);
		// IDMappingManager.addIDMappingDescription(desc);
		//
		// desc = new IDMappingDescription();
		// desc.setParsingStartLine(0);
		// desc.setParsingStopLine(-1);
		// desc.setFileName("C:/Users/Christian/.caleydo_3.1/cache/caleydo/download/3.1/mappings/homo_sapiens/INTERACTION_ID2ENTREZ_GENE_ID.txt");
		// desc.setDelimiter("\t");
		// desc.setFromIDType(EGeneIDTypes.INTERACTION_ID.name());
		// desc.setToIDType(EGeneIDTypes.ENTREZ_GENE_ID.name());
		// desc.setIdCategory(EGeneIDTypes.GENE.name());
		// desc.setMultiMapping(true);
		// desc.setCreateReverseMapping(true);
		// desc.setResolveCodeMappingUsingCodeToId_LUT(false);
		// desc.setFromDataType(EDataType.INTEGER);
		// desc.setToDataType(EDataType.INTEGER);
		// IDMappingManager.addIDMappingDescription(desc);
		//
		// desc = new IDMappingDescription();
		// desc.setParsingStartLine(0);
		// desc.setParsingStopLine(-1);
		// desc.setFileName("C:/Users/Christian/.caleydo_3.1/cache/caleydo/download/3.1/mappings/homo_sapiens/FINGERPRINT_ID2COMPOUND_ID.txt");
		// desc.setDelimiter("\t");
		// desc.setFromIDType(EGeneIDTypes.FINGERPRINT_ID.name());
		// desc.setToIDType(EGeneIDTypes.COMPOUND_ID.name());
		// desc.setIdCategory(EGeneIDTypes.GENE.name());
		// desc.setMultiMapping(true);
		// desc.setCreateReverseMapping(true);
		// desc.setResolveCodeMappingUsingCodeToId_LUT(false);
		// desc.setFromDataType(EDataType.INTEGER);
		// desc.setToDataType(EDataType.INTEGER);
		// IDMappingManager.addIDMappingDescription(desc);
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		return new SerializedPathwayView(this);
	}

	@Override
	public IDataSupportDefinition getDataSupportDefinition() {
		return DataSupportDefinitions.tableBased;
	}

	@Override
	protected void applyTablePerspectives(GLElementDecorator root, List<TablePerspective> all,
			List<TablePerspective> added, List<TablePerspective> removed) {
		root.setContent(pathwayElement);

	}

	/**
	 * @return the eventSpace, see {@link #eventSpace}
	 */
	public String getEventSpace() {
		return eventSpace;
	}

	/**
	 * @param eventSpace
	 *            setter, see {@link eventSpace}
	 */
	public void setEventSpace(String eventSpace) {
		this.eventSpace = eventSpace;
	}
}
