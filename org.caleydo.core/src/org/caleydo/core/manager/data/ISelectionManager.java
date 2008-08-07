package org.caleydo.core.manager.data;

import java.util.Collection;
import org.caleydo.core.data.selection.ISelection;
import org.caleydo.core.manager.IManager;

/**
 * Manages selections for views.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public interface ISelectionManager
	extends IManager<ISelection>
{

	public ISelection createSelection(int iItemId);


	public Collection<ISelection> getAllSelections();
}
