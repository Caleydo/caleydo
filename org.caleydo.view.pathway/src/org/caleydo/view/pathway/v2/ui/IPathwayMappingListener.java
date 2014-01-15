/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.pathway.v2.ui;

/**
 * Listener that is notified when table perspective change in a {@link PathwayDataMappingHandler}.
 *
 * @author Christian
 *
 */
public interface IPathwayMappingListener {

	/**
	 * Called when the table perspective in the specified handler changed.
	 *
	 * @param handler
	 */
	public void update(PathwayDataMappingHandler handler);

}
