package org.caleydo.core.view.opengl.layout;

import java.util.ArrayList;
import java.util.Iterator;

public class LayoutContainer
	extends ElementLayout
	implements Iterable<ElementLayout> {

	ArrayList<ElementLayout> rowElements;

	public LayoutContainer() {
		rowElements = new ArrayList<ElementLayout>();
	}

	public void appendElement(ElementLayout renderParameter) {
		rowElements.add(renderParameter);
	}

	@Override
	public Iterator<ElementLayout> iterator() {
		return rowElements.iterator();
	}

}
