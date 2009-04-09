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
import org.caleydo.core.manager.picking.PickingManager;
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
import org.caleydo.core.view.opengl.util.GLHelperFunctions;

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

	boolean bUpdateColorPointPosition = false;

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

		set = alSets.get(0);
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
		if (bUpdateColorPointPosition)
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
			gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.HISTOGRAM_COLOR_LINE, iCount));
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(SIDE_SPACING + markerPoint.getValue() * fRenderWidth, 0, 0);
			gl.glVertex3f(SIDE_SPACING + markerPoint.getValue() * fRenderWidth, viewFrustum.getHeight(), 0);
			gl.glEnd();
			gl.glPopName();
			iCount++;
		}

	}

	private void updateColorPointPosition(GL gl) {
		if (pickingTriggerMouseAdapter.wasMouseReleased())
			bUpdateColorPointPosition = false;

		Point currentPoint = pickingTriggerMouseAdapter.getPickedPoint();

		float[] fArTargetWorldCoordinates =
			GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x, currentPoint.y);

		float fWidth = fArTargetWorldCoordinates[0];
		
		if (fWidth < SIDE_SPACING)
			fWidth = SIDE_SPACING;
		if (fWidth > viewFrustum.getWidth() - SIDE_SPACING)
			fWidth = viewFrustum.getWidth() - SIDE_SPACING;
		
		fWidth = (fWidth - SIDE_SPACING) / (viewFrustum.getWidth() - 2 * SIDE_SPACING);

		// ColorMappingManager colorManager = ColorMappingManager.get();
		ArrayList<ColorMarkerPoint> markerPoints = colorMapping.getMarkerPoints();

		markerPoints.get(1).setValue(fWidth);
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
