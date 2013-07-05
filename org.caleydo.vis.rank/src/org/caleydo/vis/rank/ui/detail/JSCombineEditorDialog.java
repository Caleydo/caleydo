/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.ui.detail;

import static org.caleydo.core.event.EventPublisher.trigger;

import java.io.StringWriter;
import java.util.Objects;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.caleydo.vis.rank.internal.event.CodeUpdateEvent;
import org.caleydo.vis.rank.internal.event.DualCodeUpdateEvent;
import org.caleydo.vis.rank.model.ScriptedRankColumnModel;
import org.caleydo.vis.rank.model.mapping.ScriptedMappingFunction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
/**
 * @author Samuel Gratzl
 *
 */
public class JSCombineEditorDialog extends TitleAreaDialog {
	private static final String DEFAULT_MESSAGE = "that combines the values given provided in \"values\" array to a 0..1 range\n";
	private final Object receiver;
	private final ScriptEngine engine;
	private ScriptedRankColumnModel model;

	private Text codeUI;
	private Text testUI;
	private Text testWeightUI;
	private Text testOutputUI;

	private Text codeOrderUI;
	private Text testWeight2UI;
	private Text testOrderAUI;
	private Text testOrderBUI;
	private Text testOrderOutputUI;

	private CompiledScript script;

	private StringWriter w = new StringWriter();

	public JSCombineEditorDialog(Shell parentShell, Object receiver, ScriptedRankColumnModel model) {
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
		getShell().setText("Edit JavaScript Combination Function");
		setTitle("Edit JavaScript Combination Function");
		// Set the message
		setMessage(DEFAULT_MESSAGE, IMessageProvider.INFORMATION);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		parent = (Composite) super.createDialogArea(parent);
		final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);

