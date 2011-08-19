package org.caleydo.core.view;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.IDataDomainBasedView;

/**
 * Interface for views that use {@link ATableBasedDataDomain}, meaning that they show data stored in
 * {@link DataTable}s
 * 
 * @author Alexander Lex
 */
public interface ITableBasedDataDomainView
	extends IDataDomainBasedView<ATableBasedDataDomain>, IView {

	/**
	 * Set the perspectiveID for the records, thereby defining which perspective the view should use. The
	 * perspective is expected to be registered with the {@link DataTable}
	 * 
	 * @param recordPerspectiveID
	 */
	public void setRecordPerspectiveID(String recordPerspectiveID);

	/**
	 * Set the perspectiveID for the dimensions, thereby defining which perspective the view should use. The
	 * perspective is expected to be registered with the {@link DataTable}
	 * 
	 * @param dimensionPerspectiveID
	 */
	public void setDimensionPerspectiveID(String dimensionPerspectiveID);

}
