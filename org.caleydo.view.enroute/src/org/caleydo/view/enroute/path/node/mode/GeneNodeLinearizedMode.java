/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 *
 */
package org.caleydo.view.enroute.path.node.mode;

import java.util.List;

import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.data.DataSetSelectedEvent;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.LabelRenderer.LabelAlignment;
import org.caleydo.core.view.opengl.layout.util.Renderers;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.view.enroute.EPickingType;
import org.caleydo.view.enroute.path.APathwayPathRenderer;
import org.caleydo.view.enroute.path.node.ALinearizableNode;
import org.caleydo.view.enroute.path.node.GeneNode;
import org.caleydo.view.enroute.path.node.MultiMappingAttributeRenderer;
import org.caleydo.view.enroute.path.node.RemoveNodeButtonAttributeRenderer;

/**
 * The linearized mode for {@link GeneNode}s.
 *
 * @author Christian
 *
 */
public class GeneNodeLinearizedMode extends AGeneNodeMode {

	protected MappingRenderer colorRenderer;
	protected IPickingListener pickingListener;

	/**
	 * @param view
	 */
	public GeneNodeLinearizedMode(AGLView view, APathwayPathRenderer pathwayPathRenderer) {
		super(view, pathwayPathRenderer);
	}

	@Override
	public void apply(ALinearizableNode node) {
		this.node = node;
		destroy();
		init();
		attributeRenderers.clear();
		if (node.getParentNode() == null) {
			RemoveNodeButtonAttributeRenderer attributeRenderer = new RemoveNodeButtonAttributeRenderer(view, node,
					pathwayPathRenderer);
			attributeRenderer.addNodeId(node.hashCode());
			addAttributeRenderer(attributeRenderer);
			attributeRenderer.registerPickingListeners();
		}
		List<PathwayVertexRep> vertexReps = node.getVertexReps();
		if (vertexReps != null && !vertexReps.isEmpty()) {
			boolean isMultiMappingNode = false;
			for (PathwayVertexRep vertexRep : vertexReps) {
				if (vertexRep.getPathwayVertices().size() > 1) {
					isMultiMappingNode = true;
					break;
				}
			}
			if (isMultiMappingNode) {
				addAttributeRenderer(new MultiMappingAttributeRenderer(view, node, pathwayPathRenderer));
			}
		}

		Column baseColumn = new Column("baseColumn");
		Row baseRow = new Row("baseRow");

		colorRenderer = new MappingRenderer(view, pathwayPathRenderer, node);
		colorRenderer.setView(view);
		colorRenderer.setBorderColor(new float[] { 0, 0, 0, 1 });
		colorRenderer.setDrawBorder(true);
		colorRenderer.addPickingID(EPickingType.LINEARIZABLE_NODE.name(), node.hashCode());
		baseColumn.addBackgroundRenderer(colorRenderer);

		ElementLayout labelLayout = new ElementLayout("label");
		labelLayout.setRenderer(Renderers.createLabel(node, view).setAlignment(LabelAlignment.CENTER));
		labelLayout.setPixelSizeY(pathwayPathRenderer.getSizeConfig().getNodeTextHeight());

		ElementLayout horizontalSpacing = new ElementLayout();
		int spacing = (int) ((getMinHeightPixels() - pathwayPathRenderer.getSizeConfig().getNodeTextHeight()) / 2.0f);
		horizontalSpacing.setPixelSizeX(spacing);

		// baseRow.append(horizontalSpacing);
		baseRow.append(labelLayout);
		// baseRow.append(horizontalSpacing);

		ElementLayout verticalSpacing = new ElementLayout();
		verticalSpacing.setPixelSizeY(spacing);

		baseColumn.append(verticalSpacing);
		baseColumn.append(baseRow);
		baseColumn.append(verticalSpacing);

		layoutManager.setBaseElementLayout(baseColumn);
	}

	@Override
	public int getMinHeightPixels() {
		return pathwayPathRenderer.getSizeConfig().getRectangleNodeHeight();
	}

	@Override
	public int getMinWidthPixels() {
		return pathwayPathRenderer.getSizeConfig().getRectangleNodeWidth();
	}

