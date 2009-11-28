package org.caleydo.core.view.opengl.canvas.remote.viewbrowser;

import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.pathway.SerializedPathwayView;

public class GLPathwayViewBrowser
	extends AGLViewBrowser {

	public GLPathwayViewBrowser(GLCaleydoCanvas glCanvas, String sLabel, IViewFrustum viewFrustum) {
		super(glCanvas, sLabel, viewFrustum);
	
		viewType = EManagedObjectType.GL_PATHWAY_VIEW_BROWSER;
	}

	@Override
	protected void addInitialViews() {

		for (int pathwayIndex = 0; pathwayIndex < 10; pathwayIndex++) {
			SerializedPathwayView pathway = new SerializedPathwayView();
			pathway.setPathwayID(((PathwayGraph) GeneralManager.get().getPathwayManager().getAllItems()
				.toArray()[pathwayIndex]).getID());
			pathway.setDataDomain(EDataDomain.PATHWAY_DATA);
			newViews.add(pathway);
		}
	}
}
