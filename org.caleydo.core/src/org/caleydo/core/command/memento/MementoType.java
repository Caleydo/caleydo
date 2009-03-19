package org.caleydo.core.command.memento;

/**
 * Define types of mementos. Used by memento factory and org.caleydo.command.memento.Memento#getMementoType()
 * 
 * @author Michael Kalkusch
 */
public enum MementoType {

	/**
	 * No special kind of the memento is assigned.
	 */
	GENERAL("kind of memento is not defined"),

	/**
	 * This memento can restore a state of an object at once.
	 */
	ABSOLUTE("absolute memento; contains whole state at once"),

	/**
	 * This is one memento, that stores only the changes since the last update. It either links to other
	 * relative mementos or to an absolute memento.
	 */
	RELATIVE("relative memento; contains incremental states, which are proccessed to obtain state"),

	/**
	 * This idicates, that this memento was created by a view and not by an GUI object. View mementos in
	 * general store more data and or larger than GUI-mementos.
	 */
	VIEW("memento of a hole view; may contain several sub-mementos"),

	/**
	 * IMemento of an object, that is neither a GUI-object nor a view.
	 */
	NOGUI("memento of a GUI componenet");

	// FACTORY("");

	/**
	 * Tooltip describing, that kind of memento this is.
	 */
	private String sTooltip;

	/**
	 * Constructor
	 * 
	 * @param tooltip
	 *            describing, that kind of memento this is.
	 */
	private MementoType(String tooltip) {

		this.sTooltip = tooltip;
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
