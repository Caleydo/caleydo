/**
 * 
 */
package org.caleydo.view.scatterplot.event;

import org.caleydo.core.event.AEvent;
import org.caleydo.view.scatterplot.GLScatterplot;
import org.caleydo.view.scatterplot.dialogues.DataSelectionConfiguration;

/**
 * @author turkay
 *
 */
public class ScatterplotDataSelectionEvent extends AEvent{
	/**
	 * This configuration holds the information on the selected parameters
	 */
	private DataSelectionConfiguration dataSelectionConfiguration;
	
	/**
	 * Keeps track of the owner view, needed when multiple instances of scatterplots are active
	 */
	private Integer ownerViewID = -1;
	/**
	 *
	 */
	public ScatterplotDataSelectionEvent(DataSelectionConfiguration dataSelectionConfiguration, Integer ownerViewID) {
		this.dataSelectionConfiguration = dataSelectionConfiguration;
		this.ownerViewID = ownerViewID;
	}

	@Override
	public boolean checkIntegrity() {
		if (dataSelectionConfiguration != null)
			return true;

		return false;
	}
	
	/**
	 * @return the perspective, see {@link #dataSelectionConfiguration}
	 */
	public DataSelectionConfiguration getDataSelectionConfiguration() {
		return dataSelectionConfiguration;
	}

	/**
	 * @param perspective
	 *            setter, see {@link perspective}
	 */
	public void setDataSelectionConfiguration(DataSelectionConfiguration dataSelectionConfiguration) {
		this.dataSelectionConfiguration = dataSelectionConfiguration;
	}
	
	public Integer getOwnerViewID() {
		return ownerViewID;
	}

	public void setOwnerViewID(Integer ownerViewID) {
		this.ownerViewID = ownerViewID;
	}
}
