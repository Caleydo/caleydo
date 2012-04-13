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
package org.caleydo.view.tissuebrowser;

import gleem.linalg.Vec3f;
import gleem.linalg.open.Transform;
import java.util.ArrayList;
import java.util.HashMap;
import javax.media.opengl.GL2;
import javax.media.opengl.awt.GLCanvas;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.id.ManagedObjectType;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.core.data.selection.ElementConnectionInformation;
import org.caleydo.core.data.selection.RecordSelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDeltaItem;
import org.caleydo.core.data.selection.events.SelectionUpdateListener;
import org.caleydo.core.data.virtualarray.delta.RecordVADelta;
import org.caleydo.core.data.virtualarray.delta.VADeltaItem;
import org.caleydo.core.data.virtualarray.events.IRecordVAUpdateHandler;
import org.caleydo.core.data.virtualarray.events.RecordVADeltaEvent;
import org.caleydo.core.data.virtualarray.events.RecordVAUpdateEvent;
import org.caleydo.core.data.virtualarray.events.RecordVAUpdateListener;
import org.caleydo.core.event.view.tablebased.SelectionUpdateEvent;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.AGLViewBrowser;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.vislink.ConnectedElementRepresentationManager;
import org.caleydo.view.texture.GLTexture;
import org.caleydo.view.texture.SerializedTextureView;
import org.eclipse.swt.widgets.Composite;

/**
 * FIXME: this view uses and listenes to the clinical data domain despite beeing
 * of the tissue data domain. This should be substituted with mappings in the
 * tissue datadomain.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 * 
 */
public class GLTissueViewBrowser extends AGLViewBrowser implements IRecordVAUpdateHandler {

	public final static String VIEW_TYPE = "org.caleydo.view.tissuebrowser";

	/**
	 * 
	 * The foreign dataDomain
	 */
	public ATableBasedDataDomain foreignDataDomain;
	public final static String FOREIGN_DATADOMAIN_TYPE = "org.caleydo.datadomain.clinical";

	private HashMap<Integer, String> mapExperimentToTexturePath;

	private RecordSelectionManager experiementSelectionManager;

	private SelectionUpdateListener selectionUpdateListener;
	private RecordVAUpdateListener recordVAUpdateListener;

	private RecordPerspective recordPerspective;

	private IDType primaryIDType;

	private ArrayList<SerializedTextureView> allTissueViews;

	private boolean poolLeft = false;

	public GLTissueViewBrowser(GLCanvas glCanvas, Composite parentComposite,
			ViewFrustum viewFrustum) {

		super(glCanvas, parentComposite, viewFrustum);

		viewType = VIEW_TYPE;
		viewSymbol = EIconTextures.NO_ICON_AVAILABLE;
		mapExperimentToTexturePath = new HashMap<Integer, String>();
	}

	@Override
	public void setDataDomain(IDataDomain dataDomain) {
		this.dataDomain = dataDomain;

		this.foreignDataDomain = (ATableBasedDataDomain) DataDomainManager.get()
				.getDataDomainByID(FOREIGN_DATADOMAIN_TYPE);

		recordPerspective = foreignDataDomain.getTable().getDefaultRecordPerspective();
		primaryIDType = foreignDataDomain.getRecordIDType();

		experiementSelectionManager = foreignDataDomain.getRecordSelectionManager();

		addInitialViews();
	}

	@Override
	public IDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	protected void addInitialViews() {

		newViews.clear();
		allTissueViews = new ArrayList<SerializedTextureView>();

		for (int experimentIndex = 0; experimentIndex < MAX_VIEWS; experimentIndex++) {

			// generalManager.getViewGLCanvasManager().createGLView(
			// "org.caleydo.view.texture", parentGLCanvas, viewFrustum);

			mapExperimentToTexturePath.put(experimentIndex, "data/tissue/breast_"
					+ experimentIndex % 24 + ".jpg");

			SerializedTextureView tissue = new SerializedTextureView();
			// tissue.setDataDomainID("org.caleydo.view.texture");
			tissue.setTexturePath(mapExperimentToTexturePath.get(experimentIndex));

			tissue.setExperimentIndex(experimentIndex);

			allTissueViews.add(tissue);
		}

		for (SerializedTextureView serTissue : allTissueViews) {
			newViews.add(serTissue);
		}
	}

