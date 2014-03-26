/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.info.dataset.spi;

import org.caleydo.core.data.datadomain.IDataDomain;

/**
 * @author Samuel Gratzl
 *
 */
public interface IDataDomainDataSetItem extends IDataSetItem {
	void update(IDataDomain dataDomain);
}
