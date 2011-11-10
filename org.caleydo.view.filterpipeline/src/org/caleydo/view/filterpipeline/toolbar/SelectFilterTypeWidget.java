package org.caleydo.view.filterpipeline.toolbar;

import org.caleydo.core.event.view.filterpipeline.SetFilterTypeEvent;
import org.caleydo.core.event.view.filterpipeline.SetFilterTypeEvent.FilterType;
import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.manager.GeneralManager;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class SelectFilterTypeWidget extends ControlContribution implements IToolBarItem {

	private int targetViewId;

	/**
	 * 
	 */
	protected SelectFilterTypeWidget(int targetViewId) {
		super("SelectFilterTypeWidget" + targetViewId);
		this.targetViewId = targetViewId;
	}

	@Override
	protected Control createControl(Composite parent) {

		final Combo selectType = new Combo(parent, SWT.NONE | SWT.READ_ONLY
				| SWT.DROP_DOWN);

		String items[] = new String[] { "Genes", "Experiments" };
		selectType.setItems(items);
		selectType.select(0);

		selectType.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String type = selectType.getItem(selectType.getSelectionIndex());

				FilterType filterType = null;

				if (type == "Genes")
					filterType = FilterType.RECORD;
				else if (type == "Experiments")
					filterType = FilterType.DIMENSION;

				GeneralManager.get().getEventPublisher()
						.triggerEvent(new SetFilterTypeEvent(filterType, targetViewId));
			}
		});

		return selectType;
	}
}
