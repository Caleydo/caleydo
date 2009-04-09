package org.caleydo.core.view.opengl.canvas.storagebased;

import static org.caleydo.core.view.opengl.canvas.storagebased.HistogramRenderStyle.SIDE_SPACING;

import java.awt.Point;
import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.ColorMarkerPoint;
import org.caleydo.core.util.mapping.color.EColorMappingType;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering;
import org.caleydo.core.view.opengl.mouse.PickingMouseListener;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;

/**
 * Rendering the histogram.
 * 
 * @author Alexander Lex
 */
public class GLHistogram
	extends AGLEventListener {

	boolean bUseDetailLevel = true;
	ISet set;

	private Histogram histogram;
	private ColorMapping colorMapping;
	HistogramRenderStyle renderStyle;

	private boolean bUpdateColorPointPosition = false;
	private boolean bUpdateLeftSpread = false;
	private boolean bUpdateRightSpread = false;
	private boolean bIsFirstTimeUpdateColor = false;
	private float fColorPointPositionOffset = 0.0f;
	private int iColorMappingPointMoved = -1;

	/**
	 * Constructor.
	 * 
	 * @param iViewID
	 * @param iGLCanvasID
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLHistogram(ESetType setType, final int iGLCanvasID, final String sLabel,
		final IViewFrustum viewFrustum) {
		super(iGLCanvasID, sLabel, viewFrustum, true);

		viewType = EManagedObjectType.GL_HISTOGRAM;

		colorMapping = ColorMappingManager.get().getColorMapping(EColorMappingType.GENE_EXPRESSION);

		renderStyle = new HistogramRenderStyle(this, viewFrustum);
	}

	@Override
	public void init(GL gl) {

		for (ISet tempSet : alSets) {
			if (tempSet.getSetType() == ESetType.GENE_EXPRESSION_DATA) {
				set = tempSet;
			}
		}

		// if (set == null)
		// return;

		histogram = set.getHistogram();
	}

	@Override
	public void initLocal(GL gl) {

		iGLDisplayListIndexLocal = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexLocal;
		init(gl);
	}

	@Override
	public void initRemote(final GL gl, final int iRemoteViewID,
		final PickingMouseListener pickingTriggerMouseAdapter,
		final IGLCanvasRemoteRendering remoteRenderingGLCanvas) {

		this.remoteRenderingGLCanvas = remoteRenderingGLCanvas;

		this.pickingTriggerMouseAdapter = pickingTriggerMouseAdapter;

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;
		init(gl);

	}

	@Override
	public synchronized void setDetailLevel(EDetailLevel detailLevel) {
		if (bUseDetailLevel) {
			super.setDetailLevel(detailLevel);
			// renderStyle.setDetailLevel(detailLevel);
		}

	}

	@Override
	public synchronized void displayLocal(GL gl) {
		pickingManager.handlePicking(iUniqueID, gl);

		if (bIsDisplayListDirtyLocal) {
			buildDisplayList(gl, iGLDisplayListIndexLocal);
			bIsDisplayListDirtyLocal = false;
		}
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		display(gl);
		checkForHits(gl);

		if (eBusyModeState != EBusyModeState.OFF) {
			renderBusyMode(gl);
		}
	}

	@Override
	public synchronized void displayRemote(GL gl) {
		if (bIsDisplayListDirtyRemote) {
			buildDisplayList(gl, iGLDisplayListIndexRemote);
			bIsDisplayListDirtyRemote = false;
		}
		iGLDisplayListToCall = iGLDisplayListIndexRemote;

		display(gl);
		checkForHits(gl);

		// pickingTriggerMouseAdapter.resetEvents();
	}

	@Override
	public synchronized void display(GL gl) {
		render(gl);
		if (bUpdateColorPointPosition || bUpdateLeftSpread || bUpdateRightSpread)
			updateColorPointPosition(gl);
		// clipToFrustum(gl);
		//
		// gl.glCallList(iGLDisplayListToCall);

		// buildDisplayList(gl, iGLDisplayListIndexRemote);
	}

	private void buildDisplayList(final GL gl, int iGLDisplayListIndex) {

	}

	private void render(GL gl) {

		float fSpacing = (viewFrustum.getWidth() - 2 * SIDE_SPACING) / histogram.size();
		float fContinuousColorRegion = 1.0f / histogram.size();
		float fRenderWidth = (viewFrustum.getWidth() - 2 * SIDE_SPACING);
		float fOneHeightValue = (viewFrustum.getHeight() - 2 * SIDE_SPACING) / histogram.getLargestValue();

		int iCount = 0;
		for (Integer iValue : histogram) {
			gl.glColor3fv(colorMapping.getColor(fContinuousColorRegion * iCount), 0);
			gl.glLineWidth(3.0f);
			gl.glBegin(GL.GL_POLYGON);

			gl.glVertex3f(fSpacing * iCount + SIDE_SPACING, SIDE_SPACING, 0);
			gl.glVertex3f(fSpacing * iCount + SIDE_SPACING, SIDE_SPACING + iValue * fOneHeightValue, 0);
			gl.glVertex3f(fSpacing * (iCount + 1) + SIDE_SPACING, SIDE_SPACING + iValue * fOneHeightValue, 0);
			gl.glVertex3f(fSpacing * (iCount + 1) + SIDE_SPACING, SIDE_SPACING, 0);
			gl.glEnd();
			iCount++;
		}

		ArrayList<ColorMarkerPoint> markerPoints = colorMapping.getMarkerPoints();

		iCount = 0;
		
		for (ColorMarkerPoint markerPoint : markerPoints) {
			int iColorLinePickingID = pickingManager.getPickingID(iUniqueID, EPickingType.HISTOGRAM_COLOR_LINE, iCount);
			
			gl.glColor3f(0, 0, 1);
			gl.glPushName(iColorLinePickingID);
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(SIDE_SPACING + markerPoint.getValue() * fRenderWidth, 0, 0);
			gl.glVertex3f(SIDE_SPACING + markerPoint.getValue() * fRenderWidth, viewFrustum.getHeight(), 0);
			gl.glEnd();
			gl.glPopName();

			if (markerPoint.hasLeftSpread()) {

				float fLeftSpread = markerPoint.getLeftSpread();
				gl.glPushName(pickingManager.getPickingID(iUniqueID,
					EPickingType.HISTOGRAM_LEFT_SPREAD_COLOR_LINE, iCount));
				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(SIDE_SPACING + (markerPoint.getValue() - fLeftSpread) * fRenderWidth, 0, 0);
				gl.glVertex3f(SIDE_SPACING + (markerPoint.getValue() - fLeftSpread) * fRenderWidth,
					viewFrustum.getHeight(), 0);
				gl.glEnd();
				gl.glPopName();

				gl.glColor4f(markerPoint.getColor()[0], markerPoint.getColor()[1], markerPoint.getColor()[2],
					0.5f);

				gl.glPushName(iColorLinePickingID);
				gl.glBegin(GL.GL_POLYGON);
				gl.glVertex3f(SIDE_SPACING + (markerPoint.getValue() - fLeftSpread) * fRenderWidth,
					SIDE_SPACING, -0.1f);
				gl.glVertex3f(SIDE_SPACING + (markerPoint.getValue() - fLeftSpread) * fRenderWidth,
					viewFrustum.getHeight() - SIDE_SPACING, -0.1f);
				gl.glVertex3f(SIDE_SPACING + markerPoint.getValue() * fRenderWidth, viewFrustum.getHeight()
					- SIDE_SPACING,-0.1f);
				gl.glVertex3f(SIDE_SPACING + markerPoint.getValue() * fRenderWidth, SIDE_SPACING, -0.1f);
				gl.glEnd();
				gl.glPopName();

			}

			if (markerPoint.hasRightSpread()) {
				float fRightSpread = markerPoint.getRightSpread();

				gl.glColor3f(0, 0, 1);
				gl.glPushName(pickingManager.getPickingID(iUniqueID,
					EPickingType.HISTOGRAM_RIGHT_SPREAD_COLOR_LINE, iCount));
				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(SIDE_SPACING + (markerPoint.getValue() + fRightSpread) * fRenderWidth, 0, 0);
				gl.glVertex3f(SIDE_SPACING + (markerPoint.getValue() + fRightSpread) * fRenderWidth,
					viewFrustum.getHeight(), 0);
				gl.glEnd();
				gl.glPopName();

				gl.glColor4f(markerPoint.getColor()[0], markerPoint.getColor()[1], markerPoint.getColor()[2],
					0.5f);

				gl.glPushName(iColorLinePickingID);
				gl.glBegin(GL.GL_POLYGON);
				gl.glVertex3f(SIDE_SPACING + markerPoint.getValue() * fRenderWidth, SIDE_SPACING, -0.1f);
				gl.glVertex3f(SIDE_SPACING + markerPoint.getValue() * fRenderWidth, viewFrustum.getHeight()
					- SIDE_SPACING, -0.1f);
				gl.glVertex3f(SIDE_SPACING + (markerPoint.getValue() + fRightSpread) * fRenderWidth,
					viewFrustum.getHeight() - SIDE_SPACING,-0.1f);
				gl.glVertex3f(SIDE_SPACING + (markerPoint.getValue() + fRightSpread) * fRenderWidth,
					SIDE_SPACING, -0.1f);
				gl.glEnd();
				gl.glPopName();
			}
			iCount++;
		}

	}

	private void updateColorPointPosition(GL gl) {
		if (pickingTriggerMouseAdapter.wasMouseReleased()) {
			bUpdateColorPointPosition = false;
			bUpdateLeftSpread = false;
			bUpdateRightSpread = false;
		}


		Point currentPoint = pickingTriggerMouseAdapter.getPickedPoint();

		float[] fArTargetWorldCoordinates =
			GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x, currentPoint.y);

		ArrayList<ColorMarkerPoint> markerPoints = colorMapping.getMarkerPoints();
		ColorMarkerPoint point = markerPoints.get(iColorMappingPointMoved);
		
		float fWidth = fArTargetWorldCoordinates[0];
		
		if(bIsFirstTimeUpdateColor && bUpdateColorPointPosition)
		{
			bIsFirstTimeUpdateColor = false;
			fColorPointPositionOffset = fWidth - point.getValue() * (viewFrustum.getWidth() - 2* SIDE_SPACING);
			fWidth -= fColorPointPositionOffset;
		}
		else if(bUpdateColorPointPosition)
		{
			fWidth -= fColorPointPositionOffset;
		}
		

		if (fWidth < SIDE_SPACING)
			fWidth = SIDE_SPACING;
		if (fWidth > viewFrustum.getWidth() - SIDE_SPACING)
			fWidth = viewFrustum.getWidth() - SIDE_SPACING;

		fWidth = (fWidth - SIDE_SPACING) / (viewFrustum.getWidth() - 2 * SIDE_SPACING);
		
	

	
		if (iColorMappingPointMoved > 0) {
			ColorMarkerPoint previousPoint = markerPoints.get(iColorMappingPointMoved - 1);
			float fRightOfPrevious = previousPoint.getValue();

			fRightOfPrevious += previousPoint.getRightSpread();

			float fCurrentLeft = fWidth;
			if (bUpdateColorPointPosition) {
				fCurrentLeft -= point.getLeftSpread();
				if (fCurrentLeft <= fRightOfPrevious + 0.01f)
					fWidth = fRightOfPrevious + 0.01f + point.getLeftSpread();
			}
			if (bUpdateLeftSpread) {
				if (fCurrentLeft <= fRightOfPrevious + 0.01f)
					fWidth = fRightOfPrevious + 0.01f;
			}

		}

		if (iColorMappingPointMoved < markerPoints.size() - 1) {
			ColorMarkerPoint nextPoint = markerPoints.get(iColorMappingPointMoved + 1);
			float fLeftOfNext = nextPoint.getValue();

			fLeftOfNext -= nextPoint.getLeftSpread();

			float fCurrentRight = fWidth;
			if (bUpdateColorPointPosition) {
				fCurrentRight += point.getRightSpread();
				if (fCurrentRight >= fLeftOfNext - 0.01f)
					fWidth = fLeftOfNext - 0.01f - point.getRightSpread();
			}
			if (bUpdateRightSpread) {
				if (fCurrentRight >= fLeftOfNext - 0.01f)
					fWidth = fLeftOfNext - 0.01f;
			}

		}

		if (bUpdateColorPointPosition) {
			point.setValue(fWidth);
		}
		else if (bUpdateLeftSpread) {
			float fTargetValue = point.getValue() - fWidth;
			if (fTargetValue < 0.01f)
				fTargetValue = 0.01f;
			point.setLeftSpread(fTargetValue);
		}
		else if (bUpdateRightSpread) {
			float fTargetValue = fWidth - point.getValue();
			if (fTargetValue < 0.01f)
				fTargetValue = 0.01f;
			point.setRightSpread(fTargetValue);
		}
		colorMapping.update();
	}

	@Override
	public String getDetailedInfo() {
		return new String("");
	}

	@Override
	protected void handleEvents(EPickingType ePickingType, EPickingMode pickingMode, int iExternalID,
		Pick pick) {
		if (detailLevel == EDetailLevel.VERY_LOW) {
			pickingManager.flushHits(iUniqueID, ePickingType);
			return;
		}
		switch (ePickingType) {

			case HISTOGRAM_COLOR_LINE:

				switch (pickingMode) {
					case CLICKED:
						bUpdateColorPointPosition = true;
						bIsFirstTimeUpdateColor = true;
						iColorMappingPointMoved = iExternalID;
						// colorManager.initColorMapping(EColorMappingType.GENE_EXPRESSION, )
						break;
					case MOUSE_OVER:

						break;
					default:
						pickingManager.flushHits(iUniqueID, ePickingType);
						return;
				}
				setDisplayListDirty();
				break;
			case HISTOGRAM_LEFT_SPREAD_COLOR_LINE:
				switch (pickingMode) {
					case CLICKED:
						bUpdateLeftSpread = true;
						iColorMappingPointMoved = iExternalID;
						// colorManager.initColorMapping(EColorMappingType.GENE_EXPRESSION, )
						break;
					case MOUSE_OVER:

						break;
					default:
						pickingManager.flushHits(iUniqueID, ePickingType);
						return;
				}
				setDisplayListDirty();
				break;
			case HISTOGRAM_RIGHT_SPREAD_COLOR_LINE:
				switch (pickingMode) {
					case CLICKED:
						bUpdateRightSpread = true;
						iColorMappingPointMoved = iExternalID;
						// colorManager.initColorMapping(EColorMappingType.GENE_EXPRESSION, )
						break;
					case MOUSE_OVER:

						break;
					default:
						pickingManager.flushHits(iUniqueID, ePickingType);
						return;
				}
				setDisplayListDirty();
				break;
		}

		pickingManager.flushHits(iUniqueID, ePickingType);
	}

	@Override
	public void broadcastElements(EVAOperation type) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getNumberOfSelections(ESelectionType selectionType) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getShortInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearAllSelections() {
		// TODO Auto-generated method stub

	}

}
