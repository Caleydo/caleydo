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
import org.caleydo.view.tagclouds.GLTagCloud;
import org.caleydo.view.visbricks.brick.GLBrick;

public class TagCloudCreator implements IRemoteViewCreator {
	
	private DataTable set;

	public TagCloudCreator(DataTable set) {
		this.set = set;
	}

	@Override
	public AGLView createRemoteView(GLBrick remoteRenderingView, GL2 gl,
			GLMouseListener glMouseListener) {
		GLTagCloud tagCloud = (GLTagCloud) GeneralManager
				.get()
				.getViewManager()
				.createGLView(
						GLTagCloud.class,
						remoteRenderingView.getParentGLCanvas(),
						remoteRenderingView.getParentComposite(),
						new ViewFrustum(ECameraProjectionMode.ORTHOGRAPHIC, 0,
								1, 0, 1, -1, 1));

		tagCloud.setRemoteRenderingGLView(remoteRenderingView);
		tagCloud.setTable(set);
		tagCloud.setDataDomain(remoteRenderingView.getDataDomain());
		tagCloud.initialize();
		tagCloud.initRemote(gl, remoteRenderingView, glMouseListener);
		tagCloud.setDetailLevel(DetailLevel.LOW);
		RecordVirtualArray recordVA = remoteRenderingView.getRecordVA();
		if (recordVA != null)
			tagCloud.setRecordVA(recordVA);
		return tagCloud;
	}

}
