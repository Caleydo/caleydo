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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.data.selection.IEventBasedSelectionManagerUser;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IIDTypeMapper;
import org.caleydo.core.util.base.ILabeled;

/**
 * @author Samuel Gratzl
 *
 */
public class CategoryItem implements ILabeled, Comparable<CategoryItem> {
	private final IDCategory category;
	private final SelectionManager manager;
	private final IIDTypeMapper<Integer, String> prim2human;

	private final NavigableSet<SelectionTypeItem> children = new TreeSet<>();

	public CategoryItem(IDCategory category, IEventBasedSelectionManagerUser listener) {
		super();
		this.category = category;
		prim2human = IDMappingManagerRegistry.get().getIDMappingManager(category)
				.getIDTypeMapper(category.getPrimaryMappingType(), category.getHumanReadableIDType());
		this.manager = new EventBasedSelectionManager(listener, category.getPrimaryMappingType());
		update();
	}

	/**
	 * @return the manager, see {@link #manager}
	 */
	public SelectionManager getManager() {
		return manager;
	}

	/**
	 * @return the children, see {@link #children}
	 */
	public NavigableSet<SelectionTypeItem> getChildren() {
		return children;
	}

	/**
	 * @return the category, see {@link #category}
	 */
	public IDCategory getCategory() {
		return category;
	}

	@Override
	public String getLabel() {
		return category.getDenomination();
	}

	public boolean update() {
		boolean dirty = false;
		List<SelectionType> selectionTypes = new ArrayList<>(manager.getSelectionTypes());
		for (Iterator<SelectionType> it = selectionTypes.iterator(); it.hasNext(); ) {
			SelectionType selectionType = it.next();
			int count = manager.getNumberOfElements(selectionType);
			if (count == 0)
				it.remove();
			SelectionTypeItem item = new SelectionTypeItem(this, selectionType);
			if (!children.contains(item)) {
				children.add(item);
				dirty = true;
			}
		}
		return dirty;

	}

	/**
	 * @param id
	 * @return
	 */
	public String toLabel(Integer id) {
		Set<String> label = prim2human.apply(id);
		if (label.isEmpty())
			return "???" + id + "???";
		return label.iterator().next();
	}

	@Override
	public int compareTo(CategoryItem o) {
		return getLabel().compareToIgnoreCase(o.getLabel());
	}

	/**
	 * @return
	 */
	public int size() {
		return IDMappingManagerRegistry.get().getIDMappingManager(category).getPrimaryTypeCounter();
	}

	/**
	 * @return
	 */
	public int getTotal() {
		return manager.getNumberOfElements();
	}

}
