package org.caleydo.core.manager;

import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.view.IView;

/**
 * Manage all canvas, view, ViewRep's and GLCanvas objects.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */
public interface IViewManager
	extends IManager<IView>
{
	public IView createView(final EManagedObjectType useViewType,
			final int iParentContainerId, final String sLabel);

	public void addViewRep(IView view);

	public void removeViewRep(IView view);
}