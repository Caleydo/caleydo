package org.caleydo.view.datagraph.datacontainer.matrix;

import java.util.ArrayList;
import java.util.List;

public class CellContainer implements Comparable<CellContainer> {
	protected String id;
	protected String caption;
	protected int numSubdivisions;
	protected float position;
	protected boolean isVisible;
	protected boolean isCollapsed;
	protected List<CellContainer> childContainers = new ArrayList<CellContainer>();
	protected CellContainer parentContainer;

	@Override
	public int compareTo(CellContainer o) {
		return caption.compareTo(o.caption);
	}
}
