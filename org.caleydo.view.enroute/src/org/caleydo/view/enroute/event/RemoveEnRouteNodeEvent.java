/**
 *
 */
package org.caleydo.view.enroute.event;

import org.caleydo.core.event.AEvent;
import org.caleydo.view.enroute.path.node.ALinearizableNode;

/**
 * Event to remove a {@link ALinearizableNode} from the linearized pathway.
 *
 * @author Christian
 *
 */
public class RemoveEnRouteNodeEvent extends AEvent {

	/**
	 * The node that shall be removed.
	 */
	private ALinearizableNode node;

	public RemoveEnRouteNodeEvent(ALinearizableNode nodeToRemove) {
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
