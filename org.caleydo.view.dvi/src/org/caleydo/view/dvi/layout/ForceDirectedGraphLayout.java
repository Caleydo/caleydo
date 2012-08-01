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
package org.caleydo.view.dvi.layout;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.view.dvi.Edge;
import org.caleydo.view.dvi.GLDataViewIntegrator;
import org.caleydo.view.dvi.Graph;
import org.caleydo.view.dvi.layout.edge.rendering.AEdgeRenderer;
import org.caleydo.view.dvi.layout.edge.rendering.FreeLayoutEdgeBandRenderer;
import org.caleydo.view.dvi.layout.edge.rendering.FreeLayoutEdgeLineRenderer;
import org.caleydo.view.dvi.layout.edge.routing.CollisionAvoidanceRoutingStrategy;
import org.caleydo.view.dvi.layout.edge.routing.IEdgeRoutingStrategy;
import org.caleydo.view.dvi.node.ADataNode;
import org.caleydo.view.dvi.node.IDVINode;
import org.caleydo.view.dvi.node.ViewNode;

public class ForceDirectedGraphLayout extends AGraphLayout {
	// AN ALGORITHM FOR DRAWING GENERAL UNDIRECTED GRAPHS
	// by Tomihisa KAMADA and Satoru KAWAI, 1988

	// optimization
	// http://www.cg.tuwien.ac.at/courses/InfoVis/HallOfFame/2005/07_Pfeffer_SpringEmbedders/pfeffer05_files/Source.pdf

	// data

	protected Map<Object, Point2D> centeredPositions = null;

	// protected Rectangle2D layoutingArea = null;

	protected Collection<IDVINode> nodesToLayout = null;

	// calculation parameters
	Map<Object, Map<Object, Double>> distanceMatrix = null;

	Map<Object, Point2D> forces = null;

	double diameter = 0.0;

	double scalingFactor = 1.0;

	double L = 0.0;

	double K = 1;

	protected double constraintStartingFactor = 1.0;

	protected double constraintDecrease = 0.0;

	protected double constraintFactor = 1.0;

	protected boolean running = true;

	protected boolean initializeNodes = true;

	protected boolean initializeForces = true;

	protected boolean initializeConstraints = false;

	/**
	 * The positions of the nodes relative to the window.
	 */
	private Map<IDVINode, Pair<Float, Float>> relativeNodePositions = new HashMap<IDVINode, Pair<Float, Float>>();

	protected IEdgeRoutingStrategy edgeRoutingStrategy;

	public ForceDirectedGraphLayout(GLDataViewIntegrator view, Graph graph) {
		super(view, graph);
		edgeRoutingStrategy = new CollisionAvoidanceRoutingStrategy(graph);
	}

	// ###################
	// ## layout source ##
	// ###################
	public void setGraph(Graph graph) {
		this.graph = graph;

		initializeNodes = true;
		initializeForces = true;
		running = true;
	}

	protected boolean isDataAvailable() {
		if (graph == null)
			return false;
		if (graph.getNumberOfNodes() == 0)
			return false;

		return true;
	}

	// #####################
	// ## distance matrix ##
	// #####################
	private void setDistance(Object node1, Object node2, Double distance) {
		if (distanceMatrix == null) {
			distanceMatrix = new HashMap<Object, Map<Object, Double>>();
		}

		Map<Object, Double> map = distanceMatrix.get(node1);
		if (map == null) {
			map = new HashMap<Object, Double>();
			distanceMatrix.put(node1, map);
		}
		map.put(node2, distance);

		map = distanceMatrix.get(node2);
		if (map == null) {
			map = new HashMap<Object, Double>();
			distanceMatrix.put(node2, map);
		}
		map.put(node1, distance);
	}

	private double getDistance(Object node1, Object node2) {
		if (node1 == node2)
			return 0;

		if (distanceMatrix == null)
			return Double.POSITIVE_INFINITY;

		Map<Object, Double> map = distanceMatrix.get(node1);
		if (map == null)
			return Double.POSITIVE_INFINITY;

		Double distance = map.get(node2);

		if (distance == null)
			return Double.POSITIVE_INFINITY;
		return distance;
	}

