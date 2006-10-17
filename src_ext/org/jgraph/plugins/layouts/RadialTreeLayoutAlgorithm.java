/*
 * @(#)RadialTreeLayoutAlgorithm.java 1.0 18-MAY-2004
 * 
 * Copyright (c) 2004, Michael J. Lawley
 * All rights reserved. 
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * - Neither the name of JGraph nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific
 *   prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.jgraph.plugins.layouts;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.VertexView;

/**
 * Lays out the nodes in a graph as a radial tree (root at the centre, children in concentric ovals).
 * <p>
 * The layout algorithm is similar to that described in the paper
 * <em>"Radial Tree Graph Drawing Algorithm for Representing Large Hierarchies"</em>
 * by Greg Book and Neeta Keshary.
 * <p>
 * The algorithm is modified from that in the above paper since it
 * contains bugs and the sample code contains major inefficiencies.
 * <p>
 * Since this algorithm needs to be applied to a tree but we have
 * a directed graph, a spanning tree is first constructed then the
 * algorithm is applied to it.
 *
 * @author <a href="mailto:lawley@dstc.edu.au">michael j lawley</a>
 * @since 3.1
 * @version 1.0 init
 */

public class RadialTreeLayoutAlgorithm extends JGraphLayoutAlgorithm {

    /**
     * Property key for specifying maximum width of layout area.
     *
     * If WIDTH is specified, then CENTRE_X and RADIUS_X are
     * calculated from it based on the maximum depth of the
     * spanning tree.
     */
    public static final String KEY_WIDTH = "Width";

    /**
     * Property key for specifying maximum height of layout area.
     *
     * If HEIGHT is specified, then CENTRE_Y and RADIUS_Y are
     * calculated from it based on the maximum depth of the
     * spanning tree.
     */
    public static final String KEY_HEIGHT = "Height";

    /**
     * Property key for specifying the X-axis coordinate of the centre of the layout.
     *
     * If WIDTH is specified, then the supplied value is ignored and
     * CENTRE_X is calculated from it based on the maximum depth of the
     * spanning tree.
     */
    public static final String KEY_CENTRE_X = "CentreX";

    /**
     * Property key for specifying the Y-axis coordinate of the centre of the layout.
     *
     * If HEIGHT is specified, then the supplied value is ignored and
     * CENTRE_Y is calculated from it based on the maximum depth of the
     * spanning tree.
     */
    public static final String KEY_CENTRE_Y = "CentreY";

    /**
     * Property key for specifying the maximum horizontal distance between a parent and child node.
     *
     * If WIDTH is specified, then the supplied value is ignored and
     * RADIUS_X is calculated from it based on the maximum depth of the
     * spanning tree.
     */
    public static final String KEY_RADIUS_X = "RadiusX";

    /**
     * Property key for specifying the maximum vertical distance between a parent and child node.
     *
     * If WIDTH is specified, then the supplied value is ignored and
     * RADIUS_X is calculated from it based on the maximum depth of the
     * spanning tree.
     */
    public static final String KEY_RADIUS_Y = "RadiusY";

    //    public static final String KEY_CONTINUOUS = "Continuous";

    private static final String RADIAL_TREE_VISITED = "RadialTreeVisited";

    private static final double TWO_PI = Math.PI * 2.0;

    protected double RADIUSX;
    protected double RADIUSY;
    protected double ROOTX;
    protected double ROOTY, WIDTH, HEIGHT;

    private JGraph jgraph;

    public RadialTreeLayoutAlgorithm() {
    }

	/**
	 * Returns the name of this algorithm in human
	 * readable form.
	 */
	public String toString() {
		return "Radial Tree";
	}
	
	/**
	 * Get a human readable hint for using this layout.
	 */
	public String getHint() {
		return "Select a tree";
	}
	
	/**
	 * Returns an new instance of SugiyamaLayoutSettings
	 */
	public JGraphLayoutSettings createSettings() {
		return new RadialTreeLayoutSettings(this);
	}
	
