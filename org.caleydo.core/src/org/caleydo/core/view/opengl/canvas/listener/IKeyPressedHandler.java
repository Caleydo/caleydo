package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.manager.event.IListenerOwner;

/**
 * Interface for all classes that handle key inputs
 * 
 * @author Bernhard Schlegl
 */
public interface IKeyPressedHandler
	extends IListenerOwner {

	public void handleArrowUpPressed();

	public void handleArrowDownPressed();

	public void handleArrowLeftPressed();

	public void handleArrowRightPressed();

	public void handleArrowUpCtrlPressed();

	public void handleArrowDownCtrlPressed();

	public void handleArrowUpAltPressed();

	public void handleArrowDownAltPressed();
}
