package org.caleydo.core.data.virtualarray;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.virtualarray.group.Group;

/**
 * Implementors of this interface provide several information about a segment group ({@link Group}). A segment
 * group refers to a segment of a dimension group ({@link ADimensionGroupData}).
 * 
 * @author Partl
 */
public interface ISegmentData {

	/**
	 * @return The data domain of this segment group.
	 */
	public IDataDomain getDataDomain();

	/**
	 * @return The recordVA of this segment group.
	 */
	public RecordVirtualArray getRecordVA();

	/**
	 * @return The group itself.
	 */
	public Group getGroup();

	/**
	 * @return The label of this group.
	 */
	public String getLabel();

}