	protected double getL(Object node1, Object node2) {
		Double distance = getDistance(node1, node2);

		if (distance == Double.POSITIVE_INFINITY) {
			return 0.5;
		}

		return L * distance;
	}

	protected double getK(Object node1, Object node2) {
		Double distance = getDistance(node1, node2);

		if (distance == Double.POSITIVE_INFINITY) {
			return K / (diameter * diameter);
		} else if (distance == 0) {
			return 1;
		}

		return K / (distance * distance);
	}

	private void calculateDistanceMatrix() {
		// http://de.wikipedia.org/wiki/Floyd-Warshall-Algorithmus
		diameter = 1.0;

		if (!isDataAvailable())
			return;
		// initialize with available edges
		for (IDVINode node1 : graph.getNodes()) {
			for (IDVINode node2 : graph.getNodes()) {
				if (graph.incident(node1, node2)) {
					setDistance(node1, node2, 1.0);
				}
			}
		}

		// calculate distances
		for (IDVINode nodek : graph.getNodes()) {
			for (IDVINode nodei : graph.getNodes()) {
				for (IDVINode nodej : graph.getNodes()) {
					setDistance(
							nodei,
							nodej,
							Math.min(getDistance(nodei, nodej), getDistance(nodei, nodek)
									+ getDistance(nodek, nodej)));
				}
			}
		}

		// calculate diameter
		double distance;
		diameter = 1.0;
		for (Object node1 : graph.getNodes()) {
			for (Object node2 : graph.getNodes()) {
				distance = getDistance(node1, node2);

				if (distance != Double.POSITIVE_INFINITY) {
					diameter = Math.max(diameter, distance);
				}
			}
		}
	}

	// ############
	// ## deltas ##
	// ############
	protected Point2D calculateDelta(Object node) {
		// dx = (-eq7 - eq14 dy) / eq13
		// dy = (eq7 eq15 - eq8 eq13) / (eq13 eq16 - eq14 eq15)

		double deltaX = 0.0;
		double deltaY = 0.0;

		double[] eqs = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		// eq7 = 0 | eq8 = 1 | eq13 = 2 | eq14 = 3 | eq15 = 4 | eq16 = 5

		for (Object node2 : nodesToLayout) {
			if (node != node2) {
				calculateDelta(node, node2, eqs);
			}
		}

		deltaY = (eqs[2] * eqs[5] - eqs[3] * eqs[4]);
		if (deltaY != 0.0) {
			deltaY = (eqs[0] * eqs[4] - eqs[1] * eqs[2]) / deltaY;
		}

		if (eqs[2] == 0.0) {
			deltaX = 0.0;
		} else {
			deltaX = (-eqs[0] - eqs[3] * deltaY) / eqs[2];
		}

		return new Point2D.Double(deltaX, deltaY);
	}

	protected void calculateDelta(Object node1, Object node2, double[] eqs) {
		// pre calculation
		Point2D p1 = getNodePosition(node1, false);
		Point2D p2 = getNodePosition(node2, false);

		double k = getK(node1, node2);
		double l = getL(node1, node2);

		double dx = p1.getX() - p2.getX();
		double dy = p1.getY() - p2.getY();

		double dx2 = dx * dx;
		double dy2 = dy * dy;

		// calculate divisor
		double divisor = dx2 + dy2;

		double divisor7_8 = Math.sqrt(divisor);
		if (divisor7_8 == 0.0)
			divisor7_8 = 1.0;

		double divisor13_16 = Math.pow(divisor, 1.5);
		if (divisor13_16 == 0.0)
			divisor7_8 = 1.0;

		// calculate equations
		eqs[0] += k * dx * (1 - l / divisor7_8);
		eqs[1] += k * dy * (1 - l / divisor7_8);

		eqs[2] += k * (1 - l * dy2 / divisor13_16);
		eqs[3] += k * l * dx * dy / divisor13_16;
		eqs[4] += k * l * dx * dy / divisor13_16;
		eqs[5] += k * (1 - l * dx2 / divisor13_16);
	}

	// ############
	// ## forces ##
	// ############
	private void setForce(Object node, Point2D force) {
		if (forces == null) {
			forces = new HashMap<Object, Point2D>();
		}

		forces.put(node, force);
	}

