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
package org.caleydo.vis.rank.ui.column;

import static org.caleydo.vis.rank.ui.RenderStyle.COLUMN_SPACE;
import static org.caleydo.vis.rank.ui.RenderStyle.HIST_HEIGHT;
import static org.caleydo.vis.rank.ui.RenderStyle.LABEL_HEIGHT;

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.mixin.ICollapseableColumnMixin;
import org.caleydo.vis.rank.ui.GLPropertyChangeListeners;
import org.caleydo.vis.rank.ui.RenderStyle;
import org.caleydo.vis.rank.ui.SeparatorUI;
import org.caleydo.vis.rank.ui.SeparatorUI.IMoveHereChecker;
import org.caleydo.vis.rank.ui.StackedSeparatorUI;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
/**
 * @author Samuel Gratzl
 *
 */
public abstract class ACompositeHeaderUI extends GLElementContainer implements IGLLayout, IMoveHereChecker {
	protected int numColumns = 0;

	private final PropertyChangeListener layoutOnChange = GLPropertyChangeListeners.relayoutOnEvent(this);
	protected final PropertyChangeListener childrenChanged = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			onChildrenChanged((IndexedPropertyChangeEvent) evt);
		}
	};

	protected final boolean interactive;
	private boolean hasThick;

	private final int firstColumn;

	public ACompositeHeaderUI(boolean interactive, int firstColumn) {
		this.interactive = interactive;
		setLayout(this);
		this.firstColumn = firstColumn;
	}

	protected void init(Iterable<ARankColumnModel> children) {
		boolean hasCurrentThick = false;
		for (ARankColumnModel col : children) {
			GLElement elem = wrap(col);
			if (elem instanceof ACompositeHeaderUI)
				hasCurrentThick = true;
			this.add(elem);
			numColumns++;
		}
		if (interactive) {
			this.add(createSeparator(0)); // left
			for (int i = 0; i < numColumns; ++i)
				this.add(createSeparator(i + 1));
		}
		setHasThick(hasCurrentThick);
	}

	protected void setHasThick(boolean hasThick) {
		if (this.hasThick == hasThick) {
			return;
		}
		this.hasThick = hasThick;
	}

	protected SeparatorUI createSeparator(int index) {
		return new StackedSeparatorUI(this, 0);
	}

	@SuppressWarnings("unchecked")
	protected void onChildrenChanged(IndexedPropertyChangeEvent evt) {
		int index = evt.getIndex();
		if (evt.getOldValue() instanceof Integer) { // moved
			int movedFrom = (Integer) evt.getOldValue();
			add(index + firstColumn, get(movedFrom));
		} else if (evt.getOldValue() == null) { // new
			Collection<GLElement> news = null;
			if (evt.getNewValue() instanceof ARankColumnModel) {
				news = Collections.singleton(wrap((ARankColumnModel) evt.getNewValue()));
			} else {
				news = new ArrayList<>();
				for (ARankColumnModel c : (Collection<ARankColumnModel>) evt.getNewValue())
					news.add(wrap(c));
			}
			numColumns += news.size();
			if (!hasThick) {
				setHasThick(Iterables.any(news, Predicates.instanceOf(ACompositeHeaderUI.class)));
			}
			asList().addAll(index + firstColumn, news);
			if (interactive) {
				for (int i = 0; i < news.size(); ++i)
					add(createSeparator(0));
			}
		} else if (evt.getNewValue() == null) { // removed
			takeDown(get(index + firstColumn).getLayoutDataAs(ARankColumnModel.class, null));
			remove(index + firstColumn);
			numColumns--;
			if (interactive)
				remove(this.size() - 1); // remove last separator
			setHasThick(Iterables.any(this, Predicates.instanceOf(ACompositeHeaderUI.class)));
		} else { // replaced
			takeDown(get(index + firstColumn).getLayoutDataAs(ARankColumnModel.class, null));
			set(index + firstColumn, wrap((ARankColumnModel) evt.getNewValue()));
			setHasThick(Iterables.any(this, Predicates.instanceOf(ACompositeHeaderUI.class)));
		}
	}

	private void init(ARankColumnModel col) {
		col.addPropertyChangeListener(ARankColumnModel.PROP_WEIGHT, layoutOnChange);
		col.addPropertyChangeListener(ICollapseableColumnMixin.PROP_COLLAPSED, layoutOnChange);
	}

	private void takeDown(ARankColumnModel col) {
		col.removePropertyChangeListener(ARankColumnModel.PROP_WEIGHT, layoutOnChange);
		col.removePropertyChangeListener(ICollapseableColumnMixin.PROP_COLLAPSED, layoutOnChange);
	}

	protected abstract GLElement wrapImpl(ARankColumnModel model);

	private GLElement wrap(ARankColumnModel model) {
		init(model);
		return wrapImpl(model);
	}

	@Override
	protected void takeDown() {
		for (GLElement col : asList().subList(0, numColumns)) {
			takeDown(col.getLayoutDataAs(ARankColumnModel.class, null));
		}
		super.takeDown();
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		List<? extends IGLLayoutElement> columns = children.subList(firstColumn, numColumns + firstColumn);
		List<? extends IGLLayoutElement> separators = null;

		// align the columns normally
		float x = getLeftPadding();
		float y = getTopPadding() + (hasThick ? HIST_HEIGHT + LABEL_HEIGHT : 0);
		float hn = h - y;
		if (interactive) {
			separators = children.subList(numColumns + 1 + firstColumn, children.size());
			assert separators.size() == columns.size();
			final IGLLayoutElement sep0 = children.get(numColumns + firstColumn);
			sep0.setBounds(x + 3, y, COLUMN_SPACE, hn); // left separator
		}
		for (int i = 0; i < columns.size(); ++i) {
			IGLLayoutElement col = columns.get(i);
			ARankColumnModel model = col.getLayoutDataAs(ARankColumnModel.class, null);
			if (col.asElement() instanceof ACompositeTableColumnHeaderUI)
				col.setBounds(x, 0, model.getPreferredWidth(), h);
			else
				col.setBounds(x, y, model.getPreferredWidth(), hn);
			x += model.getPreferredWidth() + COLUMN_SPACE;
			if (interactive && separators != null) {
				IGLLayoutElement sep = separators.get(i);
				sep.setBounds(x - COLUMN_SPACE, y, COLUMN_SPACE, hn);
				((SeparatorUI) sep.asElement()).setIndex(i + 1);
			}
		}
	}

	protected float getLeftPadding() {
		return RenderStyle.COLUMN_SPACE;
	}

	protected float getTopPadding() {
		return 0;
	}
}

