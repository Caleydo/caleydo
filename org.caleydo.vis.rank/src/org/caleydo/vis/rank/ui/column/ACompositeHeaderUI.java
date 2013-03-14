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
import org.caleydo.vis.rank.config.IRankTableUIConfig;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.mixin.ICollapseableColumnMixin;
import org.caleydo.vis.rank.model.mixin.IGrabRemainingHorizontalSpace;
import org.caleydo.vis.rank.ui.GLPropertyChangeListeners;
import org.caleydo.vis.rank.ui.RenderStyle;
import org.caleydo.vis.rank.ui.SeparatorUI;
import org.caleydo.vis.rank.ui.SeparatorUI.IMoveHereChecker;

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

	protected final IRankTableUIConfig config;
	private boolean hasThick;

	private final int firstColumn;

	public ACompositeHeaderUI(IRankTableUIConfig config, int firstColumn) {
		this.config = config;
		setLayout(this);
		this.firstColumn = firstColumn;
	}

	protected void init(Iterable<ARankColumnModel> children) {
		boolean hasCurrentThick = false;
		for (ARankColumnModel col : children) {
			GLElement elem = wrap(col);
			if (elem instanceof IThickHeader)
				hasCurrentThick = true;
			this.add(elem);
			numColumns++;
		}
		if (config.isMoveAble()) {
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
		return new SeparatorUI(this, 0);
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
				setHasThick(Iterables.any(news, Predicates.instanceOf(IThickHeader.class)));
			}
			asList().addAll(index + firstColumn, news);
			if (config.isMoveAble()) {
				for (int i = 0; i < news.size(); ++i)
					add(createSeparator(0));
			}
		} else if (evt.getNewValue() == null) { // removed
			takeDown(get(index + firstColumn).getLayoutDataAs(ARankColumnModel.class, null));
			remove(index + firstColumn);
			numColumns--;
			if (config.isMoveAble())
				remove(this.size() - 1); // remove last separator
			setHasThick(Iterables.any(this, Predicates.instanceOf(IThickHeader.class)));
		} else { // replaced
			takeDown(get(index + firstColumn).getLayoutDataAs(ARankColumnModel.class, null));
			set(index + firstColumn, wrap((ARankColumnModel) evt.getNewValue()));
			setHasThick(Iterables.any(this, Predicates.instanceOf(IThickHeader.class)));
		}
	}

	private void init(ARankColumnModel col) {
		col.addPropertyChangeListener(ARankColumnModel.PROP_WIDTH, layoutOnChange);
		col.addPropertyChangeListener(ICollapseableColumnMixin.PROP_COLLAPSED, layoutOnChange);
	}

	private void takeDown(ARankColumnModel col) {
		col.removePropertyChangeListener(ARankColumnModel.PROP_WIDTH, layoutOnChange);
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
		float y = getTopPadding() + (hasThick ? (HIST_HEIGHT + LABEL_HEIGHT * 2) : 0);
		float hn = h - y;
		if (config.isMoveAble()) {
			separators = children.subList(numColumns + 1 + firstColumn, children.size());
			assert separators.size() == columns.size();
			final IGLLayoutElement sep0 = children.get(numColumns + firstColumn);
			sep0.setBounds(x, y, COLUMN_SPACE, hn); // left separator
		}
		for (int i = 0; i < columns.size(); ++i) {
			IGLLayoutElement col = columns.get(i);
			ARankColumnModel model = col.getLayoutDataAs(ARankColumnModel.class, null);
			float wi = getChildWidth(i, model);

			if (i == (columns.size() - 1) && model instanceof IGrabRemainingHorizontalSpace) {
				// catch all
				wi = w - x - RenderStyle.SCROLLBAR_WIDTH;
				col.setBounds(x, 0, wi, h);
			} else if ((x + wi + RenderStyle.COLUMN_SPACE) > w) {
				// hide the rest
				for (; i < columns.size(); ++i) {
					columns.get(i).hide();
				}
				if (config.isMoveAble() && separators != null) {
					for (; i < columns.size(); ++i)
						separators.get(i).hide();
				}
				break;
			}
			if (col.asElement() instanceof OrderColumnHeaderUI)
				col.setBounds(x, y + HIST_HEIGHT / 2, wi, hn - HIST_HEIGHT / 2);
			else if (col.asElement() instanceof IThickHeader)
				col.setBounds(x, 0, wi, h);
			else
				col.setBounds(x, y, wi, hn);
			x += wi + COLUMN_SPACE;
			if (config.isMoveAble() && separators != null) {
				IGLLayoutElement sep = separators.get(i);
				sep.setBounds(x - COLUMN_SPACE, y, COLUMN_SPACE, hn);
				((SeparatorUI) sep.asElement()).setIndex(i + 1);
			}
		}
	}

	protected abstract float getChildWidth(int i, ARankColumnModel model);

	protected float getLeftPadding() {
		return RenderStyle.COLUMN_SPACE;
	}

	protected float getTopPadding() {
		return 0;
	}
}

