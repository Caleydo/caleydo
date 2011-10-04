package org.caleydo.view.datagraph;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.caleydo.view.datagraph.node.IDataGraphNode;

public class ForceDirectedGraphLayout {
	// AN ALGORITHM FOR DRAWING GENERAL UNDIRECTED GRAPHS
	// by Tomihisa KAMADA and Satoru KAWAI, 1988

	// optimization
	// http://www.cg.tuwien.ac.at/courses/InfoVis/HallOfFame/2005/07_Pfeffer_SpringEmbedders/pfeffer05_files/Source.pdf

	// data
	protected Graph<IDataGraphNode> graph = null;

	protected Map<Object, Point2D> nodePositions = null;

	protected Map<Object, Point2D> centeredPositions = null;

	protected Rectangle2D layoutingArea = null;

	protected Collection<IDataGraphNode> nodesToLayout = null;

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

	// ###################
	// ## layout source ##
	// ###################
	public void setGraph(Graph<IDataGraphNode> graph) {
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
		for (IDataGraphNode node1 : graph.getNodes()) {
			for (IDataGraphNode node2 : graph.getNodes()) {
				if (graph.incident(node1, node2)) {
					setDistance(node1, node2, 1.0);
				}
			}
		}

		// calculate distances
		for (IDataGraphNode nodek : graph.getNodes()) {
			for (IDataGraphNode nodei : graph.getNodes()) {
				for (IDataGraphNode nodej : graph.getNodes()) {
					setDistance(
							nodei,
							nodej,
							Math.min(
									getDistance(nodei, nodej),
									getDistance(nodei, nodek)
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
	public void layout(Rectangle2D area) {
		if (area == null)
			return;

		boolean equalAreas = area.equals(layoutingArea);
		layoutingArea = area;
		running = false;

		if (graph == null)
			return;
		if (graph.getNumberOfNodes() == 0)
			return;

		nodesToLayout = getNodesToLayout();
		if (nodesToLayout == null)
			return;

		// initializations
		if (initializeNodes) {
			initializeNodePositions();

			calculateDistanceMatrix();

			centering();

			initializeNodes = false;
		}

		if (!graph.hasEdges())
			return;

		if (initializeForces || !forcesWithoutError()) {
			initializeForces();

			initializeForces = false;
		}

		running = true;

		L = scalingFactor
				* Math.max(10, Math.min(area.getWidth(), area.getHeight()))
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

				pos.setLocation(pos.getX() + delta.getX(),
						pos.getY() + delta.getY());

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

		if (nodeSet || !equalAreas) {
			centering();
		}

		if (forceMax <= 1) {
			running = false;
		}
	}

	protected Collection<IDataGraphNode> getNodesToLayout() {
		return graph.getNodes();
	}

	protected void initializeNodePositions() {
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
		double radius = Math.min(layoutingArea.getWidth(),
				layoutingArea.getHeight()) / 2.5;
		double centerX = layoutingArea.getWidth() / 2.0;
		double centerY = layoutingArea.getHeight() / 2.0;

		for (Object node : graph.getNodes()) {
			if (!isPositionAvailable(node)) {
				point = new Point2D.Double(centerX + radius * Math.sin(arc),
						centerY + radius * Math.cos(arc));

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

				force.setLocation(force.getX() - subtractForce.getX(),
						force.getY() - subtractForce.getY());
			}
		}
	}

	private void addForceInfluence(Object influencer) {
		Point2D force, addDelta;
		for (Object node : graph.getNodes()) {
			if (node != influencer) {
				force = getForce(node);

				addDelta = calculateForceInfluence(node, influencer);

				force.setLocation(force.getX() + addDelta.getX(), force.getY()
						+ addDelta.getY());
			}
		}
	}

	protected void centering() {
		if (nodesToLayout.size() == 1) {
			for (Object node : nodesToLayout) {
				centeredPositions.put(node,
						new Point2D.Double(layoutingArea.getCenterX(),
								layoutingArea.getCenterY()));
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
		double fx = maxX == minX ? 1 : (layoutingArea.getWidth())
				/ (maxX - minX);
		double fy = maxY == minY ? 1 : (layoutingArea.getHeight())
				/ (maxY - minY);

		double offsetX = layoutingArea.getMinX();
		double offsetY = layoutingArea.getMinY();

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

			centeredPositions.put(node, new Point2D.Double(
					(point.getX() - minX) * fx + offsetX, (point.getY() - minY)
							* fy + offsetY));
		}
	}

	public boolean layoutInProgress() {
		return running;
	}

	// --- setter ---
	// nodes
	public void setNodePosition(Object node, Point2D position) {
		// TODO Auto-generated method stub
		nodePositions.put(node, position);
		centeredPositions.put(node, position);
	}

	// --- getter ---
	// node position
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

	public void clearNodePositions() {
		if (centeredPositions != null)
			centeredPositions.clear();
		centeredPositions = null;
		if (nodePositions != null)
			nodePositions.clear();
		nodePositions = null;
	}
}
