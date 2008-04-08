/*
 * Copyright (C) 2001-2004 Gaudenz Alder
 *
 * GPGraphpad is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * GPGraphpad is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GPGraphpad; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

package org.jgraph.plugins.gpgraph;

import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JComponent;

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.Edge;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.Port;
import org.jgraph.pad.coreframework.jgraphsubclassers.GPGraphUI;


/**
 * The base class JGraph subclassers for GPGraphpad. It adds some hooks
 * to JGraph and have a few more serializable properties.
 * To use a custom JGraph subcalsser, subclass GPGraph and register
 * your subclass in the Graph factory.
 * @see org.jgraph.pad.coreframework.GPGraphpadModel
 */
public class GPGraph extends JGraph {

    public static String FILE_FORMAT_VERSION = "PAD-1.1";

    /**
     * in order to allow GPGraphpad to be used as a framework where a subclasser
     * can use its own graph instances (subcalssing GPGraph), this constructor
     * HAS BEEN TURNED PROTECTED and should be called from outside via the
     * public overridable
     * 
     * @see org.jgraph.pad.coreframework.GPGraphpad#createGraphInstance() method. Finally setters
     *      should be used to set the model or the view...
     */
    public GPGraph() {
        super();
    }

    /**
     * Required for serialization
     * 
     * @param model
     * @param cache
     */
    public GPGraph(GraphModel model, GraphLayoutCache cache) {
        super(model, cache);
    }

    public static void addSampleData(GraphModel model) {
    }

    //
    // Defines Semantics of the Graph
    //

    /**
     * Returns true if <code>object</code> is a vertex, that is, if it is not
     * an instance of Port or Edge, and all of its children are ports, or it has
     * no children.
     */
    public boolean isGroup(Object cell) {
        // Map the Cell to its View
        CellView view = getGraphLayoutCache().getMapping(cell, false);
        if (view != null)
            return !view.isLeaf();
        return false;
    }

    /**
     * Returns true if <code>object</code> is a vertex, that is, if it is not
     * an instance of Port or Edge, and all of its children are ports, or it has
     * no children.
     */
    public boolean isVertex(Object object) {
        if (!(object instanceof Port) && !(object instanceof Edge))
            return !isGroup(object) && object != null;
        return false;
    }

    public Object[] getSelectionVertices() {
        Object[] tmp = getSelectionCells();
        Object[] all = DefaultGraphModel.getDescendants(getModel(), tmp)
                .toArray();
        return getVertices(all);
    }

    public Object[] getVertices(Object[] cells) {
        if (cells != null) {
            ArrayList result = new ArrayList();
            for (int i = 0; i < cells.length; i++)
                if (isVertex(cells[i]))
                    result.add(cells[i]);
            return result.toArray();
        }
        return null;
    }

    public Object[] getSelectionEdges() {
        return getEdges(getSelectionCells());
    }

    public Object[] getAll() {
        return getDescendants(getRoots());
    }

    public Object[] getEdges(Object[] cells) {
        if (cells != null) {
            ArrayList result = new ArrayList();
            for (int i = 0; i < cells.length; i++)
                if (this.getModel().isEdge(cells[i]))
                    result.add(cells[i]);
            return result.toArray();
        }
        return null;
    }

    public Object getNeighbour(Object edge, Object vertex) {
        Object source = this.getModel().getSource(edge);
        if (vertex == source)
            return this.getModel().getTarget(edge);
        return source;
    }

    public CellView getSourceView(Object edge) {
        Object source = this.getModel().getSource(edge);
        return getGraphLayoutCache().getMapping(source, false);
    }

    public CellView getTargetView(Object edge) {
        Object target = this.getModel().getTarget(edge);
        return getGraphLayoutCache().getMapping(target, false);
    }

