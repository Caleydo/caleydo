package org.caleydo.core.view;

import java.util.List;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.IDataDomainBasedView;

/**
 * Interface for views that use {@link ATableBasedDataDomain}, and {@link DataContainer}s meaning that they show data stored in
 * {@link DataTable}s
 * 
 * @author Alexander Lex
 */
public interface ITableBasedDataDomainView
	extends IDataDomainBasedView<ATableBasedDataDomain>, IView, IDataContainerBasedView {



}
