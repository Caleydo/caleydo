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
package org.caleydo.data.importer.tcga;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.ColorManager;

/**
 * A list of different dataset types that we are able to load for TCGA and which
 * have a grouping. These are the dataset types that we want to compare between
 * analysis runs.
 * 
 * @author Marc Streit
 * 
 */
public enum EDataSetType {

	mRNA(ColorManager.get().getColorList(ColorManager.QUALITATIVE_COLORS).get(0)),
	mRNAseq(ColorManager.get().getColorList(ColorManager.QUALITATIVE_COLORS).get(0).getColorWithSpecificBrighness(0.8f)),
	microRNA(ColorManager.get().getColorList(ColorManager.QUALITATIVE_COLORS).get(1)),
	microRNAseq(ColorManager.get().getColorList(ColorManager.QUALITATIVE_COLORS).get(2).getColorWithSpecificBrighness(0.8f)),
	methylation(ColorManager.get().getColorList(ColorManager.QUALITATIVE_COLORS).get(3)),
	RPPA(ColorManager.get().getColorList(ColorManager.QUALITATIVE_COLORS).get(4)),
	copyNumber(ColorManager.get().getColorList(ColorManager.QUALITATIVE_COLORS).get(5)),
	mutation(ColorManager.get().getColorList(ColorManager.QUALITATIVE_COLORS).get(6)),
	clinical(ColorManager.get().getColorList(ColorManager.QUALITATIVE_COLORS).get(7));

	private Color color;

	private EDataSetType(Color color) {
		this.color = color;
	}

	/**
	 * @return the color, see {@link #color}
	 */
	public Color getColor() {
		return color;
	}
}
