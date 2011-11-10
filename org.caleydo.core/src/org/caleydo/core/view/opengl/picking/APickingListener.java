package org.caleydo.core.view.opengl.picking;

/**
 * Convenience class that can be extended instead of implementing {@link IPickingListener} if not all methods
 * need to be implemented.
 * 
 * @author Christian Partl
 */

public abstract class APickingListener
	implements IPickingListener {

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

	@Override
	public void mouseOut(Pick pick) {
	}

}
