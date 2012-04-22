/**
 * 
 */
package org.caleydo.view.linearizedpathway.node.mode;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.linearizedpathway.GLLinearizedPathway;
import org.caleydo.view.linearizedpathway.PickingType;
import org.caleydo.view.linearizedpathway.node.ALinearizableNode;
import org.caleydo.view.linearizedpathway.node.ANode;
import org.caleydo.view.linearizedpathway.node.ANodeAttributeRenderer;
import org.caleydo.view.linearizedpathway.node.CompoundNode;
import org.caleydo.view.linearizedpathway.node.RemoveNodeButtonAttributeRenderer;

/**
 * The linearized mode for {@link CompoundNode}s.
 * 
 * @author Christian
 * 
 */
public class CompoundNodeLinearizedMode extends ACompoundNodeMode {

	/**
	 * @param view
	 */
	public CompoundNodeLinearizedMode(GLLinearizedPathway view) {
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
			attributeRenderer.registerPickingListeners();
			addAttributeRenderer(attributeRenderer);
		}
	}

	@Override
	public void render(GL2 gl, GLU glu) {

		renderCircle(gl, glu, node.getPosition(),
				pixelGLConverter.getGLHeightForPixelHeight(node.getHeightPixels()));

		for (ANodeAttributeRenderer attributeRenderer : attributeRenderers) {
			attributeRenderer.render(gl);
		}

	}

	@Override
	protected void registerPickingListeners() {
		view.addIDPickingListener(new APickingListener() {

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

	@Override
	public int getMinWidthPixels() {
		return ANode.DEFAULT_HEIGHT_PIXELS;
	}

}
