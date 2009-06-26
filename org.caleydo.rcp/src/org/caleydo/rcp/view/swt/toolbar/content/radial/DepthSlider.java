package org.caleydo.rcp.view.swt.toolbar.content.radial;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.view.radial.SetMaxDisplayedHierarchyDepthEvent;
import org.caleydo.core.manager.event.view.radial.UpdateDepthSliderPositionEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.rcp.view.swt.toolbar.content.IToolBarItem;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.ui.PlatformUI;

/**
 * Toolbar item that contains a slider for specifying the maximum displayed hierarchy depth of
 * RadialHierarchy.
 * 
 * @author Christian Partl
 */
public class DepthSlider
	extends ControlContribution
	implements IToolBarItem, IListenerOwner {

	private Listener listener;
	private UpdateDepthSliderPositionListener updateSliderPositionListener;
	private Slider slider;
	private int iSelection;

	public DepthSlider(String str, int iSliderSelection) {
		super(str);
		iSelection = iSliderSelection;
	}

	@Override
	protected Control createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = layout.marginWidth = layout.horizontalSpacing = 0;
		composite.setLayout(layout);

		slider = new Slider(composite, SWT.HORIZONTAL);
		slider.setValues(iSelection, 2, 20, 1, 1, 1);
		slider.setLayoutData(new GridData(130, 20));

		listener = new Listener() {
			public void handleEvent(Event event) {
				SetMaxDisplayedHierarchyDepthEvent setMaxDisplayedHierarchyDepthEvent =
					new SetMaxDisplayedHierarchyDepthEvent();
				setMaxDisplayedHierarchyDepthEvent.setSender(this);
				setMaxDisplayedHierarchyDepthEvent.setMaxDisplayedHierarchyDepth(slider.getSelection());
				GeneralManager.get().getEventPublisher().triggerEvent(setMaxDisplayedHierarchyDepthEvent);
			}

		};
		iSelection = slider.getSelection();
		slider.addListener(SWT.Selection, listener);

		updateSliderPositionListener = new UpdateDepthSliderPositionListener();
		updateSliderPositionListener.setHandler(this);
		// TODO: Unregister listeners, but where?
		GeneralManager.get().getEventPublisher().addListener(UpdateDepthSliderPositionEvent.class,
			updateSliderPositionListener);

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
	 * Sets the slider position (selection) to a cartain value.
	 * 
	 * @param iPosition
	 *            The value the slider position (selection) is set to.
	 */
	public void setSliderPosition(int iPosition) {
		iSelection = iPosition;
		slider.setSelection(iPosition);
	}

	@Override
	public void dispose() {
		//Unregister event listener
		if (updateSliderPositionListener != null) {
			GeneralManager.get().getEventPublisher().removeListener(updateSliderPositionListener);
			updateSliderPositionListener = null;
		}
	}
}
