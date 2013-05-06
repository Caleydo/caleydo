package org.caleydo.view.scatterplot.utils;


import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.TablePerspectiveSelectionMixin;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.view.scatterplot.GLScatterplot;
import org.caleydo.view.scatterplot.ScatterplotElement;
import org.caleydo.view.scatterplot.renderstyle.ScatterplotRenderStyle;

public class ScatterplotRenderUtils {
	
	

	/**
	 * minimum value for the x-Axis
	 */
	private float xMin;
	/**
	 * minimum value for the y-Axis
	 */
	private float yMin;
	
	/**
	 * maximum value for the x-Axis
	 */
	private float xMax;
	/**
	 * maximum value for the y-Axis
	 */
	private float yMax;
	
	
	private float xScale;
	private float yScale;
	
	/**
	 * Parameter to define how much empty space
	 * is padded to the bottom and left of the scatterplot
	 */
	private float sideSpacing;
	
	private float xRange;
	private float yRange;
	
	private float POINT_SIZE_MULTIPLIER = 0.4f;
	private float POINT_BORDER_SIZE_MULTIPLIER = 0.45f;
	
	
	private ArrayList<Integer> idList;
	
	public ScatterplotRenderUtils() {
		super();
	}
	
	public void render(GL2 gl, ScatterplotElement scatterplotElement, float width, float height)
	{		
		//TODO: No need to call every render cycle, can be called at only resize events
		findScreenMappingConstants(width, height);
		
		//TODO: fix this!
//		if (view.getDetailLevel() == EDetailLevel.HIGH) 
//		{
//			renderHighDetail(gl, view);
//		}
//		else if (view.getDetailLevel() == EDetailLevel.LOW || view.getDetailLevel() == EDetailLevel.MEDIUM)
//		{
//			
//		}
		if(scatterplotElement.isRenderRemote())
		{
			renderLowDetail(gl, scatterplotElement, width, height);
		}
		else
		{
			renderHighDetail(gl, scatterplotElement, width, height);
		}
		
		
	}
	
	public void PerformDataLoadedOperations(ScatterplotElement scatterplotElement)
	{
		// First compute items id list based on visualization type
		idList = new ArrayList<Integer>();
		for (int i = 0 ; i < scatterplotElement.getDataColumns().get(0).size(); i++)
		{
			int itemID = -1;
			if (scatterplotElement.getDataSelectionConf().getVisSpaceType() == EVisualizationSpaceType.ITEMS_SPACE)
			{
				itemID = scatterplotElement.getSelection().getTablePerspective().getRecordPerspective().getVirtualArray().get(i);
			}
			else if (scatterplotElement.getDataSelectionConf().getVisSpaceType() == EVisualizationSpaceType.DIMENSIONS_SPACE)
			{
				itemID = scatterplotElement.getSelection().getTablePerspective().getDimensionPerspective().getVirtualArray().get(i);
			}
			idList.add(itemID);
		}
		
		computeDataRangeConstants(scatterplotElement.getDataColumns());
	}
	
	
	/**
	 * This function is called after the data is loaded 
	 * It computes min and max values and data ranges for using later in screen to data mappings 
	 * 
	 * Call this function every time the data mapping is changed
	 * @param dataColumns
	 */
	public void computeDataRangeConstants(ArrayList<ArrayList<Float>> dataColumns)
	{
		xMin =  Collections.min(dataColumns.get(0));
		xMax =  Collections.max(dataColumns.get(0));
		yMin =  Collections.min(dataColumns.get(1));
		yMax =  Collections.max(dataColumns.get(1));
		
		xRange = xMax - xMin;
		yRange = yMax - yMin;
	}
	
	/**
	 * Computes screen to data scaling factors
	 * Needs to be called  every time screen size is changed
	 *
	 * @param width
	 * @param height
	 */
	private void findScreenMappingConstants(float width, float height)
	{	
		sideSpacing = 15.0f; //view.getParentComposite(). getPixelGLConverter().getGLWidthForPixelWidth(ScatterplotRenderStyle.SIDE_SPACING);
		
		xScale = (width - (2.0f * sideSpacing));
		yScale = (height - (2.0f * sideSpacing));
	}
	
	
	public Point2D.Float findScreenToDataMapping(Point2D.Float pnt, ArrayList<ArrayList<Float>> dataColumns, float width, float height)
	{
		Point2D.Float dataPoint = new Point2D.Float();
		
		dataPoint.x = ((float) pnt.x - sideSpacing) / xScale * xRange + xMin;
		dataPoint.y = (height - sideSpacing - (float) pnt.y) / yScale * yRange + yMin;
		
		return dataPoint;
	}
	
