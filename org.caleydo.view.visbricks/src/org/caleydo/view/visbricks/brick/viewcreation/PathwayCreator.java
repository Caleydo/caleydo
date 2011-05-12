package org.caleydo.view.visbricks.brick.viewcreation;

import java.util.Collection;

import javax.media.opengl.GL2;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.view.opengl.camera.ECameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.view.pathway.GLPathway;
import org.caleydo.view.pathway.SerializedPathwayView;
import org.caleydo.view.visbricks.brick.GLBrick;

public class PathwayCreator implements IRemoteViewCreator {

	@Override
	public AGLView createRemoteView(GLBrick remoteRenderingView, GL2 gl,
			GLMouseListener glMouseListener) {
		
		Collection<PathwayGraph> pathways = PathwayManager.get().getAllItems();
		
		//FIXME: just for testing
		PathwayGraph firstPathway = null;
		for(PathwayGraph pathway : pathways) {
			firstPathway = pathway;
			break;
		}
		
		SerializedPathwayView serPathway = new SerializedPathwayView();
		serPathway.setDataDomainType("org.caleydo.datadomain.pathway");
		serPathway.setPathwayID(firstPathway.getID());
		
		GLPathway pathway = (GLPathway) GeneralManager
				.get()
				.getViewGLCanvasManager()
				.createGLView(
						GLPathway.class,
						remoteRenderingView.getParentGLCanvas(),

						new ViewFrustum(ECameraProjectionMode.ORTHOGRAPHIC, 0,
								1, 0, 1, -1, 1));

		pathway.setPathway(firstPathway);
		pathway.setDataDomain((PathwayDataDomain)(DataDomainManager
				.get().getDataDomain(serPathway.getDataDomainType())));
		pathway.enablePathwayTextures(true);
		pathway.enableNeighborhood(true);
		pathway.enableGeneMapping(true);
		pathway.setRemoteRenderingGLView(remoteRenderingView);
//		pathway.setSet(remoteRenderingView.getSet());
//		parCoords.setDataDomain(remoteRenderingView.getDataDomain());
		pathway.initialize();
		pathway.initRemote(gl, remoteRenderingView, glMouseListener);
//		parCoords.setDetailLevel(DetailLevel.LOW);
//		ContentVirtualArray contentVA = remoteRenderingView.getContentVA();
//		if (contentVA != null)
//			parCoords.setContentVA(contentVA);

		return pathway;
	}

}
