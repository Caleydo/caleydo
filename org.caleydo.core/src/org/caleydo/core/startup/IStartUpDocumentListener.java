/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.startup;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Listener;

/**
 * a special listener for the {@link SWT#OpenDocument} event
 *
 * @author Samuel Gratzl
 *
 */
public interface IStartUpDocumentListener extends Listener, IStartupAddon {

}
