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

import org.caleydo.core.event.EventPublisher;
import org.caleydo.vis.rank.internal.event.DistributionChangedEvent;
import org.caleydo.vis.rank.model.StackedRankColumnModel;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Samuel Gratzl
 *
 */
public class EditDistributionsDialog extends TitleAreaDialog {
	private StackedRankColumnModel model;
	private Object receiver;

	/**
	 * @param parentShell
	 * @param receiver
	 * @param model
	 */
	public EditDistributionsDialog(Shell parentShell, StackedRankColumnModel model, Object receiver) {
		super(parentShell);
		this.model = model;
		this.receiver = receiver;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		parent = (Composite) super.createDialogArea(parent);
		// FIXME
		return parent;
	}

	@Override
	public void create() {
		super.create();
		getShell().setText("Edit distributions");
		setTitle("Edit the distributions of");
		setMessage(model.getTooltip());
	}

	@Override
	protected void okPressed() {
		if (!validate())
			return;
		float[] distributions = new float[model.size()];
		// FIXME
		EventPublisher.publishEvent(new DistributionChangedEvent(distributions).to(receiver));
		super.okPressed();
	}

	/**
	 * @return
	 */
	private boolean validate() {
		// TODO Auto-generated method stub
		return false;
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
