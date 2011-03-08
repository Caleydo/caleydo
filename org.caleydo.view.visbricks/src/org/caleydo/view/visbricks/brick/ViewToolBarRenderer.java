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

	GLBrick brick;

	public ViewToolBarRenderer(GLBrick brick) {
		this.brick = brick;

	}

	@Override
	public void render(GL2 gl) {
		gl.glPushName(brick.getPickingManager().getPickingID(brick.getID(),
				EPickingType.BRICK_CLUSTER, 1));
		gl.glColor3f(1f, 0, 0);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(y, 0, 0);
		gl.glVertex3f(y, y, 0);
		gl.glVertex3f(0, y, 0);
		gl.glEnd();
		gl.glPopName();

		brick.addPickingListener(new IPickingListener() {

			@Override
			public void rightClicked(Pick pick) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseOver(Pick pick) {
				// TODO Auto-generated method stub

			}

			@Override
			public void dragged(Pick pick) {
				// TODO Auto-generated method stub

			}

			@Override
			public void doubleClicked(Pick pick) {
				// TODO Auto-generated method stub

			}

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
		}, EPickingType.BRICK_CLUSTER, 1);
	}
}
