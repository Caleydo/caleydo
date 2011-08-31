package org.caleydo.core.data.container;

import org.caleydo.core.data.virtualarray.group.Group;

/**
 * Implementors of this interface provide information about a segment group ({@link Group}). A segment group
 * refers to a segment of a dimension group ({@link ADimensionGroupData}).
 * 
 * @author Partl
 */
public interface ISegmentData
	extends IDataContainer {

	/**
	 * @return The group itself.
	 */
	public Group getGroup();

	/**
	 * @return The label of this group.
	 */
	public String getLabel();

}
