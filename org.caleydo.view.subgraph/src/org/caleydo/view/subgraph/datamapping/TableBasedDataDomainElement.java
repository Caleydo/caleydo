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
package org.caleydo.view.subgraph.datamapping;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;

/**
 * Element representing a table based data domain element in the data mapping view.
 *
 * @author Marc streit
 *
 */
public class TableBasedDataDomainElement extends ADataMappingElement implements GLButton.ISelectionCallback {

	protected final ATableBasedDataDomain dd;
	private GLExperimentalDataMapping parent;

	public TableBasedDataDomainElement(ATableBasedDataDomain dd, DataMappingState dmState,
			GLExperimentalDataMapping parent) {

		super(dmState);
		setCallback(this);

		this.dd = dd;
		this.parent = parent;
	}

	@Override
	public void onSelectionChanged(GLButton button, boolean selected) {

		if (selected)
			dmState.addDataDomain(dd);
		else
			dmState.removeDataDomain(dd);

		parent.dataSetChanged();

	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		super.renderImpl(g, w, h);
		g.color(dd.getColor().getColorWithSpecificBrighness(0.8f)).fillRect(1, 1, 16, 16);
		if (isSelected()) {
			g.fillImage("resources/icons/dataassignment/checkbox_selected.png", 1, 1, 16, 16);
		} else {
			g.fillImage("resources/icons/dataassignment/checkbox_not_selected.png", 1, 1, 16, 16);
		}


		g.drawText(dd, 19, 1, w - 18, 14);
	}
}