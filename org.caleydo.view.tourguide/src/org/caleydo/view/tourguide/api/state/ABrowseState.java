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

import org.caleydo.view.stratomex.tourguide.event.UpdateNumericalPreviewEvent;
import org.caleydo.view.stratomex.tourguide.event.UpdatePathwayPreviewEvent;
import org.caleydo.view.stratomex.tourguide.event.UpdateStratificationPreviewEvent;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.internal.OpenViewHandler;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class ABrowseState implements IState {
	private final EDataDomainQueryMode mode;
	private final String label;

	public ABrowseState(EDataDomainQueryMode mode, String label) {
		this.mode = mode;
		this.label = label;
	}

	/**
	 * @return the label, see {@link #label}
	 */
	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void onEnter() {
		OpenViewHandler.showTourGuide(mode);
	}

	@Override
	public void onLeave() {

	}

	public void onUpdate(UpdateStratificationPreviewEvent event, IReactions adapter) {

	}

	public void onUpdate(UpdatePathwayPreviewEvent event, IReactions adapter) {

	}

	public void onUpdate(UpdateNumericalPreviewEvent event, IReactions adapter) {

	}
}
