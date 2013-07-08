/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.state;

import org.caleydo.core.data.datadomain.DataDomainOracle;
import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.view.stratomex.brick.configurer.CategoricalDataConfigurer;
import org.caleydo.view.stratomex.tourguide.event.UpdateStratificationPreviewEvent;
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
	public void onUpdate(UpdateStratificationPreviewEvent event, IReactions adapter) {
		TablePerspective tp = event.getTablePerspective();
		if (DataDomainOracle.isCategoricalDataDomain(tp.getDataDomain()))
			adapter.replaceTemplate(tp, new CategoricalDataConfigurer(tp));
		else if (!DataSupportDefinitions.homogenousTables.apply(tp.getDataDomain()))
			adapter.replaceTemplate(tp, new CategoricalDataConfigurer(tp));
		else
			adapter.replaceTemplate(tp, null);
	}
}
