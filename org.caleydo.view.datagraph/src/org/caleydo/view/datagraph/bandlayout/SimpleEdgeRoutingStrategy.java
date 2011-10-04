package org.caleydo.view.datagraph.bandlayout;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.caleydo.view.datagraph.Graph;
import org.caleydo.view.datagraph.node.IDataGraphNode;

public class SimpleEdgeRoutingStrategy implements IEdgeRoutingStrategy {

	private Graph<IDataGraphNode> dataGraph;

	public SimpleEdgeRoutingStrategy(Graph<IDataGraphNode> dataGraph) {
		this.dataGraph = dataGraph;
	}

	@Override
	public void createEdge(List<Point2D> edgePoints) {
		if (edgePoints == null || edgePoints.size() < 2)
			return;

		Map<Point2D, IDataGraphNode> pointsOnBoundingBoxes = new HashMap<Point2D, IDataGraphNode>();

		for (int i = 1; i < edgePoints.size(); i++) {
			Point2D point1 = edgePoints.get(i - 1);
			Point2D point2 = edgePoints.get(i);

			for (IDataGraphNode node : dataGraph.getNodes()) {
				Rectangle2D box = node.getBoundingBox();
				int code1 = box.outcode(point1);
				int code2 = box.outcode(point2);

				boolean isPoint1OnBoundingBox = (pointsOnBoundingBoxes
						.get(point1) == node);
				boolean isPoint2OnBoundingBox = (pointsOnBoundingBoxes
						.get(point2) == node);

				if (((code1 & code2) != 0)
						|| (code1 == 0 && !isPoint1OnBoundingBox)
						|| (code2 == 0 && !isPoint2OnBoundingBox)
						|| (isPoint1OnBoundingBox && isPoint2OnBoundingBox)) {
					continue;
				}

				Point2D intersection1 = (isPoint1OnBoundingBox) ? (point1)
						: (calcIntersectionPoint(point1, point2, box, code1));
				Point2D intersection2 = (isPoint2OnBoundingBox) ? (point2)
						: (calcIntersectionPoint(point2, point1, box, code2));

				if (intersection1 != null && intersection2 != null) {

					if (intersection1.getX() <= intersection2.getX() + 0.0000001
							&& intersection1.getX() >= intersection2.getX() - 0.0000001
							&& intersection1.getY() <= intersection2.getY() + 0.0000001
							&& intersection1.getY() >= intersection2.getY() - 0.0000001) {
						continue;
					}

					Point2D[] corners = new Point2D[4];
					// corners[0] = new Point2D.Double(box.getMinX() - 0.001,
					// box.getMinY() - 0.001);
					// corners[1] = new Point2D.Double(box.getMaxX() + 0.001,
					// box.getMinY() - 0.001);
					// corners[2] = new Point2D.Double(box.getMaxX() + 0.001,
					// box.getMaxY() + 0.001);
					// corners[3] = new Point2D.Double(box.getMinX() - 0.001,
					// box.getMaxY() + 0.001);
					corners[0] = new Point2D.Double(box.getMinX(),
							box.getMinY());
					corners[1] = new Point2D.Double(box.getMaxX(),
							box.getMinY());
					corners[2] = new Point2D.Double(box.getMaxX(),
							box.getMaxY());
					corners[3] = new Point2D.Double(box.getMinX(),
							box.getMaxY());

					double minDistance = Double.MAX_VALUE;
					Point2D bendPoint = null;

					for (int j = 0; j < 4; j++) {
						if ((corners[j].getX() == point1.getX()
								&& corners[j].getY() == point1.getY() && isPoint1OnBoundingBox)
								|| (corners[j].getX() == point2.getX() && corners[j]
										.getY() == point2.getY())
								&& isPoint2OnBoundingBox) {
							continue;
						}
						double currentSummedDistance = intersection1
								.distanceSq(corners[j])
								+ intersection2.distanceSq(corners[j]);
						if (currentSummedDistance < minDistance) {
							minDistance = currentSummedDistance;
							bendPoint = corners[j];

						}
					}
					if (bendPoint == null) {
						System.out.println("null");
					}

					boolean isPointAlreadyAdded = false;

					for (Point2D point : edgePoints) {
						if (point.getX() == bendPoint.getX()
								&& point.getY() == bendPoint.getY()) {
							isPointAlreadyAdded = true;
							break;
						}
					}

					if (isPointAlreadyAdded) {
						continue;
					}

					edgePoints.add(i, bendPoint);
					pointsOnBoundingBoxes.put(bendPoint, node);
					i--;

					// gl.glPointSize(5);
					// gl.glColor3f(0, 0, 1);
					// gl.glBegin(GL2.GL_POINTS);
					// gl.glVertex2d(intersection1.getX(),
					// intersection1.getY());
					// gl.glVertex2d(intersection2.getX(),
					// intersection2.getY());
					// gl.glEnd();
					//
					// gl.glLineWidth(1);
					// gl.glColor3f(0, 0, 1);
					// gl.glBegin(GL2.GL_LINE_LOOP);
					// gl.glVertex2d(box.getMinX(), box.getMinY());
					// gl.glVertex2d(box.getMaxX(), box.getMinY());
					// gl.glVertex2d(box.getMaxX(), box.getMaxY());
					// gl.glVertex2d(box.getMinX(), box.getMaxY());
					// gl.glEnd();

					break;
				}
			}
		}

		for (int step = edgePoints.size() - 2; step >= 2; step--) {

			for (int i = 0; i + step < edgePoints.size(); i++) {
				Point2D point1 = edgePoints.get(i);
				Point2D point2 = edgePoints.get(i + step);

				boolean hasIntersection = false;

				for (IDataGraphNode node : dataGraph.getNodes()) {
					Rectangle2D box = node.getBoundingBox();
					int code1 = box.outcode(point1);
					int code2 = box.outcode(point2);

					boolean isPoint1OnBoundingBox = (pointsOnBoundingBoxes
							.get(point1) == node);
					boolean isPoint2OnBoundingBox = (pointsOnBoundingBoxes
							.get(point2) == node);

					if ((code1 & code2) != 0) {
						continue;
					}

					if ((code1 == 0 && !isPoint1OnBoundingBox
							&& edgePoints.indexOf(point1) != 0 && edgePoints
							.indexOf(point1) != edgePoints.size() - 1)
							|| (code2 == 0 && !isPoint2OnBoundingBox
									&& edgePoints.indexOf(point2) != 0 && edgePoints
									.indexOf(point2) != edgePoints.size() - 1)
							|| (isPoint1OnBoundingBox && isPoint2OnBoundingBox)) {
						hasIntersection = true;
						break;
					}

					Point2D intersection1 = (isPoint1OnBoundingBox) ? (point1)
							: (calcIntersectionPoint(point1, point2, box, code1));
					Point2D intersection2 = (isPoint2OnBoundingBox) ? (point2)
							: (calcIntersectionPoint(point2, point1, box, code2));

					if (intersection1 == null || intersection2 == null) {
						continue;
					}

					if (intersection1.distance(intersection2) < 0.000001
							&& (isPoint1OnBoundingBox || isPoint2OnBoundingBox)) {
						continue;
					}

					hasIntersection = true;
					break;
				}

				if (!hasIntersection) {
					for (int j = i + 1; j < i + step; j++) {
						edgePoints.remove(i + 1);
					}
					step = edgePoints.size() - 2;
					break;
				}
			}
		}
	}

