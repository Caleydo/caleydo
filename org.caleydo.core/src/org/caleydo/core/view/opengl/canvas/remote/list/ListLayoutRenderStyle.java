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
package org.caleydo.core.view.opengl.canvas.remote.list;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.open.Transform;

import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.remote.ARemoteViewLayoutRenderStyle;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevel;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;

/**
 * Render style for list view.
 * 
 * @author Marc Streit
 */
public class ListLayoutRenderStyle
	extends ARemoteViewLayoutRenderStyle {

	/**
	 * Constructor.
	 */
	public ListLayoutRenderStyle(ViewFrustum viewFrustum) {
		super(viewFrustum);
		initLayout();
	}

	/**
	 * Constructor.
	 */
	public ListLayoutRenderStyle(ViewFrustum viewFrustum,
		final ARemoteViewLayoutRenderStyle previousLayoutStyle) {
		super(viewFrustum, previousLayoutStyle);
		initLayout();
	}

	private void initLayout() {
		eProjectionMode = CameraProjectionMode.ORTHOGRAPHIC;

		fScalingFactorFocusLevel = 0.32f;
		fScalingFactorStackLevel = 0.13f;
		fScalingFactorPoolLevel = 0.02f;
		fScalingFactorSelectionLevel = 1f;
		fScalingFactorTransitionLevel = 0.025f;
		fScalingFactorSpawnLevel = 0.005f;
	}

	@Override
	public RemoteLevel initFocusLevel() {

		fScalingFactorFocusLevel = 4 * 0.045f / fAspectRatio;

		Transform transform = new Transform();
		transform.setTranslation(new Vec3f(-0.5f / fAspectRatio, -1.4f, 0f));
		transform.setScale(new Vec3f(fScalingFactorFocusLevel, fScalingFactorFocusLevel,
			fScalingFactorFocusLevel));

		focusLevel.getElementByPositionIndex(0).setTransform(transform);

		return focusLevel;
	}

	@Override
	public RemoteLevel initStackLevel() {
		int iMaxLayers = 4;

		// Create free pathway layer spots
		Transform transform;

		for (int iLayerIndex = 0; iLayerIndex < iMaxLayers; iLayerIndex++) {
			// Store current model-view matrix
			transform = new Transform();
			transform.setTranslation(new Vec3f(0, 0, 0f));
			transform.setScale(new Vec3f(fScalingFactorStackLevel, fScalingFactorStackLevel,
				fScalingFactorStackLevel));
			transform.setRotation(new Rotf(new Vec3f(0, 0, 0), 0));

			stackLevel.getElementByPositionIndex(iLayerIndex).setTransform(transform);
		}

		return stackLevel;
	}

	@Override
	public RemoteLevel initPoolLevel(int iSelectedRemoteLevelElementID) {
		Transform transform;

		float fSelectedScaling = 1;
		float fYAdd = 1.4f;
		int iRemoteLevelElementIndex = 0;

		for (RemoteLevelElement element : poolLevel.getAllElements()) {
			if (element.getID() == iSelectedRemoteLevelElementID) {
				fSelectedScaling = 1.3f;
			}
			else {
				fSelectedScaling = 1;
			}

			transform = new Transform();
			fYAdd -= 0.15f * fSelectedScaling;
			transform.setTranslation(new Vec3f(-1.45f * 1 / fAspectRatio, fYAdd, 4.1f));
			transform.setScale(new Vec3f(fSelectedScaling * fScalingFactorPoolLevel, fSelectedScaling
				* fScalingFactorPoolLevel, fSelectedScaling * fScalingFactorPoolLevel));

			poolLevel.getElementByPositionIndex(iRemoteLevelElementIndex).setTransform(transform);
			iRemoteLevelElementIndex++;
		}

		return poolLevel;
	}

	@Override
	public RemoteLevel initMemoLevel() {
		fScalingFactorSelectionLevel = 0.82f;

		Transform transform = new Transform();
		transform.setTranslation(new Vec3f(1.6f / fAspectRatio - fPoolLayerWidth, -1.7f, 0f));
		transform.setScale(new Vec3f(fScalingFactorSelectionLevel, fScalingFactorSelectionLevel,
			fScalingFactorSelectionLevel));

		selectionLevel.getElementByPositionIndex(0).setTransform(transform);

		// Init color bar position
		// fColorBarXPos = 1.1f / fAspectRatio;
		// fColorBarYPos = -1;
		// fColorBarWidth = 0.1f;
		// fColorBarHeight = 2.8f;

		return selectionLevel;
	}

	@Override
	public RemoteLevel initTransitionLevel() {
		Transform transform = new Transform();
		transform.setTranslation(new Vec3f(0f, 0f, 4.1f));
		transform.setScale(new Vec3f(fScalingFactorTransitionLevel, fScalingFactorTransitionLevel,
			fScalingFactorTransitionLevel));

		transitionLevel.getElementByPositionIndex(0).setTransform(transform);

		return transitionLevel;
	}

	@Override
	public RemoteLevel initSpawnLevel() {
		Transform transform = new Transform();
		transform.setTranslation(new Vec3f(-4.4f, 3.9f, 4.1f));
		transform.setScale(new Vec3f(fScalingFactorSpawnLevel, fScalingFactorSpawnLevel,
			fScalingFactorSpawnLevel));

		spawnLevel.getElementByPositionIndex(0).setTransform(transform);

		return spawnLevel;
	}
}
