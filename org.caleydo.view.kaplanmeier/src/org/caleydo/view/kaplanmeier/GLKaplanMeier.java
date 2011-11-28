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
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.ATableBasedView;
import org.caleydo.core.view.opengl.canvas.DetailLevel;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.core.view.opengl.picking.PickingType;
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

public class GLKaplanMeier extends ATableBasedView {

	public final static String VIEW_TYPE = "org.caleydo.view.kaplanmeier";

	private KaplanMeierRenderStyle renderStyle;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param viewLabel
	 * @param viewFrustum
	 */
	public GLKaplanMeier(GLCanvas glCanvas, Composite parentComposite,
			ViewFrustum viewFrustum) {

		super(glCanvas, parentComposite, viewFrustum);

		viewType = GLKaplanMeier.VIEW_TYPE;
		viewLabel = "Kaplan Meier";
	}

	@Override
	public void init(GL2 gl) {
		displayListIndex = gl.glGenLists(1);
		renderStyle = new KaplanMeierRenderStyle(viewFrustum);

		super.renderStyle = renderStyle;
		detailLevel = DetailLevel.HIGH;
	}

	@Override
	public void initLocal(GL2 gl) {
		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView,
			final GLMouseListener glMouseListener) {

		// // Register keyboard listener to GL2 canvas
		// glParentView.getParentComposite().getDisplay().asyncExec(new
		// Runnable() {
		// @Override
		// public void run() {
		// glParentView.getParentComposite().addKeyListener(glKeyListener);
		// }
		// });

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

		RecordVirtualArray recordVA = dataContainer.getRecordPerspective()
				.getVirtualArray();

		for (Group group : recordVA.getGroupList()) {
			List<Integer> recordIDs = recordVA.getIDsOfGroup(group.getGroupIndex());
			renderSingleKaplanMeierCurve(gl, recordIDs);
		}
	}

	private void renderSingleKaplanMeierCurve(GL2 gl, List<Integer> recordIDs) {
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
		Arrays.sort(sortedDataVector, 0, sortedDataVector.length - 1);
		dataVector.clear();

		// move sorted data back to array list so that we can use it as a stack
		for (int index = 0; index < recordIDs.size(); index++) {
			dataVector.add(sortedDataVector[index]);
		}

		float TIME_BINS = (float) dataContainer.getDataDomain().getTable()
				.getRawForNormalized(1);
		float timeBinStepSize = 1 / TIME_BINS;
		float currentTimeBin = 0;

		int remainingItemCount = sortedDataVector.length;
		float ySingleSampleSize = viewFrustum.getHeight() / sortedDataVector.length;

		gl.glBegin(GL2.GL_LINE_STRIP);
		gl.glColor3f(0, 1, 0);
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
	protected void handlePickingEvents(PickingType pickingType, PickingMode pickingMode,
			int externalID, Pick pick) {

		// TODO: Implement picking processing here!
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
	public void registerEventListeners() {
		super.registerEventListeners();

	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

	}

	@Override
	public void handleRedrawView() {
		setDisplayListDirty();
	}

	@Override
	protected ArrayList<ElementConnectionInformation> createElementConnectionInformation(
			IDType idType, int id) throws InvalidAttributeValueException {
		// TODO Auto-generated method stub
		return null;
	}

}
