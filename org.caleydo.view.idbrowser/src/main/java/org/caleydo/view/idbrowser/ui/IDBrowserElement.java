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
import org.caleydo.core.data.selection.MultiSelectionManagerMixin;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.event.EventListenerManager.DeepScan;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.view.contextmenu.ContextMenuCreator;
import org.caleydo.core.view.opengl.canvas.IGLMouseListener.IMouseEvent;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.basic.RadioController;
import org.caleydo.core.view.opengl.layout2.basic.ScrollBar;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator;
import org.caleydo.core.view.opengl.layout2.geom.Rect;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.datadomain.pathway.PathwayActions;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.view.idbrowser.internal.Activator;
import org.caleydo.view.idbrowser.internal.model.PathwayRow;
import org.caleydo.view.idbrowser.internal.model.PrimaryIDRow;
import org.caleydo.view.idbrowser.internal.ui.ACategoryQuery;
import org.caleydo.view.idbrowser.internal.ui.IDCategoryQuery;
import org.caleydo.view.idbrowser.internal.ui.PathwayCategoryQuery;
import org.caleydo.vis.lineup.config.RankTableConfigBase;
import org.caleydo.vis.lineup.config.RankTableUIConfigBase;
import org.caleydo.vis.lineup.model.ARankColumnModel;
import org.caleydo.vis.lineup.model.IRow;
import org.caleydo.vis.lineup.model.RankRankColumnModel;
import org.caleydo.vis.lineup.model.RankTableModel;
import org.caleydo.vis.lineup.model.StringRankColumnModel;
import org.caleydo.vis.lineup.ui.RenderStyle;
import org.caleydo.vis.lineup.ui.TableBodyUI;
import org.caleydo.vis.lineup.ui.TableUI;

import com.google.common.collect.Lists;

/**
 * element of this view holding a {@link TablePerspective}
 *
 * @author AUTHOR
 *
 */
