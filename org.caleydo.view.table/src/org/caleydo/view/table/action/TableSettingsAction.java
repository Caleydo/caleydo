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
package org.caleydo.view.table.action;

import org.caleydo.core.gui.SimpleAction;
import org.caleydo.view.table.Activator;
import org.caleydo.view.table.EDataRepresentation;
import org.caleydo.view.table.TableView;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * different settings regarding the presentation of the table
 *
 * @author Samuel Gratzl
 *
 */
public class TableSettingsAction extends SimpleAction implements IMenuCreator {
	private final TableView view;
	private Menu menu;

	public TableSettingsAction(TableView view) {
		super("Select Data Representation", "resources/icons/table_gear.png", Activator.getResourceLoader());
		this.view = view;
		setMenuCreator(this);
	}

	@Override
	public void dispose() {
		if (menu != null) {
			menu.dispose();
		}
	}

	@Override
	public Menu getMenu(Menu parent) {
		return null;
	}

	@Override
	public Menu getMenu(Control parent) {
		if (menu != null) {
			menu.dispose();
		}

		menu= new Menu(parent);

		// add different Data Representations
		EDataRepresentation current = view.getDataRepresentation();
		int i = 1;
		for (EDataRepresentation mode : EDataRepresentation.values()) {
			Action action = new SelectAction(view, mode);
			action.setChecked(mode == current);
			addActionToMenu(menu, action, i++);
		}

		@SuppressWarnings("unused")
		MenuItem sep = new MenuItem(menu, SWT.SEPARATOR);

		addActionToMenu(menu, new ChangePrecisionAction(view, true), i++);
		addActionToMenu(menu, new ChangePrecisionAction(view, false), i++);

		return menu;
	}

	private void addActionToMenu(Menu parent, Action action, int accelerator) {
	    if (accelerator < 10) {
			StringBuilder label = new StringBuilder();
			//add the numerical accelerator
			label.append('&');
			label.append(accelerator);
			label.append(' ');
			label.append(action.getText());
			action.setText(label.toString());
		}
		ActionContributionItem item= new ActionContributionItem(action);
		item.fill(parent, -1);
	}

	/**
	 * select a specific {@link EDataRepresentation}
	 *
	 * @author Samuel Gratzl
	 *
	 */
	private static class SelectAction extends Action {
		private final EDataRepresentation mode;
		private final TableView view;

		public SelectAction(TableView view, EDataRepresentation mode) {
			super(mode.getLabel());
			setToolTipText(mode.getTooltip());
			this.mode = mode;
			this.view = view;
		}

		@Override
		public void run() {
			view.setDataRepresentation(mode);
		}

	}

	/**
	 * change number precision
	 * 
	 * @author Samuel Gratzl
	 * 
	 */
	private static class ChangePrecisionAction extends Action {
		private final boolean increase;
		private final TableView view;

		public ChangePrecisionAction(TableView view, boolean increase) {
			super(increase ? "Increase Precision" : "Decrease Precision");
			this.increase = increase;
			this.view = view;
		}

		@Override
		public void run() {
			view.changeFormattingPrecision(increase ? +1 : -1);
		}

	}
}