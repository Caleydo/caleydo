package org.geneview.core.view.opengl.util;

import gleem.linalg.Vec3f;

import java.awt.Point;
import java.util.ArrayList;

import javax.media.opengl.GL;

import org.geneview.core.data.collection.IStorage;
import org.geneview.core.data.collection.set.selection.SetSelection;
import org.geneview.core.data.view.camera.IViewFrustum;
import org.geneview.core.data.view.rep.renderstyle.InfoAreaRenderStyle;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.view.opengl.canvas.parcoords.EInputDataTypes;

/**
 * 
 * Draw Info Areas
 * 
 * Pass a point, and ID and a data type and this class manages all the drawing
 * of the info areas
 * 
 * uses GLTextInfoAreaRenderer to draw the actual rectangle, is responsible for
 * creating the renderers (multiple in case of star rendering) and draw the 
 * connections from the infoarea to the selected element
 * 
 * @author Alexander Lex
 */
public class GLInfoAreaManager 
{
	
	private Point pickedPoint;
	
	private GLTextInfoAreaRenderer infoArea;
	
	private float fXOrigin = 0;
	private float fYOrigin = 0;
	
	private float fXElementOrigin = 0;
	private float fYElementOrigin = 0;
	private Vec3f vecLowerLeft;
	private IViewFrustum viewFrustum;
	
	public GLInfoAreaManager(final IGeneralManager generalManager, final IViewFrustum viewFrustum)
	{
		infoArea = new GLTextInfoAreaRenderer(generalManager, viewFrustum);
		this.viewFrustum = viewFrustum; 
	}
	
	/**
	 * Set the data to be rendered.
	 * 
	 * @param iGeneViewID
	 * @param eInputDataTypes
	 * @param pickedPoint
	 */
	public void setData(int iGeneViewID, EInputDataTypes eInputDataTypes, Point pickedPoint) 
	{
		//this.sContent = contentCreator.getStringContentForID(iGeneViewID, eInputDataTypes);
		this.pickedPoint = pickedPoint;
		vecLowerLeft = new Vec3f();
		
		infoArea.setData(iGeneViewID, eInputDataTypes);
		//miniView =  new AGLParCoordsMiniView();
//		fXOrigin = 0;
//		fYOrigin = 0;
//		fHeight = 0;
//		fWidth = 0;
	}
	
	public void setMiniViewData(ArrayList<IStorage> alStorages, ArrayList<SetSelection> alSetSelection)
	{
		infoArea.setMiniViewData(alStorages, alSetSelection);
	}
	
	
	
	/**
	 * Render the data previously set
	 * 
	 * @param gl
	 * @param bFirstTime this has to be true only the first time you render it
	 * 			and can never be true after that
	 */
	public void renderInfoArea(GL gl, boolean bFirstTime)
	{
	
		if(bFirstTime)
		{
			float[] fArWorldCoords = GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl, pickedPoint.x, pickedPoint.y);
						
			fXOrigin = fArWorldCoords[0];
			fYOrigin = fArWorldCoords[1];	
			fXElementOrigin = fXOrigin + 0.2f;
			fYElementOrigin = fYOrigin + 0.2f;
			vecLowerLeft.set(fXElementOrigin, fYElementOrigin , 0); 
		}
		
		gl.glColor3fv(InfoAreaRenderStyle.INFO_AREA_COLOR, 0);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(fXOrigin, fYOrigin, InfoAreaRenderStyle.INFO_AREA_CONNECTION_Z);
		gl.glVertex3f(fXElementOrigin, fYElementOrigin,
				InfoAreaRenderStyle.INFO_AREA_CONNECTION_Z);
		gl.glVertex3f(fXElementOrigin , fYElementOrigin + infoArea.getHeight(), 
				InfoAreaRenderStyle.INFO_AREA_CONNECTION_Z);
		gl.glEnd();
		
		infoArea.renderInfoArea(gl, vecLowerLeft, bFirstTime);
	}

}
