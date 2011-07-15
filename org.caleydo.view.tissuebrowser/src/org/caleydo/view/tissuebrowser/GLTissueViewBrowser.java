package org.caleydo.view.tissuebrowser;

import gleem.linalg.Vec3f;
import gleem.linalg.open.Transform;

import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.storage.NominalStorage;
import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDeltaItem;
import org.caleydo.core.data.virtualarray.StorageVirtualArray;
import org.caleydo.core.data.virtualarray.delta.ContentVADelta;
import org.caleydo.core.data.virtualarray.delta.VADeltaItem;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.core.manager.event.data.ReplaceVAEvent;
import org.caleydo.core.manager.event.view.storagebased.ContentVAUpdateEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.event.view.storagebased.VirtualArrayUpdateEvent;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.view.ConnectedElementRepresentationManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.AGLViewBrowser;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.listener.ContentVAUpdateListener;
import org.caleydo.core.view.opengl.canvas.listener.IContentVAUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.ReplaceContentVAListener;
import org.caleydo.core.view.opengl.canvas.listener.SelectionUpdateListener;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.texture.GLTexture;
import org.caleydo.view.texture.SerializedTextureView;

/**
 * FIXME: this view uses and listenes to the clinical data domain despite beeing
 * of the tissue data domain. This should be substituted with mappings in the
 * tissue datadomain.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 * 
 */
