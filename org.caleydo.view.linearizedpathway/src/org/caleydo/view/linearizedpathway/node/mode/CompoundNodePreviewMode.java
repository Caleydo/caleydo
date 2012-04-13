/**
 * 
 */
package org.caleydo.view.linearizedpathway.node.mode;

import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.linearizedpathway.GLLinearizedPathway;
import org.caleydo.view.linearizedpathway.PickingType;
import org.caleydo.view.linearizedpathway.node.ALinearizableNode;
import org.caleydo.view.linearizedpathway.node.ANode;
import org.caleydo.view.linearizedpathway.node.CompoundNode;

/**
 * The preview mode for {@link CompoundNode}s.
 * 
 * @author Christian
 * 
 */
public class CompoundNodePreviewMode extends ALinearizeableNodeMode {

	/**
	 * @param view
	 */
	public CompoundNodePreviewMode(GLLinearizedPathway view) {
		super(view);
	}

	@Override
	public void apply(ALinearizableNode node) {
		this.node = node;
		registerPickingListeners();
	}

	@Override
	public int getMinHeightPixels() {
		return ANode.DEFAULT_HEIGHT_PIXELS;
	}

	@Override
	public int getMinWidthPixels() {
		return ANode.DEFAULT_HEIGHT_PIXELS;
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

}
