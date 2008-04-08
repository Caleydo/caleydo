/*******************************************************************************
 * Copyright (c) 2004, 2005 Jean-Michel Lemieux, Jeff McAffer and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Hyperbola is an RCP application developed for the book 
 *     Eclipse Rich Client Platform - 
 *         Designing, Coding, and Packaging Java Applications 
 *
 * Contributors:
 *     Jean-Michel Lemieux and Jeff McAffer - initial implementation
 *******************************************************************************/
package org.geneview.rcp.model;

public class Presence {
	public static final Presence ONLINE = new Presence("Online");

	public static final Presence AWAY = new Presence("Away");

	public static final Presence DO_NOT_DISTURB = new Presence("Do Not Disturb");

	public static final Presence INVISIBLE = new Presence("Offline");

	private String value;

	private Presence(String value) {
		this.value = value;
	}

	public String toString() {
		return value;
	}
}