	private float computePointSizeAdaptively(float width, float height, int pointCount, float pointMultiplier)
	{		
		float minSize = Math.min(width, height);
		float result = 1.0f * pointMultiplier * ((float) Math.pow(minSize, 0.5)) ;
		return result;
	}
	
	private void renderHighDetail(GL2 gl, ScatterplotElement scatterplotElement, float width, float height)
	{
		// Now set the selection manager depending on whether the view shows items or dimensions
		SelectionManager selectionManager = scatterplotElement.getSelection().getRecordSelectionManager();
		float pointSize = computePointSizeAdaptively(width, height, scatterplotElement.getDataColumns().get(0).size(), POINT_SIZE_MULTIPLIER);
		float pointBorderSize = computePointSizeAdaptively(width, height, scatterplotElement.getDataColumns().get(0).size(), POINT_BORDER_SIZE_MULTIPLIER);
		
		if (scatterplotElement.getDataSelectionConf().getVisSpaceType() == EVisualizationSpaceType.DIMENSIONS_SPACE)
		{
			selectionManager = scatterplotElement.getSelection().getDimensionSelectionManager();
		}
		
		boolean anyItemSelected = false;
		
		if (selectionManager.getNumberOfElements(SelectionType.SELECTION) != 0)
		{
			anyItemSelected = true;
		}
		
		/**
		 * First render a quad as the background of the scene.
		 * This enables the screen to be pickable
		 * One TODO is to update this to be an own GL_ELEMENT 
		 */
		if (scatterplotElement.getDataSelectionConf().getVisSpaceType() == EVisualizationSpaceType.ITEMS_SPACE)
		{
			gl.glColor4f( (float) (252.0/255.0), (float) (254.0/255.0), (float) (187.0/255.0), 1.0f);			
		}
		else
		{
			gl.glColor4f( (float) (218.0/255.0), (float) (231.0/255.0), (float) (253.0/255.0), 1.0f);
		}
		
		gl.glBegin(GL2.GL_QUADS);
		
		gl.glVertex2f(0, 0);
        gl.glVertex2f(width, 0);
        gl.glVertex2f(width, height);
        gl.glVertex2f(0, height);
		
		gl.glEnd();
		
		/**
		 * Render Axes
		 */
		renderAxes(gl, width, height);
		
		/**
		 * Render data points
		 */
		gl.glPointSize(pointBorderSize);
        gl.glEnable(GL2.GL_POINT_SMOOTH);
        
        // First render the context s.t. the highlighted are placed before the selected
        gl.glColor4f( (float) (200/255.0), (float) (200/255.0), (float) (200/255.0), 0.5f);
		gl.glBegin(GL2.GL_POINTS);
		for (int i = 0 ; i < scatterplotElement.getDataColumns().get(0).size(); i++)
		{
			if(!selectionManager.checkStatus(SelectionType.SELECTION, idList.get(i)))
			{
		        gl.glVertex3f((scatterplotElement.getDataColumns().get(0).get(i) - xMin) / xRange * xScale + sideSpacing, height - ((scatterplotElement.getDataColumns().get(1).get(i) - yMin) / yRange * yScale + sideSpacing ), 0);   
			}			
		}
		gl.glEnd();
		
		// Now render the selected items with their outline
		
		gl.glPointSize(pointBorderSize);
		gl.glColor3f( 0,  0, 0);
		gl.glBegin(GL2.GL_POINTS);
		for (int i = 0 ; i < scatterplotElement.getDataColumns().get(0).size() ; i++)
		{
			if(selectionManager.checkStatus(SelectionType.SELECTION, idList.get(i)) | !anyItemSelected)			
			{
				gl.glVertex3f((scatterplotElement.getDataColumns().get(0).get(i) - xMin) / xRange * xScale + sideSpacing, height - ((scatterplotElement.getDataColumns().get(1).get(i) - yMin) / yRange * yScale + sideSpacing) , 0);		        
			}			
		}
		gl.glEnd();
		
		gl.glColor3f( (float) (253/255.0), (float) (122/255.0), (float) (55/255.0));
        gl.glPointSize(pointSize);
		gl.glBegin(GL2.GL_POINTS);
		for (int i = 0 ; i < scatterplotElement.getDataColumns().get(0).size() ; i++)
		{
			if(selectionManager.checkStatus(SelectionType.SELECTION, idList.get(i)) | !anyItemSelected)			
			{
				gl.glVertex3f((scatterplotElement.getDataColumns().get(0).get(i) - xMin) / xRange * xScale + sideSpacing, height - ((scatterplotElement.getDataColumns().get(1).get(i) - yMin) / yRange * yScale + sideSpacing), 0);		        
			}			
		}
		gl.glEnd();		
	}
	
