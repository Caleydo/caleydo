/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.kaplanmeier;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;

import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.datadomain.IDataSupportDefinition;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.data.selection.IEventBasedSelectionManagerUser;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.ISingleTablePerspectiveBasedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.connectionline.ConnectionLineRenderer;
import org.caleydo.core.view.opengl.util.connectionline.LineCrossingRenderer;
import org.caleydo.core.view.opengl.util.connectionline.LineLabelRenderer;
import org.caleydo.core.view.opengl.util.connectionline.LineLabelRenderer.EAlignmentX;
import org.caleydo.core.view.opengl.util.connectionline.LineLabelRenderer.EAlignmentY;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;

/**
 * <p>
 * Kaplan Meier GL view.
 * </p>
 * <p>
 * TODO
 * </p>
 *
 * @author Marc Streit
 * @author Christian Partl
 * @author Alexander Lex
 */

public class GLKaplanMeier extends AGLView implements ISingleTablePerspectiveBasedView, IEventBasedSelectionManagerUser {
	public static final String VIEW_TYPE = "org.caleydo.view.kaplanmeier";
	public static final String VIEW_NAME = "Kaplan-Meier Plot";

	public static final int LEFT_AXIS_SPACING_PIXELS = 70;
	public static final int BOTTOM_AXIS_SPACING_PIXELS = 50;
	public static final int TOP_AXIS_SPACING_PIXELS = 8;
	public static final int RIGHT_AXIS_SPACING_PIXELS = 20;
	public static final int AXIS_LABEL_TEXT_HEIGHT_PIXELS = 20;
	public static final int AXIS_LABEL_TEXT_SIDE_SPACING_PIXELS = 5;
	public static final int AXIS_TICK_LABEL_SPACING_PIXELS = 12;

	private EventBasedSelectionManager recordGroupSelectionManager;

	private ATableBasedDataDomain dataDomain;

	/**
	 * The maximum time value that is mapped to the x axis. If this value is not set externally, it is calculated using
	 * the tablePerspective.
	 */
	private float maxAxisTime = Float.MIN_VALUE;

	/**
	 * Determines whether {@link #maxAxisTime} was set externally.
	 */
	private boolean isMaxAxisTimeSetExternally = false;

	/**
	 * The label of the x axis.
	 */
	private String xAxisLabel;

	/**
	 * The label of the y axis.
	 */
	private String yAxisLabel;

	private TablePerspective tablePerspective;

	private boolean showKMPlot = true;

	private String[] emptyViewText;

	/**
	 * Constructor.
	 *
	 * @param glCanvas
	 * @param viewLabel
	 * @param viewFrustum
	 */
	public GLKaplanMeier(IGLCanvas glCanvas, ViewFrustum viewFrustum) {

		super(glCanvas, viewFrustum, VIEW_TYPE, VIEW_NAME);

		textRenderer = new CaleydoTextRenderer(24);
	}

	@Override
	public void initialize() {
		super.initialize();
		recordGroupSelectionManager = new EventBasedSelectionManager(this, dataDomain.getRecordGroupIDType()
				.getIDCategory().getPrimaryMappingType());
	}

	@Override
	public void init(GL2 gl) {
		displayListIndex = gl.glGenLists(1);
		if (!showKMPlot)
			return;
		if (!isMaxAxisTimeSetExternally) {
			calculateMaxAxisTime(tablePerspective);
		}
		createPickingListeners();
		xAxisLabel = tablePerspective.getDimensionPerspective().getLabel();
		yAxisLabel = "Percentage of "
				+ tablePerspective.getRecordPerspective().getIdType().getIDCategory().getCategoryName();

		detailLevel = EDetailLevel.HIGH;
	}

