/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.startup;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.caleydo.core.util.ExtensionUtils;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * @author Samuel Gratzl
 *
 */
public class CacheInitializers {
	private static final Logger log = Logger.create(CacheInitializers.class);
	private static final String EXTENSION_POINT = "org.caleydo.core.CacheInitializer";

	public static void runInitializers(SubMonitor monitor) {
		Collection<IRunnableWithProgress> inits = ExtensionUtils.findImplementation(EXTENSION_POINT, "class",
				IRunnableWithProgress.class);
		monitor.beginTask("Running Initializers", inits.size());
		for (IRunnableWithProgress init : inits) {
			try {
				init.run(monitor.newChild(1, SubMonitor.SUPPRESS_SUBTASK));
			} catch (InvocationTargetException | InterruptedException e) {
				log.error("can't initialize: " + init, e);
			}
		}
		monitor.done();
	}
}
