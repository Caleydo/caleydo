/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.dvi.datacontainer;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.datadomain.pathway.data.PathwayDataContainer;
import org.caleydo.view.dvi.GLDataViewIntegrator;
import org.caleydo.view.dvi.contextmenu.AddGroupToStratomexItem;
import org.caleydo.view.dvi.contextmenu.CreateViewItem;
import org.caleydo.view.dvi.contextmenu.ShowDataContainerInViewsItem;
import org.caleydo.view.dvi.node.ViewNode;
import org.caleydo.view.stratomex.GLStratomex;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

public class DataContainerPickingListener extends APickingListener {

	private GLDataViewIntegrator view;
	private DragAndDropController dragAndDropController;
	private ADataContainerRenderer dataContainerRenderer;

	public DataContainerPickingListener(GLDataViewIntegrator view,
			DragAndDropController dragAndDropController,
			ADataContainerRenderer dataContainerRenderer) {
		this.view = view;
		this.dragAndDropController = dragAndDropController;
		this.dataContainerRenderer = dataContainerRenderer;
	}

	private DimensionGroupRenderer getDimensionGroupRenderer(int id) {

		Collection<DimensionGroupRenderer> dimensionGroupRenderers = dataContainerRenderer
				.getDimensionGroupRenderers();

		for (DimensionGroupRenderer dimensionGroupRenderer : dimensionGroupRenderers) {
			if (dimensionGroupRenderer.getDataContainer().getID() == id) {
				return dimensionGroupRenderer;
			}
		}
		return null;
	}

	@Override
	public void clicked(Pick pick) {
		int dimensionGroupID = pick.getObjectID();

		DimensionGroupRenderer draggedComparisonGroupRenderer = getDimensionGroupRenderer(dimensionGroupID);
		if (draggedComparisonGroupRenderer == null)
			return;
		//
		// draggedComparisonGroupRenderer
		// .setSelectionType(SelectionType.SELECTION);
		Point point = pick.getPickedPoint();
		dragAndDropController.clearDraggables();
		dragAndDropController.setDraggingProperties(new Point(point.x, point.y),
				"DimensionGroupDrag");
		// dragAndDropController.setDraggingStartPosition(new Point(point.x,
		// point.y));
		dragAndDropController.addDraggable(draggedComparisonGroupRenderer);
		// dragAndDropController.setDraggingMode("DimensionGroupDrag");
		view.setDisplayListDirty();

	}

	@Override
	public void mouseOver(Pick pick) {
		DimensionGroupRenderer dimensionGroupRenderer = getDimensionGroupRenderer(pick
				.getObjectID());
		if (dimensionGroupRenderer == null)
			return;

		dimensionGroupRenderer.setColor(dimensionGroupRenderer.getBorderColor());
		view.setDisplayListDirty();
	}

	@Override
	public void mouseOut(Pick pick) {
		DimensionGroupRenderer dimensionGroupRenderer = getDimensionGroupRenderer(pick
				.getObjectID());
		if (dimensionGroupRenderer == null)
			return;

		DataContainer dataContainer = dimensionGroupRenderer.getDataContainer();

		float[] color = dataContainer.getDataDomain().getColor().getRGBA();

		if (dataContainer instanceof PathwayDataContainer) {
			color = ((PathwayDataContainer) dataContainer).getPathwayDataDomain()
					.getColor().getRGBA();
		}

		dimensionGroupRenderer.setColor(color);
		view.setDisplayListDirty();
	}

	// @Override
	// public void dragged(Pick pick) {
	// String draggingMode = dragAndDropController.getDraggingMode();
	//
	// if (!dragAndDropController.isDragging() &&
	// dragAndDropController.hasDraggables()
	// && draggingMode != null && draggingMode.equals("DimensionGroupDrag")) {
	// dragAndDropController.startDragging();
	// }
	// }

	@Override
	public void rightClicked(Pick pick) {

		int dimensionGroupID = pick.getObjectID();
		DimensionGroupRenderer dimensionGroupRenderer = getDimensionGroupRenderer(dimensionGroupID);
		if (dimensionGroupRenderer == null)
			return;
		DataContainer dataContainer = dimensionGroupRenderer.getDataContainer();

		IExtensionRegistry registry = Platform.getExtensionRegistry();

		List<Pair<String, String>> viewTypes = new ArrayList<Pair<String, String>>();

		IConfigurationElement[] viewElements = registry
				.getConfigurationElementsFor("org.eclipse.ui.views");

		IConfigurationElement[] categoryElements = registry
				.getConfigurationElementsFor("org.caleydo.view.ViewCategory");

		for (IConfigurationElement element : viewElements) {
			try {
				String bundleID = element.getAttribute("id");
				if (bundleID.startsWith("org.caleydo.view.")) {

					for (IConfigurationElement category : categoryElements) {

						if (category.getAttribute("viewID").equals(bundleID)
								&& new Boolean(category.getAttribute("isDataView"))) {
							boolean isReleaseView = new Boolean(category.getAttribute("isReleaseView"));
							if (GeneralManager.RELEASE_MODE && !isReleaseView) {
								continue;
							}
							int indexOfLastDot = -1;
							for (int i = 0; i < 4; i++) {
								indexOfLastDot = bundleID
										.indexOf('.', indexOfLastDot + 1);
							}

							bundleID = (indexOfLastDot == -1) ? (bundleID) : (bundleID
									.substring(0, indexOfLastDot));

							Bundle bundle = Platform.getBundle(bundleID);
							if (bundle != null) {
								bundle.start();
								viewTypes
										.add(new Pair<String, String>(element
												.getAttribute("name"), element
												.getAttribute("id")));
							}
						}
					}
				}
			} catch (BundleException e) {
				e.printStackTrace();
			}
		}

		Set<String> validViewIDs = DataDomainManager
				.get()
				.getAssociationManager()
				.getViewTypesForDataDomain(
						dataContainer.getDataDomain().getDataDomainType());

		List<Pair<String, String>> finalViewTypes = new ArrayList<Pair<String, String>>();

		for (String viewID : validViewIDs) {
			for (Pair<String, String> viewType : viewTypes) {
				if (viewID.equals(viewType.getSecond())) {
					finalViewTypes.add(viewType);
				}
			}
		}

		Collections.sort(finalViewTypes);

		List<CreateViewItem> createViewItems = new ArrayList<CreateViewItem>();

		for (Pair<String, String> viewType : viewTypes) {
			createViewItems.add(new CreateViewItem(viewType.getFirst(), viewType
					.getSecond(), dataContainer.getDataDomain(), dataContainer));
		}

		if (createViewItems.size() > 0) {
			view.getContextMenuCreator().addContextMenuItem(
					new ShowDataContainerInViewsItem(createViewItems));
		}

		Set<ViewNode> viewNodes = view.getViewNodes();

		if (viewNodes != null) {
			for (ViewNode node : viewNodes) {
				if (node.getRepresentedView() instanceof GLStratomex) {
					view.getContextMenuCreator().addContextMenuItem(
							new AddGroupToStratomexItem((GLStratomex) node
									.getRepresentedView(), dataContainer));
				}
			}
		}
	}
}
