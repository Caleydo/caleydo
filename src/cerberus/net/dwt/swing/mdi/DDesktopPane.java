/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.net.dwt.swing.mdi;

import java.util.Vector;
import java.util.Iterator;
import java.awt.BorderLayout;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;

import cerberus.manager.DComponentManager;
import cerberus.manager.type.ManagerObjectType;

import cerberus.command.ICommandListener;
import cerberus.net.dwt.DNetEvent;
import cerberus.net.dwt.DNetEventComponentInterface;
import cerberus.net.dwt.DNetEventListener;
import cerberus.xml.parser.DParseSaxHandler;
import cerberus.net.dwt.swing.mdi.DInternalFrame;

/**
 * @author Michael Kalkusch
 *
 */
public class DDesktopPane extends JDesktopPane implements
		DNetEventComponentInterface {

	protected Vector<DInternalFrame> vecDInternalFrame; 
	
	/**
	 * 
	 */
	public DDesktopPane() {
		super();
		
		vecDInternalFrame = new Vector<DInternalFrame> ();
		
		this.setVisible( true );
		
//		DInternalFrame refTestIFrame = new DInternalFrame("TestFame",true,true,true,true);
//		refTestIFrame.add( new JLabel("TEXT bla"), BorderLayout.CENTER );
//		refTestIFrame.pack();
//		this.add( refTestIFrame );
//		refTestIFrame.setVisible( true );	
	}

	/* (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventComponentInterface#addNetActionListener(cerberus.net.dwt.DNetEventListener)
	 */
	public void addNetActionListener(DNetEventListener addListener) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * Adds an InternalFrame to the DesktopPane and to ints internal data structur.
	 * 
	 * @param addDInternalFrame
	 */
	public void addInternalFrame( DInternalFrame addDInternalFrame ) {
		this.add( addDInternalFrame );
		vecDInternalFrame.addElement( addDInternalFrame );
	}
	
	/**
	 * Creates a new InternalFrame and links it to the DDesktopPane as well as its internal data structur.
	 * 
	 * @see javax.swing.JInternalFrame
	 * 
	 * @return reference to new DInternalFrame
	 */
	public DInternalFrame createInternalFrame( final String textHeaderInternalFrame ) {
		DInternalFrame newDInternalFrame = new DInternalFrame( textHeaderInternalFrame ,true,true,false,true);
		this.add( newDInternalFrame );
		newDInternalFrame.setVisible( true );
		
		vecDInternalFrame.addElement( newDInternalFrame );
		
		return newDInternalFrame;
	}
	

	/* (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventComponentInterface#handleNetEvent(cerberus.net.dwt.DNetEvent)
	 */
	public void handleNetEvent(DNetEvent event) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventComponentInterface#addCommandListener(cerberus.command.ICommandListener)
	 */
	public boolean addCommandListener(ICommandListener setCommandListener) {
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
	 * @see cerberus.data.xml.MementoXML#setMementoXML_usingHandler(cerberus.net.dwt.swing.parser.DParseSaxHandler)
	 */
	public boolean setMementoXML_usingHandler(DParseSaxHandler refSaxHandler) {
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
	 * @see cerberus.data.xml.MementoCallbackXML#callbackForParser(cerberus.data.manager.BaseManagerType, java.lang.String, cerberus.net.dwt.swing.parser.DParseSaxHandler)
	 */
	public void callbackForParser(final ManagerObjectType type,
			final String tag_causes_callback, 
			final String details,
			DParseSaxHandler refSaxHandler) {
		// TODO Auto-generated method stub

	}

}
