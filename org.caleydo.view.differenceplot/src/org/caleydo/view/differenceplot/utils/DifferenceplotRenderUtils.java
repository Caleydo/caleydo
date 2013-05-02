package org.caleydo.view.differenceplot.utils;


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
import org.caleydo.view.differenceplot.GLDifferenceplot;
import org.caleydo.view.differenceplot.DifferenceplotElement;

public class DifferenceplotRenderUtils {
	
	

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
	 * is padded to the bottom and left of the differenceplot
	 */
	private float sideSpacing;
	
	private float xRange;
	private float yRange;
	
	private float POINT_SIZE_MULTIPLIER = 0.4f;
	private float POINT_BORDER_SIZE_MULTIPLIER = 0.45f;
	
	
	private ArrayList<Integer> idList;
	
	public DifferenceplotRenderUtils() {
		super();
	}
	
	public void render(GL2 gl, DifferenceplotElement differenceplotElement, float width, float height)
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
		
		if(differenceplotElement.isRenderRemote())
		{
			renderLowDetail(gl, differenceplotElement, width, height, differenceplotElement.getSignificanceDiffFlagMean(), differenceplotElement.getSignificanceDiffFlagVariance());
		}
		else
		{
			renderHighDetail(gl, differenceplotElement, width, height);
		}
		
	}
	
	/**
	 * This function builds the id list of the items visualized in the view
	 * This could be a list of record or dimension IDs
	 * 
	 * @param differenceplotElement
	 */
	public void buildIDList(DifferenceplotElement differenceplotElement)
	{
		// First compute items id list based on visualization type
		idList = new ArrayList<Integer>();
		for (int i = 0 ; i < differenceplotElement.getDataColumns().get(0).size(); i++)
		{
			int itemID = -1;
			if (differenceplotElement.getDataSelectionConf().getVisSpaceType() == EVisualizationSpaceType.ITEMS_SPACE)
			{
				itemID = differenceplotElement.getSelection().getTablePerspective().getRecordPerspective().getVirtualArray().get(i);
			}
			else if (differenceplotElement.getDataSelectionConf().getVisSpaceType() == EVisualizationSpaceType.DIMENSIONS_SPACE)
			{
				itemID = differenceplotElement.getSelection().getTablePerspective().getDimensionPerspective().getVirtualArray().get(i);
			}
			idList.add(itemID);
		}
	}
	
	/**
	 * This function returns the selected record/dimension IDs
	 * to consider in the statistics computations. If the view is a dimension visualization,
	 * the selection to consider is over items. And if the view is an items visualization,
	 * the selection to consider is over dimensions.
	 * 
	 * @param differenceplotElement
	 * @return
	 */
	public ArrayList<Integer> buildSelectedIDList(DifferenceplotElement differenceplotElement)
	{
		ArrayList<Integer> selectedIDList = new ArrayList<Integer>();
		
		SelectionManager selectionManager;
		VirtualArray dataVA;
		
		// If the view is an items view, the selection of dimensions will be effective on
		// the statistics computation. 
		if (differenceplotElement.getDataSelectionConf().getVisSpaceType() == EVisualizationSpaceType.ITEMS_SPACE)
		{
			selectionManager = differenceplotElement.getSelection().getDimensionSelectionManager();
			dataVA = differenceplotElement.getTablePerspective().getDimensionPerspective().getVirtualArray();
		}
		// If the view is a dimensions view, the selection of items will be effective on
		// the statistics computation. 
		else
		{
			selectionManager = differenceplotElement.getSelection().getRecordSelectionManager();
			dataVA = differenceplotElement.getTablePerspective().getRecordPerspective().getVirtualArray();
		}
		
		// No selection, return null
		if (selectionManager.getNumberOfElements(SelectionType.SELECTION) == 0)
		{
			return null;
		}
		
		//VirtualArray recordVA = differenceplotElement.getTablePerspective().getRecordPerspective().getVirtualArray();
		//VirtualArray dimensionVA = differenceplotElement.getTablePerspective().getDimensionPerspective().getVirtualArray();
		
		//differenceplotElement.getDataSelectionConf().getVisSpaceType() == EVisualizationSpaceType.ITEMS_SPACE
		
		//for (int i = 0 ; i < dataVA.size(); i++)
		for (Integer itemID: dataVA)
		{
			//int recordID = scatterplotElement.getSelection().getTablePerspective().getRecordPerspective().getVirtualArray().get(i);
			//Integer itemID = dataVA.get(i);
			if(selectionManager.checkStatus(SelectionType.SELECTION, itemID))
			{			
				selectedIDList.add(itemID);
			}
		}
		
		return selectedIDList;
	}
	
	
	
	public void PerformDataLoadedOperations(DifferenceplotElement differenceplotElement)
	{	
		buildIDList(differenceplotElement);
		computeDataRangeConstants(differenceplotElement.getDataColumns());
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
	
	private void renderHighDetail(GL2 gl, DifferenceplotElement differenceplotElement, float width, float height)
	{
		// Now set the selection manager depending on whether the view shows items or dimensions
		SelectionManager selectionManager = differenceplotElement.getSelection().getRecordSelectionManager();
		float pointSize = computePointSizeAdaptively(width, height, differenceplotElement.getDataColumns().get(0).size(), POINT_SIZE_MULTIPLIER);
		float pointBorderSize = computePointSizeAdaptively(width, height, differenceplotElement.getDataColumns().get(0).size(), POINT_BORDER_SIZE_MULTIPLIER);
		
		if (differenceplotElement.getDataSelectionConf().getVisSpaceType() == EVisualizationSpaceType.DIMENSIONS_SPACE)
		{
			selectionManager = differenceplotElement.getSelection().getDimensionSelectionManager();
		}
		
		boolean anyItemSelected = false;
		
		if (selectionManager.getNumberOfElements(SelectionType.SELECTION) != 0)
		{
			anyItemSelected = true;
		}
		
		gl.glColor4f( (float) (200/255.0), (float) (200/255.0), (float) (200/255.0), 0.2f);
		
		gl.glBegin(GL2.GL_QUADS);
		
		gl.glVertex2f(0, 0);
        gl.glVertex2f(width, 0);
        gl.glVertex2f(width, height);
        gl.glVertex2f(0, height);
		
		gl.glEnd();
		
		renderAxes(gl, width, height);
		
		gl.glPointSize(pointBorderSize);
        gl.glEnable(GL2.GL_POINT_SMOOTH);
        
        // First render the context s.t. the highlighted are placed before the selected
        gl.glColor4f( (float) (200/255.0), (float) (200/255.0), (float) (200/255.0), 0.5f);
		gl.glBegin(GL2.GL_POINTS);
		for (int i = 0 ; i < differenceplotElement.getDataColumns().get(0).size(); i++)
		{
			//int recordID = scatterplotElement.getSelection().getTablePerspective().getRecordPerspective().getVirtualArray().get(i);
			if(!selectionManager.checkStatus(SelectionType.SELECTION, idList.get(i)))
			{
		        gl.glVertex3f((differenceplotElement.getDataColumns().get(0).get(i) - xMin) / xRange * xScale + sideSpacing, height - ((differenceplotElement.getDataColumns().get(1).get(i) - yMin) / yRange * yScale + sideSpacing ), 0);   
			}			
		}
		gl.glEnd();
		
		// Now render the selected items with their outline
		
		gl.glPointSize(pointBorderSize);
		gl.glColor3f( 0,  0, 0);
		gl.glBegin(GL2.GL_POINTS);
		for (int i = 0 ; i < differenceplotElement.getDataColumns().get(0).size() ; i++)
		{
			//int recordID = scatterplotElement.getSelection().getTablePerspective().getRecordPerspective().getVirtualArray().get(i);
			//if(!view.getSelectionManager().checkStatus(SelectionType.DESELECTED, recordID))
			if(selectionManager.checkStatus(SelectionType.SELECTION, idList.get(i)) | !anyItemSelected)			
			{
				gl.glVertex3f((differenceplotElement.getDataColumns().get(0).get(i) - xMin) / xRange * xScale + sideSpacing, height - ((differenceplotElement.getDataColumns().get(1).get(i) - yMin) / yRange * yScale + sideSpacing) , 0);		        
			}			
		}
		gl.glEnd();
		
		gl.glColor3f( (float) (253/255.0), (float) (122/255.0), (float) (55/255.0));
        gl.glPointSize(pointSize);
		gl.glBegin(GL2.GL_POINTS);
		for (int i = 0 ; i < differenceplotElement.getDataColumns().get(0).size() ; i++)
		{
			//int recordID = scatterplotElement.getSelection().getTablePerspective().getRecordPerspective().getVirtualArray().get(i);
			if(selectionManager.checkStatus(SelectionType.SELECTION, idList.get(i)) | !anyItemSelected)			
			{
				gl.glVertex3f((differenceplotElement.getDataColumns().get(0).get(i) - xMin) / xRange * xScale + sideSpacing, height - ((differenceplotElement.getDataColumns().get(1).get(i) - yMin) / yRange * yScale + sideSpacing), 0);		        
			}			
		}
		gl.glEnd();		
	}
	
	private void renderLowDetail(GL2 gl, DifferenceplotElement differenceplotElement, float width, float height, ArrayList<Boolean> significanceDiffFlags1, ArrayList<Boolean> significanceDiffFlags2)
	{
		// Now set the selection manager depending on whether the view shows items or dimensions
		SelectionManager selectionManager = differenceplotElement.getSelection().getRecordSelectionManager();
		float pointSize = computePointSizeAdaptively(width, height, differenceplotElement.getDataColumns().get(0).size(), POINT_SIZE_MULTIPLIER);
		float pointBorderSize = computePointSizeAdaptively(width, height, differenceplotElement.getDataColumns().get(0).size(), POINT_BORDER_SIZE_MULTIPLIER);
		
		if (differenceplotElement.getDataSelectionConf().getVisSpaceType() == EVisualizationSpaceType.DIMENSIONS_SPACE)
		{
			selectionManager = differenceplotElement.getSelection().getDimensionSelectionManager();
		}
		
		boolean anyItemSelected = false;
		
		if (selectionManager.getNumberOfElements(SelectionType.SELECTION) != 0)
		{
			anyItemSelected = true;
		}
		
		gl.glColor4f( (float) (200/255.0), (float) (200/255.0), (float) (200/255.0), 0.2f);
		
		gl.glBegin(GL2.GL_QUADS);
		
		gl.glVertex2f(0, 0);
        gl.glVertex2f(width, 0);
        gl.glVertex2f(width, height);
        gl.glVertex2f(0, height);
		
		gl.glEnd();
		
		renderAxes(gl, width, height);
		
		gl.glPointSize(pointBorderSize);
        gl.glEnable(GL2.GL_POINT_SMOOTH);
        
//        // First render the context s.t. the highlighted are placed before the selected
//        gl.glColor4f( (float) (200/255.0), (float) (200/255.0), (float) (200/255.0), 0.5f);
//		gl.glBegin(GL2.GL_POINTS);
//		for (int i = 0 ; i < differenceplotElement.getDataColumns().get(0).size(); i++)
//		{
//			//int recordID = scatterplotElement.getSelection().getTablePerspective().getRecordPerspective().getVirtualArray().get(i);
//			if(!selectionManager.checkStatus(SelectionType.SELECTION, idList.get(i)))
//			{
//		        gl.glVertex3f((differenceplotElement.getDataColumns().get(0).get(i) - xMin) / xRange * xScale + sideSpacing, height - ((differenceplotElement.getDataColumns().get(1).get(i) - yMin) / yRange * yScale + sideSpacing ), 0);   
//			}			
//		}
//		gl.glEnd();
		
		// Now render the selected items with their outline
		
		gl.glPointSize(pointBorderSize);
		gl.glColor3f( 0,  0, 0);
		gl.glBegin(GL2.GL_POINTS);
		for (int i = 0 ; i < differenceplotElement.getDataColumns().get(0).size() ; i++)
		{
			//int recordID = scatterplotElement.getSelection().getTablePerspective().getRecordPerspective().getVirtualArray().get(i);
			//if(!view.getSelectionManager().checkStatus(SelectionType.DESELECTED, recordID))
			if(selectionManager.checkStatus(SelectionType.SELECTION, idList.get(i)) | !anyItemSelected)			
			{
				if(significanceDiffFlags1.get(i)| significanceDiffFlags2.get(i))
				{
					gl.glVertex3f((differenceplotElement.getDataColumns().get(0).get(i) - xMin) / xRange * xScale + sideSpacing, height - ((differenceplotElement.getDataColumns().get(1).get(i) - yMin) / yRange * yScale + sideSpacing) , 0);		        
				}	
			}	
		}
		gl.glEnd();
		
		
        gl.glPointSize(pointSize);
		
		for (int i = 0 ; i < differenceplotElement.getDataColumns().get(0).size() ; i++)
		{
			//int recordID = scatterplotElement.getSelection().getTablePerspective().getRecordPerspective().getVirtualArray().get(i);
			if(selectionManager.checkStatus(SelectionType.SELECTION, idList.get(i)) | !anyItemSelected)			
			{
				gl.glBegin(GL2.GL_POINTS);
				if(significanceDiffFlags1.get(i)| significanceDiffFlags2.get(i))
				{
					gl.glColor4f( (float) (253/255.0), (float) (122/255.0), (float) (55/255.0), (float) (255/255.0));
				}
				else
				{
					//gl.glColor4f( (float) (253/255.0), (float) (122/255.0), (float) (55/255.0), (float) (100/255.0));
					gl.glColor4f( (float) (200/255.0), (float) (200/255.0), (float) (200/255.0), 0.2f);
				}
				gl.glVertex3f((differenceplotElement.getDataColumns().get(0).get(i) - xMin) / xRange * xScale + sideSpacing, height - ((differenceplotElement.getDataColumns().get(1).get(i) - yMin) / yRange * yScale + sideSpacing), 0);
				gl.glEnd();		
			}			
		}
		
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
        
        float xPnt = (0 - xMin) / xRange * xScale + sideSpacing;
        
        gl.glVertex2f(xPnt, 0);
        gl.glVertex2f(xPnt, height);
        
        float yPnt = height - ((0 - yMin) / yRange * yScale + sideSpacing);
        
        gl.glVertex2f(0, yPnt);
        gl.glVertex2f(width, yPnt);

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
	
	public void performBrushing(DifferenceplotElement scatterplotElement, SelectionRectangle rect)
	{
		ArrayList<Integer> selectedIDs = DifferenceplotDataUtils.findSelectedElements(scatterplotElement.getDataColumns(), rect);
		
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
	
	public void clearSelection(DifferenceplotElement scatterplotElement)
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
