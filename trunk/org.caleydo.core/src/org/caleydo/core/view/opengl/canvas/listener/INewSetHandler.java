package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.event.IListenerOwner;

/**
 * Interface for all classes that handle sets and want to listen to set updates. Using this interface is a
 * precondition for using a {@link NewSetListener}.
 * 
 * @author Alexander Lex
 */
public interface INewSetHandler
	extends IListenerOwner {

	/**
	 * Set the data set on which the view is operating on.
	 * 
	 * @param set
	 *            The new set to be used
	 */
	public void setSet(ISet set);

}
