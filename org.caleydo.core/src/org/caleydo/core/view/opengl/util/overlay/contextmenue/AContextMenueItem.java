package org.caleydo.core.view.opengl.util.overlay.contextmenue;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.view.remote.LoadPathwayEvent;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;

public abstract class AContextMenueItem {
	private String text;
	private EIconTextures iconTexture;
	private AEvent event;

	public void setText(String text) {
		this.text = text;
	}

	public void setIconTexture(EIconTextures iconTexture) {
		this.iconTexture = iconTexture;
	}

	public void registerEvent(AEvent event) {
		this.event = event;
	}

	public String getText() {
		return text;
	}

	public EIconTextures getIconTexture() {
		return iconTexture;
	}

}
