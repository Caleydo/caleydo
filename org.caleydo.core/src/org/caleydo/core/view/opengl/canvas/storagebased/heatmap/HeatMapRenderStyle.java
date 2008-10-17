package org.caleydo.core.view.opengl.canvas.storagebased.heatmap;

import java.util.HashMap;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.GenericSelectionManager;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

/**
 * Heat Map render styles
 * 
 * @author Alexander Lex
 */

public class HeatMapRenderStyle
	extends GeneralRenderStyle
{

	private class FieldWidthElement
	{
		protected float fWidth = 0;
		protected float fHeight = 0;
		protected float fTotalWidth = 0;

		protected FieldWidthElement(float fWidth, float fHeight, float fTotalWidth)
		{
			this.fWidth = fWidth;
			this.fHeight = fHeight;
			this.fTotalWidth = fTotalWidth;
		}
	}

	public static final float FIELD_Z = 0.0f;

	public static final float SELECTION_Z = 0.005f;

	private static final float SELECTED_FIELD_WIDTH_PERCENTAGE = 0.1f;
	private static final float MAXIMUM_SELECTED_AREA_PERCENTAGE = 0.8f;
	private float fSelectedFieldWidth = 1.0f;

	private float fMamximumNormalFieldWidth = fSelectedFieldWidth / 3 * 2;

	private float fNormalFieldWidth = 0f;

	private float fFieldHeight = 0f;

	private int iLevels = 1;

	private int iNotSelectedLevel = 1000;

//	private ArrayList<FieldWidthElement> alFieldWidths;

	private HashMap<Integer, Float> hashLevelToWidth;

	private GenericSelectionManager contentSelectionManager;

	private int iContentVAID;
	private int iStorageVAID;

	private ISet set;

	private EDetailLevel detailLevel = EDetailLevel.HIGH;

	private boolean bRenderStorageHorizontally;

	public HeatMapRenderStyle(final IViewFrustum viewFrustum,
			final GenericSelectionManager contentSelectionManager, ISet set, int iContentVAID,
			int iStorageVAID, int iNumElements, boolean bRenderStorageHorizontally)
	{

		super(viewFrustum);

		this.contentSelectionManager = contentSelectionManager;
		this.bRenderStorageHorizontally = bRenderStorageHorizontally;

		this.iContentVAID = iContentVAID;
		this.iStorageVAID = iStorageVAID;

		this.set = set;
//		alFieldWidths = new ArrayList<FieldWidthElement>();

		// init fish eye
		float fDelta = (fSelectedFieldWidth - fNormalFieldWidth) / (iLevels + 1);
		hashLevelToWidth = new HashMap<Integer, Float>();
		hashLevelToWidth.put(iNotSelectedLevel, fNormalFieldWidth);
		float fCurrentWidth = fNormalFieldWidth;
		for (int iCount = -iLevels; iCount <= iLevels; iCount++)
		{
			if (iCount < 0)
				fCurrentWidth += fDelta;
			else if (iCount == 0)
				fCurrentWidth = fSelectedFieldWidth;
			else
				fCurrentWidth -= fDelta;

			hashLevelToWidth.put(iCount, fCurrentWidth);
		}

	}

	public void setDetailLevel(EDetailLevel detailLevel)
	{
		// make sure that widht and height are completely reiinitialized

		this.detailLevel = detailLevel;
	}



	/**
	 * Set the active virtual array of the heat map here, when it changed during
	 * runtime. Needed to calculate the widht and height of an element.
	 * 
	 * @param iContentVAID the id of the content virtual array
	 */
	public void setActiveVirtualArray(int iContentVAID)
	{
		this.iContentVAID = iContentVAID;
	}

	
	/**
	 * Initializes or updates field sizes based on selections, virtual arrays
	 * etc. Call this every time something has changed.
	 */
	public void updateFieldSizes()
	{
		int iNumberSelected = contentSelectionManager
				.getNumberOfElements(ESelectionType.MOUSE_OVER);
		iNumberSelected += contentSelectionManager
				.getNumberOfElements(ESelectionType.SELECTION);
		int iNumberTotal = set.getVA(iContentVAID).size();

		float fSelecteFieldWidthPercentage = SELECTED_FIELD_WIDTH_PERCENTAGE;
		if (iNumberSelected > 0 && SELECTED_FIELD_WIDTH_PERCENTAGE * iNumberSelected > 1)
		{
			fSelecteFieldWidthPercentage = 1.0f / iNumberSelected;
		}

		if (bRenderStorageHorizontally)
		{

			fSelectedFieldWidth = getRenderWidth() * MAXIMUM_SELECTED_AREA_PERCENTAGE
					* fSelecteFieldWidthPercentage;

			fNormalFieldWidth = (getRenderWidth() - iNumberSelected * fSelectedFieldWidth)
					/ (iNumberTotal - iNumberSelected);

			fFieldHeight = getRenderHeight() / set.getVA(iStorageVAID).size();
		}
		else
		{
			fSelectedFieldWidth = getRenderHeight() * MAXIMUM_SELECTED_AREA_PERCENTAGE
					* fSelecteFieldWidthPercentage;
			fNormalFieldWidth = (getRenderHeight() - iNumberSelected * fSelectedFieldWidth)
					/ (iNumberTotal - iNumberSelected);

			fFieldHeight = (getRenderWidth() / set.getVA(iStorageVAID).size());
		}

		fNormalFieldWidth = (fNormalFieldWidth > fMamximumNormalFieldWidth) ? fMamximumNormalFieldWidth
				: fNormalFieldWidth;
	}


	public float getNormalFieldWidth()
	{
		
		return fNormalFieldWidth;
	}
	
	public float getSelectedFieldWidth()
	{
		return fSelectedFieldWidth;
	}
	
	public float getFieldHeight()
	{
		return fFieldHeight;
	}
	


	public float getYCenter()
	{

		// TODO: this is only correct for 4 rows
		return (viewFrustum.getHeight() / 2);
	}

	public float getXCenter()
	{

		return (viewFrustum.getWidth() / 2);
	}

	public float getXSpacing()
	{
		return 0.4f;
	}

	public float getYSpacing()
	{
		return 0.3f;
	}


	public void setBRenderStorageHorizontally(boolean bRenderStorageHorizontally)
	{
		this.bRenderStorageHorizontally = bRenderStorageHorizontally;
	}

	private float getRenderWidth()
	{
		// if (!bRenderStorageHorizontally)
		// {
		if (detailLevel == EDetailLevel.HIGH)
			return viewFrustum.getWidth() - 2.4f * getXSpacing() - getColorMappingBarWidth();
		return viewFrustum.getWidth();
		// }
		//
		// if (detailLevel == EDetailLevel.HIGH)
		// return viewFrustum.getHeight() - 2 * getYSpacing();
		// return viewFrustum.getHeight();
	}

	private float getRenderHeight()
	{
		// if (!bRenderStorageHorizontally)
		// {
		if (detailLevel == EDetailLevel.HIGH)
			return viewFrustum.getHeight() - 2 * getYSpacing();
		return viewFrustum.getHeight();
		// }
		// if (detailLevel == EDetailLevel.HIGH)
		// return viewFrustum.getWidth() - 2 * getXSpacing() -
		// getColorMappingBarWidth();
		// return viewFrustum.getWidth();

	}

	public float getColorMappingBarWidth()
	{
		return viewFrustum.getWidth() / 40;
	}

	public float getColorMappingBarHeight()
	{
		return viewFrustum.getHeight() / 3;
	}

}