    /**
     * Applies a radial tree layout to nodes in the jgraph with respect to the supplied gpConfiguration.
     *
     * @param graph JGraph instance
     * @param dynamic_cells List of all nodes the layout should move
     * @param static_cells List of node the layout should not move but allow for
	 */
    public void run(JGraph graph, Object[] dynamic_cells, Object[] static_cells) {
        this.jgraph = graph;
        if (dynamic_cells == null || dynamic_cells.length == 0)
        	return;
        
        CellView[] selectedCellViews =
        			graph.getGraphLayoutCache().getMapping(dynamic_cells);

        // search all roots
        List roots = getRoots(jgraph, selectedCellViews);

        TreeNode tree = getSpanningTree(selectedCellViews, roots);
        
        if (null == tree) {
        	return;
        }

        double depth = tree.getDepth();

        Rectangle2D bounds = jgraph.getCellBounds(dynamic_cells);
        
        double autoW = WIDTH;
        if ((WIDTH == ROOTX) && (ROOTX == RADIUSX) && (ROOTX == 0)) {
        	autoW = bounds.getWidth();
        }
        
        if (autoW != 0) {
	        ROOTX = autoW / 2.0;
	        RADIUSX = ROOTX / depth;
        }

        double autoH = HEIGHT;
        if ((HEIGHT == ROOTY) && (ROOTY == RADIUSY) && (ROOTY == 0)) {
        	autoH = bounds.getHeight();
        }
        
        if (autoH != 0) {
	        ROOTY = autoH / 2.0;
	        RADIUSY = ROOTY / depth;
        }

        Map viewMap = new HashMap();
        layoutTree0(viewMap, tree);
        jgraph.getGraphLayoutCache().edit(viewMap, null, null, null);
    }
    
    public void setConfiguration(Properties configuration) {
        if (configuration.containsKey(KEY_WIDTH)) {
            WIDTH = Double.parseDouble(configuration.getProperty(KEY_WIDTH));
        } else {
        	if (configuration.containsKey(KEY_CENTRE_X)) {
				ROOTX = Double.parseDouble(configuration.getProperty(KEY_CENTRE_X));
        	} else {
        		throw new IllegalArgumentException("Must specify one of KEY_WIDTH or KEY_CENTRE_X");
        	}
			if (configuration.containsKey(KEY_RADIUS_X)) {
            	RADIUSX = Double.parseDouble(configuration.getProperty(KEY_RADIUS_X));
			} else {
				throw new IllegalArgumentException("Must specify one of KEY_WIDTH or KEY_RADIUS_X");
			}
        }

        if (configuration.containsKey(KEY_HEIGHT)) {
            HEIGHT = Double.parseDouble(configuration.getProperty(KEY_HEIGHT));
        } else {
			if (configuration.containsKey(KEY_CENTRE_Y)) {
				ROOTY = Double.parseDouble(configuration.getProperty(KEY_CENTRE_Y));
			} else {
				throw new IllegalArgumentException("Must specify one of KEY_HEIGHT or KEY_CENTRE_Y");
			}
			if (configuration.containsKey(KEY_RADIUS_Y)) {
				RADIUSY = Double.parseDouble(configuration.getProperty(KEY_RADIUS_Y));
			} else {
				throw new IllegalArgumentException("Must specify one of KEY_WIDTH or KEY_RADIUS_X");
			}
        }
    }

    private void layoutTree0(Map viewMap, TreeNode node) {
        node.angle = 0;
        node.x = ROOTX;
        node.y = ROOTY;
        node.rightBisector = 0;
        node.rightTangent = 0;
        node.leftBisector = TWO_PI;
        node.leftTangent = TWO_PI;

        VertexView view = node.getView();
        if (null != view) {
            placeView(viewMap, view, ROOTX, ROOTY);
        }
        List parent = new ArrayList(1);
        parent.add(node);
        layoutTreeN(viewMap, 1, parent);
    }

