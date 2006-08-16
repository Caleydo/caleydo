/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.net.dwt.swing.component;


import java.awt.Rectangle;
import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

import org.xml.sax.*;
//import org.xml.sax.helpers.ParserFactory;
import org.xml.sax.helpers.XMLReaderFactory;
//import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import cerberus.manager.IDistComponentManager;
import cerberus.manager.type.ManagerObjectType;
//import org.xml.sax.helpers.DefaultHandler;

import cerberus.command.ICommandListener;
import cerberus.net.dwt.DNetEvent;
import cerberus.net.dwt.DNetEventComponentInterface;
import cerberus.net.dwt.DNetEventListener;
import cerberus.net.dwt.swing.parser.DButtonSaxHandler;
import cerberus.xml.parser.ISaxParserHandler;
import cerberus.net.protocol.interaction.SuperMouseEvent;
import cerberus.util.exception.CerberusRuntimeException;

/**
 * Distributed JButton.
 * 
 * @author Michael Kalkusch
 *
 */
public class DButton 
extends JButton 
implements DNetEventComponentInterface {

	static final long serialVersionUID = 80008020;
	
	static final private int iTabOffsetXML = 2;
	
	protected int iDNetEventComponentId;
	
	/**
	 * Reference to parent and/or creator of this class.
	 * Used to check, if id was changed by creator.
	 * 
	 * TODO: remove this from stable code!
	 */
	private IDistComponentManager refParentCreator;
	
	/**
	 * reference to parent object.
	 */
	private DNetEventComponentInterface setParentComponent = this;
	
	
	protected SuperMouseEvent refMouseNetEvent;
	
	/**
	 * stores references to Command listener objects.
	 */
	private Vector<ICommandListener> vecRefCommandListener;
	
	/**
	 * 
	 */
	public DButton() {
		super();
	}

	/**
	 * @param arg0
	 */
	public DButton(Icon arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public DButton(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public DButton(Action arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public DButton(String arg0, Icon arg1) {
		super(arg0, arg1);
	}
	
	
	/* (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventComponentInterface#addNetActionListener(cerberus.net.dwt.DNetEventListener)
	 */
	public void addNetActionListener( final DNetEventListener addListener) {		

	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventComponentInterface#handleNetEvent(cerberus.net.dwt.DNetEvent)
	 */
	public void handleNetEvent( final DNetEvent event ) {
		
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventComponentInterface#getNetEventComponent(cerberus.net.dwt.DNetEvent)
	 */
	public DNetEventComponentInterface getNetEventComponent( DNetEvent event ) {
		if ( containsNetEvent( event ) ) {
			return this;
		}
		return null;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventComponentInterface#addCommandListener(cerberus.command.ICommandListener)
	 */
	synchronized public boolean addCommandListener( final ICommandListener setCommandListener ) {
		
		if ( vecRefCommandListener.contains(setCommandListener)) {
			return false;
		}
		
		vecRefCommandListener.add( setCommandListener );
		return true;
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventComponentInterface#containsNetEvent(cerberus.net.dwt.DNetEvent)
	 */
	public boolean containsNetEvent( final DNetEvent event ) {
		return this.getBounds().contains( 
				event.getSuperMouseEvent().getX(),
				event.getSuperMouseEvent().getY() );
	}
	
	public  SuperMouseEvent getNetMouseEvent() {
		return refMouseNetEvent;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventMementoXML#getDNetEventId()
	 */
	public int getId() {
		return iDNetEventComponentId;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventMementoXML#setDNetEventId(java.lang.Object, int)
	 */
	public void setId( final int iSetDNetEventId ) {
		iDNetEventComponentId = iSetDNetEventId;
	}

	
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventMementoXML#createMementoXML()
	 */
	public String createMementoXMLperObject() {			
		/**
		 * XML Header
		 */
		String XML_MementoString = getTab(0) + "<DNetEventComponent dNetEvent_Id=\"" +
			this.iDNetEventComponentId + "\" label=\"DButton Swing\">\n";
		XML_MementoString += getTab(1) + "<DNetEvent_type type=\"DButton\"/>\n";
		XML_MementoString += getTab(1) + "<DNetEvent_details>\n";
		
		/**
		 * position of component
		 */
		final Rectangle rec = this.getBounds();
		XML_MementoString += getTab(2) + "<position x=\"" + rec.x + 
			"\" y=\"" + rec.y +
			"\" width=\"" + rec.width + 
			"\" height=\"" + rec.height + "\" />\n";
		
		/**
		 * State of component
		 */
		XML_MementoString += getTab(2) + "<state enabled=\"" + this.isEnabled() +
			"\" visible=\"" + this.isVisible() + 
			"\" label=\"" + this.getText() + 
			"\" tooltip=\"" + this.getToolTipText() + "\" />\n";	
		
		/**
		 * XML footer
		 */
		XML_MementoString += getTab(1) + "</DNetEvent_details>\n";		
		XML_MementoString += getTab(0) + "</DNetEventComponent>\n\n";
		
		return XML_MementoString;		
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventMementoXML#createMementoXML()
	 */
	public String createMementoXML() {
		return this.createMementoXMLperObject();
	}

	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.xml.IMementoNetEventXML#setMementoXML_usingHandler(cerberus.net.dwt.swing.parser.DParseSaxHandler)
	 */
	public synchronized boolean setMementoXML_usingHandler( final ISaxParserHandler refSaxHandler ) {
		
		try {
			/**
			 * TRy to cast refSaxHandler ...
			 */
			final DButtonSaxHandler refDButtonSaxHandler = (DButtonSaxHandler) refSaxHandler;
			
			/**
			 * Test if GUI component does already exist...
			 */			
			if ( iDNetEventComponentId != refDButtonSaxHandler.getXML_dNetEvent_Id() ) {
				System.out.println("WARNING Parsing setMementoXML_usingHandler() wrong Id!  (" + 
						this.iDNetEventComponentId + ") ==overrules==> XML: [" +
						refDButtonSaxHandler.getXML_dNetEvent_Id() + "]s");
				//FIXME do proper error message and assertion 				
				//assert false : "setMementoXML_usingHandler() did not match ID from XML file!";
				return false;
			}			
			
			this.setVisible( refDButtonSaxHandler.getXML_state_visible() );
			this.setEnabled( refDButtonSaxHandler.getXML_state_enabled() );
			this.setToolTipText( refDButtonSaxHandler.getXML_state_tooltip() );
			this.setName( refDButtonSaxHandler.getXML_state_label() );
			this.setText( refDButtonSaxHandler.getXML_state_label() );
			this.setBounds( refDButtonSaxHandler.getXML_position_x(),
					refDButtonSaxHandler.getXML_position_y(),
					refDButtonSaxHandler.getXML_position_width(),
					refDButtonSaxHandler.getXML_position_height() );
			/**
			 * memento is applied now!
			 */
			return true;
						
		}
		catch (ClassCastException cce) {
			System.out.println("DEBUG: DButton.setMementoXML_usingHandler() wrong SaxHandler. Should be (DButtonSaxHandler). " + cce.toString() );
			return false;
		}
		catch (NullPointerException ne) {
			System.out.println("DEBUG: DButton.setMementoXML_usingHandler() " + ne.toString() );
			return false;
		}
		
	}
	
	public static void main(String[] args) {
		
		DButton test = new DButton();
		
		try {
			File inputFile = new File( args[0] );
			
			FileReader inReader = new FileReader( inputFile );
			
			InputSource inStream = new InputSource( inReader );
			
			//test.setMementoXML( inStream );
	
			System.out.println("DONE");
		
		}
		catch (FileNotFoundException fnf_e) {
			fnf_e.printStackTrace();
		}
		catch (Exception e) {
			System.out.println(" error " + e.toString());
		}
	}
	
	/**
	 * formating XML output.
	 * Talking locla tab offset into account. Thus  getTab(0) also is an important call.
	 * 
	 * @param iCountTabs number of tabs to be set starting with 0 tabs
	 * @return number of tabs created
	 */
	static private String getTab( final int iCountTabs ) {
		final String sTab ="  ";
		String tabResult = "";
		
		for ( int i=0; i<iCountTabs+iTabOffsetXML ; i++) {
			tabResult += sTab;
		}
		return tabResult;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventComponentInterface#setParentCreator(cerberus.data.manager.DComponentManager)
	 */
	public final void setParentCreator( final IDistComponentManager creator) {
		refParentCreator = creator; 
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventComponentInterface#setParentComponent(cerberus.net.dwt.DNetEventComponentInterface)
	 */
	public final void setParentComponent( final DNetEventComponentInterface parentComponent) {
		setParentComponent = parentComponent; 
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.xml.IMementoNetEventXML#callbackForParser(java.lang.String)
	 */
	public void callbackForParser( final ManagerObjectType type,
			final String tag_causes_callback,
			final String details,
			final ISaxParserHandler refSaxHandler ) {
		
	}
	
}
