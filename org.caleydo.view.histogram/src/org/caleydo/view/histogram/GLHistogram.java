/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.histogram;

import static org.caleydo.view.histogram.HistogramRenderStyle.SIDE_SPACING;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.collection.table.CategoricalTable;
import org.caleydo.core.data.collection.table.NumericalTable;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.datadomain.IDataSupportDefinition;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.view.RedrawViewEvent;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.mapping.ColorMarkerPoint;
import org.caleydo.core.util.color.mapping.IColorMappingUpdateListener;
import org.caleydo.core.util.color.mapping.UpdateColorMappingEvent;
import org.caleydo.core.util.color.mapping.UpdateColorMappingListener;
import org.caleydo.core.util.format.Formatter;
import org.caleydo.core.view.ISingleTablePerspectiveBasedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.eclipse.swt.widgets.Composite;

/**
 * Rendering the histogram.
 *
 * @author Alexander Lex
 */
public class GLHistogram extends AGLView implements ISingleTablePerspectiveBasedView, IColorMappingUpdateListener {
	public static final String VIEW_TYPE = "org.caleydo.view.histogram";
	public static final String VIEW_NAME = "Histogram";

	private TablePerspective tablePerspective;
	private ATableBasedDataDomain dataDomain;

	private boolean useDetailLevel = true;

	private boolean useColor = true;

	private boolean renderColorBars = true;

	private Histogram histogram;
	// private HistogramRenderStyle renderStyle;

	private boolean bUpdateColorPointPosition = false;
	private boolean bUpdateLeftSpread = false;
	private boolean bUpdateRightSpread = false;
	private boolean bIsFirstTimeUpdateColor = false;
	private float fColorPointPositionOffset = 0.0f;
	private int iColorMappingPointMoved = -1;

	private static float[] SPREAD_LINE_COLOR = { 0.5f, 0.5f, 0.5f };

	float fRenderWidth;
	float sideSpacing = 0;

	/** Listener for changes in color mapping */
	private UpdateColorMappingListener updateColorMappingListener;

	/**
	 * Constructor.
	 *
	 * @param glCanvas
	 * @param viewLabel
	 * @param viewFrustum
	 */
	public GLHistogram(IGLCanvas glCanvas, Composite parentComposite, ViewFrustum viewFrustum) {

		super(glCanvas, parentComposite, viewFrustum, VIEW_TYPE, VIEW_NAME);

		// registerEventListeners();

		detailLevel = EDetailLevel.HIGH;
	}

	public void setRenderColorBars(boolean renderColorBars) {
		this.renderColorBars = renderColorBars;
	}

	@Override
	public void init(GL2 gl) {
		displayListIndex = gl.glGenLists(1);
		textRenderer = new CaleydoTextRenderer(18);
		createPickingListeners();
	}

	@Override
	public void initLocal(GL2 gl) {
		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView, final GLMouseListener glMouseListener) {
		this.glMouseListener = glMouseListener;
		init(gl);
	}

	@Override
	public void initData() {
		super.initData();
		if (tablePerspective != null && histogram == null) {
			if (tablePerspective.getDataDomain().getTable() instanceof NumericalTable) {
				histogram = tablePerspective.getContainerStatistics().getHistogram();
			}
		}
	}

	public void setHistogram(Histogram histogram) {
		this.histogram = histogram;
	}

	@Override
	public void setDetailLevel(EDetailLevel detailLevel) {
		super.setDetailLevel(detailLevel);

	}

	@Override
	public void displayLocal(GL2 gl) {
		if (!lazyMode)
			pickingManager.handlePicking(this, gl);
		display(gl);
	}

	@Override
	public void displayRemote(GL2 gl) {
		display(gl);

	}

	@Override
	public void display(GL2 gl) {
		processEvents();
		if (bUpdateColorPointPosition || bUpdateLeftSpread || bUpdateRightSpread)
			updateColorPointPosition(gl);

		if (isDisplayListDirty) {
			buildDisplayList(gl, displayListIndex);
			isDisplayListDirty = false;
		}

		gl.glCallList(displayListIndex);

		if (!lazyMode)
			checkForHits(gl);
	}

