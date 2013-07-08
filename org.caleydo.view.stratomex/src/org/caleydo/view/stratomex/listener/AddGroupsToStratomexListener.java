/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.listener;

import java.util.HashMap;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.stratomex.brick.configurer.ClinicalDataConfigurer;
import org.caleydo.view.stratomex.brick.sorting.ExternallyProvidedSortingStrategy;
import org.caleydo.view.stratomex.brick.sorting.NoSortingSortingStrategy;
import org.caleydo.view.stratomex.column.BrickColumn;

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

		if (event instanceof AddTablePerspectivesEvent) {
			AddTablePerspectivesEvent addTablePerspectivesEvent = (AddTablePerspectivesEvent) event;
			if (addTablePerspectivesEvent.getReceiver() == handler) {
				handler.addTablePerspectives(addTablePerspectivesEvent.getTablePerspectives(), null, null);
			}
		}
	}

	public static ClinicalDataConfigurer createKaplanConfigurer(GLStratomex handler, TablePerspective underlying,
			TablePerspective kaplan) {
		ClinicalDataConfigurer dataConfigurer = null;
		BrickColumn brickColumn = handler.getBrickColumnManager().getBrickColumn(underlying);
		if (brickColumn != null) {
			// dependent sorting
			dataConfigurer = new ClinicalDataConfigurer();
			ExternallyProvidedSortingStrategy sortingStrategy = new ExternallyProvidedSortingStrategy();
			sortingStrategy.setExternalBrick(brickColumn);
			HashMap<Perspective, Perspective> m = Maps.newHashMap();
			m.put(kaplan.getRecordPerspective(), underlying.getRecordPerspective());
			sortingStrategy.setHashConvertedRecordPerspectiveToOrginalRecordPerspective(m);
			dataConfigurer.setSortingStrategy(sortingStrategy);
		}
		return dataConfigurer;
	}

	public static ClinicalDataConfigurer createTemplateKaplanConfigurer(GLStratomex handler, TablePerspective kaplan) {
		ClinicalDataConfigurer dataConfigurer = new ClinicalDataConfigurer();
		dataConfigurer.setSortingStrategy(new NoSortingSortingStrategy());
		return dataConfigurer;
	}
}
