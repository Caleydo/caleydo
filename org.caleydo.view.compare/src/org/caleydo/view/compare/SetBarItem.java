package org.caleydo.view.compare;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;

public class SetBarItem {
	private ISet set;
	private Vec3f position;
	private float height;
	private float width;
	private PickingManager pickingManager;
	private int viewID;
	private int id;
	
	public SetBarItem(int id, int viewID, PickingManager pickingManager) {
		this.id = id;
		this.viewID = viewID;
		this.pickingManager = pickingManager;
	}

	public void render(GL gl) {

		gl.glColor4f(0.5f, 0.5f, 0.5f, 1.0f);
		gl.glPushName(pickingManager.getPickingID(viewID,
				EPickingType.COMPARE_SET_BAR_ITEM_SELECTION, id));
		gl.glBegin(GL.GL_QUADS);
		gl.glVertex3f(position.x(), position.y(), 0.0f);
		gl.glVertex3f(position.x() + width, position.y(), 0.0f);
		gl.glVertex3f(position.x() + width, position.y() + height, 0.0f);
		gl.glVertex3f(position.x(), position.y() + height, 0.0f);
		gl.glEnd();
		gl.glPopName();
	}

	public ISet getSet() {
		return set;
	}

	public void setSet(ISet set) {
		this.set = set;
	}

	public Vec3f getPosition() {
		return position;
	}

	public void setPosition(Vec3f position) {
		this.position = position;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

}
