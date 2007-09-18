/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package org.geneview.core.data.collection.virtualarray.iterator;

//import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.util.IGeneViewDefaultType;

/**
 * Types of selections.
 * 
 * @author Michael Kalkusch
 *
 * @see prometheus.data.set.SelectionInterface
 */
public enum MultiVirtualArrayIterationType 
implements IGeneViewDefaultType <MultiVirtualArrayIterationType> {

	FIRST_TO_LAST_SUCCESSIVE("Abstract IVirtualArray, that has not been instaniated"),
	FIRST_COLUMN_EACH_ROW("Abstract Virtual Array, that has not been defined"),
	FIRST_ROW_EACH_COLUMN("Virtual Array of a single block"),
	NONE("No type set");

	/**
	 * Brief description, what the IVirtualArray does.
	 */
	private final String sDescription;
	
	/**
	 * Constructor for the Enumeration.
	 * 
	 * @param sSetDescription
	 */
	private MultiVirtualArrayIterationType(String sSetDescription) {
		sDescription = sSetDescription;
	}
	
	public String getDescription() {
		return sDescription;
	}

	/*
	 * (non-Javadoc)
	 * @see cerberus.util.IGeneViewDefaultType#getTypeDefault()
	 */
	public MultiVirtualArrayIterationType getTypeDefault() {

		return MultiVirtualArrayIterationType.FIRST_COLUMN_EACH_ROW;
	}
}