	private void updateViews() {

		if (recordPerspective.getVirtualArray().size() > MAX_VIEWS)
			return;

		newViews.clear();

		clearRemoteLevel(focusLevel);
		clearRemoteLevel(poolLevel);
		clearRemoteLevel(transitionLevel);

		for (Integer experimentIndex : recordPerspective.getVirtualArray()) {
			newViews.add(allTissueViews.get(experimentIndex));
		}
	}

	@Override
	protected AGLView createView(GL2 gl, ASerializedView serView) {

		GLTexture glView = (GLTexture) super.createView(gl, serView);

		glView.setTexturePath(((SerializedTextureView) serView).getTexturePath());
		glView.setExperimentIndex(((SerializedTextureView) serView).getExperimentIndex());

		setInfo(glView, glView.getExperimentIndex());
		return glView;
	}

	@Override
	protected void initFocusLevel() {

		float xOffset = 1.5f;
		if (!poolLeft)
			xOffset = 0.1f;

		Transform transform = new Transform();
		transform.setTranslation(new Vec3f(xOffset, 1.3f, 0));
		transform.setScale(new Vec3f(0.8f, 0.8f, 1));

		focusLevel.getElementByPositionIndex(0).setTransform(transform);
	}

	@Override
	protected void initPoolLevel(int iSelectedRemoteLevelElementID) {
		Transform transform;

		float xOffset = 6.6f;
		if (poolLeft)
			xOffset = 0.1f;

		float fScalingFactorPoolLevel = 0.05f;
		float fSelectedScaling = 1;
		float fYAdd = 8f;

		int iRemoteLevelElementIndex = 0;
		for (RemoteLevelElement element : poolLevel.getAllElements()) {

			if (element.getID() == iSelectedRemoteLevelElementID) {
				fSelectedScaling = 1.8f;
				fYAdd -= 0.6f * fSelectedScaling;
			} else {
				fSelectedScaling = 1;
				fYAdd -= 0.5f * fSelectedScaling;
			}

			transform = new Transform();
			transform.setTranslation(new Vec3f(xOffset, fYAdd, 0));
			transform.setScale(new Vec3f(fScalingFactorPoolLevel * fSelectedScaling,
					fScalingFactorPoolLevel * fSelectedScaling, fScalingFactorPoolLevel
							* fSelectedScaling));

			poolLevel.getElementByPositionIndex(iRemoteLevelElementIndex).setTransform(
					transform);
			iRemoteLevelElementIndex++;
		}
	}

	@Override
	protected void initExternalSelectionLevel() {

		float fScalingFactorSelectionLevel = 1;
		Transform transform = new Transform();
		transform.setTranslation(new Vec3f(1, -2.01f, 0));
		transform.setScale(new Vec3f(fScalingFactorSelectionLevel,
				fScalingFactorSelectionLevel, fScalingFactorSelectionLevel));

		externalSelectionLevel.getElementByPositionIndex(0).setTransform(transform);
	}

	@Override
	protected void initTransitionLevel() {

		Transform transform = new Transform();
		transform.setTranslation(new Vec3f(1.5f, 1.3f, 0));
		transform.setScale(new Vec3f(0.8f, 0.8f, 1));

		transitionLevel.getElementByPositionIndex(0).setTransform(transform);

	}

