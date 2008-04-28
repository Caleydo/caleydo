package org.caleydo.core.command.memento;

/**
 * 
 * Objects store their information to restore their state inside this object,
 * by deriving a class.
 * 
 * Part of DesignPattern "IMemento"
 * 
 * @author Michael Kalkusch
 *
 */
public interface IMementoState {
	
	/**
	 * Create reasonable debug information.
	 * 
	 * @return detailed information on the memento data.
	 */
	public String toString();
	
	/**
	 * Shows all sub-structures of any exist.
	 * 
	 * @return information on sub mementos.
	 */
	public String toStringRecursive();
}
