package org.geneview.core.view.opengl.util;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;

import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.media.opengl.GL;

import org.geneview.core.data.GeneralRenderStyle;
import org.geneview.core.data.collection.IStorage;
import org.geneview.core.data.collection.set.selection.SetSelection;
import org.geneview.core.data.view.camera.IViewFrustum;
import org.geneview.core.data.view.rep.renderstyle.InfoAreaRenderStyle;
import org.geneview.core.data.view.rep.renderstyle.ParCoordsRenderStyle;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.view.opengl.canvas.parcoords.EInputDataTypes;
import org.geneview.core.view.opengl.miniview.AGLMiniView;

import com.sun.opengl.util.j2d.TextRenderer;


/**
 * Info Area Renderer. Renders an info area. It needs only an id, a data type
 * and a gl context, and renders the information.
 *
 * @author Alexander Lex
 *
 */

public class GLTextInfoAreaRenderer 
{

	private TextRenderer textRenderer;
	
	private ArrayList<String> sContent;
	//private Point pickedPoint;
	private AGLMiniView miniView;
	private InformationContentCreator contentCreator;
	
	private Vec2f vecSize;
	private float fHeight = 0;
	private float fWidth = 0;
	private float fTextWidth;
	private float fSpacing = 0;
	private float fZValue = 0.005f;
	
	private InfoAreaRenderStyle renderStyle;
	
	/**
	 * Constructor
	 * 
	 * @param generalManager
	 */
	public GLTextInfoAreaRenderer(final IGeneralManager generalManager, IViewFrustum viewFrustum)
	{
		textRenderer = new TextRenderer(new Font("Arial",
				Font.BOLD, 16), false); 
		
		contentCreator = new InformationContentCreator(generalManager);
		
		renderStyle = new InfoAreaRenderStyle(viewFrustum);
		fSpacing = renderStyle.getSpacing();
		
	}
	/**
	 * Set the data to be rendered.
	 * 
	 * @param iGeneViewID
	 * @param eInputDataTypes
	 * @param pickedPoint
	 */
	public void setData(int iGeneViewID, EInputDataTypes eInputDataTypes) 
	{
		this.sContent = contentCreator.getStringContentForID(iGeneViewID, eInputDataTypes);
		//this.pickedPoint = pickedPoint;
		//miniView =  new GLParCoordsMiniView();
		fHeight = 0;
		fWidth = 0;
		vecSize = new Vec2f();
		calculateWidthAndHeight();
	}
	
	public void setMiniViewData(ArrayList<IStorage> alStorages, ArrayList<SetSelection> alSetSelection)
	{
		miniView.setData(alStorages, alSetSelection);
	}
	/**
	 * Render the data previously set
	 * 
	 * @param gl
	 * @param bFirstTime this has to be true only the first time you render it
	 * 			and can never be true after that
	 */
	public void renderInfoArea(GL gl, Vec3f vecLowerLeft, boolean bFirstTime)
	{	
		String sCurrent;
		float fXLowerLeft = vecLowerLeft.x();
		float fYLowerLeft = vecLowerLeft.y();
		
		int iCount = 0;
		while (iCount < 2)
		{
			if (iCount == 0)
			{	
				gl.glColor4fv(InfoAreaRenderStyle.INFO_AREA_COLOR, 0);
				
				gl.glBegin(GL.GL_POLYGON);
			}
			else
			{
				gl.glColor4fv(InfoAreaRenderStyle.INFO_AREA_BORDER_COLOR, 0);
				gl.glLineWidth(InfoAreaRenderStyle.INFO_AREA_BORDER_WIDTH);
				gl.glBegin(GL.GL_LINE_STRIP);
			}
			gl.glVertex3f(fXLowerLeft, fYLowerLeft, fZValue);
			gl.glVertex3f(fXLowerLeft + fWidth, fYLowerLeft, fZValue);
			gl.glVertex3f(fXLowerLeft + fWidth, fYLowerLeft + fHeight, fZValue);
			gl.glVertex3f(fXLowerLeft, fYLowerLeft + fHeight, fZValue);
			if (iCount == 1)
				gl.glVertex3f(fXLowerLeft, fYLowerLeft, fZValue);
			gl.glEnd();
			iCount++;
		}		
		
		textRenderer.setColor(1f, 1f, 1f, 1);	
				
		float fYUpperLeft = fYLowerLeft + fHeight;
	
		float fNextLineHeight = fYUpperLeft;
				
		textRenderer.begin3DRendering();
		
		Iterator<String> contentIterator = sContent.iterator();
		iCount = 0;
		
		float fFontScaling = renderStyle.getHeadingFontScalingFactor();
		while(contentIterator.hasNext())
		{			
			if(iCount == 1)
			{
				fFontScaling = renderStyle.getSmallFontScalingFactor();
			}
			sCurrent = contentIterator.next();	
			fNextLineHeight -= ((float)textRenderer.getBounds(sCurrent).getHeight() 
								* fFontScaling
								+ fSpacing);
			
			textRenderer.draw3D(sCurrent,
						fXLowerLeft  + fSpacing, 
						fNextLineHeight, fZValue + 0.001f,
						fFontScaling);
			
			iCount++;
		}
		textRenderer.end3DRendering();	
		
		if(miniView != null)
			miniView.render(gl, fXLowerLeft + fTextWidth + fSpacing, fYLowerLeft + fSpacing);
	
		
		
//		gl.glVertex3f(fXOrigin, fYOrigin, 0);
	
	}
	
	
	
	public float getWidth()
	{
		return fWidth;
	}
	
	public float getHeight()
	{
		return fHeight;
	}
	
	private void calculateWidthAndHeight()
	{
		String sCurrent;
		
		Rectangle2D box;
		float fTemp;
		
		Iterator<String> contentIterator = sContent.iterator();
		int iCount = 0;
		float fFontScalingFactor = renderStyle.getHeadingFontScalingFactor();
		while(contentIterator.hasNext())
		{
			
			sCurrent = contentIterator.next();
			if (iCount == 1)
				fFontScalingFactor = renderStyle.getSmallFontScalingFactor();
				
			box = textRenderer.getBounds(sCurrent).getBounds2D();
			fHeight += (box.getHeight() * fFontScalingFactor);
			
			fTemp = ((float)box.getWidth() * fFontScalingFactor);
		
			if(fTemp > fWidth)
			{
				fWidth = fTemp;
			}
			fHeight += fSpacing;
			
			iCount++;
			
		}
		fWidth += 2 * fSpacing;
		fHeight += 2 * fSpacing;
		
		fTextWidth = fWidth;
		if (miniView != null)
		{
			fWidth += (miniView.getWidth() + fSpacing * 2);
			
			if(fHeight < miniView.getHeight())
				fHeight = miniView.getHeight();
			
			fHeight += fSpacing * 2;
		}
		
		vecSize.set(fWidth, fHeight);
	}	
}