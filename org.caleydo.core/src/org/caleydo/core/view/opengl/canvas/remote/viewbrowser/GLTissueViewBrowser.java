package org.caleydo.core.view.opengl.canvas.remote.viewbrowser;

import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.tissue.SerializedTissueView;

public class GLTissueViewBrowser
	extends AGLViewBrowser {

	public GLTissueViewBrowser(GLCaleydoCanvas glCanvas, String sLabel, IViewFrustum viewFrustum) {
		super(glCanvas, sLabel, viewFrustum);
		
		viewType = EManagedObjectType.GL_TISSUE_VIEW_BROWSER;
	}

	@Override
	protected void addInitialViews() {
	
		for (int pathwayIndex = 0; pathwayIndex < 10; pathwayIndex++) {
			SerializedTissueView tissue = new SerializedTissueView();
			tissue.setDataDomain(EDataDomain.TISSUE_DATA);
			newViews.add(tissue);
		}
	}
}
