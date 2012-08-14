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
package org.caleydo.view.dvi.node;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;
import org.caleydo.view.dvi.layout.AGraphLayout;

public interface IDVINode extends IDraggable {

	public List<TablePerspective> getTablePerspectives();

	public boolean showsTablePerspectives();

	public void render(GL2 gl);

	public int getHeightPixels();

	public int getWidthPixels();

	public float getHeight();

	public float getWidth();

	public Pair<Point2D, Point2D> getBottomTablePerspectiveAnchorPoints(
			TablePerspective tablePerspective);

	public Pair<Point2D, Point2D> getTopTablePerspectiveAnchorPoints(
			TablePerspective tablePerspective);

	public Pair<Point2D, Point2D> getTopAnchorPoints();

	public Pair<Point2D, Point2D> getBottomAnchorPoints();

	public Pair<Point2D, Point2D> getLeftAnchorPoints();

	public Pair<Point2D, Point2D> getRightAnchorPoints();

	public Point2D getPosition();

	public int getID();

	public void destroy();

	public void update();

	public Rectangle2D getBoundingBox();

	public void init();

	public void setUpsideDown(boolean isUpsideDown);

	public boolean isUpsideDown();

	public boolean isCustomPosition();

	public void setCustomPosition(boolean isCustomPosition);

	public void setGraphLayout(AGraphLayout graphLayout);

	public Point2D getAbsolutPositionOfRelativeTablePerspectiveRendererCoordinates(
			Point2D coordinates);

	public float getSpacingX(IDVINode node);

	public float getSpacingY(IDVINode node);

	/**
	 * @return The caption of this node.
	 */
	public String getCaption();

	/**
	 * This method should be called whenever there are changes to a node that
	 * affect its size;
	 */
	public void recalculateNodeSize();

}
