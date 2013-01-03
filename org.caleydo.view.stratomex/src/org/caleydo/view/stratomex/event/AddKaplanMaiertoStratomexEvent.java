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
package org.caleydo.view.stratomex.event;

import java.util.Collections;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.ITablePerspectiveBasedView;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;

/**
 * event triggering to show an kaplan maier plot given the underlying table perspective for the rows and the clinicial
 * variable to show
 *
 * @author Samuel Gratzl
 *
 */
public class AddKaplanMaiertoStratomexEvent extends AddTablePerspectivesEvent {
	private TablePerspective underlying;

	public AddKaplanMaiertoStratomexEvent() {

	}

	public AddKaplanMaiertoStratomexEvent(TablePerspective perspective, TablePerspective underlying,
			ITablePerspectiveBasedView receiver) {
		this.underlying = underlying;
		setTablePerspectives(Collections.singletonList(perspective));
		setReceiver(receiver);
	}

	public TablePerspective getUnderlying() {
		return underlying;
	}

	@Override
	public boolean checkIntegrity() {
		return super.checkIntegrity() && underlying != null;
	}

}
