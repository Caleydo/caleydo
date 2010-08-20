package org.caleydo.view.heatmap.creator;

import java.util.ArrayList;

import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.view.creator.AGLViewCreator;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.view.heatmap.dendrogram.GLDendrogram;
import org.caleydo.view.heatmap.dendrogram.SerializedDendogramHorizontalView;

public class ViewCreatorDendrogramHorizontal extends AGLViewCreator {

	public ViewCreatorDendrogramHorizontal() {
		super(GLDendrogram.VIEW_ID +".horizontal");
	}

	@Override
	public AGLView createGLView(GLCaleydoCanvas glCanvas, String label,
			IViewFrustum viewFrustum) {

		return new GLDendrogram(glCanvas, label, viewFrustum, true);
	}

	@Override
	public ASerializedView createSerializedView() {

		return new SerializedDendogramHorizontalView();
	}
	
	@Override
	protected void registerDataDomains() {
		ArrayList<String> dataDomainTypes = new ArrayList<String>();

		dataDomainTypes.add("org.caleydo.datadomain.genetic");
		dataDomainTypes.add("org.caleydo.datadomain.generic");

		DataDomainManager.getInstance().getAssociationManager()
				.registerDatadomainTypeViewTypeAssociation(dataDomainTypes, GLDendrogram.VIEW_ID);
	}
}