		{
			Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
			gd.minimumHeight = gd.heightHint = 100;
			group.setLayoutData(gd);
			group.setLayout(new GridLayout(1, true));
			group.setText("Combine Code");
			codeUI = new Text(group, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
			codeUI.setLayoutData(gd);
			codeUI.setText(model.getCode());
			group.pack();
		}

		{
			Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
			group.setLayoutData(gd);
			group.setText("Test");
			group.setLayout(new GridLayout(2, false));

			Label l = new Label(group, SWT.None);
			l.setText("Input: ");
			l.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
			testUI = new Text(group, SWT.BORDER);
			testUI.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			testUI.setText("0.25 0.3 0.6");

			l = new Label(group, SWT.None);
			l.setText("Weights: ");
			l.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
			testWeightUI = new Text(group, SWT.BORDER);
			testWeightUI.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			testWeightUI.setText("0.3 0.3 0.3");

			Button button = new Button(group, SWT.PUSH);
			button.setText("Test:");
			button.setData(100);
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					buttonPressed(((Integer) event.widget.getData()).intValue());
				}
			});
			button.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
			testOutputUI = new Text(group, SWT.BORDER | SWT.READ_ONLY);
			testOutputUI.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

			group.pack();
		}
		{
			Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
			group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			group.setLayout(new GridLayout(1, true));
			group.setText("Ranking Code");
			codeOrderUI = new Text(group, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
			codeOrderUI.setLayoutData(gd);
			codeOrderUI.setText(model.getCodeOrder());
			group.pack();
		}
		{
			Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
			group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			group.setText("Test");
			group.setLayout(new GridLayout(2, false));

			Label l = new Label(group, SWT.None);
			l.setText("Input A: ");
			l.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
			testOrderAUI = new Text(group, SWT.BORDER);
			testOrderAUI.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			testOrderAUI.setText("0.25 0.3 0.6");

			l = new Label(group, SWT.None);
			l.setText("Input B: ");
			l.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
			testOrderBUI = new Text(group, SWT.BORDER);
			testOrderBUI.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			testOrderBUI.setText("0.2 0.3 0.8");

			l = new Label(group, SWT.None);
			l.setText("Weights: ");
			l.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
			testWeight2UI = new Text(group, SWT.BORDER);
			testWeight2UI.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			testWeight2UI.setText("0.3 0.3 0.3");

			Button button = new Button(group, SWT.PUSH);
			button.setText("Test:");
			button.setData(101);
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					buttonPressed(((Integer) event.widget.getData()).intValue());
				}
			});
			button.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
			testOrderOutputUI = new Text(group, SWT.BORDER | SWT.READ_ONLY);
			testOrderOutputUI.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			group.pack();
		}

		return parent;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.RETRY_ID, "Apply", false);
		super.createButtonsForButtonBar(parent);
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.RETRY_ID)
			applyPressed();
		else if (buttonId == 100)
			testPressed();
		else if (buttonId == 101)
			testOrderPressed();
		super.buttonPressed(buttonId);
	}

	private void testPressed() {
		try {
			float[] fs = parseArray(testUI.getText());
			if (!verifyCode())
				return;
			w.getBuffer().setLength(0); // reset
			Bindings b = engine.createBindings();
			b.put("v", new Wrapper(fs, 0));
			b.put("weights", parseArray(testWeightUI.getText()));
			b.put("mode", "apply");
			String output = Objects.toString(script.eval(b));
			testOutputUI.setText(output);
			String extra = w.toString();
			if (extra.length() > 0)
				setMessage(extra, IMessageProvider.WARNING);
			else
				setMessage(DEFAULT_MESSAGE, IMessageProvider.INFORMATION);
		} catch (NumberFormatException e) {
			testOutputUI.setText("Invalid input: " + e.getMessage());
		} catch (ScriptException e) {
			testOutputUI.setText("Error: " + e.getMessage());
		}
	}

	private void testOrderPressed() {
		try {
			float[] as = parseArray(testOrderAUI.getText());
			float[] bs = parseArray(testOrderBUI.getText());
			if (!verifyCodeOrder() || !verifyCode())
				return;
			w.getBuffer().setLength(0); // reset
			Bindings b = engine.createBindings();
			b.put("a", new Wrapper(as, 0));
			b.put("b", new Wrapper(bs, 0));
			b.put("weights", parseArray(testWeightUI.getText()));
			b.put("mode", "multi");
			Object output = script.eval(b);
			if (output instanceof Number)
				output = ((Number) output).intValue();
			testOrderOutputUI.setText(Objects.toString(output));
			String extra = w.toString();
			if (extra.length() > 0)
				setMessage(extra, IMessageProvider.WARNING);
			else
				setMessage(DEFAULT_MESSAGE, IMessageProvider.INFORMATION);
		} catch (NumberFormatException e) {
			testOutputUI.setText("Invalid input: " + e.getMessage());
		} catch (ScriptException e) {
			testOutputUI.setText("Error: " + e.getMessage());
		}
	}

	public static class Wrapper {
		private final float[] values;
		private float value;

		/**
         *
         */
		public Wrapper(float[] values, float value) {
			this.values = values;
			this.value = value;
		}

		/**
		 * @return the values, see {@link #values}
		 */
		public float[] getValues() {
			return values;
		}

		/**
		 * @return the value, see {@link #value}
		 */
		public float getValue() {
			return value;
		}

		/**
		 * @param value
		 *            setter, see {@link value}
		 */
		public void setValue(float value) {
			this.value = value;
		}
	}

	/**
	 * @param text
	 * @return
	 */
	private static float[] parseArray(String text) {
		String[] ss = text.split("[\\s;]");
		float[] vs = new float[ss.length];
		for (int i = 0; i < ss.length; ++i)
			vs[i] = Float.parseFloat(ss[i]);
		return vs;
	}

	private boolean verifyCode() {
		String fullCode = ScriptedRankColumnModel.fullCode(codeUI.getText(), codeOrderUI.getText());
		Compilable c = (Compilable) engine;
		try {
			this.script = c.compile(fullCode);
			// dummy test
			Bindings b = engine.createBindings();
			b.put("v", new Wrapper(new float[] { 0.5f, 0.2f, 0.1f }, 0));
			b.put("weights", new float[] { 0.3f, 0.3f, 0.3f });
			b.put("mode", "apply");
			Object r = script.eval(b);
			if (!(r instanceof Number)) {
				setErrorMessage("function must return a float value");
				return false;
			}
			setErrorMessage(null);
			return true;
		} catch (ScriptException e) {
			setErrorMessage(String.format("Invalid code:\nRow %d Column %d: %s", e.getLineNumber(),
					e.getColumnNumber(), e.getMessage()));
		}
		return false;
	}

	private boolean verifyCodeOrder() {
		String fullCode = ScriptedRankColumnModel.fullCode(codeUI.getText(), codeOrderUI.getText());
		Compilable c = (Compilable) engine;
		try {
			this.script = c.compile(fullCode);
			// dummy test
			Bindings b = engine.createBindings();
			b.put("a", new Wrapper(new float[] { 0.5f, 0.2f, 0.1f }, 0.5f));
			b.put("b", new Wrapper(new float[] { 0.2f, 0.5f, 0.7f }, 0.3f));
			b.put("weights", new float[] { 0.3f, 0.3f, 0.3f });
			b.put("mode", "order");
			Object r = script.eval(b);
			if (!(r instanceof Number)) {
				setErrorMessage("function must return an int value");
				return false;
			}
			setErrorMessage(null);
			return true;
		} catch (ScriptException e) {
			setErrorMessage(String.format("Invalid code:\nRow %d Column %d: %s", e.getLineNumber(),
					e.getColumnNumber(), e.getMessage()));
		}
		return false;
	}

	private void applyPressed() {
		if (!verifyCode() || !verifyCodeOrder())
			return;
		trigger(new CodeUpdateEvent(codeUI.getText()).to(receiver));
	}

	@Override
	protected void okPressed() {
		if (!verifyCode() || !verifyCodeOrder())
			return;
		trigger(new DualCodeUpdateEvent(codeUI.getText(), codeOrderUI.getText()).to(receiver));
		super.okPressed();
	}
}
