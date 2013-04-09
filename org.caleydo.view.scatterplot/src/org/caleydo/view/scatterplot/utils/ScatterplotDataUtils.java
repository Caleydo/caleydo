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
	
	public static ArrayList<Integer> findSelectedElements(GLScatterplot view, SelectionRectangle rect)
	{
		ArrayList<Integer> result = new ArrayList<>();
				
		for (int i = 0 ; i < view.getDataColumns().get(0).size(); i++)
		{
			float xVal = view.getDataColumns().get(0).get(i);
			float yVal = view.getDataColumns().get(1).get(i);
			if(xVal >= rect.getxMin() & xVal <= rect.getxMax() & yVal >= rect.getyMin() & yVal <= rect.getyMax())
			{
				result.add(i);
			}
		}
		
		return result;
	}
	
}
