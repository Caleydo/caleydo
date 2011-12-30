package org.caleydo.view.datagraph.node;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import javax.media.opengl.GL2;
import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;
import org.caleydo.view.datagraph.layout.AGraphLayout;

public interface IDataGraphNode extends IDraggable {

	public List<DataContainer> getDataContainers();

	public boolean showsDataContainers();

	public void render(GL2 gl);

	public int getHeightPixels();

	public int getWidthPixels();

	public float getHeight();

	public float getWidth();

	public Pair<Point2D, Point2D> getBottomDataContainerAnchorPoints(
			DataContainer dataContainer);

	public Pair<Point2D, Point2D> getTopDataContainerAnchorPoints(
			DataContainer dataContainer);

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

	public Point2D getAbsolutPositionOfRelativeDataContainerRendererCoordinates(
			Point2D coordinates);

	public float getSpacingX(IDataGraphNode node);

	public float getSpacingY(IDataGraphNode node);

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
