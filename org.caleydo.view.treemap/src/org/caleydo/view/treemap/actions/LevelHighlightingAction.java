/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.treemap.actions;

import org.caleydo.core.event.EventPublisher;
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
		// setText(UNIQUE_OBJECT);
		// setToolTipText(UNIQUE_OBJECT);
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
				
				EventPublisher.INSTANCE.triggerEvent(event);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		return composite;
	}
}
