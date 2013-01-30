package org.caleydo.core.data.datadomain.event;


import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.event.AEvent;

public class CreateClusteringEvent extends AEvent {
	/**
	 * Determines whether a dimension- or record clustering is created.
	 */
	private boolean isDimensionClustering;

	public CreateClusteringEvent() {

	}

	public CreateClusteringEvent(ATableBasedDataDomain dataDomain, boolean isDimensionClustering) {
		this.setEventSpace(dataDomain.getDataDomainID());
		this.isDimensionClustering = isDimensionClustering;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

	/**
	 * @param isDimensionClustering
	 *            setter, see {@link #isDimensionClustering}
	 */
	public void setDimensionClustering(boolean isDimensionClustering) {
		this.isDimensionClustering = isDimensionClustering;
	}

	/**
	 * @return the isDimensionClustering, see {@link #isDimensionClustering}
	 */
	public boolean isDimensionClustering() {
		return isDimensionClustering;
	}
}
