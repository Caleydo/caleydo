package org.caleydo.view.treemap.layout.algorithm;

import org.caleydo.view.treemap.layout.ATreeMapNode;

public interface ILayoutAlgorithm {

	public static final int SIMPLE_LAYOUT_ALGORITHM = 0;
	public static final int SQUARIFIED_LAYOUT_ALGORITHM=1;
	
	public void layout(ATreeMapNode tree);

}
