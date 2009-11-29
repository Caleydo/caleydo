package org.caleydo.core.view.opengl.canvas.remote.viewbrowser;

import gleem.linalg.Vec3f;
import gleem.linalg.open.Transform;

import java.util.HashMap;

import javax.media.opengl.GL;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.EVAType;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.specialized.clinical.ClinicalUseCase;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.tissue.GLTissue;
import org.caleydo.core.view.opengl.canvas.tissue.SerializedTissueView;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;

public class GLTissueViewBrowser
	extends AGLViewBrowser {

	private HashMap<Integer, String> mapExperimentToTexturePath;

	private SelectionManager experiementSelectionManager;

	public GLTissueViewBrowser(GLCaleydoCanvas glCanvas, String sLabel, IViewFrustum viewFrustum) {
		super(glCanvas, sLabel, viewFrustum);

		viewType = EManagedObjectType.GL_TISSUE_VIEW_BROWSER;
		mapExperimentToTexturePath = new HashMap<Integer, String>();
	}

	@Override
	public void setUseCase(IUseCase useCase) {
		super.setUseCase(useCase);

		experiementSelectionManager = new SelectionManager.Builder(EIDType.EXPERIMENT_INDEX).build();
		generateTissuePatientConnection();
	}

	@Override
	protected void addInitialViews() {

		ClinicalUseCase clinicalUseCase =
			(ClinicalUseCase) generalManager.getUseCase(EDataDomain.CLINICAL_DATA);

		for (Integer experimentIndex : clinicalUseCase.getVA(EVAType.CONTENT)) {

			generalManager.getViewGLCanvasManager().createGLEventListener(ECommandType.CREATE_GL_TISSUE,
				parentGLCanvas, "", viewFrustum);

			SerializedTissueView tissue = new SerializedTissueView();
			tissue.setDataDomain(EDataDomain.TISSUE_DATA);
			tissue.setTexturePath(mapExperimentToTexturePath.get(experimentIndex));
			newViews.add(tissue);
		}
	}

	@Override
	protected AGLEventListener createView(GL gl, ASerializedView serView) {

		AGLEventListener glView = super.createView(gl, serView);

		((GLTissue) glView).setTexturePath(((SerializedTissueView) serView).getTexturePath());
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

		float fScalingFactorTransitionLevel = 1;
		Transform transform = new Transform();
		transform.setTranslation(new Vec3f(0, -2f, 0.1f));
		transform.setScale(new Vec3f(fScalingFactorTransitionLevel, fScalingFactorTransitionLevel,
			fScalingFactorTransitionLevel));

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

	/**
	 * This method generates only valid associations for Asslaber dataset!
	 */
	private void generateTissuePatientConnection() {

		ClinicalUseCase clinicalUseCase =
			(ClinicalUseCase) generalManager.getUseCase(EDataDomain.CLINICAL_DATA);
		ISet clinicalSet = clinicalUseCase.getSet();

		if (clinicalSet.get(0) == null)
			return;

		for (int index = 0; index < clinicalSet.depth(); index++) {
			
			// FIXME: just for faster loading of data flipper
			if (index>5)
				break;
			
			mapExperimentToTexturePath.put(index, "data/tissue/breast_" + index % 24 + ".jpg");
		}

		// for (Integer vaID : clinicalUseCase.getVA(EVAType.CONTENT))
		// {
		// set.getStorageFromVA(
		// }
		//		
		// for (IStorage storage : set) {
		// String experiment = storage.getLabel();
		// mapExperimentToTexturePath.put(experiment, )
		// }

	}

}
