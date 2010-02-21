package org.caleydo.view.scatterplot.toolbar;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.view.storagebased.InitAxisComboEvent;
import org.caleydo.core.manager.event.view.storagebased.YAxisSelectorEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.rcp.view.toolbar.IToolBarItem;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;

/**
 * Toolbar item that contains a slider for TODO
 * 
 * @author Juergen Pillhofer
 */
public class YAxisSelector extends ControlContribution implements IToolBarItem,
		IListenerOwner {

	private InitYAxisComboListener initAxisComboListener;
	private Combo combo;
	private int iSelection;

	public YAxisSelector(String str, int iSliderSelection) {
		super(str);
		iSelection = iSliderSelection;
	}

	@Override
	protected Control createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = layout.marginWidth = layout.horizontalSpacing = 0;
		composite.setLayout(layout);

		combo = new Combo(composite, SWT.HORIZONTAL);
		combo.setLayoutData(new GridData(130, 20));
		// combo.add("Hansi");
		// combo.add("Ferdl");
		// combo.add("Seppi");
		// combo.add("Peter");
		// combo.add("Korl");
		// combo.select(1);
		//		

		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// sSelection = combo.getText();
				iSelection = combo.getSelectionIndex();
				YAxisSelectorEvent yAxisSelectorEvent = new YAxisSelectorEvent();
				yAxisSelectorEvent.setSender(this);
				yAxisSelectorEvent.setSelectedAxis(iSelection);
				GeneralManager.get().getEventPublisher().triggerEvent(
						yAxisSelectorEvent);
			}
		});

		initAxisComboListener = new InitYAxisComboListener();
		initAxisComboListener.setHandler(this);
		GeneralManager.get().getEventPublisher().addListener(
				InitAxisComboEvent.class, initAxisComboListener);

		return composite;
	}

	@Override
	public void queueEvent(
			final AEventListener<? extends IListenerOwner> listener,
			final AEvent event) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				listener.handleEvent(event);
			}
		});

	}

	/**
	 * Inits the Combobox with values.
	 * 
	 * @param sItems
	 *            An Array Of Strings representing the items in the combobox.
	 */
	public void initComboString(String[] sItems) {

		// combo.setItems(sItems); Doesnt work ? strange;

		for (String tmp : sItems) {
			combo.add(tmp);

		}
		if (sItems != null && sItems.length > 1)
			combo.select(1);

	}

	@Override
	public void dispose() {
		// Unregister event listener
		if (initAxisComboListener != null) {
			GeneralManager.get().getEventPublisher().removeListener(
					initAxisComboListener);
			initAxisComboListener = null;
		}
	}
}