public class GLTissueViewBrowser extends AGLViewBrowser implements
		IContentVAUpdateHandler {

	public final static String VIEW_TYPE = "org.caleydo.view.tissuebrowser";

	/**
	 * 
	 * The foreign dataDomain
	 */
	public ASetBasedDataDomain foreignDataDomain;
	public final static String FOREIGN_DATADOMAIN_TYPE = "org.caleydo.datadomain.clinical";

	private HashMap<Integer, String> mapExperimentToTexturePath;

	private ContentSelectionManager experiementSelectionManager;

	private SelectionUpdateListener selectionUpdateListener;
	private ContentVAUpdateListener virtualArrayUpdateListener;
	private ReplaceContentVAListener replaceVirtualArrayListener;

	private IDType primaryIDType;

	private ArrayList<SerializedTextureView> allTissueViews;

	private boolean poolLeft = false;

	public GLTissueViewBrowser(GLCaleydoCanvas glCanvas, ViewFrustum viewFrustum) {
		super(glCanvas, viewFrustum);

		viewType = VIEW_TYPE;
		viewSymbol = EIconTextures.NO_ICON_AVAILABLE;
		mapExperimentToTexturePath = new HashMap<Integer, String>();
	}

	@Override
	public void setDataDomain(IDataDomain dataDomain) {
		this.dataDomain = dataDomain;

		this.foreignDataDomain = (ASetBasedDataDomain) DataDomainManager.get()
				.getDataDomainByID(FOREIGN_DATADOMAIN_TYPE);

		contentVA = foreignDataDomain.getContentVA(ISet.CONTENT);
		primaryIDType = foreignDataDomain.getContentIDType();

		experiementSelectionManager = foreignDataDomain.getContentSelectionManager();

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
			tissue.setDataDomainID("org.caleydo.view.texture");
			tissue.setTexturePath(mapExperimentToTexturePath.get(experimentIndex));

			tissue.setExperimentIndex(experimentIndex);

			allTissueViews.add(tissue);
		}

		for (SerializedTextureView serTissue : allTissueViews) {
			newViews.add(serTissue);
		}
	}

	private void updateViews() {

		if (contentVA.size() > MAX_VIEWS)
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
	// mapExperimentToTexturePath.put(index, "data/tissue/breast_" + index % 24
	// + ".jpg");
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
	public void handleSelectionUpdate(ISelectionDelta selectionDelta,
			boolean scrollToSelection, String info) {
		if (selectionDelta.getIDType() == primaryIDType) {
			experiementSelectionManager.setDelta(selectionDelta);

			for (SelectionDeltaItem item : selectionDelta) {
				int id = item.getPrimaryID() % MAX_VIEWS;
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
		selectionUpdateListener.setDataDomainType(FOREIGN_DATADOMAIN_TYPE);
		eventPublisher.addListener(SelectionUpdateEvent.class, selectionUpdateListener);

		virtualArrayUpdateListener = new ContentVAUpdateListener();
		virtualArrayUpdateListener.setHandler(this);
		virtualArrayUpdateListener.setDataDomainType(FOREIGN_DATADOMAIN_TYPE);
		eventPublisher.addListener(VirtualArrayUpdateEvent.class,
				virtualArrayUpdateListener);

		replaceVirtualArrayListener = new ReplaceContentVAListener();
		replaceVirtualArrayListener.setHandler(this);
		replaceVirtualArrayListener.setDataDomainType(FOREIGN_DATADOMAIN_TYPE);
		eventPublisher.addListener(ReplaceVAEvent.class, replaceVirtualArrayListener);

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

		if (virtualArrayUpdateListener != null) {
			eventPublisher.removeListener(virtualArrayUpdateListener);
			virtualArrayUpdateListener = null;
		}

		if (replaceVirtualArrayListener != null) {
			eventPublisher.removeListener(replaceVirtualArrayListener);
			replaceVirtualArrayListener = null;
		}

	}

	public ContentSelectionManager getSelectionManager() {
		return experiementSelectionManager;
	}

	public void setPoolSide(boolean poolLeft) {
		this.poolLeft = poolLeft;
	}

	@Override
	protected void removeSelection(int iElementID) {

		experiementSelectionManager.remove(iElementID);
		ContentVADelta vaDelta = new ContentVADelta(ISet.CONTENT, primaryIDType);
		vaDelta.add(VADeltaItem.removeElement(iElementID));

		ContentVAUpdateEvent virtualArrayUpdateEvent = new ContentVAUpdateEvent();
		virtualArrayUpdateEvent.setSender(this);
		virtualArrayUpdateEvent.setVirtualArrayDelta(vaDelta);
		virtualArrayUpdateEvent.setInfo(getShortInfo());
		eventPublisher.triggerEvent(virtualArrayUpdateEvent);
	}

	@Override
	public void handleVAUpdate(ContentVADelta vaDelta, String info) {
		if (vaDelta.getIDType() == primaryIDType) {
			experiementSelectionManager.setVADelta(vaDelta);
		}
	}

	@Override
	public void replaceContentVA(int setID, String dataDomainType, String vaType) {
		// if (idCategory != EIDCategory.EXPERIMENT)
		// return;
		// s
		// IDataDomain clinicalUseCase = GeneralManager.get().getUseCase(
		// EDataDomain.CLINICAL_DATA);
		//
		// String primaryVAType =
		// clinicalUseCase.getVATypeForIDCategory(idCategory);
		// if (primaryVAType == null)
		// return;
		//
		// contentVA = clinicalUseCase.getContentVA(vaType);
		// // contentSelectionManager.setVA(contentVA);
		//
		// initData();
		// updateViews();
	}

	@Override
	protected void handlePickingEvents(EPickingType pickingType,
			EPickingMode pickingMode, int externalID, Pick pick) {
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

			GLTexture textureView = (GLTexture) generalManager.getViewGLCanvasManager()
					.getGLView(externalID);
			if (textureView == null) {
				System.out.println("Warning, unrecognized view ID");
				return;
			}
			int experimentIndex = textureView.getExperimentIndex();

			experiementSelectionManager.clearSelection(selectionType);
			experiementSelectionManager.addToType(selectionType, experimentIndex);

			experiementSelectionManager.addConnectionID(generalManager.getIDCreator()
					.createID(EManagedObjectType.CONNECTION), experimentIndex);

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
			event.setDataDomainType(FOREIGN_DATADOMAIN_TYPE);
			event.setSelectionDelta(delta);
			eventPublisher.triggerEvent(event);

		}
	}

	private void handleConnectedElementRep(SelectionDeltaItem item, int sourceViewID) {

		ArrayList<Vec3f> points = new ArrayList<Vec3f>();
		points.add(new Vec3f(1f, 1f, 0));
		SelectedElementRep rep = new SelectedElementRep(primaryIDType, sourceViewID,
				points);

		ArrayList<Integer> connectionIDs = item.getConnectionIDs();
		;

		for (Integer connectionID : connectionIDs) {
			ConnectedElementRepresentationManager.get().addSelection(connectionID, rep,
					item.getSelectionType());
		}
	}

	private void setInfo(GLTexture tissueView, Integer experimentIndex) {
		StorageVirtualArray va = foreignDataDomain.getSet().getStorageData(ISet.STORAGE)
				.getStorageVA();

		NominalStorage<String> storage = (NominalStorage<String>) foreignDataDomain
				.getSet().get(va.get(1));
		String label = storage.getRaw(experimentIndex);

		tissueView.setInfo(label);
	}

	private void setInfo(SerializedTextureView tissueView, Integer experimentIndex) {
		StorageVirtualArray va = foreignDataDomain.getSet().getStorageData(ISet.STORAGE)
				.getStorageVA();

		NominalStorage<String> storage = (NominalStorage<String>) foreignDataDomain
				.getSet().get(va.get(1));
		String label = storage.getRaw(experimentIndex);

		tissueView.setInfo(label);
	}

}