	private void buildDisplayList(final GL2 gl, int iGLDisplayListIndex) {
		gl.glNewList(iGLDisplayListIndex, GL2.GL_COMPILE);

		if (useDetailLevel) {
			// renderStyle.setDetailLevel(detailLevel);
			if (detailLevel == EDetailLevel.LOW || detailLevel == EDetailLevel.MEDIUM) {
				sideSpacing = pixelGLConverter.getGLWidthForPixelWidth(HistogramRenderStyle.SIDE_SPACING_DETAIL_LOW);
			} else {
				sideSpacing = pixelGLConverter.getGLWidthForPixelWidth(SIDE_SPACING);
			}
		}

		renderHistogram(gl);
		if (renderColorBars && detailLevel != EDetailLevel.LOW)
			renderColorBars(gl);
		gl.glEndList();
	}

	/**
	 * Render the histogram itself
	 *
	 * @param gl
	 */
	private void renderHistogram(GL2 gl) {
		if (histogram == null) {
			if (dataDomain != null && tablePerspective != null) {
				histogram = tablePerspective.getContainerStatistics().getHistogram();
			} else if (dataDomain != null) {
				TablePerspective defaultTablePerspective = dataDomain.getDefaultTablePerspective();
				histogram = defaultTablePerspective.getContainerStatistics().getHistogram();
			} else {
				return;
			}
		}

		float spacing = (viewFrustum.getWidth() - 2 * sideSpacing) / histogram.size();
		float continuousColorDistance = 1.0f / histogram.size();

		float fOneHeightValue = (viewFrustum.getHeight() - 2 * sideSpacing) / histogram.getLargestValue();

		int iCount = 0;

		for (int bucketCount = 0; bucketCount < histogram.size(); bucketCount++) {
			Integer iValue = histogram.get(bucketCount);

			if (useColor) {
				float[] color;

				if (dataDomain.getTable() instanceof CategoricalTable<?>) {
					CategoricalTable<?> cTable = (CategoricalTable<?>) dataDomain.getTable();
					color = cTable.getCategoryDescriptions().getCategoryProperties().get(bucketCount).getColor()
							.getRGBA();
				} else if (!dataDomain.getTable().isDataHomogeneous() && tablePerspective != null) {
					CategoricalClassDescription<?> specific = (CategoricalClassDescription<?>) dataDomain.getTable()
							.getDataClassSpecificDescription(
									tablePerspective.getDimensionPerspective().getVirtualArray().get(0),
									tablePerspective.getRecordPerspective().getVirtualArray().get(0));

					color = specific.getCategoryProperties().get(bucketCount).getColor().getRGBA();
				} else {
					color = dataDomain.getColorMapper().getColor(
							continuousColorDistance * iCount + continuousColorDistance / 2);
				}
				gl.glColor3fv(color, 0);
			}

			gl.glBegin(GL2.GL_POLYGON);
			gl.glVertex3f(spacing * iCount + sideSpacing, sideSpacing, 0);
			gl.glVertex3f(spacing * iCount + sideSpacing, sideSpacing + iValue * fOneHeightValue, 0);
			gl.glVertex3f(spacing * (iCount + 1) + sideSpacing, sideSpacing + iValue * fOneHeightValue, 0);
			gl.glVertex3f(spacing * (iCount + 1) + sideSpacing, sideSpacing, 0);
			gl.glEnd();

			gl.glLineWidth(0.3f);

			gl.glColor3fv(Color.DARK_GRAY.getRGBA(), 0);
			gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex3f(spacing * iCount + sideSpacing, sideSpacing, 0);
			gl.glVertex3f(spacing * iCount + sideSpacing, sideSpacing + iValue * fOneHeightValue, 0);
			gl.glVertex3f(spacing * (iCount + 1) + sideSpacing, sideSpacing + iValue * fOneHeightValue, 0);
			gl.glVertex3f(spacing * (iCount + 1) + sideSpacing, sideSpacing, 0);
			gl.glEnd();

			iCount++;
		}

	}

