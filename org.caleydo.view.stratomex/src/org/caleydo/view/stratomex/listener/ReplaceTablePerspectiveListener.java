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

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.data.ReplaceTablePerspectiveEvent;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.stratomex.brick.configurer.ClinicalDataConfigurer;
import org.caleydo.view.stratomex.column.BrickColumn;
import org.caleydo.view.stratomex.event.ReplaceKaplanMaierPerspectiveEvent;

/**
 * @author alexsb
 *
 */
public class ReplaceTablePerspectiveListener extends AEventListener<GLStratomex> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ReplaceKaplanMaierPerspectiveEvent) {
			ReplaceKaplanMaierPerspectiveEvent e = (ReplaceKaplanMaierPerspectiveEvent) event;
			if (handler.getID() == e.getViewID()) {
				// update normally
				TablePerspective underlying = e.getUnderlying();
				BrickColumn brickColumn = handler.getBrickColumnManager().getBrickColumn(e.getOldPerspective());
				// brickColumn.getTablePerspective()
				ClinicalDataConfigurer dataConfigurer = AddGroupsToStratomexListener.createKaplanConfigurer(handler,
						underlying,
						e.getNewPerspective());
				if (dataConfigurer != null)
					brickColumn.setBrickConfigurer(dataConfigurer);
				handler.replaceTablePerspective(e.getNewPerspective(), e.getOldPerspective());

				assert brickColumn != null;
			}
		}
		if (event instanceof ReplaceTablePerspectiveEvent) {
			ReplaceTablePerspectiveEvent rEvent = (ReplaceTablePerspectiveEvent) event;
			if (handler.getID() == rEvent.getViewID()) {
				handler.replaceTablePerspective(rEvent.getNewPerspective(),
						rEvent.getOldPerspective());
			}
		}

	}
}
