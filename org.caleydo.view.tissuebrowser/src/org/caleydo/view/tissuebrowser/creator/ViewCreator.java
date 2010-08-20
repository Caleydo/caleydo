package org.caleydo.view.tissuebrowser.creator;

import java.util.ArrayList;

import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.view.creator.AGLViewCreator;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.view.tissuebrowser.GLTissueViewBrowser;
import org.caleydo.view.tissuebrowser.SerializedTissueViewBrowserView;

public class ViewCreator extends AGLViewCreator {

	public ViewCreator() {
		super(GLTissueViewBrowser.VIEW_ID);
	}

	@Override
	public AGLView createGLView(GLCaleydoCanvas glCanvas, String label,
			IViewFrustum viewFrustum) {

		return new GLTissueViewBrowser(glCanvas, label, viewFrustum);
	}

	@Override
	public ASerializedView createSerializedView() {

		return new SerializedTissueViewBrowserView();
	}

	@Override
	protected void registerDataDomains() {
		ArrayList<String> dataDomainTypes = new ArrayList<String>();
		dataDomainTypes.add("org.caleydo.datadomain.tissue");

		DataDomainManager
				.getInstance()
				.getAssociationManager()
				.registerDatadomainTypeViewTypeAssociation(dataDomainTypes,
						GLTissueViewBrowser.VIEW_ID);
	}
}