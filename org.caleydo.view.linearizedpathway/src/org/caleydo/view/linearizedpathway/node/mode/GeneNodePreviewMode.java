/**
 * 
 */
package org.caleydo.view.linearizedpathway.node.mode;

import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.core.view.opengl.layout.util.ILabelTextProvider;
import org.caleydo.core.view.opengl.layout.util.LabelRenderer;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.linearizedpathway.GLLinearizedPathway;
import org.caleydo.view.linearizedpathway.PickingType;
import org.caleydo.view.linearizedpathway.node.ALinearizableNode;
import org.caleydo.view.linearizedpathway.node.ANode;
import org.caleydo.view.linearizedpathway.node.GeneNode;

/**
 * Preview mode for gene {@link GeneNode}s.
 * 
 * @author Christian
 * 
 */
public class GeneNodePreviewMode extends ALinearizeableNodeMode implements
		ILabelTextProvider {

	public static final int MIN_NODE_WIDTH_PIXELS = 150;

	/**
	 * @param view
	 */
	public GeneNodePreviewMode(GLLinearizedPathway view) {
		super(view);
	}

	@Override
	public void apply(ALinearizableNode node) {
		this.node = node;
		registerPickingListeners();

		Column baseColumn = new Column("baseColumn");
		Row baseRow = new Row("baseRow");
		ColorRenderer colorRenderer = new ColorRenderer(new float[] { 1, 1, 1, 1 });
		colorRenderer.setView(view);
		colorRenderer.setBorderColor(new float[] { 0, 0, 0, 1 });
		colorRenderer
				.addPickingID(PickingType.LINEARIZABLE_NODE.name(), node.getNodeId());
		baseColumn.addBackgroundRenderer(colorRenderer);

		ElementLayout labelLayout = new ElementLayout("label");
		LabelRenderer labelRenderer = new LabelRenderer(view, this);
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

		node.setBaseLayout(baseColumn);
	}

	@Override
	public int getMinHeightPixels() {
		return ANode.DEFAULT_HEIGHT_PIXELS;
	}

	@Override
	public int getMinWidthPixels() {
		return MIN_NODE_WIDTH_PIXELS;
	}

	@Override
	protected void registerPickingListeners() {
		view.addIDPickingListener(new APickingListener() {
			@Override
			public void clicked(Pick pick) {
				view.setExpandedBranchSummaryNode(null);
				view.selectBranch(node);
			}
		}, PickingType.LINEARIZABLE_NODE.name(), node.getNodeId());

	}

	@Override
	public void unregisterPickingListeners() {
		view.removeAllIDPickingListeners(PickingType.LINEARIZABLE_NODE.name(),
				node.getNodeId());
	}

	@Override
	public String getLabelText() {
		return node.getCaption();
	}

}
