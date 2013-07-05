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
package org.caleydo.vis.rank.ui.column;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.vis.rank.internal.event.WeightsChangedEvent;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.StackedRankColumnModel;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author Samuel Gratzl
 * 
 */
public class EditWeightsDialog extends TitleAreaDialog implements VerifyListener, ModifyListener {
	private StackedRankColumnModel model;
	private Object receiver;

	private List<Text> texts = new ArrayList<>();

	private Text sum;

	public EditWeightsDialog(Shell parentShell, StackedRankColumnModel model, Object receiver) {
		super(parentShell);
		this.model = model;
		this.receiver = receiver;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		parent = (Composite) super.createDialogArea(parent);

		Composite p = new Composite(parent, SWT.NONE);
		p.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		p.setLayout(new GridLayout(2, false));

		float[] dists = model.getWeights();

		FocusListener focusListener = new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				Text t = (Text) e.widget;
				t.selectAll();
			}

			@Override
			public void focusLost(FocusEvent e) {
				Text t = (Text) e.widget;
				if (t.getSelectionCount() > 0) {
					t.clearSelection();
				}
			}
		};

		for (int i = 0; i < dists.length; ++i) {
			ARankColumnModel r = model.get(i);
			Label l = new Label(p, SWT.NONE);
			l.setText(clean(r.getTitle()));
			Text t = new Text(p, SWT.BORDER);
			t.addFocusListener(focusListener);
			t.setText(toString(dists[i] * 100));
			t.addModifyListener(this);
			t.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			texts.add(t);
		}

		Label l = new Label(p, SWT.NONE);
		l.setText("Total");
		l.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		this.sum = new Text(p, SWT.BORDER | SWT.READ_ONLY);
		this.sum.setText(toString(100));
		this.sum.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		this.sum.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		return parent;
	}

	private String clean(String title) {
		String t = title;
		if (title.indexOf('\n') >= 0) {
			t = title.substring(0, title.indexOf('\n'));
		}
		return t;
	}

	@Override
	public void modifyText(ModifyEvent e) {
		if (validate()) {
			float sum = 0;
			float[] vs = getValues();
			for (float v : vs)
				sum += v;
			this.sum.setText(toString(sum * 100));
		}
	}

	@Override
	public void verifyText(VerifyEvent e) {
		// if (validate(e)) {
		// float sum = 0;
		// float[] vs = getValues(e);
		// for (float v : vs)
		// sum += v;
		// this.sum.setText(toString(sum * 100));
		// }
	}

	protected String toString(float f) {
		return String.format(Locale.ENGLISH, "%.2f", f);
	}

	@Override
	public void create() {
		super.create();
		getShell().setText("Edit attribute weights");
		setTitle("Edit the attribute weight of");
		setMessage(model.getTitle());
	}

	@Override
	protected void okPressed() {
		if (!validate())
			return;
		float[] weights = getValues();
		EventPublisher.trigger(new WeightsChangedEvent(weights).to(receiver));
		super.okPressed();
	}

	protected float[] getValues() {
		float[] weights = new float[model.size()];
		for (int i = 0; i < texts.size(); ++i) {
			String s = texts.get(i).getText();
			weights[i] = Float.parseFloat(s) * 0.01f;
		}
		return weights;
	}

	/**
	 * @param e2
	 * @return
	 */
	private boolean validate() {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < texts.size(); ++i) {
			String s = texts.get(i).getText();
			if (s.isEmpty()) {
				b.append("missing value").append('\n');
			} else {
				try {
					Float.parseFloat(s);
				} catch (NumberFormatException e) {
					b.append("can't parse: " + s).append('\n');
				}
			}
		}
		if (b.length() > 0)
			setErrorMessage(b.toString());
		else
			setErrorMessage(null);
		return b.length() == 0;
	}

	public static void show(final StackedRankColumnModel model, final Object receiver) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				new EditWeightsDialog(new Shell(), model, receiver).open();
			}
		});
	}

}
