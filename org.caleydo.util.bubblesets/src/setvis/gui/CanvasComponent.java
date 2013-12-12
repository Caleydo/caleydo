/**
 *
 */
package setvis.gui;

import gleem.linalg.Vec2f;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.media.opengl.glu.GLUtessellator;
import javax.swing.JComponent;

import org.caleydo.core.data.selection.SelectionType;

import setvis.SetOutline;
import setvis.bubbleset.BubbleSet;
import setvis.ch.ConvexHull;
import setvis.shape.AbstractShapeGenerator;
import setvis.shape.BSplineShapeGenerator;
import setvis.shape.BezierShapeGenerator;
import setvis.shape.PolygonShapeGenerator;
import setvis.shape.ShapeSimplifier;

/**
 * The component for maintaining and displaying the rectangles.
 *
 * @author Joschi <josua.krause@googlemail.com>
 *
 */
public class CanvasComponent extends JComponent implements Canvas {

	/**
	 * A class to identify a given rectangle.
	 *
	 * @author Joschi <josua.krause@googlemail.com>
	 *
	 */
	public class Position {
		/**
		 * The group in which the rectangle is.
		 */
		public final int groupID;

		/**
		 * The rectangle. This is the actual reference, so that modifying this rectangle results in modifying the
		 * original rectangle.
		 */
		public final Rectangle2D rect;

		/**
		 * Generates a Position object.
		 *
		 * @param groupID
		 *            The group id.
		 * @param rect
		 *            The reference to the rectangle.
		 */
		private Position(final int groupID, final Rectangle2D rect) {
			this.groupID = groupID;
			this.rect = rect;
		}
	}

	// serial version uid
	private static final long serialVersionUID = -310139729093190621L;

	/**
	 * The list of canvas listeners.
	 */
	private final List<CanvasListener> canvasListeners;

	/**
	 * A list of all groups containing lists of the group members.
	 */
	private final List<List<Rectangle2D>> items;
	private final List<List<Line2D>> edges;
	private final List<Color> colorList;
	private final List<Integer> outlineThickness;
	private final List<Boolean> isVisibleList;

	protected GLUtessellator tobj;

	/**
	 * The mouse and mouse motion listener for the interaction.
	 */
	private final MouseAdapter mouse = new MouseAdapter() {

		@Override
		public void mouseClicked(final MouseEvent e) {
			final double x = e.getX();
			final double y = e.getY();
			switch (e.getButton()) {
			case MouseEvent.BUTTON1:
				// left click -> shaper item
				if (getItemsAt(x, y).isEmpty()) {
					addItem(curItemGroup, x, y, curItemWidth, curItemHeight);
				}
				break;
			case MouseEvent.BUTTON3:
				// right click -> remove items
				removeItem(x, y);
				break;
			}
			invalidateOutlines(CanvasListener.ITEMS);
		}

		// the last mouse position
		private Point p = null;

		// the items to move or an empty list if the background is moved
		private List<Position> items = null;

		@Override
		public void mousePressed(final MouseEvent e) {
			// only left click dragging counts
			p = e.getButton() == MouseEvent.BUTTON1 ? e.getPoint() : null;
			if (p == null) {
				return;
			}
			items = getItemsAt(p.x, p.y);
		}

		@Override
		public void mouseDragged(final MouseEvent e) {
			if (p == null) {
				return;
			}
			final Point n = e.getPoint();
			move(p, n);
			p = n;
		}

		@Override
		public void mouseReleased(final MouseEvent e) {
			final Point n = e.getPoint();
			if (p == null || p.equals(n)) {
				return;
			}
			move(p, n);
			p = null;
		}

		/**
		 * Moves {@link #items} by the difference of the given positions.
		 *
		 * @param from
		 *            The previous position.
		 * @param to
		 *            The position.
		 */
		private void move(final Point from, final Point to) {
			final double dx = to.x - from.x;
			final double dy = to.y - from.y;
			if (items.isEmpty()) {
				translateScene(dx, dy);
			} else {
				for (final Position p : items) {
					moveItem(p, dx, dy);
				}
				invalidateOutlines(CanvasListener.ITEMS);
			}
			repaint();
		}

		@Override
		public void mouseWheelMoved(final MouseWheelEvent e) {
			zoomTo(e.getX(), e.getY(), e.getWheelRotation());
		}

	};

