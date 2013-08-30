/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.gui.util;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;

/**
 * @author Samuel Gratzl
 *
 */
public class SpinnerFieldEditor extends FieldEditor {

	/**
	 * Value that will feed Spinner.setIncrement(int).
	 */
	private int incrementValue;

	/**
	 * Value that will feed Spinner.setMaximum(int).
	 */
	private int maxValue;

	/**
	 * Value that will feed Spinner.setMinimum(int).
	 */
	private int minValue;

	/**
	 * Old integer value.
	 */
	private int oldValue;

	private int pageIncrementValue;

	/**
	 * The spinner, or <code>null</code> if none.
	 */
	protected Spinner spinner;

	/**
	 * Creates a spinner field editor.
	 *
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param parent
	 *            the parent of the field editor's control
	 */
	public SpinnerFieldEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent);
		setDefaultValues();
	}

	/**
	 * Creates a spinner field editor with particular spinner values.
	 *
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param parent
	 *            the parent of the field editor's control
	 * @param min
	 *            the value used for Spinner.setMinimum(int).
	 * @param max
	 *            the value used for Spinner.setMaximum(int).
	 * @param increment
	 *            the value used for Spinner.setIncrement(int).
	 * @param pageIncrement
	 *            the value used for Spinner.setPageIncrement(int).
	 */
	public SpinnerFieldEditor(String name, String labelText, Composite parent, int min, int max, int increment,
			int pageIncrement) {
		super(name, labelText, parent);
		setValues(min, max, increment, pageIncrement);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.preference.FieldEditor#adjustForNumColumns(int)
	 */
	@Override
	protected void adjustForNumColumns(int numColumns) {
		((GridData) spinner.getLayoutData()).horizontalSpan = numColumns - 1;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.preference.FieldEditor#doFillIntoGrid(org.eclipse.swt.widgets.Composite, int)
	 */
	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		Control control = getLabelControl(parent);
		GridData gd = new GridData();
		control.setLayoutData(gd);

		spinner = getSpinnerControl(parent);
		gd = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		gd.horizontalSpan = numColumns - 1;
		spinner.setLayoutData(gd);
		updateSpinner();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.preference.FieldEditor#doLoad()
	 */
	@Override
	protected void doLoad() {
		if (spinner != null) {
			int value = getPreferenceStore().getInt(getPreferenceName());
			spinner.setSelection(value);
			oldValue = value;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.preference.FieldEditor#doLoadDefault()
	 */
	@Override
	protected void doLoadDefault() {
		if (spinner != null) {
			int value = getPreferenceStore().getDefaultInt(getPreferenceName());
			spinner.setSelection(value);
		}
		valueChanged();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.preference.FieldEditor#doStore()
	 */
	@Override
	protected void doStore() {
		getPreferenceStore().setValue(getPreferenceName(), spinner.getSelection());
	}

	/**
	 * Returns the value that will be used for Spinner.setIncrement(int).
	 *
	 * @return the value.
	 * @see org.eclipse.swt.widgets.Spinner#setIncrement(int)
	 */
	public int getIncrement() {
		return incrementValue;
	}

	/**
	 * Returns the value that will be used for Spinner.setMaximum(int).
	 *
	 * @return the value.
	 * @see org.eclipse.swt.widgets.Spinner#setMaximum(int)
	 */
	public int getMaximum() {
		return maxValue;
	}

	/**
	 * Returns the value that will be used for Spinner.setMinimum(int).
	 *
	 * @return the value.
	 * @see org.eclipse.swt.widgets.Spinner#setMinimum(int)
	 */
	public int getMinimum() {
		return minValue;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.preference.FieldEditor#getNumberOfControls()
	 */
	@Override
	public int getNumberOfControls() {
		return 2;
	}

	/**
	 * Returns the value that will be used for Spinner.setPageIncrement(int).
	 *
	 * @return the value.
	 * @see org.eclipse.swt.widgets.Spinner#setPageIncrement(int)
	 */
	public int getPageIncrement() {
		return pageIncrementValue;
	}

	/**
	 * Returns this field editor's spinner control.
	 *
	 * @return the spinner control, or <code>null</code> if no spinner field is created yet
	 */
	public Spinner getSpinnerControl() {
		return spinner;
	}

	/**
	 * Returns this field editor's spinner control. The control is created if it does not yet exist.
	 *
	 * @param parent
	 *            the parent
	 * @return the spinner control
	 */
	private Spinner getSpinnerControl(Composite parent) {
		if (spinner == null) {
			spinner = new Spinner(parent, SWT.NONE);
			spinner.setFont(parent.getFont());
			spinner.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					valueChanged();
				}
			});
			spinner.addDisposeListener(new DisposeListener() {
				@Override
				public void widgetDisposed(DisposeEvent event) {
					spinner = null;
				}
			});
		} else {
			checkParent(spinner, parent);
		}
		return spinner;
	}

	/**
	 * Set default values for the various spinner fields. These defaults are:<br>
	 * <ul>
	 * <li>Minimum = 0
	 * <li>Maximim = 10
	 * <li>Increment = 1
	 * <li>Page Increment = 1
	 * </ul>
	 */
	private void setDefaultValues() {
		setValues(0, 10, 1, 1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.preference.FieldEditor#setFocus()
	 */
	@Override
	public void setFocus() {
		if (spinner != null && !spinner.isDisposed()) {
			spinner.setFocus();
		}
	}

	/**
	 * Set the value to be used for Spinner.setIncrement(int) and update the spinner.
	 *
	 * @param increment
	 *            a value greater than 0.
	 * @see org.eclipse.swt.widgets.Spinner#setIncrement(int)
	 */
	public void setIncrement(int increment) {
		this.incrementValue = increment;
		updateSpinner();
	}

	/**
	 * Set the value to be used for Spinner.setMaximum(int) and update the spinner.
	 *
	 * @param max
	 *            a value greater than 0.
	 * @see org.eclipse.swt.widgets.Spinner#setMaximum(int)
	 */
	public void setMaximum(int max) {
		this.maxValue = max;
		updateSpinner();
	}

	/**
	 * Set the value to be used for Spinner.setMinumum(int) and update the spinner.
	 *
	 * @param min
	 *            a value greater than 0.
	 * @see org.eclipse.swt.widgets.Spinner#setMinimum(int)
	 */
	public void setMinimum(int min) {
		this.minValue = min;
		updateSpinner();
	}

	/**
	 * Set the value to be used for Spinner.setPageIncrement(int) and update the spinner.
	 *
	 * @param pageIncrement
	 *            a value greater than 0.
	 * @see org.eclipse.swt.widgets.Spinner#setPageIncrement(int)
	 */
	public void setPageIncrement(int pageIncrement) {
		this.pageIncrementValue = pageIncrement;
		updateSpinner();
	}

	/**
	 * Set all Spinner values.
	 *
	 * @param min
	 *            the value used for Spinner.setMinimum(int).
	 * @param max
	 *            the value used for Spinner.setMaximum(int).
	 * @param increment
	 *            the value used for Spinner.setIncrement(int).
	 * @param pageIncrement
	 *            the value used for Spinner.setPageIncrement(int).
	 */
	private void setValues(int min, int max, int increment, int pageIncrement) {
		this.incrementValue = increment;
		this.maxValue = max;
		this.minValue = min;
		this.pageIncrementValue = pageIncrement;
		updateSpinner();
	}

	/**
	 * Update the spinner particulars with set values.
	 */
	private void updateSpinner() {
		if (spinner != null && !spinner.isDisposed()) {
			spinner.setMinimum(getMinimum());
			spinner.setMaximum(getMaximum());
			spinner.setIncrement(getIncrement());
			spinner.setPageIncrement(getPageIncrement());
		}
	}

	/**
	 * Informs this field editor's listener, if it has one, about a change to the value (<code>VALUE</code> property)
	 * provided that the old and new values are different.
	 * <p>
	 * This hook is <em>not</em> called when the spinner is initialized (or reset to the default value) from the
	 * preference store.
	 * </p>
	 */
	protected void valueChanged() {
		setPresentsDefaultValue(false);

		int newValue = spinner.getSelection();
		if (newValue != oldValue) {
			fireStateChanged(IS_VALID, false, true);
			fireValueChanged(VALUE, new Integer(oldValue), new Integer(newValue));
			oldValue = newValue;
		}
	}
}
