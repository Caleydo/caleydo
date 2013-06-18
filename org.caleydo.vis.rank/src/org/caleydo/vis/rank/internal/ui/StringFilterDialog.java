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
package org.caleydo.vis.rank.internal.ui;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.vis.rank.internal.event.FilterEvent;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 *
 *
 * @author Samuel Gratzl
 *
 */
public class StringFilterDialog extends AFilterDialog {
	private final String filter;

	private Text text;

	private String hint;


	public StringFilterDialog(Shell parentShell, String title, String hint, Object receiver, String filter,
			boolean filterGlobally, boolean hasSnapshots, Point loc) {
		super(parentShell, "Filter " + title, receiver, filterGlobally, hasSnapshots, loc);
		this.hint = hint;
		this.filter = filter;
		this.setShellStyle(SWT.CLOSE);
	}
	@Override
	protected void createSpecificFilterUI(Composite composite) {
		// create message
		GridData gd;

		text = new Text(composite, SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.widthHint = getCharWith(composite, 20);
		text.setLayoutData(gd);
		text.setText(filter == null ? "" : filter);
		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				String newText = text.getText();
				if (newText.length() >= 2 || newText.isEmpty())
					triggerEvent(false);
			}
		});
		text.selectAll();
		text.setFocus();

		final ControlDecoration deco = new ControlDecoration(text, SWT.TOP | SWT.RIGHT);

		// Re-use an existing image
		Image image = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION)
				.getImage();
		// Set description and image
		deco.setDescriptionText("Edit filter " + hint);
		deco.setImage(image);
		// Hide deco if not in focus
		deco.setShowOnlyOnFocus(true);

		createApplyGlobally(composite);
		addOKButton(composite, false);
	}

	@Override
	protected void triggerEvent(boolean cancel) {
		if (cancel) // original values
			EventPublisher.trigger(new FilterEvent(filter, filterGlobally).to(receiver));
		else {
			String t = text.getText();
			t = t.trim();
			t = t.isEmpty() ? null : t;
			EventPublisher.trigger(new FilterEvent(t, isFilterGlobally()).to(receiver));
		}
	}
}
