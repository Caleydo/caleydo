/*
 * @(#)DefaultCostFunction.java 1.0 12-MAY-2004
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
 *
 */
package org.jgraph.plugins.algebra;

import java.awt.geom.Point2D;

import org.jgraph.graph.CellMapper;
import org.jgraph.graph.CellView;
import org.jgraph.graph.EdgeView;

/**
 * @author Gaudenz Alder
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class DefaultCostFunction implements CostFunction {
	
	protected CellMapper mapper = null;

	public DefaultCostFunction(CellMapper mapper) {
		this.mapper = mapper;
	}
	
	/**
	 *
	 */
	public double getCost(Object cell) {
		CellView view = mapper.getMapping(cell, false);
		return getLength(view);
	}

	/**
	 *
	 */
	public static double getLength(CellView view) {
		double cost = 1;
		if (view instanceof EdgeView) {
			EdgeView edge = (EdgeView) view;
			Point2D last = null, current = null;
			for (int i = 0; i < edge.getPointCount(); i++) {
				current = edge.getPoint(i);
				if (last != null)
					cost += last.distance(current);
				last = current;
			}
		}
		return cost;
	}
}