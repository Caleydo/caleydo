package org.caleydo.view.scatterplot.utils;


import java.util.ArrayList;
import java.util.Collections;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.TablePerspectiveSelectionMixin;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.view.scatterplot.GLScatterplot;
import org.caleydo.view.scatterplot.renderstyle.ScatterplotRenderStyle;

public class ScatterplotRenderUtils {
	
	/**
	 * minimum value for the x-Axis
	 */
	private static float xMin;
	/**
	 * minimum value for the y-Axis
	 */
	private static float yMin;
	
	private static float xScale;
	private static float yScale;
	private static float sideSpacing;
	
	private static float xRange;
	private static float yRange;
	
	
	public static void render(GL2 gl, ArrayList<ArrayList<Float>> dataColumns, float width, float height)
	{		
		
		findDataToScreenMappings(dataColumns);
		
		//TODO: fix this!
//		if (view.getDetailLevel() == EDetailLevel.HIGH) 
//		{
//			renderHighDetail(gl, view);
//		}
//		else if (view.getDetailLevel() == EDetailLevel.LOW || view.getDetailLevel() == EDetailLevel.MEDIUM)
//		{
//			
//		}
		renderHighDetail(gl, dataColumns, width, height);
		
	}
	
	private static void findDataToScreenMappings(ArrayList<ArrayList<Float>> dataColumns)
	{
		//TODO: fix this!
		float width = 1;//view.getViewFrustum().getWidth();
		float height = 1;//view.getViewFrustum().getHeight();
		xMin =  Collections.min(dataColumns.get(0));
		float xMax =  Collections.max(dataColumns.get(0));
		yMin =  Collections.min(dataColumns.get(1));
		float yMax =  Collections.max(dataColumns.get(1));
		
		xRange = xMax - xMin;
		yRange = yMax - yMin;
		
		//TODO: fix this!
		sideSpacing = 0.1f; //view.getParentComposite(). getPixelGLConverter().getGLWidthForPixelWidth(ScatterplotRenderStyle.SIDE_SPACING);
		
		xScale = (width - (2.0f * sideSpacing));
		yScale = (height - (2.0f * sideSpacing));
	}
	
	private static void renderHighDetail(GL2 gl, ArrayList<ArrayList<Float>> dataColumns, float width, float height)
	{
//		SelectionRectangle rect = new SelectionRectangle(40.0f, 60.0f, 3.0f, 7.0f);
//		ArrayList<Integer> randSelection = ScatterplotDataUtils.findSelectedElements(dataColumns, rect);
//		
////		for (int i = 0 ; i < 300 ; i++)
////		{
////			randSelection.add( (int) (Math.random()* 1000));		
////		}
//		
//		renderAxes(gl, width, height);
//		
//		gl.glPointSize(22);
//        gl.glEnable(GL2.GL_POINT_SMOOTH);
//        
//        // First render the context s.t. the highlighted are placed before the selected
//        gl.glColor4f( (float) (200/255.0), (float) (200/255.0), (float) (200/255.0), 0.5f);
//		gl.glBegin(GL2.GL_POINTS);gl.glBegin(GL2.GL_POINTS);
//		for (int i = 0 ; i < dataColumns.get(0).size(); i++)
//		{
//			int recordID = view.getTablePerspective().getRecordPerspective().getVirtualArray().get(i);
//			if(!view.getSelectionManager().checkStatus(SelectionType.DESELECTED, recordID))
//			{
//		        gl.glVertex3f((dataColumns.get(0).get(i) - xMin) / xRange * xScale + sideSpacing, (view.getDataColumns().get(1).get(i) - yMin) / yRange * yScale + sideSpacing , 0);   
//			}			
//		}
//		gl.glEnd();
//		
//		// Now render the selected items with their outline
//		
//		gl.glPointSize(22);
//		gl.glColor3f( 0,  0, 0);
//		gl.glBegin(GL2.GL_POINTS);
//		for (int i = 0 ; i < dataColumns.get(0).size() ; i++)
//		{
//			int recordID = view.getTablePerspective().getRecordPerspective().getVirtualArray().get(i);
//			if(!view.getSelectionManager().checkStatus(SelectionType.DESELECTED, recordID))
//			{
//				gl.glVertex3f((dataColumns.get(0).get(i) - xMin) / xRange * xScale + sideSpacing, (view.getDataColumns().get(1).get(i) - yMin) / yRange * yScale + sideSpacing , 0);		        
//			}			
//		}
//		gl.glEnd();
//		
//		gl.glColor3f( (float) (253/255.0), (float) (122/255.0), (float) (55/255.0));
//        gl.glPointSize(20);
//		gl.glBegin(GL2.GL_POINTS);
//		for (int i = 0 ; i < view.getDataColumns().get(0).size() ; i++)
//		{
//			int recordID = view.getTablePerspective().getRecordPerspective().getVirtualArray().get(i);
//			if(!view.getSelectionManager().checkStatus(SelectionType.DESELECTED, recordID))
//			{
//				gl.glVertex3f((dataColumns.get(0).get(i) - xMin) / xRange * xScale + sideSpacing, (view.getDataColumns().get(1).get(i) - yMin) / yRange * yScale + sideSpacing , 0);		        
//			}			
//		}
//		gl.glEnd();		
	}
	
	/**
	 * This function renders the two perpendicular axes lines passing through the min. values of x and y
	 * @param gl
	 * @param view
	 */
	private static void renderAxes(GL2 gl, float width, float height)
	{
		gl.glColor4f(0,0,0, 0.3f);
        gl.glEnable(GL2.GL_LINE_STIPPLE);
        gl.glLineWidth(2.0f);
        gl.glLineStipple(8, (short) 0xAAAA);
        gl.glBegin(GL2.GL_LINES);
        
        gl.glVertex2f(sideSpacing, 0);
        gl.glVertex2f(sideSpacing, height);
        
        gl.glVertex2f(0, sideSpacing);
        gl.glVertex2f(width, sideSpacing);

        gl.glEnd();
	}
	

}
