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
	extends AManager
	implements ISelectionManager
{

	private HashMap<Integer, ISelection> hashSelectionIdToSelection;

	/**
	 * Constructor.
	 */
	public SelectionManager(final IGeneralManager generalManager)
	{

		super(generalManager, IGeneralManager.iUniqueID_TypeOffset_Selection,
				EManagerType.DATA_SELECTION);

		hashSelectionIdToSelection = new HashMap<Integer, ISelection>();
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

		return hashSelectionIdToSelection.values();
	}

	public void unregisterItem(ISelection selection)
	{

		hashSelectionIdToSelection.remove(selection.getId());
	}

	public void removeSelection(int iItemId)
	{

		hashSelectionIdToSelection.remove(iItemId);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IManager#getItem(int)
	 */
	public Object getItem(int iItemId)
	{

		return hashSelectionIdToSelection.get(iItemId);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IManager#hasItem(int)
	 */
	public boolean hasItem(int iItemId)
	{

		return hashSelectionIdToSelection.containsKey(iItemId);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IManager#size()
	 */
	public int size()
	{

		return hashSelectionIdToSelection.size();
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IManager#registerItem(java.lang.Object,
	 * int)
	 */
	public boolean registerItem(Object registerItem, int iItemId)
	{

		hashSelectionIdToSelection.put(iItemId, (ISelection) registerItem);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IManager#unregisterItem(int)
	 */
	public boolean unregisterItem(int iItemId)
	{

		hashSelectionIdToSelection.remove(iItemId);
		return true;
	}
}
