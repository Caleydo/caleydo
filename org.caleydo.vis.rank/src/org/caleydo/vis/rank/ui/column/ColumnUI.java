package org.caleydo.vis.rank.ui.column;

import java.util.Iterator;
import java.util.List;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.vis.rank.internal.ui.anim.ReRankTransition;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.mixin.ICollapseableColumnMixin;
import org.caleydo.vis.rank.ui.IColumnRenderInfo;

public class ColumnUI extends AnimatedGLElementContainer implements ITableColumnUI, IGLLayout, IColumnRenderInfo {
	protected final ARankColumnModel model;

	public ColumnUI(ARankColumnModel model) {
		this.model = model;
		this.setLayoutData(model);
		this.setDefaultInTransition(ReRankTransition.INSTANCE);
		this.setDefaultMoveTransition(ReRankTransition.INSTANCE);
		this.setDefaultOutTransition(ReRankTransition.INSTANCE);
		this.setLayout(this);
	}

	/**
	 * @return the model, see {@link #model}
	 */
	@Override
	public ARankColumnModel getModel() {
		return model;
	}

	@Override
	public GLElement asGLElement() {
		return this;
	}

	@Override
	public ColumnUI setData(Iterable<IRow> rows, IColumModelLayout parent) {
		int s = size();
		int i = 0;
		Iterator<IRow> it = rows.iterator();
		while (it.hasNext() && i < s) {
			get(i++).setLayoutData(it.next());
		}
		if (i < s) {
			for (int j = s - 1; j >= i; j--)
				remove(get(j));
		} else {
			while (it.hasNext())
				this.add(model.createValue().setLayoutData(it.next()));
		}
		return this;
	}

	@Override
	public void update() {
		relayout();
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		final IColumModelLayout p = (IColumModelLayout) getParent();
		p.layoutRows(model, children, w, h);
	}

	@Override
	public boolean isCollapsed() {
		return ((model instanceof ICollapseableColumnMixin) && ((ICollapseableColumnMixin) model).isCollapsed());
	}

	@Override
	public VAlign getAlignment() {
		return getColumnParent().getAlignment(this);
	}

	protected IColumModelLayout getColumnParent() {
		return (IColumModelLayout) getParent();
	}

	@Override
	public boolean hasFreeSpace() {
		return getColumnParent().hasFreeSpace(this);
	}
}