/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi.node;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.util.base.ILabelProvider;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;
import org.caleydo.view.dvi.layout.AGraphLayout;

public interface IDVINode extends IDraggable, ILabelProvider {

	public List<TablePerspective> getTablePerspectives();

	public boolean showsTablePerspectives();

	public void render(GL2 gl);

	public int getHeightPixels();

	public int getWidthPixels();

	public float getHeight();

	public float getWidth();

	public Pair<Point2D, Point2D> getBottomObjectAnchorPoints(
Object containedObject);

	public Pair<Point2D, Point2D> getTopObjectAnchorPoints(
Object containedObject);

	public Pair<Point2D, Point2D> getTopAnchorPoints();

	public Pair<Point2D, Point2D> getBottomAnchorPoints();

	public Pair<Point2D, Point2D> getLeftAnchorPoints();

	public Pair<Point2D, Point2D> getRightAnchorPoints();

	public Point2D getPosition();

	public int getID();

	public void destroy();

	public void update();

	public Rectangle2D getBoundingBox();

	/**
	 * Initializes the node. This method is intended to be called right after
	 * object construction.
	 */
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
	 * This method should be called whenever there are changes to a node that
	 * affect its size;
	 */
	public void recalculateNodeSize();

}
