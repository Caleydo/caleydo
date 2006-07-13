/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.net.dwt;

import cerberus.manager.DComponentManager;
import cerberus.net.dwt.DNetEvent;
import cerberus.command.CommandListener;
import cerberus.data.xml.MementoNetEventXML;

//import prometheus.net.protocol.interaction.SuperMouseEvent;
//import prometheus.command.CommandInterface;

/**
 * Base interface for all DNetEvent GUI objects.
 * 
 * @see prometheus.net.dwt.DNetEventListener
 * @see prometheus.net.dwt.DNetEvent
 * @see prometheus.data.xml.MementoNetEventXML
 * 
 * @author Michael Kalkusch
 */
public interface DNetEventComponentInterface 
extends MementoNetEventXML 
{
	
	/**
	 * Adds a Listener to the GUI object.
	 * 
	 * @param addListener DNetEventListener to be added.
	 */
	public void addNetActionListener( DNetEventListener addListener );
	
	/**
	 * Callback method triggert by prometheus.net.dwt.DNetEvent
	 * 
	 * @param event DNEtEvent that triggered the callback
	 */
	public void handleNetEvent( DNetEvent event );
	
	/**
	 * Sets a reference to the command listener.
	 * 
	 * @param setCommandListener reference to the command listener
	 */
	public boolean addCommandListener( CommandListener setCommandListener );
	
	/**
	 * Test if a component has to handle the event.
	 * 
	 * @param event Event to test
	 * @return TRUE if this or a sub element is responsible for hadling the event.
	 */
	public boolean containsNetEvent( DNetEvent event );
	
	
	/**
	 * Test if this object or it's cild objects are responsible for an event to be handled.
	 * 
	 * @param event test if this event is handled by thsi component or it's cildren
	 * @return responsibel object or null
	 */
	public DNetEventComponentInterface getNetEventComponent( DNetEvent event );
	
	/**
	 * Set reference to creator, that manages all GUI components.
	 * @param creator reference to manager of GUI components
	 */
	public void setParentCreator( final DComponentManager creator);
	
	/**
	 * Set referecne to parent GUI container.
	 * Used for event handling.
	 * 
	 * @param parentComponent reference to parent GUI container, used for event handling.
	 */
	public void setParentComponent( final DNetEventComponentInterface parentComponent);
	
}
