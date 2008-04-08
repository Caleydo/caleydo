package cerberus.net.dwt.swing.collection;

import java.awt.Graphics;

import cerberus.command.ICommandListener;
import cerberus.manager.IDistComponentManager;
import cerberus.manager.IGeneralManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.net.dwt.DNetEvent;
import cerberus.net.dwt.DNetEventComponentInterface;
import cerberus.net.dwt.DNetEventListener;
import cerberus.xml.parser.ISaxParserHandler;

public interface IDSwingSelectionCanvas {

	/**
	 * Get the singelton
	 * 
	 * @see cerberus.data.IUniqueManagedObject#getManager()
	 */
	public abstract IGeneralManager getManager();

	/* (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventComponentInterface#addNetActionListener(cerberus.net.dwt.DNetEventListener)
	 */
	public abstract void addNetActionListener(DNetEventListener addListener);

	/* (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventComponentInterface#handleNetEvent(cerberus.net.dwt.DNetEvent)
	 */
	public abstract void handleNetEvent(DNetEvent event);

	/* (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventComponentInterface#addCommandListener(cerberus.command.ICommandListener)
	 */
	public abstract boolean addCommandListener(
			ICommandListener setCommandListener);

	/* (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventComponentInterface#containsNetEvent(cerberus.net.dwt.DNetEvent)
	 */
	public abstract boolean containsNetEvent(DNetEvent event);

	/* (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventComponentInterface#getNetEventComponent(cerberus.net.dwt.DNetEvent)
	 */
	public abstract DNetEventComponentInterface getNetEventComponent(
			DNetEvent event);

	/* (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventMementoXML#getDNetEventId()
	 */
	public abstract int getId();

	/* (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventMementoXML#setDNetEventId(java.lang.Object, int)
	 */
	public abstract void setId(int iSetDNetEventId);

	public abstract void setParentCreator(final IDistComponentManager creator);

	public abstract void setParentComponent(
			final DNetEventComponentInterface parentComponent);

	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.xml.IMementoNetEventXML#setMementoXML_usingHandler(cerberus.net.dwt.swing.parser.DParseSaxHandler)
	 */
	public abstract boolean setMementoXML_usingHandler(
			final ISaxParserHandler refSaxHandler);

	/* (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventMementoXML#createMementoXML()
	 */
	public abstract String createMementoXMLperObject();

	/* (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventMementoXML#createMementoXML()
	 */
	public abstract String createMementoXML();

	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.xml.IMementoNetEventXML#callbackForParser(java.lang.String)
	 */
	public abstract void callbackForParser(final ManagerObjectType type,
			final String tag_causes_callback, final String details,
			final ISaxParserHandler refSaxHandler);

	/**
	 * Get the type of this object.
	 * 
	 * @return type of this object
	 */
	public abstract ManagerObjectType getBaseType();

	public abstract void paintDComponent(Graphics g);

	public abstract void notifySelectionHasChangedInGui();

	public abstract void updateAllSelectionsFromGui();

	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.collection.ViewCanvas#updateState()
	 */
	public abstract void updateState();

}