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
package org.caleydo.core.io.gui.dataimport.widget;

import org.caleydo.core.util.base.BooleanCallback;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * @author Samuel Gratzl
 *
 */
public class SelectAllNoneWidget {
	private final Group group;

	public SelectAllNoneWidget(Composite parent, final BooleanCallback callback) {
		this.group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		this.group.setText("Column Selection");
		this.group.setLayout(new GridLayout(2, false));
		this.group.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));
		Button all = new Button(this.group, SWT.PUSH);
		all.setText("Select All");
		all.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				callback.on(true);
			}
		});
		Button none = new Button(this.group, SWT.PUSH);
		none.setText("Select None");
		none.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				callback.on(false);
			}
		});
	}

	public void setEnabled(boolean enabled) {
		this.group.setEnabled(enabled);
	}
}
