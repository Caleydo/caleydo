package org.caleydo.view.filterpipeline.representation;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;
import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;

import java.util.ArrayList;

import javax.media.opengl.GL2;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.core.view.opengl.util.vislink.NURBSCurve;
import org.caleydo.view.filterpipeline.renderstyle.FilterPipelineRenderStyle;

/**
 * @author Thomas Geymayer
 *
 */
public class FilterRepresentationMetaOrAdvanced
	extends FilterRepresentationMetaOr
{
	private int displayListOutputLines = -1;
	private boolean displayListDirty = true;
	private float oldHeightRight = 0;
	
	private static final float spacingLeft = 0.1f;
	private static final float spacingRight = 0.25f;
	private static final float subfilterScalingX = 1.f - spacingLeft - spacingRight;
	
	private static final float spacingTop = 0.08f;
	private static final float spacingBottom = 0.03f;
	private static final float subfilterScalingY = 1.f - spacingTop - spacingBottom;
	
	private static int NUMBER_OF_SPLINE_POINTS = 35;
	
	private ConnectionBandRenderer inputRenderer = new ConnectionBandRenderer();

	public FilterRepresentationMetaOrAdvanced( FilterPipelineRenderStyle renderStyle,
											   PickingManager pickingManager,
											   int viewId )
	{
		super(renderStyle, pickingManager, viewId);
	}
	
	@Override
	public void render(GL2 gl, CaleydoTextRenderer textRenderer)
	{
		if( sizesDirty )
			calculateSizes();

		heightLeft = getHeightLeft();
		heightRight = getHeightRight();
		
		if( heightRight != oldHeightRight )
		{
			displayListDirty = true;
			oldHeightRight = heightRight;
		}

		renderBasicShape(gl, textRenderer, renderStyle.FILTER_OR_COLOR);
	
		float scaleY = calculateFilterScalingY();
		float offsetY = scaleY * heightLeft;
		scaleY *= 0.9f;
		
		Vec2f curPos = new Vec2f
		(
			vPos.x() + spacingLeft * vSize.x(),
			vPos.y() + spacingBottom * heightLeft
		);
		float delta = heightLeft - heightRight;
		float subFilterWidth = subfilterScalingX * vSize.x();
		float subFilterRight = curPos.x() + subFilterWidth;
		float filterRight = vPos.x() + vSize.x();
		
		// keep track of unhandled elements
		SortedSet<Integer> remainingElements =
			new TreeSet<Integer>( elementsPassed );
		
		if( displayListDirty )
		{
			displayListDirty = false;

		}

		for( int i = 0;
			 i < subFilterSizes.length;
			 ++i, curPos.setY(curPos.y() + offsetY) )
		{
			heightRight = vSize.y() * (subFilterSizes[i]/100.f);

			gl.glPushName(pickingManager.getPickingID(viewId, EPickingType.FILTERPIPE_SUB_FILTER, i));
			renderShape
			(
				gl,
				GL2.GL_QUADS,
				curPos,
				subFilterWidth,
				scaleY * heightLeft,
				scaleY * heightRight,
				renderStyle.getFilterColor(i),
				Z_POS_BODY + 0.1f
			);
			gl.glLineWidth(1);
			renderShape
			(
				gl,
				GL2.GL_LINE_LOOP,
				curPos,
				subFilterWidth,
				scaleY * heightLeft,
				scaleY * heightRight,
				renderStyle.FILTER_BORDER_COLOR,
				Z_POS_BORDER + 0.1f
			);
			gl.glPopName();
			
			renderInputBand
			(
				gl,
				new Vec2f( vPos.x(), vPos.y() + heightLeft ),
				new Vec2f( curPos.x(), curPos.y() + scaleY * heightLeft ),
				vPos,
				curPos,
				0.7f - ((float)i/subFilterSizes.length) * 0.6f,
				delta,
				renderStyle.getFilterColorCombined(i)
			);

			if( mouseOverItem == i )
			{
				// render mouse over
				gl.glLineWidth(SelectionType.MOUSE_OVER.getLineWidth());
				renderShape
				(
					gl,
					GL2.GL_LINE_LOOP,
					curPos,
					subFilterWidth,
					scaleY * heightLeft,
					scaleY * heightRight,
					SelectionType.MOUSE_OVER.getColor(),
					Z_POS_MARK
				);
			}
			
//			if( elementsPassedAll.size() > 0 )
//			{
//				// render common output
//				renderShape
//				(
//					gl,
//					GL2.GL_QUADS,
//					new Vec2f(curPos.x() + subfilterScalingX * vSize.x(), curPos.y()),
//					vSize.x()/4.f,
//					((float)elementsPassedAll.size()/subFilterSizes[i]) * scaleY * heightRight,
//					((float)elementsPassedAll.size()/filter.getOutput().size()) * getHeightRight(),
//					vPos.y() - curPos.y(),
//					new float[]{0.2f, 0.9f, 0.2f, 0.5f},
//					Z_POS_BODY
//				);
//			}

			// get output of sub filter
			SortedSet<Integer> subFilterPassed =
				new TreeSet<Integer>( subFiltersPassedElements.get(i) );
			
			// calculate intersection with the not yet drawn elements
			subFilterPassed.retainAll(remainingElements);
			
			// get elements only passed this filter
			//for( int other = i + 1; other < subFilterSizes.length; ++other )
			for( int other = 0; other < subFilterSizes.length; ++other )
			{
				if( other != i )
					subFilterPassed.removeAll(subFiltersPassedElements.get(other));
			}
			
			float curY = vPos.y() + ((float)(elementsPassed.size() - remainingElements.size())/filter.getInput().size()) * heightLeft;
			remainingElements.removeAll(subFilterPassed);
			
			gl.glBegin(GL2.GL_QUADS);
			{
				gl.glColor4fv(new float[]{0.2f, 0.9f, 0.2f, 0.5f}, 0);
		
				gl.glVertex3f(subFilterRight, curPos.y(), Z_POS_BODY);
				gl.glVertex3f(subFilterRight, curPos.y() + ((float)subFilterPassed.size()/subFilterSizes[i]) * scaleY * heightRight, Z_POS_BODY);
				gl.glVertex3f(filterRight, curY + ((float)subFilterPassed.size()/filter.getInput().size()) * heightLeft, Z_POS_BODY);
				gl.glVertex3f(filterRight, curY, Z_POS_BODY);			
			}
			gl.glEnd();
			
			// get elements passed this filter and a second one
			for( int second = 0; second < subFilterSizes.length; ++second )
			{
				if( second == i )
					continue;
				
				// get output of sub filter
				subFilterPassed =
					new TreeSet<Integer>( subFiltersPassedElements.get(i) );
				
				// calculate intersection with the not yet drawn elements and with second filter
				subFilterPassed.retainAll(remainingElements);				
				subFilterPassed.retainAll(subFiltersPassedElements.get(second));
				
				for( int other = 0; other < subFilterSizes.length; ++other )
				{
					if( other == i || other == second )
						continue;

					subFilterPassed.removeAll(subFiltersPassedElements.get(other));
				}
				
				curY = vPos.y() + ((float)(elementsPassed.size() - remainingElements.size())/filter.getInput().size()) * heightLeft;
				remainingElements.removeAll(subFilterPassed);
				
				gl.glBegin(GL2.GL_QUADS);
				{
					gl.glColor4fv(new float[]{0.2f, 0.9f, 0.2f, 0.5f}, 0);
			
					gl.glVertex3f(subFilterRight, curPos.y(), Z_POS_BODY);
					gl.glVertex3f(subFilterRight, curPos.y() + ((float)subFilterPassed.size()/subFilterSizes[i]) * scaleY * heightRight, Z_POS_BODY);
					gl.glVertex3f(filterRight, curY + ((float)subFilterPassed.size()/filter.getInput().size()) * heightLeft, Z_POS_BODY);
					gl.glVertex3f(filterRight, curY, Z_POS_BODY);			
				}
				gl.glEnd();
			}
		}
		
		// reset height
		heightRight = getHeightRight();
		
//		if( displayListDirty )
//		{
//			displayListDirty = false;
//
//			if( displayListOutputLines < 0 )
//				displayListOutputLines = gl.glGenLists(1);
//
//			gl.glNewList(displayListOutputLines, GL2.GL_COMPILE);
//		
//			int[] currentPositions = new int[subFiltersPassedElements.size()];
//			int[] currentSteps = new int[subFiltersPassedElements.size()];
//			int numSteps = elementsPassed.size() - elementsPassedAll.size();
//			int step = 0;
//	
//			gl.glLineWidth(0.5f);
//			gl.glBegin(GL2.GL_LINES);
//			
//			// render not common elements
//			for (Integer element : elementsPassed)
//			{
//				boolean skip = elementsPassedAll.contains(element);
//				
//				if( !skip )
//					gl.glColor4f
//					(
//						0.9f - (0.5f * step)/numSteps,
//						0.2f + (0.8f * step)/numSteps,
//						0.1f,
//						0.2f
//					);
//				
//				for( int i = 0; i < subFiltersPassedElements.size(); ++i )
//				{
//					if( currentPositions[i] >= subFiltersPassedElements.get(i).size() )
//						continue;
//	
//					if( (Integer)subFiltersPassedElements.get(i).toArray()[currentPositions[i]] == element )
//					{
//						++currentPositions[i];
//						
//						if( !skip )
//						{
//							gl.glVertex3f
//							(
//								vPos.x() + (1.f - spacingRight) * vSize.x(),
//								vPos.y() + spacingBottom * heightLeft
//										 + i * offsetY
//								+ ((float)(elementsPassedAll.size() + currentSteps[i]++)/subFilterSizes[i]) * scaleY
//								  * vSize.y() * (subFilterSizes[i]/100.f), // equals heightRight of sub filter
//								Z_POS_BODY + 0.1f
//							);
//							gl.glVertex3f
//							(
//								vPos.x() + vSize.x(),
//								vPos.y() + ((float)(elementsPassedAll.size() + step)/filter.getOutput().size()) * heightRight,
//								Z_POS_BODY + 0.1f
//							);
//						}
//					}
//				}
//				
//				if( !skip )
//					++step;
//			}
//			
//			gl.glEnd();
//			gl.glEndList();
//		}
//
//		gl.glCallList(displayListOutputLines);
		
		// render selection/mouseover if needed
		if( selectionType != SelectionType.NORMAL && mouseOverItem < 0 )
		{
			gl.glLineWidth
			( 
				(selectionType == SelectionType.SELECTION)
					? SelectionType.SELECTION.getLineWidth()
	                : SelectionType.MOUSE_OVER.getLineWidth()
	        );
			
			renderShape
			(
				gl,
				GL2.GL_LINE_LOOP,
				(selectionType == SelectionType.SELECTION)
					? SelectionType.SELECTION.getColor()
					: SelectionType.MOUSE_OVER.getColor(),
				Z_POS_MARK
			);
		}
	}
	
	/**
	 * Calculate the scaling factor for the subfilters
	 * 
	 * @return
	 */
	private float calculateFilterScalingY()
	{
		// calculate the available space for the subfilters		
		float delta = heightLeft - heightRight;
		float availableHeightLeft = subfilterScalingY * (heightLeft - spacingLeft * delta);
		float availableHeightRight = subfilterScalingY * (heightRight + spacingRight * delta);		
		
		float scalingLeft = availableHeightLeft / (subFilterSizes.length * heightLeft);
		
		// the last filter only uses the space it needs to allow the whole
		// filters use more space upwards
		int totalElementsRight = (subFilterSizes.length - 1) * filter.getInput().size()
		                       + subFilterSizes[subFilterSizes.length - 1];
		float totalHeightRight = vSize.y() * (totalElementsRight/100.f); 

		float scalingRight = availableHeightRight / totalHeightRight;
		
		return scalingLeft < scalingRight ? scalingLeft : scalingRight;
	}
	
	/**
	 * Render the input as an nurbs band
	 * @param gl
	 */
	private void renderInputBand
	( 
			GL2 gl,
			Vec2f topLeft,
			Vec2f topRight,
			Vec2f bottomLeft,
			Vec2f bottomRight,
			float horizontScale,
			float delta,
			float[] color
	)
	{
		ArrayList<Vec3f> topInputPoints = new ArrayList<Vec3f>();
		float inputWidth = topRight.x() - topLeft.x();
		
		// top curve
		topInputPoints.add
		(
			new Vec3f
			(
				topLeft.x(),
				topLeft.y(),
				Z_POS_BODY
			)
		);
		topInputPoints.add
		(
			new Vec3f
			(
				topLeft.x() + horizontScale * inputWidth,
				topLeft.y() - spacingLeft * delta,
				Z_POS_BODY
			)
		);
		topInputPoints.add
		(
			new Vec3f
			(
				topLeft.x() + horizontScale * inputWidth,
				topRight.y(),
				Z_POS_BODY
			)
		);
		topInputPoints.add
		(
			new Vec3f
			(
				topRight.x(),
				topRight.y(),
				Z_POS_BODY
			)
		);
		
		// bottom curve
		ArrayList<Vec3f> bottomInputPoints = new ArrayList<Vec3f>();
		bottomInputPoints.add
		(
			new Vec3f
			(
				bottomRight.x(),
				bottomRight.y(),
				Z_POS_BODY
			)
		);
		bottomInputPoints.add
		(
			new Vec3f
			(
				bottomLeft.x() + horizontScale * inputWidth,
				bottomRight.y(),
				Z_POS_BODY
			)
		);
		bottomInputPoints.add
		(
			new Vec3f
			(
				bottomLeft.x() + horizontScale * inputWidth,
				bottomLeft.y(),
				Z_POS_BODY
			)
		);
		bottomInputPoints.add
		(
			new Vec3f
			(
				bottomLeft.x(),
				bottomLeft.y(),
				Z_POS_BODY
			)
		);
		
		NURBSCurve topCurve =
			new NURBSCurve(topInputPoints, NUMBER_OF_SPLINE_POINTS);

		// Band border
		gl.glLineWidth(1);
		gl.glColor4f(0.5f, 0.5f, 0.5f, 1f);
		gl.glBegin(GL2.GL_LINE_STRIP);
		{
			for (Vec3f point : topCurve.getCurvePoints())
				gl.glVertex3f(point.x(), point.y(), point.z());
		}
		gl.glEnd();
		
		// Band border
		NURBSCurve bottomCurve = new NURBSCurve(bottomInputPoints, NUMBER_OF_SPLINE_POINTS);
		gl.glBegin(GL2.GL_LINE_STRIP);
		{
			for (Vec3f point : bottomCurve.getCurvePoints())
				gl.glVertex3f(point.x(), point.y(), point.z());
		}
		gl.glEnd();
		
		ArrayList<Vec3f> points = topCurve.getCurvePoints();
		points.addAll(bottomCurve.getCurvePoints());
		gl.glColor4fv(color, 0);
		inputRenderer.init(gl); // TODO
		inputRenderer.render(gl, points);
	}
}
