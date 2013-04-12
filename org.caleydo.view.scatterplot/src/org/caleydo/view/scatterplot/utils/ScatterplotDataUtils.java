/**
 * 
 */
package org.caleydo.view.scatterplot.utils;

import java.util.ArrayList;

import org.caleydo.view.scatterplot.GLScatterplot;

/**
 * @author turkay
 *
 */
public class ScatterplotDataUtils {
	
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
	
}
