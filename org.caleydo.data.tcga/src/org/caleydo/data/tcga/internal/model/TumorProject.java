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
}