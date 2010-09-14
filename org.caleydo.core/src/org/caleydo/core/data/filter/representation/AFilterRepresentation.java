package org.caleydo.core.data.filter.representation;

import org.caleydo.core.data.filter.Filter;
import org.caleydo.core.data.virtualarray.delta.VirtualArrayDelta;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public abstract class AFilterRepresentation<DeltaType extends VirtualArrayDelta<?>, FilterType extends Filter<DeltaType>> {

	protected Composite parentComposite;

	protected FilterType filter;

	public void create() {

		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				parentComposite = new Shell();
				parentComposite.setLayout(new RowLayout(SWT.VERTICAL));
				final Button ok = new Button(parentComposite, SWT.PUSH);
				ok.setText("Apply");
				Button cancel = new Button(parentComposite, SWT.PUSH);
				cancel.setText("Cancel");
				Listener listener = new Listener() {
					public void handleEvent(Event event) {
						if (event.widget != ok) {
							triggerRemoveFilterEvent();
						}
						((Shell) parentComposite).close();
					}
				};
				ok.addListener(SWT.Selection, listener);
				cancel.addListener(SWT.Selection, listener);
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
