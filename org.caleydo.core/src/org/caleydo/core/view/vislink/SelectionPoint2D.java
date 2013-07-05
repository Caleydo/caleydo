/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.view.vislink;

import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.swt.graphics.Point;

/**
 * Represents one point in a selection with its 2D canvas or display related coordinates, the related caleydo
 * application specified by its network name (deskoXID) and the providing view's viewID.
 * 
 * @author Werner Puff
 */
public class SelectionPoint2D {

	/** viewID that generated the selection point */
	protected int viewID;

	/** network name of the caleydo application that generated the selection point */
	protected String deskoXID;

	/** x coordinate of the selection point (either canvas or display related) */
	protected int x;

	/** y coordinate of the selection point (either canvas or display related) */
	protected int y;

	/**
	 * Default constructor. Especially needed by JAXB
	 */
	public SelectionPoint2D() {
		deskoXID = null;
		viewID = -1;
		x = 0;
		y = 0;
	}

	/**
	 * Creates a new selection point with the given parameters
	 * 
	 * @param deskoXID
	 *            network name of the generating caleydo application
	 * @param viewID
	 *            viewID of the generating view
	 * @param point
	 *            x/y coordinates of the selection point
	 */
	public SelectionPoint2D(String deskoXID, int viewID, Point point) {
		this.deskoXID = deskoXID;
		this.viewID = viewID;
		this.x = point.x;
		this.y = point.y;
	}

	/**
	 * Gets the network name of the caleydo application that generated the selection point
	 * 
	 * @return network name
	 */
	public String getDeskoXID() {
		return deskoXID;
	}

	/**
	 * Sets the network name of the caleydo application that generated the selection point
	 * 
	 * @param deskoXID
	 *            network name
	 */
	public void setDeskoXID(String deskoXID) {
		this.deskoXID = deskoXID;
	}

	/**
	 * Gets the view id of the view that generated the selection point
	 * 
	 * @return view id
	 */
	public int getViewID() {
		return viewID;
	}

	/**
	 * Sets the view id of the view that generated the selection point
	 * 
	 * @param viewID
	 *            view id
	 */
	public void setViewID(int viewID) {
		this.viewID = viewID;
	}

	/**
	 * Gets the selection point's x coordinate
	 * 
	 * @return x coordinate
	 */
	public int getX() {
		return x;
	}

	/**
	 * Gets the selection point's x coordinate
	 * 
	 * @param x
	 *            x coordinate
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Gets the selection point's y coordinate
	 * 
	 * @return y coordinate
	 */
	public int getY() {
		return y;
	}

	/**
	 * Gets the selection point's y coordinate
	 * 
	 * @param y
	 *            y coordinate
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Gets the x/y coordinates of the selection point as {@link Point}
	 * 
	 * @param point
	 *            x/y coordinates of the selection point
	 */
	@XmlTransient
	public Point getPoint() {
		return new Point(x, y);
	}

	/**
	 * Sets the x/y coordinates of the selection point
	 * 
	 * @param point
	 *            x/y coordinates of the selection point
	 */
	public void setPoint(Point point) {
		this.x = point.x;
		this.y = point.y;
	}

	/**
	 * Provides a {@link String} representation of the selection point
	 * 
	 * @return {@link String} representation
	 */
	@Override
	public String toString() {
		String s = "{'" + deskoXID + "', " + viewID + ", " + "(" + x + ", " + y + ") }";
		return s;
	}
}
