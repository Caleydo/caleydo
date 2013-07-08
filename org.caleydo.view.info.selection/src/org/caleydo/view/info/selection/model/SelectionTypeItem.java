/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.info.selection.model;

import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.util.base.ILabeled;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * @author Samuel Gratzl
 *
 */
public class SelectionTypeItem implements ILabeled, Comparable<SelectionTypeItem> {
	private final CategoryItem parent;
	private final SelectionType selectionType;
	private final Color color;

	public SelectionTypeItem(CategoryItem parent, SelectionType selectionType) {
		super();
		this.parent = parent;
		this.selectionType = selectionType;
		this.color = selectionType.getColor().getSWTColor(Display.getCurrent());
	}

	/**
	 * @return the color, see {@link #color}
	 */
	public Color getColor() {
		return color;
	}

	public NavigableSet<ElementItem> getChildren() {
		NavigableSet<ElementItem> items = new TreeSet<>();
		Set<Integer> elements = parent.getManager().getElements(selectionType);
		if (elements != null)
			for (Integer id : elements) {
				items.add(new ElementItem(this, id, parent.toLabel(id)));
			}
		return items;
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public int size() {
		return parent.getManager().getNumberOfElements(selectionType);
	}

	/**
	 * @return the parent, see {@link #parent}
	 */
	public CategoryItem getParent() {
		return parent;
	}

	/**
	 * @return the selectionType, see {@link #selectionType}
	 */
	public SelectionType getSelectionType() {
		return selectionType;
	}

	@Override
	public String getLabel() {
		return selectionType.getType();
	}

	public boolean isDefaultOne() {
		return selectionType == SelectionType.SELECTION || selectionType == SelectionType.MOUSE_OVER;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((selectionType == null) ? 0 : selectionType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SelectionTypeItem other = (SelectionTypeItem) obj;
		if (selectionType == null) {
			if (other.selectionType != null)
				return false;
		} else if (!selectionType.equals(other.selectionType))
			return false;
		return true;
	}

	public String getPreview() {
		StringBuilder b = new StringBuilder();
		int i = 0;
		for (ElementItem item : getChildren()) {
			if (i++ >= 3)
				break;
			b.append(item.getLabel()).append(", ");
		}
		if (b.length() > 0)
			b.setLength(b.length() - 2);
		return b.toString();
	}

	@Override
	public int compareTo(SelectionTypeItem o) {
		return selectionType.compareTo(o.selectionType);
	}
}
