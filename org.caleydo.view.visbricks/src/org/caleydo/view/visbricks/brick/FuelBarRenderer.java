package org.caleydo.view.visbricks.brick;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;

public class FuelBarRenderer extends LayoutRenderer {

	private static final float SIDE_SPACING_PORTION = 0.2f;

	private GLBrick brick;
	private SelectionManager selectionManager;

	public FuelBarRenderer(GLBrick brick) {
		this.brick = brick;
		selectionManager = brick.getContentGroupSelectionManager();
	}

	@Override
	public void render(GL2 gl) {
		// FIXME: this should not be necessary, since fuel bars are only for
		// those view that actually contain a group
		if (brick.getGroup() == null)
			return;

		ISet set = brick.getSet();
		ContentVirtualArray contentVA = brick.getContentVA();

		if (set == null || contentVA == null)
			return;

		ContentVirtualArray setContentVA = set.getContentData(Set.CONTENT).getContentVA();

		if (setContentVA == null)
			return;

		int totalNumElements = setContentVA.size();

		int currentNumElements = contentVA.size();

		float fuelWidth = (float) x / totalNumElements * currentNumElements;

		// gl.glColor3f(0.5f, 0.5f, 0.5f);
		// gl.glBegin(GL2.GL_QUADS);
		// gl.glVertex3f(0, 0, 0);
		// gl.glVertex3f(x, 0, 0);
		// gl.glVertex3f(x, y, 0);
		// gl.glVertex3f(0, y, 0);
		gl.glPushName(brick.getPickingManager().getPickingID(brick.getID(),
				EPickingType.BRICK, brick.getID()));

		gl.glBegin(GL2.GL_QUADS);
		if (selectionManager.checkStatus(SelectionType.SELECTION, brick.getGroup()
				.getID()))
			gl.glColor3f(1, 0, 0);
		else
			gl.glColor3f(0.3f, 0.3f, 0.3f);

		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(x, 0, 0);
		gl.glColor3f(0.1f, 0.1f, 0.1f);
		gl.glVertex3f(x, y, 0);
		gl.glVertex3f(0, y, 0);

		gl.glColor3f(0, 0, 0);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(fuelWidth, 0, 0);
		gl.glColor3f(1, 1f, 1);
		gl.glVertex3f(fuelWidth, y, 0);
		gl.glVertex3f(0, y, 0);
		gl.glEnd();
		gl.glPopName();

//		brick.addPickingListener(new APickingListener() {
//
//			@Override
//			public void clicked(Pick pick) {
//				// set.cluster(clusterState);
//				System.out.println("picked brick");
//
//				selectionManager.clearSelection(SelectionType.SELECTION);
//				selectionManager.addToType(SelectionType.SELECTION, brick.getGroup()
//						.getID());
//
//				SelectionUpdateEvent event = new SelectionUpdateEvent();
//				event.setDataDomainType(brick.getDataDomain().getDataDomainType());
//				event.setSender(brick);
//				SelectionDelta delta = selectionManager.getDelta();
//				event.setSelectionDelta(delta);
//				GeneralManager.get().getEventPublisher().triggerEvent(event);
//
//			}
//		}, EPickingType.BRICK, brick.getID());

	}
}