public class IDBrowserElement extends GLElementContainer implements ISelectionCallback,
		MultiSelectionManagerMixin.ISelectionMixinCallback {

	private final RankTableModel table = new RankTableModel(new RankTableConfigBase() {
		@Override
		public boolean isDestroyOnHide(ARankColumnModel model) {
			return true;
		}
	});
	private TableUI tableUI;

	@DeepScan
	private final MultiSelectionManagerMixin selections = new MultiSelectionManagerMixin(this);

	public IDBrowserElement() {
		setLayout(GLLayouts.flowHorizontal(10));

		initData();
	}

	/**
	 * @return the table, see {@link #table}
	 */
	public RankTableModel getTable() {
		return table;
	}

	/**
	 *
	 */
	private void initData() {
		initQueries();
		initTable();
	}

	/**
	 * @return
	 */
	public TableBodyUI getBody() {
		return tableUI.getBody();
	}

	@Override
	public void onSelectionChanged(GLButton button, boolean selected) {
		if (button == null)
			return;
		assert button instanceof ACategoryQuery;
		ACategoryQuery q = (ACategoryQuery) button;

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
			mask = q.getMask();

			List<ARankColumnModel> columns = new ArrayList<>(table.getColumns());
			for (ARankColumnModel c : columns.subList(2, columns.size())) {
				table.remove(c);
			}
			q.addColumns(table);
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

		this.tableUI = new TableUI(table, new RankTableUIConfigBase(true, true, true) {
			@Override
			public boolean canEditValues() {
				return false;
			}

			@Override
			public EButtonBarPositionMode getButtonBarPosition() {
				return EButtonBarPositionMode.OVER_LABEL;
			}

			@Override
			public void onRowClick(RankTableModel table, Pick pick, IRow row, boolean isSelected,
					IGLElementContext context) {
				if (pick.getPickingMode() == PickingMode.RIGHT_CLICKED) {
					ContextMenuCreator c = new ContextMenuCreator();
					if (row instanceof PathwayRow) {
						PathwayGraph pathway = ((PathwayRow) row).getPathway();
						PathwayActions.addToContextMenu(c, pathway, this, true);
					}
					if (c.hasMenuItems()) {
						context.getSWTLayer().showContextMenu(c);
					}
				}
				onRowClickImpl(pick, row);
				super.onRowClick(table, pick, row, isSelected, context);
			}

			@Override
			public void renderRowBackground(GLGraphics g, Rect rect, boolean even, IRow row,
					IRow selected) {
				if (!g.isPickingPass())
					renderRowBackgroundImpl(g, rect.x(), rect.y(), rect.width(), rect.height(), even, row, selected);
				else
					super.renderRowBackground(g, rect, even, row, selected);
			}
		});

		ScrollingDecorator sc = new ScrollingDecorator(this.tableUI, new ScrollBar(true), null,
				RenderStyle.SCROLLBAR_WIDTH);
		this.add(sc);
	}

	protected void renderRowBackgroundImpl(GLGraphics g, float x, float y, float w, float h, boolean even, IRow row,
			IRow selected) {
		if (row == selected) {
			g.color(RenderStyle.COLOR_SELECTED_ROW);
			g.incZ();
			g.fillRect(x, y, w, h);
			g.color(RenderStyle.COLOR_SELECTED_BORDER);
			g.drawLine(x, y, x + w, y);
			g.drawLine(x, y + h, x + w, y + h);
			g.decZ();
		} else if (row instanceof PrimaryIDRow && isSelected((PrimaryIDRow) row)) {
			g.color(SelectionType.SELECTION.getColor().brighter());
			g.incZ();
			g.fillRect(x, y, w, h);
			g.color(RenderStyle.COLOR_SELECTED_BORDER);
			g.drawLine(x, y, x + w, y);
			g.drawLine(x, y + h, x + w, y + h);
			g.decZ();
		} else if (!even) {
			g.color(RenderStyle.COLOR_BACKGROUND_EVEN);
			g.fillRect(x, y, w, h);
		}
	}

	/**
	 * @param pick
	 * @param row
	 */
	protected void onRowClickImpl(Pick pick, IRow row) {
		if (!(row instanceof PrimaryIDRow))
			return;
		PrimaryIDRow r = (PrimaryIDRow)row;
		System.out.println(pick.getPickingMode() + r.getLabel());
		if (!(r.getPrimary() instanceof Integer))
			return;
		switch(pick.getPickingMode()) {
		case CLICKED:
			boolean ctrlDown = ((IMouseEvent) pick).isCtrlDown();
			if (isSelected(r))
				clear(r, ctrlDown);
			else
				select(r, ctrlDown);
			break;
		default:
			break;
		}
	}

	public void select(PrimaryIDRow r, boolean additional) {
		SelectionManager m = getOrCreate(r.getPrimaryIDType());
		if (!additional)
			m.clearSelection(SelectionType.SELECTION);
		m.addToType(SelectionType.SELECTION, (Integer) r.getPrimary());
		selections.fireSelectionDelta(m);
	}

	public void clear(PrimaryIDRow r, boolean additional) {
		SelectionManager m = getOrCreate(r.getPrimaryIDType());
		if (!additional)
			m.clearSelection(SelectionType.SELECTION);
		else
			m.removeFromType(SelectionType.SELECTION, (Integer) r.getPrimary());
		selections.fireSelectionDelta(m);
	}

	/**
	 * @param r
	 * @return
	 */
	private boolean isSelected(PrimaryIDRow r) {
		if (!(r.getPrimary() instanceof Integer))
			return false;
		SelectionManager m = getOrCreate(r.getPrimaryIDType());
		return m.checkStatus(SelectionType.SELECTION, (Integer) r.getPrimary());
	}

	/**
	 * @param primaryIDType
	 * @return
	 */
	private SelectionManager getOrCreate(IDType idType) {
		SelectionManager m = selections.getSelectionManager(idType);
		if (m != null)
			return m;
		m = new SelectionManager(idType);
		selections.add(m);
		return m;
	}

	private void initQueries() {
		RadioController controller = new RadioController(this);
		controller.setSelected(-1);
		List<ACategoryQuery> queries = new ArrayList<>();
		for (IDCategory cat : IDCategory.getAllRegisteredIDCategories()) {
			if (cat.getPublicIdTypes().isEmpty())
				continue;
			if (IDMappingManagerRegistry.get().getIDMappingManager(cat).getAllMappedIDs(cat.getPrimaryMappingType())
					.isEmpty())
				continue;
			IDCategoryQuery q = new IDCategoryQuery(cat);
			controller.add(q);
			q.setSize(-1, 20);
			queries.add(q);
			selections.add(new SelectionManager(cat.getPrimaryMappingType()));
		}
		Collections.sort(queries);
		{
			final PathwayCategoryQuery p = new PathwayCategoryQuery();
			controller.add(p);
			p.setSize(-1, 20);
			queries.add(p);
		}

		GLElementContainer c = new GLElementContainer(GLLayouts.flowVertical(2));
		c.asList().addAll(queries);
		c.setSize(-1, queries.size()*(20+2));
		this.add(ScrollingDecorator.wrap(c, RenderStyle.SCROLLBAR_WIDTH).setSize(200, -1));
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

	@Override
	public void onSelectionUpdate(SelectionManager manager) {
		tableUI.getBody().repaint();
	}

}
