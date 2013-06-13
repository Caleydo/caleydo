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
import org.caleydo.vis.rank.model.ScriptedRankColumnModel;
import org.caleydo.vis.rank.model.mapping.ScriptedMappingFunction;
import org.caleydo.vis.rank.model.mixin.IScriptedColumnMixin;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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
	private IScriptedColumnMixin model;

	private Text codeUI;
	private Text testUI;
	private Text testOutputUI;
	private CompiledScript script;

	private StringWriter w = new StringWriter();

	public JSCombineEditorDialog(Shell parentShell, Object receiver, IScriptedColumnMixin model) {
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
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.minimumHeight = gd.heightHint = 250;
		group.setLayoutData(gd);
		group.setLayout(new GridLayout(1, true));
		group.setText("Code");
		codeUI = new Text(group, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		codeUI.setLayoutData(gd);
		codeUI.setText(model.getCode());

		group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		group.setText("Test");
		group.setLayout(new GridLayout(2, false));
		Label l = new Label(group, SWT.None);
		l.setText("Input: ");
		l.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));
		testUI = new Text(group, SWT.BORDER);
		testUI.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		testUI.setText("0.25 0.3 0.6");
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
			float[] fs = parseArray(testUI.getText());
			if (!verifyCode())
				return;
			w.getBuffer().setLength(0); // reset
			Bindings b = engine.createBindings();
			b.put("vs", fs);
			String output = Objects.toString(script.eval(b));
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
		String fullCode = ScriptedRankColumnModel.fullCode(codeUI.getText());
		Compilable c = (Compilable) engine;
		try {
			this.script = c.compile(fullCode);
			// dummy test
			Bindings b = engine.createBindings();
			b.put("vs", new float[] { 0.5f, 0.2f, 0.1f });
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
}
