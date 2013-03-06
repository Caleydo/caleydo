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
package org.caleydo.vis.rank.ui;

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
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.data.loader.ResourceLocators;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.model.mixin.ICollapseableColumnMixin;
import org.caleydo.vis.rank.ui.SeparatorUI.IMoveHereChecker;
import org.caleydo.vis.rank.ui.column.ACompositeTableColumnHeaderUI;
import org.caleydo.vis.rank.ui.column.ColumnUIs;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

/**
 * a visualzation of the table header row, in HTML it would be the thead section
 *
 * @author Samuel Gratzl
 *
 */
public final class TableHeaderUI extends GLElementContainer implements IGLLayout, IMoveHereChecker {
	private final RankTableModel table;
	private final PropertyChangeListener layoutOnChange = GLPropertyChangeListeners.relayoutOnEvent(this);
	private final PropertyChangeListener columnsChanged = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			onColumnsChanged((IndexedPropertyChangeEvent) evt);
		}
	};

	private int numColumns = 0;
	private final boolean interactive;
	private boolean hasThick;

	public TableHeaderUI(RankTableModel table) {
		this.table = table;
		this.table.addPropertyChangeListener(RankTableModel.PROP_COLUMNS, columnsChanged);
		this.interactive = table.getConfig().isInteractive();

		boolean hasCurrentThick = false;
		for (ARankColumnModel col : table.getColumns()) {
			GLElement elem = wrap(col);
			if (elem instanceof ACompositeTableColumnHeaderUI)
				hasCurrentThick = true;
			this.add(elem);
			numColumns++;
		}
		if (interactive) {
			this.add(new SeparatorUI(this, -1)); // left
			for (int i = 0; i < numColumns; ++i)
				this.add(new SeparatorUI(this, i));
		}
		setLayout(this);
		setSize(-1, (HIST_HEIGHT + LABEL_HEIGHT) * 1);
		setHasThick(hasCurrentThick);
	}

	private void init(ARankColumnModel col) {
		col.addPropertyChangeListener(ARankColumnModel.PROP_WEIGHT, layoutOnChange);
		col.addPropertyChangeListener(ICollapseableColumnMixin.PROP_COLLAPSED, layoutOnChange);
	}

	private void takeDown(ARankColumnModel col) {
		col.removePropertyChangeListener(ARankColumnModel.PROP_WEIGHT, layoutOnChange);
		col.removePropertyChangeListener(ICollapseableColumnMixin.PROP_COLLAPSED, layoutOnChange);
	}

	/**
	 * @param evt
	 */
	protected void onColumnsChanged(IndexedPropertyChangeEvent evt) {
		int index = evt.getIndex();
		if (evt.getOldValue() instanceof Integer) { // moved
			int movedFrom = (Integer) evt.getOldValue();
			add(index, get(movedFrom));
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
				setHasThick(Iterables.any(news, Predicates.instanceOf(ACompositeTableColumnHeaderUI.class)));
			}
			asList().addAll(index, news);
			if (interactive) {
				for (int i = 0; i < news.size(); ++i)
					add(new SeparatorUI(this));
			}
		} else if (evt.getNewValue() == null) { // removed
			takeDown(get(index).getLayoutDataAs(ARankColumnModel.class, null));
			remove(index);
			numColumns--;
			if (interactive)
				remove(this.size() - 1); // remove last separator
			setHasThick(Iterables.any(this, Predicates.instanceOf(ACompositeTableColumnHeaderUI.class)));
		} else { // replaced
			takeDown(get(index).getLayoutDataAs(ARankColumnModel.class, null));
			set(index, wrap((ARankColumnModel) evt.getNewValue()));
			setHasThick(Iterables.any(this, Predicates.instanceOf(ACompositeTableColumnHeaderUI.class)));
		}
	}

	/**
	 * @param any
	 */
	private void setHasThick(boolean hasThick) {
		if (this.hasThick == hasThick) {
			return;
		}
		this.hasThick = hasThick;
		setSize(-1, (HIST_HEIGHT + LABEL_HEIGHT) * (hasThick ? 2 : 1));
	}

	private GLElement wrap(ARankColumnModel model) {
		init(model);
		return ColumnUIs.createHeader(model, interactive, true);
	}

	@Override
	protected void takeDown() {
		this.table.removePropertyChangeListener(RankTableModel.PROP_COLUMNS, columnsChanged);
		for (GLElement col : asList().subList(0, numColumns)) {
			takeDown(col.getLayoutDataAs(ARankColumnModel.class, null));
		}
		super.takeDown();
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.pushResourceLocator(ResourceLocators.classLoader(this.getClass().getClassLoader()));
		super.renderImpl(g, w, h);
		g.popResourceLocator();
	}


	/**
	 * layout cols
	 */
	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		//align the columns normally
		float x = COLUMN_SPACE;

		List<? extends IGLLayoutElement> columns = children.subList(0, numColumns);

		float y = hasThick ? HIST_HEIGHT + LABEL_HEIGHT : 0;
		float hn = hasThick ? h - HIST_HEIGHT - LABEL_HEIGHT : h;
		List<? extends IGLLayoutElement> separators = null;
		if (interactive) {
			separators = children.subList(numColumns + 1, children.size());
			assert separators.size() == columns.size();
			children.get(numColumns).setBounds(0, y, COLUMN_SPACE, hn); // left separator
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
				((SeparatorUI) sep.asElement()).setIndex(i);
			}
		}
	}

	@Override
	public boolean canMoveHere(int index, ARankColumnModel model) {
		return table.isMoveAble(model, index + 1);
	}

	@Override
	public void moveHere(int index, ARankColumnModel model) {
		assert canMoveHere(index, model);
		this.table.move(model, index + 1);
	}
}