    private void layoutTreeN(Map viewMap, int level, List nodes) {
        double i;
        double prevAngle = 0.0;
        TreeNode parent, node, firstParent = null, prevParent = null;
        List parentNodes = new ArrayList();

        Iterator nitr = nodes.iterator();
        while (nitr.hasNext()) {
            parent = (TreeNode) nitr.next();

            List children = parent.getChildren();
            double rightLimit = parent.rightLimit();
            double angleSpace = (parent.leftLimit() - rightLimit) / children.size();

            Iterator itr = children.iterator();
            for (i = 0.5; itr.hasNext(); i++) {
                node = (TreeNode) itr.next();
                VertexView view = node.getView();

                node.angle = rightLimit + (i * angleSpace);
                node.x = ROOTX + ((level * RADIUSX) * Math.cos(node.angle));
                node.y = ROOTY + ((level * RADIUSY) * Math.sin(node.angle));

                placeView(viewMap, view, node.x, node.y);

                // Is it a parent node?
                if (node.hasChildren()) {
                    parentNodes.add(node);

                    if (null == firstParent) {
                        firstParent = node;
                    }

                    // right bisector limit
                    double prevGap = node.angle - prevAngle;
                    node.rightBisector = node.angle - (prevGap / 2.0);
                    if (null != prevParent) {
                        prevParent.leftBisector = node.rightBisector;
                    }

                    double arcAngle = level / (level + 1.0);
                    double arc = 2.0 * Math.asin(arcAngle);

                    node.leftTangent = node.angle + arc;
                    node.rightTangent = node.angle - arc;

                    prevAngle = node.angle;
                    prevParent = node;
                }
            }
        }

        if (null != firstParent) {
            double remaningAngle = TWO_PI - prevParent.angle;
            firstParent.rightBisector = (firstParent.angle - remaningAngle) / 2.0;
            if (firstParent.rightBisector < 0) {
                prevParent.leftBisector = firstParent.rightBisector + TWO_PI + TWO_PI;
            } else {
                prevParent.leftBisector = firstParent.rightBisector + TWO_PI;
            }
        }

        if (parentNodes.size() > 0) {
            layoutTreeN(viewMap, level + 1, parentNodes);
        }
    }

