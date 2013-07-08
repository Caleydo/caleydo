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
package org.caleydo.core.view.opengl.camera;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;

/**
 * Camera model using yaw, pitch and roll to manipulate the camera. Note: using OpenGL2 camera convention.
 * camera viewing in -z direction onto the x-y plane.
 * 
 * @see org.caleydo.core.view.opengl.camera.IViewCamera
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class ViewCameraPitchRollYaw
	extends ViewCameraBase {

	protected float fRollZ = 0.0f;

	protected float fPitchY = 0.0f;

	protected float fYawX = 0.0f;

	protected Rotf quadResult = new Rotf();

	protected Rotf quatRollZ = new Rotf();

	protected Rotf quatPitchY = new Rotf();

	protected Rotf quatYawX = new Rotf();

	/**
	 * Defines update behavior. If TURE the ViewMatrix is updated via setViewRotate(Rotf) .
	 * 
	 * @see prometheus.data.collection.view.camera.ViewCameraBase#setCameraRotation(Rotf)
	 * @see prometheus.data.collection.view.camera.ViewCameraBase#getCameraMatrix()
	 */
	protected boolean bEnableImmediateCameraUpdate = true;

	/**
	 * Constructor.
	 */
	public ViewCameraPitchRollYaw(int iId) {

		super(iId);
	}

	private void updateRollZ(final float fSetRadiant) {

		quatRollZ.set(new Vec3f((float) Math.sin(fSetRadiant * 0.5f), 0, 0),
			(float) Math.cos(fSetRadiant * 0.5f));

		updateQuaternion();
	}

	private void updatePitchY(final float fSetRadiant) {

		quatPitchY.set(new Vec3f(0, (float) Math.sin(fSetRadiant * 0.5f), 0),
			(float) Math.cos(fSetRadiant * 0.5f));

		updateQuaternion();
	}

	private void updateYawX(final float fSetRadiant) {

		quatYawX.set(new Vec3f(0, 0, (float) Math.sin(fSetRadiant * 0.5f)),
			(float) Math.cos(fSetRadiant * 0.5f));

		updateQuaternion();
	}

	/**
	 * Updates quaternion from roll, pitch and yaw. If prometheus.data.collection
	 * .view.camera.ViewCameraPitchRollYaw#bEnableImmediateCameraUpdate is TRUE the ViewMatrix is updated by
	 * calling prometheus.data.collection.view.camera.ViewCameraBase#setViewRotate(Rotf) .
	 * 
	 * @see prometheus.data.collection.view.camera.ViewCameraBase#setCameraRotation(Rotf)
	 * @see prometheus.data.collection.view.camera.ViewCameraPitchRollYaw#bEnableImmediateCameraUpdate
	 */
	private void updateQuaternion() {

		quadResult = quatRollZ.times(quatPitchY).times(quatYawX);

		if (bEnableImmediateCameraUpdate) {
			setCameraRotation(quadResult);
		}
	}

	/**
	 * Sets yaw angle in radians and updates the quaternion.
	 * 
	 * @see prometheus.data.collection.view.camera.ViewCameraPitchRollYaw#addYawX(float)
	 * @param fSetRadiant
	 *            yaw angle in radians
	 */
	public void setYawX(final float fSetRadiant) {

		fYawX = fSetRadiant;

		updateYawX(fSetRadiant);
	}

	/**
	 * Sets pitch angle in radians and updates the quaternion.
	 * 
	 * @see prometheus.data.collection.view.camera.ViewCameraPitchRollYaw#addPitchY(float)
	 * @param fSetRadiant
	 *            pitch angle in radians
	 */
	public void setPitchY(final float fSetRadiant) {

		fPitchY = fSetRadiant;

		updatePitchY(fSetRadiant);
	}

	/**
	 * Sets roll angle in radians and updates the quaternion.
	 * 
	 * @see prometheus.data.collection.view.camera.ViewCameraPitchRollYaw#addRollZ(float)
	 * @param fSetRadiant
	 *            roll angle in radians
	 */
	public void setRollZ(final float fSetRadiant) {

		fRollZ = fSetRadiant;

		updateRollZ(fSetRadiant);
	}

	/**
	 * Adds a roll increment in radians and updates the quaternion.
	 * 
	 * @see prometheus.data.collection.view.camera.ViewCameraPitchRollYaw#setYawX(float)
	 * @param fSetRadiant
	 *            roll increment in radians
	 */
	public void addYawX(final float fSetRadiant) {

		fYawX += fSetRadiant;

		updateYawX(fYawX);
	}

	/**
	 * Adds a roll increment in radians and updates the quaternion.
	 * 
	 * @see prometheus.data.collection.view.camera.ViewCameraPitchRollYaw#setPitchY(float)
	 * @param fSetRadiant
	 *            roll increment in radians
	 */
	public void addPitchY(final float fSetRadiant) {

		fPitchY += fSetRadiant;

		updatePitchY(fPitchY);
	}

	/**
	 * Adds a roll increment in radians and updates the quaternion.
	 * 
	 * @see prometheus.data.collection.view.camera.ViewCameraPitchRollYaw#setRollZ(float)
	 * @param fSetRadiant
	 *            roll increment in radians
	 */
	public void addRollZ(final float fSetRadiant) {

		fRollZ += fSetRadiant;

		updateRollZ(fRollZ);
	}

	/**
	 * Call set method by index.
	 * 
	 * @see prometheus.data.collection.view.camera.ViewCameraPitchRollYaw#addPitchY(float)
	 * @see prometheus.data.collection.view.camera.ViewCameraPitchRollYaw#setRollZ(float)
	 * @see prometheus.data.collection.view.camera.ViewCameraPitchRollYaw#addYawX(float)
	 * @param iIndex
	 *            index from [0..2] were 0..yawX, 1..pitchY and 2..rollZ
	 * @param fSetRadiant
	 *            rotation angel in radians
	 */
	public void setByIndex(final int iIndex, final float fSetRadiant) {

		switch (iIndex) {
			case 0:
				setYawX(fSetRadiant);
				return;
			case 1:
				setPitchY(fSetRadiant);
				return;
			case 2:
				setRollZ(fSetRadiant);
				return;
			default:
				throw new IndexOutOfBoundsException("index [" + Integer.toString(iIndex)
					+ "] out of bounds [0..2] ");
		}
	}

	/**
	 * Get yaw component of rotation.
	 * 
	 * @return yaw component of rotation in radians
	 */
	public float getYawX() {

		return fYawX;
	}

	/**
	 * Get pitch component of rotation.
	 * 
	 * @return pitch component of rotation in radians
	 */
	public float getPitchY() {

		return fPitchY;
	}

	/**
	 * Get roll component of rotation.
	 * 
	 * @return roll component of rotation in radians
	 */
	public float getRollZ() {

		return fRollZ;
	}

	/**
	 * Get resulting rotation from pitch, roll and yaw. Note: This value is updated with each set method.
	 * 
	 * @return current rotation calculated from pitch, roll and yaw.
	 */
	public Rotf getRotation() {

		return quadResult;
	}

	/**
	 * Set state for update behaviour.
	 * 
	 * @see prometheus.data.collection.view.camera.ViewCameraBase#getCameraMatrix()
	 * @param bImmediateUpdate
	 *            TRUE force an immediate update once a setter is called, which recalulates the ViewMatrix
	 */
	public void setImmediateCameraUpdate(final boolean bImmediateUpdate) {

		bEnableImmediateCameraUpdate = bImmediateUpdate;
	}

	/**
	 * Get state information on update behaviour.
	 * 
	 * @see prometheus.data.collection.view.camera.ViewCameraBase#getCameraMatrix()
	 * @return TURE if ViewMatrix is updated immediately
	 */
	public boolean isImmediateCameraUpdateEnabled() {

		return bEnableImmediateCameraUpdate;
	}

	/**
	 * Creates a clone of this camera by copying all settings.
	 */
	@Override
	public ViewCameraBase clone() {

		ViewCameraBase exportClone = new ViewCameraBase(uniqueID);

		exportClone.setCameraAll(v3fCameraPosition, v3fCameraScale, rotfCameraRotation);

		return exportClone;
	}

	@Override
	public String toString() {

		return "[" + super.toString() + " (" + Float.toString(fYawX) + "|" + Float.toString(fPitchY) + "|"
			+ Float.toString(fRollZ) + ")]";
	}
}
