package org.caleydo.view.treemap.layout.algorithm;

import java.util.Vector;

import org.caleydo.view.treemap.layout.ATreeMapNode;

public class SquarifiedLayoutAlgorithm implements ILayoutAlgorithm {

	@Override
	public void layout(ATreeMapNode tree) {
		// TODO Auto-generated method stub

	}

	private void squarify(Vector<Integer> children, Vector<Integer> row, int w) {
		int c = children.firstElement();
		Vector<Integer> clone = (Vector<Integer>) row.clone();
		clone.add(c);
		// if(worst(row, w) <= worst(clone, w))
		// squarify()
	}

	private float worst(Vector<Integer> row, int w) {
		return 0f;
	}

	private ATreeMapNode getNode(int id) {
		return null;
	}

	public class Rectangle {

		float xmin;
		float xmax;
		float ymin;
		float ymax;

		public Rectangle(float xmin, float xmax, float ymin, float ymax){
			this.xmin=xmin;
			this.xmax=xmax;
			this.ymin=ymin;
			this.ymax=ymax;
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
	}

}
