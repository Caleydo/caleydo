/**
 *
 */
package org.caleydo.view.enroute.path.node.mode;

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.core.view.opengl.layout.util.LabelRenderer;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.enroute.EPickingType;
import org.caleydo.view.enroute.path.EnRoutePathRenderer;
import org.caleydo.view.enroute.path.node.ALinearizableNode;
import org.caleydo.view.enroute.path.node.ComplexNode;

/**
 * The preview mode for {@link ComplexNode}s.
 *
 * @author Christian
 *
 */
public class ComplexNodePreviewMode extends ALayoutBasedNodeMode implements IComplexNodeMode {

	// public static final int MIN_NODE_WIDTH_PIXELS = 70;

	protected EnRoutePathRenderer enRoutePathRenderer;

	/**
	 * @param view
	 */
	public ComplexNodePreviewMode(AGLView view, EnRoutePathRenderer pathwayPathRenderer) {
		super(view, pathwayPathRenderer);
		this.enRoutePathRenderer = pathwayPathRenderer;
	}

	@Override
	public void apply(ALinearizableNode node) {
		this.node = node;
		destroy();
		init();
		attributeRenderers.clear();

		Column baseColumn = new Column("baseColumn");
		Row baseRow = new Row("baseRow");
		ColorRenderer colorRenderer = new ColorRenderer(new float[] { 1, 1, 1, 1 });
		colorRenderer.setView(view);
		colorRenderer.setBorderColor(new float[] { 0, 0, 0, 1 });
		colorRenderer.setDrawBorder(true);
		colorRenderer.addPickingID(EPickingType.LINEARIZABLE_NODE.name(), node.getNodeId());
		baseColumn.addBackgroundRenderer(colorRenderer);

		ElementLayout labelLayout = new ElementLayout("label");
		LabelRenderer labelRenderer = new LabelRenderer(view, node);
		labelRenderer.setAlignment(LabelRenderer.LabelAlignment.CENTER);

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

		for (ALinearizableNode childNode : ((ComplexNode) node).getNodes()) {
			enRoutePathRenderer.setPreviewMode(childNode);
		}
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
		view.addIDPickingListener(new APickingListener() {
			@Override
			public void clicked(Pick pick) {
				enRoutePathRenderer.setExpandedBranchSummaryNode(null);
				ALinearizableNode branchNode = node;
				while (branchNode.getParentNode() != null) {
					branchNode = branchNode.getParentNode();
				}

				enRoutePathRenderer.selectBranch(branchNode);
			}
		}, EPickingType.LINEARIZABLE_NODE.name(), node.getNodeId());

	}

	@Override
	public void destroy() {
		super.destroy();
		view.removeAllIDPickingListeners(EPickingType.LINEARIZABLE_NODE.name(), node.getNodeId());
	}

	@Override
	public void updateSubNodePositions() {
		// TODO implement

	}

}
