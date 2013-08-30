/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.ui.column;

import static org.caleydo.vis.lineup.ui.RenderStyle.COLUMN_SPACE;
import static org.caleydo.vis.lineup.ui.RenderStyle.HIST_HEIGHT;

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.vis.lineup.config.IRankTableUIConfig;
import org.caleydo.vis.lineup.model.ACompositeRankColumnModel;
import org.caleydo.vis.lineup.model.ARankColumnModel;
import org.caleydo.vis.lineup.model.RankTableModel;
import org.caleydo.vis.lineup.model.mixin.ICollapseableColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IGrabRemainingHorizontalSpace;
import org.caleydo.vis.lineup.ui.GLPropertyChangeListeners;
import org.caleydo.vis.lineup.ui.RenderStyle;
import org.caleydo.vis.lineup.ui.SeparatorUI;
import org.caleydo.vis.lineup.ui.SeparatorUI.IMoveHereChecker;

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
			switch (evt.getPropertyName()) {
			case RankTableModel.PROP_COLUMNS:
			case ACompositeRankColumnModel.PROP_CHILDREN:
				onChildrenChanged((IndexedPropertyChangeEvent) evt);
				break;
			}
		}
	};

	protected final IRankTableUIConfig config;
	protected float thickOffset;

	private final int firstColumn;

	public ACompositeHeaderUI(IRankTableUIConfig config, int firstColumn) {
		this.config = config;
		setLayout(this);
		this.firstColumn = firstColumn;
	}

	protected void init(Iterable<ARankColumnModel> children) {
		for (ARankColumnModel col : children) {
			GLElement elem = wrap(col);
			this.add(elem);
			numColumns++;
		}
		if (config.isMoveAble()) {
			this.add(createSeparator(0)); // left
			for (int i = 0; i < numColumns; ++i)
				this.add(createSeparator(i + 1));
		}
		setHasThick(max(this, 0));
	}

	protected void setHasThick(float thickOffset) {
		if (this.thickOffset == thickOffset) {
			return;
		}
		this.thickOffset = thickOffset;
	}

	/**
	 * @return the config, see {@link #config}
	 */
	public IRankTableUIConfig getConfig() {
		return config;
	}

	protected SeparatorUI createSeparator(int index) {
		return new SeparatorUI(this, 0);
	}

	protected final float max(Iterable<?> its, float max) {
		boolean smallHeader = isSmallHeader();
		for (IThickHeader h : Iterables.filter(its, IThickHeader.class))
			max = Math.max(h.getTopPadding(smallHeader), max);
		return max;
	}

	@SuppressWarnings("unchecked")
	protected void onChildrenChanged(IndexedPropertyChangeEvent evt) {
		int index = evt.getIndex();
		if (evt.getOldValue() instanceof Integer) { // moved
			int movedFrom = (Integer) evt.getOldValue();
			add(index + firstColumn, get(movedFrom + firstColumn));
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
			setHasThick(max(news, thickOffset));
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
			setHasThick(max(this, 0));
		} else { // replaced
			takeDown(get(index + firstColumn).getLayoutDataAs(ARankColumnModel.class, null));
			set(index + firstColumn, wrap((ARankColumnModel) evt.getNewValue()));
			setHasThick(max(this, 0));
		}
	}

	protected final void onChildrenOrderChanged(final List<ARankColumnModel> children) {
		final List<GLElement> before = new ArrayList<>(asList().subList(0, firstColumn));
		final List<GLElement> after = new ArrayList<>(asList().subList(firstColumn + numColumns, size()));
		sortBy(new Comparator<GLElement>() {
			@Override
			public int compare(GLElement o1, GLElement o2) {
				int b1 = before.indexOf(o1);
				int b2 = before.indexOf(o2);
				if (b1 != -1 && b2 != -1)
					return b1 - b2;
				if (b1 != -1)
					return -1;
				if (b2 != -1)
					return 1;
				int a1 = after.indexOf(o1);
				int a2 = after.indexOf(o2);
				if (a1 != -1 && a2 != -1)
					return a1 - a2;
				if (a1 != -1)
					return 1;
				if (a2 != -1)
					return -1;

				// check model
				return children.indexOf(o1.getLayoutDataAs(ARankColumnModel.class, null))
						- children.indexOf(o2.getLayoutDataAs(ARankColumnModel.class, null));
			}
		});
	}

	private void init(ARankColumnModel col) {
		if (col == null)
			return;
		col.addPropertyChangeListener(ARankColumnModel.PROP_WIDTH, layoutOnChange);
		col.addPropertyChangeListener(ICollapseableColumnMixin.PROP_COLLAPSED, layoutOnChange);
	}

	private void takeDown(ARankColumnModel col) {
		if (col == null)
			return;
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

	protected abstract boolean isSmallHeader();

	protected final float layoutColumns(List<? extends IGLLayoutElement> children, float w, float h) {
		List<? extends IGLLayoutElement> columns = children.subList(firstColumn, numColumns + firstColumn);
		List<? extends IGLLayoutElement> separators = null;

		final boolean smallHeader = isSmallHeader();

		// align the columns normally
		float x = getLeftPadding();
		float y = getTopPadding(smallHeader) + thickOffset;
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

			if (i == (columns.size() - 1) && model instanceof IGrabRemainingHorizontalSpace && (!model.isCollapsed())) {
				// catch all
				wi = w - x - RenderStyle.SCROLLBAR_WIDTH;
				col.setBounds(x, 0, wi, h);
			}
			if (col.asElement() instanceof OrderColumnHeaderUI)
				if (smallHeader)
					col.setBounds(x, y, wi, hn);
				else
					col.setBounds(x, y + HIST_HEIGHT / 2, wi, hn - HIST_HEIGHT / 2);
			else if (col.asElement() instanceof IThickHeader) {
				float offset = thickOffset - ((IThickHeader) col.asElement()).getTopPadding(smallHeader);
				col.setBounds(x, offset, wi, h - offset);
			} else
				col.setBounds(x, y, wi, hn);
			x += wi + COLUMN_SPACE;
			if (config.isMoveAble() && separators != null) {
				IGLLayoutElement sep = separators.get(i);
				wi = COLUMN_SPACE;
				if (i == (columns.size() - 1)) {
					wi = w - x + COLUMN_SPACE;
				}
				sep.setBounds(x - COLUMN_SPACE, y, wi, hn);
				((SeparatorUI) sep.asElement()).setIndex(i + 1);
			}
		}
		return x;
	}

	protected abstract float getChildWidth(int i, ARankColumnModel model);

	protected float getLeftPadding() {
		return RenderStyle.COLUMN_SPACE;
	}

	protected float getTopPadding(boolean isSmallHeader) {
		return 0;
	}
}

