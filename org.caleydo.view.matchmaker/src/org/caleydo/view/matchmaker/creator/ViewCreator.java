package org.caleydo.view.matchmaker.creator;

import java.util.ArrayList;

import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.view.creator.AGLViewCreator;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.view.matchmaker.GLMatchmaker;
import org.caleydo.view.matchmaker.SerializedMatchmakerView;
import org.caleydo.view.matchmaker.toolbar.MatchmakerToolBarContent;

public class ViewCreator extends AGLViewCreator {

	public ViewCreator() {
		super(GLMatchmaker.VIEW_ID);
	}

	@Override
	public AGLView createGLView(GLCaleydoCanvas glCanvas, ViewFrustum viewFrustum) {

		return new GLMatchmaker(glCanvas, viewFrustum);
	}

	@Override
	public ASerializedView createSerializedView() {

		return new SerializedMatchmakerView();
	}

	@Override
	public Object createToolBarContent() {
		return new MatchmakerToolBarContent();
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
						GLMatchmaker.VIEW_ID);
	}
}