	private void createPickingListeners() {

		VirtualArray recordVA = tablePerspective.getRecordPerspective().getVirtualArray();

		for (Group group : recordVA.getGroupList()) {
			if (!group.isLabelDefault())
				addIDPickingTooltipListener(group.getLabel(), EPickingType.KM_CURVE.name(), group.getID());
		}

		addTypePickingListener(new APickingListener() {

			@Override
			public void mouseOver(Pick pick) {
				// if (mouseOverGroupID != pick.getObjectID()) {
				// mouseOverGroupID = pick.getObjectID();
				// setDisplayListDirty();
				// }
				recordGroupSelectionManager.clearSelection(SelectionType.MOUSE_OVER);
				recordGroupSelectionManager.addToType(SelectionType.MOUSE_OVER, pick.getObjectID());
				recordGroupSelectionManager.triggerSelectionUpdateEvent();
				setDisplayListDirty();
			}

			@Override
			public void mouseOut(Pick pick) {
				// if (mouseOverGroupID == pick.getObjectID()) {
				// mouseOverGroupID = -1;
				// setDisplayListDirty();
				// }
				recordGroupSelectionManager.removeFromType(SelectionType.MOUSE_OVER, pick.getObjectID());
				recordGroupSelectionManager.triggerSelectionUpdateEvent();
				setDisplayListDirty();
			}

			@Override
			public void clicked(Pick pick) {

				recordGroupSelectionManager.clearSelection(SelectionType.SELECTION);
				recordGroupSelectionManager.addToType(SelectionType.SELECTION, pick.getObjectID());
				recordGroupSelectionManager.triggerSelectionUpdateEvent();
				setDisplayListDirty();
			}

		}, EPickingType.KM_CURVE.name());

	}

	public static int calculateMaxAxisTime(TablePerspective tablePerspective) {
		VirtualArray recordVA = tablePerspective.getRecordPerspective().getVirtualArray();

		VirtualArray dimensionVA = tablePerspective.getDimensionPerspective().getVirtualArray();

		int maxAxisTime = 0;
		boolean containsNegativeValues = false;
		boolean containsPositiveValues = false;

		final Table table = tablePerspective.getDataDomain().getTable();
		final Integer dimensionID = dimensionVA.get(0);
		for (Integer recordID : recordVA) {
			Number raw = table.getRaw(dimensionID, recordID);

			// check for invalid data
			if (raw == null || (raw instanceof Integer && raw.intValue() == Integer.MIN_VALUE)
					|| (raw instanceof Float && ((Float) raw).isNaN()))
				continue;

			int v = raw.intValue();
			if (v > 0)
				containsPositiveValues = true;
			if (v < 0)
				containsNegativeValues = true;

			if (containsPositiveValues && containsNegativeValues) {
				throw new IllegalStateException(
						"Data contains positive and negative values. KM plot cannot handle this data.");
			}
			if (Math.abs(v) > Math.abs(maxAxisTime))
				maxAxisTime = v;
		}

		return maxAxisTime;
	}

	@Override
	public void initLocal(GL2 gl) {
		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView, final GLMouseListener glMouseListener) {

		setMouseListener(glMouseListener);

		init(gl);
	}

	@Override
	public void displayLocal(GL2 gl) {
		pickingManager.handlePicking(this, gl);
		display(gl);
		if (busyState != EBusyState.OFF) {
			renderBusyMode(gl);
		}
	}

	@Override
	public void displayRemote(GL2 gl) {
		display(gl);
	}

	@Override
	public void display(GL2 gl) {

		if (isDisplayListDirty) {
			buildDisplayList(gl, displayListIndex);
			isDisplayListDirty = false;
		}
		gl.glCallList(displayListIndex);

		checkForHits(gl);
	}

	private void buildDisplayList(final GL2 gl, int displayListIndex) {

		gl.glNewList(displayListIndex, GL2.GL_COMPILE);

		if (!showKMPlot) {
			renderEmptyViewText(gl, emptyViewText);
			gl.glEndList();
			return;
		}

		Integer groupID = null;
		VirtualArray recordVA;
		if (tablePerspective.getParentTablePerspective() != null) {
			recordVA = tablePerspective.getParentTablePerspective().getRecordPerspective().getVirtualArray();
			groupID = tablePerspective.getRecordGroup().getID();

		} else {
			recordVA = tablePerspective.getRecordPerspective().getVirtualArray();
		}

		if (groupID != null) {
			// render this first to consider z-order
			renderCurve(gl, tablePerspective.getRecordGroup(), recordVA, true, true);
		}
		for (Group group : recordVA.getGroupList()) {
			if (groupID != null && group.getID() == groupID)
				continue;
			renderCurve(gl, group, recordVA, false, groupID != null);
		}

		if (detailLevel == EDetailLevel.HIGH) {
			renderAxes(gl);
		}

		gl.glEndList();
	}

