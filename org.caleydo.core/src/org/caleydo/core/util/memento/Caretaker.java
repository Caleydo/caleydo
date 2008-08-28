package org.caleydo.core.util.memento;

import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.general.GeneralManager;

public class Caretaker
{

	IGeneralManager generalManager;

	public Caretaker()
	{
		generalManager = GeneralManager.get();
	}

	public void createMementos()
	{
		// generalManager.getSelectionManager().getAllItems()
	}
}