	/**
	 * The generator of the shapes of the sets.
	 */
	private AbstractShapeGenerator shaper;

	/**
	 * The cached shapes of the outlines.
	 */
	private Shape[] groupShapes;

	/**
	 * The current group new rectangles will be added to.
	 */
	private int curItemGroup;

	/**
	 * The width new rectangles will get.
	 */
	private int curItemWidth;

	/**
	 * The height new rectangles will get.
	 */
	private int curItemHeight;

	/**
	 * The scene translation on the x axis.
	 */
	private double dx;

	/**
	 * The scene translation on the y axis.
	 */
	private double dy;

	/**
	 * The scene zoom factor.
	 */
	private double zoom;

	private int selectionID;

	/**
	 * Creates a canvas component.
	 *
	 * @param shaper
	 *            The shape generator for the outlines.
	 */
	public CanvasComponent(final AbstractShapeGenerator shaper) {
		this.shaper = shaper;
		canvasListeners = new LinkedList<CanvasListener>();
		items = new ArrayList<List<Rectangle2D>>();
		edges = new ArrayList<List<Line2D>>();
		colorList = new ArrayList<Color>();
		outlineThickness = new ArrayList<Integer>();
		isVisibleList = new ArrayList<Boolean>();
		addGroup(new Color(1, 0, 0), 1, true);
		dx = 0.0;
		dy = 0.0;
		zoom = 1.0;
		curItemGroup = 0;
		curItemWidth = 50;
		curItemHeight = 30;
		addMouseListener(mouse);
		addMouseMotionListener(mouse);
		addMouseWheelListener(mouse);
		selectionID = 0;
	}

	@Override
	public void setShapeAndOutline(final OutlineType outlineType, final ShapeType shapeType) {
		final SetOutline oldOut = shaper.getSetOutline();
		final OutlineType oldType = OutlineType.getFor(oldOut);
		SetOutline outline;
		if (oldType == outlineType) {
			final ShapeType oldShapeType = ShapeType.getFor(shaper);
			if (oldShapeType == shapeType) {
				return;
			}
			outline = oldOut;
		} else {
			switch (outlineType) {
			case BUBBLE_SETS:
				outline = new BubbleSet();
				break;
			case CONVEX_HULL:
				outline = new ConvexHull();
				break;
			default:
				throw new InternalError("" + outlineType);
			}
		}
		setShapeAndOutline(outline, shapeType);
	}

	@Override
	public void setShapeAndOutline(final SetOutline outline, final ShapeType shapeType) {
		AbstractShapeGenerator asc;
		switch (shapeType) {
		case LINES:
			asc = new PolygonShapeGenerator(outline);
			break;
		case BEZIER:
			asc = new BezierShapeGenerator(outline);
			break;
		case BSPLINE:
			// TODO: add own slider
			if (simplifyTolerance < 0.0) {
				asc = new BSplineShapeGenerator(outline);
			} else {
				asc = new BSplineShapeGenerator(new ShapeSimplifier(new PolygonShapeGenerator(outline),
						simplifyTolerance));
			}
			break;
		default:
			throw new InternalError("" + shapeType);
		}
		setShapeCreator(asc);
	}

	@Override
	public void setShapeCreator(final AbstractShapeGenerator shaper) {
		this.shaper = shaper;
		invalidateOutlines(CanvasListener.GENERATORS);
	}

	@Override
	public AbstractShapeGenerator getShapeCreator() {
		return shaper;
	}

