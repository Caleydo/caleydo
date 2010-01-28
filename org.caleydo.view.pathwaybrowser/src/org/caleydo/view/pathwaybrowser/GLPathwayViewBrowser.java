package org.caleydo.view.pathwaybrowser;

import gleem.linalg.Vec3f;
import gleem.linalg.open.Transform;

import java.util.Set;

import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.manager.event.view.remote.LoadPathwayEvent;
import org.caleydo.core.manager.event.view.remote.LoadPathwaysByGeneEvent;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLViewBrowser;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;
import org.caleydo.rcp.view.listener.AddPathwayListener;
import org.caleydo.rcp.view.listener.IRemoteRenderingHandler;
import org.caleydo.rcp.view.listener.LoadPathwaysByGeneListener;
import org.caleydo.view.pathway.SerializedPathwayView;

public class GLPathwayViewBrowser extends AGLViewBrowser
		implements
			IRemoteRenderingHandler {

	public final static String VIEW_ID = "org.caleydo.view.pathwaybrowser";

	private LoadPathwaysByGeneListener loadPathwaysByGeneListener = null;
	private AddPathwayListener addPathwayListener = null;

	public GLPathwayViewBrowser(GLCaleydoCanvas glCanvas, String sLabel,
			IViewFrustum viewFrustum) {
		super(glCanvas, sLabel, viewFrustum);

		viewType = "org.caleydo.view.pathwaybrowser";
	}

	@Override
	protected void addInitialViews() {

		// for (int pathwayIndex = 0; pathwayIndex < 5; pathwayIndex++) {
		// SerializedPathwayView pathway = new SerializedPathwayView();
		// pathway.setPathwayID(((PathwayGraph)
		// GeneralManager.get().getPathwayManager().getAllItems()
		// .toArray()[pathwayIndex]).getID());
		// pathway.setDataDomain(EDataDomain.PATHWAY_DATA);
		// newViews.add(pathway);
		// }
	}

	@Override
	protected void initFocusLevel() {
		Transform transform = new Transform();
		transform.setTranslation(new Vec3f(0, 1.3f, 0));
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
			} else {
				fSelectedScaling = 1;
				fYAdd -= 0.5f * fSelectedScaling;
			}

			transform = new Transform();
			transform.setTranslation(new Vec3f(6.5f, fYAdd, 0));
			transform.setScale(new Vec3f(fScalingFactorPoolLevel
					* fSelectedScaling, fScalingFactorPoolLevel
					* fSelectedScaling, fScalingFactorPoolLevel
					* fSelectedScaling));

			poolLevel.getElementByPositionIndex(iRemoteLevelElementIndex)
					.setTransform(transform);
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

		externalSelectionLevel.getElementByPositionIndex(0).setTransform(
				transform);
	}

	@Override
	protected void initTransitionLevel() {

		Transform transform = new Transform();
		transform.setTranslation(new Vec3f(0, 1.3f, 0));
		transform.setScale(new Vec3f(0.8f, 0.8f, 1));

		transitionLevel.getElementByPositionIndex(0).setTransform(transform);

	}

	@Override
	protected void initSpawnLevel() {

		float fScalingFactorSpawnLevel = 0.05f;
		Transform transform = new Transform();
		transform.setTranslation(new Vec3f(6.5f, 5, -0.2f));
		transform.setScale(new Vec3f(fScalingFactorSpawnLevel,
				fScalingFactorSpawnLevel, fScalingFactorSpawnLevel));

		spawnLevel.getElementByPositionIndex(0).setTransform(transform);
	}

	@Override
	public String getShortInfo() {
		return "Pathway Browser";
	}

	@Override
	public String getDetailedInfo() {
		StringBuffer sInfoText = new StringBuffer();
		sInfoText.append("Pathway Browser");
		return sInfoText.toString();
	}

	@Override
	public void registerEventListeners() {

		super.registerEventListeners();

		addPathwayListener = new AddPathwayListener();
		addPathwayListener.setHandler(this);
		eventPublisher.addListener(LoadPathwayEvent.class, addPathwayListener);

		loadPathwaysByGeneListener = new LoadPathwaysByGeneListener();
		loadPathwaysByGeneListener.setHandler(this);
		eventPublisher.addListener(LoadPathwaysByGeneEvent.class,
				loadPathwaysByGeneListener);
	}

	@Override
	public void unregisterEventListeners() {

		super.unregisterEventListeners();

		if (addPathwayListener != null) {
			eventPublisher.removeListener(addPathwayListener);
			addPathwayListener = null;
		}

		if (loadPathwaysByGeneListener != null) {
			eventPublisher.removeListener(loadPathwaysByGeneListener);
			loadPathwaysByGeneListener = null;
		}
	}

	@Override
	public void addPathwayView(int iPathwayID) {
		if (!generalManager.getPathwayManager().isPathwayVisible(
				generalManager.getPathwayManager().getItem(iPathwayID))) {
			SerializedPathwayView serPathway = new SerializedPathwayView(
					EDataDomain.GENETIC_DATA);
			serPathway.setPathwayID(iPathwayID);
			newViews.add(serPathway);
		}
	}

	@Override
	public void loadDependentPathways(Set<PathwayGraph> newPathwayGraphs) {

		// add new pathways to bucket
		for (PathwayGraph pathway : newPathwayGraphs) {
			addPathwayView(pathway.getID());
		}

		if (!newViews.isEmpty()) {
			disableUserInteraction();
		}
	}

	@Override
	public void setConnectionLinesEnabled(boolean enabled) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setGeneMappingEnabled(boolean geneMappingEnabled) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNeighborhoodEnabled(boolean neighborhoodEnabled) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPathwayTexturesEnabled(boolean pathwayTexturesEnabled) {
		// TODO Auto-generated method stub

	}

	@Override
	public void toggleNavigationMode() {
		// TODO Auto-generated method stub

	}

	@Override
	public void toggleZoom() {
		// TODO Auto-generated method stub

	}
}
