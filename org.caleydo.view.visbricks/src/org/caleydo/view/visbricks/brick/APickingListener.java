package org.caleydo.view.visbricks.brick;

import org.caleydo.core.manager.picking.Pick;

/**
 * @author Partl
 * 
 * Convenience class, if not all methods need to be implemented.
 *
 */

public abstract class APickingListener implements IPickingListener {

	@Override
	public void clicked(Pick pick) {
	}

	@Override
	public void doubleClicked(Pick pick) {
	}

	@Override
	public void rightClicked(Pick pick) {
	}

	@Override
	public void mouseOver(Pick pick) {
	}

	@Override
	public void dragged(Pick pick) {
	}

}
