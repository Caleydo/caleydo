/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.stratomex.t;

import org.caleydo.view.stratomex.RcpGLStratomexView;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.api.vis.ITourGuideView;
import org.caleydo.view.tourguide.spi.adapter.IViewAdapter;
import org.caleydo.view.tourguide.spi.adapter.IViewAdapterFactory;
import org.eclipse.ui.IViewPart;

/**
 * @author Samuel Gratzl
 *
 */
public class StratomexAdapterFactory implements IViewAdapterFactory {

	@Override
	public IViewAdapter createFor(IViewPart view, EDataDomainQueryMode mode, ITourGuideView vis) {
		if (view instanceof RcpGLStratomexView)
			return new StratomexAdapter(((RcpGLStratomexView) view).getView(), vis);
		return null;
	}

}
