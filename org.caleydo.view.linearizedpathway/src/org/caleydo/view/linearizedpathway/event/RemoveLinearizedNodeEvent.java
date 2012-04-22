/**
 * 
 */
package org.caleydo.view.linearizedpathway.event;

import org.caleydo.core.event.AEvent;
import org.caleydo.view.linearizedpathway.node.ALinearizableNode;

/**
 * Event to remove a {@link ALinearizableNode} from the linearized pathway.
 * 
 * @author Christian
 * 
 */
public class RemoveLinearizedNodeEvent extends AEvent {

	/**
	 * The node that shall be removed.
	 */
	private ALinearizableNode node;

	public RemoveLinearizedNodeEvent(ALinearizableNode nodeToRemove) {
		this.node = nodeToRemove;
	}

	@Override
	public boolean checkIntegrity() {
		return node != null;
	}

	/**
	 * @param node
	 *            setter, see {@link #node}
	 */
	public void setNode(ALinearizableNode node) {
		this.node = node;
	}

	/**
	 * @return the node, see {@link #node}
	 */
	public ALinearizableNode getNode() {
		return node;
	}

}
