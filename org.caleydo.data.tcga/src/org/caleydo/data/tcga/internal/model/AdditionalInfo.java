/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.data.tcga.internal.model;


/**
 * @author Samuel Gratzl
 *
 */
public class AdditionalInfo {
	private AdditionalPerspectiveInfo gene;
	private AdditionalPerspectiveInfo sample;

	/**
	 * @param gene
	 *            setter, see {@link gene}
	 */
	public void setGene(AdditionalPerspectiveInfo gene) {
		this.gene = gene;
	}

	/**
	 * @return the gene, see {@link #gene}
	 */
	public AdditionalPerspectiveInfo getGene() {
		return gene;
	}

	/**
	 * @return the sample, see {@link #sample}
	 */
	public AdditionalPerspectiveInfo getSample() {
		return sample;
	}

	public int getGeneCount() {
		return gene != null ? gene.getCount() : 0;
	}

	public int getSampleCount() {
		return sample != null ? sample.getCount() : 0;
	}

	public int getGeneStratifications() {
		return gene != null ? gene.getGroupings().size() : 0;
	}

	public int getSampleStratifications() {
		return sample != null ? sample.getGroupings().size() : 0;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AdditionalInfo [gene=");
		builder.append(gene);
		builder.append(", sample=");
		builder.append(sample);
		builder.append("]");
		return builder.toString();
	}

}

