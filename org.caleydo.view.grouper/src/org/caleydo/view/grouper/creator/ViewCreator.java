package org.caleydo.view.grouper.creator;

import java.util.ArrayList;

import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.view.creator.AGLViewCreator;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.view.grouper.GLGrouper;
import org.caleydo.view.grouper.SerializedGrouperView;

public class ViewCreator extends AGLViewCreator {

	public ViewCreator() {
		super(GLGrouper.VIEW_ID);
	}

	@Override
	public AGLView createGLView(GLCaleydoCanvas glCanvas, ViewFrustum viewFrustum) {

		return new GLGrouper(glCanvas, viewFrustum);
	}

	@Override
	public ASerializedView createSerializedView() {

		return new SerializedGrouperView();
	}

	@Override
	protected void registerDataDomains() {
		ArrayList<String> dataDomainTypes = new ArrayList<String>();
		dataDomainTypes.add("org.caleydo.datadomain.genetic");
		dataDomainTypes.add("org.caleydo.datadomain.generic");

		DataDomainManager
				.get()
				.getAssociationManager()
				.registerDatadomainTypeViewTypeAssociation(dataDomainTypes,
						GLGrouper.VIEW_ID);
	}
}
