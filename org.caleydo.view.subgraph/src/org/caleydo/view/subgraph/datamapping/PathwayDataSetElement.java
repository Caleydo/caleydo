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
 * Radio for selecting which table perspective to map in pathway
 *
 * @author Alexander Lex
 *
 */
public class PathwayDataSetElement extends GLButton {

	private ATableBasedDataDomain dd;
	private String label = null;

	public PathwayDataSetElement(ATableBasedDataDomain dd) {

		setMode(EButtonMode.CHECKBOX);
		setSize(170, 18);
		// setCallback(this);
		setLayoutData(dd);
		this.dd = dd;
	}

	public PathwayDataSetElement(String label) {

		setMode(EButtonMode.CHECKBOX);
		setSize(150, 18);

		this.label = label;
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		super.renderImpl(g, w, h);
		// if (dd != null) {
		// g.color(dd.getColor().getColorWithSpecificBrighness(0.8f)).fillRect(1, 1, 16, 16);
		// }
		if (isSelected()) {
			g.fillImage("resources/icons/dataassignment/radio_selected.png", 2, 2, 14, 14);
		} else {
			g.fillImage("resources/icons/dataassignment/radio_not_selected.png", 2, 2, 14, 14);
		}
		if (label != null) {
			g.drawText(label, 19, 1, w - 18, 14);
		} else {
			g.drawText(dd, 19, 1, w - 18, 14);

		}
	}
}