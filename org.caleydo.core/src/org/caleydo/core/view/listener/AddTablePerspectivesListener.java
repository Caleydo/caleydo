/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.listener;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.event.view.TablePerspectivesChangedEvent;
import org.caleydo.core.view.IMultiTablePerspectiveBasedView;
import org.caleydo.core.view.ISingleTablePerspectiveBasedView;
import org.caleydo.core.view.ITablePerspectiveBasedView;
import org.caleydo.core.view.opengl.canvas.AGLView;

/**
 * Listener for {@link AddTablePerspectivesEvent}s for setting data containers to
 * {@link IMultiTablePerspectiveBasedView}s.
 *
 * @author Alexander Lex
 *
 */
public class AddTablePerspectivesListener<T extends ITablePerspectiveBasedView & IListenerOwner> extends
		AEventListener<T> {

	@Override
	public void handleEvent(AEvent event) {

		if (event instanceof AddTablePerspectivesEvent) {
			AddTablePerspectivesEvent addTablePerspectivesEvent = (AddTablePerspectivesEvent) event;
			if (addTablePerspectivesEvent.getReceiver() == handler || addTablePerspectivesEvent.getReceiver() == null) {
				add(addTablePerspectivesEvent.getTablePerspectives());
			}
		}
	}

	protected void add(List<TablePerspective> tablePerspectives) {
		List<TablePerspective> validTablePerspectives = new ArrayList<TablePerspective>(tablePerspectives.size());
		for (TablePerspective tablePerspective : tablePerspectives) {
			if (handler.getDataSupportDefinition().apply(tablePerspective.getDataDomain())) {
				validTablePerspectives.add(tablePerspective);
			}
		}
		if (validTablePerspectives.isEmpty()) {
			// Make clear for (e.g. for DVI) that no perspectives have
			// been added.
			TablePerspectivesChangedEvent e = new TablePerspectivesChangedEvent((AGLView) handler);
			e.setSender(this);
			EventPublisher.trigger(e);
		} else if (handler instanceof IMultiTablePerspectiveBasedView) {
			((IMultiTablePerspectiveBasedView) handler).addTablePerspectives(validTablePerspectives);
		} else if (handler instanceof ISingleTablePerspectiveBasedView) {
			ISingleTablePerspectiveBasedView view = ((ISingleTablePerspectiveBasedView) handler);
			if (validTablePerspectives.size() > 1) {
				throw new IllegalStateException("Tried to set multiple perspectives ("
						+ validTablePerspectives.toString() + ")s for a single table perspective view ("
						+ view.toString() + ")");
			}

			view.setTablePerspective(validTablePerspectives.get(0));
			view.setDataDomain(validTablePerspectives.get(0).getDataDomain());
		}
	}
}
