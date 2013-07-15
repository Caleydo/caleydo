/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.internal.ui;

import java.util.Calendar;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.vis.rank.internal.event.DateFilterEvent;
import org.caleydo.vis.rank.model.DateRankColumnModel.DateMode;
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

	private final DateMode mode;
	private final Calendar before;
	private final Calendar after;

	private DateTime beforeUI;
	private DateTime afterUI;


	public DateFilterDialog(Shell parentShell, String title, Object receiver, Calendar min, Calendar max,
			boolean filterGlobally, boolean hasSnapshots, Point loc, DateMode mode) {
		super(parentShell, "Filter " + title, receiver, filterGlobally, hasSnapshots, loc);
		this.before = min;
		this.after = max;
		this.mode = mode;
	}

	@Override
	public void create() {
		super.create();
		getShell().setText("Edit Filter of " + title);
	}

	private static void set(DateTime dateUI, Calendar date, DateMode mode) {
		if (date == null)
			return;
		switch (mode) {
		case DATE:
			dateUI.setDate(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_YEAR));
			break;
		case DATE_TIME:
			dateUI.setDate(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_YEAR));
			dateUI.setTime(date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), date.get(Calendar.SECOND));
			break;
		case TIME:
			dateUI.setTime(date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), date.get(Calendar.SECOND));
			break;
		}
	}

	private static Calendar get(DateTime dateUI, DateMode mode) {
		Calendar cal = Calendar.getInstance();
		cal.clear();
		switch (mode) {
		case DATE:
			cal.set(dateUI.getYear(), dateUI.getMonth(), dateUI.getDay());
			break;
		case DATE_TIME:
			cal.set(dateUI.getYear(), dateUI.getMonth(), dateUI.getDay());
			cal.set(Calendar.HOUR_OF_DAY, dateUI.getHours());
			cal.set(Calendar.MINUTE, dateUI.getMinutes());
			cal.set(Calendar.SECOND, dateUI.getSeconds());
			break;
		case TIME:
			cal.set(Calendar.HOUR_OF_DAY, dateUI.getHours());
			cal.set(Calendar.MINUTE, dateUI.getMinutes());
			cal.set(Calendar.SECOND, dateUI.getSeconds());
			break;
		}
		return cal;
	}

	private static int asFlags(DateMode mode) {
		switch (mode) {
		case DATE:
			return SWT.DATE;
		case TIME:
			return SWT.TIME;
		case DATE_TIME:
			return SWT.DATE | SWT.TIME;
		}
		throw new IllegalStateException();
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
		beforeUI = new DateTime(p, SWT.BORDER | SWT.DROP_DOWN | asFlags(mode));
		beforeUI.setLayoutData(d);
		set(beforeUI, before, mode);

		Label l = new Label(p, SWT.NONE);
		l.setText("<= VALUE <=");
		l.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, true));

		afterUI = new DateTime(p, SWT.BORDER | SWT.DROP_DOWN | asFlags(mode));
		afterUI.setLayoutData(d);
		set(afterUI, after, mode);

		createApplyGlobally(composite);
		addOKButton(composite, false);
	}

	@Override
	protected void triggerEvent(boolean cancel) {
		if (cancel) {
			EventPublisher.trigger(new DateFilterEvent(before, after).to(receiver));
			return;
		}
		Calendar b = get(beforeUI, mode);
		Calendar a = get(afterUI, mode);
		EventPublisher.trigger(new DateFilterEvent(b, a).to(receiver));
	}
}