	private Point2D calcIntersectionPoint(Point2D point1, Point2D point2,
			Rectangle2D rect, int code1) {

		double k = 0;

		if (point1.getX() != point2.getX()) {
			k = (point2.getY() - point1.getY())
					/ (point2.getX() - point1.getX());
		}

		if ((code1 & Rectangle2D.OUT_LEFT) != 0) {
			double y = point1.getY() + ((rect.getMinX() - point1.getX()) * k);

			if (y <= rect.getMaxY() && y >= rect.getMinY())
				return new Point2D.Double(rect.getMinX(), y);
		}

		if ((code1 & Rectangle2D.OUT_RIGHT) != 0) {
			double y = point1.getY() + ((rect.getMaxX() - point1.getX()) * k);
			if (y <= rect.getMaxY() && y >= rect.getMinY())
				return new Point2D.Double(rect.getMaxX(), y);
		}

		if ((code1 & Rectangle2D.OUT_TOP) != 0) {
			double x = point1.getX();
			if (k != 0) {
				x += (rect.getMinY() - point1.getY()) / k;
			}

			if (x <= rect.getMaxX() && x >= rect.getMinX())
				return new Point2D.Double(x, rect.getMinY());
		}

		if ((code1 & Rectangle2D.OUT_BOTTOM) != 0) {
			double x = point1.getX();
			if (k != 0) {
				x += (rect.getMaxY() - point1.getY()) / k;
			}

			if (x <= rect.getMaxX() && x >= rect.getMinX())
				return new Point2D.Double(x, rect.getMaxY());
		}

		return null;
	}

}
