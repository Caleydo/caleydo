/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 * 
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.kaplanmeier;

import gleem.linalg.Vec3f;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.management.InvalidAttributeValueException;
import javax.media.opengl.GL2;
import javax.media.opengl.awt.GLCanvas;
import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.data.selection.ElementConnectionInformation;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IDType;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.ColorManager;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.ATableBasedView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.ToolTipPickingListener;
import org.caleydo.core.view.opengl.util.connectionline.ConnectionLineRenderer;
import org.caleydo.core.view.opengl.util.connectionline.LineCrossingRenderer;
import org.caleydo.core.view.opengl.util.connectionline.LineLabelRenderer;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.eclipse.swt.widgets.Composite;

/**
 * <p>
 * Kaplan Meier GL2 view.
 * </p>
 * <p>
 * TODO
 * </p>
 * 
 * @author Marc Streit
 * @author Christian
 */

public class GLKaplanMeier extends ATableBasedView {
	public static String VIEW_TYPE = "org.caleydo.view.kaplanmeier";

	public static String VIEW_NAME = "Kaplan-Meier Plot";

	public static String DEFAULT_X_AXIS_LABEL = "Time (Days)";
	public static String DEFAULT_Y_AXIS_LABEL = "Percentage of Patients";

	protected static final int LEFT_AXIS_SPACING_PIXELS = 70;
	protected static final int BOTTOM_AXIS_SPACING_PIXELS = 50;
	protected static final int TOP_AXIS_SPACING_PIXELS = 8;
	protected static final int RIGHT_AXIS_SPACING_PIXELS = 20;
	protected static final int AXIS_LABEL_TEXT_HEIGHT_PIXELS = 20;
	protected static final int AXIS_LABEL_TEXT_SIDE_SPACING_PIXELS = 5;
	protected static final int AXIS_TICK_LABEL_SPACING_PIXELS = 12;

	private SelectionManager recordGroupSelectionManager;

	/**
	 * The maximum time value that is mapped to the x axis. If this value is not
	 * set externally, it is calculated using the tablePerspective.
	 */
	private float maxAxisTime = Float.MIN_VALUE;

	/**
	 * Determines whether {@link #maxAxisTime} was set externally.
	 */
	private boolean isMaxAxisTimeSetExternally = false;

	/**
	 * The label of the x axis.
	 */
	private String xAxisLabel = DEFAULT_X_AXIS_LABEL;

	/**
	 * The label of the y axis.
	 */
	private String yAxisLabel = DEFAULT_Y_AXIS_LABEL;

	/**
	 * The id of the group whose curve was mouse overed.
	 */
	private int mouseOverGroupID = -1;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param viewLabel
	 * @param viewFrustum
	 */
	public GLKaplanMeier(GLCanvas glCanvas, Composite parentComposite,
			ViewFrustum viewFrustum) {

		super(glCanvas, parentComposite, viewFrustum, VIEW_TYPE, VIEW_NAME);

		textRenderer = new CaleydoTextRenderer(24);
	}

	@Override
	public void initialize() {
		super.initialize();
		recordGroupSelectionManager = dataDomain.getRecordGroupSelectionManager().clone();
	}

	@Override
	public void init(GL2 gl) {
		displayListIndex = gl.glGenLists(1);

		if (!isMaxAxisTimeSetExternally) {
			calculateMaxAxisTime();
		}
		createPickingListeners();

		detailLevel = EDetailLevel.HIGH;
	}

	private void createPickingListeners() {

		RecordVirtualArray recordVA = tablePerspective.getRecordPerspective()
				.getVirtualArray();

		for (Group group : recordVA.getGroupList()) {
			ToolTipPickingListener toolTipPickingListener = new ToolTipPickingListener(
					this, group.getLabel());
			addIDPickingListener(toolTipPickingListener, EPickingType.KM_CURVE.name(),
					group.getID());
		}

		addTypePickingListener(new APickingListener() {

			@Override
			public void mouseOver(Pick pick) {
				if (mouseOverGroupID != pick.getObjectID()) {
					mouseOverGroupID = pick.getObjectID();
					setDisplayListDirty();
				}
			}

			@Override
			public void mouseOut(Pick pick) {
				if (mouseOverGroupID == pick.getObjectID()) {
					mouseOverGroupID = -1;
					setDisplayListDirty();
				}
			}

		}, EPickingType.KM_CURVE.name());

	}

