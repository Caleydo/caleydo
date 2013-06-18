/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.data.importer.tcga;

import org.caleydo.core.util.color.AlexColorPalette;
import org.caleydo.core.util.color.Color;

/**
 * A list of different dataset types that we are able to load for TCGA and which have a grouping. These are the dataset
 * types that we want to compare between analysis runs.
 *
 * @author Marc Streit
 *
 */
public enum EDataSetType {

	mRNA("mRNA", AlexColorPalette.Medium.get().get(0)),
	mRNAseq("mRNA-seq", AlexColorPalette.Medium.get().get(0).darker()),
	microRNA("microRNA", AlexColorPalette.Medium.get().get(1)),
	microRNAseq("microRNA-seq", AlexColorPalette.Medium.get().get(1).darker()),
	methylation("Methylation", AlexColorPalette.Medium.get().get(2)),
	RPPA("RPPA", AlexColorPalette.Medium.get().get(3)),
	copyNumber("Copy Number", AlexColorPalette.Medium.get().get(4)),
	mutation("Mutations", AlexColorPalette.Medium.get().get(5)),
	clinical("Clinical", AlexColorPalette.Medium.get().get(6));

	private final Color color;

	private final String name;

	private EDataSetType(String name, Color color) {
		this.name = name;
		this.color = color;
	}

	/**
	 * @return the color, see {@link #color}
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @return the name, see {@link #name}
	 */
	public String getName() {
		return name;
	}

	public String getTCGAAbbr() {
		switch (this) {
		case copyNumber:
			return "CopyNumber";
		case mutation:
			return "Mutation";
		case clinical:
			return "Clinical";
		case mRNAseq:
			return "mRNAseq";
		case microRNA:
			return "miR";
		case microRNAseq:
			return "miRseq";
		default:
			return this.getName();
		}
	}
}
