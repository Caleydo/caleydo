/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.net.dwt.swing.mdi;

import java.awt.Graphics;

import javax.swing.JInternalFrame;

import cerberus.manager.IDistComponentManager;
import cerberus.manager.IGeneralManager;
import cerberus.manager.type.ManagerObjectType;

import cerberus.command.ICommandListener;
import cerberus.data.collection.view.IViewCanvas;
import cerberus.net.dwt.DNetEvent;
import cerberus.net.dwt.DNetEventComponentInterface;
import cerberus.net.dwt.DNetEventListener;
import cerberus.xml.parser.ISaxParserHandler;

/**
 * @author Michael Kalkusch
 *
 */
public class DInternalFrame 
extends JInternalFrame 
implements DNetEventComponentInterface, IViewCanvas {

	/**
	 * 
	 */
	public DInternalFrame() {
		super();
	}

	/**
	 * @param arg0
	 */
	public DInternalFrame(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public DInternalFrame(String arg0, boolean arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public DInternalFrame(String arg0, boolean arg1, boolean arg2) {
		super(arg0, arg1, arg2);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public DInternalFrame(String arg0, boolean arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 * @param arg4
	 */
	public DInternalFrame(String arg0, boolean arg1, boolean arg2,
			boolean arg3, boolean arg4) {
		super(arg0, arg1, arg2, arg3, arg4);
	}

	/* (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventComponentInterface#addNetActionListener(cerberus.net.dwt.DNetEventListener)
	 */
	/* (non-Javadoc)
	 * @see cerberus.net.dwt.swing.mdi.IDInternalFrame#addNetActionListener(cerberus.net.dwt.DNetEventListener)
	 */
	public void addNetActionListener(DNetEventListener addListener) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventComponentInterface#handleNetEvent(cerberus.net.dwt.DNetEvent)
	 */
	/* (non-Javadoc)
	 * @see cerberus.net.dwt.swing.mdi.IDInternalFrame#handleNetEvent(cerberus.net.dwt.DNetEvent)
	 */
	public void handleNetEvent(DNetEvent event) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventComponentInterface#addCommandListener(cerberus.command.ICommandListener)
	 */
	/* (non-Javadoc)
	 * @see cerberus.net.dwt.swing.mdi.IDInternalFrame#addCommandListener(cerberus.command.ICommandListener)
	 */
	public boolean addCommandListener(ICommandListener setCommandListener) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventComponentInterface#containsNetEvent(cerberus.net.dwt.DNetEvent)
	 */
	/* (non-Javadoc)
	 * @see cerberus.net.dwt.swing.mdi.IDInternalFrame#containsNetEvent(cerberus.net.dwt.DNetEvent)
	 */
	public boolean containsNetEvent(DNetEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventComponentInterface#getNetEventComponent(cerberus.net.dwt.DNetEvent)
	 */
	/* (non-Javadoc)
	 * @see cerberus.net.dwt.swing.mdi.IDInternalFrame#getNetEventComponent(cerberus.net.dwt.DNetEvent)
	 */
	public DNetEventComponentInterface getNetEventComponent(DNetEvent event) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventComponentInterface#setParentCreator(cerberus.data.manager.DComponentManager)
	 */
	/* (non-Javadoc)
	 * @see cerberus.net.dwt.swing.mdi.IDInternalFrame#setParentCreator(cerberus.manager.IDistComponentManager)
	 */
	public void setParentCreator(IDistComponentManager creator) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventComponentInterface#setParentComponent(cerberus.net.dwt.DNetEventComponentInterface)
	 */
	/* (non-Javadoc)
	 * @see cerberus.net.dwt.swing.mdi.IDInternalFrame#setParentComponent(cerberus.net.dwt.DNetEventComponentInterface)
	 */
	public void setParentComponent(DNetEventComponentInterface parentComponent) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see cerberus.data.xml.IMementoNetEventXML#createMementoXMLperObject()
	 */
	/* (non-Javadoc)
	 * @see cerberus.net.dwt.swing.mdi.IDInternalFrame#createMementoXMLperObject()
	 */
	public String createMementoXMLperObject() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.xml.IMementoXML#setMementoXML_usingHandler(cerberus.net.dwt.swing.parser.DParseSaxHandler)
	 */
	/* (non-Javadoc)
	 * @see cerberus.net.dwt.swing.mdi.IDInternalFrame#setMementoXML_usingHandler(cerberus.xml.parser.ISaxParserHandler)
	 */
	public boolean setMementoXML_usingHandler(ISaxParserHandler refSaxHandler) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.xml.IMementoItemXML#getId()
	 */
	/* (non-Javadoc)
	 * @see cerberus.net.dwt.swing.mdi.IDInternalFrame#getId()
	 */
	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.xml.IMementoItemXML#setId(cerberus.data.manager.GeneralManager, int)
	 */
	/* (non-Javadoc)
	 * @see cerberus.net.dwt.swing.mdi.IDInternalFrame#setId(int)
	 */
	public void setId(int iSetDNetEventId) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see cerberus.data.xml.IMementoItemXML#createMementoXML()
	 */
	/* (non-Javadoc)
	 * @see cerberus.net.dwt.swing.mdi.IDInternalFrame#createMementoXML()
	 */
	public String createMementoXML() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.xml.IMementoCallbackXML#callbackForParser(cerberus.data.manager.BaseManagerType, java.lang.String, cerberus.net.dwt.swing.parser.DParseSaxHandler)
	 */
	/* (non-Javadoc)
	 * @see cerberus.net.dwt.swing.mdi.IDInternalFrame#callbackForParser(cerberus.manager.type.ManagerObjectType, java.lang.String, java.lang.String, cerberus.xml.parser.ISaxParserHandler)
	 */
	public void callbackForParser( final ManagerObjectType type,
			final String tag_causes_callback,
			final String details,
			final ISaxParserHandler refSaxHandler) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ViewCanvas#updateState()
	 */
	/* (non-Javadoc)
	 * @see cerberus.net.dwt.swing.mdi.IDInternalFrame#updateState()
	 */
	public void updateState() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.UniqueManagedInterface#getManager()
	 */
	/* (non-Javadoc)
	 * @see cerberus.net.dwt.swing.mdi.IDInternalFrame#getManager()
	 */
	public IGeneralManager getManager() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.UniqueManagedInterface#getBaseType()
	 */
	/* (non-Javadoc)
	 * @see cerberus.net.dwt.swing.mdi.IDInternalFrame#getBaseType()
	 */
	public ManagerObjectType getBaseType() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.net.dwt.swing.mdi.IDInternalFrame#paintDComponent(java.awt.Graphics)
	 */
	public void paintDComponent( Graphics g ) {
		
	}

}
