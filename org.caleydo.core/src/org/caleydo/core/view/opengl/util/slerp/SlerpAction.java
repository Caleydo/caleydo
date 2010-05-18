package org.caleydo.core.view.opengl.util.slerp;

import org.caleydo.core.manager.general.GeneralManager;
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
		destinationRemoteLevelElement.setGLView(GeneralManager.get().getViewGLCanvasManager().getGLView(
			iElementID));

		if (iElementID != -1) {
			AGLView glView = GeneralManager.get().getViewGLCanvasManager().getGLView(iElementID);
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