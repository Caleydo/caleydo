/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.internal.ui;

import java.util.Date;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.vis.rank.internal.event.DateFilterEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Samuel Gratzl
 *
 */
public class DateFilterDialog extends AFilterDialog {

	private final Date before;
	private final Date after;

	private DateTime minUI;
	private DateTime maxUI;


	public DateFilterDialog(Shell parentShell, String title, Object receiver, Date min, Date max,
			boolean filterGlobally, boolean hasSnapshots, Point loc) {
		super(parentShell, "Filter " + title, receiver, filterGlobally, hasSnapshots, loc);
		this.before = min;
		this.after = max;
	}

	@Override
	public void create() {
		super.create();
		getShell().setText("Edit Filter of " + title);
	}

	@Override
	protected void createSpecificFilterUI(Composite composite) {
		Composite p = new Composite(composite, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		p.setLayout(layout);

		GridData d = new GridData(SWT.LEFT, SWT.CENTER, true, true);
		d.widthHint = getCharWith(composite, 50);
		minUI = new DateTime(p, SWT.BORDER | SWT.DROP_DOWN | SWT.TIME | SWT.DATE);
		minUI.setLayoutData(d);

		Label l = new Label(p, SWT.NONE);
		l.setText("<= VALUE <=");
		l.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, true));

		maxUI = new DateTime(p, SWT.BORDER | SWT.DROP_DOWN);
		maxUI.setLayoutData(d);

		createApplyGlobally(composite);
		addOKButton(composite, false);
	}

	@Override
	protected void triggerEvent(boolean cancel) {
		if (cancel) {
			EventPublisher.trigger(new DateFilterEvent(before, after).to(receiver));
			return;
		}
		// String t = minUI.get
		// Integer minV = t.length() > 0 ? new Integer(t) : null;
		// t = maxUI.getText().trim();
		// Integer maxV = t.length() > 0 ? new Integer(t) : null;
		// EventPublisher.trigger(new DateFilterEvent(minV, maxV).to(receiver));
	}
}
