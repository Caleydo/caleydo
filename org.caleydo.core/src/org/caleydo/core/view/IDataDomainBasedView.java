/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view;

import org.caleydo.core.data.datadomain.IDataDomain;

/**
 * This is obsolete for {@link ITablePerspectiveBasedView}s. We need to think
 * what we want to do with the others.
 * 
 * @author Alexander Lex
 * 
 * @param <DataDomainType>
 */
@Deprecated
public interface IDataDomainBasedView<DataDomainType extends IDataDomain> {

	/**
	 * Set the data domain which determines the behavior of the view. Attention:
	 * The data domain need not be changed at runtime.
	 * 
	 * @param dataDomain
	 */
	public void setDataDomain(DataDomainType dataDomain);

	/**
	 * Get the data domain the view is operating on
	 * 
	 * @return
	 */
	public DataDomainType getDataDomain();

}
