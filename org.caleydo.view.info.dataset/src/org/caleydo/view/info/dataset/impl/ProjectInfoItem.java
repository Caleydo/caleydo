/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.info.dataset.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Locale;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ProjectMetaData;
import org.caleydo.core.util.system.BrowserUtils;
import org.caleydo.view.info.dataset.spi.IDataSetItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;

/**
 * project meta data
 * 
 * @author Samuel Gratzl
 * 
 */
public class ProjectInfoItem implements IDataSetItem {
	@Override
	public ExpandItem create(ExpandBar expandBar) {
		ProjectMetaData metaData = GeneralManager.get().getMetaData();
		if (metaData.keys().isEmpty())
			return null;
		ExpandItem expandItem = new ExpandItem(expandBar, SWT.NONE);
		expandItem.setText("Project: " + metaData.getName());
		Composite g = new Composite(expandBar, SWT.NONE);
		g.setLayout(new GridLayout(2, false));
		createLine(
				g,
				"Creation Date",
				DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.ENGLISH).format(
						metaData.getCreationDate()));
		for (String key : metaData.keys()) {
			createLine(g, key, metaData.get(key));
		}

		expandItem.setControl(g);
		expandItem.setHeight(g.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		expandItem.setExpanded(false);

		return expandItem;
	}

	private void createLine(Composite parent, String label, String value) {
		if (label == null || label.trim().isEmpty() || value == null || value.trim().isEmpty())
			return;
		Label l = new Label(parent, SWT.NO_BACKGROUND);
		l.setText(label + ":");
		l.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		try {
			final URL url = new URL(value);
			Link v = new Link(parent, SWT.NO_BACKGROUND);

			value = url.toExternalForm();
			if (value.length() > 20)
				value = value.substring(0, 20 - 3) + "...";
			v.setText("<a href=\"" + url.toExternalForm() + "\">" + value + "</a>");
			v.setToolTipText(url.toExternalForm());
			v.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
			v.addSelectionListener(BrowserUtils.LINK_LISTENER);
		} catch (MalformedURLException e) {
			Label v = new Label(parent, SWT.NO_BACKGROUND);
			v.setText(value);
			v.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		}
	}
}
