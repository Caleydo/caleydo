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
package org.caleydo.view.tourguide.internal.view.ui;

import gleem.linalg.Vec2f;

import java.util.Comparator;
import java.util.List;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.view.tourguide.internal.model.ADataDomainQuery;
import org.caleydo.view.tourguide.internal.model.CategoricalDataDomainQuery;
import org.caleydo.view.tourguide.internal.model.InhomogenousDataDomainQuery;
import org.caleydo.view.tourguide.internal.model.PathwayDataDomainQuery;
import org.caleydo.view.tourguide.internal.model.StratificationDataDomainQuery;
import org.caleydo.view.tourguide.internal.view.specific.IDataDomainQueryModeSpecfics;

import com.google.common.collect.Iterables;

public class DataDomainQueryUI extends GLElementContainer implements IGLLayout, Comparator<GLElement>, IPickingListener {

	private IDataDomainQueryModeSpecfics specifics;

	private int counter = 0;

	public DataDomainQueryUI(Iterable<ADataDomainQuery> queries,
			IDataDomainQueryModeSpecfics specifics) {
		super();
		setLayout(this);
		this.specifics = specifics;

		for (ADataDomainQuery q : queries) {
			add(createFor(q));
		}

		sortBy(this);

		setLayoutData(new Vec2f(130, guessMultiColumnHeight()));
	}

	@Override
	public void pick(Pick pick) {
		if (pick.getPickingMode() == PickingMode.DOUBLE_CLICKED) {
			int id = pick.getObjectID();
			// deactivate all others
			for (ADataDomainElement child : Iterables.filter(this, ADataDomainElement.class)) {
				if (id != child.getPickingObjectId() && child.getModel().isActive())
					child.setSelected(false);
				System.out.println(child.getModel() + " " + child.getModel().isActive());
			}
		}
	}
	/**
	 * @return
	 */
	private float guessMultiColumnHeight() {
		float y = 2;

		int actCat = 0;
		float actMaxY = y;
		for (ADataDomainElement child : Iterables.filter(this, ADataDomainElement.class)) {
			int cat = specifics.getCategory(child.getModel().getDataDomain());
			if (actCat != cat) {
				actCat = cat;
				if (y > actMaxY)
					actMaxY = y;
				y = 2;
			}
			y += 20;
		}
		return Math.max(y, actMaxY);
	}

	@Override
	public int compare(GLElement o1, GLElement o2) {
		ADataDomainQuery a1 = o1.getLayoutDataAs(ADataDomainQuery.class, null);
		ADataDomainQuery a2 = o2.getLayoutDataAs(ADataDomainQuery.class, null);

		return specifics.getCategory(a1.getDataDomain()) - specifics.getCategory(a2.getDataDomain());
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		if (w < 130 * 2) { // linear
			float y = 2;
			w -= 4;

			int actCat = 0;
			for (IGLLayoutElement child : children) {
				int cat = specifics.getCategory(child.getLayoutDataAs(ADataDomainQuery.class, null).getDataDomain());
				if (actCat != cat) {
					y += 2;
					actCat = cat;
				}
				child.setBounds(2, y, w, 18);
				y += 20;
			}
		} else {
			// in blocks
			float x = 2;
			float y = 2;
			w -= 4;

			int actCat = 0;
			float actMaxY = y;
			for (IGLLayoutElement child : children) {
				int cat = specifics.getCategory(child.getLayoutDataAs(ADataDomainQuery.class, null).getDataDomain());
				if (actCat != cat) {
					x += w * (1.f / specifics.getNumCategories());
					actCat = cat;
					if (y > actMaxY)
						actMaxY = y;
					y = 2;
				}
				child.setBounds(x, y, w * 1.f / specifics.getNumCategories() - 2, 18);
				y += 20;
			}
		}
	}

	private ADataDomainElement createFor(ADataDomainQuery q) {
		ADataDomainElement elem = wrap(q);
		elem.onPick(this);
		elem.setPickingObjectId(++counter);
		return elem;
	}

	private ADataDomainElement wrap(ADataDomainQuery q) {
		if (q instanceof CategoricalDataDomainQuery)
			return new CategoricalDataDomainElement((CategoricalDataDomainQuery) q);
		if (q instanceof PathwayDataDomainQuery)
			return new PathwayDataDomainElement((PathwayDataDomainQuery) q);
		if (q instanceof StratificationDataDomainQuery)
			return new TableDataDomainElement((StratificationDataDomainQuery) q);
		if (q instanceof InhomogenousDataDomainQuery)
			return new ColumnDataDomainElement((InhomogenousDataDomainQuery) q);
		throw new IllegalStateException();
	}

	public void updateSelections() {
		for (ADataDomainElement elem : Iterables.filter(this, ADataDomainElement.class)) {
			elem.updateSelection();
		}
	}

	/**
	 * @param query
	 */
	public void add(ADataDomainQuery query) {
		this.add(createFor(query));
		sortBy(this);
		setLayoutData(new Vec2f(150, guessMultiColumnHeight()));
		relayoutParent();
	}

	/**
	 * @param query
	 */
	public void remove(ADataDomainQuery query) {
		for (ADataDomainElement elem : Iterables.filter(this, ADataDomainElement.class)) {
			if (elem.getModel() == query) {
				remove(elem);
				break;
			}
		}
		setLayoutData(new Vec2f(150, guessMultiColumnHeight()));
		relayoutParent();
	}
}