	private void renderCurve(GL2 gl, Group group, VirtualArray recordVA, boolean fillCurve, boolean hasPrimaryCurve) {

		List<Integer> recordIDs = recordVA.getIDsOfGroup(group.getGroupIndex());

		int lineWidth = 1;

		Color color = Color.DARK_GRAY;
		lineWidth = 1;
		if (hasPrimaryCurve && !fillCurve) {
			color = Color.LIGHT_GRAY;
		}
		SelectionType selectionType = recordGroupSelectionManager.getHighestSelectionType(group.getID());
		if (selectionType != null) {
			// || (group.getID() == mouseOverGroupID)) {
			lineWidth += 1;
			color = selectionType.getColor();
		}

		if (detailLevel == EDetailLevel.HIGH)
			lineWidth *= 2;

		gl.glLineWidth(lineWidth);

		renderSingleKaplanMeierCurve(gl, recordIDs, fillCurve, group, color);

	}

	private void renderAxes(GL2 gl) {

		float originX = pixelGLConverter.getGLWidthForPixelWidth(LEFT_AXIS_SPACING_PIXELS);
		float originY = pixelGLConverter.getGLHeightForPixelHeight(BOTTOM_AXIS_SPACING_PIXELS);

		float axisLabelWidth = textRenderer.getRequiredTextWidthWithMax(xAxisLabel,
				pixelGLConverter.getGLHeightForPixelHeight(20), viewFrustum.getWidth());

		textRenderer.setColor(Color.BLACK);
		textRenderer.renderTextInBounds(gl, xAxisLabel, viewFrustum.getWidth() / 2.0f - axisLabelWidth / 2.0f,
				pixelGLConverter.getGLHeightForPixelHeight(AXIS_LABEL_TEXT_SIDE_SPACING_PIXELS), 0,
				viewFrustum.getWidth(), pixelGLConverter.getGLHeightForPixelHeight(AXIS_LABEL_TEXT_HEIGHT_PIXELS));

		axisLabelWidth = textRenderer.getRequiredTextWidthWithMax(yAxisLabel,
				pixelGLConverter.getGLHeightForPixelHeight(20), viewFrustum.getWidth());

		textRenderer.renderRotatedTextInBounds(
				gl,
				yAxisLabel,
				pixelGLConverter.getGLHeightForPixelHeight(AXIS_LABEL_TEXT_SIDE_SPACING_PIXELS
						+ AXIS_LABEL_TEXT_HEIGHT_PIXELS), viewFrustum.getHeight() / 2.0f - axisLabelWidth / 2.0f, 0,
				viewFrustum.getWidth(), pixelGLConverter.getGLHeightForPixelHeight(AXIS_LABEL_TEXT_HEIGHT_PIXELS), 90);

		renderSingleAxis(gl, originX, originY, true, 6, maxAxisTime);
		renderSingleAxis(gl, originX, originY, false, 6, 100);
	}

