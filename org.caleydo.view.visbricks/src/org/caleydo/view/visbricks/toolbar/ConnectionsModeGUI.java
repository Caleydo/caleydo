package org.caleydo.view.visbricks.toolbar;

import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.view.storagebased.ConnectionsModeEvent;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Slider;

public class ConnectionsModeGUI extends ControlContribution implements IToolBarItem {

	protected ConnectionsModeGUI(String id) {
		super(id);
	}

	@Override
	protected Control createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NULL);
		RowLayout layout = new RowLayout();
		// layout.marginHeight = layout.marginWidth = layout.horizontalSpacing =
		// 0;
		composite.setLayout(layout);

		final Button[] radios = new Button[1];
		radios[0] = new Button(composite, SWT.CHECK);
		radios[0].setSelection(true);
		radios[0].setText("Switch Connections ON/OFF");

		final Button dynamicTrendHighlightButton = new Button(composite, SWT.CHECK);
		dynamicTrendHighlightButton.setText("Dynamic Connection Highlight Focus");

		final Slider slider = new Slider(composite, SWT.HORIZONTAL);
		slider.setValues(0, 0, 100, 1, 1, 1);
		slider.setLayoutData(new RowData(130, 20));

		Listener listener = new Listener() {
			@Override
			public void handleEvent(Event event) {

				if (dynamicTrendHighlightButton.getSelection() == false)
					slider.setEnabled(false);
				else
					slider.setEnabled(true);
				
				GeneralManager
						.get()
						.getEventPublisher()
						.triggerEvent(
								new ConnectionsModeEvent(radios[0].getSelection(),
										dynamicTrendHighlightButton.getSelection(),
										slider.getSelection() / 100f));
			}

		};
		slider.addListener(SWT.Selection, listener);
		dynamicTrendHighlightButton.addListener(SWT.Selection, listener);
		radios[0].addListener(SWT.Selection, listener);

		return composite;
	};
}
