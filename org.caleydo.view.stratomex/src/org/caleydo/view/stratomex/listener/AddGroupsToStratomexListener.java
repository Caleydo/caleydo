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
package org.caleydo.view.stratomex.listener;

import java.util.HashMap;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.RecordPerspective;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.stratomex.brick.configurer.ClinicalDataConfigurer;
import org.caleydo.view.stratomex.brick.sorting.ExternallyProvidedSortingStrategy;
import org.caleydo.view.stratomex.column.BrickColumn;
import org.caleydo.view.stratomex.event.AddGroupsToStratomexEvent;
import org.caleydo.view.stratomex.event.AddKaplanMaiertoStratomexEvent;

import com.google.common.collect.Maps;

/**
 * Listener for the event {@link AddGroupsToStratomexEvent}.
 *
 * @author Christian Partl
 * @auhtor Alexander Lex
 *
 */
public class AddGroupsToStratomexListener extends AEventListener<GLStratomex> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof AddGroupsToStratomexEvent) {
			AddGroupsToStratomexEvent addGroupsToStratomexEvent = (AddGroupsToStratomexEvent) event;
			if (addGroupsToStratomexEvent.getReceiver() == handler) {
				handler.addTablePerspectives(addGroupsToStratomexEvent.getTablePerspectives(),
						addGroupsToStratomexEvent.getDataConfigurer());
			}
		}

		else if (event instanceof AddKaplanMaiertoStratomexEvent) {
			AddKaplanMaiertoStratomexEvent e = (AddKaplanMaiertoStratomexEvent) event;
			if (e.getReceiver() == handler) {
				TablePerspective underlying = e.getUnderlying();
				TablePerspective kaplan = e.getTablePerspectives().get(0);
				ClinicalDataConfigurer dataConfigurer = createKaplanConfigurer(handler, underlying, kaplan);
				handler.addTablePerspectives(e.getTablePerspectives(), dataConfigurer);
			}
		}

		else if (event instanceof AddTablePerspectivesEvent) {
			AddTablePerspectivesEvent addTablePerspectivesEvent = (AddTablePerspectivesEvent) event;
			if (addTablePerspectivesEvent.getReceiver() == handler) {
				handler.addTablePerspectives(addTablePerspectivesEvent.getTablePerspectives(), null);
			}
		}
	}

	static ClinicalDataConfigurer createKaplanConfigurer(GLStratomex handler, TablePerspective underlying,
			TablePerspective kaplan) {
		ClinicalDataConfigurer dataConfigurer = null;
		BrickColumn brickColumn = handler.getBrickColumnManager().getBrickColumn(underlying);
		if (brickColumn != null) {
			// dependent sorting
			dataConfigurer = new ClinicalDataConfigurer();
			ExternallyProvidedSortingStrategy sortingStrategy = new ExternallyProvidedSortingStrategy();
			sortingStrategy.setExternalBricks(brickColumn.getBricks());
			HashMap<RecordPerspective, RecordPerspective> m = Maps.newHashMap();
			m.put(kaplan.getRecordPerspective(), underlying.getRecordPerspective());
			sortingStrategy.setHashConvertedRecordPerspectiveToOrginalRecordPerspective(m);
			dataConfigurer.setSortingStrategy(sortingStrategy);
		}
		return dataConfigurer;
	}
}
