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
package org.caleydo.view.bucket;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.open.Transform;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.remote.ARemoteViewLayoutRenderStyle;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevel;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;

/**
 * Render style for jukebox view.
 * 
 * @author Marc Streit
 */
public class JukeboxLayoutRenderStyle extends ARemoteViewLayoutRenderStyle {

	/**
	 * Constructor.
	 */
	public JukeboxLayoutRenderStyle(ViewFrustum viewFrustum) {
		super(viewFrustum);
		initLayout();
	}

	/**
	 * Constructor.
	 */
	public JukeboxLayoutRenderStyle(ViewFrustum viewFrustum,
			final ARemoteViewLayoutRenderStyle previousLayoutStyle) {
		super(viewFrustum, previousLayoutStyle);
		initLayout();
	}

	private void initLayout() {
		eProjectionMode = CameraProjectionMode.ORTHOGRAPHIC;

		fScalingFactorFocusLevel = 0.28f;
		fScalingFactorStackLevel = 0.13f;
		fScalingFactorPoolLevel = 0.02f;
		fScalingFactorSelectionLevel = 0.05f;
		fScalingFactorTransitionLevel = 0.025f;
		fScalingFactorSpawnLevel = 0.005f;
	}

	@Override
	public RemoteLevel initFocusLevel() {
		fScalingFactorFocusLevel = 4 * 0.045f / fAspectRatio;

		Transform transform = new Transform();
		transform.setTranslation(new Vec3f(0f / fAspectRatio, -0.9f, 0f));
		transform.setScale(new Vec3f(fScalingFactorFocusLevel, fScalingFactorFocusLevel,
				fScalingFactorFocusLevel));

		focusLevel.getElementByPositionIndex(0).setTransform(transform);

		return focusLevel;
	}

	@Override
	public RemoteLevel initStackLevel() {
		float fTiltAngleDegree = 57; // degree
		float fTiltAngleRad = Vec3f.convertGrad2Radiant(fTiltAngleDegree);
		float fLayerYPos = 0.6f;
		int iMaxLayers = 4;

		// Create free pathway layer spots
		Transform transform;
		for (int iLevelIndex = 0; iLevelIndex < iMaxLayers; iLevelIndex++) {
			// Store current model-view matrix
			transform = new Transform();
			transform.setTranslation(new Vec3f(-0.9f / fAspectRatio, fLayerYPos, 0f));

			// DKT horizontal stack
			// transform.setTranslation(new Vec3f(-2.7f + fLayerYPos, 1.1f, 0));
			// transform.setRotation(new Rotf(new Vec3f(-0.7f, -1f, 0),
			// fTiltAngleRad));
			transform.setScale(new Vec3f(fScalingFactorStackLevel,
					fScalingFactorStackLevel, fScalingFactorStackLevel));
			transform.setRotation(new Rotf(new Vec3f(-1f, -0.7f, 0), fTiltAngleRad));

			stackLevel.getElementByPositionIndex(iLevelIndex).setTransform(transform);

			fLayerYPos -= 0.7f;
		}

		return stackLevel;
	}

	@Override
	public RemoteLevel initPoolLevel(int iSelectedRemoteLevelElementID) {
		float fSelectedScaling = 1;
		float fYAdd = -1.4f;

		int iRemoteLevelElementID = 0;
		for (RemoteLevelElement element : poolLevel.getAllElements()) {
			if (element.getID() == iSelectedRemoteLevelElementID) {
				fSelectedScaling = 2;
			} else {
				fSelectedScaling = 1;
			}

			Transform transform = new Transform();
			transform.setTranslation(new Vec3f(-1.45f * 1 / fAspectRatio, fYAdd, 4.1f));

			fYAdd += 0.19f * fSelectedScaling;

			transform.setScale(new Vec3f(fSelectedScaling * fScalingFactorPoolLevel,
					fSelectedScaling * fScalingFactorPoolLevel, fSelectedScaling
							* fScalingFactorPoolLevel));

			poolLevel.getElementByPositionIndex(iRemoteLevelElementID).setTransform(
					transform);
			iRemoteLevelElementID++;
		}

		return poolLevel;
	}

	@Override
	public RemoteLevel initMemoLevel() {
		Transform transform;
		float fMemoPos = 0.0f;
		for (int iMemoIndex = 0; iMemoIndex < selectionLevel.getCapacity(); iMemoIndex++) {
			// Store current model-view matrix
			transform = new Transform();
			transform.setTranslation(new Vec3f(fMemoPos, -1.4f, 4.1f));
			transform.setScale(new Vec3f(fScalingFactorSelectionLevel,
					fScalingFactorSelectionLevel, fScalingFactorSelectionLevel));

			selectionLevel.getElementByPositionIndex(0).setTransform(transform);
			;

			fMemoPos += 0.42f;
		}

		fTrashCanXPos = 1.3f / fAspectRatio;
		fTrashCanYPos = -1.4f;
		fTrashCanWidth = 0.3f;
		fTrashCanHeight = 0.35f;

		// fColorBarXPos = -0.1f / fAspectRatio;
		// fColorBarYPos = -0.9f;
		// fColorBarWidth = 0.1f;
		// fColorBarHeight = 2f;

		return selectionLevel;
	}

	@Override
	public RemoteLevel initTransitionLevel() {
		Transform transform = new Transform();
		transform.setTranslation(new Vec3f(0f, 0f, 4.1f));
		transform.setScale(new Vec3f(fScalingFactorTransitionLevel,
				fScalingFactorTransitionLevel, fScalingFactorTransitionLevel));

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
		;

		return spawnLevel;
	}
}
