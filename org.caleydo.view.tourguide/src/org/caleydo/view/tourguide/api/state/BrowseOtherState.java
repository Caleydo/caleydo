/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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
		if (underlying == null) {// standalone --> doesn't work
			ClinicalDataConfigurer clinicalDataConfigurer = new ClinicalDataConfigurer();
			clinicalDataConfigurer.setSortingStrategy(new NoSortingSortingStrategy());
			adapter.replaceTemplate(numerical, clinicalDataConfigurer);
		} else { // dependent
			adapter.replaceClinicalTemplate(underlying, numerical);
		}
	}


}
