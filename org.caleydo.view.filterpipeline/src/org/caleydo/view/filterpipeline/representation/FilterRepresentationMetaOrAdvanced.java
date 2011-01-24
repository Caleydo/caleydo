package org.caleydo.view.filterpipeline.representation;

import gleem.linalg.Vec2f;
import javax.media.opengl.GL2;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
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

		// render total filter
		gl.glPushName(iPickingID);
		renderShape
		(
			gl,
			GL2.GL_QUADS,
			renderStyle.FILTER_COMBINED_BACKGROUND_COLOR,
			Z_POS_BODY
		);
		gl.glPopName();
		
		// we can only use the space to the height on the right size as
		// otherwise filters can go outside the total filter
		float totalRight = 0.94f * (heightLeft - 0.75f * (heightLeft - heightRight));
		
		// the last filter only uses the space it needs to allow the whole
		// filters use more space upwards
		int totalElements = (subFilterSizes.length - 1) * filter.getInput().size()
		                  + subFilterSizes[subFilterSizes.length - 1];
		
		float totalLeft =
			  ((float)(subFilterSizes.length * filter.getInput().size()) / totalElements)
			* totalRight;
		
		float maxLeft = 0.94f * (heightLeft - 0.25f * (heightLeft - heightRight));
		
		if( totalLeft > maxLeft )
			totalLeft = maxLeft;

		// smallSize = scale * fullSize
		float scale = 0.93f * (totalLeft/subFilterSizes.length) / heightLeft;
		
		for( int i = 0; i < subFilterSizes.length; ++i )
		{
			heightRight = 0.5f * vSize.y() * (subFilterSizes[i]/100.f);
			Vec2f pos = vPos.copy();
			pos.add(new Vec2f(vSize.x()/4.f,(0.06f + (float)i/subFilterSizes.length) * totalLeft));
			
			gl.glPushName(pickingManager.getPickingID(viewId, EPickingType.FILTERPIPE_SUB_FILTER, i));
			renderShape
			(
				gl,
				GL2.GL_QUADS,
				pos,
				vSize.x() / 2.f,
				scale * heightLeft,
				scale * heightRight,
				renderStyle.getFilterColor(i),
				Z_POS_BODY + 0.1f
			);
			gl.glPopName();
			
			// render input
			renderShape
			(
				gl,
				GL2.GL_QUADS,
				vPos,
				vSize.x()/4.f,
				heightLeft,
				scale * heightLeft,
				pos.y() - vPos.y(),
				new float[]{.2f, .9f, .4f, .3f},
				Z_POS_BODY
			);
			
			if( mouseOverItem == i )
			{
				// render mouse over
				gl.glLineWidth(SelectionType.MOUSE_OVER.getLineWidth());
				renderShape
				(
					gl,
					GL2.GL_LINE_LOOP,
					pos,
					vSize.x() / 2.f,
					scale * heightLeft,
					scale * heightRight,
					SelectionType.MOUSE_OVER.getColor(),
					Z_POS_MARK
				);
			}
			
			if( elementsPassedAll.size() > 0 )
			{
				// render common output
				pos.setX(pos.x() + vSize.x()/2.f);
				renderShape
				(
					gl,
					GL2.GL_QUADS,
					pos,
					vSize.x()/4.f,
					((float)elementsPassedAll.size()/subFilterSizes[i]) * scale * heightRight,
					((float)elementsPassedAll.size()/filter.getOutput().size()) * getHeightRight(),
					vPos.y() - pos.y(),
					new float[]{0.2f, 0.9f, 0.2f, 0.5f},
					Z_POS_BODY
				);
			}
		}
		
		// reset height
		heightRight = getHeightRight();
		
		if( displayListDirty )
		{
			displayListDirty = false;

			if( displayListOutputLines < 0 )
				displayListOutputLines = gl.glGenLists(1);

			gl.glNewList(displayListOutputLines, GL2.GL_COMPILE);
		
			int[] currentPositions = new int[subFiltersPassedElements.size()];
			int[] currentSteps = new int[subFiltersPassedElements.size()];
			int numSteps = elementsPassed.size() - elementsPassedAll.size();
			int step = 0;
	
			gl.glLineWidth(0.5f);
			gl.glBegin(GL2.GL_LINES);
			
			// render not common elements
			for (Integer element : elementsPassed)
			{
				boolean skip = elementsPassedAll.contains(element);
				
				if( !skip )
					gl.glColor4f
					(
						0.4f + (0.5f * step)/numSteps,
						1 - (0.8f * step)/numSteps,
						0.1f,
						0.2f
					);
				
				for( int i = 0; i < subFiltersPassedElements.size(); ++i )
				{
					if( currentPositions[i] >= subFiltersPassedElements.get(i).size() )
						continue;
	
					if( (Integer)subFiltersPassedElements.get(i).toArray()[currentPositions[i]] == element )
					{
						++currentPositions[i];
						
						if( !skip )
						{
							gl.glVertex3f
							(
								vPos.x() + 0.75f * vSize.x(),
								  vPos.y() + (0.06f + (float)i/subFilterSizes.length) * totalLeft
								+ ((float)(elementsPassedAll.size() + currentSteps[i]++)/subFilterSizes[i]) * scale
								  * 0.5f * vSize.y() * (subFilterSizes[i]/100.f), // equals heightRight of sub filter
								Z_POS_BODY + 0.1f
							);
							gl.glVertex3f
							(
								vPos.x() + vSize.x(),
								vPos.y() + ((float)(elementsPassedAll.size() + step)/filter.getOutput().size()) * heightRight,
								Z_POS_BODY + 0.1f
							);
						}
					}
				}
				
				if( !skip )
					++step;
			}
			
			gl.glEnd();
			gl.glEndList();
		}

		gl.glCallList(displayListOutputLines);
		
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
		
		// currently not filtered elements
		textRenderer.renderText
		(
			gl,
			""+filter.getOutput().size(),
			vPos.x() + vSize.x() - 0.4f,
			vPos.y() + heightRight + 0.05f,
			Z_POS_TEXT,
			0.007f,
			20
		);
		
		// label
		textRenderer.renderText
		(
			gl,
			(filter.getOutput().size() - filter.getInput().size())
			+ " (-"+filter.getSizeVADelta()+")",
			vPos.x() + 0.05f,
			vPos.y() + 0.05f,
			Z_POS_TEXT,
			0.007f,
			20
		);
	}
}
