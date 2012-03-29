package org.caleydo.core.view.opengl.util.hierarchy;

import gleem.linalg.open.Transform;
import org.caleydo.core.data.AUniqueObject;
import org.caleydo.core.data.datadomain.IDataDomainBasedView;
import org.caleydo.core.data.id.ManagedObjectType;
import org.caleydo.core.manager.GeneralManager;
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
		super(GeneralManager.get().getIDCreator().createID(ManagedObjectType.REMOTE_LEVEL_ELEMENT));

		RemoteElementManager.get().registerItem(this);
		this.remoteLevel = remoteLevel;
	}

	public AGLView getGLView() {
		return glView;
	}

	/**
	 * glView, if not null (which is legal),can be an instance of AGLView as well as of
	 * {@link IDataDomainBasedView}. While the AGLView is checked at compile time, the IDataDomainBasedView is
	 * checked at run-time, and only if the view is of the type IDataDomainBasedView it is stored and
	 * accessible as such
	 * 
	 * @param glView
	 */
	public void setGLView(AGLView glView) {

		if (glView instanceof IDataDomainBasedView<?>)
			this.dataDomainBasedView = (IDataDomainBasedView<?>) glView;
		this.glView = glView;

	}

	public IDataDomainBasedView<?> getDataDomainBasedView() {
		return dataDomainBasedView;
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
