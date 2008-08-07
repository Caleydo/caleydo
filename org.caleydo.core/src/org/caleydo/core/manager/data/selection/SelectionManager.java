package org.caleydo.core.manager.data.selection;

import java.util.Collection;
import java.util.HashMap;
import org.caleydo.core.data.selection.ISelection;
import org.caleydo.core.data.selection.Selection;
import org.caleydo.core.manager.AManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.data.ISelectionManager;
import org.caleydo.core.manager.type.EManagerObjectType;
import org.caleydo.core.manager.type.EManagerType;

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


	/**
	 * Constructor.
	 */
	public SelectionManager(final IGeneralManager generalManager)
	{

		super(generalManager, IGeneralManager.iUniqueID_TypeOffset_Selection,
				EManagerType.DATA_SELECTION);

	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.data.ISelectionManager#createSelection()
	 */
	public ISelection createSelection(int iItemId)
	{

		ISelection newSelection = new Selection(iItemId, generalManager);

		registerItem(newSelection, iItemId);

		return newSelection;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.data.ISelectionManager#getAllSelections()
	 */
	public Collection<ISelection> getAllSelections()
	{
		return hashItems.values();
	}



}
