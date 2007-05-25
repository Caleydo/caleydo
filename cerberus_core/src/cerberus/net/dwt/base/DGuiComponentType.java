/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.net.dwt.base;

/**
 * Types of different distributed gui components.
 * 
 * @see prometheus.net.dwt.DNetEvent
 * @see prometheus.net.dwt.DNetEventListener
 * @see prometheus.net.dwt.DNetEventComponentInterface
 * 
 * @author Michael Kalkusch
 *
 */
public enum DGuiComponentType {

	NONE(),
	PANEL(),
	BUTTON(),
	LIST(),
	CHECKBOX(),	
	FRAME(),
	JHISTOGRAM();
	
	// private void NetEventType() { };
	
	/**
	 * Convert a String to an DGuiComponentType.
	 */
	public final static DGuiComponentType getType( final String parse ){
		
		if ( parse.equalsIgnoreCase( "BUTTON" )) {
			return DGuiComponentType.BUTTON;
		}
		else if ( parse.equalsIgnoreCase( "PANEL" )) {
			return DGuiComponentType.PANEL;
		}
		else if ( parse.equalsIgnoreCase( "LIST" )) {
			return DGuiComponentType.LIST;
		}
		else if ( parse.equalsIgnoreCase( "FRAME" )) {
			return DGuiComponentType.FRAME;
		}
		else if ( parse.equalsIgnoreCase( "JHISTOGRAM" )) {
			return DGuiComponentType.JHISTOGRAM;
		}
		
		return DGuiComponentType.NONE;
	
	}
}
