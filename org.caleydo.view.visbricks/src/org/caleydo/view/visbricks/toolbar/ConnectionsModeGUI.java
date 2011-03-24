package org.caleydo.view.visbricks.toolbar;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.view.storagebased.ConnectionsModeEvent;
import org.caleydo.rcp.view.toolbar.IToolBarItem;
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

		final Button[] radios = new Button[3];
		radios[0] = new Button(composite, SWT.RADIO);
		radios[0].setSelection(true);
		radios[0].setText("Show All Connections");

		radios[1] = new Button(composite, SWT.RADIO);
		radios[1].setText("Turn Off Connections");

		radios[2] = new Button(composite, SWT.RADIO);
		radios[2].setText("Dynamic Connection Highlight Focus");

		final Slider slider = new Slider(composite, SWT.HORIZONTAL);
		slider.setValues(0, 0, 100, 1, 1, 1);
		slider.setLayoutData(new RowData(130, 20));

		Listener listener = new Listener() {
			@Override
			public void handleEvent(Event event) {

				if (radios[2].getSelection() == false)
					slider.setEnabled(false);
				else
					slider.setEnabled(true);
				
				GeneralManager
						.get()
						.getEventPublisher()
						.triggerEvent(
								new ConnectionsModeEvent(!radios[1].getSelection(),
										radios[2].getSelection(),
										slider.getSelection() / 100f));
			}

		};
		slider.addListener(SWT.Selection, listener);
//		radios[0].addListener(SWT.Selection, listener);
		radios[1].addListener(SWT.Selection, listener);
		radios[2].addListener(SWT.Selection, listener);

		return composite;
	};
}