	/**
	 * Render the color bars for selecting the color mapping
	 *
	 * @param gl
	 */
	private void renderColorBars(GL2 gl) {

		fRenderWidth = (viewFrustum.getWidth() - 2 * sideSpacing);
		List<ColorMarkerPoint> markerPoints = dataDomain.getColorMapper().getMarkerPoints();

		int iCount = 0;

		for (ColorMarkerPoint markerPoint : markerPoints) {
			int iColorLinePickingID = pickingManager.getPickingID(uniqueID, EPickingType.HISTOGRAM_COLOR_LINE.name(),
					iCount);

			boolean bIsFirstOrLast = false;
			float fPickingScaling = 0.8f;
			if (iCount == 0 || iCount == markerPoints.size() - 1)
				bIsFirstOrLast = true;

			if (markerPoint.hasLeftSpread()) {

				float fLeftSpread = markerPoint.getLeftSpread();
				int iLeftSpreadPickingID = pickingManager.getPickingID(uniqueID,
						EPickingType.HISTOGRAM_LEFT_SPREAD_COLOR_LINE.name(), iCount);

				// the left polygon between the central line and the spread
				Color color = markerPoint.getColor();
				gl.glColor4f(color.r, color.g, color.b, 0.3f);

				float fLeft = sideSpacing + (markerPoint.getMappingValue() - fLeftSpread) * fRenderWidth;
				float fRight = sideSpacing + markerPoint.getMappingValue() * fRenderWidth;

				// the right part which picks the central line
				if (!bIsFirstOrLast)
					gl.glPushName(iColorLinePickingID);
				gl.glBegin(GL2.GL_POLYGON);
				gl.glVertex3f(fRight + fPickingScaling * (fLeft - fRight), sideSpacing, 0.01f);
				gl.glVertex3f(fRight + fPickingScaling * (fLeft - fRight), viewFrustum.getHeight() - sideSpacing, 0.01f);
				gl.glVertex3f(fRight, viewFrustum.getHeight() - sideSpacing, 0.01f);
				gl.glVertex3f(fRight, sideSpacing, 0.01f);
				gl.glEnd();
				if (!bIsFirstOrLast)
					gl.glPopName();

				// the left part which picks the spread
				gl.glPushName(iLeftSpreadPickingID);
				gl.glBegin(GL2.GL_POLYGON);
				gl.glVertex3f(fLeft, sideSpacing, 0.01f);
				gl.glVertex3f(fLeft, viewFrustum.getHeight() - sideSpacing, 0.01f);
				gl.glVertex3f(fRight + fPickingScaling * (fLeft - fRight), viewFrustum.getHeight() - sideSpacing, 0.01f);
				gl.glVertex3f(fRight + fPickingScaling * (fLeft - fRight), sideSpacing, 0.01f);
				gl.glEnd();
				gl.glPopName();

				// the left spread line
				gl.glColor3fv(SPREAD_LINE_COLOR, 0);
				gl.glPushName(iLeftSpreadPickingID);
				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(sideSpacing + (markerPoint.getMappingValue() - fLeftSpread) * fRenderWidth, 0, 0.1f);
				gl.glVertex3f(sideSpacing + (markerPoint.getMappingValue() - fLeftSpread) * fRenderWidth,
						viewFrustum.getHeight(), 0.1f);
				gl.glEnd();
				gl.glPopName();
				if (fLeftSpread > HistogramRenderStyle.SPREAD_CAPTION_THRESHOLD)
					renderCaption(gl, markerPoint.getMappingValue() - fLeftSpread);

			}

			if (markerPoint.hasRightSpread()) {
				float fRightSpread = markerPoint.getRightSpread();

				float fLeft = sideSpacing + markerPoint.getMappingValue() * fRenderWidth;
				float fRight = sideSpacing + (markerPoint.getMappingValue() + fRightSpread) * fRenderWidth;

				int iRightSpreadPickingID = pickingManager.getPickingID(uniqueID,
						EPickingType.HISTOGRAM_RIGHT_SPREAD_COLOR_LINE.name(), iCount);

				// the polygon between the central line and the right spread
				// the first part which picks the central line
				Color color = markerPoint.getColor();
				gl.glColor4f(color.r, color.g, color.b, 0.3f);
				if (!bIsFirstOrLast)
					gl.glPushName(iColorLinePickingID);
				gl.glBegin(GL2.GL_POLYGON);
				gl.glVertex3f(fLeft, sideSpacing, 0.01f);
				gl.glVertex3f(fLeft, viewFrustum.getHeight() - sideSpacing, 0.01f);
				gl.glVertex3f(fLeft + fPickingScaling * (fRight - fLeft), viewFrustum.getHeight() - sideSpacing, 0.01f);
				gl.glVertex3f(fLeft + fPickingScaling * (fRight - fLeft), sideSpacing, 0.01f);
				gl.glEnd();
				if (!bIsFirstOrLast)
					gl.glPopName();

				// the second part which picks the spread
				gl.glPushName(iRightSpreadPickingID);
				gl.glBegin(GL2.GL_POLYGON);
				gl.glVertex3f(fLeft + fPickingScaling * (fRight - fLeft), sideSpacing, 0.01f);
				gl.glVertex3f(fLeft + fPickingScaling * (fRight - fLeft), viewFrustum.getHeight() - sideSpacing, 0.01f);
				gl.glVertex3f(fRight, viewFrustum.getHeight() - sideSpacing, 0.01f);
				gl.glVertex3f(fRight, sideSpacing, 0.01f);
				gl.glEnd();
				gl.glPopName();

				// the right spread line
				gl.glColor3fv(SPREAD_LINE_COLOR, 0);
				gl.glPushName(iRightSpreadPickingID);
				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(sideSpacing + (markerPoint.getMappingValue() + fRightSpread) * fRenderWidth, 0, 0.01f);
				gl.glVertex3f(sideSpacing + (markerPoint.getMappingValue() + fRightSpread) * fRenderWidth,
						viewFrustum.getHeight(), 0.01f);
				gl.glEnd();
				gl.glPopName();
				if (fRightSpread > HistogramRenderStyle.SPREAD_CAPTION_THRESHOLD)
					renderCaption(gl, markerPoint.getMappingValue() + fRightSpread);

			}

			// the central line
			// gl.glColor3f(0, 0, 1);
			// if (!bIsFirstOrLast)
			// gl.glPushName(iColorLinePickingID);
			// gl.glBegin(GL.GL_LINES);
			// gl.glVertex3f(SIDE_SPACING + markerPoint.getValue() *
			// fRenderWidth,
			// 0, 0);
			// gl.glVertex3f(SIDE_SPACING + markerPoint.getValue() *
			// fRenderWidth,
			// viewFrustum.getHeight(), 0);
			// gl.glEnd();
			// if (!bIsFirstOrLast)
			// gl.glPopName();

			renderCaption(gl, markerPoint.getMappingValue());

			iCount++;
		}

	}

