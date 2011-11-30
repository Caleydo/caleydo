package org.caleydo.view.datagraph.toolbar;

import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.view.datagraph.GLDataGraph;
import org.caleydo.view.datagraph.event.ShowDataConnectionsEvent;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * Class that specifies toolbar items that are not buttons for
 * {@link GLDataGraph}.
 * 
 * @author Christian
 * 
 */
public class ToolBarWidgets
	extends ControlContribution
	implements IToolBarItem
{

	protected ToolBarWidgets(String id)
	{
		super(id);

	}

	@Override
	protected Control createControl(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NULL);
		RowLayout layout = new RowLayout();
		// layout.marginHeight = layout.marginWidth = layout.horizontalSpacing =
		// 0;
		composite.setLayout(layout);

		final Button showDataConnectionsCheckBox = new Button(composite, SWT.CHECK);
		showDataConnectionsCheckBox.setSelection(true);
		showDataConnectionsCheckBox.setText("Show Data Connections");

		Listener listener = new Listener()
		{
			@Override
			public void handleEvent(Event event)
			{
				GeneralManager
						.get()
						.getEventPublisher()
						.triggerEvent(
								new ShowDataConnectionsEvent(showDataConnectionsCheckBox
										.getSelection()));
			}

		};

		showDataConnectionsCheckBox.addListener(SWT.Selection, listener);

		return composite;
	}

}
