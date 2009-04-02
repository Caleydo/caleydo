package org.caleydo.core.view.swt.collab;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.manager.event.EMediatorType;
import org.caleydo.core.manager.event.IEventContainer;
import org.caleydo.core.manager.event.IMediatorReceiver;
import org.caleydo.core.manager.event.IMediatorSender;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.swt.ASWTView;
import org.caleydo.core.view.swt.ISWTView;
import org.eclipse.swt.SWT;
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

	/** utility class for logging */
	private Logger log = Logger.getLogger(CollabViewRep.class.getName());
	
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
		
		composite = new Composite(parentComposite, SWT.NULL);
		GridLayout layout = new GridLayout(2, false);
		composite.setLayout(layout);
		
		addSerializeControls(composite);
		addTestControls(composite);
		addBucketControls(composite);
		addEmptyComposite(composite);

	}
		
	public void addSerializeControls(Composite composite) {
		Composite serializeComposite = new Composite(composite, SWT.NULL);
		serializeComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		GridLayout layout = new GridLayout(3, false);
		layout.marginWidth = layout.marginHeight = layout.horizontalSpacing = 0;
		serializeComposite.setLayout(layout);

		Text text = new Text(serializeComposite, SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		text.setTextLimit(10000);
	    GridData data = new GridData(GridData.FILL_BOTH);
		// data.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
	    data.horizontalSpan = 3;
	    data.verticalSpan = 10;
	    text.setLayoutData(data);

	    Button button;
	    Listener testListener;

	    button = new Button(serializeComposite, SWT.CENTER);
		button.setText("test");
		testListener = new Listener() {
			public void handleEvent(Event event) {
				log.log(Level.INFO, "test button pressed");
			}
		};
		button.addListener(SWT.Selection, testListener);
	}
	
	public void addBucketControls(Composite composite) {
		Composite bucketControls = new Composite(composite, SWT.NULL);
		bucketControls.setLayoutData(new GridData(GridData.FILL_BOTH));
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

	public void addTestControls(Composite composite) {
		Composite testControls = new Composite(composite, SWT.NULL);
		testControls.setLayoutData(new GridData(GridData.FILL_BOTH));

		GridLayout testLayout = new GridLayout(2, false);
		testControls.setLayout(testLayout);

		Button button;
	    button = new Button(testControls, SWT.NULL);
		button.setText("enable busy");
		EnableBusyListener enableListener = new EnableBusyListener();
		enableListener.setRequester(this);
		button.addListener(SWT.Selection, enableListener);

		button = new Button(testControls, SWT.CENTER);
		button.setText("disable busy");
		DisableBusyListener disableListener = new DisableBusyListener();
		disableListener.setRequester(this);
		button.addListener(SWT.Selection, disableListener);
	}
	
	public void addEmptyComposite(Composite composite) {
		Composite empty = new Composite(composite, SWT.NULL);
		empty.setLayoutData(new GridData(GridData.FILL_BOTH));

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
