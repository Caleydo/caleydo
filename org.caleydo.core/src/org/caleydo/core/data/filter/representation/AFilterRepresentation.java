/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.filter.representation;

import org.caleydo.core.data.filter.Filter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

/**
 * Base class for filter dialogs
 *
 * @author Alexander Lex
 * @author Thomas Geymayer
 * @author Marc Streit
 */
public abstract class AFilterRepresentation {

	protected Composite parentComposite;

	protected Filter filter;

	protected Button okButton;

	protected boolean isDirty = true;

	/**
	 * Whether currently the filter representation is displayed. Used to prevent multiple open filter
	 * representations of the same filter.
	 */
	protected boolean isDisplayed = false;

	/**
	 * @return true if a new window was opened, false if a window was already opened
	 */
	public synchronized boolean create() {
		// get focus instead of creating new dialog if a dialog is
		// already opened
		final boolean requestFocus = isDisplayed;

		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (requestFocus) {
					parentComposite.setFocus();
				}
				else {
					parentComposite = new Shell();
					parentComposite.setLayout(new GridLayout(1, false));
				}
			}
		});

		isDisplayed = true;
		return !requestFocus;
	}

	protected void addOKCancel() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				Composite composite = new Composite(parentComposite, SWT.NONE);
				composite.setLayout(new GridLayout(4, false));
				GridData gridData = new GridData();
				gridData.grabExcessHorizontalSpace = true;
				gridData.horizontalAlignment = GridData.FILL;
				composite.setLayoutData(gridData);

				Label dummy = new Label(composite, SWT.NONE);
				dummy.setLayoutData(gridData);

				okButton = new Button(composite, SWT.PUSH);
				okButton.setText("  OK  ");
				final Button deleteButton = new Button(composite, SWT.PUSH);
				deleteButton.setText("Delete");
				Button cancelButton = new Button(composite, SWT.PUSH);
				cancelButton.setText("Cancel");
				Listener listener = new Listener() {
					@Override
					public void handleEvent(Event event) {
						if (event.widget == deleteButton)
							triggerRemoveFilterEvent();
						else if (event.widget == okButton)
							applyFilter();

						isDisplayed = false;
						((Shell) parentComposite).close();
					}
				};
				okButton.addListener(SWT.Selection, listener);
				deleteButton.addListener(SWT.Selection, listener);
				cancelButton.addListener(SWT.Selection, listener);

				Monitor primary = parentComposite.getDisplay().getPrimaryMonitor();
				Rectangle bounds = primary.getBounds();
				Rectangle rect = parentComposite.getBounds();
				int x = bounds.x + (bounds.width - rect.width) / 2;
				int y = bounds.y + (bounds.height - rect.height) / 2;
				parentComposite.setLocation(x, y);

				parentComposite.pack();

				((Shell) parentComposite).open();
			}
		});
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	protected abstract void createVADelta();

	protected abstract void triggerRemoveFilterEvent();

	protected abstract void applyFilter();
}
