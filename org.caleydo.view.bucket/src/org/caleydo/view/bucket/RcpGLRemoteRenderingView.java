package org.caleydo.view.bucket;

import java.util.ArrayList;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.view.base.rcp.ARcpGLViewPart;
import org.eclipse.swt.widgets.Composite;

public class RcpGLRemoteRenderingView extends ARcpGLViewPart {

	private ArrayList<Integer> iAlContainedViewIDs;

	/**
	 * Constructor.
	 */
	public RcpGLRemoteRenderingView() {
		super();

		iAlContainedViewIDs = new ArrayList<Integer>();
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		createGLCanvas();
		createGLEventListener(initSerializedView, glCanvas.getID());
	}

	@Override
	public ASerializedView createDefaultSerializedView() {
		SerializedRemoteRenderingView serializedView = new SerializedRemoteRenderingView(
				EDataDomain.GENETIC_DATA);
		return serializedView;
	}

	@Override
	public void dispose() {
		GLRemoteRendering glRemoteView = (GLRemoteRendering) GeneralManager
				.get().getViewGLCanvasManager().getGLEventListener(iViewID);

		for (Integer iContainedViewID : iAlContainedViewIDs) {
			glRemoteView.removeView(GeneralManager.get()
					.getViewGLCanvasManager().getGLEventListener(
							iContainedViewID));
		}

		super.dispose();

		GeneralManager.get().getViewGLCanvasManager()
				.getConnectedElementRepresentationManager()
				.clearByView(iViewID);

		GeneralManager.get().getPathwayManager().resetPathwayVisiblityState();
	}

	@Override
	public String getViewGUIID() {
		return GLRemoteRendering.VIEW_ID;
	}
}