	private void renderCaption(GL2 gl, float normalizedValue) {

		if (detailLevel != EDetailLevel.HIGH || !(dataDomain.getTable() instanceof NumericalTable))
			return;

		double correspondingValue = ((NumericalTable) dataDomain.getTable()).getRawForNormalized(dataDomain.getTable()
				.getDefaultDataTransformation(), normalizedValue);

		String text = Formatter.formatNumber(correspondingValue);
		textRenderer.renderTextInBounds(gl, text, sideSpacing + normalizedValue * fRenderWidth
				+ HistogramRenderStyle.CAPTION_SPACING, 0, 0.01f, pixelGLConverter.getGLWidthForPixelWidth(100),
				pixelGLConverter.getGLHeightForPixelHeight(HistogramRenderStyle.SIDE_SPACING));
	}

	/**
	 * React on drag operations of the color lines and areas
	 *
	 * @param gl
	 */
	private void updateColorPointPosition(GL2 gl) {
		if (glMouseListener.wasMouseReleased()) {
			// send out a major update which tells the hhm to update its
			// textures
			UpdateColorMappingEvent event = new UpdateColorMappingEvent();
			event.setSender(this);
			eventPublisher.triggerEvent(event);

			bUpdateColorPointPosition = false;
			bUpdateLeftSpread = false;
			bUpdateRightSpread = false;
		}

		setDisplayListDirty();
		Point currentPoint = glMouseListener.getPickedPoint();

		float[] fArTargetWorldCoordinates = GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl,
				currentPoint.x, currentPoint.y);