	private void renderSingleAxis(GL2 gl, float originX, float originY, boolean isXAxis, int numTicks,
			float maxTickValue) {
		List<Vec3f> xAxisLinePoints = new ArrayList<Vec3f>();
		xAxisLinePoints.add(new Vec3f(originX, originY, 0));
		xAxisLinePoints.add(new Vec3f(originX + ((isXAxis) ? getPlotWidth() : 0), originY
				+ ((isXAxis) ? 0 : getPlotHeight()), 0));

		ConnectionLineRenderer axis = new ConnectionLineRenderer();
		float step = maxTickValue / (numTicks - 1);

		for (int i = 0; i < numTicks; i++) {
			LineCrossingRenderer lineCrossingRenderer = new LineCrossingRenderer(i / 5.0f, pixelGLConverter);
			LineLabelRenderer lineLabelRenderer = new LineLabelRenderer((float) i / (float) (numTicks - 1),
					pixelGLConverter, new Integer(i * (int) step).toString(), textRenderer);
			if (isXAxis) {
				lineLabelRenderer.setLineOffsetPixels(-AXIS_TICK_LABEL_SPACING_PIXELS);
				lineLabelRenderer.setXCentered(true);
				lineLabelRenderer.setAlignmentX(EAlignmentX.RIGHT);
			} else {
				lineLabelRenderer.setLineOffsetPixels(AXIS_TICK_LABEL_SPACING_PIXELS);
				lineLabelRenderer.setYCentered(true);
				lineLabelRenderer.setAlignmentY(EAlignmentY.MIDDLE);
			}
			lineCrossingRenderer.setLineWidth(2);
			axis.addAttributeRenderer(lineCrossingRenderer);
			axis.addAttributeRenderer(lineLabelRenderer);
		}
		axis.setLineWidth(2);
		axis.renderLine(gl, xAxisLinePoints);
	}

	private float getPlotHeight() {
		return viewFrustum.getHeight()
				- (detailLevel == EDetailLevel.HIGH ? pixelGLConverter
						.getGLHeightForPixelHeight(BOTTOM_AXIS_SPACING_PIXELS + TOP_AXIS_SPACING_PIXELS) : 0);
	}

	private float getPlotWidth() {
		return viewFrustum.getWidth()
				- (detailLevel == EDetailLevel.HIGH ? pixelGLConverter.getGLWidthForPixelWidth(LEFT_AXIS_SPACING_PIXELS
						+ RIGHT_AXIS_SPACING_PIXELS) : 0);
	}

	private void renderSingleKaplanMeierCurve(GL2 gl, List<Integer> recordIDs, boolean fillCurve, Group group,
			Color color) {

		VirtualArray dimensionVA = tablePerspective.getDimensionPerspective().getVirtualArray();

		ArrayList<Float> dataVector = new ArrayList<Float>();

		final Table table = tablePerspective.getDataDomain().getTable();
		final Integer dimensionID = dimensionVA.get(0);
		for (Integer recordID : recordIDs) {
			float normalizedValue = table.getNormalizedValue(dimensionID, recordID);
			if (Float.isNaN(normalizedValue)) {
				// we assume that those who don't have an entry are still alive.
				dataVector.add(1f);
			} else
				dataVector.add(normalizedValue);
		}

		Collections.sort(dataVector);

		gl.glPushName(pickingManager.getPickingID(getID(), EPickingType.KM_CURVE.name(), group.getID()));

		if (fillCurve) {
			//
			if (color.isGray())
				gl.glColor3fv(Color.MEDIUM_DARK_GRAY.getRGB(), 0);
			// gl.glColor4fv(color.brighter().brighter().getRGBA(), 0);
			else
				gl.glColor4fv(color.lessSaturated().getRGBA(), 0);
			// gl.glColor4fv(color.getRGBA(), 0);
			@SuppressWarnings("unchecked")
			ArrayList<Float> clone = (ArrayList<Float>) dataVector.clone();
			drawFilledCurve(gl, clone);

		}

		// gl.glColor4fv(color.getRGBA(), 0);
		drawCurve(gl, dataVector, color, group);
		gl.glPopName();

	}

