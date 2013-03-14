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
import org.caleydo.vis.rank.internal.event.DistributionChangedEvent;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.StackedRankColumnModel;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Samuel Gratzl
 *
 */
public class EditDistributionsDialog extends TitleAreaDialog {
	private StackedRankColumnModel model;
	private Object receiver;

	private List<Text> texts = new ArrayList<>();

	public EditDistributionsDialog(Shell parentShell, StackedRankColumnModel model, Object receiver) {
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

		float[] dists = model.getDistributions();
		for (int i = 0; i < dists.length; ++i) {
			ARankColumnModel r = model.get(i);
			Label l = new Label(p, SWT.NONE);
			l.setText(r.getHeaderRenderer().toString());
			Text t = new Text(p, SWT.BORDER);
			t.setText(String.format(Locale.ENGLISH, "%.2f", dists[i] * 100));
			t.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			texts.add(t);
		}
		// FIXME
		return parent;
	}

	@Override
	public void create() {
		super.create();
		getShell().setText("Edit distributions");
		setTitle("Edit the distributions of");
		setMessage(model.getTitle());
	}

	@Override
	protected void okPressed() {
		if (!validate())
			return;
		float[] distributions = new float[model.size()];
		for (int i = 0; i < texts.size(); ++i) {
			distributions[i] = Float.parseFloat(texts.get(i).getText()) * 0.01f;
		}
		EventPublisher.publishEvent(new DistributionChangedEvent(distributions).to(receiver));
		super.okPressed();
	}

	/**
	 * @return
	 */
	private boolean validate() {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < texts.size(); ++i) {
			try {
				Float.parseFloat(texts.get(i).getText());
			} catch (NumberFormatException e) {
				b.append("can't parse: " + texts.get(i).getText()).append('\n');
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
				new EditDistributionsDialog(new Shell(), model, receiver).open();
			}
		});
	}

}
