package org.caleydo.vis.rank.ui.column;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec4f;

import java.util.BitSet;
import java.util.List;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.IGLElementParent;
import org.caleydo.core.view.opengl.layout2.animation.AAnimation.EAnimationType;
import org.caleydo.core.view.opengl.layout2.animation.ALayoutAnimation;
import org.caleydo.core.view.opengl.layout2.animation.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.animation.DummyAnimation;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.vis.rank.internal.ui.anim.ReRankTransition;
import org.caleydo.vis.rank.layout.IRowLayoutInstance.IRowSetter;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.model.mixin.ICollapseableColumnMixin;
import org.caleydo.vis.rank.ui.IColumnRenderInfo;
import org.caleydo.vis.rank.ui.TableBodyUI;
import org.caleydo.vis.rank.ui.detail.ValueElement;

import com.jogamp.common.util.IntIntHashMap;

public class ColumnUI extends AnimatedGLElementContainer implements ITableColumnUI, IGLLayout, IColumnRenderInfo {
	private static final int INITIAL_POOL_SIZE = 100;

	protected static final int FLAG_FROM_ABOVE = 1;
	protected static final int FLAG_FROM_BELOW = 2;
	protected static final int FLAG_TO_ABOVE = 3;
	protected static final int FLAG_TO_BELOW = 4;
	protected static final int FLAG_NONE = 0;

	protected final ARankColumnModel model;

	private final IntIntHashMap rowIndexToGlElement = new IntIntHashMap();
	private final BitSet before = new BitSet();

	private final BitSet inPool = new BitSet(INITIAL_POOL_SIZE);

