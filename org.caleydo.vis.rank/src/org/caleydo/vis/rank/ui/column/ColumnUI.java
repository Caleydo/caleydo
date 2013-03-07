package org.caleydo.vis.rank.ui.column;

import java.util.Iterator;
import java.util.List;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.animation.ALayoutAnimation;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.vis.rank.internal.ui.anim.ReRankTransition;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.mixin.ICollapseableColumnMixin;
import org.caleydo.vis.rank.model.mixin.IRankableColumnMixin;
import org.caleydo.vis.rank.ui.IColumnRenderInfo;

public class ColumnUI extends AnimatedGLElementContainer implements ITableColumnUI, IGLLayout, IColumnRenderInfo {
	private final ARankColumnModel model;

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
	protected ALayoutAnimation createMoveAnimation(IGLLayoutElement elem) {
		if (!getColumnParent().causesReorderingLayouting()) {
			this.setDefaultMoveTransition(null);
			ALayoutAnimation anim = super.createMoveAnimation(elem);
			this.setDefaultMoveTransition(ReRankTransition.INSTANCE);
			return anim;
		}
		return super.createMoveAnimation(elem);
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

	private IColumModelLayout getColumnParent() {
		return (IColumModelLayout) getParent();
	}

	@Override
	public boolean hasFreeSpace() {
		return getColumnParent().hasFreeSpace(this);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		if (model instanceof IRankableColumnMixin) {
			// IRankableColumnMixin m = (IRankableColumnMixin) model;
			//
			// final GL2 gl = g.gl;
			// final float z = g.z();
			// // render the quad strip of between the columns
			// g.color(model.getBgColor());
			// gl.glBegin(GL2.GL_QUAD_STRIP);
			// gl.glVertex3f(0, 0, z);
			// gl.glVertex3f(w, 0, z);
			// for (GLElement elem : this) {
			// Vec2f xy = elem.getLocation();
			// Vec2f wh = elem.getSize();
			// if (wh.x() <= 0)
			// break;
			// float v = m.getValue(elem.getLayoutDataAs(IRow.class, null));
			// gl.glVertex3f(xy.x(), xy.y() + 2, z);
			// gl.glVertex3f(xy.x() + w * v, xy.y() + 2, z);
			// gl.glVertex3f(xy.x(), xy.y() + wh.y() - 2, z);
			// gl.glVertex3f(xy.x() + w * v, xy.y() + wh.y() - 2, z);
			// }
			// g.gl.glEnd();
			//

		}
		super.renderImpl(g, w, h);
	}
}