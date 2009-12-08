package org.caleydo.core.view.opengl.util.hierarchy;

import gleem.linalg.open.Transform;

import org.caleydo.core.data.AUniqueObject;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;

public class RemoteLevelElement
	extends AUniqueObject {
	private Transform transform;

	/**
	 * The remote level in which the element is contained.
	 */
	private RemoteLevel remoteLevel;

	private boolean bIsLocked = false;

	/**
	 * ID of the element that is rendered at this remote level position.
	 */
	private int iContainedElementID = -1;

	public RemoteLevelElement(RemoteLevel remoteLevel) {
		super(GeneralManager.get().getIDManager().createID(EManagedObjectType.REMOTE_LEVEL_ELEMENT));

		RemoteElementManager.get().registerItem(this);
		this.remoteLevel = remoteLevel;
	}

	public int getContainedElementID() {
		return iContainedElementID;
	}

	public void setContainedElementID(int iContainedElementID) {
		this.iContainedElementID = iContainedElementID;
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
		return iContainedElementID == -1 ? true : false;
	}

	public void lock(boolean bLock) {
		this.bIsLocked = bLock;
	}

	public boolean isLocked() {
		return bIsLocked;
	}
}
