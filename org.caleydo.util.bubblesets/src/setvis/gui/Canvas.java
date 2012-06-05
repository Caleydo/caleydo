package setvis.gui;

import java.util.List;

import setvis.SetOutline;
import setvis.gui.CanvasComponent.Position;
import setvis.shape.AbstractShapeGenerator;
import setvis.shape.ShapeSimplifier;

/**
 * Canvas is a direct interface to the visible canvas. The complete interaction
 * can be done here.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 * 
 */
public interface Canvas {

    /**
     * Adds a canvas listener.
     * 
     * @param listener
     *            The listener to add. If it is <code>null</code> an
     *            {@link NullPointerException} is thrown.
     */
    void addCanvasListener(CanvasListener listener);

    /**
     * @param listener
     *            The canvas listener to remove.
     */
    void removeCanvasListener(CanvasListener listener);

    /**
     * Fires canvas changes and repaint the canvas.
     * 
     * @param changes
     *            The changes as defined in {@link CanvasListener}.
     */
    void fireCanvasChange(int changes);

    /**
     * Sets the shape generator by its type and the outline by an object.
     * 
     * @param outline
     *            The outline generator.
     * @param shapeType
     *            The shape generator type.
     */
    void setShapeAndOutline(SetOutline outline, ShapeType shapeType);

    /**
     * Sets the shape and outline generator by its type.
     * 
     * @param outlineType
     *            The outline generator type.
     * @param shapeType
     *            The shape generator type.
     */
    void setShapeAndOutline(OutlineType outlineType, ShapeType shapeType);

    /**
     * @param shaper
     *            Directly sets the shape generator.
     */
    void setShapeCreator(AbstractShapeGenerator shaper);

    /**
     * @return The current shape generator.
     */
    AbstractShapeGenerator getShapeCreator();

    /**
     * Sets the radius of the shape. You should prefer this method over
     * {@link AbstractShapeGenerator#setRadius(double)}, because it
     * automatically refreshes the canvas and the other method does not.
     * 
     * @param border
     *            The new border size.
     */
    void setShapeBorder(double border);

    /**
     * @return The current shape border size.
     */
    double getShapeBorder();

    /**
     * @param drawPoints
     *            Whether to draw control points.
     */
    void setDrawPoints(boolean drawPoints);

    /**
     * @return Whether control points are drawn.
     */
    boolean isDrawingPoints();

    /**
     * @param tolerance
     *            Sets the tolerance for simplification.
     * 
     * @see ShapeSimplifier
     */
    void setTolerance(double tolerance);

    /**
     * @return The tolerance for simplification.
     * 
     * @see ShapeSimplifier
     */
    double getTolerance();

    /**
     * @return Informative text about the current display.
     */
    String getInfoText();

    /**
     * @return Java creation text.
     */
    String getCreationText();

    /**
     * Translates the whole scene.
     * 
     * @param dx
     *            The translation on the x axis.
     * @param dy
     *            The translation on the y axis.
     */
    void translateScene(double dx, double dy);

    /**
     * Zooms to the on screen (in components coordinates) position.
     * 
     * @param x
     *            The x coordinate.
     * @param y
     *            The y coordinate.
     * @param factor
     *            The factor to alter the zoom level.
     */
    void zoomTo(double x, double y, double factor);

    /**
     * Zooms towards the center of the display area.
     * 
     * @param factor
     *            The zoom factor.
     */
    void zoom(double factor);

    /**
     * Returns the view-port to the default position.
     */
    void defaultView();

    /**
     * Sets the current view-port as the default position.
     */
    void setDefaultView();

    /**
     * Adds a new empty group.
     */
    void addGroup();

    /**
     * Removes the most recently added group.
     */
    void removeLastGroup();

    /**
     * Removes the group denoted by {@link #getCurrentGroup()}.
     */
    void removeSelectedGroup();

    /**
     * Sets the group to which newly created items will be added.
     * 
     * @param curItemGroup
     *            The new group id.
     */
    void setCurrentGroup(int curItemGroup);

    /**
     * @return The current group id. It determines the group of newly created
     *         items.
     */
    int getCurrentGroup();

    /**
     * @return The number of distinct groups.
     */
    int getGroupCount();

    /**
     * Sets the width newly created items will get.
     * 
     * @param curItemWidth
     *            The new width.
     */
    void setCurrentItemWidth(int curItemWidth);

    /**
     * @return The width newly created items will get.
     */
    int getCurrentItemWidth();

    /**
     * Sets the height newly created items will get.
     * 
     * @param curItemHeight
     *            The new height.
     */
    void setCurrentItemHeight(int curItemHeight);

    /**
     * @return The height newly created items will get.
     */
    int getCurrentItemHeight();

    /**
     * Adds a new item to the canvas.
     * 
     * @param groupID
     *            The group id.
     * @param tx
     *            The x position in component coordinates.
     * @param ty
     *            The y position in component coordinates.
     * @param width
     *            The width.
     * @param height
     *            The height.
     */
    void addItem(int groupID, double tx, double ty, double width, double height);

    
    public void addEdge(final int groupID, final double startX, final double startY, 
    										final double endX, final double endY);
    /**
     * Generates a list of all items at the position {@code (tx, ty)}.
     * 
     * @param tx
     *            The x value in component coordinates.
     * @param ty
     *            The y value in component coordinates.
     * @return A list of {@link Position}s.
     */
    List<Position> getItemsAt(double tx, double ty);

    /**
     * Removes all items at the given position.
     * 
     * @param x
     *            The x value in component coordinates.
     * @param y
     *            The y value in component coordinates.
     */
    void removeItem(double x, double y);

    /**
     * Moves the item given by {@code pos}.
     * 
     * @param pos
     *            The identifier for the item.
     * @param dx
     *            The translation on the x axis.
     * @param dy
     *            The translation on the y axis.
     */
    void moveItem(Position pos, double dx, double dy);

}