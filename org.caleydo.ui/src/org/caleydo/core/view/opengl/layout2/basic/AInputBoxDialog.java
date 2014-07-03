/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.basic;

import gleem.linalg.Vec2f;

import org.caleydo.core.event.ADirectedEvent;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class AInputBoxDialog extends Window {
	private final Point loc;
	protected final String title;
	protected final Object receiver;
	private Text text;
	private Button ok;

	public AInputBoxDialog(Shell parentShell, String title, GLElement receiver, Composite canvas) {
		super(parentShell);
		this.title = title;
		this.receiver = receiver;
		if (canvas != null) {
			final Vec2f location = receiver.getAbsoluteLocation();
			this.loc = canvas.toDisplay((int) location.x(), (int) location.y());
		} else {
			this.loc = null;
		}
		this.setShellStyle(SWT.CLOSE);
	}

	@Override
	protected Point getInitialLocation(Point initialSize) {
		if (loc == null)
			super.getInitialLocation(initialSize);
		Point computeSize = getShell().getChildren()[0].computeSize(SWT.DEFAULT, SWT.DEFAULT);
		return new Point(loc.x, loc.y - computeSize.y);
	}

	@Override
	public void create() {
		super.create();
		final Shell shell = getShell();
		shell.addListener(SWT.Traverse, new Listener() {
			@Override
			public void handleEvent(Event event) {
				switch (event.detail) {
				case SWT.TRAVERSE_ESCAPE:
					shell.close();
					event.detail = SWT.TRAVERSE_NONE;
					event.doit = false;
					break;
				}
			}
		});
		shell.setText(title);
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, 0);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 3;
		layout.numColumns = 3;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label l = new Label(composite, SWT.None);
		l.setText("Value: ");
		l.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		// create message
		text = new Text(composite, SWT.BORDER);
		final GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.widthHint = 200;
		gd.horizontalIndent = 10;
		text.setLayoutData(gd);
		text.setText(getInitialValue());

		final Image image = FieldDecorationRegistry.getDefault()
				.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR).getImage();

		final ControlDecoration deco = new ControlDecoration(text, SWT.TOP | SWT.LEFT);
		deco.setImage(image);
		deco.hide();

		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				String r = verify(text.getText());
				if (r != null) {
					deco.setDescriptionText(r);
					deco.show();
					ok.setEnabled(false);
				} else {
					deco.hide();
					ok.setEnabled(true);
				}
			}
		});
		text.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				if (e.detail == SWT.CANCEL) {
					// nothing
				} else if (ok.isEnabled()) {
					set(text.getText());
				}
			}
		});
		text.selectAll();
		text.setFocus();

		addOKButton(composite);
		composite.pack();
		return composite;
	}

	/**
	 *
	 */
	protected abstract void set(String value);

	/**
	 * @param text2
	 * @return
	 */
	protected abstract String verify(String value);

	/**
	 * @return
	 */
	protected abstract String getInitialValue();


	/**
	 * @param b
	 */
	private final void addOKButton(Composite composite) {
		this.ok = new Button(composite, SWT.PUSH);
		this.ok.setText(IDialogConstants.OK_LABEL);
		this.ok.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

		this.ok.getShell().setDefaultButton(this.ok);
		this.ok.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				set(text.getText());
				setReturnCode(OK);
				close();
			}
		});
	}

	@Override
	protected void handleShellCloseEvent() {
		set(getInitialValue());
		super.handleShellCloseEvent();
	}

	public static class SetValueEvent extends ADirectedEvent {
		private final String value;

		public SetValueEvent(String value) {
			this.value = value;
		}

		/**
		 * @return the value, see {@link #value}
		 */
		public String getValue() {
			return value;
		}
	}

}
