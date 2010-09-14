package org.caleydo.core.data.filter.representation;

import org.caleydo.core.data.filter.Filter;
import org.caleydo.core.data.virtualarray.IVAType;
import org.caleydo.core.data.virtualarray.delta.VirtualArrayDelta;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

public abstract class AFilterRepresentation<VAType extends IVAType, DeltaType extends VirtualArrayDelta<?, VAType>, FilterType extends Filter<VAType, DeltaType>> {

	protected Composite parentComposite;

	protected FilterType filter;

	public void init() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				parentComposite = new Shell();

				parentComposite.setLayout(new RowLayout());
				final Button ok = new Button(parentComposite, SWT.PUSH);
				ok.setText("Apply");
				Button cancel = new Button(parentComposite, SWT.PUSH);
				cancel.setText("Cancel");
				Listener listener = new Listener() {
					public void handleEvent(Event event) {
						// result[0] = event.widget == ok;
						((Shell) parentComposite).close();
					}
				};
				ok.addListener(SWT.Selection, listener);
				cancel.addListener(SWT.Selection, listener);
			}
		});
	}

	public void open() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				((Shell) parentComposite).open();
			}
		});
	}
	
	public void setFilter(FilterType filter) {
		this.filter = filter;
	}

	protected abstract void createVADelta();
}