	public ColumnUI(ARankColumnModel model) {
		this.model = model;
		rowIndexToGlElement.setKeyNotFoundValue(-1);
		this.setLayoutData(model);
		this.setDefaultInTransition(ReRankTransition.INSTANCE);
		this.setDefaultMoveTransition(ReRankTransition.INSTANCE);
		this.setDefaultOutTransition(ReRankTransition.INSTANCE);
		this.setLayout(this);
	}

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);
		TableBodyUI body = findTableBodyUI();
		for (int i = 0; i < INITIAL_POOL_SIZE; ++i) {
			this.add(createPoolItem(body), 0);
		}
		inPool.set(0, INITIAL_POOL_SIZE);
	}

	private TableBodyUI findTableBodyUI() {
		IGLElementParent act = getParent();
		while (act != null && !(act instanceof TableBodyUI))
			act = act.getParent();
		return (TableBodyUI) act;
	}

	private ValueElement createPoolItem(TableBodyUI body) {
		ValueElement v = model.createValue();
		v.setVisibility(EVisibility.HIDDEN);
		v.onPick(body.getSelectedRowListener());
		return v;
	}

	@Override
	protected void updateMoveAnimation(ALayoutAnimation anim, IGLLayoutElement elem, Vec4f before, Vec4f after) {
		int flag = ((ValueElement) elem.asElement()).getAnimationFlag();
		switch (flag) {
		case FLAG_NONE:
			break;
		case FLAG_FROM_ABOVE:
			before = before.copy();
			before.setY(0);
			before.setW(0);
			break;
		case FLAG_FROM_BELOW:
			before = before.copy();
			before.setY(getSize().y());
			before.setW(0);
			break;
		case FLAG_TO_ABOVE:
			after = after.copy();
			after.setY(0);
			after.setW(0);
			break;
		case FLAG_TO_BELOW:
			after = after.copy();
			after.setY(getSize().y());
			after.setW(0);
			break;
		}
		super.updateMoveAnimation(anim, elem, before, after);
	}

	@Override
	protected ALayoutAnimation createMoveAnimation(IGLLayoutElement elem, Vec4f before, Vec4f after, int duration) {
		int flag = ((ValueElement) elem.asElement()).getAnimationFlag();
		switch (flag) {
		case FLAG_NONE:
			break;
		case FLAG_FROM_ABOVE:
			before = before.copy();
			before.setY(0);
			before.setW(0);
			break;
		case FLAG_FROM_BELOW:
			before = before.copy();
			before.setY(getSize().y());
			before.setW(0);
			break;
		case FLAG_TO_ABOVE:
			after = after.copy();
			after.setY(0);
			after.setW(0);
			break;
		case FLAG_TO_BELOW:
			after = after.copy();
			after.setY(getSize().y());
			after.setW(0);
			break;
		}

		if (getColumnParent().getRanker(model).isInternalReLayout()) {
			DummyAnimation d = new DummyAnimation(EAnimationType.MOVE);
			d.init(before, after);
			return d;
		}
		return super.createMoveAnimation(elem, before, after, duration);
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
	public void layout(int deltaTimeMs) {
		final int rows = getColumnParent().getNumVisibleRows(model);
		final int cached = this.size();
		if (cached < rows) {
			int addItems = Math.min(20, rows - cached); // add twenty more at least
			TableBodyUI body = findTableBodyUI();
			// enlarge the pool
			for (int i = 0; i < addItems; ++i)
				add(createPoolItem(body), 0);
			inPool.set(cached, cached + addItems);
		} else if ((cached - rows) > 20) { // more than enough free again
			int firstToCheck = 0;
			int itemsToRemove = Math.max(0, cached - rows - 20);
			boolean anyRemoved = false;
			for (int i = cached - 1; i >= 0; --i) {
				if (itemsToRemove <= 0) {
					firstToCheck = i;
					break;
				}
				if (inPool.get(i)) {
					remove(get(i), 0);
					inPool.clear(i);
					anyRemoved = true;
					itemsToRemove--;
				}
			}
			if (anyRemoved) {
				for (int i = firstToCheck; i < size(); ++i) {
					if (inPool.get(i))
						continue;
					int row = get(i).getLayoutDataAs(IRow.class, null).getIndex();
					rowIndexToGlElement.put(row, i);
				}
			}
		}
		super.layout(deltaTimeMs);
	}

	@Override
	public Vec4f getBounds(int rowIndex) {
		int index = rowIndexToGlElement.get(rowIndex);
		if (index < 0) {
			Vec2f s = getSize();
			return new Vec4f(0, before.get(rowIndex) ? 0 : s.y(), s.x(), 0);
		}
		return get(index).getBounds();
	}

	@Override
	public void doLayout(final List<? extends IGLLayoutElement> children, float w, float h) {
		final RankTableModel table = model.getTable();
		final IColumModelLayout p = (IColumModelLayout) getParent();
		IRowSetter setter = new IRowSetter() {
			@Override
			public void set(int rowIndex, float x, float y, float w, float h, boolean pickable) {
				int at = rowIndexToGlElement.get(rowIndex);
				if (h <= 0 && at >= 0) { // not visible but was visible
					// free element
					before.set(rowIndex, y <= 0);
					rowIndexToGlElement.remove(rowIndex);
					inPool.set(at);
					// set for the out animation if it isn't reused immediately
					IGLLayoutElement row = children.get(at);
					row.setBounds(x, y, w, h);
					ValueElement elem = (ValueElement) row.asElement();
					elem.setAnimationFlag(before.get(rowIndex) ? FLAG_TO_ABOVE : FLAG_TO_BELOW);
					elem.setVisibility(EVisibility.VISIBLE);
				} else if (h <= 0) { // still not visible
					before.set(rowIndex, y <= 0);
				} else if (at < 0) { // become visible
					if (inPool.isEmpty()) {
						// FIXME error
					} else {
						at = inPool.nextSetBit(0); // last one for better cache behavior
						inPool.clear(at);
						rowIndexToGlElement.put(rowIndex, at);
						IGLLayoutElement row = children.get(at);
						row.setBounds(x, y, w, h);
						ValueElement elem = (ValueElement) row.asElement();
						// layoutAnimations.remove(asLayoutElement(elem));
						elem.setRow(table.getDataItem(rowIndex)); // set act data
						elem.setAnimationFlag(before.get(rowIndex) ? FLAG_FROM_ABOVE : FLAG_FROM_BELOW);
						elem.setVisibility(pickable ? EVisibility.PICKABLE : EVisibility.VISIBLE);
					}
				} else { // still visible
					IGLLayoutElement row = children.get(at);
					row.setBounds(x, y, w, h);
					ValueElement elem = (ValueElement) row.asElement();
					elem.setAnimationFlag(FLAG_NONE);
					elem.setVisibility(pickable ? EVisibility.PICKABLE : EVisibility.VISIBLE);
				}
			}
		};
		p.layoutRows(model, setter, w, h);
	}

	@Override
	public boolean isCollapsed() {
		return ((model instanceof ICollapseableColumnMixin) && ((ICollapseableColumnMixin) model).isCollapsed());
	}

	@Override
	public VAlign getAlignment() {
		return getColumnParent().getAlignment(this);
	}

	@Override
	public Color getBarOutlineColor() {
		return getColumnParent().getBarOutlineColor();
	}

	protected IColumModelLayout getColumnParent() {
		return (IColumModelLayout) getParent();
	}

	@Override
	public boolean hasFreeSpace() {
		return getColumnParent().hasFreeSpace(this);
	}
}