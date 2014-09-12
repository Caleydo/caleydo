/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation;

import java.util.List;

import org.caleydo.core.id.IDType;
import org.caleydo.view.enroute.mappeddataview.overlay.IOverlayData;

/**
 * Classifies ids. Specify an id and get the corresponding {@link SimpleCategory}.
 *
 * @author Christian
 *
 */
public interface IIDClassifier extends IOverlayData {

	/**
	 * @param id
	 * @param idType
	 * @return The {@link SimpleCategory} that corresponds to the specified id, or null if no corresponding category
	 *         could be determined.
	 */
	public SimpleCategory apply(Object id, IDType idType);

	public List<SimpleCategory> getDataClasses();

}
