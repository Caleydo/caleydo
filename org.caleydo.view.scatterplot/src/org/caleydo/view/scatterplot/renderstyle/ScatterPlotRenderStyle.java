package org.caleydo.view.scatterplot.renderstyle;

//import java.util.HashMap;

import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.view.scatterplot.GLScatterplot;

/**
 * ScatterPlot render styles
 * 
 * @author Juergen Pillhofer
 */

public class ScatterPlotRenderStyle extends GeneralRenderStyle {

	public static final float FIELD_Z = 0.001f;

	public static final float SELECTION_Z = 0.005f;

	public static final float[] X_AXIS_COLOR = {0.0f, 0.0f, 0.0f, 1.0f};
	public static final float X_AXIS_LINE_WIDTH = 2.0f;
	public static final float[] Y_AXIS_COLOR = {0.0f, 0.0f, 0.0f, 1.0f};
	public static final float Y_AXIS_LINE_WIDTH = 2.0f;
	public static final float XYAXISDISTANCE = 0.2f;
	public static final float AXIS_Z = 0.0f;
	
	public static int NR_TEXTURESX =5;
	public static int NR_TEXTURESY =5;
	public static int NR_TEXTURES = 25;

	public static float POINTSIZE = 0.05f;

	private int iPointSize = 1;
	public static EScatterPointType POINTSTYLE = EScatterPointType.BOX;

	public static final float XLABELROTATIONNAGLE = 0.0f;
	public static final float YLABELROTATIONNAGLE = 90.0f;
	public static final float XLABELDISTANCE = 0.03f;
	public static final float YLABELDISTANCE = 0.10f;

	public static final int NUMBER_AXIS_MARKERS = 19;

	public static final int MIN_AXIS_LABEL_TEXT_SIZE = 60;
	public static final int MIN_NUMBER_TEXT_SIZE = 55;

	public static final float LABEL_Z = 0.004f;
	public static final float TEXT_ON_LABEL_Z = LABEL_Z + 0.0001f;
	public static final float AXIS_MARKER_WIDTH = 0.02f;

	public static final int LABEL_TEXT_MIN_SIZE = 50;



	private float fRenderHeight = 0f;
	private float fRenderWith = 0f;
	
	private boolean bIsEmbedded=true;


	private float fSizeHeatmapArrow = 0.17f;

	public ScatterPlotRenderStyle(GLScatterplot scatterPlot,
			IViewFrustum viewFrustum) {

		super(viewFrustum);

		fRenderHeight=viewFrustum.getHeight();
		fRenderWith=viewFrustum.getWidth();	
	}

	
	public void setIsEmbedded(boolean value)
	{
		bIsEmbedded=value;
	}
	
	

	public void setTextureNr(int x,int y)
	{
		NR_TEXTURESX=x;
		NR_TEXTURESY=y;			
		NR_TEXTURES=NR_TEXTURESX*NR_TEXTURESY;		
	}
	
	
	public float getYCenter() {

		// TODO: this is only correct for 4 rows
		return viewFrustum.getHeight() / 2;
	}

	public float getXCenter() {

		return viewFrustum.getWidth() / 2;
	}

	public float getXSpacing() {
		return 0.4f;
	}

	public float getYSpacing() {
		return 0.3f;
	}

	public void setPOINTSTYLE(EScatterPointType Type) {
		POINTSTYLE = Type;
	}

	public void setPointSize(int value) {
		POINTSIZE = value / 100.0f;
		iPointSize = value;
	}

	public int getPointSize() {
		return iPointSize;
	}

		
//	public void setRenderWidth(boolean bFullView) {
//
//		if (bFullView) fRenderWith=viewFrustum.getWidth();
//		else fRenderWith = getXCenter();
//		
//	}
//	
//	public void setRenderHeight(boolean bFullView) {		
//		if (bFullView) fRenderHeight=viewFrustum.getHeight();
//		else fRenderHeight = getYCenter();
//
//	}
	
	public float getRenderWidth() {

		//return fRenderWith; 		
		if (!bIsEmbedded)
			return viewFrustum.getWidth();
			else return getXCenter();
		
					
	}

	public float getRenderHeight() {

		//
		if (!bIsEmbedded)
		return viewFrustum.getHeight();
		else return getYCenter();

	}

	public float getLabelHeight() {

		return getRenderHeight() / 2;

	}

	public float getLAbelWidth() {

		return getRenderWidth() / 2;

	}

	public float getAxisHeight() {
		return getRenderHeight() - 2 * XYAXISDISTANCE;
	}

	public float getAxisWidth() {
		return getRenderWidth() - 2 * XYAXISDISTANCE;
	}

	public float getSizeHeatmapArrow() {
		return fSizeHeatmapArrow;
	}
}
