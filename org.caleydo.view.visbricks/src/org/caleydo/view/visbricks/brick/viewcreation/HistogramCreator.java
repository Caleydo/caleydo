package org.caleydo.view.visbricks.brick.viewcreation;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.opengl.camera.ECameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.DetailLevel;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.histogram.GLHistogram;
import org.caleydo.view.visbricks.brick.GLBrick;

/**
 * Creator for a remote rendered {@link GLHistogram}.
 * 
 * @author Christian Partl
 * 
 */
public class HistogramCreator implements IRemoteViewCreator {

	@Override
	public AGLView createRemoteView(GLBrick remoteRenderingView, GL2 gl,
			GLMouseListener glMouseListener) {

		GLHistogram histogram = (GLHistogram) GeneralManager
		.get()
		.getViewGLCanvasManager()
		.createGLView(
				GLHistogram.class,
				remoteRenderingView.getParentGLCanvas(),

				new ViewFrustum(ECameraProjectionMode.ORTHOGRAPHIC, 0,
						1, 0, 1, -1, 1));

		histogram.setRemoteRenderingGLView(remoteRenderingView);
		ISet set = remoteRenderingView.getSet();
		ContentVirtualArray contentVA = remoteRenderingView.getContentVA();
		histogram.setHistogram(set.getHistogram(contentVA));
		histogram.setDataDomain(remoteRenderingView.getDataDomain());
		histogram.initialize();
		histogram.initRemote(gl, remoteRenderingView, glMouseListener);
		histogram.setDetailLevel(DetailLevel.LOW);
		
//		Cset.getContentData(Set.CONTENT)
//		if (contentVA != null)
//			histogram.setContentVA(contentVA);
		
		return histogram;
	}

}
