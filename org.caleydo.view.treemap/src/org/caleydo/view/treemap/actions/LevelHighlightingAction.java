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
package org.caleydo.view.treemap.actions;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.view.treemap.listener.LevelHighlightingEvent;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Scale;

/**
 * Scale for highlighting different hierarchylevel in the treemap
 *
 * @author Michael Lafer
 *
 */

public class LevelHighlightingAction extends ControlContribution {

	public static final String LABEL = "Hierarchy Level";

	// public static final String ICON =
	// "resources/icons/view/tablebased/clustering.png";

	public LevelHighlightingAction() {
		super("");
		// setText(TEXT);
		// setToolTipText(TEXT);
		// setImageDescriptor(ImageDescriptor.createFromImage(new
		// ResourceLoader().getImage(
		// PlatformUI.getWorkbench().getDisplay(), ICON)));
		// setChecked(false);
	}

	Scale scale;

	@Override
	protected Control createControl(Composite parent) {
		// TODO Auto-generated method stub

		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = layout.marginWidth = layout.horizontalSpacing = 0;
		composite.setLayout(layout);

		scale = new Scale(composite, SWT.HORIZONTAL);
		scale.setSize(200, 30);
		scale.setMinimum(0);
		scale.setMaximum(10);
		scale.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				System.out.println("scale: " + scale.getSelection());
				LevelHighlightingEvent event = new LevelHighlightingEvent();
				event.setHierarchyLevel(scale.getSelection());
				GeneralManager.get().getEventPublisher().triggerEvent(event);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		return composite;
	}
}
