/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.search.api;

import java.util.Collection;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.id.IDType;
import org.eclipse.jface.action.MenuManager;

/**
 * extension point for defining actions that should come up with the context menu of a search result row
 *
 * @author Samuel Gratzl
 *
 */
public interface ISearchResultActionFactory {
	/**
	 * create the actions related to the given {@link Perspective} of this row
	 *
	 * @param mgr
	 * @param row
	 * @param perspectives
	 * @return whether an item was created or not
	 */
	boolean createPerspectiveActions(MenuManager mgr, IResultRow row, Collection<Perspective> perspectives);

	/**
	 * create general actions based on the {@link IDType}s of this row
	 *
	 * @param mgr
	 * @param row
	 * @return whether an item was created or not
	 */
	boolean createIDTypeActions(MenuManager mgr, IResultRow row);
}
