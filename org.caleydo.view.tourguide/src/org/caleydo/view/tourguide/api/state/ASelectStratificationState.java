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
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.view.stratomex.tourguide.event.SelectStratificationEvent;
import org.caleydo.view.stratomex.tourguide.event.SelectStratificationReplyEvent;

import com.google.common.base.Predicate;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class ASelectStratificationState extends ASelectTransition implements Predicate<TablePerspective> {
	private Object receiver;

	public ASelectStratificationState(IState target, Object receiver) {
		super(target);
		this.receiver = receiver;
	}

	@Override
	public void onEnterImpl() {
		EventPublisher.trigger(new SelectStratificationEvent(this).to(receiver).from(this));
	}

	@ListenTo(sendToMe = true)
	private void onEvent(SelectStratificationReplyEvent event) {
		handleSelection(event.getTablePerspective());

		switchToTarget();
	}

	/**
	 * @param tablePerspective
	 * @param group
	 */
	protected abstract void handleSelection(TablePerspective tablePerspective);
}