    public Object[] getEdgesBetween(Object vertex1, Object vertex2) {
        ArrayList result = new ArrayList();
        Set edges = DefaultGraphModel.getEdges(graphModel,
                new Object[] { vertex1 });
        Iterator it = edges.iterator();
        while (it.hasNext()) {
            Object edge = it.next();
            Object source = this.getModel().getSource(edge);
            Object target = this.getModel().getTarget(edge);
            if ((source == vertex1 && target == vertex2)
                    || (source == vertex2 && target == vertex1))
                result.add(edge);
        }
        return result.toArray();
    }

    /**
     * Overrides <code>JComponent</code>'buttonSelect
     * <code>getToolTipText</code> method in order to allow the graph
     * controller to create a tooltip for the topmost cell under the
     * mousepointer. This differs from JTree where the renderers tooltip is
     * used.
     * <p>
     * NOTE: For <code>JGraph</code> to properly display tooltips of its
     * renderers, <code>JGraph</code> must be a registered component with the
     * <code>ToolTipManager</code>. This can be done by invoking
     * <code>ToolTipManager.sharedInstance().registerComponent(graph)</code>.
     * This is not done automatically!
     * 
     * @param event
     *            the <code>MouseEvent</code> that initiated the
     *            <code>ToolTip</code> display
     * @return a string containing the tooltip or <code>null</code> if
     *         <code>event</code> is null
     */
    public String getToolTipText(MouseEvent event) {
        if (event != null) {
            Object cell = getFirstCellForLocation(event.getX(), event.getY());
            if (cell != null) {
                String tmp = convertValueToString(cell);
                String s = "<html>";
                if (tmp != null && tmp.length() > 0)
                    s = s + "<strong>" + tmp + "</strong><br>";
                return s + getToolTipForCell(cell) + "</html>";
            }
        }
        return null;
    }

    protected String getToolTipForCell(Object cell) {
        CellView view = getGraphLayoutCache().getMapping(cell, false);
        String s = "";
        Rectangle2D bounds = view.getBounds();
        if (bounds != null) {
            s = s + "Location: " + bounds.getX() + ", " + bounds.getY()
                    + "<br>";
            s = s + "Size: " + bounds.getX() + ", " + bounds.getY() + "<br>";
        }
        java.util.List points = GraphConstants.getPoints(view.getAttributes());
        if (points != null)
            s = s + "Points: " + points.size() + "<br>";
        if (!(cell instanceof Edge) && !(cell instanceof Port)) {
            s = s + "Children: " + graphModel.getChildCount(cell) + "<br>";
            int n = DefaultGraphModel.getEdges(getModel(),
                    new Object[] { cell }).size();
            s = s + "Edges: " + n;
        } else if (cell instanceof Edge) {
            Edge edge = (Edge) cell;
            Object source = graphModel.getSource(edge);
            if (source != null) {
                String host = convertValueToString(graphModel.getParent(source));
                String port = convertValueToString(source);
                s = s + "Source: " + host + ":" + port + "<br>";
            }
            Object target = graphModel.getTarget(edge);
            if (target != null) {
                String host = convertValueToString(graphModel.getParent(target));
                String port = convertValueToString(target);
                s = s + "Target: " + host + "/" + port + "<br>";
            }
        }
        return s;
    }

    /**
     * Notification from the <code>UIManager</code> that the L&F has changed.
     * Replaces the current UI object with the latest version from the
     * <code>UIManager</code>. Subclassers can override this to support
     * different GraphUIs.
     * 
     * @see JComponent#updateUI
     * 
     */
    public void updateUI() {
        setUI(new GPGraphUI());
        invalidate();
    }

    /**
     * Returns true if the given vertices are conntected by a single edge in
     * this document.
     */
    public boolean isNeighbour(Object v1, Object v2) {
        return DefaultGraphModel.containsEdgeBetween(this.getModel(), v1, v2);
    }

    // Serialization support
    private void readObject(ObjectInputStream s) throws IOException,
            ClassNotFoundException {
        s.defaultReadObject();
    }
}