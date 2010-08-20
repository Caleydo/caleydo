package org.caleydo.view.template.creator;

import java.util.ArrayList;

import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.view.creator.AGLViewCreator;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.view.template.GLTemplate;
import org.caleydo.view.template.SerializedTemplateView;
import org.caleydo.view.template.toolbar.TemplateToolBarContent;

public class ViewCreator extends AGLViewCreator {

	public ViewCreator() {
		super(GLTemplate.VIEW_ID);
	}

	@Override
	public AGLView createGLView(GLCaleydoCanvas glCanvas, String label,
			IViewFrustum viewFrustum) {

		return new GLTemplate(glCanvas, label, viewFrustum);
	}

	@Override
	public ASerializedView createSerializedView() {

		return new SerializedTemplateView();
	}

	@Override
	public Object createToolBarContent() {
		return new TemplateToolBarContent();
	}

	@Override
	protected void registerDataDomains() {
		ArrayList<String> dataDomainTypes = new ArrayList<String>();

		// TODO: ADD THE POSSIBLE DATA DOMAINS FOR THIS VIEW
		// dataDomainTypes.add("org.caleydo.datadomain.genetic");

		DataDomainManager.getInstance().getAssociationManager()
				.registerDatadomainTypeViewTypeAssociation(dataDomainTypes, GLTemplate.VIEW_ID);
	}
}