		List<ColorMarkerPoint> markerPoints = dataDomain.getColorMapper().getMarkerPoints();
		ColorMarkerPoint markerPoint = markerPoints.get(iColorMappingPointMoved);

		float fClickedPointX = fArTargetWorldCoordinates[0];

		if (bIsFirstTimeUpdateColor && bUpdateColorPointPosition) {
			bIsFirstTimeUpdateColor = false;
			fColorPointPositionOffset = fClickedPointX - sideSpacing - markerPoint.getMappingValue()
					* (viewFrustum.getWidth() - 2 * sideSpacing);
			fClickedPointX -= fColorPointPositionOffset;
		} else if (bUpdateColorPointPosition) {
			fClickedPointX -= fColorPointPositionOffset;
		}

		if (fClickedPointX < sideSpacing)
			fClickedPointX = sideSpacing;
		if (fClickedPointX > viewFrustum.getWidth() - sideSpacing)
			fClickedPointX = viewFrustum.getWidth() - sideSpacing;

		fClickedPointX = (fClickedPointX - sideSpacing) / (viewFrustum.getWidth() - 2 * sideSpacing);

		if (iColorMappingPointMoved > 0) {
			ColorMarkerPoint previousPoint = markerPoints.get(iColorMappingPointMoved - 1);
			float fRightOfPrevious = previousPoint.getMappingValue();

			fRightOfPrevious += previousPoint.getRightSpread();

			float fCurrentLeft = fClickedPointX;
			if (bUpdateColorPointPosition) {
				fCurrentLeft -= markerPoint.getLeftSpread();
				if (fCurrentLeft <= fRightOfPrevious + 0.01f)
					fClickedPointX = fRightOfPrevious + 0.01f + markerPoint.getLeftSpread();
			}
			if (bUpdateLeftSpread) {
				if (fCurrentLeft <= fRightOfPrevious + 0.01f)
					fClickedPointX = fRightOfPrevious + 0.01f;
			}

		}

		if (iColorMappingPointMoved < markerPoints.size() - 1) {
			ColorMarkerPoint nextPoint = markerPoints.get(iColorMappingPointMoved + 1);
			float fLeftOfNext = nextPoint.getMappingValue();

			fLeftOfNext -= nextPoint.getLeftSpread();

			float fCurrentRight = fClickedPointX;
			if (bUpdateColorPointPosition) {
				fCurrentRight += markerPoint.getRightSpread();
				if (fCurrentRight >= fLeftOfNext - 0.01f)
					fClickedPointX = fLeftOfNext - 0.01f - markerPoint.getRightSpread();
			}
			if (bUpdateRightSpread) {
				if (fCurrentRight >= fLeftOfNext - 0.01f)
					fClickedPointX = fLeftOfNext - 0.01f;
			}

		}

		if (bUpdateColorPointPosition) {
			if (fClickedPointX < 0)
				fClickedPointX = 0;
			if (fClickedPointX > 1)
				fClickedPointX = 1;
			markerPoint.setMappingValue(fClickedPointX);
		} else if (bUpdateLeftSpread) {
			float fTargetValue = markerPoint.getMappingValue() - fClickedPointX;
			if (fTargetValue < 0.01f)
				fTargetValue = 0.01f;
			markerPoint.setLeftSpread(fTargetValue);
		} else if (bUpdateRightSpread) {
			float fTargetValue = fClickedPointX - markerPoint.getMappingValue();
			if (fTargetValue < 0.01f)
				fTargetValue = 0.01f;
			markerPoint.setRightSpread(fTargetValue);
		}
		dataDomain.getColorMapper().update();

