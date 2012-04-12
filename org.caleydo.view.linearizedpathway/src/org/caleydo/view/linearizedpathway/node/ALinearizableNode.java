/**
 * 
 */
package org.caleydo.view.linearizedpathway.node;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.linearizedpathway.GLLinearizedPathway;
import org.caleydo.view.linearizedpathway.PickingType;

/**
 * Base class for all nodes that can be linearized.
 * 
 * @author Christian
 * 
 */
public abstract class ALinearizableNode extends ANode {
	
	/**
	 * Determines whether the node shows a preview of its data.
	 */
	protected boolean isPreviewMode = false;

	/**
	 * @param pixelGLConverter
	 * @param view
	 * @param nodeId
	 */
	public ALinearizableNode(PixelGLConverter pixelGLConverter, GLLinearizedPathway view,
			int nodeId) {
		super(pixelGLConverter, view, nodeId);
	}

	@Override
	protected void registerPickingListeners() {
		view.addIDPickingListener(new APickingListener() {
			@Override
			public void clicked(Pick pick) {
				
				if(isPreviewMode) {
					view.setExpandedBranchSummaryNode(null);
					view.selectBranch(ALinearizableNode.this);
				}
			}
		}, PickingType.LINEARIZABLE_NODE.name(), nodeId);
	}
	
	@Override
	public void unregisterPickingListeners() {
		view.removeAllIDPickingListeners(PickingType.GENE_NODE.name(), nodeId);
	}
	
	/**
	 * @param isPreviewMode
	 *            setter, see {@link #isPreviewMode}
	 */
	public void setPreviewMode(boolean isPreviewMode) {
		this.isPreviewMode = isPreviewMode;
	}

	/**
	 * @return the isPreviewMode, see {@link #isPreviewMode}
	 */
	public boolean isPreviewMode() {
		return isPreviewMode;
	}

}
