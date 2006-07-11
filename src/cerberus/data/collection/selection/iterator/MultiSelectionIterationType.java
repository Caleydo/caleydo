/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.data.collection.selection.iterator;

/**
 * Types of selections.
 * 
 * @author Michael Kalkusch
 *
 * @see prometheus.data.set.SelectionInterface
 */
public enum MultiSelectionIterationType {

	FIRST_TO_LAST_SUCCESSIVE("Abstract Selection, that has not been instaniated"),
	FIRST_COLUMN_EACH_ROW("Abstract Virtual Array, that has not been defined"),
	FIRST_ROW_EACH_COLUMN("Virtual Array of a single block"),
	NONE("No type set");

	/**
	 * Brief description, what the Selection does.
	 */
	private final String sDescription;
	
	/**
	 * Constructor for the Enumeration.
	 * 
	 * @param sSetDescription
	 */
	private MultiSelectionIterationType(String sSetDescription) {
		sDescription = sSetDescription;
	}
	
	public String getDescription() {
		return sDescription;
	}
}
