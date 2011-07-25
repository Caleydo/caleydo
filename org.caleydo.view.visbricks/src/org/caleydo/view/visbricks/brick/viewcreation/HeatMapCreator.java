package org.caleydo.view.visbricks.brick.viewcreation;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.opengl.camera.ECameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.template.BrickHeatMapTemplate;
import org.caleydo.view.visbricks.brick.GLBrick;

/**
 * Creator for a remote rendered {@link GLHeatMap}.
 * 
 * @author Christian Partl
 * 
 */
public class HeatMapCreator implements IRemoteViewCreator {
	
	private DataTable set;
	
	public HeatMapCreator(DataTable set) {
		this.set = set;
	}

	@Override
	public AGLView createRemoteView(GLBrick remoteRenderingView, GL2 gl,
			GLMouseListener glMouseListener) {

		GLHeatMap heatMap = (GLHeatMap) GeneralManager
				.get()
				.getViewGLCanvasManager()
				.createGLView(
						GLHeatMap.class,
						remoteRenderingView.getParentGLCanvas(),
						remoteRenderingView.getParentComposite(),
						new ViewFrustum(ECameraProjectionMode.ORTHOGRAPHIC, 0,
								1, 0, 1, -1, 1));

		heatMap.setRemoteRenderingGLView(remoteRenderingView);
		heatMap.setDataTable(set);
		heatMap.setDataDomain(remoteRenderingView.getDataDomain());
		heatMap.setRenderTemplate(new BrickHeatMapTemplate(heatMap));
		heatMap.initialize();
		heatMap.initRemote(gl, remoteRenderingView, glMouseListener);
		RecordVirtualArray recordVA = remoteRenderingView.getRecordVA();
		if (recordVA != null)
			heatMap.setRecordVA(recordVA);

		return heatMap;
	}

}
