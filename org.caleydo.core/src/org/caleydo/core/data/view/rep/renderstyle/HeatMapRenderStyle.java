package org.caleydo.core.data.view.rep.renderstyle;

import gleem.linalg.Vec2f;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.GenericSelectionManager;
import org.caleydo.core.data.view.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;

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

	public static final float SELECTION_Z = 0.001f;

	private float fSelectedFieldWidth = 1.0f;

	private float fMamximumNormalFieldWidth = fSelectedFieldWidth / 3 * 2;

	private float fNormalFieldWidth = 0f;

	private float fFieldHeight = 0f;

	private int iLevels = 1;

	private int iNotSelectedLevel = 1000;

	private ArrayList<FieldWidthElement> alFieldWidths;

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
		alFieldWidths = new ArrayList<FieldWidthElement>();

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
		this.detailLevel = detailLevel;
	}

	public void initFieldSizes()
	{
		resetFieldDimensions();
		Set<Integer> selectedSet = contentSelectionManager
				.getElements(ESelectionType.SELECTION);

		Set<Integer> mouseOverSet = contentSelectionManager
				.getElements(ESelectionType.MOUSE_OVER);

		int iVAIndex;
		for (int iSelectedIndex : selectedSet)
		{
			iVAIndex = set.getVA(iContentVAID).indexOf(iSelectedIndex);
			alFieldWidths.get(iVAIndex).fWidth = fSelectedFieldWidth;
		}

		for (int iSelectedIndex : mouseOverSet)
		{
			iVAIndex = set.getVA(iContentVAID).indexOf(iSelectedIndex);
			alFieldWidths.get(iVAIndex).fWidth = fSelectedFieldWidth;
		}
	}

	public void setContentSelection(int iContentSelection)
	{
		this.iContentVAID = iContentSelection;
	}

	public void resetFieldDimensions()
	{
		int iNumberSelected = contentSelectionManager
				.getNumberOfElements(ESelectionType.MOUSE_OVER);
		iNumberSelected += contentSelectionManager
				.getNumberOfElements(ESelectionType.SELECTION);
		int iNumberTotal = set.getVA(iContentVAID).size();

		if (bRenderStorageHorizontally)
		{
			fNormalFieldWidth = (getRenderWidth() - iNumberSelected * fSelectedFieldWidth)
					/ (iNumberTotal - iNumberSelected);

			fNormalFieldWidth = (fNormalFieldWidth > fMamximumNormalFieldWidth) ? fMamximumNormalFieldWidth
					: fNormalFieldWidth;

			fFieldHeight = getRenderHeight() / set.getVA(iStorageVAID).size();
		}
		else
		{
			fNormalFieldWidth = (getRenderHeight() - iNumberSelected * fSelectedFieldWidth)
					/ (iNumberTotal - iNumberSelected);
			fFieldHeight = (getRenderWidth() / set.getVA(iStorageVAID).size());
		}

		alFieldWidths.clear();

		float fTotalFieldWidth = 0;
		float fCurrentFieldWidth = 0;
		for (int iContentIndex : set.getVA(iContentVAID))
		{
			fCurrentFieldWidth = isSelected(iContentIndex) ? fSelectedFieldWidth
					: fNormalFieldWidth;
			alFieldWidths.add(new FieldWidthElement(fCurrentFieldWidth, fFieldHeight,
					fTotalFieldWidth));
			fTotalFieldWidth += fCurrentFieldWidth;
		}
	}

	private boolean isSelected(int iIndex)
	{
		if (contentSelectionManager.checkStatus(ESelectionType.MOUSE_OVER, iIndex))
			return true;
		if (contentSelectionManager.checkStatus(ESelectionType.SELECTION, iIndex))
			return true;

		return false;
	}

	public float getXDistanceAt(int iIndex)
	{
		return alFieldWidths.get(iIndex).fTotalWidth;
	}

	public float getXDistanceAfter(int iIndex)
	{
		return alFieldWidths.get(iIndex).fTotalWidth + alFieldWidths.get(iIndex).fWidth;
	}

	public Vec2f getFieldWidthAndHeight(int iIndex)
	{
		Vec2f vecWidthAndHeight = new Vec2f();
		float fWidth = alFieldWidths.get(iIndex).fWidth;
		vecWidthAndHeight.set(fWidth, fFieldHeight);

		return vecWidthAndHeight;
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
		return 0.5f;
	}

	public float getYSpacing()
	{
		return 0.5f;
	}

	public float getNormalFieldWidth()
	{

		return fNormalFieldWidth;
	}

	private float getRenderWidth()
	{
		if (detailLevel == EDetailLevel.HIGH)
			return viewFrustum.getWidth() - 2 * getXSpacing();
		return viewFrustum.getWidth();
	}

	private float getRenderHeight()
	{
		if (detailLevel == EDetailLevel.HIGH)
			return viewFrustum.getHeight() - 2 * getYSpacing();
		return viewFrustum.getHeight();
	}

}
