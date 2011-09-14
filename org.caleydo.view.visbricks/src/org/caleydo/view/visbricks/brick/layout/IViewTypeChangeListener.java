package org.caleydo.view.visbricks.brick.layout;

import org.caleydo.view.visbricks.brick.EContainedViewType;
import org.caleydo.view.visbricks.brick.GLBrick;

/**
 * A listener that is notified when the view type of a brick is changed. The
 * listeners have to be rigistered at the {@link GLBrick} in order to be
 * notified.
 * 
 * @author Partl
 * 
 */
public interface IViewTypeChangeListener {

	/**
	 * This method is called when a view in the brick is changed.
	 * 
	 * @param viewType
	 *            The type of view the brick was changed to.
	 */
	public void viewTypeChanged(EContainedViewType viewType);

}
