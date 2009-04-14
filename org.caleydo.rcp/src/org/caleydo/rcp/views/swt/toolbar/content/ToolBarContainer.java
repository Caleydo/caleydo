package org.caleydo.rcp.views.swt.toolbar.content;

import java.util.ArrayList;

import org.eclipse.jface.action.IAction;

/**
 * Holds a ordered group of tool-bar items displayed as one group in the toolbar   
 * @author Werner Puff
 */
public class ToolBarContainer {

	/** image path to FIXXXME: which image is this??? */
	private String imagePath;
	
	/** title of the container */
	private String title;
	
	/**  */
	private static final long serialVersionUID = 1L;

	/**
	 * FIXXXME: path to which image?
	 * @return
	 */
	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	/**
	 * Returns the title to displayed with this toolbar container
	 * @return title of the container
	 */
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
