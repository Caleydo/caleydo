/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.internal.ui;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.vis.rank.internal.event.SearchEvent;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 *
 *
 * @author Samuel Gratzl
 *
 */
public class StringSearchDialog extends AFilterDialog {
	private final String filter;
	private Text text;

	private String hint;

	private Button backward;
	private Button wrapSearch;

	public StringSearchDialog(Shell parentShell, String title, String hint, Object receiver, String filter, Point loc) {
		super(parentShell, "Search in " + title, receiver, false, false, loc);
		this.hint = hint;
		this.filter = filter;
		this.setShellStyle(SWT.CLOSE);
	}
	@Override
	protected void createSpecificFilterUI(Composite composite) {
		// create message
		GridData gd;

		text = new Text(composite, SWT.BORDER | SWT.SEARCH | SWT.ICON_SEARCH);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.widthHint = getCharWith(composite, 20);
		text.setLayoutData(gd);
		text.setText(filter == null ? "" : filter);
//		text.addModifyListener(new ModifyListener() {
//			@Override
//			public void modifyText(ModifyEvent e) {
//				String newText = text.getText();
//				if (newText.length() >= 2 || newText.isEmpty())
//					triggerEvent(false);
//			}
		// });
		text.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				if (e.detail == SWT.CANCEL) {
					// nothing
				} else {
					triggerEvent(false);
				}
			}
		});
		text.selectAll();
		text.setFocus();

		final ControlDecoration deco = new ControlDecoration(text, SWT.TOP | SWT.RIGHT);

		// Re-use an existing image
		Image image = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION)
				.getImage();
		// Set description and image
		deco.setDescriptionText("Search string " + hint);
		deco.setImage(image);
		// Hide deco if not in focus
		deco.setShowOnlyOnFocus(true);

		addFindButton(composite, false);
		{
			Composite s = new Composite(composite, SWT.NONE);
			s.setLayout(new FillLayout());
			s.setLayoutData(twoColumns(new GridData(SWT.LEFT, SWT.CENTER, true, false)));

			wrapSearch = new Button(s, SWT.CHECK);
			wrapSearch.setText("Wra&p Search");
			wrapSearch.setSelection(true);

			backward = new Button(s, SWT.CHECK);
			backward.setText("&Backward");
		}
	}

	protected final void addFindButton(Composite composite, boolean spanOverTwoColumns) {
		Button b = new Button(composite, SWT.PUSH);
		b.setText("&Find");
		GridData gd = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		gd.horizontalSpan = spanOverTwoColumns ? 2 : 1;
		b.setLayoutData(gd);

		// b.getShell().setDefaultButton(b);
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				triggerEvent(false);
				// setReturnCode(OK);
				// close();
			}
		});
	}

	@Override
	protected void triggerEvent(boolean cancel) {
		if (cancel) // original values
			return;

		String t = text.getText();
		t = t.trim();
		t = t.isEmpty() ? null : t;
		EventPublisher.trigger(new SearchEvent(t, wrapSearch.getSelection(), !backward.getSelection()).to(receiver));
	}
}
