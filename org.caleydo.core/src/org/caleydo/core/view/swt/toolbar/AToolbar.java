package org.caleydo.core.view.swt.toolbar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public abstract class AToolbar {

	protected Composite swtContainer;
	
	protected ToolBar toolBar;
	
	protected int iWidth = 100;
	
	/**
	 * Constructor
	 * 
	 * @param pathwayGraphViewRep
	 */
	public AToolbar(Composite swtContainer) {
		
		this.swtContainer = swtContainer;
	}
	
	protected void initToolbar() {
		
		toolBar = new ToolBar(swtContainer, SWT.NONE);
		//toolBar.setBounds(0, 0, iWidth, 30);
	}
	
	/**
   	* Helper function to create tool item
   	* 
   	* @param parent the parent toolbar
   	* @param type the type of tool item to create
   	* @param text the text to display on the tool item
   	* @param image the image to display on the tool item
   	* @param hotImage the hot image to display on the tool item
   	* @param toolTipText the tool tip text for the tool item
   	* @return ToolItem
   	*/
	protected ToolItem createToolItem(ToolBar parent, int type, String text,
		  Image image, Image hotImage, String toolTipText) {
		
	  	ToolItem item = new ToolItem(parent, type);
    	item.setText(text);
    	item.setImage(image);
    	item.setHotImage(hotImage);
    	item.setToolTipText(toolTipText);
    	return item;
  	}
}
