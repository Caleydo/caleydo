package org.caleydo.view.treemap.creator;

import java.util.ArrayList;

import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.view.creator.AGLViewCreator;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.view.treemap.GLTreeMap;
import org.caleydo.view.treemap.SerializedTreeMapView;
import org.caleydo.view.treemap.toolbar.TreeMapToolBarContent;

public class ViewCreator extends AGLViewCreator {

	public ViewCreator() {
		super(GLTreeMap.VIEW_ID);
	}

	@Override
	public AGLView createGLView(GLCaleydoCanvas glCanvas, ViewFrustum viewFrustum) {

		return new GLTreeMap(glCanvas, viewFrustum);
	}

	@Override
	public ASerializedView createSerializedView() {

		return new SerializedTreeMapView();
	}

	@Override
	public Object createToolBarContent() {
		return new TreeMapToolBarContent();
	}

	@Override
	protected void registerDataDomains() {
		ArrayList<String> dataDomainTypes = new ArrayList<String>();
		dataDomainTypes.add("org.caleydo.datadomain.genetic");
		// dataDomainTypes.add("org.caleydo.datadomain.generic");

		DataDomainManager.get().getAssociationManager().registerDatadomainTypeViewTypeAssociation(dataDomainTypes, GLTreeMap.VIEW_ID);
	}
}
