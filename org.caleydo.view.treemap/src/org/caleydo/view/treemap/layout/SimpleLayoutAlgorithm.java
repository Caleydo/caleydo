package org.caleydo.view.treemap.layout;

public class SimpleLayoutAlgorithm implements ILayoutAlgorithm {

	@Override
	public void layout(AbstractTree tree, IGlPainter painter) {
		// TODO Auto-generated method stub
		painter.init();
		paintHelp(tree.getRoot(), 0, 0, 1, 1, HORIZONTAL_ALIGNMENT, painter);
		painter.finish();
	}

	private static final boolean HORIZONTAL_ALIGNMENT = true;
	private static final boolean VERTICAL_ALIGNMENT = false;

	private void paintHelp(AbstractTreeNode root, float xOffset, float yOffset,
			float xMax, float yMax, boolean alignment, IGlPainter painter) {
		root.setMinX(xOffset);
		root.setMinY(yOffset);
		root.setMaxX(xMax);
		root.setMaxY(yMax);
		
		if (root.getChildren() != null && root.getChildren().size() > 0) {
			float area = (xMax - xOffset) * (yMax - yOffset);
			if (alignment == HORIZONTAL_ALIGNMENT) {
				float x = xOffset;
				float size = 0;
				for (AbstractTreeNode node : root.getChildren()) {
					size = (node.getSize() / area) * (xMax - xOffset);
					paintHelp(node, x, yOffset, x + size, yMax, !alignment,
							painter);
					x += size;
				}
			} else {
				float y = yOffset;
				float size;
				for (AbstractTreeNode node : root.getChildren()) {
					size = (node.getSize() / area) * (yMax - yOffset);
					paintHelp(node, xOffset, y, xMax, y + size, !alignment,
							painter);
					y += size;
				}
			}
		} 
//		else {
//			// painter.paintRectangle(xOffset, yOffset, xMax, yMax,
//			// root.getColorAttribute());
//
//			// System.out.println("painting "+root.getAreaColor());
//			System.out.println("painting: " + root.getLabel() + " " + xOffset
//					+ " " + yOffset + " " + xMax + " " + yMax);
//		}
	}
}