	@Override
	protected void initSpawnLevel() {

		float fScalingFactorSpawnLevel = 0.05f;
		Transform transform = new Transform();
		transform.setTranslation(new Vec3f(6.5f, 5, -0.2f));
		transform.setScale(new Vec3f(fScalingFactorSpawnLevel, fScalingFactorSpawnLevel,
				fScalingFactorSpawnLevel));

		spawnLevel.getElementByPositionIndex(0).setTransform(transform);
	}

	// /**
	// * This method generates only valid associations for Asslaber dataset!
	// */
	// private void generateTissuePatientConnection() {
	//
	// ClinicalUseCase clinicalUseCase =
	// (ClinicalUseCase) generalManager.getUseCase(EDataDomain.CLINICAL_DATA);
	// DataTable clinicalSet = clinicalUseCase.getTable();
	//
	// if (clinicalSet.get(0) == null)
	// return;
	//
	// for (int index = 0; index < clinicalSet.depth(); index++) {
	//
	// mapExperimentToTexturePath.put(index, "data/tissue/breast_" + index % 24
	// + ".jpg");
	// }
	//
	// // for (Integer vaID : clinicalUseCase.getVA(EVAType.CONTENT))
	// // {
	// // table.getDimensionFromVA(
	// // }
	// //
	// // for (IDimension dimension : set) {
	// // String experiment = dimension.getLabel();
	// // mapExperimentToTexturePath.put(experiment, )
	// // }
	//
	// }

	@Override
	public void handleSelectionUpdate(SelectionDelta selectionDelta,
			boolean scrollToSelection, String info) {
		if (selectionDelta.getIDType() == primaryIDType) {
			experiementSelectionManager.setDelta(selectionDelta);

			for (SelectionDeltaItem item : selectionDelta) {
				int id = item.getID() % MAX_VIEWS;
				GLTexture textureView = ((GLTexture) containedGLViews.get(id));
				SelectionType selectionType = item.getSelectionType();
				if (item.isRemove())
					selectionType = SelectionType.NORMAL;
				else {
					handleConnectedElementRep(item, textureView.getID());
				}

				textureView.setCurrentSelectionType(selectionType);
			}
		}
	}

	/**
	 * FIXME: should be moved to a bucket-mediator registers the event-listeners
	 * to the event framework
	 */
	@Override
	public void registerEventListeners() {
		super.registerEventListeners();

		selectionUpdateListener = new SelectionUpdateListener();
		selectionUpdateListener.setHandler(this);
		selectionUpdateListener.setDataDomainID(FOREIGN_DATADOMAIN_TYPE);
		eventPublisher.addListener(SelectionUpdateEvent.class, selectionUpdateListener);

		recordVAUpdateListener = new RecordVAUpdateListener();
		recordVAUpdateListener.setHandler(this);
		recordVAUpdateListener.setDataDomainID(FOREIGN_DATADOMAIN_TYPE);
		eventPublisher.addListener(RecordVAUpdateEvent.class, recordVAUpdateListener);
	}

	/**
	 * FIXME: should be moved to a bucket-mediator registers the event-listeners
	 * to the event framework
	 */
	@Override
	public void unregisterEventListeners() {

		super.unregisterEventListeners();

		if (selectionUpdateListener != null) {
			eventPublisher.removeListener(selectionUpdateListener);
			selectionUpdateListener = null;
		}

		if (recordVAUpdateListener != null) {
			eventPublisher.removeListener(recordVAUpdateListener);
			recordVAUpdateListener = null;
		}

	}

	public RecordSelectionManager getSelectionManager() {
		return experiementSelectionManager;
	}

	public void setPoolSide(boolean poolLeft) {
		this.poolLeft = poolLeft;
	}

	@Override
	protected void removeSelection(int iElementID) {

		experiementSelectionManager.remove(iElementID);
		RecordVADelta vaDelta = new RecordVADelta(recordPerspective.getID(),
				primaryIDType);
		vaDelta.add(VADeltaItem.removeElement(iElementID));

		RecordVADeltaEvent virtualArrayUpdateEvent = new RecordVADeltaEvent();
		virtualArrayUpdateEvent.setSender(this);
		virtualArrayUpdateEvent.setVirtualArrayDelta(vaDelta);
		eventPublisher.triggerEvent(virtualArrayUpdateEvent);
	}

