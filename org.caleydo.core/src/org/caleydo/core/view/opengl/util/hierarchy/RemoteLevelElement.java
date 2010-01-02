package org.caleydo.core.view.opengl.util.hierarchy;

import gleem.linalg.open.Transform;

import org.caleydo.core.data.AUniqueObject;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.view.opengl.canvas.AGLView;

public class RemoteLevelElement
	extends AUniqueObject {
	private Transform transform;

	/**
	 * The remote level in which the element is contained.
	 */
	private RemoteLevel remoteLevel;

	private boolean bIsLocked = false;

	private AGLView glView;

	public RemoteLevelElement(RemoteLevel remoteLevel) {
		super(GeneralManager.get().getIDManager().createID(EManagedObjectType.REMOTE_LEVEL_ELEMENT));

		RemoteElementManager.get().registerItem(this);
		this.remoteLevel = remoteLevel;
	}

	public AGLView getGLView() {
		return glView;
	}

	public void setGLView(AGLView glView) {
		this.glView = glView;
	}

	public RemoteLevel getRemoteLevel() {
		return remoteLevel;
	}

	public Transform getTransform() {
		return transform;
	}

	public void setTransform(Transform transform) {
		this.transform = transform;
	}

	public boolean isFree() {
		return glView == null ? true : false;
	}

	public void lock(boolean bLock) {
		this.bIsLocked = bLock;
	}

	public boolean isLocked() {
		return bIsLocked;
	}
}
