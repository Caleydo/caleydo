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
package org.caleydo.core.view.opengl.util.hierarchy;

import gleem.linalg.open.Transform;
import org.caleydo.core.id.object.ManagedObjectType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.base.AUniqueObject;
import org.caleydo.core.view.IDataDomainBasedView;
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