		RedrawViewEvent event = new RedrawViewEvent();
		event.setSender(this);
		event.setEventSpace(dataDomain.getDataDomainID());
		eventPublisher.triggerEvent(event);
	}

	private void createPickingListeners() {

		addTypePickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				bUpdateColorPointPosition = true;
				bIsFirstTimeUpdateColor = true;
				iColorMappingPointMoved = pick.getObjectID();
				setDisplayListDirty();
			}

		}, EPickingType.HISTOGRAM_COLOR_LINE.name());

		addTypePickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				bUpdateLeftSpread = true;
				iColorMappingPointMoved = pick.getObjectID();
				setDisplayListDirty();
			}
		}, EPickingType.HISTOGRAM_LEFT_SPREAD_COLOR_LINE.name());

		addTypePickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				bUpdateRightSpread = true;
				iColorMappingPointMoved = pick.getObjectID();
				setDisplayListDirty();
			}
		}, EPickingType.HISTOGRAM_RIGHT_SPREAD_COLOR_LINE.name());

	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedHistogramView serializedForm = new SerializedHistogramView(this);
		return serializedForm;
	}

	@Override
	public int getMinPixelHeight(EDetailLevel detailLevel) {
		switch (detailLevel) {
		case HIGH:
			return 300;
		case MEDIUM:
			return 100;
		case LOW:
			return 40;
		default:
			return 40;
		}
	}

	@Override
	public int getMinPixelWidth(EDetailLevel detailLevel) {
		switch (detailLevel) {
		case HIGH:
			return 300;
		case MEDIUM:
			return 100;
		case LOW:
			return 40;
		default:
			return 40;
		}
	}

	/**
	 * Determines color mode of histogram.
	 *
	 * @param useColor
	 *            If false the histogram is rendered B/W
	 */
	public void setUseColor(boolean useColor) {
		this.useColor = useColor;
	}

	@Override
	public boolean isDataView() {
		return false;
	}

	@Override
	protected void destroyViewSpecificContent(GL2 gl) {
		gl.glDeleteLists(displayListIndex, 1);
	}

	@Override
	public IDataSupportDefinition getDataSupportDefinition() {
		return DataSupportDefinitions.tableBased;
	}

	/**
	 * Sets the datadomain and clears the histogram and sets the display list dirty. Can be set at runtime.
	 */
	@Override
	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
		histogram = null;
		isDisplayListDirty = true;
	}

	@Override
	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	/**
	 * Sets the TablePerspective and clears the histogram and sets the display list dirty. Can be set at runtime.
	 */
	@Override
	public void setTablePerspective(TablePerspective tablePerspective) {
		this.tablePerspective = tablePerspective;
		histogram = null;
		isDisplayListDirty = true;
	}

	@Override
	public TablePerspective getTablePerspective() {
		return tablePerspective;
	}

	@Override
	public List<TablePerspective> getTablePerspectives() {
		ArrayList<TablePerspective> tablePerspectives = new ArrayList<TablePerspective>(1);
		tablePerspectives.add(tablePerspective);
		return tablePerspectives;
	}

	@Override
	public void updateColorMapping() {
		setDisplayListDirty();
	}

	@Override
	public void registerEventListeners() {

		super.registerEventListeners();
		updateColorMappingListener = new UpdateColorMappingListener();
		updateColorMappingListener.setHandler(this);
		eventPublisher.addListener(UpdateColorMappingEvent.class, updateColorMappingListener);
	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();
		if (updateColorMappingListener != null) {
			eventPublisher.removeListener(updateColorMappingListener);
			updateColorMappingListener = null;
		}
	}

}
