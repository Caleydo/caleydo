package org.caleydo.core.view.opengl.util.overlay.contextmenu;

/**
 * Implementation of IContextMenuEntry for a heading. A heading is displayed, but not clickable and has no
 * icon.
 * 
 * @author Alexander Lex
 */
public class Heading
	implements IContextMenuEntry {

	private String text;

	/**
	 * Construct a heading by setting its text
	 * @param text
	 */
	public Heading(String text) {
		this.text = text;
	}

	/**
	 * Returns the text of the heading
	 * @return the text of the heading
	 */
	public String getText() {
		return text;
	}

}
