package org.caleydo.core.view.opengl.util.hierarchy;

import gleem.linalg.open.Transform;

import org.caleydo.core.data.AUniqueObject;
import org.caleydo.core.manager.IDataDomain;
import org.caleydo.core.manager.datadomain.IDataDomainBasedView;
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

	/**
	 * the view held in the remote level element, the same view with a different type is also stored in
	 * {@link #dataDomainBasedView}
	 */
	private AGLView glView = null;
	/** this is the same as {@link #glView}, only the type is different */
	private IDataDomainBasedView<?> dataDomainBasedView = null;

	public RemoteLevelElement(RemoteLevel remoteLevel) {
		super(GeneralManager.get().getIDManager().createID(EManagedObjectType.REMOTE_LEVEL_ELEMENT));

		RemoteElementManager.get().registerItem(this);
		this.remoteLevel = remoteLevel;
	}

	public AGLView getGLView() {
		return glView;
	}

	public IDataDomainBasedView<?> getDataDomainBasedView() {
		return dataDomainBasedView;
	}

	/**
	 * glView, if not null (which is legal), has to be an instance of AGLView as well as of
	 * {@link IDataDomainBasedView}. While the AGLView is checked at compile time, the IDataDomainBasedView is
	 * checked at run-time, and an {@link IllegalArgumentException} is thrown if the view is no
	 * IDataDomainBasedView
	 * 
	 * @param glView
	 */
	public void setGLView(AGLView glView) {
		if (glView == null)
			return;
		if (!(glView instanceof IDataDomainBasedView<?>))
			throw new IllegalArgumentException(glView
				+ " is not of the type IDataDomainBasedView which is required for remote level elements.");
		this.glView = glView;
		this.dataDomainBasedView = (IDataDomainBasedView<?>) glView;
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