	private void calculateMaxAxisTime() {
		RecordVirtualArray recordVA = tablePerspective.getRecordPerspective()
				.getVirtualArray();

		DimensionVirtualArray dimensionVA = tablePerspective.getDimensionPerspective()
				.getVirtualArray();

		maxAxisTime = 0;
		boolean containsNegativeValues = false;
		boolean containsPositiveValues = false;

		for (Group group : recordVA.getGroupList()) {
			List<Integer> recordIDs = recordVA.getIDsOfGroup(group.getGroupIndex());
			for (int recordID = 0; recordID < recordIDs.size(); recordID++) {

				float rawValue = tablePerspective
						.getDataDomain()
						.getTable()
						.getFloat(DataRepresentation.RAW, recordIDs.get(recordID),
								dimensionVA.get(0));

				if (rawValue > 0)
					containsPositiveValues = true;
				if (rawValue < 0)
					containsNegativeValues = true;

				if (containsPositiveValues && containsNegativeValues) {
					throw new IllegalStateException(
							"Data contains positive and negative values. KM plot cannot handle this data.");
				}
				if (rawValue != Float.NaN && Math.abs(rawValue) > Math.abs(maxAxisTime))
					maxAxisTime = rawValue;
			}
		}
	}

	@Override
	public void initLocal(GL2 gl) {
		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView,
			final GLMouseListener glMouseListener) {

		this.glMouseListener = glMouseListener;

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

		RecordVirtualArray recordVA = tablePerspective.getRecordPerspective()
				.getVirtualArray();

		// do not fill curve if multiple curves are rendered in this plot
		boolean fillCurve = recordVA.getGroupList().size() > 1 ? false : true;

		List<Color> colors = ColorManager.get().getColorList(
				ColorManager.QUALITATIVE_COLORS);
		for (Group group : recordVA.getGroupList()) {
			List<Integer> recordIDs = recordVA.getIDsOfGroup(group.getGroupIndex());

			int colorIndex = 0;
			if (tablePerspective.getRecordGroup() != null)
				colorIndex = tablePerspective.getRecordGroup().getGroupIndex();
			else
				colorIndex = group.getGroupIndex();

			// We only have 10 colors in the diverging color map
			colorIndex = colorIndex % 10;

			int lineWidth = 1;
			if ((recordGroupSelectionManager.getNumberOfElements(SelectionType.SELECTION) == 1 && (Integer) recordGroupSelectionManager
					.getElements(SelectionType.SELECTION).toArray()[0] == group.getID())
					|| (group.getID() == mouseOverGroupID)) {
				lineWidth = 2;
			}
			// else
			// fillCurve = false;

			if (detailLevel == EDetailLevel.HIGH)
				lineWidth *= 2;

			gl.glLineWidth(lineWidth);

			renderSingleKaplanMeierCurve(gl, recordIDs, colors.get(colorIndex),
					fillCurve, group.getID());
		}

		if (detailLevel == EDetailLevel.HIGH) {
			renderAxes(gl);
		}

		gl.glEndList();
	}

	private void renderAxes(GL2 gl) {

		float originX = pixelGLConverter
				.getGLWidthForPixelWidth(LEFT_AXIS_SPACING_PIXELS);
		float originY = pixelGLConverter
				.getGLHeightForPixelHeight(BOTTOM_AXIS_SPACING_PIXELS);

		float axisLabelWidth = textRenderer.getRequiredTextWidthWithMax(xAxisLabel,
				pixelGLConverter.getGLHeightForPixelHeight(20), viewFrustum.getWidth());

		textRenderer.renderTextInBounds(gl, xAxisLabel, viewFrustum.getWidth() / 2.0f
				- axisLabelWidth / 2.0f, pixelGLConverter
				.getGLHeightForPixelHeight(AXIS_LABEL_TEXT_SIDE_SPACING_PIXELS), 0,
				viewFrustum.getWidth(), pixelGLConverter
						.getGLHeightForPixelHeight(AXIS_LABEL_TEXT_HEIGHT_PIXELS));

		axisLabelWidth = textRenderer.getRequiredTextWidthWithMax(yAxisLabel,
				pixelGLConverter.getGLHeightForPixelHeight(20), viewFrustum.getWidth());

		textRenderer.renderRotatedTextInBounds(gl, yAxisLabel, pixelGLConverter
				.getGLHeightForPixelHeight(AXIS_LABEL_TEXT_SIDE_SPACING_PIXELS
						+ AXIS_LABEL_TEXT_HEIGHT_PIXELS), viewFrustum.getHeight() / 2.0f
				- axisLabelWidth / 2.0f, 0, viewFrustum.getWidth(), pixelGLConverter
				.getGLHeightForPixelHeight(AXIS_LABEL_TEXT_HEIGHT_PIXELS), 90);

		renderSingleAxis(gl, originX, originY, true, 6, maxAxisTime);
		renderSingleAxis(gl, originX, originY, false, 6, 100);
	}

