/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.state;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.view.stratomex.brick.configurer.ClinicalDataConfigurer;
import org.caleydo.view.stratomex.brick.sorting.NoSortingSortingStrategy;
import org.caleydo.view.stratomex.tourguide.event.UpdateNumericalPreviewEvent;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;

/**
 * @author Samuel Gratzl
 *
 */
public class BrowseOtherState extends ABrowseState {
	protected Perspective underlying;

	public BrowseOtherState(String label) {
		super(EDataDomainQueryMode.OTHER, label);
	}

	/**
	 * @param underlying
	 *            setter, see {@link underlying}
	 */
	public void setUnderlying(Perspective underlying) {
		this.underlying = underlying;
	}

	@Override
	public void onUpdate(UpdateNumericalPreviewEvent event, IReactions adapter) {
		show(event.getTablePerspective(), adapter);
	}

	protected void show(TablePerspective numerical, IReactions adapter) {
		if (underlying == null) { // stand alone
			ClinicalDataConfigurer clinicalDataConfigurer = new ClinicalDataConfigurer();
			clinicalDataConfigurer.setSortingStrategy(new NoSortingSortingStrategy());
			adapter.replaceTemplate(numerical, clinicalDataConfigurer, true);
		} else { // dependent
			adapter.replaceClinicalTemplate(underlying, numerical, false, true);
		}
	}


}
