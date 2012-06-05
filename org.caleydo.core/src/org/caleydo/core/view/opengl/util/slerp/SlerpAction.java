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
package org.caleydo.core.view.opengl.util.slerp;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;

/**
 * Slerp action in 3D scene.
 * 
 * @author Marc Streit
 */
public class SlerpAction {
	private int iElementID = -1;

	private RemoteLevelElement originRemoteLevelElement;

	private RemoteLevelElement destinationRemoteLevelElement;

	/**
	 * Constructor.
	 */
	public SlerpAction(int iElementID, RemoteLevelElement originRemoteLevelElement,
		RemoteLevelElement destinationRemoteLevelElement) {
		this.originRemoteLevelElement = originRemoteLevelElement;
		this.destinationRemoteLevelElement = destinationRemoteLevelElement;
		this.iElementID = iElementID;
	}

	/**
	 * Constructor.
	 */
	public SlerpAction(RemoteLevelElement originRemoteLevelElement,
		RemoteLevelElement destinationRemoteLevelElement) {
		this.originRemoteLevelElement = originRemoteLevelElement;
		this.destinationRemoteLevelElement = destinationRemoteLevelElement;

		if (originRemoteLevelElement.getGLView() == null)
			this.iElementID = -1;
		else
			this.iElementID = originRemoteLevelElement.getGLView().getID();
	}

	public void start() {
		originRemoteLevelElement.setGLView(null);
	}

	public void finished() {
		destinationRemoteLevelElement.setGLView(GeneralManager.get().getViewManager().getGLView(iElementID));

		if (iElementID != -1) {
			AGLView glView = GeneralManager.get().getViewManager().getGLView(iElementID);
			if (glView != null)
				glView.setRemoteLevelElement(destinationRemoteLevelElement);
		}
	}

	public int getElementId() {
		return iElementID;
	}

	public RemoteLevelElement getOriginRemoteLevelElement() {
		if (originRemoteLevelElement == null)
			throw new IllegalStateException("Slerp origin layer is null!");

		return originRemoteLevelElement;
	}

	public RemoteLevelElement getDestinationRemoteLevelElement() {
		if (destinationRemoteLevelElement == null)
			throw new IllegalStateException("Slerp destination layer is null!");

		return destinationRemoteLevelElement;
	}
}