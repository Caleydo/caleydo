/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.data.PathwayTablePerspective;
import org.caleydo.view.dvi.GLDataViewIntegrator;
import org.caleydo.view.dvi.layout.AGraphLayout;
import org.caleydo.view.dvi.tableperspective.AMultiTablePerspectiveRenderer;
import org.caleydo.view.dvi.tableperspective.TablePerspectiveListRenderer;

public class PathwayDataNode extends ADataNode {

	private AMultiTablePerspectiveRenderer tablePerspectiveRenderer;
	private PathwayDataDomain dataDomain;
	private Row bodyRow;

	public PathwayDataNode(AGraphLayout graphLayout, GLDataViewIntegrator view,
			DragAndDropController dragAndDropController, Integer id, IDataDomain dataDomain) {
		super(graphLayout, view, dragAndDropController, id, dataDomain);
		this.dataDomain = (PathwayDataDomain) dataDomain;

	}

	@Override
	public ElementLayout setupLayout() {

		Row baseRow = createDefaultBaseRow(dataDomain.getColor(), getID());

		ElementLayout spacingLayoutX = createDefaultSpacingX();

		baseColumn = new Column();

		baseRow.append(spacingLayoutX);
		baseRow.append(baseColumn);
		baseRow.append(spacingLayoutX);

		ElementLayout captionLayout = createDefaultCaptionLayout(getID());

		ElementLayout lineSeparatorLayout = createDefaultLineSeparatorLayout();

		bodyRow = new Row("bodyRow");

		if (getVisibleTablePerspectives().size() > 0) {
			bodyRow.addBackgroundRenderer(new ColorRenderer(new float[] { 1, 1, 1, 1 }));
		}

		bodyColumn = new Column("bodyColumn");

		tablePerspectiveRenderer = new TablePerspectiveListRenderer(this, view, dragAndDropController,
				getVisibleTablePerspectives());

		List<Pair<String, Integer>> pickingIDsToBePushed = new ArrayList<Pair<String, Integer>>();
		pickingIDsToBePushed.add(new Pair<String, Integer>(DATA_GRAPH_NODE_PENETRATING_PICKING_TYPE, id));

		tablePerspectiveRenderer.setPickingIDsToBePushed(pickingIDsToBePushed);

		ElementLayout compGroupLayout = new ElementLayout("compGroupOverview");
		compGroupLayout.setRatioSizeY(1);
		// compGroupLayout.setPixelSizeX(compGroupOverviewRenderer.getMinWidthPixels());
		compGroupLayout.setRenderer(tablePerspectiveRenderer);

		ElementLayout spacingLayoutY = createDefaultSpacingY();

		bodyColumn.append(compGroupLayout);
		bodyColumn.append(spacingLayoutY);

		bodyRow.append(bodyColumn);

		baseColumn.append(spacingLayoutY);
		baseColumn.append(bodyRow);
		baseColumn.append(spacingLayoutY);
		baseColumn.append(lineSeparatorLayout);
		baseColumn.append(captionLayout);
		baseColumn.append(spacingLayoutY);

		setUpsideDown(isUpsideDown);

		return baseRow;
	}

	@Override
	public void update() {
		tablePerspectiveRenderer.setTablePerspectives(getVisibleTablePerspectives());
		recalculateNodeSize();
		bodyRow.clearBackgroundRenderers();
		if (getVisibleTablePerspectives().size() > 0) {
			bodyRow.addBackgroundRenderer(new ColorRenderer(new float[] { 1, 1, 1, 1 }));
		}
	}

	@Override
	public void destroy() {
		tablePerspectiveRenderer.destroy();
	}

	@Override
	protected AMultiTablePerspectiveRenderer getTablePerspectiveRenderer() {
		return tablePerspectiveRenderer;
	}

	@Override
	public List<TablePerspective> getTablePerspectives() {
		List<PathwayTablePerspective> containers = dataDomain.getTablePerspectives();

		List<Pair<String, TablePerspective>> sortedContainers = new ArrayList<Pair<String, TablePerspective>>(
				containers.size());

		for (PathwayTablePerspective container : containers) {
			sortedContainers.add(new Pair<String, TablePerspective>(container.getLabel(), container));
		}

		Collections.sort(sortedContainers, Pair.<String> compareFirst());

		List<TablePerspective> tablePerspectives = new ArrayList<TablePerspective>(containers.size());
		for (Pair<String, TablePerspective> containerPair : sortedContainers) {
			tablePerspectives.add(containerPair.getSecond());
		}

		return tablePerspectives;

		// return new ArrayList<TablePerspective>(dataDomain.get);
	}

	@Override
	protected int getMinTitleBarWidthPixels() {

		float textWidth = view.getTextRenderer().getRequiredTextWidthWithMax(dataDomain.getLabel(),
				pixelGLConverter.getGLHeightForPixelHeight(CAPTION_HEIGHT_PIXELS), MIN_TITLE_BAR_WIDTH_PIXELS);

		return pixelGLConverter.getPixelWidthForGLWidth(textWidth);
	}

	@Override
	public String getProviderName() {
		return "Pathway Data Node";
	}

}
