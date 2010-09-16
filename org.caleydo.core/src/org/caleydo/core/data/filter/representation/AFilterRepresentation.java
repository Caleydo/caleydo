package org.caleydo.core.data.filter.representation;

import org.caleydo.core.data.filter.Filter;
import org.caleydo.core.data.virtualarray.delta.VirtualArrayDelta;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

public abstract class AFilterRepresentation<DeltaType extends VirtualArrayDelta<?>, FilterType extends Filter<DeltaType>> {

	protected Composite parentComposite;

	protected FilterType filter;

	public void create() {

		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				parentComposite = new Shell();
				parentComposite.setLayout(new GridLayout(1, false));
			}
		});
	}

	protected void addOKCancel() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				Composite composite = new Composite(parentComposite, SWT.NONE);
				composite.setLayout(new GridLayout(3, false));
				GridData gridData = new GridData();
				gridData.grabExcessHorizontalSpace = true;
				gridData.horizontalAlignment = GridData.FILL;
				composite.setLayoutData(gridData);

				Label dummy = new Label(composite, SWT.NONE);
				dummy.setLayoutData(gridData);
				
				final Button okButton = new Button(composite, SWT.PUSH);
				okButton.setText("  OK  ");
				Button cancelButton = new Button(composite, SWT.PUSH);
				cancelButton.setText("Cancel");
				Listener listener = new Listener() {
					public void handleEvent(Event event) {
						if (event.widget != okButton) {
							triggerRemoveFilterEvent();
						}
						((Shell) parentComposite).close();
					}
				};
				okButton.addListener(SWT.Selection, listener);
				cancelButton.addListener(SWT.Selection, listener);
				
				Monitor primary = parentComposite.getDisplay().getPrimaryMonitor();
				Rectangle bounds = primary.getBounds();
				Rectangle rect = parentComposite.getBounds();
				int x = bounds.x + (bounds.width - rect.width) / 2;
				int y = bounds.y + (bounds.height - rect.height) / 2;
				parentComposite.setLocation(x, y);

				parentComposite.pack();

				((Shell) parentComposite).open();
			}
		});
	}

	public void setFilter(FilterType filter) {
		this.filter = filter;
	}

	protected abstract void createVADelta();

	protected abstract void triggerRemoveFilterEvent();
}
