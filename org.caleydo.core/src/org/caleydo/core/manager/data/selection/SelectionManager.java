package org.caleydo.core.manager.data.selection;

import org.caleydo.core.data.selection.ISelection;
import org.caleydo.core.data.selection.Selection;
import org.caleydo.core.manager.AManager;
import org.caleydo.core.manager.data.ISelectionManager;

/**
 * Manages selections for views.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class SelectionManager
	extends AManager<ISelection>
	implements ISelectionManager
{
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.data.ISelectionManager#createSelection()
	 */
	public ISelection createSelection()
	{

		ISelection newSelection = new Selection();
		registerItem(newSelection);

		return newSelection;
	}
}
