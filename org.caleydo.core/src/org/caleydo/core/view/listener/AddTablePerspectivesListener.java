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
package org.caleydo.core.view.listener;

import java.util.ArrayList;
import java.util.List;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.view.TablePerspectivesChangedEvent;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.IMultiTablePerspectiveBasedView;
import org.caleydo.core.view.ISingleTablePerspectiveBasedView;
import org.caleydo.core.view.ITablePerspectiveBasedView;
import org.caleydo.core.view.opengl.canvas.AGLView;

/**
 * Listener for {@link AddTablePerspectivesEvent}s for setting data containers
 * to {@link IMultiTablePerspectiveBasedView}s.
 * 
 * @author Alexander Lex
 * 
 */
public class AddTablePerspectivesListener extends
		AEventListener<ITablePerspectiveBasedView> {

	@Override
	public void handleEvent(AEvent event) {

		if (event instanceof AddTablePerspectivesEvent) {
			AddTablePerspectivesEvent addTablePerspectivesEvent = (AddTablePerspectivesEvent) event;
			if (addTablePerspectivesEvent.getReceiver() == handler) {
				List<TablePerspective> validTablePerspectives = new ArrayList<TablePerspective>(
						addTablePerspectivesEvent.getTablePerspectives().size());
				for (TablePerspective tablePerspective : addTablePerspectivesEvent
						.getTablePerspectives()) {
					if (handler.getDataSupportDefinition().isDataDomainSupported(
							tablePerspective.getDataDomain())) {
						validTablePerspectives.add(tablePerspective);
					}
				}
				if (validTablePerspectives.size() == 0) {
					// Make clear for (e.g. for DVI) that no perspectives have
					// been added.
					TablePerspectivesChangedEvent e = new TablePerspectivesChangedEvent(
							(AGLView) handler);
					e.setSender(this);
					GeneralManager.get().getEventPublisher().triggerEvent(e);
				} else {

					if (handler instanceof IMultiTablePerspectiveBasedView) {
						((IMultiTablePerspectiveBasedView) handler)
								.addTablePerspectives(validTablePerspectives);
					} else if (handler instanceof ISingleTablePerspectiveBasedView) {
						ISingleTablePerspectiveBasedView view = ((ISingleTablePerspectiveBasedView) handler);
						if (validTablePerspectives.isEmpty()) {
							return;

						}
						if (validTablePerspectives.size() > 1) {
							throw new IllegalStateException(
									"Tried to set multiple perspectives ("
											+ validTablePerspectives.toString()
											+ ")s for a single table perspective view ("
											+ view.toString() + ")");
						}

					view
								.setTablePerspective(validTablePerspectives.get(0));
					view.setDataDomain(validTablePerspectives.get(0).getDataDomain());

					}
				}
			}
		}
	}
}
