/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.internal.ui;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.vis.lineup.internal.event.IntegerFilterEvent;
import org.caleydo.vis.lineup.model.mixin.IFilterColumnMixin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Samuel Gratzl
 *
 */
public class IntegerFilterDialog extends AFilterDialog {

	private final int min;
	private final int max;

	private Text minUI;
	private Text maxUI;


	public IntegerFilterDialog(Shell parentShell, String title, Object receiver, int min, int max,
			IFilterColumnMixin model, boolean hasSnapshots, Point loc) {
		super(parentShell, "Filter " + title, receiver, model, hasSnapshots, loc);
		this.min = min;
		this.max = max;
	}

	@Override
	public void create() {
		super.create();
		getShell().setText("Edit Filter of size");
	}

	@Override
	protected void createSpecificFilterUI(Composite composite) {
		VerifyListener isNumber = new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent e) {
				String text = e.text;
				text = text.replaceAll("\\D|-", "");
				e.text = text;
			}
		};
		Composite p = new Composite(composite, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		p.setLayout(layout);

		GridData d = new GridData(SWT.LEFT, SWT.CENTER, true, true);
		d.widthHint = getCharWith(composite, 4);
		minUI = new Text(p, SWT.BORDER);
		minUI.setLayoutData(d);
		if (min > 0)
			minUI.setText(min + "");
		minUI.addVerifyListener(isNumber);

		Label l = new Label(p, SWT.NONE);
		l.setText("<= VALUE <=");
		l.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, true));

		maxUI = new Text(p, SWT.BORDER);
		maxUI.setLayoutData(d);
		if (max < Integer.MAX_VALUE)
			maxUI.setText(max + "");
		maxUI.addVerifyListener(isNumber);

		addButtonAndOption(composite);
	}

	@Override
	protected void triggerEvent(boolean cancel) {
		if (cancel) {
			EventPublisher.trigger(new IntegerFilterEvent(min, max).to(receiver));
			return;
		}
		String t = minUI.getText().trim();
		Integer minV = t.length() > 0 ? new Integer(t) : null;
		t = maxUI.getText().trim();
		Integer maxV = t.length() > 0 ? new Integer(t) : null;
		EventPublisher.trigger(new IntegerFilterEvent(minV, maxV).to(receiver));
	}
}
