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
package org.caleydo.vis.rank.internal.ui;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.vis.rank.internal.event.SizeFilterEvent;
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
			boolean filterGlobally, boolean hasSnapshots, Point loc) {
		super(parentShell, "Filter " + title, receiver, filterGlobally, hasSnapshots, loc);
		this.min = min;
		this.max = max;
	}

	@Override
	public void create() {
		super.create();
		getShell().setText("Edit filter of size");
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

		createApplyGlobally(composite);
		addOKButton(composite, false);
	}

	@Override
	protected void triggerEvent(boolean cancel) {
		if (cancel) {
			EventPublisher.trigger(new SizeFilterEvent(min, max).to(receiver));
			return;
		}
		String t = minUI.getText().trim();
		Integer minV = t.length() > 0 ? new Integer(t) : null;
		t = maxUI.getText().trim();
		Integer maxV = t.length() > 0 ? new Integer(t) : null;
		EventPublisher.trigger(new SizeFilterEvent(minV, maxV).to(receiver));
	}
}