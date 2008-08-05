/*******************************************************************************
 * Copyright (c) 2005 Jean-Michel Lemieux, Jeff McAffer and others. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Hyperbola is an RCP application developed for the book Eclipse Rich Client
 * Platform - Designing, Coding, and Packaging Java Applications See
 * http://eclipsercp.org Contributors: Jean-Michel Lemieux and Jeff McAffer -
 * initial API and implementation org.eclipsercp.hyperbola.model.Contact
 *******************************************************************************/
package org.caleydo.rcp.model;

import org.eclipse.core.runtime.PlatformObject;

public abstract class Contact
	extends PlatformObject
{
	public abstract String getName();

	public abstract ContactsGroup getParent();
}
