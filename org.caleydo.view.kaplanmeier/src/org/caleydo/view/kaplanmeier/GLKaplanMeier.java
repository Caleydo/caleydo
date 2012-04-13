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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.management.InvalidAttributeValueException;
import javax.media.opengl.GL2;
import javax.media.opengl.awt.GLCanvas;
import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.selection.ElementConnectionInformation;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.ColorManager;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.ATableBasedView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.view.kaplanmeier.renderstyle.KaplanMeierRenderStyle;
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
 */

public class GLKaplanMeier
	extends ATableBasedView {

	public final static String VIEW_TYPE = "org.caleydo.view.kaplanmeier";

	private KaplanMeierRenderStyle renderStyle;

	private SelectionManager recordGroupSelectionManager;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param viewLabel
	 * @param viewFrustum
	 */
	public GLKaplanMeier(GLCanvas glCanvas, Composite parentComposite, ViewFrustum viewFrustum) {

		super(glCanvas, parentComposite, viewFrustum);

		viewType = GLKaplanMeier.VIEW_TYPE;
		viewLabel = "Kaplan Meier";
	}

	@Override
	public void initialize() {
		super.initialize();
		recordGroupSelectionManager = dataDomain.getRecordGroupSelectionManager().clone();
	}

	@Override
	public void init(GL2 gl) {
		displayListIndex = gl.glGenLists(1);
		renderStyle = new KaplanMeierRenderStyle(viewFrustum);

		super.renderStyle = renderStyle;
		detailLevel = EDetailLevel.HIGH;
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

		renderKaplanMeierCurve(gl);

		checkForHits(gl);
	}

	private void renderKaplanMeierCurve(final GL2 gl) {

		RecordVirtualArray recordVA = dataContainer.getRecordPerspective().getVirtualArray();

		// do not fill curve if multiple curves are rendered in this plot
		boolean fillCurve = recordVA.getGroupList().size() > 1 ? false : true;

		List<Color> colors = ColorManager.get().getColorList(ColorManager.QUALITATIVE_COLORS);
		for (Group group : recordVA.getGroupList()) {
			List<Integer> recordIDs = recordVA.getIDsOfGroup(group.getGroupIndex());

			int colorIndex = 0;
			if (dataContainer.getRecordGroup() != null)
				colorIndex = dataContainer.getRecordGroup().getGroupIndex();
			else
				colorIndex = group.getGroupIndex();

			// We only have 10 colors in the diverging color map
			colorIndex = colorIndex % 10;

			int lineWidth = 1;
			if (recordGroupSelectionManager.getElements(SelectionType.SELECTION).size() == 1
					&& (Integer) recordGroupSelectionManager.getElements(
							SelectionType.SELECTION).toArray()[0] == group.getID()) {
				lineWidth = 2;
			}
//			else
//				fillCurve = false;

			if (detailLevel == EDetailLevel.HIGH)
				lineWidth *= 2;

			gl.glLineWidth(lineWidth);

			renderSingleKaplanMeierCurve(gl, recordIDs, colors.get(colorIndex), fillCurve);
		}
	}

	private void renderSingleKaplanMeierCurve(GL2 gl, List<Integer> recordIDs, Color color,
			boolean fillCurve) {

//		if (recordIDs.size() == 0)
//			return;
		DimensionVirtualArray dimensionVA = dataContainer.getDimensionPerspective()
				.getVirtualArray();
		

		ArrayList<Float> dataVector = new ArrayList<Float>();

		for (int recordID = 0; recordID < recordIDs.size(); recordID++) {
			dataVector.add(dataContainer
					.getDataDomain()
					.getTable()
					.getFloat(DataRepresentation.NORMALIZED, recordIDs.get(recordID),
							dimensionVA.get(0)));
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
			// We cannot use transparency here because of artefacts. Hence, we
			// need
			// to brighten the color by multiplying it with a factor
			gl.glColor3f(color.r * 1.3f, color.g * 1.3f, color.b * 1.3f);
			drawFilledCurve(gl, dataVector);

			dataVector.clear();
			// move sorted data back to array list so that we can use it as a
			// stack
			for (int index = 0; index < recordIDs.size(); index++) {
				dataVector.add(sortedDataVector[index]);
			}

			gl.glColor3fv(color.getRGB(), 0);
			drawCurve(gl, dataVector);
		}
		else {
			gl.glColor3fv(color.getRGB(), 0);
			drawCurve(gl, dataVector);
		}
	}

	private void drawFilledCurve(GL2 gl, ArrayList<Float> dataVector) {

		float TIME_BINS = (float) dataContainer.getDataDomain().getTable()
				.getRawForNormalized(1);

		float timeBinStepSize = 1 / TIME_BINS;
		float currentTimeBin = 0;

		int remainingItemCount = dataVector.size();
		float ySingleSampleSize = viewFrustum.getHeight() / dataVector.size();

		for (int binIndex = 0; binIndex < TIME_BINS; binIndex++) {

			while (dataVector.size() > 0 && dataVector.get(0) <= currentTimeBin) {
				dataVector.remove(0);
				remainingItemCount--;
			}

			float y = (float) remainingItemCount * ySingleSampleSize;
			gl.glBegin(GL2.GL_LINE_STRIP);
			gl.glVertex3f(currentTimeBin * viewFrustum.getWidth(), 0, 0);
			gl.glVertex3f(currentTimeBin * viewFrustum.getWidth(), y, 0);
			currentTimeBin += timeBinStepSize;
			gl.glVertex3f(currentTimeBin * viewFrustum.getWidth(), y, 0);
			gl.glVertex3f(0, y, 0);
			gl.glEnd();
		}

	}

	private void drawCurve(GL2 gl, ArrayList<Float> dataVector) {

		float TIME_BINS = (float) dataContainer.getDataDomain().getTable()
				.getRawForNormalized(1);

		float timeBinStepSize = 1 / TIME_BINS;
		float currentTimeBin = 0;

		int remainingItemCount = dataVector.size();
		float ySingleSampleSize = viewFrustum.getHeight() / dataVector.size();

		gl.glBegin(GL2.GL_LINE_STRIP);
		gl.glVertex3f(0, viewFrustum.getHeight(), 0);

		for (int binIndex = 0; binIndex < TIME_BINS; binIndex++) {

			while (dataVector.size() > 0 && dataVector.get(0) <= currentTimeBin) {
				dataVector.remove(0);
				remainingItemCount--;
			}

			float y = (float) remainingItemCount * ySingleSampleSize;

			gl.glVertex3f(currentTimeBin * viewFrustum.getWidth(), y, 0);
			currentTimeBin += timeBinStepSize;
			gl.glVertex3f(currentTimeBin * viewFrustum.getWidth(), y, 0);
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
	public void handleSelectionUpdate(SelectionDelta selectionDelta,
			boolean scrollToSelection, String info) {
		super.handleSelectionUpdate(selectionDelta, scrollToSelection, info);

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
}
