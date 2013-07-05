/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 *
 */
package org.caleydo.core.io.gui.dataimport.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.caleydo.core.util.base.ICallback;
import org.caleydo.core.util.collection.Pair;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

/**
 * widget for selecting a delimiter
 *
 * @author Samuel Gratzl
 *
 */
public class DelimiterWidget {
	private final List<Button> buttons = new ArrayList<>();
	private final Text customized;
	private final ICallback<String> callback;
	private final Group group;

	public DelimiterWidget(Composite parent, ICallback<String> callback) {
		this.group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		this.callback = callback;
		group.setText("Separated by (delimiter)");
		group.setLayout(new RowLayout());

		for (Pair<String, String> elem : Arrays.asList(Pair.make("TAB", "\t"), Pair.make(";", ";"),
				Pair.make(",", ","), Pair.make(".", "."), Pair.make("SPACE", " "))) {
			Button b = new Button(group, SWT.RADIO);
			b.setText(elem.getFirst());
			b.setData(elem.getSecond());
			b.setBounds(10, 5, 75, 30);
			buttons.add(b);
		}
		buttons.get(0).setSelection(true);

		SelectionListener selectionListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button selectedButton = (Button) e.getSource();
				customized.setEnabled(false);
				fireDelimeterChange((String) selectedButton.getData());
			}
		};
		for (Button b : buttons) { // register afterwards for ignoring setting default
			b.addSelectionListener(selectionListener);
		}

		Button customizedDelimiter = new Button(group, SWT.RADIO);
		customizedDelimiter.setText("Other");
		customizedDelimiter.setBounds(10, 5, 75, 30);
		customizedDelimiter.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				customized.setEnabled(true);
				fireDelimeterChange(" ");
			}
		});
		buttons.add(customizedDelimiter);

		customized = new Text(group, SWT.BORDER);
		customized.setBounds(0, 0, 75, 30);
		customized.setTextLimit(1);
		customized.setEnabled(false);
		customized.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				fireDelimeterChange(customized.getText());
			}
		});
	}

	public void setEnabled(boolean enabled) {
		this.group.setEnabled(enabled);
	}

	public String getDelimeter() {
		for (Button b : buttons) {
			if (!b.getSelection())
				continue;

			String d = (String) b.getData();
			if (d == null) {
				d = customized.getText();
			}
			return d;
		}
		return null;
	}

	public void setDelimeter(String delimeter) {
		for (Button b : buttons) {
			String d = (String) b.getData();
			if (d != null && d.equals(delimeter)) {
				b.setSelection(true);
				return;
			}
		}
		// customized
		buttons.get(buttons.size() - 1).setSelection(true);
		customized.setText(delimeter);
	}

	protected void fireDelimeterChange(String delimeter) {
		callback.on(delimeter);
	}
}
