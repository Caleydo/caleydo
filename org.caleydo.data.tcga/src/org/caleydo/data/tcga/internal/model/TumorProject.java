/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.data.tcga.internal.model;

import java.net.URL;

import com.google.gson.annotations.SerializedName;


public class TumorProject implements Comparable<TumorProject> {
	private String tumorAbbreviation;
	private String tumorName;
	@SerializedName("Caleydo JNLP")
	private URL jnlp;
	@SerializedName("Caleydo Project")
	private URL project;
	@SerializedName("Firehose Report")
	private URL report;

	private transient Run parent;

	@Override
	public String toString() {
		return tumorAbbreviation + " - " + tumorName;
	}
	/**
	 * @return the project, see {@link #project}
	 */
	public URL getProject() {
		return project;
	}

	/**
	 * @param project
	 *            setter, see {@link project}
	 */
	public void setProject(URL project) {
		this.project = project;
	}

	/**
	 * @param tumorAbbreviation
	 *            setter, see {@link tumorAbbreviation}
	 */
	public void setTumorAbbreviation(String tumorAbbreviation) {
		this.tumorAbbreviation = tumorAbbreviation;
	}

	/**
	 * @param tumorName
	 *            setter, see {@link tumorName}
	 */
	public void setTumorName(String tumorName) {
		this.tumorName = tumorName;
	}

	/**
	 * @return the parent, see {@link #parent}
	 */
	public Run getParent() {
		return parent;
	}

	/**
	 * @param parent
	 *            setter, see {@link parent}
	 */
	public void setParent(Run parent) {
		this.parent = parent;
	}

	@Override
	public int compareTo(TumorProject o) {
		return tumorAbbreviation.compareToIgnoreCase(o.tumorAbbreviation);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tumorAbbreviation == null) ? 0 : tumorAbbreviation.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TumorProject other = (TumorProject) obj;
		if (tumorAbbreviation == null) {
			if (other.tumorAbbreviation != null)
				return false;
		} else if (!tumorAbbreviation.equals(other.tumorAbbreviation))
			return false;
		return true;
	}

}
