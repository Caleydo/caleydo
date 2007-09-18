/*
 * @(#)JGraphLayoutSettings.java 1.0 12-JUL-2004
 * 
 * Copyright (c) 2001-2004, Gaudenz Alder
 * All rights reserved. 
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jgraph.plugins.layouts;

/**
 * Allows the algorithm to create an object that
 * may be applied back to the algorithm.
 */
public interface JGraphLayoutSettings {
	
	/**
	 * (Re)read settings from layout algorithm.
	 */
	public void revert();

	/**
	 * Apply the current settings to the layout that
	 * created this object.
	 */
	public void apply();

}
