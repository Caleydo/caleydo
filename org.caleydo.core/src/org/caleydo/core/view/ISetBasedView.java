package org.caleydo.core.view;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.ISetBasedDataDomain;
import org.caleydo.core.manager.datadomain.IDataDomainBasedView;
import org.caleydo.core.view.opengl.canvas.listener.INewSetHandler;

/**
 * Interface for views that show data which is stored in sets.
 * 
 * @author Alexander Lex
 */
public interface ISetBasedView
	extends IDataDomainBasedView<ISetBasedDataDomain>, IView, INewSetHandler {

	/**
	 * Returns the current set which the view is rendering.
	 */
	@Deprecated
	public ISet getSet();

	@Deprecated
	public void setSet(ISet set);
}