	@Override
	protected void init() {
		pickingListener = new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				if (!node.isPickable())
					return;
				// EventBasedSelectionManager selectionManager = pathwayPathRenderer.getGeneSelectionManager();
				EventBasedSelectionManager vertexSelectionManager = pathwayPathRenderer.getVertexSelectionManager();
				EventBasedSelectionManager metaboliteSelectionManager = pathwayPathRenderer
						.getMetaboliteSelectionManager();
				metaboliteSelectionManager.clearSelection(SelectionType.SELECTION);
				// selectionManager.clearSelection(SelectionType.SELECTION);
				// for (Integer davidId : node.getPrimaryPathwayVertexRep().getDavidIDs()) {
				// selectionManager.addToType(SelectionType.SELECTION, davidId);
				// }
				// selectionManager.triggerSelectionUpdateEvent();
				vertexSelectionManager.clearSelection(SelectionType.SELECTION);
				vertexSelectionManager.addToType(SelectionType.SELECTION, node.getPrimaryPathwayVertexRep().getID());
				vertexSelectionManager.triggerSelectionUpdateEvent();

				node.setSelectionType(SelectionType.SELECTION);

				if (pathwayPathRenderer.getMappedPerspective() != null) {
					EventPublisher.trigger(new DataSetSelectedEvent(pathwayPathRenderer.getMappedPerspective()));
				}
				// colorRenderer.setColor(SelectionType.MOUSE_OVER.getColor());
				// pathwayPathRenderer.setHighlightDirty(true);
				// view.setDisplayListDirty();

			}

			@Override
			public void mouseOver(Pick pick) {
				if (!node.isPickable())
					return;
				// EventBasedSelectionManager selectionManager = pathwayPathRenderer.getGeneSelectionManager();
				EventBasedSelectionManager metaboliteSelectionManager = pathwayPathRenderer
						.getMetaboliteSelectionManager();
				metaboliteSelectionManager.clearSelection(SelectionType.MOUSE_OVER);
				// selectionManager.clearSelection(SelectionType.MOUSE_OVER);
				// for (Integer davidId : node.getPrimaryPathwayVertexRep().getDavidIDs()) {
				// selectionManager.addToType(SelectionType.MOUSE_OVER, davidId);
				// }
				// selectionManager.triggerSelectionUpdateEvent();

				EventBasedSelectionManager vertexSelectionManager = pathwayPathRenderer.getVertexSelectionManager();
				vertexSelectionManager.clearSelection(SelectionType.MOUSE_OVER);
				vertexSelectionManager.addToType(SelectionType.MOUSE_OVER, node.getPrimaryPathwayVertexRep().getID());
				vertexSelectionManager.triggerSelectionUpdateEvent();

				node.setSelectionType(SelectionType.MOUSE_OVER);
				// colorRenderer.setColor(SelectionType.MOUSE_OVER.getColor());
				// pathwayPathRenderer.setHighlightDirty(true);

			}

			@Override
			public void mouseOut(Pick pick) {
				if (!node.isPickable())
					return;
				// EventBasedSelectionManager selectionManager = pathwayPathRenderer.getGeneSelectionManager();
				// for (Integer davidId : node.getPrimaryPathwayVertexRep().getDavidIDs()) {
				// selectionManager.removeFromType(SelectionType.MOUSE_OVER, davidId);
				// }
				// selectionManager.triggerSelectionUpdateEvent();

				EventBasedSelectionManager vertexSelectionManager = pathwayPathRenderer.getVertexSelectionManager();
				vertexSelectionManager.removeFromType(SelectionType.MOUSE_OVER, node.getPrimaryPathwayVertexRep()
						.getID());
				vertexSelectionManager.triggerSelectionUpdateEvent();

				// node.setSelectionType(SelectionType.NORMAL);
				// colorRenderer.setColor(new float[] { 1, 1, 1, 1 });
				// pathwayPathRenderer.setHighlightDirty(true);

			}
		};
		view.addIDPickingListener(pickingListener, EPickingType.LINEARIZABLE_NODE.name(), node.hashCode());

	}

	@Override
	public void destroy() {
		super.destroy();
		view.removeIDPickingListener(pickingListener, EPickingType.LINEARIZABLE_NODE.name(), node.hashCode());
	}

}
