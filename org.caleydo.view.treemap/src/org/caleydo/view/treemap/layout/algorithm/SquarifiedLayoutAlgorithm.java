package org.caleydo.view.treemap.layout.algorithm;

import java.util.ArrayList;
import java.util.Vector;

import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.view.treemap.layout.ATreeMapNode;

public class SquarifiedLayoutAlgorithm implements ILayoutAlgorithm {

	@Override
	public void layout(ATreeMapNode root) {
		root.setMinX(0);
		root.setMinY(0);
		root.setMaxX(1);
		root.setMaxY(1);
		
		layoutHelp(root);
		
		
	}

	private void layoutHelp(ATreeMapNode node) {
		ArrayList<ATreeMapNode> children = node.getChildren();

		if (children == null || children.size() == 0)
			return;

		
		Vector<Integer> cv = new Vector<Integer>();
		for (ATreeMapNode n : children) {
			cv.add(node.getID());
		}

		rect = new Rectangle(node.getMinX(), node.getMaxX(), node.getMinY(), node.getMaxY());
		squarify(cv, new Vector<Integer>(), rect.width());

		for (ATreeMapNode n : children) {
			layout(n);
		}
	}

	private Rectangle rect;
	private Tree<ATreeMapNode> tree;

	private void squarify(Vector<Integer> children, Vector<Integer> row, float w) {
		int c = head(children);
		if (worst(row, w) <= worst(concat(row, c), w)) {
			squarify(tail(children), concat(row, c), w);
		} else {
			rect.layoutRow(row);
			squarify(children, new Vector<Integer>(), rect.width());
		}

	}

	private float worst(Vector<Integer> row, float w) {
		if (row.size() == 0)
			return 0f;

		float rmin = Float.MAX_VALUE;
		float rmax = Float.MIN_VALUE;
		float s = 0;

		for (int id : row) {
			if (getNode(id).getSize() < rmin)
				rmin = getNode(id).getSize();
			if (getNode(id).getSize() > rmax)
				rmax = getNode(id).getSize();
			s += getNode(id).getSize();
		}

		return (float) Math.max((Math.pow(w, 2) * rmax) / Math.pow(s, 2), Math.pow(s, 2) / (Math.pow(w, 2) * rmin));
	}

	@SuppressWarnings("unchecked")
	private Vector<Integer> tail(Vector<Integer> v) {
		Vector<Integer> tail = (Vector<Integer>) v.clone();
		tail.remove(0);
		return tail;
	}

	@SuppressWarnings("unchecked")
	private Vector<Integer> concat(Vector<Integer> v, int e) {
		Vector<Integer> clone = (Vector<Integer>) v.clone();
		clone.add(e);
		return clone;
	}

	private int head(Vector<Integer> v) {
		return v.firstElement();
	}

	private ATreeMapNode getNode(int id) {
		return tree.getNodeByNumber(id);
	}

	public class Rectangle {

		float xmin;
		float xmax;
		float ymin;
		float ymax;

		public Rectangle(float xmin, float xmax, float ymin, float ymax) {
			this.xmin = xmin;
			this.xmax = xmax;
			this.ymin = ymin;
			this.ymax = ymax;
		}

		public float width() {
			return xmax - xmin < ymax - ymin ? xmax - xmin : ymax - ymin;
		}

		public void layoutRow(Vector<Integer> row) {
			float sizeSum = 0;
			for (int id : row) {
				sizeSum += getNode(id).getSize();
			}
			if (xmax - xmin < ymax - ymin) {
				float ysize = ymax - ymin;
				float xsize = sizeSum / ysize;
				float yoffset = 0;
				for (int id : row) {
					getNode(id).setMaxY(yoffset + ymin);
					yoffset += ysize * (getNode(id).getSize() / sizeSum);
					getNode(id).setMaxY(yoffset + ymin);
					getNode(id).setMinX(xmin);
					getNode(id).setMaxX(xmax + xsize);
				}
				xmax += xsize;
			} else {
				float xsize = xmax - xmin;
				float ysize = sizeSum / xsize;

				float xoffset = 0;
				for (int i = 0; i < row.size(); i++) {
					getNode(row.get(i)).setMinX(xoffset + xmin);
					xoffset += xsize * (getNode(row.get(i)).getSize() / sizeSum);
					getNode(row.get(i)).setMaxX(xoffset + xmin);
					getNode(row.get(i)).setMinY(ymin);
					getNode(row.get(i)).setMaxY(ymin + ysize);
				}
				ymin += ysize;
			}
		}

		public boolean getDirection() {
			return xmax - xmin < ymax - ymin;
		}
	}

}
