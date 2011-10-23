package org.caleydo.view.datagraph.datacontainer.matrix;

import java.util.ArrayList;
import java.util.List;

public class CellContainer {
	protected String id;
	protected String caption;
	protected int numSubdivisions;
	protected float position;
	protected boolean isVisible;
	protected List<CellContainer> childContainers = new ArrayList<CellContainer>();
	protected CellContainer parentContainer;
}
