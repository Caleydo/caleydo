/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation;

import java.util.List;

import org.caleydo.view.enroute.mappeddataview.overlay.IOverlayData;

import com.google.common.base.Function;

/**
 * Classifies data values. Specify a data value and get the corresponding {@link SimpleCategory}.
 *
 * @author Christian
 *
 */
public interface IDataClassifier extends Function<Object, SimpleCategory>, IOverlayData {

	public List<SimpleCategory> getDataClasses();

}
