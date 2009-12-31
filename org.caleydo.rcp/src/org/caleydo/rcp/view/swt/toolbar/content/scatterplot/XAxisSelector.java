package org.caleydo.rcp.view.swt.toolbar.content.scatterplot;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.view.storagebased.InitAxisComboEvent;
import org.caleydo.core.manager.event.view.storagebased.XAxisSelectorEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.rcp.view.swt.toolbar.content.IToolBarItem;
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
 * @author TODO
 */
public class XAxisSelector
	extends ControlContribution
	implements IToolBarItem, IListenerOwner {

	private InitXAxisComboListener initAxisComboListener;
	private Combo combo;
	private int iSelection;

	public XAxisSelector(String str, int iSliderSelection) {
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
		// combo.select(0);

		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// sSelection = combo.getText();
				iSelection = combo.getSelectionIndex();
				XAxisSelectorEvent xAxisSelectorEvent = new XAxisSelectorEvent();
				xAxisSelectorEvent.setSender(this);
				xAxisSelectorEvent.setSelectedAxis(iSelection);
				GeneralManager.get().getEventPublisher().triggerEvent(xAxisSelectorEvent);
			}
		});

		initAxisComboListener = new InitXAxisComboListener();
		initAxisComboListener.setHandler(this);
		GeneralManager.get().getEventPublisher().addListener(InitAxisComboEvent.class, initAxisComboListener);

		return composite;
	}

	@Override
	public void queueEvent(final AEventListener<? extends IListenerOwner> listener, final AEvent event) {
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
		if (sItems != null)
			combo.select(0);

	}

	@Override
	public void dispose() {
		// Unregister event listener
		if (initAxisComboListener != null) {
			GeneralManager.get().getEventPublisher().removeListener(initAxisComboListener);
			initAxisComboListener = null;
		}
	}
}
