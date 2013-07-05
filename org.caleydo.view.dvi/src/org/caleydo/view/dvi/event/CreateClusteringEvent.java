/**
 * 
 */
package org.caleydo.view.dvi.event;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.util.clusterer.gui.ClusterDialog;

/**
 * Event to trigger the {@link ClusterDialog} in order to create a
 * clustering for a datadomain.
 * 
 * @author Christian Partl
 * 
 */
public class CreateClusteringEvent extends AEvent {

	/**
	 * The datadomain a clustering shall be created for.
	 */
	private ATableBasedDataDomain dataDomain;

	/**
	 * Determines whether a dimension- or record clustering is created.
	 */
	private boolean isDimensionClustering;

	public CreateClusteringEvent() {

	}

	public CreateClusteringEvent(ATableBasedDataDomain dataDomain,
			boolean isDimensionClustering) {
		this.dataDomain = dataDomain;
		this.isDimensionClustering = isDimensionClustering;
	}

	@Override
	public boolean checkIntegrity() {
		return dataDomain != null;
	}

	/**
	 * @param dataDomain
	 *            setter, see {@link #dataDomain}
	 */
	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	/**
	 * @return the dataDomain, see {@link #dataDomain}
	 */
	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
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