	private Point2D getForce(Object node) {
		if (forces == null)
			return null;

		return forces.get(node);
	}

	private boolean forcesWithoutError() {
		for (Object node : nodesToLayout) {
			Point2D force = getForce(node);

			if (force == null)
				continue;

			if (Double.isNaN(force.getX()) || Double.isNaN(force.getY()))
				return false;
		}

		return true;
	}

	protected Point2D calculateForce(Object node) {
		double[] deltas = { 0.0, 0.0 };

		for (Object node2 : nodesToLayout) {
			if (node != node2) {
				calculateForce(node, node2, deltas);
			}
		}

		return new Point2D.Double(deltas[0], deltas[1]);
	}

	protected void calculateForce(Object node1, Object node2, double[] deltas) {
		Point2D p1 = getNodePosition(node1, false);
		Point2D p2 = getNodePosition(node2, false);

		double k = getK(node1, node2);
		double l = getL(node1, node2);

		double dx = p1.getX() - p2.getX();
		double dy = p1.getY() - p2.getY();

		double dx2 = dx * dx;
		double dy2 = dy * dy;

		// calculate divisor
		double divisor = dx2 + dy2;

		double divisor7_8 = Math.sqrt(divisor);
		if (divisor7_8 == 0.0)
			divisor7_8 = 1.0;

		// calculate equations
		deltas[0] += k * dx * (1 - l / divisor7_8);
		deltas[1] += k * dy * (1 - l / divisor7_8);
	}

	protected Point2D calculateForceInfluence(Object node, Object influencer) {
		double deltaX = 0.0;
		double deltaY = 0.0;

		double dx;
		double dx2;
		double dy;
		double dy2;

		double divisor;
		double divisor7_8;

		double k;
		double l;

		Point2D p1 = getNodePosition(node, false);
		Point2D p2 = getNodePosition(influencer, false);

		k = getK(node, influencer);
		l = getL(node, influencer);

		dx = p1.getX() - p2.getX();
		dy = p1.getY() - p2.getY();

		dx2 = dx * dx;
		dy2 = dy * dy;

		// calculate divisor
		divisor = dx2 + dy2;

		divisor7_8 = Math.sqrt(divisor);
		if (divisor7_8 == 0.0)
			divisor7_8 = 1.0;

		// calculate equations
		deltaX = k * dx * (1 - l / divisor7_8);
		deltaY = k * dy * (1 - l / divisor7_8);

		return new Point2D.Double(deltaX, deltaY);
	}

	// ########################
	// ## layout calculation ##
	// ########################
	@Override
	public void layout(Rectangle2D area) {
		if (area == null)
			return;

		running = false;

		if (graph == null)
			return;
		if (graph.getNumberOfNodes() == 0)
			return;

		nodesToLayout = getNodesToLayout();
		if (nodesToLayout == null)
			return;

		// // initializations
		// if (initializeNodes) {
		initializeNodePositions(area);

		calculateDistanceMatrix();

		centering(area);

		initializeNodes = false;
		// }

		if (!graph.hasEdges())
			return;

		// if (initializeForces || !forcesWithoutError()) {
		initializeForces();

		initializeForces = false;
		// }

		running = true;

		L = scalingFactor * Math.max(10, Math.min(area.getWidth(), area.getHeight()))
				/ diameter;

		// find biggest delta
		int numberOfNodesSet = 100;
		double forceMax = 0.0;
		boolean nodeSet = false;

		while (numberOfNodesSet > 0) {
			numberOfNodesSet--;

			double forceLength;
			forceMax = 0.0;

			Object nodeMax = null;
			;
			Point2D force;

			for (Object node : nodesToLayout) {
				force = getForce(node);
				if (force == null) {
					forceLength = 0;
				} else {
					forceLength = force.distance(0, 0);
				}

				if (forceLength > forceMax) {
					forceMax = forceLength;
					nodeMax = node;
				}
			}

			if (forceMax > 1) {
				nodeSet = true;

				Point2D pos = getNodePosition(nodeMax, false);
				Point2D delta = calculateDelta(nodeMax);

				substractForceInfluence(nodeMax);

				pos.setLocation(pos.getX() + delta.getX(), pos.getY() + delta.getY());

				addForceInfluence(nodeMax);

				initializeForce(nodeMax);
			} else {
				numberOfNodesSet = 0;
			}

			constraintFactor -= constraintDecrease;
			if (constraintFactor < 0) {
				constraintFactor = 0;
			}
		}

		if (nodeSet) {
			centering(area);
		}

		if (forceMax <= 1) {
			running = false;
		}

		for (IDVINode node : nodesToLayout) {
			node.setUpsideDown(false);
		}

		fitNodesToDrawingArea(area);

		relativeNodePositions.clear();

		for (IDVINode node : nodesToLayout) {
			Point2D position = getNodePosition(node);

			float relativePosX = (float) position.getX() / (float) area.getWidth();
			float relativePosY = (float) position.getY() / (float) area.getHeight();
			relativeNodePositions.put(node, new Pair<Float, Float>(relativePosX,
					relativePosY));
		}
	}

