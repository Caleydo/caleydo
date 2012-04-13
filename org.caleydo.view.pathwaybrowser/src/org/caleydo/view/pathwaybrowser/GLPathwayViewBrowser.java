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
package org.caleydo.view.pathwaybrowser;

import gleem.linalg.Vec3f;
import gleem.linalg.open.Transform;
import java.util.Set;
import javax.media.opengl.GL2;
import javax.media.opengl.awt.GLCanvas;
import org.caleydo.core.event.view.remote.LoadPathwayEvent;
import org.caleydo.core.event.view.remote.LoadPathwaysByGeneEvent;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.AGLViewBrowser;
import org.caleydo.core.view.opengl.canvas.listener.AddPathwayListener;
import org.caleydo.core.view.opengl.canvas.listener.IRemoteRenderingHandler;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.datadomain.pathway.IPathwayLoader;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.listener.LoadPathwaysByGeneListener;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.view.pathway.GLPathway;
import org.caleydo.view.pathway.SerializedPathwayView;
import org.eclipse.swt.widgets.Composite;

public class GLPathwayViewBrowser extends AGLViewBrowser implements
		IRemoteRenderingHandler, IPathwayLoader {

	public final static String VIEW_TYPE = "org.caleydo.view.pathwaybrowser";

	private LoadPathwaysByGeneListener loadPathwaysByGeneListener = null;
	private AddPathwayListener addPathwayListener = null;

	public GLPathwayViewBrowser(GLCanvas glCanvas, Composite parentComposite,
			ViewFrustum viewFrustum) {

		super(glCanvas, parentComposite, viewFrustum);

		viewLabel = "Pathway Browser";

		viewType = VIEW_TYPE;
		viewSymbol = EIconTextures.PATHWAY_SYMBOL;

	}

	@Override
	protected void addInitialViews() {

		// for (int pathwayIndex = 0; pathwayIndex < MAX_VIEWS; pathwayIndex++)
		// {
		// SerializedPathwayView pathway = new SerializedPathwayView();
		// pathway.setPathwayID(((PathwayGraph)
		// GeneralManager.get().getPathwayManager()
		// .getAllItems().toArray()[pathwayIndex]).getID());
		// pathway.setDataDomainType(dataDomain.getDataDomainType());
		// newViews.add(pathway);
		// }
	}

	@Override
	protected AGLView createView(GL2 gl, ASerializedView serView) {

		AGLView glView = super.createView(gl, serView);

		GLPathway glPathway = (GLPathway) glView;
		glPathway.setPathway(((SerializedPathwayView) serView).getPathwayID());

		return glView;
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
		transform.setTranslation(new Vec3f(0, 1.3f, 0));
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
	public void addPathwayView(int iPathwayID, String dataDomainID) {

		if (dataDomain.getDataDomainID() != dataDomainID)
			return;

		if (!PathwayManager.get().isPathwayVisible(
				PathwayManager.get().getItem(iPathwayID))) {
			SerializedPathwayView serPathway = new SerializedPathwayView(
					dataDomain.getDataDomainID());
			serPathway.setPathwayID(iPathwayID);
			newViews.add(serPathway);
		}
	}

	@Override
	public void loadDependentPathways(Set<PathwayGraph> newPathwayGraphs) {

		// add new pathways to bucket
		for (PathwayGraph pathway : newPathwayGraphs) {
			addPathwayView(pathway.getID(), dataDomain.getDataDomainID());
		}

		if (!newViews.isEmpty()) {
			disableUserInteraction();
		}
	}

	@Override
	public void resetView(boolean reinitialize) {

		if (reinitialize) {
			PathwayManager.get().resetPathwayVisiblityState();
		}
	}

	@Override
	public boolean readyForLoadingNewViews() {
		return super.readyForLoadingNewViews();
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