	@Override
	public void handleRecordVAUpdate(String recordPerspectiveID) {

		System.out.println("What to do?");
		// experiementSelectionManager.virtualArrayUpdated(dataDomain
		// .getRecordVA(DataTable.DIMENSION));

	}

	@Override
	protected void handlePickingEvents(PickingType pickingType, PickingMode pickingMode,
			int externalID, Pick pick) {
		super.handlePickingEvents(pickingType, pickingMode, externalID, pick);

		switch (pickingType) {
		case REMOTE_VIEW_SELECTION:
			SelectionType selectionType = SelectionType.NORMAL;
			switch (pickingMode) {
			case MOUSE_OVER:
				selectionType = SelectionType.MOUSE_OVER;
				break;
			case CLICKED:
				selectionType = SelectionType.SELECTION;
				break;
			default:
				return;
			}

			GLTexture textureView = (GLTexture) generalManager.getViewManager()
					.getGLView(externalID);
			if (textureView == null) {
				System.out.println("Warning, unrecognized view ID");
				return;
			}
			int experimentIndex = textureView.getExperimentIndex();

			experiementSelectionManager.clearSelection(selectionType);
			experiementSelectionManager.addToType(selectionType, experimentIndex);

			experiementSelectionManager.addConnectionID(generalManager.getIDCreator()
					.createID(ManagedObjectType.CONNECTION), experimentIndex);

			SelectionDelta delta = experiementSelectionManager.getDelta();

			ConnectedElementRepresentationManager.get().clear(
					experiementSelectionManager.getIDType(), selectionType);

			for (SelectionDeltaItem item : delta) {
				if (item.isRemove())
					continue;
				handleConnectedElementRep(item, textureView.getID());
			}
			setInfo(textureView, experimentIndex);
			SelectionUpdateEvent event = new SelectionUpdateEvent();
			event.setDataDomainID(FOREIGN_DATADOMAIN_TYPE);
			event.setSelectionDelta(delta);
			eventPublisher.triggerEvent(event);

		}
	}

	private void handleConnectedElementRep(SelectionDeltaItem item, int sourceViewID) {

		ArrayList<Vec3f> points = new ArrayList<Vec3f>();
		points.add(new Vec3f(1f, 1f, 0));
		ElementConnectionInformation rep = new ElementConnectionInformation(
				primaryIDType, sourceViewID, points);

		ArrayList<Integer> connectionIDs = item.getConnectionIDs();
		;

		for (Integer connectionID : connectionIDs) {
			ConnectedElementRepresentationManager.get().addSelection(connectionID, rep,
					item.getSelectionType());
		}
	}

	private void setInfo(GLTexture tissueView, Integer experimentIndex) {
		// DimensionVirtualArray va = foreignDataDomain.getTable()
		// .getDimensionPerspective(dimensionPerspectiveID).getVirtualArray();
		//
		//
		// NominalDimension<String> dimension = (NominalDimension<String>)
		// foreignDataDomain
		// .getTable().get(va.get(1));
		// String label = dimension.getRaw(experimentIndex);

		tissueView.setInfo(foreignDataDomain.getDimensionLabel(experimentIndex));
	}

	private void setInfo(SerializedTextureView tissueView, Integer experimentIndex) {
		// DimensionVirtualArray va = foreignDataDomain.getTable()
		// .getDimensionPerspective(dimensionPerspectiveID).getVirtualArray();
		//
		// NominalDimension<String> dimension = (NominalDimension<String>)
		// foreignDataDomain
		// .getTable().get(va.get(1));
		// String label = dimension.getRaw(experimentIndex);

		tissueView.setInfo(foreignDataDomain.getDimensionLabel(experimentIndex));

	}
}
