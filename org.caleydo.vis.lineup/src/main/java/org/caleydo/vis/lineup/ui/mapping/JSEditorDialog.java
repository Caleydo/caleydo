/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.ui.mapping;

import static org.caleydo.core.event.EventPublisher.trigger;

import java.io.StringWriter;
import java.util.Objects;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.caleydo.vis.lineup.internal.event.CodeUpdateEvent;
import org.caleydo.vis.lineup.model.mapping.ScriptedMappingFunction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
/**
 * @author Samuel Gratzl
 *
 */
public class JSEditorDialog extends TitleAreaDialog {
	private static final String DEFAULT_MESSAGE = "that maps the input value in the variable named: \"value\" to a 0..1 range\n"
			+ "where \"value_min\" and \"value_max\" are the current or defined minimal/maximal input values";
	private final Object receiver;
	private final ScriptEngine engine;
	private ScriptedMappingFunction model;

	private Text codeUI;
	private Text testUI;
	private Text testOutputUI;
	private CompiledScript script;

	private StringWriter w = new StringWriter();

	public JSEditorDialog(Shell parentShell, Object receiver, ScriptedMappingFunction model) {
		super(parentShell);
		setBlockOnOpen(false);
		this.receiver = receiver;
		this.engine = ScriptedMappingFunction.createEngine();
		this.engine.getContext().setWriter(w);
		this.model = model;
	}

	@Override
	public void create() {
		super.create();
		getShell().setText("Edit JavaScript Mapping Function");
		setTitle("Edit JavaScript Mapping Function");
		// Set the message
		setMessage(DEFAULT_MESSAGE, IMessageProvider.INFORMATION);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		parent = (Composite) super.createDialogArea(parent);
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.minimumHeight = gd.heightHint = 250;
		group.setLayoutData(gd);
		group.setLayout(new GridLayout(1, true));
		group.setText("Code");
		codeUI = new Text(group, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		codeUI.setLayoutData(gd);
		codeUI.setText(model.toJavaScript());

		group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		group.setText("Test");
		group.setLayout(new GridLayout(2, false));
		Label l = new Label(group, SWT.None);
		l.setText("Input: ");
		l.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));
		testUI = new Text(group, SWT.BORDER);
		testUI.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		testUI.setText("0.25");
		l = new Label(group, SWT.None);
		l.setText("Output: ");
		l.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));
		testOutputUI = new Text(group, SWT.BORDER | SWT.READ_ONLY);
		testOutputUI.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));


		return parent;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, 5, "Test", false);
		createButton(parent, IDialogConstants.RETRY_ID, "Apply", false);
		super.createButtonsForButtonBar(parent);
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.RETRY_ID)
			applyPressed();
		else if (buttonId == 5)
			testPressed();
		super.buttonPressed(buttonId);
	}

	private void testPressed() {
		try {
			double f = Double.parseDouble(testUI.getText());
			if (!verifyCode())
				return;
			w.getBuffer().setLength(0); // reset
			Bindings b = engine.createBindings();
			b.put("v", f);
			model.addBindings(b);
			final Object r = script.eval(b);
			String output = Objects.toString(r);
			testOutputUI.setText(output);
			String extra = w.toString();
			if (extra.length() > 0)
				setMessage(extra, IMessageProvider.WARNING);
			else
				setMessage(DEFAULT_MESSAGE, IMessageProvider.INFORMATION);
		} catch(NumberFormatException e) {
			testOutputUI.setText("Invalid input: " + e.getMessage());
		} catch (ScriptException e) {
			testOutputUI.setText("Error: " + e.getMessage());
		}
	}

	private boolean verifyCode() {
		String fullCode = ScriptedMappingFunction.fullCode(codeUI.getText());
		Compilable c = (Compilable) engine;
		try {
			this.script = c.compile(fullCode);
			// dummy test
			Bindings b = engine.createBindings();
			b.put("v", 0.5);
			model.addBindings(b);
			Object r = script.eval(b);
			if (!(r instanceof Number)) {
				setErrorMessage("function must return a float value");
				return false;
			}
			setErrorMessage(null);
			return true;
		} catch (ScriptException e) {
			setErrorMessage(String.format("Invalid code:\nRow %d Column %d: %s", e.getLineNumber(),
					e.getColumnNumber(),
					e.getMessage()));
		}
		return false;
	}

	private void applyPressed() {
		if (!verifyCode())
			return;
		trigger(new CodeUpdateEvent(codeUI.getText()).to(receiver));
	}

	@Override
	protected void okPressed() {
		if (!verifyCode())
			return;
		trigger(new CodeUpdateEvent(codeUI.getText()).to(receiver));
		super.okPressed();
	}

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(1, false));

		ScriptedMappingFunction model = new ScriptedMappingFunction(0, 1);
		new JSEditorDialog(shell, null, model).open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();

	}
}
