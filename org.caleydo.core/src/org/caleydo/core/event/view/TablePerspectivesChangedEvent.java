/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.event.view;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.view.AView;

/**
 * This event should be triggered when the {@link TablePerspective}(s) of a view change(s).
 *
 * @author Partl
 * @author Alexander Lex
 */
public class TablePerspectivesChangedEvent
	extends AEvent {

	private AView view;

	public TablePerspectivesChangedEvent(AView view) {
		this.setView(view);
	}

	@Override
	public boolean checkIntegrity() {
		if (view == null)
			return false;
		return true;
	}

	public void setView(AView view) {
		this.view = view;
	}

	public AView getView() {
		return view;
	}

}
