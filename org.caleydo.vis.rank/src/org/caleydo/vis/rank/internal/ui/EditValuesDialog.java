/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.internal.ui;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.vis.rank.internal.event.SetValueEvent;
import org.caleydo.vis.rank.model.ACompositeRankColumnModel;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.OrderColumn;
import org.caleydo.vis.rank.model.RankRankColumnModel;
import org.caleydo.vis.rank.model.mixin.ISetableColumnMixin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * a swt dialog with a title and description text fields
 *
 * @author Samuel Gratzl
 *
 */
public class EditValuesDialog extends Dialog {
	private final List<ARankColumnModel> columns;
	private final Object receiver;
	private final IRow row;

	public EditValuesDialog(Shell parentShell, IRow row,
			Iterator<ARankColumnModel> columns, Object receiver) {
		super(parentShell);
		this.receiver = receiver;
		this.row = row;
		this.columns = filterColumns(columns);
	}

	private static List<ARankColumnModel> filterColumns(Iterator<ARankColumnModel> columns2) {
		List<ARankColumnModel> r = new ArrayList<ARankColumnModel>();
		while (columns2.hasNext()) {
			ARankColumnModel c = columns2.next();
			if (c instanceof RankRankColumnModel || c instanceof OrderColumn)
				continue;
			if (c instanceof ACompositeRankColumnModel) {
				r.addAll(filterColumns(((ACompositeRankColumnModel) c).iterator()));
			} else {
				r.add(c);
			}
		}
		return r;
	}

	@Override
	public void create() {
		super.create();
		getShell().setText("Edit values of row: " + row);
	}

	@Override
	protected Composite createDialogArea(Composite parent) {
		Composite res = (Composite) super.createDialogArea(parent);
		ScrolledComposite scrolledComposite = new ScrolledComposite(res, SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setExpandHorizontal(true);
		Composite p = new Composite(scrolledComposite, SWT.NONE);
		{
			p.setLayout(new GridLayout(4, false));

			for (final ARankColumnModel col : columns) {
				Label l = new Label(p, SWT.RIGHT);
				l.setText(col.getTitle() + ":");
				boolean editAble = (col instanceof ISetableColumnMixin);
				final Text t = new Text(p, SWT.SINGLE | (editAble ? SWT.NONE : SWT.READ_ONLY)
						| SWT.BORDER);
				t.setText(col.getValue(row));

				if (editAble) {
					final ISetableColumnMixin sc = (ISetableColumnMixin) col;
					t.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
					Button set = new Button(p, SWT.PUSH);
					set.setText("Apply");
					final Button revert = new Button(p, SWT.PUSH);
					revert.setText("Revert");
					revert.setToolTipText("Original: " + sc.getOriginalValue(row));
					revert.setEnabled(sc.isOverriden(row));
					revert.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							onRevertValue(sc, t);
							revert.setEnabled(false);
							t.setText(sc.getOriginalValue(row));
							super.widgetSelected(e);
						}
					});
					set.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							onApplyValue(sc, t);
							revert.setEnabled(true);
							super.widgetSelected(e);
						}
					});
				} else {
					GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
					gd.horizontalSpan = 3;
					t.setLayoutData(gd);

				}
			}
		}

		scrolledComposite.setContent(p);
		Point point = p.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		p.setSize(point);
		scrolledComposite.setMinSize(point);

		return res;
	}

	protected void onRevertValue(ISetableColumnMixin col, Text t) {
		EventPublisher.trigger(new SetValueEvent(row, col, null).to(receiver));
	}

	protected void onApplyValue(ISetableColumnMixin col, Text t) {
		EventPublisher.trigger(new SetValueEvent(row, col, t.getText()).to(receiver));
	}

	@Override
	protected void okPressed() {
		super.okPressed();
	}
}

