package org.caleydo.rcp.view.swt.toolbar;

import org.caleydo.core.view.IView;
import org.caleydo.rcp.view.swt.toolbar.content.AToolBarContent;

/**
 * Infos about toolbar content.  
 * @author Werner Puff
 */
public class ToolBarInfo {

	/** core-view class of this info */
	Class<? extends IView> viewClass;
	
	/** class reference to the ToolBarContent class related to this info */
	Class<? extends AToolBarContent> contentClass;
	
	/** view-id as used within rcp framework and configuration */
	String rcpID;

	/** information if the related view should be ignored when rendering toolbars */
	boolean ignored;

}
