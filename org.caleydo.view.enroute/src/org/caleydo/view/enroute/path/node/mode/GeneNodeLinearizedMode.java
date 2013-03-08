/**
 *
 */
package org.caleydo.view.enroute.path.node.mode;

import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.core.view.opengl.layout.util.LabelRenderer.LabelAlignment;
import org.caleydo.core.view.opengl.layout.util.Renderers;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.enroute.EPickingType;
import org.caleydo.view.enroute.path.APathwayPathRenderer;
import org.caleydo.view.enroute.path.node.ALinearizableNode;
import org.caleydo.view.enroute.path.node.GeneNode;
import org.caleydo.view.enroute.path.node.RemoveNodeButtonAttributeRenderer;

/**
 * The linearized mode for {@link GeneNode}s.
 *
 * @author Christian
 *
 */
public class GeneNodeLinearizedMode extends AGeneNodeMode {

	protected ColorRenderer colorRenderer;
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

		Column baseColumn = new Column("baseColumn");
		Row baseRow = new Row("baseRow");
		colorRenderer = new ColorRenderer(this);
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
				EventBasedSelectionManager selectionManager = pathwayPathRenderer.getGeneSelectionManager();
				EventBasedSelectionManager metaboliteSelectionManager = pathwayPathRenderer
						.getMetaboliteSelectionManager();
				metaboliteSelectionManager.clearSelection(SelectionType.SELECTION);
				selectionManager.clearSelection(SelectionType.SELECTION);
				for (Integer davidId : node.getPrimaryPathwayVertexRep().getDavidIDs()) {
					selectionManager.addToType(SelectionType.SELECTION, davidId);
				}
				selectionManager.triggerSelectionUpdateEvent();

				node.setSelectionType(SelectionType.SELECTION);
				// colorRenderer.setColor(SelectionType.MOUSE_OVER.getColor());
				pathwayPathRenderer.setDisplayListDirty(true);
				// view.setDisplayListDirty();

			}

			@Override
			public void mouseOver(Pick pick) {
				if (!node.isPickable())
					return;
				EventBasedSelectionManager selectionManager = pathwayPathRenderer.getGeneSelectionManager();
				EventBasedSelectionManager metaboliteSelectionManager = pathwayPathRenderer
						.getMetaboliteSelectionManager();
				metaboliteSelectionManager.clearSelection(SelectionType.MOUSE_OVER);
				selectionManager.clearSelection(SelectionType.MOUSE_OVER);
				for (Integer davidId : node.getPrimaryPathwayVertexRep().getDavidIDs()) {
					selectionManager.addToType(SelectionType.MOUSE_OVER, davidId);
				}
				selectionManager.triggerSelectionUpdateEvent();

				node.setSelectionType(SelectionType.MOUSE_OVER);
				// colorRenderer.setColor(SelectionType.MOUSE_OVER.getColor());
				pathwayPathRenderer.setDisplayListDirty(true);

			}

			@Override
			public void mouseOut(Pick pick) {
				if (!node.isPickable())
					return;
				EventBasedSelectionManager selectionManager = pathwayPathRenderer.getGeneSelectionManager();
				for (Integer davidId : node.getPrimaryPathwayVertexRep().getDavidIDs()) {
					selectionManager.removeFromType(SelectionType.MOUSE_OVER, davidId);
				}
				selectionManager.triggerSelectionUpdateEvent();

				// node.setSelectionType(SelectionType.NORMAL);
				// colorRenderer.setColor(new float[] { 1, 1, 1, 1 });
				pathwayPathRenderer.setDisplayListDirty(true);

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
