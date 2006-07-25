/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.net.dwt.swing.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.Iterator;
import java.util.Hashtable;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import cerberus.manager.CommandManager;
import cerberus.manager.DComponentManager;
import cerberus.manager.type.ManagerObjectType;

import cerberus.command.CommandListener;
import cerberus.command.CommandInterface;
import cerberus.net.dwt.DNetEvent;
import cerberus.net.dwt.DNetEventComponentInterface;
import cerberus.net.dwt.DNetEventListener;
import cerberus.xml.parser.DParseSaxHandler;
import cerberus.net.dwt.swing.menu.DMenuCmdActionListener;
import cerberus.util.exception.PrometheusRuntimeException;

/**
 * @author Michael Kalkusch
 *
 */
public class DMenuBootStraper implements DNetEventComponentInterface 
 {

	private JMenuBar gui_menuBar;
	
	private Vector<JMenuItem> gui_vec_MenuItem;
	
	private Hashtable<String,JMenuItem> hashMenuLookupName_to_MenuItem;
	
	private Hashtable<String,CommandInterface> hashMenuLookupName_to_Command;
	
	/**
	 * String to define and address root menu.
	 * 
	 * @see cerberus.net.dwt.swing.menu.DMenuBootStraper#addMenuItemWithCommand(String, String, String, char, String, boolean, CommandInterface)
	 */
	public final static String MENU_ROOT = "ROOT";
	
	public final static String MENU_SEPERATOR = "SEPERATOR";
	
	/**
	 * Make sure root menu is only assigned once!
	 */
	private boolean bGui_hasMenuBar_one_Menu = false;
	
	protected CommandManager refCommandManager;
	
	/**
	 * 
	 */
	public DMenuBootStraper( CommandManager setCommandManager ) {
		super();
		
		refCommandManager = setCommandManager;
		
		gui_menuBar = new JMenuBar();
		
		hashMenuLookupName_to_MenuItem = new Hashtable<String,JMenuItem>();
		
		hashMenuLookupName_to_Command = new Hashtable<String,CommandInterface>(); 
		
		gui_vec_MenuItem = new Vector<JMenuItem>();
	}
	
	private JMenuItem getMenuItemByString( final String sMenuLookupName) {		
		return hashMenuLookupName_to_MenuItem.get( sMenuLookupName );		
	}

	private boolean hasMenuItemByString( final String sMenuLookupName) {		
		return hashMenuLookupName_to_MenuItem.containsKey( sMenuLookupName );		
	}
	
	/**
	 * Get the menu bar.
	 * 
	 * @return menu bar
	 */
	public JMenuBar getMenuBar() {
		return gui_menuBar;
	}
	
	/**
	 * 
	 * @see cerberus.net.dwt.swing.menu.DMenuBootStraper#MENU_ROOT
	 * 
	 * @param sMenuLookupName
	 * @param sMenuText
	 * @param sMenuTooltip
	 * @param sMenuMnemonic
	 * @param sMenuParentLookupName
	 * @param isItem
	 * @param refCommand
	 */
	public JMenuItem addMenuItemWithCommand( 
			final String sMenuLookupName,
			final String sMenuText,
			final String sMenuTooltip,
			final char sMenuMnemonic,
			final String sMenuParentLookupName,
			final boolean isItem,
			final CommandInterface refCommand ) {
		
		/**
		 * SEPERATOR doen not need its own Menu-Object
		 */
		if ( sMenuLookupName.equalsIgnoreCase( MENU_SEPERATOR ) ) {
				
			if ( sMenuParentLookupName.equalsIgnoreCase( MENU_ROOT ) ) {
				assert false : "Can not add Separator to parent [" +
					sMenuParentLookupName + "]";
			}
			else {
				JMenuItem parentMenuObject = this.getMenuItemByString( sMenuParentLookupName );
				
				try {
					JMenu buffer = (JMenu) parentMenuObject;
					buffer.addSeparator();
				}
				catch (NullPointerException npe) {
					assert false : "Can not add Separator to parent [" +
						sMenuParentLookupName + "]";
				}
			}
			return null;
			
		}
		
		
		JMenuItem newMenuObject = null;
			
		/**
		 * Type of menu object...
		 */
		if ( isItem ) {
			newMenuObject = new JMenuItem();
		}
		else {
			newMenuObject = new JMenu();
		}
		
		
		/**
		 * Set menu item parameter...
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
		if ( sMenuTooltip.length() > 0 ) {
			newMenuObject.setToolTipText( sMenuTooltip );
		}
		
		
		/**
		 * Secial case of root menu...
		 */
		if ( sMenuParentLookupName.equalsIgnoreCase( MENU_ROOT ) ) {
			
//			if ( ! bGui_hasMenuBar_one_Menu ) {
				bGui_hasMenuBar_one_Menu = true;
				gui_menuBar.add( newMenuObject );
//			}
//			else {
//				throw new PrometheusRuntimeException("DMenuBootStraper::add.. try to assign root menu a second time");
//			}	
			
		}
		else {
			JMenuItem parentMenuObject = this.getMenuItemByString( sMenuParentLookupName );
			
			if ( parentMenuObject != null ) {
				parentMenuObject.add( newMenuObject );
			}
			else {
				throw new PrometheusRuntimeException("DMenuBootStraper::add.. can not find parent menu ["+
						sMenuParentLookupName + "] for child menu [" +
						sMenuLookupName + "]" );			
			} // end if ( parentMenuObject != null ) ... else 
			
			if ( sMenuParentLookupName.equalsIgnoreCase( MENU_SEPERATOR ) ) {
				try {
					((JMenu) parentMenuObject).addSeparator();
					return null;
				}
				catch (NullPointerException npe) {
					throw  new PrometheusRuntimeException("DMenuBootStraper::add.. can not add Seperator to non Menu item ["+
							sMenuParentLookupName + "]");
				}
			} // end if ( sMenuParentLookupName.equalsIgnoreCase( MENU_SEPERATOR ) ) {
			
		} // end  if ( sMenuParentLookupName.equalsIgnoreCase( MENU_ROOT ) )  ... else
		
		
		
		
	
		
	
		
		hashMenuLookupName_to_MenuItem.put( sMenuLookupName, newMenuObject );
		
		gui_vec_MenuItem.addElement( newMenuObject );
		
		/**
		 * Add refCommand only if not null...
		 */
		if ( refCommand != null ) {
			hashMenuLookupName_to_Command.put( sMenuLookupName, refCommand );
						
			newMenuObject.addActionListener( 
					new DMenuCmdActionListener( this.refCommandManager, refCommand ) );
		}
		
		return newMenuObject;
	}

	/* (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventComponentInterface#addNetActionListener(cerberus.net.dwt.DNetEventListener)
	 */
	public void addNetActionListener(DNetEventListener addListener) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventComponentInterface#handleNetEvent(cerberus.net.dwt.DNetEvent)
	 */
	public void handleNetEvent(DNetEvent event) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventComponentInterface#addCommandListener(cerberus.command.CommandListener)
	 */
	public boolean addCommandListener(CommandListener setCommandListener) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventComponentInterface#containsNetEvent(cerberus.net.dwt.DNetEvent)
	 */
	public boolean containsNetEvent(DNetEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventComponentInterface#getNetEventComponent(cerberus.net.dwt.DNetEvent)
	 */
	public DNetEventComponentInterface getNetEventComponent(DNetEvent event) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventComponentInterface#setParentCreator(cerberus.data.manager.DComponentManager)
	 */
	public void setParentCreator(DComponentManager creator) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventComponentInterface#setParentComponent(cerberus.net.dwt.DNetEventComponentInterface)
	 */
	public void setParentComponent(DNetEventComponentInterface parentComponent) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see cerberus.data.xml.MementoNetEventXML#createMementoXMLperObject()
	 */
	public String createMementoXMLperObject() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.xml.MementoXML#setMementoXML_usingHandler(cerberus.xml.parser.DParseSaxHandler)
	 */
	public boolean setMementoXML_usingHandler( final DParseSaxHandler refSaxHandler) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	/* (non-Javadoc)
	 * @see cerberus.data.xml.MementoItemXML#getId()
	 */
	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.xml.MementoItemXML#setId(cerberus.data.manager.GeneralManager, int)
	 */
	public void setId(int iSetDNetEventId) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see cerberus.data.xml.MementoItemXML#createMementoXML()
	 */
	public String createMementoXML() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.xml.MementoCallbackXML#callbackForParser(cerberus.data.manager.BaseManagerType, java.lang.String, cerberus.xml.parser.DParseSaxHandler)
	 */
	public void callbackForParser( final ManagerObjectType type,
			final String tag_causes_callback,
			final String details,
			final DParseSaxHandler refSaxHandler) {
		// TODO Auto-generated method stub

	}


}
