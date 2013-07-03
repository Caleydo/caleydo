/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GL2;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.IMultiTablePerspectiveBasedView;
import org.caleydo.core.view.ITablePerspectiveBasedView;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;
import org.caleydo.core.view.opengl.util.draganddrop.IDropArea;
import org.caleydo.datadomain.pathway.data.PathwayTablePerspective;
import org.caleydo.view.dvi.GLDataViewIntegrator;
import org.caleydo.view.dvi.layout.AGraphLayout;
import org.caleydo.view.dvi.tableperspective.AMultiTablePerspectiveRenderer;
import org.caleydo.view.dvi.tableperspective.TablePerspectiveListRenderer;
import org.caleydo.view.dvi.tableperspective.TablePerspectiveRenderer;

public class MultiTablePerspectiveViewNode extends ViewNode implements IDropArea {

	protected TablePerspectiveListRenderer tablePerspectiveListRenderer;
	protected List<TablePerspective> tablePerspectives;

	public MultiTablePerspectiveViewNode(AGraphLayout graphLayout,
			GLDataViewIntegrator view, DragAndDropController dragAndDropController,
 Integer id, IView representedView) {
		super(graphLayout, view, dragAndDropController, id, representedView);
	}


	@Override
	protected ElementLayout setupLayout() {
		ElementLayout baseLayout = super.setupLayout();

		bodyColumn.clear();

		ElementLayout tablePerspectiveLayout = new ElementLayout("datContainerList");

		tablePerspectiveListRenderer = new TablePerspectiveListRenderer(this, view,
				dragAndDropController, getTablePerspectives());

		List<Pair<String, Integer>> pickingIDsToBePushed = new ArrayList<Pair<String, Integer>>();
		pickingIDsToBePushed.add(new Pair<String, Integer>(
				DATA_GRAPH_NODE_PENETRATING_PICKING_TYPE, id));

		tablePerspectiveListRenderer.setPickingIDsToBePushed(pickingIDsToBePushed);

		tablePerspectiveLayout.setRatioSizeY(1);
		tablePerspectiveLayout.setRenderer(tablePerspectiveListRenderer);

		ElementLayout spacingLayoutY = createDefaultSpacingY();

		bodyColumn.append(tablePerspectiveLayout);
		bodyColumn.append(spacingLayoutY);

		return baseLayout;
	}

	@Override
	protected AMultiTablePerspectiveRenderer getTablePerspectiveRenderer() {
		return tablePerspectiveListRenderer;
	}

	@Override
	public boolean showsTablePerspectives() {
		return true;
	}

	@Override
	public void handleDragOver(GL2 gl, Set<IDraggable> draggables,
			float mouseCoordinateX, float mouseCoordinateY) {

	}

	@Override
	public List<TablePerspective> getTablePerspectives() {

		if (tablePerspectives == null) {
			retrieveTablePerspectives();
		}

		return tablePerspectives;
	}

	protected void retrieveTablePerspectives() {

		tablePerspectives = new ArrayList<TablePerspective>(
				((ITablePerspectiveBasedView) representedView).getTablePerspectives());
		if (tablePerspectives == null) {
			tablePerspectives = new ArrayList<TablePerspective>();
			return;
		}

		Set<IDataDomain> dataDomains = new HashSet<IDataDomain>();

		for (TablePerspective container : tablePerspectives) {
			if (container instanceof PathwayTablePerspective) {
				dataDomains.add(((PathwayTablePerspective) container)
						.getPathwayDataDomain());
			} else {
				dataDomains.add(container.getDataDomain());
			}
		}

		sortTablePerspectives();

	}

	private void sortTablePerspectives() {
		List<TablePerspective> containers = new ArrayList<TablePerspective>(
				tablePerspectives);

		List<Pair<Float, ADataNode>> sortedDataNodes = new ArrayList<Pair<Float, ADataNode>>();

		for (IDataDomain dataDomain : dataDomains) {
			ADataNode dataNode = view.getDataNode(dataDomain);
			if (dataNode != null) {
				sortedDataNodes.add(new Pair<Float, ADataNode>((float) dataNode
						.getPosition().getX(), dataNode));
			}
		}

		Collections.sort(sortedDataNodes, Pair.<Float> compareFirst());
		tablePerspectives.clear();

		for (Pair<Float, ADataNode> dataNodePair : sortedDataNodes) {
			ADataNode dataNode = dataNodePair.getSecond();

			List<TablePerspective> sortedNodeTablePerspectives = dataNode
					.getTablePerspectives();

			for (TablePerspective nodeContainer : sortedNodeTablePerspectives) {
				TablePerspective addedContainer = null;
				for (TablePerspective container : containers) {
					if (nodeContainer == container) {
						tablePerspectives.add(container);
						addedContainer = container;
						break;
					}
				}
				if (addedContainer != null)
					containers.remove(addedContainer);
			}
		}
	}

	@Override
	public void handleDrop(GL2 gl, Set<IDraggable> draggables, float mouseCoordinateX,
			float mouseCoordinateY, DragAndDropController dragAndDropController) {
		ArrayList<TablePerspective> tablePerspectives = new ArrayList<TablePerspective>();
		for (IDraggable draggable : draggables) {
			if (draggable instanceof TablePerspectiveRenderer) {
				TablePerspectiveRenderer dimensionGroupRenderer = (TablePerspectiveRenderer) draggable;
				tablePerspectives.add(dimensionGroupRenderer.getTablePerspective());
			}
		}

		if (!tablePerspectives.isEmpty()) {
			// FIXME: this needs to be looked at again
			// System.out.println("Drop");
			TablePerspective tablePerspective = tablePerspectives.get(0);
			AddTablePerspectivesEvent event = new AddTablePerspectivesEvent(
					tablePerspective);
			event.setReceiver((IMultiTablePerspectiveBasedView) representedView);
			event.setSender(this);
			GeneralManager.get().getEventPublisher().triggerEvent(event);

			if (tablePerspective instanceof PathwayTablePerspective) {
				dataDomains.add(((PathwayTablePerspective) tablePerspective)
						.getPathwayDataDomain());
			} else {
				dataDomains.add(tablePerspective.getDataDomain());
			}
			getTablePerspectives().add(tablePerspective);
			sortTablePerspectives();
			tablePerspectiveListRenderer.setTablePerspectives(getTablePerspectives());
			view.updateGraphEdgesOfViewNode(this);
			recalculateNodeSize();
			graphLayout.fitNodesToDrawingArea(view.calculateGraphDrawingArea());
			view.setDisplayListDirty();
		}

		// dragAndDropController.clearDraggables();

	}

	@Override
	public void destroy() {
		super.destroy();
		// overviewTablePerspectiveRenderer.destroy();
		tablePerspectiveListRenderer.destroy();
	}

	@Override
	public void update() {
		dataDomains = representedView.getDataDomains();
		retrieveTablePerspectives();
		tablePerspectiveListRenderer.setTablePerspectives(getTablePerspectives());
		recalculateNodeSize();
		graphLayout.fitNodesToDrawingArea(view.calculateGraphDrawingArea());
		view.setDisplayListDirty();
	}

	@Override
	public void render(GL2 gl) {
		// retrieveTablePerspectives();
		// tablePerspectiveListRenderer.setTablePerspectives(getTablePerspectives());
		super.render(gl);
	}

	@Override
	public void handleDropAreaReplaced() {
		// TODO Auto-generated method stub

	}

}