	@Override
	public void setShapeBorder(final double border) {
		getShapeCreator().setRadius(border);
		invalidateOutlines(CanvasListener.GENERATORS);
	}

	@Override
	public double getShapeBorder() {
		return getShapeCreator().getRadius();
	}

	@Override
	public void translateScene(final double dx, final double dy) {
		this.dx += dx;
		this.dy += dy;
		notifyCanvasListeners(CanvasListener.TRANSLATION);
	}

	/**
	 * Zooms to the on screen (in components coordinates) position.
	 *
	 * @param x
	 *            The x coordinate.
	 * @param y
	 *            The y coordinate.
	 * @param zooming
	 *            The amount of zooming.
	 */
	public void zoomTo(final double x, final double y, final int zooming) {
		zoomTo(x, y, Math.pow(1.1, -zooming));
	}

	@Override
	public void zoomTo(final double x, final double y, final double factor) {
		// P = (off - mouse) / zoom
		// P = (newOff - mouse) / newZoom
		// newOff = (off - mouse) / zoom * newZoom + mouse
		// newOff = (off - mouse) * factor + mouse
		zoom *= factor;
		// set the offset directly
		dx = (dx - x) * factor + x;
		dy = (dy - y) * factor + y;
		invalidateOutlines(CanvasListener.SCREEN);
	}

	@Override
	public void zoom(final double factor) {
		final Dimension dim = getSize();
		zoomTo(dim.width / 2.0, dim.height / 2.0, factor);
	}

	/**
	 * The default x offset.
	 */
	private double defaultDx = 0.0;

	/**
	 * The default y offset.
	 */
	private double defaultDy = 0.0;

	/**
	 * The default zoom.
	 */
	private double defaultZoom = 1.0;

	@Override
	public void defaultView() {
		dx = defaultDx;
		dy = defaultDy;
		zoom = defaultZoom;
		invalidateOutlines(CanvasListener.SCREEN);
	}

	@Override
	public void setDefaultView() {
		defaultDx = dx;
		defaultDy = dy;
		defaultZoom = zoom;
	}

	@Override
	public void addCanvasListener(final CanvasListener listener) {
		if (listener == null) {
			throw new NullPointerException("listener");
		}
		canvasListeners.add(listener);
		listener.canvasChanged(CanvasListener.ALL);
	}

	@Override
	public void removeCanvasListener(final CanvasListener listener) {
		canvasListeners.remove(listener);
	}

	/**
	 * Notifies all canvas listeners.
	 *
	 * @param changes
	 *            The changes of the canvas as defined in {@link CanvasListener#canvasChanged(int)}.
	 */
	protected void notifyCanvasListeners(final int changes) {
		for (final CanvasListener cl : canvasListeners) {
			cl.canvasChanged(changes);
		}
	}

	@Override
	public void fireCanvasChange(final int changes) {
		invalidateOutlines(changes);
	}

	/**
	 * Signalizes that something has changed. This results in clearing the outline cache, notifying the parent and a
	 * call to {@link #repaint()}.
	 *
	 * @param changes
	 *            The changes of the canvas as defined in {@link CanvasListener#canvasChanged(int)}.
	 */
	protected void invalidateOutlines(final int changes) {
		groupShapes = null;
		notifyCanvasListeners(changes);
		repaint();
	}

	@Override
	public void addGroup() {
		curItemGroup = items.size();
		items.add(new LinkedList<Rectangle2D>());
		edges.add(new LinkedList<Line2D>());
		colorList.add(new Color(0, 1, 0));// default Color
		isVisibleList.add(true);
		invalidateOutlines(CanvasListener.GROUPS);
	}

	public void addGroup(Color aColor, Integer borderThickness, boolean isVisible) {
		curItemGroup = items.size();
		items.add(new LinkedList<Rectangle2D>());
		edges.add(new LinkedList<Line2D>());
		outlineThickness.add(borderThickness);
		colorList.add(aColor);
		isVisibleList.add(isVisible);
		invalidateOutlines(CanvasListener.GROUPS);
	}

