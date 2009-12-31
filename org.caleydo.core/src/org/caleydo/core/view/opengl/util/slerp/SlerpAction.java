package org.caleydo.core.view.opengl.util.slerp;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
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
		this.iElementID = originRemoteLevelElement.getContainedElementID();
	}

	public void start() {
		originRemoteLevelElement.setContainedElementID(-1);
	}

	public void finished() {
		destinationRemoteLevelElement.setContainedElementID(iElementID);

		if (iElementID != -1) {
			AGLEventListener glView =
				GeneralManager.get().getViewGLCanvasManager().getGLEventListener(iElementID);
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