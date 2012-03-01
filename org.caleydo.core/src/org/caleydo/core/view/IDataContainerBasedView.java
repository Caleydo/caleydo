/**
 * 
 */
package org.caleydo.core.view;

import java.util.List;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.container.DataContainer;

/**
 * Interface for views that manage (multiple) data containers
 * 
 * @author Alexander Lex
 */
public interface IDataContainerBasedView {

	/**
	 * Set the perspectives for the records and dimensions, thereby defining
	 * which perspectives the view should use. The perspective is expected to be
	 * registered with the {@link DataTable}
	 * 
	 * @param recordPerspectiveID
	 * @param dimensionPerspectiveID
	 */
	public void setDataContainer(DataContainer dataContainer);

	/**
	 * Returns all {@link DataContainer}s that this view and all of its remote
	 * views render.
	 * 
	 * @return
	 */
	public List<DataContainer> getDataContainers();
}
