/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.idbrowser.ui;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.idbrowser.internal.Activator;
import org.caleydo.view.idbrowser.internal.ui.IDTypeQuery;
import org.caleydo.vis.lineup.config.RankTableConfigBase;
import org.caleydo.vis.lineup.config.RankTableUIConfigBase;
import org.caleydo.vis.lineup.model.RankRankColumnModel;
import org.caleydo.vis.lineup.model.RankTableModel;
import org.caleydo.vis.lineup.model.StringRankColumnModel;
import org.caleydo.vis.lineup.ui.TableUI;

import com.google.common.collect.Lists;

/**
 * element of this view holding a {@link TablePerspective}
 *
 * @author AUTHOR
 *
 */
public class IDBrowserElement extends GLElementContainer implements ISelectionCallback {

	private final RankTableModel table = new RankTableModel(new RankTableConfigBase());

	public IDBrowserElement() {
		setLayout(GLLayouts.flowHorizontal(10));

		initData();
	}

	/**
	 *
	 */
	private void initData() {
		initQueries();
		initTable();
	}

	@Override
	public void onSelectionChanged(GLButton button, boolean selected) {
		assert button instanceof IDTypeQuery;
		IDTypeQuery q = (IDTypeQuery) button;

		BitSet mask = table.getDataMask();
		if (mask == null)
			mask = new BitSet();
		else
			mask = (BitSet) mask.clone();
		if (selected) {
			if (!q.inited()) { // lazy init
				int size = table.getDataSize();
				table.addData(Lists.newArrayList(q.create()));
				q.init(size, table.getDataSize());
			}
			// show elems
			mask.or(q.getMask());
		} else
			// hide elems
			mask.andNot(q.getMask());
		table.setDataMask(mask);
	}

	/**
	 *
	 */
	private void initTable() {
		table.add(new RankRankColumnModel());
		table.add(new StringRankColumnModel(GLRenderers.drawText("ID", VAlign.CENTER), StringRankColumnModel.DEFAULT));

		TableUI ui = new TableUI(table, new RankTableUIConfigBase(true, true, true) {
			@Override
			public boolean canEditValues() {
				return false;
			}

			@Override
			public EButtonBarPositionMode getButtonBarPosition() {
				return EButtonBarPositionMode.OVER_LABEL;
			}
		});
		this.add(ui);
	}

	private void initQueries() {
		List<IDTypeQuery> queries = new ArrayList<>();
		for (IDCategory cat : IDCategory.getAllRegisteredIDCategories()) {
			List<IDType> publics = cat.getPublicIdTypes();
			IDMappingManager m = IDMappingManagerRegistry.get().getIDMappingManager(cat);
			if (publics.isEmpty())
				continue;
			for (IDType public_ : publics) {
				if (m.getAllMappedIDs(public_).isEmpty())
					continue;
				IDTypeQuery q = new IDTypeQuery(public_);
				q.setCallback(this);
				q.setSize(-1, 20);
				queries.add(q);
			}
		}
		Collections.sort(queries);

		GLElementContainer c = new GLElementContainer(GLLayouts.flowVertical(2));
		c.asList().addAll(queries);
		c.setSize(-1, queries.size()*(20+2));
		this.add(ScrollingDecorator.wrap(c, 8).setSize(200, -1));
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.pushResourceLocator(Activator.getResourceLocator());
		super.renderImpl(g, w, h);
		g.popResourceLocator();
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		g.pushResourceLocator(Activator.getResourceLocator());
		super.renderPickImpl(g, w, h);
		g.popResourceLocator();
	}

}