    private void placeView(Map viewMap, VertexView view, double x, double y) {
    	Rectangle2D rect = view.getBounds();
        Rectangle bounds = new Rectangle((int) rect.getX(), 
				(int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
        //(Rectangle) view.getBounds().clone();
        bounds.x = (int) Math.round(x);
        bounds.y = (int) Math.round(y);
        Object cell = view.getCell();
        Map map = new Hashtable();
        GraphConstants.setBounds(map, bounds);
        viewMap.put(cell, map);
    }

    private List getChildren(VertexView view, List level) {
        ArrayList children = new ArrayList();
        Object vertex = view.getCell();
        GraphModel model = jgraph.getModel();
        int portCount = model.getChildCount(vertex);

        // iterate any NodePort
        for (int i = 0; i < portCount; i++) {
            Object port = model.getChild(vertex, i);

            // iterate any Edge in the port
            Iterator itrEdges = model.edges(port);

            while (itrEdges.hasNext()) {
                Object edge = itrEdges.next();

                // if the Edge is a forward edge we should follow this edge
                if (port == model.getSource(edge)) {
                    Object targetPort = model.getTarget(edge);
                    Object targetVertex = model.getParent(targetPort);
                    VertexView targetVertexView =
                        (VertexView) jgraph.getGraphLayoutCache().getMapping(targetVertex, false);
                    if (level.contains(targetVertexView)) {
                        children.add(targetVertexView);
                    }
                }
            }
        }
        return children;
    }

    private List getRoots(JGraph jgraph, CellView[] cellViews) {
        List roots = new ArrayList();

        GraphModel model = jgraph.getModel();

        for (int i = 0; i < cellViews.length; i++) {
            if (cellViews[i] instanceof VertexView) {
                VertexView vertexView = (VertexView) cellViews[i];
                boolean isRoot = true;
                Object vertex = vertexView.getCell();
                int portCount = model.getChildCount(vertex);
                for (int j = 0; isRoot && j < portCount; j++) {
                    Object port = model.getChild(vertex, j);

                    Iterator itrEdges = model.edges(port);
                    while (isRoot && itrEdges.hasNext()) {
                        Object edge = itrEdges.next();

                        if (model.getTarget(edge) == port) {
                            isRoot = false;
                        }
                    }
                }
                if (isRoot) {
                    roots.add(vertexView);
                }
            }
        }

        return roots;
    }

    /**
     * Algorithm assumes a single root node so if there are multiple roots
     * (nodes with no incoming edges), then we construct the spanning tree
     * with an invisible root node that is the parent of the real roots.
     */
    private TreeNode getSpanningTree(CellView[] cellViews, List roots) {
        List vertexViews = new ArrayList(cellViews.length);

        // first: mark all as not visited
        for (int i = 0; i < cellViews.length; i++) {
            if (cellViews[i] instanceof VertexView) {
                VertexView vertexView = (VertexView) cellViews[i];
                vertexView.getAttributes().remove(RADIAL_TREE_VISITED);
                vertexViews.add(vertexView);
            }
        }

        TreeNode node;

        if (roots.size() == 0) {
			if (vertexViews.size() == 0) {
				return null;
			}
            // else, pick an arbitrary node
            roots.add(vertexViews.get(0));
        }

        if (roots.size() > 1) {
            node = new TreeNode(null);
            buildSpanningTree(vertexViews, node, roots);
        } else {
            VertexView vertexView = (VertexView) roots.get(0);
            node = new TreeNode(vertexView);
            vertexView.getAttributes().put(RADIAL_TREE_VISITED, Boolean.TRUE);
            buildSpanningTree(vertexViews, node, getChildren(vertexView, vertexViews));
        }

        return node;
    }

    /**
     * Breadth-first traversal of the graph.
     */
    private void buildSpanningTree(List vertexViews, TreeNode node, List children) {
        Iterator itr = children.iterator();
        while (itr.hasNext()) {
            VertexView vertexView = (VertexView) itr.next();
            if (null == vertexView.getAttributes().get(RADIAL_TREE_VISITED)) {
                vertexView.getAttributes().put(RADIAL_TREE_VISITED, Boolean.TRUE);
                TreeNode childNode = new TreeNode(vertexView);
                node.addChild(childNode);
            }
        }

        itr = node.getChildren().iterator();
        while (itr.hasNext()) {
            TreeNode childNode = (TreeNode) itr.next();
            VertexView vertexView = childNode.getView();
            buildSpanningTree(vertexViews, childNode, getChildren(vertexView, vertexViews));
        }
    }

    private static class TreeNode {

        private VertexView view;
        private List children = new ArrayList();

        public double angle, x, y, rightBisector, leftBisector, rightTangent, leftTangent;

        TreeNode(VertexView view) {
            this.view = view;
        }

        public int getDepth() {
            int depth = 1;
            Iterator itr = children.iterator();
            while (itr.hasNext()) {
                TreeNode node = (TreeNode) itr.next();
                int childDepth = node.getDepth();
                if (childDepth >= depth) {
                    depth = childDepth + 1;
                }
            }
            return depth;
        }

        public VertexView getView() {
            return view;
        }

        public void addChild(TreeNode node) {
            children.add(node);
        }

        public List getChildren() {
            return children;
        }

        public boolean hasChildren() {
            return children.size() > 0;
        }

        public double leftLimit() {
            return Math.min(normalize(leftBisector), (leftTangent));
        }

        public double rightLimit() {
            return Math.max(normalize(rightBisector), (rightTangent));
        }

        private double normalize(double angle) {
            /*
                while (angle > TWO_PI) {
                    angle -= TWO_PI;
                }
                while (angle < -TWO_PI) {
                    angle += TWO_PI;
                }
            */
            return angle;
        }
    }

}
