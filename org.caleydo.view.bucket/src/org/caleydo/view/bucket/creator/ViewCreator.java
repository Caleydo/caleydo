package org.caleydo.view.bucket.creator;

import java.util.ArrayList;

import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.view.creator.AGLViewCreator;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.remote.ARemoteViewLayoutRenderStyle;
import org.caleydo.view.bucket.GLBucket;
import org.caleydo.view.bucket.SerializedBucketView;
import org.caleydo.view.bucket.toolbar.RemoteRenderingToolBarContent;

public class ViewCreator extends AGLViewCreator {

	public ViewCreator() {
		super(GLBucket.VIEW_ID);
	}

	@Override
	public AGLView createGLView(GLCaleydoCanvas glCanvas, ViewFrustum viewFrustum) {

		return new GLBucket(glCanvas, viewFrustum,
				ARemoteViewLayoutRenderStyle.LayoutMode.BUCKET);
	}

	@Override
	public ASerializedView createSerializedView() {

		return new SerializedBucketView();
	}

	@Override
	public Object createToolBarContent() {
		return new RemoteRenderingToolBarContent();
	}

	@Override
	protected void registerDataDomains() {
		ArrayList<String> dataDomainTypes = new ArrayList<String>();
		dataDomainTypes.add("org.caleydo.datadomain.genetic");

		DataDomainManager
				.getInstance()
				.getAssociationManager()
				.registerDatadomainTypeViewTypeAssociation(dataDomainTypes,
						GLBucket.VIEW_ID);
	}
}
