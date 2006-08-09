/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.command.memento;

/**
 * Define types of mementos.
 * 
 * Used by mementofactory and prometheus.command.memento.Memento#getMementoType()
 * 
 * @author Michael Kalkusch
 *
 */
public enum MementoType {

	/**
	 * No special kind of the memento is assigned.
	 */
	GENERAL("kind of memento is not defined"),
	
	/**
	 * This memento can restore a state of an object at once.
	 */
	ABSOLUTE("absolute memento; contains hole state at once"),
	
	/**
	 * This is one memento, that stores only the cahnges since the last update.
	 * It either links to other relative mementos or to an absolute memento.
	 */
	RELATIVE("relative memento; contains incremental states, which are proccessed to obtain state"),
	
	/**
	 * This idicates, that this memento was created by a view and not by an GUI object.
	 * View mementos in general store more data and ar larger than GUI-mementos.
	 */
	VIEW("memento of a hole view; may contain several sub-mementos"),
	
	/**
	 * IMemento of an object, that is neither a GUI-object nor a view. 
	 */
	NOGUI("memento of a GUI componenet");
	
	//FACTORY("");
	
	
	/**
	 * Tooltip describing, that kind of memento this is.
	 */
	private String sTooltip;
	
	/**
	 * Constructor
	 * 
	 * @param tooltip describing, that kind of memento this is.
	 */
	private MementoType( String tooltip ) {
		
	}
	
	/**
	 * Get the tooltip for this enumeration type.
	 * 
	 * @return tooltip for this enumeration type.
	 */
	public String getToolTip() {
		return sTooltip;
	}
}
