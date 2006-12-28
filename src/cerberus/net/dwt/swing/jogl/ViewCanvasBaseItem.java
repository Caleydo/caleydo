/**
 * 
 */
package cerberus.net.dwt.swing.jogl;

import java.awt.Graphics;

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
public class ViewCanvasBaseItem implements IViewCanvas {

	/**
	 * 
	 */
	public ViewCanvasBaseItem() {
				
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ViewCanvas#paintDComponent(java.awt.Graphics)
	 */
	public void paintDComponent(Graphics g) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ViewCanvas#updateState()
	 */
	public void updateState() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.UniqueManagedInterface#getManager()
	 */
	public IGeneralManager getManager() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.UniqueManagedInterface#setId(int)
	 */
	public void setId(int iSetCollectionId) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.UniqueManagedInterface#getId()
	 */
	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.UniqueManagedInterface#getBaseType()
	 */
	public ManagerObjectType getBaseType() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.xml.IMementoItemXML#createMementoXML()
	 */
	public String createMementoXML() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.xml.IMementoXML#setMementoXML_usingHandler(cerberus.net.dwt.swing.parser.DParseSaxHandler)
	 */
	public boolean setMementoXML_usingHandler(ISaxParserHandler refSaxHandler) {
		// TODO Auto-generated method stub
		return false;
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
	 * @see cerberus.net.dwt.DNetEventComponentInterface#setParentCreator(cerberus.manager.IDistComponentManager)
	 */
	public void setParentCreator(IDistComponentManager creator) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventComponentInterface#setParentComponent(cerberus.net.dwt.DNetEventComponentInterface)
	 */
	public void setParentComponent(DNetEventComponentInterface parentComponent) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see cerberus.data.xml.IMementoNetEventXML#createMementoXMLperObject()
	 */
	public String createMementoXMLperObject() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.xml.IMementoCallbackXML#callbackForParser(cerberus.manager.BaseManagerType, java.lang.String, java.lang.String, cerberus.net.dwt.swing.parser.DParseSaxHandler)
	 */
	public void callbackForParser(ManagerObjectType type,
			String tag_causes_callback, String details,
			ISaxParserHandler refSaxHandler) {
		// TODO Auto-generated method stub

	}

}
