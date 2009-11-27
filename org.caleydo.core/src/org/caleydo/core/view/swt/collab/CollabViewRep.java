package org.caleydo.core.view.swt.collab;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.EventPublisher;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.net.Connection;
import org.caleydo.core.net.ENetworkStatus;
import org.caleydo.core.net.EventFilterBridge;
import org.caleydo.core.net.IGroupwareManager;
import org.caleydo.core.net.NetworkManager;
import org.caleydo.core.net.config.ConfigureableEventBridge;
import org.caleydo.core.net.config.IConfigureableEventList;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.serialize.SerializedDummyView;
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
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Werner Puff
 */
public class CollabViewRep
	extends ASWTView
	implements IView, ISWTView, IListenerOwner {

	/** data-name to store the related {@link EventFilterBridge} to a TreeItem */
	public static final String ITEM_DATA_BRIDGE = "bridge";

	/** data-name to store the related {@link EventPublisher} to a TreeItem */
	public static final String ITEM_DATA_PUBLISHER = "publisher";

	/** data-name to store the related event-class to a checkbox-button */
	public static final String ITEM_DATA_EVENT_TYPE = "eventType";

	/** main swt-composite of this view */
	private Composite baseComposite;

	private NetworkManager networkManager;

	private Text serializationTextField;

	private TestSerializationListener testSerializationListener;
	private TestSendNetworkMessageListener testSendNetworkMessageListener;

	private RedrawCollabViewListener redrawCollabViewListener;

	private Composite dataControls;

	/**
	 * Constructor.
	 * 
	 * @param iParentContainerId
	 * @param sLabel
	 */
	public CollabViewRep(final int iParentContainerId, final String sLabel) {
		super(iParentContainerId, sLabel, GeneralManager.get().getIDManager().createID(
			EManagedObjectType.VIEW_SWT_TABULAR_DATA_VIEWER));
	}

	@Override
	public void initViewSWTComposite(Composite parentComposite) {
		this.parentComposite = parentComposite;
		baseComposite = null;
		IGroupwareManager groupwareManager = GeneralManager.get().getGroupwareManager();
		if (groupwareManager != null) {
			networkManager = groupwareManager.getNetworkManager();
		}
	}

	@Override
	public void drawView() {
		if (baseComposite != null) {
			baseComposite.dispose();
		}

		baseComposite = new Composite(parentComposite, SWT.NULL);
		GridLayout layout = new GridLayout(2, false);
		baseComposite.setLayout(layout);

		addSerializeControls(baseComposite);
		addTestControls(baseComposite);
		addTreeComposite(baseComposite);
		addDataControls(baseComposite);
		// addEmptyComposite(composite);

		parentComposite.redraw();
		parentComposite.layout(true);
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();

		testSerializationListener = new TestSerializationListener();
		testSerializationListener.setHandler(this);
		eventPublisher.addListener(TestSerializationEvent.class, testSerializationListener);

		redrawCollabViewListener = new RedrawCollabViewListener();
		redrawCollabViewListener.setHandler(this);
		eventPublisher.addListener(RedrawCollabViewEvent.class, redrawCollabViewListener);

	}

	@Override
	public void unregisterEventListeners() {
		if (testSerializationListener != null) {
			eventPublisher.removeListener(testSerializationListener);
			testSerializationListener = null;
		}
		if (redrawCollabViewListener != null) {
			eventPublisher.removeListener(redrawCollabViewListener);
			redrawCollabViewListener = null;
		}
	}

	public void addSerializeControls(Composite composite) {
		Composite serializeComposite = new Composite(composite, SWT.NULL);
		serializeComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		GridLayout layout = new GridLayout(3, false);
		layout.marginWidth = layout.marginHeight = layout.horizontalSpacing = 0;
		serializeComposite.setLayout(layout);

		serializationTextField = new Text(serializeComposite, SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		serializationTextField.setTextLimit(10000);
		GridData data = new GridData(GridData.FILL_BOTH);
		// data.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
		data.horizontalSpan = 3;
		data.verticalSpan = 10;
		serializationTextField.setLayoutData(data);

		Button button = new Button(serializeComposite, SWT.CENTER);
		button.setText("send event");
		testSendNetworkMessageListener = new TestSendNetworkMessageListener();
		testSendNetworkMessageListener.setMessageField(serializationTextField);
		button.addSelectionListener(testSendNetworkMessageListener);
	}

	public void addTestControls(Composite composite) {
		Composite testControls = new Composite(composite, SWT.NULL);
		testControls.setLayoutData(new GridData(GridData.FILL_BOTH));

		GridLayout testLayout = new GridLayout(2, false);
		testControls.setLayout(testLayout);

		Button button;
		Label label;

		button = new Button(testControls, SWT.CENTER);
		button.setText("start server");
		StartServerListener startServerListener = new StartServerListener();
		button.addListener(SWT.Selection, startServerListener);
		ENetworkStatus status = ENetworkStatus.STATUS_STOPPED;
		if (networkManager != null) {
			status = networkManager.getStatus();
		}
		switch (status) {
			case STATUS_STOPPED:
			case STATUS_STARTED:
				label = new Label(testControls, SWT.LEFT);
				label.setText("Use this caledyo application as a server");
				break;
			case STATUS_SERVER:
				button.setEnabled(false);
				label = new Label(testControls, SWT.LEFT);
				label.setText("Server started");
				break;
			case STATUS_CLIENT:
				button.setEnabled(false);
				label = new Label(testControls, SWT.LEFT);
				label.setText("Already connected as a client");
				break;
			default:
				button.setEnabled(false);
				label = new Label(testControls, SWT.LEFT);
				label.setText("unknown network status");
		}

		button = new Button(testControls, SWT.CENTER);
		button.setText("start deskotheque server");
		StartDeskothequeServerListener startDeskothequeServerListener = new StartDeskothequeServerListener();
		button.addListener(SWT.Selection, startDeskothequeServerListener);
		status = ENetworkStatus.STATUS_STOPPED;
		if (networkManager != null) {
			status = networkManager.getStatus();
		}
		switch (status) {
			case STATUS_STOPPED:
			case STATUS_STARTED:
				label = new Label(testControls, SWT.LEFT);
				label.setText("Use this caledyo application as a deskotheque server");
				break;
			case STATUS_SERVER:
				button.setEnabled(false);
				label = new Label(testControls, SWT.LEFT);
				label.setText("Server started");
				break;
			case STATUS_CLIENT:
				button.setEnabled(false);
				label = new Label(testControls, SWT.LEFT);
				label.setText("Already connected as a client");
				break;
			default:
				button.setEnabled(false);
				label = new Label(testControls, SWT.LEFT);
				label.setText("unknown network status");
		}

		button = new Button(testControls, SWT.NULL);
		button.setText("connect to visdaemon");
		StartVisLinksListener startVisLinksListener = new StartVisLinksListener();
		startVisLinksListener.setRequester(this);
		button.addListener(SWT.Selection, startVisLinksListener);

		button = new Button(testControls, SWT.NULL);
		button.setText("disconnect from visdaemon");
		StopVisLinksListener stopVisLinksListener = new StopVisLinksListener();
		stopVisLinksListener.setRequester(this);
		button.addListener(SWT.Selection, stopVisLinksListener);

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

		button = new Button(testControls, SWT.CENTER);
		button.setText("add pathway");
		TestButtonListener pathwayListener = new TestButtonListener();
		pathwayListener.setRequester(this);
		button.addListener(SWT.Selection, pathwayListener);
		label = new Label(testControls, SWT.LEFT);
		label.setText("");

		org.eclipse.swt.widgets.List viewList = new org.eclipse.swt.widgets.List(testControls, SWT.LEFT);
		IViewManager vm = GeneralManager.get().getViewGLCanvasManager();
		Collection<AGLEventListener> views = vm.getAllGLEventListeners();
		int[] viewIds = new int[views.size()];
		int viewIndex = 0;
		for (AGLEventListener view : views) {
			viewIds[viewIndex++] = view.getID();
			viewList.add(viewIndex + " - " + view.getClass().getCanonicalName());
		}
		Text target = new Text(testControls, SWT.LEFT);
		target.setText("Client-1");

		new Composite(testControls, SWT.NULL);
	}

	public void addTreeComposite(Composite composite) {
		Composite eventControls = new Composite(composite, SWT.NULL);
		eventControls.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout eventLayout = new GridLayout(1, false);
		eventControls.setLayout(eventLayout);

		final Tree tree = new Tree(eventControls, SWT.VIRTUAL | SWT.BORDER);
		GridData data = new GridData(GridData.FILL_BOTH);
		tree.setLayoutData(data);

		Listener itemListener = new Listener() {
			public void handleEvent(Event event) {
				System.out.println("itemListener.handleEvent()");
				removeDataControls();
				if (event.item instanceof TreeItem) {
					drawDataControls((TreeItem) event.item);
				}
			}
		};
		tree.addListener(SWT.Selection, itemListener);
		createEventBridgeTree(tree, generalManager.getGroupwareManager());
	}

	public void createEventBridgeTree(Tree tree, IGroupwareManager groupwareManager) {
		if (groupwareManager != null) {
			networkManager = groupwareManager.getNetworkManager();
			if (networkManager.getGlobalOutgoingPublisher() != null) {
				EventFilterBridge outgoingBridge = networkManager.getOutgoingEventBridge();
				TreeItem outgoingRoot = new TreeItem(tree, SWT.NULL);
				String bridgeName = outgoingBridge.getName();
				bridgeName += "(local=" + outgoingBridge.isBridgeLocalEvents();
				bridgeName += ", remote=" + outgoingBridge.isBridgeRemoteEvents() + ")";
				outgoingRoot.setText(bridgeName);
				outgoingRoot.setData(ITEM_DATA_BRIDGE, outgoingBridge);
				outgoingRoot.setData(ITEM_DATA_PUBLISHER, networkManager.getCentralEventPublisher());

				EventFilterBridge incomingBridge = networkManager.getIncomingEventBridge();
				TreeItem incomingRoot = new TreeItem(tree, SWT.NULL);
				bridgeName = incomingBridge.getName();
				bridgeName += "(local=" + incomingBridge.isBridgeLocalEvents();
				bridgeName += ", remote=" + incomingBridge.isBridgeRemoteEvents() + ")";
				incomingRoot.setText(bridgeName);
				incomingRoot.setData(ITEM_DATA_BRIDGE, incomingBridge);
				incomingRoot.setData(ITEM_DATA_PUBLISHER, networkManager.getGlobalIncomingPublisher());

				EventPublisher globalNetworkPublisher = networkManager.getGlobalOutgoingPublisher();
				List<Connection> connections = networkManager.getConnections();
				for (Connection connection : connections) {
					TreeItem item = new TreeItem(outgoingRoot, SWT.NULL);
					item.setText(connection.getOutgoingBridge().getName());
					item.setData(ITEM_DATA_BRIDGE, connection.getOutgoingBridge());
					item.setData(ITEM_DATA_PUBLISHER, globalNetworkPublisher);

					item = new TreeItem(incomingRoot, SWT.NULL);
					item.setText(connection.getIncomingBridge().getName());
					item.setData(ITEM_DATA_BRIDGE, connection.getIncomingBridge());
					item.setData(ITEM_DATA_PUBLISHER, connection.getIncomingPublisher());
				}

			}
		}
	}

	public void drawDataControls(TreeItem item) {
		dataControls = new Composite(baseComposite, SWT.NULL);
		dataControls.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout dataLayout = new GridLayout(1, false);
		dataControls.setLayout(dataLayout);

		Label dataLabel = new Label(dataControls, SWT.NULL);
		dataLabel.setText(item.getText());

		EventFilterBridge bridge = (EventFilterBridge) item.getData(ITEM_DATA_BRIDGE);
		EventPublisher publisher = (EventPublisher) item.getData(ITEM_DATA_PUBLISHER);

		List<Button> buttonList = new ArrayList<Button>();

		IConfigureableEventList config = new ConfigureableEventBridge(bridge, publisher);
		Collection<Class<? extends AEvent>> selected = config.getSelectedEventTypes();
		for (Class<? extends AEvent> eventType : config.getAllEventTypes()) {
			Button button = new Button(dataControls, SWT.CHECK);
			button.setText(eventType.getSimpleName());
			button.setData(ITEM_DATA_EVENT_TYPE, eventType);
			if (selected.contains(eventType)) {
				button.setSelection(true);
			}
			buttonList.add(button);
		}

		Button saveButton = new Button(dataControls, SWT.NULL);
		saveButton.setText("apply");
		SaveEventBridgeConfigurationListener saveListener = new SaveEventBridgeConfigurationListener();
		saveListener.setBridge(bridge);
		saveListener.setPublisher(publisher);
		saveListener.setEventButtonList(buttonList);
		saveButton.addSelectionListener(saveListener);

		baseComposite.layout();
		baseComposite.redraw();
	}

	public void removeDataControls() {
		if (dataControls != null) {
			dataControls.dispose();
			dataControls = null;
		}
	}

	public void addDataControls(Composite composite) {
		dataControls = new Composite(composite, SWT.NULL);
		dataControls.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout dataLayout = new GridLayout(1, false);
		dataControls.setLayout(dataLayout);

		Label dataLabel = new Label(dataControls, SWT.NULL);
		dataLabel.setText("empty");
	}

	public void addEmptyComposite(Composite composite) {
		Composite empty = new Composite(composite, SWT.NULL);
		empty.setLayoutData(new GridData(GridData.FILL_BOTH));

		GridLayout bucketLayout = new GridLayout(1, false);
		empty.setLayout(bucketLayout);

		Label label = new Label(empty, SWT.NULL);
		label.setText("empty");
	}

	public void setSerializationText(String text) {
		serializationTextField.setText(text);
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedDummyView serializedForm = new SerializedDummyView(EDataDomain.UNSPECIFIED);
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public void initFromSerializableRepresentation(ASerializedView ser) {
		// nothing to initialize
	}

	public void dispose() {
		System.out.println("CollabViewRep.dispose()");
		unregisterEventListeners();
	}

	/*
	 * public void addBucketControls(Composite composite) { Composite bucketControls = new
	 * Composite(composite, SWT.NULL); bucketControls.setLayoutData(new GridData(GridData.FILL_BOTH));
	 * GridLayout bucketLayout = new GridLayout(3, false); bucketControls.setLayout(bucketLayout); Button
	 * button; Label label; label = new Label(bucketControls, SWT.LEFT); label.setText(""); button = new
	 * Button(bucketControls, SWT.LEFT); button.setText("top"); label = new Label(bucketControls, SWT.LEFT);
	 * label.setText(""); button = new Button(bucketControls, SWT.LEFT); button.setText("left"); button = new
	 * Button(bucketControls, SWT.LEFT); button.setText("center"); button = new Button(bucketControls,
	 * SWT.LEFT); button.setText("right"); label = new Label(bucketControls, SWT.LEFT); label.setText("");
	 * button = new Button(bucketControls, SWT.LEFT); button.setText("bottom"); label = new
	 * Label(bucketControls, SWT.LEFT); label.setText(""); }
	 */
}
