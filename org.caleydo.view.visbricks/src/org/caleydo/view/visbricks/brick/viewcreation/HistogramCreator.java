package org.caleydo.view.visbricks.brick.viewcreation;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.opengl.camera.ECameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.DetailLevel;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.view.histogram.GLHistogram;
import org.caleydo.view.visbricks.brick.GLBrick;

/**
 * Creator for a remote rendered {@link GLHistogram}.
 * 
 * @author Christian Partl
 * 
 */
public class HistogramCreator implements IRemoteViewCreator {

	private DataTable dataTable;

	public HistogramCreator(DataTable dataTable) {
		this.dataTable = dataTable;
	}

	@Override
	public AGLView createRemoteView(GLBrick remoteRenderingView, GL2 gl,
			GLMouseListener glMouseListener) {

		GLHistogram histogram = (GLHistogram) GeneralManager
				.get()
				.getViewGLCanvasManager()
				.createGLView(
						GLHistogram.class,
						remoteRenderingView.getParentGLCanvas(),
						remoteRenderingView.getParentComposite(),
						new ViewFrustum(ECameraProjectionMode.ORTHOGRAPHIC, 0,
								1, 0, 1, -1, 1));

		histogram.setRemoteRenderingGLView(remoteRenderingView);
		RecordVirtualArray recordVA = remoteRenderingView.getRecordVA();
		histogram.setHistogram(dataTable.getMetaData().getHistogram(recordVA));
		histogram.setDataDomain(remoteRenderingView.getDataDomain());
		histogram.initialize();
		histogram.initRemote(gl, remoteRenderingView, glMouseListener);
		histogram.setDetailLevel(DetailLevel.LOW);

		// CdataTable.getContentData(Set.CONTENT)
		// if (recordVA != null)
		// histogram.setRecordVA(recordVA);

		return histogram;
	}

}
