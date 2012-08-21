/**
 * 
 */
package org.caleydo.view.enroute.node.mode;

import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.core.view.opengl.layout.util.LabelRenderer;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.enroute.EPickingType;
import org.caleydo.view.enroute.GLEnRoutePathway;
import org.caleydo.view.enroute.node.ALinearizableNode;
import org.caleydo.view.enroute.node.ANode;
import org.caleydo.view.enroute.node.GeneNode;
import org.caleydo.view.enroute.node.RemoveNodeButtonAttributeRenderer;

/**
 * The linearized mode for {@link GeneNode}s.
 * 
 * @author Christian
 * 
 */
public class GeneNodeLinearizedMode extends AGeneNodeMode {

	protected ColorRenderer colorRenderer;

	/**
	 * @param view
	 */
	public GeneNodeLinearizedMode(GLEnRoutePathway view) {
		super(view);
	}

	@Override
	public void apply(ALinearizableNode node) {
		this.node = node;
		unregisterPickingListeners();
		registerPickingListeners();
		attributeRenderers.clear();
		if (node.getParentNode() == null) {
			RemoveNodeButtonAttributeRenderer attributeRenderer = new RemoveNodeButtonAttributeRenderer(
					view, node);
			attributeRenderer.addNodeId(node.getNodeId());
			addAttributeRenderer(attributeRenderer);
			attributeRenderer.registerPickingListeners();
		}

		Column baseColumn = new Column("baseColumn");
		Row baseRow = new Row("baseRow");
		colorRenderer = new ColorRenderer(this);
		colorRenderer.setView(view);
		colorRenderer.setBorderColor(new float[] { 0, 0, 0, 1 });
		colorRenderer
				.addPickingID(EPickingType.LINEARIZABLE_NODE.name(), node.getNodeId());
		baseColumn.addBackgroundRenderer(colorRenderer);

		ElementLayout labelLayout = new ElementLayout("label");
		LabelRenderer labelRenderer = new LabelRenderer(view, node);
		labelRenderer.setAlignment(LabelRenderer.ALIGN_CENTER);

		labelLayout.setRenderer(labelRenderer);
		labelLayout.setPixelSizeY(16);

		ElementLayout horizontalSpacing = new ElementLayout();
		horizontalSpacing.setPixelSizeX(2);

		// baseRow.append(horizontalSpacing);
		baseRow.append(labelLayout);
		// baseRow.append(horizontalSpacing);

		ElementLayout verticalSpacing = new ElementLayout();
		verticalSpacing.setPixelSizeY(2);

		baseColumn.append(verticalSpacing);
		baseColumn.append(baseRow);
		baseColumn.append(verticalSpacing);

		layoutManager.setBaseElementLayout(baseColumn);
	}

	@Override
	public int getMinHeightPixels() {
		return ANode.DEFAULT_HEIGHT_PIXELS;
	}

	@Override
	public int getMinWidthPixels() {
		return ANode.DEFAULT_WIDTH_PIXELS;
	}

	@Override
	protected void registerPickingListeners() {
		view.addIDPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				EventBasedSelectionManager selectionManager = view
						.getGeneSelectionManager();
				EventBasedSelectionManager metaboliteSelectionManager = view
						.getMetaboliteSelectionManager();
				metaboliteSelectionManager.clearSelection(SelectionType.SELECTION);
				selectionManager.clearSelection(SelectionType.SELECTION);
				for (Integer davidId : node.getPathwayVertexRep().getDavidIDs()) {
					selectionManager.addToType(SelectionType.SELECTION, davidId);
				}
				selectionManager.triggerSelectionUpdateEvent();

				node.setSelectionType(SelectionType.SELECTION);
				// colorRenderer.setColor(SelectionType.MOUSE_OVER.getColor());
				view.setDisplayListDirty();

			}

			@Override
			public void mouseOver(Pick pick) {
				EventBasedSelectionManager selectionManager = view
						.getGeneSelectionManager();
				EventBasedSelectionManager metaboliteSelectionManager = view
						.getMetaboliteSelectionManager();
				metaboliteSelectionManager.clearSelection(SelectionType.MOUSE_OVER);
				selectionManager.clearSelection(SelectionType.MOUSE_OVER);
				for (Integer davidId : node.getPathwayVertexRep().getDavidIDs()) {
					selectionManager.addToType(SelectionType.MOUSE_OVER, davidId);
				}
				selectionManager.triggerSelectionUpdateEvent();

				node.setSelectionType(SelectionType.MOUSE_OVER);
				// colorRenderer.setColor(SelectionType.MOUSE_OVER.getColor());
				view.setDisplayListDirty();

			}

			@Override
			public void mouseOut(Pick pick) {
				EventBasedSelectionManager selectionManager = view
						.getGeneSelectionManager();
				for (Integer davidId : node.getPathwayVertexRep().getDavidIDs()) {
					selectionManager.removeFromType(SelectionType.MOUSE_OVER, davidId);
				}
				selectionManager.triggerSelectionUpdateEvent();

				// node.setSelectionType(SelectionType.NORMAL);
				// colorRenderer.setColor(new float[] { 1, 1, 1, 1 });
				view.setDisplayListDirty();

			}
		}, EPickingType.LINEARIZABLE_NODE.name(), node.getNodeId());

	}

	@Override
	public void unregisterPickingListeners() {
		super.unregisterPickingListeners();
		view.removeAllIDPickingListeners(EPickingType.LINEARIZABLE_NODE.name(),
				node.getNodeId());
	}

}