	@Override
	public void removeLastGroup() {
		final int last = items.size() - 1;
		if (last == 0) {
			return;
		}
		items.remove(last);
		edges.remove(last);
		colorList.remove(last);
		outlineThickness.remove(last);
		isVisibleList.remove(last);
		if (curItemGroup == last) {
			curItemGroup = 0;
		}
		invalidateOutlines(CanvasListener.GROUPS);
	}

	@Override
	public void removeSelectedGroup() {
		if (items.size() <= 1) {
			return;
		}
		items.remove(curItemGroup);
		edges.remove(curItemGroup);
		colorList.remove(curItemGroup);
		outlineThickness.remove(curItemGroup);
		isVisibleList.remove(curItemGroup);
		--curItemGroup;
		if (curItemGroup < 0) {
			curItemGroup = 0;
		}
		invalidateOutlines(CanvasListener.GROUPS);
	}

	public void clearCurrentGroup() {
		items.get(curItemGroup).clear();
		edges.get(curItemGroup).clear();
	}

	/**
	 * removes current group without updating the outline. this allows to remove ALL groups
	 */
	public void removeCurrentGroup() {
		if (items.size() < 1) {
			return;
		}
		items.remove(curItemGroup);
		edges.remove(curItemGroup);
		colorList.remove(curItemGroup);
		outlineThickness.remove(curItemGroup);
		isVisibleList.remove(curItemGroup);
		--curItemGroup;
	}

	public void removeAllGroups() {
		int numberOfGroups = getGroupCount() - 1;
		while (getGroupCount() > 0) {
			setCurrentGroup(numberOfGroups);
			removeCurrentGroup();
			numberOfGroups--;
		}
	}

	@Override
	public void setCurrentGroup(final int curItemGroup) {
		this.curItemGroup = curItemGroup;
		notifyCanvasListeners(CanvasListener.GROUPS);
	}

	@Override
	public int getCurrentGroup() {
		return curItemGroup;
	}

	@Override
	public int getGroupCount() {
		return items.size();
	}

	@Override
	public void setCurrentItemWidth(final int curItemWidth) {
		this.curItemWidth = curItemWidth;
		notifyCanvasListeners(CanvasListener.RECT_SIZE);
	}

	@Override
	public int getCurrentItemWidth() {
		return curItemWidth;
	}

	@Override
	public void setCurrentItemHeight(final int curItemHeight) {
		this.curItemHeight = curItemHeight;
		notifyCanvasListeners(CanvasListener.RECT_SIZE);
	}

	@Override
	public int getCurrentItemHeight() {
		return curItemHeight;
	}

	/**
	 * Calculates the real coordinate of the given input in screen coordinates.
	 *
	 * @param s
	 *            The coordinate in screen coordinates. Due to uniform zooming both horizontal and vertical coordinates
	 *            can be converted.
	 * @return In real coordinates.
	 */
	protected double inReal(final double s) {
		return s / zoom;
	}

	/**
	 * Calculates the real coordinate from the components coordinate.
	 *
	 * @param x
	 *            The components x coordinate.
	 * @return The real coordinate.
	 */
	public double getXForScreen(final double x) {
		return inReal(x - dx);
	}

	/**
	 * Calculates the real coordinate from the components coordinate.
	 *
	 * @param y
	 *            The components y coordinate.
	 * @return The real coordinate.
	 */
	public double getYForScreen(final double y) {
		return inReal(y - dy);
	}

	@Override
	public void addEdge(final int groupID, final double startX, final double startY, final double endX,
			final double endY) {
		final double sx = getXForScreen(startX);
		final double sy = getYForScreen(startY);
		final double ex = getXForScreen(endX);
		final double ey = getYForScreen(endY);

		final List<Line2D> group = edges.get(groupID);
		group.add(new Line2D.Double(sx, sy, ex, ey));
		notifyCanvasListeners(CanvasListener.ITEMS);
	}

