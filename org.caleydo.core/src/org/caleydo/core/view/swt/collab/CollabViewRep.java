package org.caleydo.core.view.swt.collab;

import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.manager.event.EMediatorType;
import org.caleydo.core.manager.event.IEventContainer;
import org.caleydo.core.manager.event.IMediatorReceiver;
import org.caleydo.core.manager.event.IMediatorSender;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.swt.ASWTView;
import org.caleydo.core.view.swt.ISWTView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * 
 * @author Werner Puff
 */
public class CollabViewRep
	extends ASWTView
	implements IView, ISWTView, IMediatorReceiver, IMediatorSender {

	/** main swt-composite of this view */
	private Composite composite;

	/**
	 * Constructor.
	 * @param iParentContainerId
	 * @param sLabel 
	 */
	public CollabViewRep(final int iParentContainerId, final String sLabel) {
		super(iParentContainerId, sLabel, GeneralManager.get().getIDManager().createID(
			EManagedObjectType.VIEW_SWT_TABULAR_DATA_VIEWER));

		// GeneralManager.get().getEventPublisher().addSender(EMediatorType.SELECTION_MEDIATOR, this);
		// GeneralManager.get().getEventPublisher().addReceiver(EMediatorType.SELECTION_MEDIATOR, this);

	}

	@Override
	public void initViewSWTComposite(Composite parentComposite) {
		
		Composite composite = new Composite(parentComposite, SWT.NULL);
		GridLayout layout = new GridLayout(2, false);
		composite.setLayout(layout);
		
		addSerializeControls(composite);
		addEmptyComposite(composite);
		addBucketControls(composite);
		addEmptyComposite(composite);
	}
		
	public void addSerializeControls(Composite composite) {
		Composite serializeComposite = new Composite(composite, SWT.LEFT | SWT.TOP);
		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.BEGINNING;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.minimumWidth = 600;
		gridData.minimumHeight = 300;
		serializeComposite.setLayoutData(gridData);

		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = layout.marginHeight = layout.horizontalSpacing = 0;
		serializeComposite.setLayout(layout);

		Text text = new Text(serializeComposite, SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		text.setTextLimit(10000);
	    GridData data = new GridData(GridData.FILL_BOTH);
		// data.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
	    data.horizontalSpan = 2;
	    data.verticalSpan = 10;
	    text.setLayoutData(data);

		Button button = new Button(serializeComposite, SWT.CENTER);
		button.setText("test");
		Listener testListener = new Listener() {
			public void handleEvent(Event event) {
				System.out.println("test button pressed");
			}
		};
		button.addListener(SWT.Selection, testListener);
	}
	
	public void addBucketControls(Composite composite) {
		Composite bucketControls = new Composite(composite, SWT.NULL);
		GridLayout bucketLayout = new GridLayout(3, false);
		bucketControls.setLayout(bucketLayout);

		Button button;
		Label label;
		label = new Label(bucketControls, SWT.LEFT);
		label.setText("");
		button = new Button(bucketControls, SWT.LEFT);
		button.setText("top");
		label = new Label(bucketControls, SWT.LEFT);
		label.setText("");
		button = new Button(bucketControls, SWT.LEFT);
		button.setText("left");
		button = new Button(bucketControls, SWT.LEFT);
		button.setText("center");
		button = new Button(bucketControls, SWT.LEFT);
		button.setText("right");
		label = new Label(bucketControls, SWT.LEFT);
		label.setText("");
		button = new Button(bucketControls, SWT.LEFT);
		button.setText("bottom");
		label = new Label(bucketControls, SWT.LEFT);
		label.setText("");
	}

	public void addEmptyComposite(Composite composite) {
		Composite empty = new Composite(composite, SWT.NULL);
		GridLayout bucketLayout = new GridLayout(1, false);
		empty.setLayout(bucketLayout);
		Label label = new Label(empty, SWT.NULL);
		label.setText("empty");
	}
	
	@Override
	public void drawView() {

	}

	@Override
	public void handleExternalEvent(IUniqueObject eventTrigger, IEventContainer eventContainer,
		EMediatorType eMediatorType) {
		switch (eventContainer.getEventType()) {

		}
	}

	@Override
	public void triggerEvent(EMediatorType eMediatorType, IEventContainer eventContainer) {
		generalManager.getEventPublisher().triggerEvent(eMediatorType, this, eventContainer);

	}

}
