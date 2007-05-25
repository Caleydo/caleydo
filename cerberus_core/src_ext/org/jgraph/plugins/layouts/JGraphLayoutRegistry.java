/*
 * @(#)JGraphLayoutRegistry.java 1.0 18-MAY-2004
 * 
 * Copyright (c) 2001-2005, Gaudenz Alder
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

import java.util.ArrayList;

/**
 * @author Gaudenz Alder
 * 
 * Allows to maintain a list of layout algorithms. Contains all default
 * algorithms that ship with this package. This is a singleton class.
 */
public class JGraphLayoutRegistry {

	/**
	 * Contains the shared registry instance
	 */
	protected static JGraphLayoutRegistry sharedJGraphLayoutRegistry = new JGraphLayoutRegistry();

	/**
	 * Class initialization
	 */
	static {
		// Register Local Layouts
		sharedJGraphLayoutRegistry.add(new AnnealingLayoutAlgorithm());
		sharedJGraphLayoutRegistry.add(new CircleGraphLayout());
		sharedJGraphLayoutRegistry.add(new GEMLayoutAlgorithm(
				new AnnealingLayoutAlgorithm(true)));
		sharedJGraphLayoutRegistry.add(new MoenLayoutAlgorithm());
		sharedJGraphLayoutRegistry.add(new RadialTreeLayoutAlgorithm());
		sharedJGraphLayoutRegistry.add(new SpringEmbeddedLayoutAlgorithm());
		sharedJGraphLayoutRegistry.add(new SugiyamaLayoutAlgorithm());
		sharedJGraphLayoutRegistry.add(new TreeLayoutAlgorithm());
		sharedJGraphLayoutRegistry.add(new OrderedTreeLayoutAlgorithm());
	}

	/**
	 * Returns the current registry (singleton)
	 */
	public static JGraphLayoutRegistry getSharedJGraphLayoutRegistry() {
		return sharedJGraphLayoutRegistry;
	}

	/**
	 * Contains the registered layouts
	 */
	protected ArrayList layouts = new ArrayList();

	/**
	 * Register a new Layout
	 */
	public void add(JGraphLayoutAlgorithm layout) {
		layouts.add(layout);
	}

	/**
	 * @return Returns the layouts.
	 */
	public ArrayList getLayouts() {
		return layouts;
	}

}