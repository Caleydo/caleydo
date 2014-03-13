/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.imageviewer.internal;

import java.util.List;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.view.ARcpGLElementViewPart;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.layout2.AGLElementView;
import org.caleydo.datadomain.image.ImageDataDomain;
import org.caleydo.datadomain.image.LayeredImage;
import org.caleydo.view.imageviewer.internal.serial.SerializedImageViewerView;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 *
 * @author Thomas Geymayer
 *
 */
public class ImageViewerViewPart extends ARcpGLElementViewPart {

	public ImageViewerViewPart() {
		super(SerializedImageViewerView.class);
	}

	@Override
	protected AGLElementView createView(IGLCanvas canvas) {
		return new ImageViewerView(glCanvas);
	}

	@Override
	public void addToolBarContent(IToolBarManager toolBarManager) {
		toolBarManager.add(new MyComboBox("Hello"));
		toolBarManager.update(true);
	}

	protected class MyComboBox extends ControlContribution {

		Combo domainCombo;
		Combo imageCombo;

		public MyComboBox(String str) {
			super(str);
		}

		public ImageDataDomain getSelectedDomain() {
			return (ImageDataDomain) getDomains().get(domainCombo.getSelectionIndex());
		}

		public LayeredImage getSelectedImage() {
			return getSelectedDomain().getImageSet().getImage(imageCombo.getText());
		}

		@Override
		protected Control createControl(Composite parent) {

			final int comboStyle = SWT.NONE | SWT.READ_ONLY | SWT.DROP_DOWN;

			final Composite composite = new Composite(parent, SWT.NULL);
			RowLayout layout = new RowLayout();
			composite.setLayout(layout);

			domainCombo = new Combo(composite, comboStyle);
			imageCombo = new Combo(composite, comboStyle);
			imageCombo.add("<select image>");
			imageCombo.select(0);
			imageCombo.setEnabled(false);

			for (IDataDomain dataDomain : getDomains())
				domainCombo.add(dataDomain.getLabel());
			domainCombo.select(0);

			domainCombo.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					imageCombo.removeAll();

					ImageDataDomain imageSet = getSelectedDomain();
					for (String name : imageSet.getImageSet().getImageNames())
						imageCombo.add(name);
					imageCombo.select(0);
					imageCombo.setEnabled(true);
				}
			});

			imageCombo.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					((ImageViewerView) getView()).getImageViewer().setImage(getSelectedImage());
				}
			});

			return composite;
		}

		protected List<IDataDomain> getDomains() {
			return DataDomainManager.get().getDataDomainsByType(ImageDataDomain.DATA_DOMAIN_TYPE);
		}
	}
}
