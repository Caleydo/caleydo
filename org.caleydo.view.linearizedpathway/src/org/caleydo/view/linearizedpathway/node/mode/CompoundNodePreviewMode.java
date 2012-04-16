/**
 * 
 */
package org.caleydo.view.linearizedpathway.node.mode;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.linearizedpathway.GLLinearizedPathway;
import org.caleydo.view.linearizedpathway.PickingType;
import org.caleydo.view.linearizedpathway.node.ALinearizableNode;
import org.caleydo.view.linearizedpathway.node.CompoundNode;

/**
 * The preview mode for {@link CompoundNode}s.
 * 
 * @author Christian
 * 
 */
public class CompoundNodePreviewMode extends ACompoundNodeMode {

	/**
	 * @param view
	 */
	public CompoundNodePreviewMode(GLLinearizedPathway view) {
		super(view);
	}

	@Override
	public void apply(ALinearizableNode node) {
		this.node = node;
		unregisterPickingListeners();
		registerPickingListeners();
		attributeRenderers.clear();
	}

	@Override
	protected void registerPickingListeners() {
		view.addIDPickingListener(new APickingListener() {
			@Override
			public void clicked(Pick pick) {
				view.setExpandedBranchSummaryNode(null);
				view.selectBranch(node);
			}

			@Override
			public void mouseOver(Pick pick) {
				node.setSelectionType(SelectionType.MOUSE_OVER);
				circleColor = SelectionType.MOUSE_OVER.getColor();
				view.setDisplayListDirty();
			}

			@Override
			public void mouseOut(Pick pick) {
				node.setSelectionType(SelectionType.NORMAL);
				circleColor = DEFAULT_CIRCLE_COLOR;
				view.setDisplayListDirty();
			}
		}, PickingType.LINEARIZABLE_NODE.name(), node.getNodeId());
	}

	@Override
	public void unregisterPickingListeners() {
		super.unregisterPickingListeners();
		view.removeAllIDPickingListeners(PickingType.LINEARIZABLE_NODE.name(),
				node.getNodeId());

	}

}
