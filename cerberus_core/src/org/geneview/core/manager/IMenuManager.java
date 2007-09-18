/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package org.geneview.core.manager;

import javax.swing.JMenuItem;


import org.geneview.core.command.ICommand;

/**
 * Manager for Menus.
 * 
 * @author Michael Kalkusch
 *
 */
public interface IMenuManager extends IGeneralManager {

	/**
	 * 
	 * @param iMenuId
	 * @return
	 */
	public JMenuItem getMenuBarById( final int iMenuId );
	
	
	/**
	 * Create a new menu bound to a frame.
	 * 
	 * @param iFrameId frame to bound the menu to
	 * @param sMenuId new menu id
	 * @param sRootMenuId root menu to link new menu item to
	 * @param sMenuText label of the menu
	 * @param sMenuTooltipText tooltip of the menu
	 * @param sMenuMnemonic mnemonic or '*' if no mnemonic should be set
	 * @param bIsItem TURE if it is a leave, FALSE if it is a sub-menu
	 * @param refCommand command to be executed if menu item is clicked
	 * @return unique Id of the new menu item or 0 if a seperator was added
	 */
	public int createMenu( final int iFrameId,
			final String sMenuId,
			final String sRootMenuId,
			final String sMenuText,
			final String sMenuTooltipText,
			final char sMenuMnemonic,
			final boolean bIsItem,
			final ICommand refCommand );
	
	/**
	 * ISet state of menu item.
	 * 
	 * @param iMenuId id to identify menu
	 * @param bSetEnabled set menu enabeld or disabled
	 */
	public void setMenuEnabled( final int iMenuId,
			final boolean bSetEnabled );
	
	/**
	 * Sets state of a menu item.
	 * 
	 * @param iMenuId id to identify menu
	 * @param bSetVisible set menu visible or invisible
	 */
	public void setMenuVisible( final int iMenuId,
			final boolean bSetVisible );
	
//	public void setMenuBarById( final int iFrameId );
	
}
