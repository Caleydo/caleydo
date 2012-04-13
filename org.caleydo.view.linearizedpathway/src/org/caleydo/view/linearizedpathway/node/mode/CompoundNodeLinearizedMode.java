/**
 * 
 */
package org.caleydo.view.linearizedpathway.node.mode;

import org.caleydo.view.linearizedpathway.GLLinearizedPathway;
import org.caleydo.view.linearizedpathway.node.ALinearizableNode;
import org.caleydo.view.linearizedpathway.node.ANode;
import org.caleydo.view.linearizedpathway.node.CompoundNode;

/**
 * The linearized mode for {@link CompoundNode}s.
 * 
 * @author Christian
 * 
 */
public class CompoundNodeLinearizedMode extends ALinearizeableNodeMode {

	/**
	 * @param view
	 */
	public CompoundNodeLinearizedMode(GLLinearizedPathway view) {
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

	}

	@Override
	public void unregisterPickingListeners() {
		
	}

}
