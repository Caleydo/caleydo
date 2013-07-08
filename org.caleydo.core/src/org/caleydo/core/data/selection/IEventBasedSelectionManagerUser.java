/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.selection;

/**
 * Interface for classes using {@link EventBasedSelectionManager}s. Is used to
 * call-back the owner.
 * 
 * @author Alexander Lex
 * 
 */
public interface IEventBasedSelectionManagerUser {

	/**
	 * Called by the {@link EventBasedSelectionManager} if a changes has
	 * happened in the {@link EventBasedSelectionManager}
	 */
	public void notifyOfSelectionChange(EventBasedSelectionManager selectionManager);

}