	@Override
	public void addItem(final int groupID, final double tx, final double ty, final double width, final double height) {
		final double x = getXForScreen(tx);
		final double y = getYForScreen(ty);
		final List<Rectangle2D> group = items.get(groupID);
		group.add(new Rectangle2D.Double(x - width * 0.5, y - height * 0.5, width, height));
		notifyCanvasListeners(CanvasListener.ITEMS);
	}

	public void resolveEdgeIntersections(HashSet<Rectangle2D> noneBSetRects) {
		// check all edges if they intersect with any of the noneBSetsRects
		Iterator iter = noneBSetRects.iterator();
		while (iter.hasNext()) {
			Rectangle2D noneBSetRect = (Rectangle2D) iter.next();
			for (int i = 0; i < edges.size(); i++) {
				List<Line2D> edgeList_i = edges.get(i);
				List<Line2D> newEdges = new LinkedList<Line2D>();
				for (int j = 0; j < edgeList_i.size(); j++) {
					Line2D edge = edgeList_i.get(j);
					// test intersection for edge and noneBSetRect
					if (noneBSetRect.intersectsLine(edge)) {
						System.out.println(" ---- ---- --- -- found Intersecting Rectangle \n");
						//
						double sx, sy, ex, ey;
						Line2D nEdge1;
						Line2D nEdge2;
						Line2D nEdge3;

						if (edge.getY1() > noneBSetRect.getX()) {
							sx = edge.getX1();
							sy = edge.getY1();
							ex = getXForScreen(noneBSetRect.getX());
							ey = getYForScreen(noneBSetRect.getY());
							nEdge1 = new Line2D.Double(sx, sy, ex, ey);
							//
							sx = getXForScreen(noneBSetRect.getX());
							sy = getYForScreen(noneBSetRect.getY());
							ex = getXForScreen(noneBSetRect.getX());
							ey = getYForScreen(noneBSetRect.getY()) - (noneBSetRect.getHeight());
							nEdge2 = new Line2D.Double(sx, sy, ex, ey);
							//
							sx = noneBSetRect.getX();
							sy = noneBSetRect.getY() - (noneBSetRect.getHeight());
							ex = edge.getX2();
							ey = edge.getY2();
							nEdge3 = new Line2D.Double(sx, sy, ex, ey);
						} else {
							sx = edge.getX2();
							sy = edge.getY2();
							ex = getXForScreen(noneBSetRect.getX());
							ey = getYForScreen(noneBSetRect.getY());// -noneBSetRect.getHeight();
							nEdge1 = new Line2D.Double(sx, sy, ex, ey);
							//
							sx = getXForScreen(noneBSetRect.getX());
							sy = getYForScreen(noneBSetRect.getY());
							ex = getXForScreen(noneBSetRect.getX());
							ey = getYForScreen(noneBSetRect.getY()) + noneBSetRect.getHeight();
							nEdge2 = new Line2D.Double(sx, sy, ex, ey);
							//
							sx = getXForScreen(noneBSetRect.getX());
							sy = getYForScreen(noneBSetRect.getY()) + noneBSetRect.getHeight();
							ex = edge.getX1();
							ey = edge.getY1();
							nEdge3 = new Line2D.Double(sx, sy, ex, ey);
						}
						//
						newEdges.add(nEdge1);
						newEdges.add(nEdge2);
						newEdges.add(nEdge3);
					} else {
						newEdges.add(edge);
					}
				}// for loop
				edges.set(i, newEdges);
			}// for edges.size
		}// while
	}

	@Override
	public List<Position> getItemsAt(final double tx, final double ty) {
		final double x = getXForScreen(tx);
		final double y = getYForScreen(ty);
		final List<Position> res = new LinkedList<Position>();
		int groupID = 0;
		for (final List<Rectangle2D> group : items) {
			for (final Rectangle2D r : group.toArray(new Rectangle2D[group.size()])) {
				if (r.contains(x, y)) {
					res.add(new Position(groupID, r));
				}
			}
			++groupID;
		}
		return res;
	}

