package org.caleydo.view.bucket;

import java.util.ArrayList;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.view.base.rcp.ARcpGLViewPart;
import org.eclipse.swt.widgets.Composite;

public class RcpGLBucketView extends ARcpGLViewPart {

	private ArrayList<Integer> iAlContainedViewIDs;

	/**
	 * Constructor.
	 */
	public RcpGLBucketView() {
		super();

		iAlContainedViewIDs = new ArrayList<Integer>();
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		createGLCanvas();
		createGLView(initSerializedView, glCanvas.getID());
	}

	@Override
	public ASerializedView createDefaultSerializedView() {
		SerializedRemoteRenderingView serializedView = new SerializedRemoteRenderingView(
				EDataDomain.GENETIC_DATA);
		return serializedView;
	}

	@Override
	public void dispose() {
		GLBucket glRemoteView = (GLBucket) GeneralManager.get()
				.getViewGLCanvasManager().getGLView(view.getID());

		for (Integer iContainedViewID : iAlContainedViewIDs) {
			glRemoteView.removeView(GeneralManager.get()
					.getViewGLCanvasManager().getGLView(
							iContainedViewID));
		}

		super.dispose();

		GeneralManager.get().getViewGLCanvasManager()
				.getConnectedElementRepresentationManager()
				.clearByView(view.getID());

		GeneralManager.get().getPathwayManager().resetPathwayVisiblityState();
	}

	@Override
	public String getViewGUIID() {
		return GLBucket.VIEW_ID;
	}
}
