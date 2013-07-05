/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.treemap.layout.algorithm;

import org.caleydo.view.treemap.layout.ATreeMapNode;

/**
 * Simple and fast layout algorithm which doesn't cares about ration of
 * displayed nodes.
 * 
 * @author Michael Lafer
 * 
 */

public class SimpleLayoutAlgorithm implements ILayoutAlgorithm {

	@Override
	public void layout(ATreeMapNode tree) {

		paintHelp(tree, 0, 0, 1, 1, HORIZONTAL_ALIGNMENT);

	}

	private static final boolean HORIZONTAL_ALIGNMENT = true;
	@SuppressWarnings("unused")
	private static final boolean VERTICAL_ALIGNMENT = false;

	private void paintHelp(ATreeMapNode root, float xOffset, float yOffset, float xMax, float yMax, boolean alignment) {
		root.setMinX(xOffset);
		root.setMinY(yOffset);
		root.setMaxX(xMax);
		root.setMaxY(yMax);

		if (root.getChildren() != null && root.getChildren().size() > 0) {
			float area = (xMax - xOffset) * (yMax - yOffset);
			if (alignment == HORIZONTAL_ALIGNMENT) {
				float x = xOffset;
				float size = 0;
				for (ATreeMapNode node : root.getChildren()) {
					size = (node.getSize() / area) * (xMax - xOffset);
					paintHelp(node, x, yOffset, x + size, yMax, !alignment);
					x += size;
				}
			} else {
				float y = yOffset;
				float size;
				for (ATreeMapNode node : root.getChildren()) {
					size = (node.getSize() / area) * (yMax - yOffset);
					paintHelp(node, xOffset, y, xMax, y + size, !alignment);
					y += size;
				}
			}
		}
		// else {
		// // painter.paintRectangle(xOffset, yOffset, xMax, yMax,
		// // root.getColorAttribute());
		//
		// // System.out.println("painting "+root.getAreaColor());
		// System.out.println("painting: " + root.getLabel() + " " + xOffset
		// + " " + yOffset + " " + xMax + " " + yMax);
		// }
	}
}
