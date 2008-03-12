package org.geneview.core.data.view.rep.renderstyle;

import gleem.linalg.Vec2f;

import java.util.ArrayList;
import java.util.HashMap;

import org.geneview.core.data.GeneralRenderStyle;
import org.geneview.core.data.view.camera.IViewFrustum;
import org.geneview.core.view.opengl.util.selection.EViewInternalSelectionType;
import org.geneview.core.view.opengl.util.selection.GenericSelectionManager;

/**
 * 
 * @author Alexander Lex
 *
 */

public class HeatMapRenderStyle 
extends GeneralRenderStyle 
{
	public static final float FIELD_Z = 0.0f;
	public static final float SELECTION_Z = 0.001f;
	
	private float fSelectedFieldWidth = 0.3f;
	private float fSelectedFieldHeight = 0.6f;
	private float fNormalFieldWidth = 0.05f;
	private float fNormalFieldHeight = 0.4f;
	
	private int iLevels = 3;
	private int iNotSelectedLevel = 1000;
	
	private ArrayList<Float> fAlFieldWidths;
	private HashMap<Integer, Float> hashLevelToWidth;

	private GenericSelectionManager verticalSelectionManager;
	private ArrayList<Integer> alContentSelection;
	
	
	public HeatMapRenderStyle(final IViewFrustum viewFrustum, 
			final GenericSelectionManager horizontalSelectionManager, final ArrayList<Integer> alContentSelection)
	{
		super(viewFrustum);
		this.verticalSelectionManager = horizontalSelectionManager;
		this.alContentSelection = alContentSelection;
		fAlFieldWidths = new ArrayList<Float>();
		
		// init fish eye
		float fDelta = (fSelectedFieldWidth - fNormalFieldWidth) / (iLevels + 1);
		hashLevelToWidth = new HashMap<Integer, Float>();
		hashLevelToWidth.put(iNotSelectedLevel, fNormalFieldWidth);
		float fCurrentWidth = fNormalFieldWidth;
		for(int iCount = -iLevels; iCount <= iLevels; iCount++)
		{
			if(iCount < 0)
				fCurrentWidth += fDelta;
			else if (iCount == 0)
				fCurrentWidth = fSelectedFieldWidth;
			else
				fCurrentWidth -= fDelta;
			
			hashLevelToWidth.put(iCount, fCurrentWidth);
		}
		
	}
	
	/**
	 * 
	 * @param iStorageIndex The index of the entry in alContentSelection
	 * @return
	 */
	public Vec2f getAndInitFieldWidthAndHeight(final int iContentSelectionIndex)
	{
		int iCurrentLevel = iNotSelectedLevel;
		
		for(int iCount = -iLevels; iCount <= iLevels; iCount++)
		{
			if(iContentSelectionIndex + iCount < 0 ||
					iContentSelectionIndex + iCount >= alContentSelection.size())
				continue;
			else
			{
				
				if(verticalSelectionManager.
						checkStatus(EViewInternalSelectionType.SELECTION, alContentSelection.get(iContentSelectionIndex + iCount)) ||
						verticalSelectionManager.checkStatus(
								EViewInternalSelectionType.MOUSE_OVER, alContentSelection.get(iContentSelectionIndex + iCount)))
				{
					//TODO: this needs reviewing
					if (iCurrentLevel == iNotSelectedLevel || 
							(iCount < 0 && iCount >= iCurrentLevel) || 
							(iCount >= 0 && iCount <= iCurrentLevel)) 
						iCurrentLevel = iCount;
				}
				
			}
		}
		
		
		Vec2f vecWidthAndHeight = new Vec2f();
		float fWidth = hashLevelToWidth.get(iCurrentLevel);
		vecWidthAndHeight.set(fWidth, calcHeightFromWidth(fWidth));
		
//		if(verticalSelectionManager.
//				checkStatus(EViewInternalSelectionType.SELECTION, iStorageIndex) ||
//				verticalSelectionManager.checkStatus(
//						EViewInternalSelectionType.MOUSE_OVER, iStorageIndex))
//		{			
//			vecWidthAndHeight.set(fSelectedFieldWidth, fSelectedFieldHeight);	
//		}
//		else
//		{
//			vecWidthAndHeight.set(fNormalFieldWidth, fNormalFieldHeight);
//		}
		
		fAlFieldWidths.add(fWidth);
		return vecWidthAndHeight;
	}
	
	private float calcHeightFromWidth(float fWidth)
	{
		return 2.5f*fWidth;
	}
	
	public void clearFieldWidths()
	{
		fAlFieldWidths.clear();
	}
	
	public float getXDistanceAt(int iIndex)
	{
		float fXDistance = 0;
		for(int iCount = 0; iCount < iIndex; iCount++)
		{
			fXDistance += fAlFieldWidths.get(iCount);
		}
		
		return fXDistance;
		
	}
	
	public float getXDistanceAfter(int iIndex)
	{
		float fXDistance = 0;
		for(int iCount = 0; iCount <= iIndex; iCount++)
		{
			fXDistance += fAlFieldWidths.get(iCount);
		}
		
		return fXDistance;
		
	}
	
	public Vec2f getFieldWidthAndHeight(int iIndex)
	{
		Vec2f vecWidthAndHeight = new Vec2f();
		vecWidthAndHeight.set(fAlFieldWidths.get(iIndex), calcHeightFromWidth(fAlFieldWidths.get(iIndex)));
		
		return vecWidthAndHeight;
	}
	
//	public float getFieldHeight(int iIndex)
//	{
//		return 0.4f;
//	}
	
	public float getYCenter()
	{
		// TODO: this is only correct for 4 rows
		return (fFrustumHeight / 2);
	}
	
	public float getXCenter()
	{
		return (fFrustumWidth / 2);
	}
	
//	public float getXSpacing()
//	{
//		return COORDINATE_SIDE_SPACING * fScaling;
//	}
//	
//	public float getYSpacing()
//	{
//		
//	}
	
	
	
}
