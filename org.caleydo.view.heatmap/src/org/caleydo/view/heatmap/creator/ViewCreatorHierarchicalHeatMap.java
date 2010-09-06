package org.caleydo.view.heatmap.creator;

import java.util.ArrayList;

import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.view.creator.AGLViewCreator;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.view.heatmap.hierarchical.GLHierarchicalHeatMap;
import org.caleydo.view.heatmap.hierarchical.SerializedHierarchicalHeatMapView;
import org.caleydo.view.heatmap.toolbar.HierarchicalHeatMapToolBarContent;

public class ViewCreatorHierarchicalHeatMap extends AGLViewCreator {

	public ViewCreatorHierarchicalHeatMap() {
		super(GLHierarchicalHeatMap.VIEW_ID);
	}

	@Override
	public AGLView createGLView(GLCaleydoCanvas glCanvas, ViewFrustum viewFrustum) {

		return new GLHierarchicalHeatMap(glCanvas, viewFrustum);
	}

	@Override
	public ASerializedView createSerializedView() {

		return new SerializedHierarchicalHeatMapView();
	}

	@Override
	public Object createToolBarContent() {
		return new HierarchicalHeatMapToolBarContent();
	}

	@Override
	protected void registerDataDomains() {
		ArrayList<String> dataDomainTypes = new ArrayList<String>();

		dataDomainTypes.add("org.caleydo.datadomain.genetic");
		dataDomainTypes.add("org.caleydo.datadomain.generic");

		DataDomainManager
				.getInstance()
				.getAssociationManager()
				.registerDatadomainTypeViewTypeAssociation(dataDomainTypes,
						GLHierarchicalHeatMap.VIEW_ID);
	}
}
