package org.caleydo.core.util.mapping.color;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.view.tablebased.RedrawViewEvent;
import org.caleydo.core.io.gui.IDataOKListener;
import org.caleydo.core.manager.GeneralManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

public class ChooseColorMappingDialog
	extends Dialog
	implements IDataOKListener {

	private ColorMapper colorMapper;

	private ATableBasedDataDomain dataDomain;

	private Group colorSchemeGroup;

	public ChooseColorMappingDialog(Shell parent, ATableBasedDataDomain dataDomain) {
		super(parent);
		this.dataDomain = dataDomain;
		colorMapper = dataDomain.getColorMapper();
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Choose Color Map for " + dataDomain.getLabel() + " Data");
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		colorSchemeGroup = new Group(parent, SWT.NONE);
		colorSchemeGroup.setLayout(new GridLayout(2, false));
		colorSchemeGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		for (final EDefaultColorSchemes colorScheme : EDefaultColorSchemes.values()) {

			Button button = new Button(colorSchemeGroup, SWT.RADIO);
			button.setText(colorScheme.getColorSchemeName());
			button.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					colorMapper = ColorMapper.createDefaultMapper(colorScheme);

					dataDomain.setColorMapper(colorMapper);

					EventPublisher eventPublisher = GeneralManager.get().getEventPublisher();

					RedrawViewEvent redrawEvent = new RedrawViewEvent();
					redrawEvent.setDataDomainID(dataDomain.getDataDomainID());
					eventPublisher.triggerEvent(redrawEvent);

					UpdateColorMappingEvent event = new UpdateColorMappingEvent();
					event.setDataDomainID(dataDomain.getDataDomainID());
					// event.setSender(this);
					eventPublisher.triggerEvent(event);
				}
			});

			CLabel colorMappingPreview = new CLabel(colorSchemeGroup, SWT.SHADOW_IN | SWT.RADIO);
			GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.minimumWidth = 250;
			colorMappingPreview.setLayoutData(gridData);
			ColorMapper.createColorMappingPreview(ColorMapper.createDefaultMapper(colorScheme),
				colorMappingPreview);
		}

		return parent;
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		Control control = super.createButtonBar(parent);
		// if (!dataChooserComposite.isOK())
		// getButton(IDialogConstants.OK_ID).setEnabled(false);
		return control;
	}

	@Override
	protected void okPressed() {
		super.okPressed();
	}

	/**
	 * @return the colorMapper, see {@link #colorMapper}
	 */
	public ColorMapper getColorMapper() {
		return colorMapper;
	}

	@Override
	public void dataOK() {
		getButton(IDialogConstants.OK_ID).setEnabled(true);
	}
}
