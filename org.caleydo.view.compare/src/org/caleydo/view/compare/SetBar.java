package org.caleydo.view.compare;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.manager.picking.PickingManager;

public class SetBar {

	private ArrayList<ISet> sets;
	private ArrayList<SetBarItem> items;
	private Vec3f position;
	private float height;
	private float width;
	private PickingManager pickingManager;
	private int viewID;
	
	public SetBar(int viewID, PickingManager pickingManager) {
		this.viewID = viewID;
		this.pickingManager = pickingManager;
		items = new ArrayList<SetBarItem>();
		sets = new ArrayList<ISet>();
	}
	
	public void render(GL gl) {
		for(SetBarItem item : items) {
			item.render(gl);
		}
	}
	
	public Vec3f getPosition() {
		return position;
	}

	public void setPosition(Vec3f position) {
		this.position = position;
		
		float currentPositionX = position.x();
		for(SetBarItem item : items) {
			item.setPosition(new Vec3f(currentPositionX, position.y(), position.z()));
			currentPositionX += item.getWidth();
		}
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
		for(SetBarItem item : items) {
			item.setHeight(height);
		}
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
		float itemWidth = width / (float) items.size();
		for(SetBarItem item : items) {
			item.setWidth(itemWidth);
		}
	}

	public void setSets(ArrayList<ISet> sets) {
		this.sets.clear();
		this.sets.addAll(sets);
		items.clear();
		
		float itemWidth = width / (float) sets.size();
		int itemID = 0;
		float currentPositionX = position.x();
		
		for(ISet set : sets) {
			SetBarItem item = new SetBarItem(itemID, viewID, pickingManager);
			item.setSet(set);
			item.setHeight(height);
			item.setWidth(itemWidth);
			item.setPosition(new Vec3f(currentPositionX, position.y(), position.z()));
			currentPositionX += item.getWidth();
			itemID++;
		}
	}
}
