package org.caleydo.core.view.opengl.layout;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A row for a row-layout. The row is a {@link LayoutParameters} element and
 * contains other LayoutParameters
 * 
 * @author Alexander Lex
 * 
 */
public class Row extends LayoutParameters implements Iterable<LayoutParameters> {

	ArrayList<LayoutParameters> rowElements;

	public Row() {
		rowElements = new ArrayList<LayoutParameters>();
	}

	public void appendElement(LayoutParameters renderParameter) {
		rowElements.add(renderParameter);
	}

	@Override
	public Iterator<LayoutParameters> iterator() {
		return rowElements.iterator();
	}

}
