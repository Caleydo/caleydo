package org.caleydo.core.view;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.view.opengl.canvas.listener.INewSetHandler;

/**
 * Interface for views that show data which is stored in sets.
 * 
 * @author Alexander Lex
 */
public interface ISetBasedView
	extends IView, INewSetHandler {

	/**
	 * Returns the current set which the view is rendering.
	 */
	public ISet getSet();
	
	public void setSet(ISet set);
}