	private void drawFilledCurve(GL2 gl, ArrayList<Float> dataVector) {

		float plotHeight = getPlotHeight();
		float plotWidth = getPlotWidth();
		float bottomAxisSpacing = (detailLevel == EDetailLevel.HIGH ? pixelGLConverter
				.getGLWidthForPixelWidth(BOTTOM_AXIS_SPACING_PIXELS) : 0);
		float leftAxisSpacing = (detailLevel == EDetailLevel.HIGH ? pixelGLConverter
				.getGLWidthForPixelWidth(LEFT_AXIS_SPACING_PIXELS) : 0);

		float timeBinStepSize = 1 / Math.abs(maxAxisTime);
		float currentTimeBin = 0;

		int remainingItemCount = dataVector.size();
		float ySingleSampleSize = plotHeight / dataVector.size();

		float z = 0.1f;
		for (int binIndex = 0; binIndex < Math.abs(maxAxisTime); binIndex++) {

			while (dataVector.size() > 0 && dataVector.get(0) <= currentTimeBin) {
				dataVector.remove(0);
				remainingItemCount--;
			}

			float y = remainingItemCount * ySingleSampleSize;

			gl.glBegin(GL2GL3.GL_QUADS);
			gl.glVertex3f(leftAxisSpacing + currentTimeBin * plotWidth, bottomAxisSpacing, z);
			gl.glVertex3f(leftAxisSpacing + currentTimeBin * plotWidth, bottomAxisSpacing + y, z);
			currentTimeBin += timeBinStepSize;
			gl.glVertex3f(leftAxisSpacing + currentTimeBin * plotWidth, bottomAxisSpacing + y, z);
			gl.glVertex3f(leftAxisSpacing + currentTimeBin * plotWidth, bottomAxisSpacing, z);
			gl.glEnd();
		}

	}

	private void drawCurve(GL2 gl, ArrayList<Float> dataVector, Color color, Group group) {

		float plotHeight = getPlotHeight();
		float plotWidth = getPlotWidth();

		float bottomAxisSpacing = (detailLevel == EDetailLevel.HIGH ? pixelGLConverter
				.getGLWidthForPixelWidth(BOTTOM_AXIS_SPACING_PIXELS) : 0);
		float leftAxisSpacing = (detailLevel == EDetailLevel.HIGH ? pixelGLConverter
				.getGLWidthForPixelWidth(LEFT_AXIS_SPACING_PIXELS) : 0);

		float timeBinStepSize = 1 / Math.abs(maxAxisTime);
		float currentTimeBin = 0;

		int remainingItemCount = dataVector.size();
		float ySingleSampleSize = plotHeight / dataVector.size();

		float z = 0.11f;
		List<Vec3f> linePoints = new ArrayList<>();
		linePoints.add(new Vec3f(leftAxisSpacing, bottomAxisSpacing + plotHeight, z));
		// gl.glBegin(GL.GL_LINE_STRIP);
		// gl.glVertex3f(leftAxisSpacing, bottomAxisSpacing + plotHeight, z);

		for (int binIndex = 0; binIndex < Math.abs(maxAxisTime); binIndex++) {

			while (dataVector.size() > 0 && dataVector.get(0) <= currentTimeBin) {
				dataVector.remove(0);
				remainingItemCount--;
			}

			float y = remainingItemCount * ySingleSampleSize;

			// gl.glVertex3f(leftAxisSpacing + currentTimeBin * plotWidth, bottomAxisSpacing + y, z);
			linePoints.add(new Vec3f(leftAxisSpacing + currentTimeBin * plotWidth, bottomAxisSpacing + y, z));
			currentTimeBin += timeBinStepSize;
			linePoints.add(new Vec3f(leftAxisSpacing + currentTimeBin * plotWidth, bottomAxisSpacing + y, z));
			// gl.glVertex3f(leftAxisSpacing + currentTimeBin * plotWidth, bottomAxisSpacing + y, z);
		}

		ConnectionLineRenderer connectionLineRenderer = new ConnectionLineRenderer();
		connectionLineRenderer.setLineColor(color.getRGBA());
		float translationZ = 0;
		if (detailLevel == EDetailLevel.HIGH) {
			LineLabelRenderer lineLabelRenderer = new LineLabelRenderer(1f, pixelGLConverter, group.getLabel(),
					textRenderer);
			SelectionType selectionType = recordGroupSelectionManager.getHighestSelectionType(group.getID());
			if (selectionType == SelectionType.SELECTION) {
				translationZ = 0.1f;
				lineLabelRenderer.setBackGroundColor(new float[] { 1, 1, 1, 1f });
				lineLabelRenderer.setTextColor(color.getRGBA());
			} else if (selectionType == SelectionType.MOUSE_OVER) {
				lineLabelRenderer.setBackGroundColor(new float[] { 1, 1, 1, 1f });
				lineLabelRenderer.setTextColor(color.getRGBA());
				translationZ = 0.2f;
			} else {
				lineLabelRenderer.setBackGroundColor(new float[] { 1, 1, 1, 0.5f });
			}

			lineLabelRenderer.setAlignmentX(EAlignmentX.RIGHT);

			lineLabelRenderer.setLabelTranslation(10, 0);
			connectionLineRenderer.addAttributeRenderer(lineLabelRenderer);
		}
		gl.glPushMatrix();
		gl.glTranslatef(0, 0, translationZ);
		connectionLineRenderer.renderLine(gl, linePoints);
		gl.glPopMatrix();
		// gl.glEnd();
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedKaplanMeierView serializedForm = new SerializedKaplanMeierView(this);
		return serializedForm;
	}

