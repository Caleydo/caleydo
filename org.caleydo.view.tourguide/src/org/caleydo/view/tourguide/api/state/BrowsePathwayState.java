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

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.view.stratomex.tourguide.event.UpdatePathwayPreviewEvent;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;

/**
 * @author Samuel Gratzl
 *
 */
public class BrowsePathwayState extends ABrowseState {
	protected Perspective underlying;

	public BrowsePathwayState(String label) {
		super(EDataDomainQueryMode.PATHWAYS, label);
	}

	/**
	 * @param underlying
	 *            setter, see {@link underlying}
	 */
	public final void setUnderlying(Perspective underlying) {
		this.underlying = underlying;
	}

	@Override
	public void onUpdate(UpdatePathwayPreviewEvent event, IReactions adapter) {
		adapter.replacePathwayTemplate(underlying, event.getPathway());
	}
}
