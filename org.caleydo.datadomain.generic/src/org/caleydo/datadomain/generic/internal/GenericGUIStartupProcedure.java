/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.generic.internal;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.startup.ImportStartupProcedure;

import com.google.common.base.Function;

/**
 * Startup procedure for project wizard.
 *
 * @author Marc Streit
 */
public class GenericGUIStartupProcedure extends ImportStartupProcedure {

	@Override
	public boolean run(Function<String, Void> setTitle) {
		DataDomainManager.get().initalizeDataDomain("org.caleydo.datadomain.generic");
		return super.run(setTitle);
	}
}
