package org.caleydo.view.compare.rendercommand;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL;

import org.caleydo.core.data.selection.ContentGroupList;
import org.caleydo.view.compare.layout.AHeatMapLayout;
import org.caleydo.view.compare.layout.HeatMapLayoutDetailViewRight;
import org.caleydo.view.heatmap.dendrogram.GLDendrogram;
import org.caleydo.view.matchmaker.HeatMapWrapper;

public class DendrogramRenderCommand implements IHeatMapRenderCommand {

	@Override
	public ERenderCommandType getRenderCommandType() {
		return ERenderCommandType.DENDROGRAM;
	}

	@Override
	public void render(GL gl, HeatMapWrapper heatMapWrapper) {

		GLDendrogram<ContentGroupList> dendrogram = heatMapWrapper
				.getDendrogram();
		AHeatMapLayout layout = heatMapWrapper.getLayout();
		
		if(layout instanceof HeatMapLayoutDetailViewRight) {
			dendrogram.setMirrored(true);
		}

		Vec3f dendrogramPosition = layout.getDendrogramPosition();

		gl.glTranslatef(dendrogramPosition.x(), dendrogramPosition.y(),
				dendrogramPosition.z());
		dendrogram.getViewFrustum().setLeft(dendrogramPosition.x());
		dendrogram.getViewFrustum().setBottom(dendrogramPosition.y());
		dendrogram.getViewFrustum().setRight(
				dendrogramPosition.x() + layout.getDendrogramWidth());
		dendrogram.getViewFrustum().setTop(
				dendrogramPosition.y() + layout.getDendrogramHeight());
		dendrogram.displayRemote(gl);

		gl.glTranslatef(-dendrogramPosition.x(), -dendrogramPosition.y(),
				-dendrogramPosition.z());

	}

}
