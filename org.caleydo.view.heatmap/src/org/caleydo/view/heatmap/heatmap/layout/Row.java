package org.caleydo.view.heatmap.heatmap.layout;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A row for a row-layout. The row is a {@link RenderParameters} element and
 * contains other RenderParameters
 * 
 * @author Alexander Lex
 * 
 */
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