	private void renderSingleAxis(GL2 gl, float originX, float originY, boolean isXAxis,
			int numTicks, float maxTickValue) {
		List<Vec3f> xAxisLinePoints = new ArrayList<Vec3f>();
		xAxisLinePoints.add(new Vec3f(originX, originY, 0));
		xAxisLinePoints.add(new Vec3f(originX + ((isXAxis) ? getPlotWidth() : 0), originY
				+ ((isXAxis) ? 0 : getPlotHeight()), 0));

		ConnectionLineRenderer axis = new ConnectionLineRenderer();
		float step = maxTickValue / (float) (numTicks - 1);

		for (int i = 0; i < numTicks; i++) {
			LineCrossingRenderer lineCrossingRenderer = new LineCrossingRenderer(
					(float) i / 5.0f, pixelGLConverter);
			LineLabelRenderer lineLabelRenderer = new LineLabelRenderer((float) i
					/ (float) (numTicks - 1), pixelGLConverter, new Integer(i
					* (int) step).toString(), textRenderer);
			if (isXAxis) {
				lineLabelRenderer.setLineOffsetPixels(-AXIS_TICK_LABEL_SPACING_PIXELS);
				lineLabelRenderer.setXCentered(true);
			} else {
				lineLabelRenderer.setLineOffsetPixels(AXIS_TICK_LABEL_SPACING_PIXELS);
				lineLabelRenderer.setYCentered(true);
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
						.getGLHeightForPixelHeight(BOTTOM_AXIS_SPACING_PIXELS
								+ TOP_AXIS_SPACING_PIXELS) : 0);
	}

	private float getPlotWidth() {
		return viewFrustum.getWidth()
				- (detailLevel == EDetailLevel.HIGH ? pixelGLConverter
						.getGLWidthForPixelWidth(LEFT_AXIS_SPACING_PIXELS
								+ RIGHT_AXIS_SPACING_PIXELS) : 0);
	}

	private void renderSingleKaplanMeierCurve(GL2 gl, List<Integer> recordIDs,
			Color color, boolean fillCurve, int groupID) {

		// if (recordIDs.size() == 0)
		// return;
		DimensionVirtualArray dimensionVA = tablePerspective.getDimensionPerspective()
				.getVirtualArray();

		ArrayList<Float> dataVector = new ArrayList<Float>();
		// Float maxValue = Float.MIN_VALUE;
		// maxAxisTime = Float.MIN_VALUE;

		for (int recordID = 0; recordID < recordIDs.size(); recordID++) {
			float normalizedValue = tablePerspective
					.getDataDomain()
					.getTable()
					.getFloat(DataRepresentation.NORMALIZED, recordIDs.get(recordID),
							dimensionVA.get(0));
			dataVector.add(normalizedValue);
		}
		Float[] sortedDataVector = new Float[dataVector.size()];
		dataVector.toArray(sortedDataVector);
		Arrays.sort(sortedDataVector);
		dataVector.clear();

		// move sorted data back to array list so that we can use it as a stack
		for (int index = 0; index < recordIDs.size(); index++) {
			dataVector.add(sortedDataVector[index]);
		}

		if (fillCurve) {
			// We cannot use transparency here because of artifacts. Hence, we
			// need
			// to brighten the color by multiplying it with a factor
			// TODO: Use correct brightening of HSV color model in the future.
			gl.glColor3f(color.r * 1.3f, color.g * 1.3f,
					color.b * 1.3f);
			drawFilledCurve(gl, dataVector);

			dataVector.clear();
			// move sorted data back to array list so that we can use it as a
			// stack
			for (int index = 0; index < recordIDs.size(); index++) {
				dataVector.add(sortedDataVector[index]);
			}
		}

		if (!fillCurve && detailLevel == EDetailLevel.HIGH) {
			gl.glPushName(pickingManager.getPickingID(getID(),
					EPickingType.KM_CURVE.name(), groupID));
		}
		gl.glColor3fv(color.getRGB(), 0);
		drawCurve(gl, dataVector);
		if (!fillCurve && detailLevel == EDetailLevel.HIGH) {
			gl.glPopName();
		}

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

		for (int binIndex = 0; binIndex < Math.abs(maxAxisTime); binIndex++) {

			while (dataVector.size() > 0 && dataVector.get(0) <= currentTimeBin) {
				dataVector.remove(0);
				remainingItemCount--;
			}

			float y = (float) remainingItemCount * ySingleSampleSize;
			gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3f(leftAxisSpacing + currentTimeBin * plotWidth,
					bottomAxisSpacing, 0);
			gl.glVertex3f(leftAxisSpacing + currentTimeBin * plotWidth, bottomAxisSpacing
					+ y, 0);
			currentTimeBin += timeBinStepSize;
			gl.glVertex3f(leftAxisSpacing + currentTimeBin * plotWidth, bottomAxisSpacing
					+ y, 0);
			gl.glVertex3f(leftAxisSpacing + currentTimeBin * plotWidth,
					bottomAxisSpacing, 0);
			gl.glEnd();
		}

	}

	private void drawCurve(GL2 gl, ArrayList<Float> dataVector) {

		float plotHeight = getPlotHeight();
		float plotWidth = getPlotWidth();

		float bottomAxisSpacing = (detailLevel == EDetailLevel.HIGH ? pixelGLConverter
				.getGLWidthForPixelWidth(BOTTOM_AXIS_SPACING_PIXELS) : 0);
		float leftAxisSpacing = (detailLevel == EDetailLevel.HIGH ? pixelGLConverter
				.getGLWidthForPixelWidth(LEFT_AXIS_SPACING_PIXELS) : 0);
		// float TIME_BINS = 20;
		// float TIME_BINS = (float) tablePerspective.getDataDomain().getTable()
		// .getRawForNormalized(1);

		float timeBinStepSize = 1 / Math.abs(maxAxisTime);
		float currentTimeBin = 0;

		int remainingItemCount = dataVector.size();
		float ySingleSampleSize = plotHeight / dataVector.size();

		gl.glBegin(GL2.GL_LINE_STRIP);
		gl.glVertex3f(leftAxisSpacing, bottomAxisSpacing + plotHeight, 1);

		for (int binIndex = 0; binIndex < Math.abs(maxAxisTime); binIndex++) {

			while (dataVector.size() > 0 && dataVector.get(0) <= currentTimeBin) {
				dataVector.remove(0);
				remainingItemCount--;
			}

			float y = (float) remainingItemCount * ySingleSampleSize;

			gl.glVertex3f(leftAxisSpacing + currentTimeBin * plotWidth, bottomAxisSpacing
					+ y, 1);
			currentTimeBin += timeBinStepSize;
			gl.glVertex3f(leftAxisSpacing + currentTimeBin * plotWidth, bottomAxisSpacing
					+ y, 1);
		}

		gl.glEnd();
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedKaplanMeierView serializedForm = new SerializedKaplanMeierView();
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public String toString() {
		return "TODO: ADD INFO THAT APPEARS IN THE LOG";
	}

	@Override
	public void handleRedrawView() {
		setDisplayListDirty();
	}

	@Override
	public void handleSelectionUpdate(SelectionDelta selectionDelta) {
		super.handleSelectionUpdate(selectionDelta);

		if (selectionDelta.getIDType() == recordGroupSelectionManager.getIDType()) {
			recordGroupSelectionManager.setDelta(selectionDelta);
		}
	}

	@Override
	protected ArrayList<ElementConnectionInformation> createElementConnectionInformation(
			IDType idType, int id) throws InvalidAttributeValueException {
		// TODO Auto-generated method stub
		return null;
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
	public void registerEventListeners() {
		super.registerEventListeners();
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
}