	@Override
	public void fitNodesToDrawingArea(Rectangle2D area) {
		if (nodesToLayout == null)
			return;

		for (IDVINode node : nodesToLayout) {

			Point2D nodePosition = getNodePosition(node);
			if (nodePosition == null)
				continue;

			double nodePositionX = nodePosition.getX();
			if (nodePositionX + node.getWidthPixels() / 2.0f > area.getMaxX()) {
				nodePositionX = area.getMaxX() - node.getWidthPixels() / 2.0f;
			} else if (nodePositionX - node.getWidthPixels() / 2.0f < area.getMinX()) {
				nodePositionX = area.getMinX() + node.getWidthPixels() / 2.0f;
			}

			double nodePositionY = getNodePosition(node).getY();
			if (nodePositionY + node.getHeightPixels() / 2.0f > area.getMaxY()) {
				nodePositionY = area.getMaxY() - node.getHeightPixels() / 2.0f;
			} else if (nodePositionY - node.getHeightPixels() / 2.0f < area.getMinY()) {
				nodePositionY = area.getMinY() + node.getHeightPixels() / 2.0f;
			}

			setNodePosition(node, new Point2D.Float((float) nodePositionX,
					(float) nodePositionY));
		}

		view.setNodePositionsUpdated(true);
	}

	protected Collection<IDVINode> getNodesToLayout() {
		return graph.getNodes();
	}

	protected void initializeNodePositions(Rectangle2D area) {
		if (graph == null)
			return;
		if (graph.getNumberOfNodes() == 0)
			return;

		if (nodePositions == null) {
			nodePositions = new HashMap<Object, Point2D>();
		}

		if (centeredPositions == null) {
			centeredPositions = new HashMap<Object, Point2D>();
		}

		Point2D point;
		double arc = 0;
		double arcStep = 2 * Math.PI / graph.getNumberOfNodes();
		double radius = Math.min(area.getWidth(), area.getHeight()) / 2.5;
		double centerX = area.getWidth() / 2.0;
		double centerY = area.getHeight() / 2.0;

		for (Object node : graph.getNodes()) {
			if (!isPositionAvailable(node)) {
				point = new Point2D.Double(centerX + radius * Math.sin(arc), centerY
						+ radius * Math.cos(arc));

				nodePositions.put(node, point);
				centeredPositions.put(node, point);

				arc += arcStep;
			}
		}
	}

	private boolean isPositionAvailable(Object node) {
		if (nodePositions == null)
			return false;

		Point2D pos = nodePositions.get(node);
		if (pos == null)
			return false;

		if (Double.isNaN(pos.getX()) || Double.isNaN(pos.getY()))
			return false;

		return true;
	}

	private void initializeForces() {
		Point2D force;

		for (Object node : graph.getNodes()) {
			force = calculateForce(node);

			setForce(node, force);
		}
	}

	private void initializeForce(Object node) {
		Point2D force;

		force = calculateForce(node);

		setForce(node, force);
	}

	private void substractForceInfluence(Object influencer) {
		Point2D force, subtractForce;
		for (Object node : graph.getNodes()) {
			if (node != influencer) {
				force = getForce(node);

				subtractForce = calculateForceInfluence(node, influencer);

				force.setLocation(force.getX() - subtractForce.getX(), force.getY()
						- subtractForce.getY());
			}
		}
	}

