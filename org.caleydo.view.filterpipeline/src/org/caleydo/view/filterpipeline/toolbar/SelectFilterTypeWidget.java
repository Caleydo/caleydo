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
package org.caleydo.view.filterpipeline.toolbar;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.view.filterpipeline.GLFilterPipeline;
import org.caleydo.view.filterpipeline.listener.SetFilterTypeEvent;
import org.caleydo.view.filterpipeline.listener.SetFilterTypeEvent.FilterType;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class SelectFilterTypeWidget extends ControlContribution {

	private GLFilterPipeline glView;

	public SelectFilterTypeWidget(GLFilterPipeline glView) {
		super("SelectFilterTypeWidget" + glView.getViewName());
		this.glView = glView;
	}

	@Override
	protected Control createControl(Composite parent) {

		final Combo selectType = new Combo(parent, SWT.NONE | SWT.READ_ONLY
				| SWT.DROP_DOWN);

		String items[] = new String[] { "Genes", "Experiments" };
		selectType.setItems(items);
		selectType.select(0);

		selectType.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String type = selectType.getItem(selectType.getSelectionIndex());

				FilterType filterType = null;

				if (type == "Genes")
					filterType = FilterType.RECORD;
				else if (type == "Experiments")
					filterType = FilterType.DIMENSION;

				GeneralManager.get().getEventPublisher()
						.triggerEvent(new SetFilterTypeEvent(filterType, glView.getID()));
			}
		});

		return selectType;
	}
}
