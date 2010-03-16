package org.caleydo.view.heatmap.heatmap.template;

import java.util.ArrayList;
import java.util.Iterator;

public class Row extends RenderParameters implements Iterable<RenderParameters> {

	ArrayList<RenderParameters> rowElements;

	public Row() {
		rowElements = new ArrayList<RenderParameters>();
	}

	public void appendElement(RenderParameters renderParameter) {
		rowElements.add(renderParameter);
	}

	@Override
	public Iterator<RenderParameters> iterator() {
		return rowElements.iterator();
	}

}