	/**
	 * Renders the scatterplot in lower detail
	 * Currently, only the axes is not rendered.
	 * @param gl
	 * @param scatterplotElement
	 * @param width
	 * @param height
	 */
	private void renderLowDetail(GL2 gl, ScatterplotElement scatterplotElement, float width, float height)
	{
		// Now set the selection manager depending on whether the view shows items or dimensions
		SelectionManager selectionManager = scatterplotElement.getSelection().getRecordSelectionManager();
		float pointSize = computePointSizeAdaptively(width, height, scatterplotElement.getDataColumns().get(0).size(), POINT_SIZE_MULTIPLIER);
		float pointBorderSize = computePointSizeAdaptively(width, height, scatterplotElement.getDataColumns().get(0).size(), POINT_BORDER_SIZE_MULTIPLIER);
		
		if (scatterplotElement.getDataSelectionConf().getVisSpaceType() == EVisualizationSpaceType.DIMENSIONS_SPACE)
		{
			selectionManager = scatterplotElement.getSelection().getDimensionSelectionManager();
		}
		
		boolean anyItemSelected = false;
		
		if (selectionManager.getNumberOfElements(SelectionType.SELECTION) != 0)
		{
			anyItemSelected = true;
		}
		
		/**
		 * First render a quad as the background of the scene.
		 * This enables the screen to be pickable
		 * One TODO is to update this to be an own GL_ELEMENT 
		 */
		if (scatterplotElement.getDataSelectionConf().getVisSpaceType() == EVisualizationSpaceType.ITEMS_SPACE)
		{
			gl.glColor4f( (float) (252.0/255.0), (float) (254.0/255.0), (float) (187.0/255.0), 1.0f);			
		}
		else
		{
			gl.glColor4f( (float) (218.0/255.0), (float) (231.0/255.0), (float) (253.0/255.0), 1.0f);
		}
		
		gl.glBegin(GL2.GL_QUADS);
		
		gl.glVertex2f(0, 0);
        gl.glVertex2f(width, 0);
        gl.glVertex2f(width, height);
        gl.glVertex2f(0, height);
		
		gl.glEnd();
		
		/**
		 * Render Axes - not enabled in low detail mode
		 */
		//renderAxes(gl, width, height);
		
		/**
		 * Render data points
		 */
		gl.glPointSize(pointBorderSize);
        gl.glEnable(GL2.GL_POINT_SMOOTH);
        
        // First render the context s.t. the highlighted are placed before the selected
        gl.glColor4f( (float) (200/255.0), (float) (200/255.0), (float) (200/255.0), 0.5f);
		gl.glBegin(GL2.GL_POINTS);
		for (int i = 0 ; i < scatterplotElement.getDataColumns().get(0).size(); i++)
		{
			if(!selectionManager.checkStatus(SelectionType.SELECTION, idList.get(i)))
			{
		        gl.glVertex3f((scatterplotElement.getDataColumns().get(0).get(i) - xMin) / xRange * xScale + sideSpacing, height - ((scatterplotElement.getDataColumns().get(1).get(i) - yMin) / yRange * yScale + sideSpacing ), 0);   
			}			
		}
		gl.glEnd();
		
		// Now render the selected items with their outline
		
		gl.glPointSize(pointBorderSize);
		gl.glColor3f( 0,  0, 0);
		gl.glBegin(GL2.GL_POINTS);
		for (int i = 0 ; i < scatterplotElement.getDataColumns().get(0).size() ; i++)
		{
			if(selectionManager.checkStatus(SelectionType.SELECTION, idList.get(i)) | !anyItemSelected)			
			{
				gl.glVertex3f((scatterplotElement.getDataColumns().get(0).get(i) - xMin) / xRange * xScale + sideSpacing, height - ((scatterplotElement.getDataColumns().get(1).get(i) - yMin) / yRange * yScale + sideSpacing) , 0);		        
			}			
		}
		gl.glEnd();
		
		gl.glColor3f( (float) (253/255.0), (float) (122/255.0), (float) (55/255.0));
        gl.glPointSize(pointSize);
		gl.glBegin(GL2.GL_POINTS);
		for (int i = 0 ; i < scatterplotElement.getDataColumns().get(0).size() ; i++)
		{
			if(selectionManager.checkStatus(SelectionType.SELECTION, idList.get(i)) | !anyItemSelected)			
			{
				gl.glVertex3f((scatterplotElement.getDataColumns().get(0).get(i) - xMin) / xRange * xScale + sideSpacing, height - ((scatterplotElement.getDataColumns().get(1).get(i) - yMin) / yRange * yScale + sideSpacing), 0);		        
			}			
		}
		gl.glEnd();		
	}
	
