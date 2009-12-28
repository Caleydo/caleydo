package org.caleydo.core.view.opengl.canvas.grouper;

import java.util.ArrayList;

import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

/**
 * Histogram render styles
 * 
 * @author Alexander Lex
 */

public class GrouperRenderStyle
	extends GeneralRenderStyle {
	
	public static final float GUI_ELEMENT_MIN_SIZE = 50.0f;
	public static final float ELEMENT_LEFT_SPACING = 0.3f;
	public static final float ELEMENT_TOP_SPACING = 0.2f;
	public static final float ELEMENT_BOTTOM_SPACING = 0.2f;
	
	public static final float TEXT_SCALING = 0.01f;
	public static final float TEXT_SPACING = 0.02f;
	public static final float[] TEXT_COLOR = { 0.0f, 0.0f, 0.0f, 1f };
	public static final float[] TEXT_BG_COLOR = { 0.5f, 0.5f, 0.5f, 1f };
	
	private ArrayList<float[]> alGroupLevelColors;


	public GrouperRenderStyle(GLGrouper histogram, IViewFrustum viewFrustum) {

		super(viewFrustum);
		
		alGroupLevelColors = new ArrayList<float[]>();
		float fArGroupColor[] = new float[4];
		fArGroupColor[0] = 1.0f;
		fArGroupColor[1] = 0.0f;
		fArGroupColor[2] = 0.0f;
		fArGroupColor[3] = 1.0f;
		alGroupLevelColors.add(fArGroupColor);
		
		fArGroupColor = new float[4];
		fArGroupColor[0] = 0.0f;
		fArGroupColor[1] = 1.0f;
		fArGroupColor[2] = 0.0f;
		fArGroupColor[3] = 1.0f;
		alGroupLevelColors.add(fArGroupColor);
		
		fArGroupColor = new float[4];
		fArGroupColor[0] = 0.0f;
		fArGroupColor[1] = 0.0f;
		fArGroupColor[2] = 1.0f;
		fArGroupColor[3] = 1.0f;
		alGroupLevelColors.add(fArGroupColor);
		
		fArGroupColor = new float[4];
		fArGroupColor[0] = 0.8f;
		fArGroupColor[1] = 0.4f;
		fArGroupColor[2] = 0.0f;
		fArGroupColor[3] = 1.0f;
		alGroupLevelColors.add(fArGroupColor);
		
		fArGroupColor = new float[4];
		fArGroupColor[0] = 0.0f;
		fArGroupColor[1] = 0.8f;
		fArGroupColor[2] = 0.4f;
		fArGroupColor[3] = 1.0f;
		alGroupLevelColors.add(fArGroupColor);
		
		fArGroupColor = new float[4];
		fArGroupColor[0] = 0.4f;
		fArGroupColor[1] = 0.0f;
		fArGroupColor[2] = 0.8f;
		fArGroupColor[3] = 1.0f;
		alGroupLevelColors.add(fArGroupColor);
	}
	
	public ArrayList<float[]> getGroupLevelColors() {
		return alGroupLevelColors;
	}

	public void setGroupLevelColors(ArrayList<float[]> alGroupLevelColors) {
		this.alGroupLevelColors = alGroupLevelColors;
	}
	
	public float[] getGroupColorForLevel(int iLevel) {
		
		while(iLevel >= alGroupLevelColors.size()) {
			iLevel -= alGroupLevelColors.size();
		}
		
		if(iLevel < 0) {
			iLevel = 0;
		}
		
		return alGroupLevelColors.get(iLevel);
	}

}
