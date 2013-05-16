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
package org.caleydo.view.tourguide.internal.stratomex.state;

import org.caleydo.core.data.datadomain.DataDomainOracle;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.view.stratomex.brick.configurer.CategoricalDataConfigurer;
import org.caleydo.view.stratomex.tourguide.event.UpdateStratificationPreviewEvent;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.api.state.ABrowseState;
import org.caleydo.view.tourguide.api.state.ISelectReaction;

/**
 * @author Samuel Gratzl
 *
 */
public class BrowseStratificationState extends ABrowseState {
	public BrowseStratificationState(String label) {
		super(EDataDomainQueryMode.STRATIFICATIONS, label);
	}

	@Override
	public void onUpdate(UpdateStratificationPreviewEvent event, ISelectReaction adapter) {
		TablePerspective tp = event.getTablePerspective();
		if (DataDomainOracle.isCategoricalDataDomain(tp.getDataDomain()))
			adapter.replaceTemplate(tp, new CategoricalDataConfigurer(tp));
		else
			adapter.replaceTemplate(tp, null);
	}
}
