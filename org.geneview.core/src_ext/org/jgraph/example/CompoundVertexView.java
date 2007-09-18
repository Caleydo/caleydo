package org.jgraph.example;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import org.jgraph.graph.AbstractCellView;
import org.jgraph.graph.CellView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexView;

/**
 * Vertex view that supports visual vertex nesting to show inclusion edges in a
 * compound graph. Differs from a selection group view as follows:
 * <ul>
 * <li>Bounds of a compound vertex view with child views are not determined
 * directly by the child views' bounds, but rather are independently settable
 * like the bounds of a leaf vertex.</li>
 * <li>Scaling a parent compound vertex view does not scale its child views,
 * although translating a parent compound vertex view does translate its child
 * views.</li>
 * </ul>
 * Like any {@link VertexView}, the bounds of a compound vertex view are forced
 * to always remain large enough to enclose its child views. Otherwise, no
 * restrictions are placed on the bounds.
 * 
 * @author J. Pulley
 */
public class CompoundVertexView extends VertexView {

	/**
	 * Initializes a new view for a compound vertex.
	 */
	public CompoundVertexView() {
	}

	/**
	 * Initializes a new view for a compound vertex with the specified model
	 * object.
	 * 
	 * @param cell
	 *            model object
	 */
	public CompoundVertexView(Object cell) {
		super(cell);
	}

	/**
	 * Retrieve this view's bounds from the view's attributes.
	 * 
	 * @return view's bounds
	 */
	public Rectangle2D getBounds() {
		return GraphConstants.getBounds(getAllAttributes());
	}

	/**
	 * Set this view's bounds in the view's attributes. If the new bounds do not
	 * completely enclose any child vertices, the new bounds are set to the
	 * {@link Rectangle2D#createUnion(Rectangle2D) union} of the child vertices'
	 * bounds and the argument.
	 * 
	 * @param newBounds
	 *            new bounds
	 */
	public void setBounds(Rectangle2D newBounds) {
		GraphConstants.setBounds(getAllAttributes(), newBounds);
		checkChildBounds();
	}

	/**
	 * Update attributes for this view and indicate to the parent this child has
	 * been updated.
	 */
	public void update() {
		super.update();
		checkChildBounds();
	}

	/**
	 * Translate this view and all child views by <code>dx, dy</code>.
	 * 
	 * @param dx
	 *            x-axis translation
	 * @param dy
	 *            y-axis translation
	 */
	public void translate(double dx, double dy) {
		getAllAttributes().translate(dx, dy);
		int moveableAxis = GraphConstants.getMoveableAxis(getAllAttributes());
		if (moveableAxis == GraphConstants.X_AXIS) {
			dy = 0;
		} else if (moveableAxis == GraphConstants.Y_AXIS) {
			dx = 0;
		}
		Iterator it = childViews.iterator();
		while (it.hasNext()) {
			Object view = it.next();
			if (view instanceof AbstractCellView) {
				AbstractCellView child = (AbstractCellView) view;
				child.translate(dx, dy);
			}
		}
	}

	/**
	 * Scale this view by <code>sx</code> and <code>sy</code>, relative to
	 * <code>origin</code>. Child views are not scaled.
	 * 
	 * @param sx
	 *            x scaling factor
	 * @param sy
	 *            y scaling factor
	 */
	public void scale(double sx, double sy, Point2D origin) {
		getAllAttributes().scale(sx, sy, origin);
		checkChildBounds();
	}

	/**
	 * If this view's current bounds do not completely enclose all child vertex
	 * views, sets this view's bounds to the union of the current bounds and the
	 * childrens' bounds.
	 */
	private void checkChildBounds() {
		if (!isLeaf()) {
			Rectangle2D bounds = GraphConstants.getBounds(getAllAttributes());
			if (bounds == null) {
				bounds = new Rectangle2D.Double();
			}
			CellView[] childViewArray = (CellView[]) childViews
					.toArray(new CellView[0]);
			Rectangle2D childBounds = AbstractCellView
					.getBounds(childViewArray);
			if (!bounds.contains(childBounds)) {
				bounds = bounds.createUnion(childBounds);
				GraphConstants.setBounds(getAllAttributes(), bounds);
			}
		}
	}
}
