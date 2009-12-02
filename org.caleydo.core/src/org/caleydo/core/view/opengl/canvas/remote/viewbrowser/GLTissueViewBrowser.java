package org.caleydo.core.view.opengl.canvas.remote.viewbrowser;

import gleem.linalg.Vec3f;
import gleem.linalg.open.Transform;

import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.GL;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.EVAType;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.IVirtualArrayDelta;
import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.event.data.ReplaceVirtualArrayEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.event.view.storagebased.VirtualArrayUpdateEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.listener.IVirtualArrayUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.ReplaceVirtualArrayListener;
import org.caleydo.core.view.opengl.canvas.listener.SelectionUpdateListener;
import org.caleydo.core.view.opengl.canvas.listener.VirtualArrayUpdateListener;
import org.caleydo.core.view.opengl.canvas.tissue.GLTissue;
import org.caleydo.core.view.opengl.canvas.tissue.SerializedTissueView;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;

public class GLTissueViewBrowser
	extends AGLViewBrowser
	implements IVirtualArrayUpdateHandler {

	private static final int VIEW_THRESHOLD = 20;

	private HashMap<Integer, String> mapExperimentToTexturePath;

	private SelectionManager experiementSelectionManager;

	private SelectionUpdateListener selectionUpdateListener;
	private VirtualArrayUpdateListener virtualArrayUpdateListener;
	private ReplaceVirtualArrayListener replaceVirtualArrayListener;

	private EIDType primaryIDType = EIDType.EXPERIMENT_INDEX;

	private ArrayList<SerializedTissueView> allTissueViews;

	public GLTissueViewBrowser(GLCaleydoCanvas glCanvas, String sLabel, IViewFrustum viewFrustum) {
		super(glCanvas, sLabel, viewFrustum);

		viewType = EManagedObjectType.GL_TISSUE_VIEW_BROWSER;
		mapExperimentToTexturePath = new HashMap<Integer, String>();
	}

	@Override
	public void setUseCase(IUseCase useCase) {
		super.setUseCase(useCase);
		contentVA = GeneralManager.get().getUseCase(EDataDomain.CLINICAL_DATA).getVA(EVAType.CONTENT);
		experiementSelectionManager = new SelectionManager.Builder(primaryIDType).build();
		experiementSelectionManager.setVA(contentVA);

		addInitialViews();
	}

	@Override
	protected void addInitialViews() {

		newViews.clear();
		allTissueViews = new ArrayList<SerializedTissueView>();

		ISet geneticSet = generalManager.getUseCase(EDataDomain.GENETIC_DATA).getSet();
		for (int experimentIndex = 0; experimentIndex < geneticSet.size(); experimentIndex++) {

			generalManager.getViewGLCanvasManager().createGLEventListener(ECommandType.CREATE_GL_TISSUE,
				parentGLCanvas, "", viewFrustum);

			mapExperimentToTexturePath.put(experimentIndex, "data/tissue/breast_" + experimentIndex % 24
				+ ".jpg");

			SerializedTissueView tissue = new SerializedTissueView();
			tissue.setDataDomain(EDataDomain.TISSUE_DATA);
			tissue.setTexturePath(mapExperimentToTexturePath.get(experimentIndex));
			tissue.setLabel(geneticSet.get(experimentIndex).getLabel());
			tissue.setExperimentIndex(experimentIndex);

			allTissueViews.add(tissue);
		}
	}

	private void updateViews() {

		if (contentVA.size() > VIEW_THRESHOLD)
			return;

		newViews.clear();

		clearRemoteLevel(focusLevel);
		clearRemoteLevel(poolLevel);
		clearRemoteLevel(transitionLevel);

		for (Integer experimentIndex : contentVA) {
			newViews.add(allTissueViews.get(experimentIndex));
		}
	}

	@Override
	protected AGLEventListener createView(GL gl, ASerializedView serView) {

		AGLEventListener glView = super.createView(gl, serView);

		((GLTissue) glView).setTexturePath(((SerializedTissueView) serView).getTexturePath());
		glView.setLabel(((SerializedTissueView) serView).getLabel());
		((GLTissue) glView).setExperimentIndex(((SerializedTissueView) serView).getExperimentIndex());
		return glView;
	}

	@Override
	protected void initFocusLevel() {
		Transform transform = new Transform();
		transform.setTranslation(new Vec3f(1.5f, 1.3f, 0));
		transform.setScale(new Vec3f(0.8f, 0.8f, 1));

		focusLevel.getElementByPositionIndex(0).setTransform(transform);
	}

	@Override
	protected void initPoolLevel(int iSelectedRemoteLevelElementID) {
		Transform transform;

		float fScalingFactorPoolLevel = 0.05f;
		float fSelectedScaling = 1;
		float fYAdd = 8f;

		int iRemoteLevelElementIndex = 0;
		for (RemoteLevelElement element : poolLevel.getAllElements()) {

			if (element.getID() == iSelectedRemoteLevelElementID) {
				fSelectedScaling = 1.8f;
				fYAdd -= 0.6f * fSelectedScaling;
			}
			else {
				fSelectedScaling = 1;
				fYAdd -= 0.5f * fSelectedScaling;
			}

			transform = new Transform();
			transform.setTranslation(new Vec3f(0.1f, fYAdd, 0));
			transform.setScale(new Vec3f(fScalingFactorPoolLevel * fSelectedScaling, fScalingFactorPoolLevel
				* fSelectedScaling, fScalingFactorPoolLevel * fSelectedScaling));

			poolLevel.getElementByPositionIndex(iRemoteLevelElementIndex).setTransform(transform);
			iRemoteLevelElementIndex++;
		}
	}

	@Override
	protected void initExternalSelectionLevel() {

		float fScalingFactorSelectionLevel = 1;
		Transform transform = new Transform();
		transform.setTranslation(new Vec3f(1, -2.01f, 0));
		transform.setScale(new Vec3f(fScalingFactorSelectionLevel, fScalingFactorSelectionLevel,
			fScalingFactorSelectionLevel));

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

	@Override
	public String getShortInfo() {
		return "Tissue Browser";
	}

	@Override
	public String getDetailedInfo() {
		StringBuffer sInfoText = new StringBuffer();
		sInfoText.append("Tissue Browser");
		return sInfoText.toString();
	}

	// /**
	// * This method generates only valid associations for Asslaber dataset!
	// */
	// private void generateTissuePatientConnection() {
	//
	// ClinicalUseCase clinicalUseCase =
	// (ClinicalUseCase) generalManager.getUseCase(EDataDomain.CLINICAL_DATA);
	// ISet clinicalSet = clinicalUseCase.getSet();
	//
	// if (clinicalSet.get(0) == null)
	// return;
	//
	// for (int index = 0; index < clinicalSet.depth(); index++) {
	//
	// mapExperimentToTexturePath.put(index, "data/tissue/breast_" + index % 24 + ".jpg");
	// }
	//
	// // for (Integer vaID : clinicalUseCase.getVA(EVAType.CONTENT))
	// // {
	// // set.getStorageFromVA(
	// // }
	// //
	// // for (IStorage storage : set) {
	// // String experiment = storage.getLabel();
	// // mapExperimentToTexturePath.put(experiment, )
	// // }
	//
	// }

	@Override
	public void handleSelectionUpdate(ISelectionDelta selectionDelta, boolean scrollToSelection, String info) {
		if (selectionDelta.getIDType() == primaryIDType)
			experiementSelectionManager.setDelta(selectionDelta);
	}

	/**
	 * FIXME: should be moved to a bucket-mediator registers the event-listeners to the event framework
	 */
	@Override
	public void registerEventListeners() {
		super.registerEventListeners();

		selectionUpdateListener = new SelectionUpdateListener();
		selectionUpdateListener.setHandler(this);
		eventPublisher.addListener(SelectionUpdateEvent.class, selectionUpdateListener);

		virtualArrayUpdateListener = new VirtualArrayUpdateListener();
		virtualArrayUpdateListener.setHandler(this);
		eventPublisher.addListener(VirtualArrayUpdateEvent.class, virtualArrayUpdateListener);

		replaceVirtualArrayListener = new ReplaceVirtualArrayListener();
		replaceVirtualArrayListener.setHandler(this);
		eventPublisher.addListener(ReplaceVirtualArrayEvent.class, replaceVirtualArrayListener);

	}

	/**
	 * FIXME: should be moved to a bucket-mediator registers the event-listeners to the event framework
	 */
	@Override
	public void unregisterEventListeners() {

		super.unregisterEventListeners();

		if (selectionUpdateListener != null) {
			eventPublisher.removeListener(selectionUpdateListener);
			selectionUpdateListener = null;
		}

		if (virtualArrayUpdateListener != null) {
			eventPublisher.removeListener(virtualArrayUpdateListener);
			virtualArrayUpdateListener = null;
		}

		if (replaceVirtualArrayListener != null) {
			eventPublisher.removeListener(replaceVirtualArrayListener);
			replaceVirtualArrayListener = null;
		}

	}

	@Override
	public void handleVirtualArrayUpdate(IVirtualArrayDelta vaDelta, String info) {
		if (vaDelta.getIDType() == primaryIDType) {
			experiementSelectionManager.setVADelta(vaDelta);
		}
	}

	@Override
	public void replaceVirtualArray(EIDCategory idCategory, EVAType vaType) {

		if (idCategory != EIDCategory.EXPERIMENT)
			return;

		IUseCase clinicalUseCase = GeneralManager.get().getUseCase(EDataDomain.CLINICAL_DATA);

		String primaryVAType = clinicalUseCase.getVATypeForIDCategory(idCategory);
		if (primaryVAType == null)
			return;

		EVAType suggestedVAType = EVAType.getVATypeForPrimaryVAType(primaryVAType);

		if (vaType != suggestedVAType || vaType.getPrimaryVAType() != primaryVAType)
			return;

		if (vaType == storageVAType) {
			storageVA = clinicalUseCase.getVA(vaType);
			// storageSelectionManager.setVA(storageVA);
		}
		else if (vaType == contentVAType) {
			contentVA = clinicalUseCase.getVA(vaType);
			// contentSelectionManager.setVA(contentVA);
		}
		else
			return;

		initData();
		updateViews();
	}

	public SelectionManager getSelectionManager() {
		return experiementSelectionManager;
	}
}
