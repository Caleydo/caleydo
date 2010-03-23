package org.caleydo.view.compare;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.opengl.util.texture.TextureManager;

public class SelectionWindowArrow {
	
	private SetBarSelectionWindow selectionWindow;
	private boolean isLeft;
	private TextureManager textureManager;
	

	public SelectionWindowArrow(SetBarSelectionWindow selectionWindow, boolean isLeft, TextureManager textureManager) {
		this.selectionWindow = selectionWindow;
		this.isLeft = isLeft;
		this.textureManager = textureManager;
	}
	
	public void render(GL gl) {
		
	}
}
