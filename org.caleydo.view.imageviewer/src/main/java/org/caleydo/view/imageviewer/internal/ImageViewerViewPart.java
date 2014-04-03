/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.imageviewer.internal;

import java.util.List;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.event.ADirectedEvent;
import org.caleydo.core.event.EventPublisher;
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

	protected ImageSelector imageSelector;

	public ImageViewerViewPart() {
		super(SerializedImageViewerView.class);
	}

	@Override
	protected AGLElementView createView(IGLCanvas canvas) {
		return new ImageViewerView(glCanvas);
	}

	@Override
	public void addToolBarContent(IToolBarManager toolBarManager) {
		toolBarManager.add(imageSelector = new ImageSelector());
		toolBarManager.update(true);
	}

	// TODO update on view.init()
	// imageSelector.onImageChange();

	public static class SelectImageEvent extends ADirectedEvent {

		public LayeredImage image;

		public SelectImageEvent(LayeredImage img) {
			image = img;
		}

		@Override
		public boolean checkIntegrity() {
			return image != null;
		}

	}

	protected class ImageSelector extends ControlContribution {

		Combo domainCombo;
		Combo imageCombo;

		public ImageSelector() {
			super("ImageSelector");
		}

		public ImageDataDomain getSelectedDomain() {
			List<IDataDomain> domains = getDomains();
			int selectionIndex = domainCombo.getSelectionIndex();
			if( selectionIndex < 0 || selectionIndex >= domains.size() )
				return null;
			return (ImageDataDomain) domains.get(selectionIndex);
		}

		public LayeredImage getSelectedImage() {
			ImageDataDomain dataDomain = getSelectedDomain();
			if( dataDomain == null )
				return null;
			return dataDomain.getImageSet().getImage(imageCombo.getText());
		}

		public void onDomainChange() {
			imageCombo.removeAll();
			imageCombo.setEnabled(true);

			ImageDataDomain imageSet = getSelectedDomain();
			if( imageSet == null )
				return;

			for (String name : imageSet.getImageSet().getImageNames())
				imageCombo.add(name);
			imageCombo.select(0);

			onImageChange();
		}

		public void onImageChange() {
			LayeredImage img = getSelectedImage();
			if( img == null )
				return;
			EventPublisher.trigger(
				new SelectImageEvent(img).to(
					((ImageViewerView) getView()).getImageViewer()
				)
			);
		}

		@Override
		protected Control createControl(Composite parent) {

			final int comboStyle = SWT.NONE | SWT.READ_ONLY | SWT.DROP_DOWN;

			final Composite composite = new Composite(parent, SWT.NULL);
			RowLayout layout = new RowLayout();
			composite.setLayout(layout);

			domainCombo = new Combo(composite, comboStyle);
			imageCombo = new Combo(composite, comboStyle);

			domainCombo.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					onDomainChange();
				}
			});

			imageCombo.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					onImageChange();
				}
			});
			imageCombo.add("<no image>");
			imageCombo.select(0);
			imageCombo.setEnabled(false);

			for (IDataDomain dataDomain : getDomains())
				domainCombo.add(dataDomain.getLabel());
			domainCombo.select(0);

			onDomainChange();

			return composite;
		}

		protected List<IDataDomain> getDomains() {
			return DataDomainManager.get().getDataDomainsByType(ImageDataDomain.DATA_DOMAIN_TYPE);
		}
	}
}
