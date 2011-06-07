package org.caleydo.core.manager.picking;


/**
 * Listener for several mouse events. The listener has to be registered at a
 * view together with the pickingID and {@link EPickingType} of the drawn object that
 * this listener should be used for.
 * 
 * @author Christian Partl
 * 
 */
public interface IPickingListener {

	/**
	 * Called, when the object corresponding to this listener has been clicked.
	 * 
	 * @param pick
	 */
	public void clicked(Pick pick);

	/**
	 * Called, when the object corresponding to this listener has been double clicked.
	 * 
	 * @param pick
	 */
	public void doubleClicked(Pick pick);

	/**
	 * Called, when the object corresponding to this listener has been right clicked.
	 * 
	 * @param pick
	 */
	public void rightClicked(Pick pick);

	/**
	 * Called, when the mouse has been moved over the object corresponding to this listener.
	 * 
	 * @param pick
	 */
	public void mouseOver(Pick pick);

	/**
	 * Called, when the object corresponding to this listener has been right dragged.
	 * 
	 * @param pick
	 */
	public void dragged(Pick pick);

}
