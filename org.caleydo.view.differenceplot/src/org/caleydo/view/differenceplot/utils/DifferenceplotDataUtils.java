/**
 * 
 */
package org.caleydo.view.differenceplot.utils;

import java.util.ArrayList;

import org.caleydo.core.data.perspective.table.EStatisticsType;
import org.caleydo.view.differenceplot.GLDifferenceplot;
import org.caleydo.view.differenceplot.dialogues.DataSelectionConfiguration;

/**
 * @author turkay
 *
 */
public class DifferenceplotDataUtils {
	
	public static ArrayList<Integer> findSelectedElements(ArrayList<ArrayList<Float>> dataColumns, SelectionRectangle rect)
	{
		ArrayList<Integer> result = new ArrayList<>();
				
		for (int i = 0 ; i < dataColumns.get(0).size(); i++)
		{
			float xVal = dataColumns.get(0).get(i);
			float yVal = dataColumns.get(1).get(i);
			if(xVal >= rect.getxMin() & xVal <= rect.getxMax() & yVal >= rect.getyMin() & yVal <= rect.getyMax())
			{
				result.add(i);
			}
		}
		
		return result;
	}
	
	/**
	 * 
	 * @return
	 */
	public static DataSelectionConfiguration buildDefaultDataSelection()
	{
		DataSelectionConfiguration dataSelectionConf = new DataSelectionConfiguration();
		int configurationOption = 0;
		switch (configurationOption) {
		case 0:
			// View will show derived data
			// It will display median vs. IQR as default
			// It will be a dimension visualization
			
			
			// Set the selected Axis IDs to pass to the view
			ArrayList<Integer> axisIDs = new ArrayList<>();
			 
			axisIDs.add((Integer) EStatisticsType.MEDIAN.ordinal());
			axisIDs.add((Integer) EStatisticsType.IQR.ordinal());
			
			dataSelectionConf.setAxisIDs(axisIDs);
			
			// Set the selected labels to pass to the view
			ArrayList<String> axisLabels = new ArrayList<>();
			 
			axisLabels.add( EStatisticsType.MEDIAN.name() );
			axisLabels.add( EStatisticsType.IQR.name() );
			
			dataSelectionConf.setAxisLabels(axisLabels);
			
			// Set the selected vis domain
			dataSelectionConf.setVisSpaceType(EVisualizationSpaceType.DIMENSIONS_SPACE);
			
			// Set the selected data generation domain, e.g., raw or derived
			dataSelectionConf.setDataResourceType(EDataGenerationType.DERIVED_DATA);		

			
			break;

		default:
			break;
		}
		return dataSelectionConf;
	}
	
}
