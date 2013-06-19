/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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