	private void addForceInfluence(Object influencer) {
		Point2D force, addDelta;
		for (Object node : graph.getNodes()) {
			if (node != influencer) {
				force = getForce(node);

				addDelta = calculateForceInfluence(node, influencer);

				force.setLocation(force.getX() + addDelta.getX(),
						force.getY() + addDelta.getY());
			}
		}
	}

	protected void centering(Rectangle2D area) {
		if (nodesToLayout.size() == 1) {
			for (Object node : nodesToLayout) {
				centeredPositions.put(node,
						new Point2D.Double(area.getCenterX(), area.getCenterY()));
			}

			return;
		}

		// determine center
		double minX = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;

		Point2D point;
		for (Object node : nodesToLayout) {
			point = getNodePosition(node, false);

			minX = Math.min(minX, point.getX());
			maxX = Math.max(maxX, point.getX());
			minY = Math.min(minY, point.getY());
			maxY = Math.max(maxY, point.getY());
		}

		// update scaling
		double fx = maxX == minX ? 1 : (area.getWidth()) / (maxX - minX);
		double fy = maxY == minY ? 1 : (area.getHeight()) / (maxY - minY);

		double offsetX = area.getMinX();
		double offsetY = area.getMinY();

		// double factor = 1.0;
		// if (fx < fy) {
		// factor = fx;
		//
		// offsetY += (layoutingArea.getHeight() - factor * (maxY-minY)) / 2;
		// } else {
		// factor = fy;
		//
		// offsetX += (layoutingArea.getWidth() - factor * (maxX-minX)) / 2;
		// }

		for (Object node : nodesToLayout) {
			point = getNodePosition(node, false);

			centeredPositions.put(node, new Point2D.Double((point.getX() - minX) * fx
					+ offsetX, (point.getY() - minY) * fy + offsetY));
		}
	}

	public boolean layoutInProgress() {
		return running;
	}

	// --- setter ---
	// nodes
	@Override
	public void setNodePosition(Object node, Point2D position) {
		// TODO Auto-generated method stub
		nodePositions.put(node, position);
		centeredPositions.put(node, position);
	}

	// --- getter ---
	// node position
	@Override
	public Point2D getNodePosition(Object node) {
		return getNodePosition(node, true);
	}

	public Point2D getNodePosition(Object node, boolean centered) {
		if (centered) {
			if (centeredPositions == null)
				return null;

			return centeredPositions.get(node);
		} else {
			if (nodePositions == null)
				return null;

			return nodePositions.get(node);
		}
	}

	@Override
	public void clearNodePositions() {
		if (centeredPositions != null)
			centeredPositions.clear();
		centeredPositions = null;
		if (nodePositions != null)
			nodePositions.clear();
		nodePositions = null;
	}

	@Override
	public AEdgeRenderer getLayoutSpecificEdgeRenderer(Edge edge) {

		IDVINode node1 = edge.getNode1();
		IDVINode node2 = edge.getNode2();

		AEdgeRenderer edgeRenderer = null;

		if (node1 instanceof ViewNode || node2 instanceof ViewNode) {
			edgeRenderer = new FreeLayoutEdgeBandRenderer(edge, view);

		} else {
			edgeRenderer = new FreeLayoutEdgeLineRenderer(edge, view, view.getEdgeLabel(
					(ADataNode) node1, (ADataNode) node2));
		}

		edgeRenderer.setEdgeRoutingStrategy(edgeRoutingStrategy);
		return edgeRenderer;
	}

	@Override
	public AEdgeRenderer getCustomLayoutEdgeRenderer(Edge edge) {
		return getLayoutSpecificEdgeRenderer(edge);
	}

	@Override
	public void applyIncrementalLayout(Rectangle2D area) {
		for (IDVINode node : nodesToLayout) {
			Pair<Float, Float> relativePosition = relativeNodePositions.get(node);
			setNodePosition(node, new Point2D.Double(relativePosition.getFirst()
					* (float) area.getWidth(), relativePosition.getSecond()
					* (float) area.getHeight()));
		}

	}
}
