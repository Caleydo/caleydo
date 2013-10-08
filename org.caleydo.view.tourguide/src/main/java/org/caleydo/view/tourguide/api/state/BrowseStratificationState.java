/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.state;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;

/**
 * @author Samuel Gratzl
 *
 */
public class BrowseStratificationState extends ABrowseState {
	public BrowseStratificationState(String label) {
		super(EDataDomainQueryMode.STRATIFICATIONS, label);
	}

	@Override
	public void onUpdateStratification(TablePerspective tablePerspective, IReactions adapter) {
		adapter.replaceTemplate(tablePerspective, false);
	}
}
