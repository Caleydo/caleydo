package org.caleydo.core.view.opengl.util.overlay.contextmenu;

/**
 * A scroll button which indicates whether there are additional elements in the context menu that are hidden
 * (either below or above)
 * 
 * @author Alexander Lex
 */
public class ScrollButton
	implements IContextMenuEntry {
	private boolean isUp = true;

	public ScrollButton(boolean isUp) {
		this.isUp = isUp;
	}

	public boolean isUp() {
		return isUp;
	}

}
