/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.spi.adapter;

import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.eclipse.ui.IViewPart;

/**
 * @author Samuel Gratzl
 *
 */
public interface IViewAdapterFactory {

	/**
	 * @param view
	 * @param mode
	 * @return
	 */
	IViewAdapter createFor(IViewPart view, EDataDomainQueryMode mode);

}
