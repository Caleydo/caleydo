package org.caleydo.view.datagraph.contextmenu;

import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.view.datagraph.event.OpenViewEvent;

public class OpenViewItem extends AContextMenuItem {

	public OpenViewItem(AGLView view) {

		setLabel("Open View");

		// ARcpGLViewPart viewPart = GeneralManager.get().getViewManager()
		// .getViewPartFromView(view);

		OpenViewEvent event = new OpenViewEvent(view);
		event.setSender(this);
		registerEvent(event);
	}
}
