/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.manager.menu.swing;

import java.util.Hashtable;

import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IMenuManager;
import cerberus.manager.IViewGLCanvasManager;
import cerberus.manager.base.AAbstractManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.manager.type.ManagerType;

//import prometheus.command.CommandType;
import cerberus.command.ICommand;

import cerberus.util.exception.CerberusRuntimeException;

//import prometheus.net.dwt.swing.jogl.WorkspaceSwingFrame;

//TODO: fix import in next line!! 2006-07-11
////import prometheus.net.dwt.swing.menu.DMenuCmdActionListener;

/**
 * @author Michael Kalkusch
 *
 */
public class SwingMenuManager 
 extends AAbstractManager
 implements IMenuManager {
	
	private final IViewGLCanvasManager refViewGLCanvasManager;
	
	private final ICommandManager refCommandManager;
	
	private Hashtable<Integer,JMenuItem> hashMenu;
	
	private final int iInitSizeMenuItems = 40;
	
	public final static String MENU_ROOT = "ROOT";
	
	public final static String MENU_SEPERATOR = "SEPERATOR";
	
	private Hashtable<String,JMenuItem> hashMenuLookupName_to_MenuItem;
	
	/**
	 * 
	 */
	public SwingMenuManager(IGeneralManager setGeneralManager) {
		super( setGeneralManager,
				IGeneralManager.iUniqueId_TypeOffset_GUI_AWT_Menu,
				ManagerType.MENU );

		
		refViewGLCanvasManager = 
			setGeneralManager.getSingelton().getViewGLCanvasManager();
		
		refCommandManager = 
			setGeneralManager.getSingelton().getCommandManager();
			
		hashMenu = new Hashtable<Integer,JMenuItem> (iInitSizeMenuItems);
		
		hashMenuLookupName_to_MenuItem = new Hashtable<String,JMenuItem>();
	}

	private JMenuItem getMenuItemByString( final String sMenuLookupName) {		
		return hashMenuLookupName_to_MenuItem.get( sMenuLookupName );		
	}
	
	/* (non-Javadoc)
	 * @see prometheus.manager.MenuManager#getMenuBarById(java.lang.String)
	 */
	public JMenuItem getMenuBarById(int iMenuId) {
		return hashMenu.get( iMenuId );
	}

	/* (non-Javadoc)
	 * @see prometheus.manager.MenuManager#createMenu(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String, char, boolean, prometheus.command.CommandType)
	 */
	public int createMenu(final int iFrameId, 
			final String sMenuId, 
			final String sRootMenuId,
			final String sMenuText, 
			final String sMenuTooltipText, 
			final char sMenuMnemonic,
			final boolean bIsItem, 
			final ICommand refCommand) {
		
//		WorkspaceSwingFrame bufFrame = refViewCanvasManager.getItemWorkspace( iFrameId );
		
		//TODO: this is not checked currently!! 2006-07-11
		JMenuBar gui_menuBar = 
			(JMenuBar) refViewGLCanvasManager.getItem( iFrameId );
		
		/**
		 * SEPERATOR does not need its own Menu-Object
		 */
		if ( sMenuId.equalsIgnoreCase( MENU_SEPERATOR ) ) {
				
			if ( sRootMenuId.equalsIgnoreCase( MENU_ROOT ) ) {
				assert false : "Can not add Separator to parent [" +
				sRootMenuId + "]";
			}
			else {
				JMenuItem parentMenuObject = 
					this.getMenuItemByString( sRootMenuId );
				
				try {
					JMenu buffer = (JMenu) parentMenuObject;
					buffer.addSeparator();
				}
				catch (NullPointerException npe) {
					assert false : "Can not add Separator to parent [" +
					sRootMenuId + "]";
				}
			}
			return 0;
			
		}
		
		
		JMenuItem newMenuObject = null;
			
		/**
		 * Type of menu object...
		 */
		if ( bIsItem ) {
			newMenuObject = new JMenuItem();
		}
		else {
			newMenuObject = new JMenu();
		}
		
		
		/**
		 * ISet menu item parameter...
		 */
		
		/**
		 * set Mnemonic
		 */
		if ( ! Character.isSpaceChar( sMenuMnemonic ) ) {
			if ( Character.isLetterOrDigit( sMenuMnemonic ) ) {
				newMenuObject.setMnemonic( sMenuMnemonic );
			}
		}
		
		/**
		 * Text
		 */
		newMenuObject.setText( sMenuText );
		
		/**
		 * Tooltip
		 */
		if ( sMenuTooltipText.length() > 0 ) {
			newMenuObject.setToolTipText( sMenuTooltipText );
		}
		
		
		/**
		 * Secial case of root menu...
		 */
		if ( sRootMenuId.equalsIgnoreCase( MENU_ROOT ) ) {
			
//			if ( ! bGui_hasMenuBar_one_Menu ) {
				//bGui_hasMenuBar_one_Menu = true;
				gui_menuBar.add( newMenuObject );
//			}
//			else {
//				throw new CerberusRuntimeException("DMenuBootStraper::add.. try to assign root menu a second time");
//			}	
			
		}
		else {
			JMenuItem parentMenuObject = 
				getMenuItemByString( sRootMenuId );
			
			if ( parentMenuObject != null ) {
				parentMenuObject.add( newMenuObject );
			}
			else {
				throw new CerberusRuntimeException("DMenuBootStraper::add.. can not find parent menu ["+
						sRootMenuId + "] for child menu [" +
						sMenuId + "]" );			
			} // end if ( parentMenuObject != null ) ... else 
			
			if ( sMenuId.equalsIgnoreCase( MENU_SEPERATOR ) ) {
				try {
					((JMenu) parentMenuObject).addSeparator();
					return 0;
				}
				catch (NullPointerException npe) {
					throw  new CerberusRuntimeException("DMenuBootStraper::add.. can not add Seperator to non Menu item ["+
							sRootMenuId + "]");
				}
			} // end if ( sMenuParentLookupName.equalsIgnoreCase( MENU_SEPERATOR ) ) {
			
		} // end  if ( sMenuParentLookupName.equalsIgnoreCase( MENU_ROOT ) )  ... else
		
		
		final int iNewMenuId = this.createId(null);
		
		hashMenu.put( iNewMenuId ,newMenuObject );
		
		hashMenuLookupName_to_MenuItem.put( sMenuId, newMenuObject );
		
		//gui_vec_MenuItem.addElement( newMenuObject );
		
		
		/**
		 * Add refCommand only if not null...
		 */
		if ( refCommand != null ) {
			//hashMenuLookupName_to_Command.put( sMenuId, refCommand );
			
			throw new RuntimeException(" can not addActionListener( ** )!!");
			
			//FIXME: next line needs fix/port to new system! 2006-07-11
//			newMenuObject.addActionListener( 
//					new DMenuCmdActionListener( this.refCommandManager, refCommand ) );
		}
		
		return iNewMenuId;

	}

	/* (non-Javadoc)
	 * @see prometheus.manager.MenuManager#setMenuEnabled(java.lang.String, boolean)
	 */
	public void setMenuEnabled(int iMenuId, boolean bSetEnabled) {
		hashMenu.get( iMenuId ).setEnabled( bSetEnabled );
	}

	/* (non-Javadoc)
	 * @see prometheus.manager.MenuManager#setMenuVisible(java.lang.String, boolean)
	 */
	public void setMenuVisible(int iMenuId, boolean bSetVisible) {
		hashMenu.get( iMenuId ).setVisible( bSetVisible );
	}

	/* (non-Javadoc)
	 * @see prometheus.manager.GeneralManager#hasItem(int)
	 */
	public boolean hasItem(int iItemId) {
		return hashMenu.containsKey( iItemId );
	}

	/* (non-Javadoc)
	 * @see prometheus.manager.GeneralManager#getItem(int)
	 */
	public Object getItem(int iItemId) {
		return hashMenu.get( iItemId );
	}

	/* (non-Javadoc)
	 * @see prometheus.manager.GeneralManager#size()
	 */
	public int size() {
		return hashMenu.size();
	}


	/* (non-Javadoc)
	 * @see prometheus.manager.GeneralManager#registerItem(java.lang.Object, int, prometheus.manager.BaseManagerType)
	 */
	public boolean registerItem(Object registerItem, int iItemId,
			ManagerObjectType type) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see prometheus.manager.GeneralManager#unregisterItem(int, prometheus.manager.BaseManagerType)
	 */
	public boolean unregisterItem(int iItemId, ManagerObjectType type) {
		// TODO Auto-generated method stub
		return false;
	}


}
