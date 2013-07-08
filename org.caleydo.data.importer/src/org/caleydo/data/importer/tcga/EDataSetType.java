/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
