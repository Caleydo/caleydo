/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.entourage.ui;

import gleem.linalg.Vec2f;

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Set;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementDecorator;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.basic.EButtonIcon;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator.IHasMinSize;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.core.view.opengl.util.text.ETextStyle;
import org.caleydo.view.tourguide.entourage.model.CheckColumnModel;
import org.caleydo.view.tourguide.entourage.model.GroupRow;
import org.caleydo.view.tourguide.entourage.model.SizeRankColumnModel;
import org.caleydo.vis.lineup.config.RankTableConfigBase;
import org.caleydo.vis.lineup.config.RankTableUIConfigBase;
import org.caleydo.vis.lineup.model.IRow;
import org.caleydo.vis.lineup.model.RankTableModel;
import org.caleydo.vis.lineup.model.StringRankColumnModel;
import org.caleydo.vis.lineup.ui.TableUI;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Iterables;

/**
 * @author Samuel Gratzl
 *
 */
public class GroupElements extends GLElementDecorator implements IHasMinSize, PropertyChangeListener {

	private RankTableModel table;
	private TableUI tableUI;
	private CheckColumnModel check;
	private IGroupCallback callback;

	/**
	 *
	 */
	public GroupElements(IGroupCallback callback) {
		table = new RankTableModel(new RankTableConfigBase() {
			@Override
			public boolean isDefaultCollapseAble() {
				return false;
			}

			@Override
			public boolean isDefaultHideAble() {
				return false;
			}
		});
		check = new CheckColumnModel();
		check.addPropertyChangeListener(CheckColumnModel.PROP_CHECKED, this);

		table.add(check);
		table.add(new StringRankColumnModel(GLRenderers.drawText("Group", VAlign.CENTER), StringRankColumnModel.DEFAULT));
		table.add(new SizeRankColumnModel("Size", new Function<IRow, Integer>() {
			@Override
			public Integer apply(IRow input) {
				if (!(input instanceof GroupRow))
					return 0;
				return ((GroupRow) input).getGroup().getSize();
			}
		}).setWidth(40));
		table.addPropertyChangeListener(RankTableModel.PROP_SELECTED_ROW, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				IRow new_ = (IRow) evt.getNewValue();
				if (new_ != null)
					check.set(new_, !check.is(new_));
			}
		});
		tableUI = new TableUI(table, new RankTableUIConfigBase(true, false, false) {
			@Override
			public boolean canEditValues() {
				return false;
			}

			@Override
			public boolean isSmallHeaderByDefault() {
				return true;
			}
		});
		setContent(tableUI);

		this.callback = callback;
	}

	/**
	 * @param predicate
	 */
	public void set(Predicate<Group> predicate) {
		IGroupCallback bak = callback;
		callback = null;
		for (GroupRow row : Iterables.filter(table.getMaskedData(), GroupRow.class)) {
			check.set(row, predicate.apply(row.getGroup()));
		}
		callback = bak;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		switch (evt.getPropertyName()) {
		case CheckColumnModel.PROP_CHECKED:
			if (callback != null) {
				boolean checked = (Boolean) evt.getNewValue();
				if (evt instanceof IndexedPropertyChangeEvent) {
					int index = ((IndexedPropertyChangeEvent) evt).getIndex();
					GroupRow r = (GroupRow) table.getDataItem(index);
					callback.onGroupSelectionChanged(r.getGroup(), checked);
				} else {
					for (IRow r : table.getData()) {
						callback.onGroupSelectionChanged(((GroupRow) r).getGroup(), checked);
					}
				}
			}

			break;
		default:
			break;
		}
	}

	/**
	 * @param callback
	 *            setter, see {@link callback}
	 */
	public void setCallback(IGroupCallback callback) {
		this.callback = callback;
	}

	@Override
	public Vec2f getMinSize() {
		return new Vec2f(240, -1);
	}

	public void set(Perspective perspective) {
		if (perspective == null) {
			table.setDataMask(new BitSet());
			return;
		}

		Collection<GroupRow> add = new ArrayList<>();
		for (Group g : perspective.getVirtualArray().getGroupList()) {
			add.add(new GroupRow(g));
		}
		int s = table.getDataSize();
		table.addData(add);
		BitSet oldOut = new BitSet(s + add.size());
		oldOut.set(s, s + add.size());
		table.setDataMask(oldOut);
	}

	public Set<Group> getSelection() {
		Builder<Group> r = ImmutableSet.builder();
		for (GroupRow row : Iterables.filter(table.getMaskedData(), GroupRow.class)) {
			if (check.is(row))
				r.add(row.getGroup());
		}
		return r.build();
	}

	public SelectAllNoneElement createSelectAllNone() {
		return new SelectAllNoneElement();
	}

	public class SelectAllNoneElement extends GLButton implements IGLRenderer, ISelectionCallback {
		public SelectAllNoneElement() {
			super(EButtonMode.CHECKBOX);
			this.setRenderer(this);
			this.setSelected(true);
			this.setSize(-1, 18);
			this.setCallback(this);
		}

		@Override
		public void onSelectionChanged(GLButton button, boolean selected) {
			check.set(selected);
		}

		@Override
		public void render(GLGraphics g, float w, float h, GLElement parent) {
			final boolean s = isSelected();
			String icon = EButtonIcon.CHECKBOX.get(s);
			g.fillImage(icon, 1, 1, h - 2, h - 2);
			String label = s ? "Select None" : "Select All";
			if (label != null && label.length() > 0)
				g.drawText(label, h, 0, w - h, 13, VAlign.LEFT, ETextStyle.ITALIC);
		}
	}

	public interface IGroupCallback {
		void onGroupSelectionChanged(Group group, boolean selected);
	}

}