	/**
	 * This function renders the two perpendicular axes lines passing through the min. values of x and y
	 * @param gl
	 * @param view
	 */
	private void renderAxes(GL2 gl, float width, float height)
	{
		gl.glColor4f(0,0,0, 0.3f);
        gl.glEnable(GL2.GL_LINE_STIPPLE);
        gl.glLineWidth(2.0f);
        gl.glLineStipple(8, (short) 0xAAAA);
        gl.glBegin(GL2.GL_LINES);
        
        gl.glVertex2f(sideSpacing, 0);
        gl.glVertex2f(sideSpacing, height);
        
        gl.glVertex2f(0, height - sideSpacing);
        gl.glVertex2f(width, height - sideSpacing);

        gl.glEnd();
	}
	
	
	public void renderSelectionRectangle(GL2 gl, SelectionRectangle rect, float w, float h)
	{
		if (rect == null)
		{
			return;
		}
		
		gl.glEnable(gl.GL_LINE_STIPPLE);
        gl.glPolygonMode(gl.GL_FRONT_AND_BACK, gl.GL_LINE);
        gl.glLineStipple(2, (short) 0x1C47);
        
        gl.glColor3f( (float) 0, (float) 0, (float) 0);
        
		gl.glBegin(GL2.GL_QUADS);
		
		gl.glVertex2f(rect.getLeft(), rect.getTop());
        gl.glVertex2f(rect.getRight(), rect.getTop());
        gl.glVertex2f(rect.getRight(), rect.getBottom());
        gl.glVertex2f(rect.getLeft(), rect.getBottom());
		
		gl.glEnd();
		
		gl.glPolygonMode(gl.GL_FRONT_AND_BACK, gl.GL_FILL);
	}
	
	public void performBrushing(ScatterplotElement scatterplotElement, SelectionRectangle rect)
	{
		ArrayList<Integer> selectedIDs = ScatterplotDataUtils.findSelectedElements(scatterplotElement.getDataColumns(), rect);
		
		SelectionManager selectionManager = scatterplotElement.getSelection().getRecordSelectionManager();
		if (scatterplotElement.getDataSelectionConf().getVisSpaceType() == EVisualizationSpaceType.DIMENSIONS_SPACE)
		{
			selectionManager = scatterplotElement.getSelection().getDimensionSelectionManager();
		}
		
		selectionManager.clearSelection(SelectionType.SELECTION);

		for (Integer selectedID: selectedIDs )
		{
			selectionManager.addToType(SelectionType.SELECTION, idList.get(selectedID));
		}

		scatterplotElement.getSelection().fireSelectionDelta(selectionManager.getIDType());
	}
	
	public void clearSelection(ScatterplotElement scatterplotElement)
	{
		SelectionManager selectionManager = scatterplotElement.getSelection().getRecordSelectionManager();
		if (scatterplotElement.getDataSelectionConf().getVisSpaceType() == EVisualizationSpaceType.DIMENSIONS_SPACE)
		{
			selectionManager = scatterplotElement.getSelection().getDimensionSelectionManager();
		}
		
		selectionManager.clearSelection(SelectionType.SELECTION);
		
		scatterplotElement.getSelection().fireSelectionDelta(selectionManager.getIDType());
	}
	
	public boolean pickedSelectionRectangle(Point clickedPoint, SelectionRectangle rectangle)
	{
		boolean result = false;
		
		if (rectangle == null)
			return false;
		
		float yScreenMin = Math.min(rectangle.getTop(), rectangle.getBottom());
		float yScreenMax = Math.max(rectangle.getTop(), rectangle.getBottom());
		
		float xScreenMin = Math.min(rectangle.getLeft(), rectangle.getRight());
		float xScreenMax = Math.max(rectangle.getLeft(), rectangle.getRight());
		
		if(rectangle.getLeft() == -1)
		{
			return false;
		}
		
		if(clickedPoint.x >= xScreenMin & clickedPoint.x <= xScreenMax &
				clickedPoint.y >= yScreenMin & clickedPoint.y <= yScreenMax)
		{
			result = true;
		}
		
		return result;
	}
	

}
