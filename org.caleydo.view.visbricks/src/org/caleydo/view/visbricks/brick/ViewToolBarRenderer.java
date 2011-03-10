package org.caleydo.view.visbricks.brick;

import javax.media.opengl.GL2;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.data.StartClusteringEvent;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.rcp.dialog.cluster.StartClusteringDialog;
import org.eclipse.swt.widgets.Shell;

public class ViewToolBarRenderer extends LayoutRenderer {

	private static final int CLUSTER_BUTTON_ID = 1;
	private static final int HEATMAP_BUTTON_ID = 2;
	private static final int PARCOORDS_BUTTON_ID = 3;
	private static final int HISTOGRAM_BUTTON_ID = 4;

	GLBrick brick;

	private boolean pickingListenersRegistered;

	public ViewToolBarRenderer(GLBrick brick) {
		this.brick = brick;
		pickingListenersRegistered = false;

	}

	@Override
	public void render(GL2 gl) {

		if (!pickingListenersRegistered)
			registerPickingListeners();

		float buttonSpacing = 0.05f * x;

		gl.glPushName(brick.getPickingManager().getPickingID(brick.getID(),
				EPickingType.BRICK_TOOLBAR_BUTTONS, CLUSTER_BUTTON_ID));
		gl.glColor3f(1f, 0, 0);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(y, 0, 0);
		gl.glVertex3f(y, y, 0);
		gl.glVertex3f(0, y, 0);
		gl.glEnd();
		gl.glPopName();

		gl.glPushName(brick.getPickingManager().getPickingID(brick.getID(),
				EPickingType.BRICK_TOOLBAR_BUTTONS, HEATMAP_BUTTON_ID));
		gl.glColor3f(0, 1, 0);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(y + buttonSpacing, 0, 0);
		gl.glVertex3f(2 * y + buttonSpacing, 0, 0);
		gl.glVertex3f(2 * y + buttonSpacing, y, 0);
		gl.glVertex3f(y + buttonSpacing, y, 0);
		gl.glEnd();
		gl.glPopName();

		gl.glPushName(brick.getPickingManager().getPickingID(brick.getID(),
				EPickingType.BRICK_TOOLBAR_BUTTONS, PARCOORDS_BUTTON_ID));
		gl.glColor3f(0, 0, 1);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(2 * y + 2 * buttonSpacing, 0, 0);
		gl.glVertex3f(3 * y + 2 * buttonSpacing, 0, 0);
		gl.glVertex3f(3 * y + 2 * buttonSpacing, y, 0);
		gl.glVertex3f(2 * y + 2 * buttonSpacing, y, 0);
		gl.glEnd();
		gl.glPopName();
		
		gl.glPushName(brick.getPickingManager().getPickingID(brick.getID(),
				EPickingType.BRICK_TOOLBAR_BUTTONS, HISTOGRAM_BUTTON_ID));
		gl.glColor3f(0, 1, 1);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(3 * y + 3 * buttonSpacing, 0, 0);
		gl.glVertex3f(4 * y + 3 * buttonSpacing, 0, 0);
		gl.glVertex3f(4 * y + 3 * buttonSpacing, y, 0);
		gl.glVertex3f(3 * y + 3 * buttonSpacing, y, 0);
		gl.glEnd();
		gl.glPopName();
	}

	private void registerPickingListeners() {
		brick.addPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				// set.cluster(clusterState);
				System.out.println("cluster");

				brick.getParentGLCanvas().getParentComposite().getDisplay()
						.asyncExec(new Runnable() {
							@Override
							public void run() {
								StartClusteringDialog dialog = new StartClusteringDialog(
										new Shell(), brick.getDataDomain());
								dialog.open();
								ClusterState clusterState = dialog
										.getClusterState();

								StartClusteringEvent event = null;
								// if (clusterState != null && set != null)

								event = new StartClusteringEvent(clusterState,
										brick.getSet().getID());
								event.setDataDomainType(brick.getDataDomain()
										.getDataDomainType());
								GeneralManager.get().getEventPublisher()
										.triggerEvent(event);
							}
						});

			}
		}, EPickingType.BRICK_TOOLBAR_BUTTONS, CLUSTER_BUTTON_ID);

		brick.addPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				brick.setRemoteView(GLBrick.HEATMAP_VIEW);
			}
		}, EPickingType.BRICK_TOOLBAR_BUTTONS, HEATMAP_BUTTON_ID);

		brick.addPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				brick.setRemoteView(GLBrick.PARCOORDS_VIEW);
			}
		}, EPickingType.BRICK_TOOLBAR_BUTTONS, PARCOORDS_BUTTON_ID);
		
		brick.addPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				brick.setRemoteView(GLBrick.HISTOGRAM_VIEW);
			}
		}, EPickingType.BRICK_TOOLBAR_BUTTONS, HISTOGRAM_BUTTON_ID);

		pickingListenersRegistered = true;
	}
}
