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
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.internal.view.model.ADataDomainQuery;
import org.caleydo.view.tourguide.internal.view.model.CategoricalDataDomainQuery;
import org.caleydo.view.tourguide.internal.view.model.PathwayDataDomainQuery;
import org.caleydo.view.tourguide.internal.view.model.TableDataDomainQuery;

import com.google.common.collect.Iterables;

public class DataDomainQueryUI extends GLElementContainer implements IGLLayout, Comparator<GLElement> {

	public DataDomainQueryUI(Iterable<ADataDomainQuery> queries) {
		super();
		setLayout(this);

		for (ADataDomainQuery q : queries) {
			add(createFor(q));
		}

		sortBy(this);

		setLayoutData(new Vec2f(130, guessMultiColumnHeight()));
	}

	/**
	 * @return
	 */
	private float guessMultiColumnHeight() {
		float y = 2;

		EDataDomainQueryMode act = EDataDomainQueryMode.values()[0];
		int actCat = 0;
		float actMaxY = y;
		for (ADataDomainElement child : Iterables.filter(this, ADataDomainElement.class)) {
			EDataDomainQueryMode mode = child.getModel().getMode();
			int cat = mode.getCategory(child.getModel().getDataDomain());
			if (mode != act) {
				y = actMaxY + 8;
				actMaxY = y;
				act = mode;
				actCat = cat;
			} else if (actCat != cat) {
				actCat = cat;
				if (y > actMaxY)
					actMaxY = y;
				y = 2;
			}
			y += 20;
		}
		return y;
	}

	@Override
	public int compare(GLElement o1, GLElement o2) {
		ADataDomainQuery a1 = o1.getLayoutDataAs(ADataDomainQuery.class, null);
		ADataDomainQuery a2 = o2.getLayoutDataAs(ADataDomainQuery.class, null);

		EDataDomainQueryMode m1 = a1.getMode();
		EDataDomainQueryMode m2 = a2.getMode();
		if (m1 != m2)
			return m1.ordinal() - m2.ordinal();
		return m1.getCategory(a1.getDataDomain()) - m2.getCategory(a2.getDataDomain());
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		if (w < 130 * 2) { // linear
			float y = 2;
			w -= 4;

			EDataDomainQueryMode act = EDataDomainQueryMode.values()[0];
			int actCat = 0;
			for (IGLLayoutElement child : children) {
				EDataDomainQueryMode mode = child.getLayoutDataAs(ADataDomainQuery.class, null).getMode();
				int cat = mode.getCategory(child.getLayoutDataAs(ADataDomainQuery.class, null).getDataDomain());
				if (mode != act) {
					y += 6;
					act = mode;
					actCat = cat;
				} else if (actCat != cat) {
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

			EDataDomainQueryMode act = EDataDomainQueryMode.values()[0];
			int actCat = 0;
			float actMaxY = y;
			for (IGLLayoutElement child : children) {
				EDataDomainQueryMode mode = child.getLayoutDataAs(ADataDomainQuery.class, null).getMode();
				int cat = mode.getCategory(child.getLayoutDataAs(ADataDomainQuery.class, null).getDataDomain());
				if (mode != act) {
					x = 2;
					y = actMaxY + 8;
					actMaxY = y;
					act = mode;
					actCat = cat;
				} else if (actCat != cat) {
					x += w * (1.f / mode.getNumCategories());
					actCat = cat;
					if (y > actMaxY)
						actMaxY = y;
					y = 2;
				}
				child.setBounds(x, y, w * 1.f / mode.getNumCategories() - 2, 18);
				y += 20;
			}
		}
		// // super(new GLFlowLayout(false, 5, new GLPadding(5)));
		// float total = 10;
		// for (EDataDomainQueryMode mode : EDataDomainQueryMode.values()) {
		// if (mode.getNumCategories() > 1) {
		// GLElementContainer c = new GLElementContainer(GLLayouts.flowHorizontal(2));
		// GLElementContainer[] cs = new GLElementContainer[mode.getNumCategories()];
		// for(int i = 0; i < cs.length; ++i)
		// cs[i] = new GLElementContainer(GLLayouts.flowVertical(2));
		// for (ADataDomainQuery q : queries) {
		// if (mode.isCompatible(q.getDataDomain()))
		// cs[mode.getCategory(q.getDataDomain())].add(createFor(q));
		// }
		//
		// for (int i = 0; i < cs.length; ++i) {
		// cs[i].setSize(-1, cs[i].size() * 20);
		// c.add(cs[i]);
		// }
		// c.pack(false, true);
		// this.add(c);
		// total += c.getSize().y();
		// } else {
		// GLElementContainer c = new GLElementContainer(GLLayouts.flowVertical(2));
		// for (ADataDomainQuery q : queries) {
		// if (mode.isCompatible(q.getDataDomain()))
		// c.add(createFor(q));
		// }
		// c.setSize(-1, c.size() * 20);
		// this.add(c);
		// total += c.getSize().y();
		// }
		// }
	}

	/**
	 * @param children
	 * @return
	 */
	private int[] computeBlockSizes(List<? extends IGLLayoutElement> children) {
		int total = 0;
		for (EDataDomainQueryMode mode : EDataDomainQueryMode.values())
			total += mode.getNumCategories();
		int[] r = new int[total];
		int i = 0;
		EDataDomainQueryMode act = EDataDomainQueryMode.values()[0];
		int actCat = 0;
		for (IGLLayoutElement child : children) {
			EDataDomainQueryMode mode = child.getLayoutDataAs(ADataDomainQuery.class, null).getMode();
			int cat = mode.getCategory(child.getLayoutDataAs(ADataDomainQuery.class, null).getDataDomain());
			if (mode != act || actCat != cat) {
				i++;
				act = mode;
				actCat = cat;
			}
			r[i]++;
		}
		return null;
	}

	private ADataDomainElement createFor(ADataDomainQuery q) {
		if (q instanceof CategoricalDataDomainQuery)
			return new CategoricalDataDomainElement((CategoricalDataDomainQuery) q);
		if (q instanceof PathwayDataDomainQuery)
			return new PathwayDataDomainElement((PathwayDataDomainQuery) q);
		return new TableDataDomainElement((TableDataDomainQuery) q);
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