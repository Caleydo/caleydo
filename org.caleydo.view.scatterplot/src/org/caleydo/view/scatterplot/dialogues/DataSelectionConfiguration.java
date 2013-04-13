/**
 * 
 */
package org.caleydo.view.scatterplot.dialogues;

import java.util.List;
import java.util.ArrayList;

import org.caleydo.view.scatterplot.utils.EDataGenerationType;
import org.caleydo.view.scatterplot.utils.EVisualizationSpaceType;

/**
 * A class to hold which/what type of data columns have been selected through the GUI
 * 
 * @author cagatay turkay
 *
 */
public class DataSelectionConfiguration {
	
	
	private EDataGenerationType dataResourceType;
	private EVisualizationSpaceType visSpaceType;
	private List<Integer> axisIDs = new ArrayList<>();
	private List<String> axisLabels = new ArrayList<>();
	
	public DataSelectionConfiguration() {
	}

	/**
	 * @return the dataResourceType
	 */
	public EDataGenerationType getDataResourceType() {
		return dataResourceType;
	}

	/**
	 * @param dataResourceType the dataResourceType to set
	 */
	public void setDataResourceType(EDataGenerationType dataResourceType) {
		this.dataResourceType = dataResourceType;
	}

	/**
	 * @return the visSpaceType
	 */
	public EVisualizationSpaceType getVisSpaceType() {
		return visSpaceType;
	}

	/**
	 * @param visSpaceType the visSpaceType to set
	 */
	public void setVisSpaceType(EVisualizationSpaceType visSpaceType) {
		this.visSpaceType = visSpaceType;
	}

	/**
	 * @return the axisIDs
	 */
	public List<Integer> getAxisIDs() {
		return axisIDs;
	}

	/**
	 * @param axisIDs the axisIDs to set
	 */
	public void setAxisIDs(List<Integer> axisIDs) {
		this.axisIDs = axisIDs;
	}

	/**
	 * @return the axisLabels
	 */
	public List<String> getAxisLabels() {
		return axisLabels;
	}

	/**
	 * @param axisLabels the axisLabels to set
	 */
	public void setAxisLabels(List<String> axisLabels) {
		this.axisLabels = axisLabels;
	}
}
