/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.ui;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.basic.RadioController;
import org.caleydo.core.view.opengl.layout2.basic.ScrollBar;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.data.loader.ResourceLocators;
import org.caleydo.vis.rank.config.IRankTableUIConfig;
import org.caleydo.vis.rank.internal.ui.ButtonBar;
import org.caleydo.vis.rank.layout.IRowHeightLayout;
import org.caleydo.vis.rank.model.NestedRankColumnModel;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.model.ScriptedRankColumnModel;
import org.caleydo.vis.rank.model.StackedRankColumnModel;

/**
 * @author Samuel Gratzl
 *
 */
public class RankTableUI extends GLElementContainer implements ISelectionCallback {

	public RankTableUI() {
	}

	/**
	 * initializes this visualization with the given data
	 *
	 * @param table
	 *            the model to use
	 * @param config
	 *            the ui config to use
	 * @param layouts
	 *            one or more {@link IRowHeightLayout} to provide to the user
	 */
	public void init(final RankTableModel table, IRankTableUIConfig config, IRowHeightLayout... layouts) {
		setLayout(GLLayouts.flowVertical(0));
		ButtonBar buttons = new ButtonBar();
		buttons.setzDelta(0.5f);

		if (layouts.length > 1) { // more than one row height layout to choose
			RadioController radio = new RadioController(this);
			for(int i = 0; i < layouts.length; ++i) {
				GLButton b = new GLButton();
				b.setLayoutData(layouts[i]);
				b.setRenderer(renderer);
				radio.add(b);
				buttons.addButton(b);
			}
		}
		buttons.add(new SimpleRankTableStatsElement(table));
		buttons.addSpacer();
		{
			GLButton b = new GLButton();
			b.setCallback(new ISelectionCallback() {
				@Override
				public void onSelectionChanged(GLButton button, boolean selected) {
					StackedRankColumnModel m = new StackedRankColumnModel();
					m.setWidth(100);
					table.add(m);
				}
			});
			buttons.addButton(b, "Create an empty Stacked Combined Column", RenderStyle.ICON_ADD_STACKED,
					RenderStyle.ICON_ADD_STACKED);
		}
		{
			GLButton b = new GLButton();
			b.setCallback(new ISelectionCallback() {
				@Override
				public void onSelectionChanged(GLButton button, boolean selected) {
					NestedRankColumnModel m = new NestedRankColumnModel();
					m.setWidth(100);
					table.add(m);
				}
			});
			buttons.addButton(b, "Create an empty Nested Combined Column", RenderStyle.ICON_ADD_NESTED,
					RenderStyle.ICON_ADD_NESTED);
		}
		{
			GLButton b = new GLButton();
			b.setCallback(new ISelectionCallback() {
				@Override
				public void onSelectionChanged(GLButton button, boolean selected) {
					ScriptedRankColumnModel m = new ScriptedRankColumnModel();
					m.setWidth(100);
					table.add(m);
				}
			});
			buttons.addButton(b, "Create an empty Scripted Combined Column", RenderStyle.ICON_ADD_SCRIPTED,
					RenderStyle.ICON_ADD_SCRIPTED);
		}
		{
			GLButton b = new GLButton();
			b.setCallback(new ISelectionCallback() {
				@Override
				public void onSelectionChanged(GLButton button, boolean selected) {
					table.addSnapshot(null);
				}
			});
			buttons.addButton(b, "Create a new Separator Column", RenderStyle.ICON_ADD_SEPARATOR,
					RenderStyle.ICON_ADD_SEPARATOR);
		}

		this.add(buttons);

		TableUI tableui = new TableUI(table, config, layouts);
		ScrollingDecorator sc = new ScrollingDecorator(tableui, new ScrollBar(true), null, RenderStyle.SCROLLBAR_WIDTH);
		this.add(sc);
		if (config.isInteractive() && config.isShowColumnPool())
			this.add(new ColumnPoolUI(table, config));
	}

	private static final IGLRenderer renderer = new  IGLRenderer() {
		/**
		 * renders a {@link IRowHeightLayout} switch
		 */
		@Override
		public void render(GLGraphics g, float w, float h, GLElement parent) {
			IRowHeightLayout l = parent.getLayoutDataAs(IRowHeightLayout.class, null);
			boolean selected = ((GLButton)parent).isSelected();
			g.fillImage(l.getIcon(), 0, 0, w, h);
			if (selected)
				g.color(Color.RED).drawRect(0, 0, w, h);
		}
	};


	/**
	 * change the {@link IRowHeightLayout} to the selected one
	 */
	@Override
	public void onSelectionChanged(GLButton button, boolean selected) {
		IRowHeightLayout l = button.getLayoutDataAs(IRowHeightLayout.class, null);
		TableBodyUI body = findBody();
		body.setRowLayout(l);
	}

	public TableBodyUI findBody() {
		ScrollingDecorator scbody = (ScrollingDecorator) get(1);
		TableUI table = (TableUI) scbody.getContent();
		TableBodyUI body = (TableBodyUI) table.get(1);
		return body;
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		// push my resource locator to find the icons
		g.pushResourceLocator(ResourceLocators.classLoader(this.getClass().getClassLoader()));

		super.renderImpl(g, w, h);

		g.popResourceLocator();
	}

	/**
	 * @param wheelRotation
	 */
	protected void onWheelMoved(int wheelRotation) {
		if (wheelRotation == 0)
			return;
		TableBodyUI body = findBody();
		body.scroll(-wheelRotation);
	}
}