	@Override
	public void removeItem(final double x, final double y) {
		final List<Position> pos = getItemsAt(x, y);
		for (final Position p : pos) {
			final List<Rectangle2D> group = items.get(p.groupID);
			group.remove(p.rect);
		}
		notifyCanvasListeners(CanvasListener.ITEMS);
	}

	@Override
	public void moveItem(final Position pos, final double dx, final double dy) {
		final Rectangle2D r = pos.rect;
		r.setRect(r.getMinX() + inReal(dx), r.getMinY() + inReal(dy), r.getWidth(), r.getHeight());
		notifyCanvasListeners(CanvasListener.ITEMS);
	}

	// the java creation code
	private String javaText = "";

	@Override
	public String getCreationText() {
		return javaText;
	}

	// the info text
	private String infoText = "";

	@Override
	public String getInfoText() {
		return infoText;
	}

	public void setSelection(int id) {
		selectionID = id;
	}

	private Color selectionColor = SelectionType.SELECTION.getColor().getAWTColor();

	public void setSelectionColor(float[] newSelectionColor) {
		selectionColor = new Color(newSelectionColor[0], newSelectionColor[1], newSelectionColor[2]);
	}

	@Override
	public void paint(final Graphics gfx) {
		boolean textChanged = false;
		final Graphics2D g2d = (Graphics2D) gfx;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		final int count = getGroupCount();
		if (groupShapes == null) {
			// the cache needs to be recreated
			final AbstractShapeGenerator shaping = new ShapeSimplifier(shaper, simplifyTolerance);
			groupShapes = shaping.createShapesForLists(items, edges);
			final StringBuilder sb = new StringBuilder();
			ShapeType.creationText(shaping, sb);
			javaText = sb.toString();
			textChanged = true;
		}
		// draw background
		g2d.setColor(Color.WHITE);
		final Rectangle2D r = getBounds();
		g2d.fillRect(0, 0, (int) r.getWidth() - 1, (int) r.getHeight() - 1);
		// translate the scene
		g2d.translate(dx, dy);
		// zoom the scene
		g2d.scale(zoom, zoom);
		final float step = 1f / count;
		float hue = 0f;
		int pos = 0;
		int controlPoints = 0;
		// draw the outlines
		for (int i = items.size() - 1; i >= 0; i--) {
			if (i == selectionID)
				continue;
			if (!isVisibleList.get(i))
				continue;
			// for (int i =0; i < items.size() ; ++i) {
			// final Color c = new Color(Color.HSBtoRGB(hue, 0.7f, 1f));
			final Color c = colorList.get(i);
			final Color t = new Color(~0x80000000 & c.getRGB(), true);
			// final Color t = colorList.get(i);//new Color(~0x80000000 & c.getRGB(), true);

			final Shape gs = groupShapes[i];
			if (gs != null) {
				g2d.setColor(t);
				g2d.fill(gs);
				g2d.setColor(c);
				g2d.setStroke(new BasicStroke(outlineThickness.get(i)));
				g2d.draw(gs);
				if (drawPoints) {
					g2d.setColor(Color.BLACK);
					final PathIterator pi = gs.getPathIterator(new AffineTransform());
					final double[] coords = new double[6];
					while (!pi.isDone()) {
						pi.currentSegment(coords);
						g2d.fill(new Rectangle2D.Double(coords[0] - 0.5, coords[1] - 0.5, 1, 1));
						pi.next();
						++controlPoints;
					}
				}
			}
			hue += step;
			++pos;
		}

		// render selected set
		if (selectionID >= 0 && isVisibleList.get(selectionID)) {
			// final Color c = colorList.get(selectionID);
			final Color c = selectionColor;
			final Color t = new Color(~0x80000000 & c.getRGB(), true);
			// final Color t = colorList.get(i);//new Color(~0x80000000 & c.getRGB(), true);

			final Shape gs = groupShapes[selectionID];
			if (gs != null) {
				g2d.setColor(t);
				g2d.fill(gs);
				g2d.setColor(c);
				g2d.setStroke(new BasicStroke(outlineThickness.get(selectionID)));
				g2d.draw(gs);
				if (drawPoints) {
					g2d.setColor(Color.BLACK);
					final PathIterator pi = gs.getPathIterator(new AffineTransform());
					final double[] coords = new double[6];
					while (!pi.isDone()) {
						pi.currentSegment(coords);
						g2d.fill(new Rectangle2D.Double(coords[0] - 0.5, coords[1] - 0.5, 1, 1));
						pi.next();
						++controlPoints;
					}
				}
			}
		} else {
			// System.out.println("isVisibleList.get(selectionID) = false");
		}
		// hue = 0f;
		// pos = 0;
		// int rects = 0;
		// draw the items
		// for (final List<Rectangle2D> group : items) {
		// final Color c = new Color(Color.HSBtoRGB(hue, 0.7f, 1f));
		// g2d.setColor(c);
		// for (final Rectangle2D item : group) {
		// final Graphics2D g = (Graphics2D) g2d.create();
		// g.setStroke(new BasicStroke(3));
		// final int w = (int) item.getWidth();
		// final int h = (int) item.getHeight();
		// g.translate(item.getMinX(), item.getMinY());
		// g.fillRect(0, 0, w, h);
		// g.setColor(Color.BLACK);
		// g.drawRect(0, 0, w, h);
		// g.dispose();
		// }
		// hue += step;
		// ++pos;
		// rects += group.size();
		// }
		// // draw edges
		// for (final List<Line2D> group : edges) {
		// for (final Line2D edge : group) {
		// final Graphics2D g = (Graphics2D) g2d.create();
		// g.setColor(Color.RED);
		// g.setStroke(new BasicStroke(5));
		// g.draw(edge);
		// g.dispose();
		// }
		// hue += step;
		// ++pos;
		// rects += group.size();
		// }
		// final String info = "Groups: " + items.size() + " Items: " + rects
		// + (controlPoints > 0 ? " Points: " + controlPoints : "");
		// if (!infoText.equals(info)) {
		// infoText = info;
		// textChanged = true;
		// }
		// if (textChanged) {
		// notifyCanvasListeners(CanvasListener.TEXT);
		// }
	}