	@Override
	public String toString() {
		return "GLKaplanMeier";
	}

	@Override
	public int getMinPixelHeight(EDetailLevel detailLevel) {

		switch (detailLevel) {
		case HIGH:
			return 400;
		case MEDIUM:
			return 100;
		case LOW:
			return 50;
		default:
			return 50;
		}
	}

	@Override
	public int getMinPixelWidth(EDetailLevel detailLevel) {

		switch (detailLevel) {
		case HIGH:
			return 400;
		case MEDIUM:
			return 100;
		case LOW:
			return 50;
		default:
			return 50;
		}
	}

	/**
	 * @param maxAxisTime
	 *            setter, see {@link #maxAxisTime}
	 */
	public void setMaxAxisTime(float maxAxisTime) {
		this.maxAxisTime = maxAxisTime;
		isMaxAxisTimeSetExternally = true;
	}

	/**
	 * @return the maxAxisTime, see {@link #maxAxisTime}
	 */
	public float getMaxAxisTime() {
		return maxAxisTime;
	}

	/**
	 * @param xAxisLabel
	 *            setter, see {@link #xAxisLabel}
	 */
	public void setxAxisLabel(String xAxisLabel) {
		this.xAxisLabel = xAxisLabel;
	}

	/**
	 * @return the xAxisLabel, see {@link #xAxisLabel}
	 */
	public String getxAxisLabel() {
		return xAxisLabel;
	}

	/**
	 * @param yAxisLabel
	 *            setter, see {@link #yAxisLabel}
	 */
	public void setyAxisLabel(String yAxisLabel) {
		this.yAxisLabel = yAxisLabel;
	}

	/**
	 * @return the yAxisLabel, see {@link #yAxisLabel}
	 */
	public String getyAxisLabel() {
		return yAxisLabel;
	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();
		removeAllPickingListeners();
	}

	@Override
	protected void destroyViewSpecificContent(GL2 gl) {
		gl.glDeleteLists(displayListIndex, 1);

	}

	@Override
	public IDataSupportDefinition getDataSupportDefinition() {
		return DataSupportDefinitions.tableBased;
	}

	@Override
	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;

	}

	@Override
	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	public void notifyOfSelectionChange(EventBasedSelectionManager selectionManager) {
		setDisplayListDirty();

	}

	@Override
	public void setTablePerspective(TablePerspective tablePerspective) {
		this.tablePerspective = tablePerspective;
	}

	@Override
	public TablePerspective getTablePerspective() {
		return tablePerspective;
	}

	@Override
	public List<TablePerspective> getTablePerspectives() {
		List<TablePerspective> tpList = new ArrayList<>(1);
		tpList.add(tablePerspective);
		return tpList;
	}

	/**
	 * @param showKMPlot
	 *            setter, see {@link showKMPlot}
	 */
	public void setShowKMPlot(boolean showKMPlot) {
		this.showKMPlot = showKMPlot;
	}

	/**
	 * @param emptyViewText
	 *            setter, see {@link emptyViewText}
	 */
	public void setEmptyViewText(String[] emptyViewText) {
		this.emptyViewText = emptyViewText;
	}
}