	public List<Vec2f> getShapePoints(final Graphics gfx) {
		if (groupShapes == null) {
			// the cache needs to be recreated
			final AbstractShapeGenerator shaping = new ShapeSimplifier(shaper, simplifyTolerance);
			groupShapes = shaping.createShapesForLists(items, edges);
		}

		final Shape gs = groupShapes[selectionID];
		AffineTransform tf = new AffineTransform();
		PathIterator pi = gs.getPathIterator(tf);
		float[] position = new float[6];
		int type = -1;
		List<Vec2f> points = new ArrayList<>();

		if (gs != null) {
			while (!pi.isDone() && type != PathIterator.SEG_CLOSE) {
				pi.next();
				type = pi.currentSegment(position);
				points.add(new Vec2f(position[0], position[1]));
			}
		}

		return points;
	}

	// whether to draw points
	private boolean drawPoints;

	@Override
	public boolean isDrawingPoints() {
		return drawPoints;
	}

	@Override
	public void setDrawPoints(final boolean drawPoints) {
		this.drawPoints = drawPoints;
		invalidateOutlines(CanvasListener.SCREEN);
	}

	// the tolerance of simplification
	private double simplifyTolerance;

	@Override
	public double getTolerance() {
		return simplifyTolerance;
	}

	@Override
	public void setTolerance(final double tolerance) {
		final boolean chg = simplifyTolerance != tolerance;
		simplifyTolerance = tolerance;
		if (chg) {
			invalidateOutlines(CanvasListener.GENERATORS);
		}
	}

}
