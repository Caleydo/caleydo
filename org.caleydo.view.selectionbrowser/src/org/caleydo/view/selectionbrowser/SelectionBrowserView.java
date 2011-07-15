package org.caleydo.view.selectionbrowser;

import java.util.ArrayList;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.mapping.IDCategory;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.SelectionTypeEvent;
import org.caleydo.core.data.selection.StorageSelectionManager;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.delta.ContentVADelta;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.datadomain.IDataDomainBasedView;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.EventPublisher;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.view.ClearSelectionsEvent;
import org.caleydo.core.manager.event.view.SelectionCommandEvent;
import org.caleydo.core.manager.event.view.storagebased.RedrawViewEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.event.view.storagebased.VirtualArrayUpdateEvent;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.canvas.listener.ClearSelectionsListener;
import org.caleydo.core.view.opengl.canvas.listener.ContentVAUpdateListener;
import org.caleydo.core.view.opengl.canvas.listener.IContentVAUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.RedrawViewListener;
import org.caleydo.core.view.opengl.canvas.listener.SelectionCommandListener;
import org.caleydo.core.view.opengl.canvas.listener.SelectionUpdateListener;
import org.caleydo.core.view.swt.ASWTView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;

/**
 * Selection browser that is located in the side-bar.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class SelectionBrowserView extends ASWTView implements
		IDataDomainBasedView<ASetBasedDataDomain>, ISelectionUpdateHandler,
		IContentVAUpdateHandler, ISelectionCommandHandler, IViewCommandHandler {

	private final static String SELECTION_TYPE_NAME_1 = "Selected by group 1";
	private final static String SELECTION_TYPE_NAME_2 = "Selected by group 2";
	private final static String SELECTION_TYPE_NAME_3 = "Selected by group 3";
	private final static String SELECTION_TYPE_NAME_4 = "Selected by group 4";
	
	/** Colors taken from color brewer qualitative "Set 1" with 8 colors */
	private final static float[] SELECTION_COLOR_1 = new float[] { 152f/255, 78f/255, 163f/255, 1 };
	private final static float[] SELECTION_COLOR_2 = new float[] { 1, 127f/255, 0, 1 }; // yellow	
	private final static float[] SELECTION_COLOR_3 = new float[] { 247f/255, 129f/255, 191f/255, 1 };
//	private final static float[] SELECTION_COLOR_3 = new float[] { 1, 1, 51f/255, 1 };
	private final static float[] SELECTION_COLOR_4 = new float[] { 166f/255, 86f/255, 40f/255, 1 };
		
	ASetBasedDataDomain dataDomain;

	GeneralManager generalManager = null;
	EventPublisher eventPublisher = null;

	ContentSelectionManager contentSelectionManager;
	StorageSelectionManager storageSelectionManager;

	private Tree selectionTree;
	private TreeItem contentTree;

	private Label lblTest;
	private Button btnMerge;
	private Button btnSub;

	private SelectionUpdateListener selectionUpdateListener;
	private ContentVAUpdateListener virtualArrayUpdateListener;
	private SelectionCommandListener selectionCommandListener;

	private RedrawViewListener redrawViewListener;
	private ClearSelectionsListener clearSelectionsListener;

	/**
	 * Constructor.
	 */
	public SelectionBrowserView(Composite parentComposite) {

		super(-1, parentComposite);
		generalManager = GeneralManager.get();
		eventPublisher = generalManager.getEventPublisher();
		registerEventListeners();
	}

	private void initContent() {
		String contentVAType = ISet.CONTENT;
		contentSelectionManager = dataDomain.getContentSelectionManager();

		ContentVirtualArray contentVA = dataDomain.getContentVA(contentVAType);
		contentSelectionManager.setVA(contentVA);

		initSelectedByGroupSelectionTypes();
	}


	private void initSelectedByGroupSelectionTypes() {

		// Check if types have already been added
		for (SelectionType selectionType : contentSelectionManager.getSelectionTypes()) {
			if (selectionType.getType().equals(SELECTION_TYPE_NAME_1))
				return;
		}
		
		boolean isVisible = true;
		
		ArrayList<SelectionType> selectedByGroupSelectionTypes = new ArrayList<SelectionType>();

		selectedByGroupSelectionTypes.add(new SelectionType(SELECTION_TYPE_NAME_1,
				SELECTION_COLOR_1, 1, isVisible, true, 1));
		
		selectedByGroupSelectionTypes.add(new SelectionType(SELECTION_TYPE_NAME_2,
				SELECTION_COLOR_2, 1, isVisible, true, 1));
		
		selectedByGroupSelectionTypes.add(new SelectionType(SELECTION_TYPE_NAME_3,
				SELECTION_COLOR_3, 1, isVisible, true, 1));
		
		selectedByGroupSelectionTypes.add(new SelectionType(SELECTION_TYPE_NAME_4,
				SELECTION_COLOR_4, 1, isVisible, true, 1));

		for (SelectionType selectionType : selectedByGroupSelectionTypes) {
			
			selectionType.setManaged(true);
			SelectionTypeEvent selectionTypeEvent = new SelectionTypeEvent(selectionType);
			eventPublisher.triggerEvent(selectionTypeEvent);
		}
		
//		SelectionTypeEvent selectionTypeEvent = new SelectionTypeEvent(
//				selectedByGroupSelectionTypes.get(0));
//		selectionTypeEvent.setCurrent(true);
//		GeneralManager.get().getEventPublisher().triggerEvent(selectionTypeEvent);
	}
	
	@Override
	public void draw() {

		selectionTree = new Tree(parentComposite, SWT.NULL | SWT.MULTI);

		Label sepLabel = new Label(parentComposite, SWT.SEPARATOR | SWT.HORIZONTAL);

		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.minimumHeight = 8;
		gridData.verticalAlignment = SWT.CENTER;
		sepLabel.setLayoutData(gridData);

		btnMerge = new Button(parentComposite, SWT.WRAP);
		btnMerge.setAlignment(SWT.CENTER);
		btnMerge.setText("Merge Selection(s)");
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.verticalAlignment = SWT.CENTER;
		gridData.minimumWidth = 120;
		btnMerge.setLayoutData(gridData);

		btnSub = new Button(parentComposite, SWT.WRAP);
		btnSub.setAlignment(SWT.CENTER);
		btnSub.setText("Delete Selection(s)");
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.verticalAlignment = SWT.CENTER;
		gridData.minimumWidth = 120;
		btnSub.setLayoutData(gridData);

		sepLabel = new Label(parentComposite, SWT.SEPARATOR | SWT.HORIZONTAL);

		gridData = new GridData(GridData.FILL_BOTH);
		gridData.minimumHeight = 10;
		sepLabel.setLayoutData(gridData);

		btnMerge.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				lblTest.setText("Merge Clicked!");
				mergeSelections();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
				lblTest.setText("Merge Default Clicked!");
			}
		});

		btnSub.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				lblTest.setText("Del Clicked!");
				deleteSelections();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
				lblTest.setText("Del Default Clicked!");
			}
		});

		lblTest = new Label(parentComposite, SWT.WRAP);
		lblTest.setAlignment(SWT.CENTER);
		lblTest.setText("");
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.minimumWidth = 145;
		gridData.widthHint = 145;
		gridData.minimumHeight = 82;
		gridData.heightHint = 82;
		lblTest.setLayoutData(gridData);

		gridData = new GridData(GridData.FILL_BOTH);
		gridData.minimumHeight = 62;
		gridData.heightHint = 156;
		if (System.getProperty("os.name").contains("Win")) {
			// In windows the list needs more space because of no multi line
			// support
			gridData.widthHint = 145;
			gridData.minimumWidth = 145;
		}

		selectionTree.setLayoutData(gridData);
		
		selectionTree.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {

				SelectionTypeEvent selectionTypeEvent = new SelectionTypeEvent(
						(SelectionType) event.item.getData());
				selectionTypeEvent.setCurrent(true);
				GeneralManager.get().getEventPublisher().triggerEvent(selectionTypeEvent);
			}
		});

		contentTree = new TreeItem(selectionTree, SWT.NONE);
		contentTree.setExpanded(true);
		contentTree.setData(-1);
		contentTree.setText("Content Selections");

		updateContentTree();
	}

	private void deleteSelections() {
		String tmpString = "Deletion: ";

		if ((selectionTree.getSelection().length) == 0) {
			lblTest.setText(tmpString + "No selected item(s).");
			return;
		}

		for (TreeItem selection : selectionTree.getSelection()) {
			SelectionType tmpSelectionType = (SelectionType) selection.getData();
			tmpString += tmpSelectionType.toString() + ",";
			SelectionTypeEvent event = new SelectionTypeEvent();
			event.addSelectionType(tmpSelectionType);
			event.setRemove(true);
			eventPublisher.triggerEvent(event);
		}

		ISelectionDelta selectionDelta = contentSelectionManager.getDelta();
		SelectionUpdateEvent event2 = new SelectionUpdateEvent();
		event2.setSender(this);
		event2.setSelectionDelta((SelectionDelta) selectionDelta);
		eventPublisher.triggerEvent(event2);
		lblTest.setText(tmpString + " deleted.");
		updateContentTree();
	}

	private void mergeSelections() {
		String tmpString = "Merging: ";

		if ((selectionTree.getSelection().length) < 2) {
			lblTest.setText(tmpString + "To few selected items for merge operation.");
			return;
		}

		boolean bIsFirst = true;
		SelectionType firstSelectionType = SelectionType.SELECTION;

		for (TreeItem selection : selectionTree.getSelection()) {
			if (bIsFirst) {
				bIsFirst = false;
				firstSelectionType = (SelectionType) selection.getData();
			} else {
				SelectionType tmpSelectionType = (SelectionType) selection.getData();
				tmpString += tmpSelectionType.toString() + ",";
				contentSelectionManager.moveType(tmpSelectionType, firstSelectionType);

				SelectionTypeEvent event = new SelectionTypeEvent();
				event.addSelectionType(tmpSelectionType);
				event.setRemove(true);
				eventPublisher.triggerEvent(event);
			}
		}

		ISelectionDelta selectionDelta = contentSelectionManager.getDelta();
		SelectionUpdateEvent event2 = new SelectionUpdateEvent();
		event2.setSender(this);
		event2.setSelectionDelta((SelectionDelta) selectionDelta);
		eventPublisher.triggerEvent(event2);

		lblTest.setText(tmpString + " merged into " + firstSelectionType.toString() + ".");

		updateContentTree();

	}

	private void updateContentTree() {
		ArrayList<SelectionType> sTypes = contentSelectionManager.getSelectionTypes();
		Color color = null;
		contentTree.removeAll();
		for (SelectionType tmpSelectionType : sTypes) {

			if (SelectionType.isDefaultType(tmpSelectionType)
					|| !tmpSelectionType.isManaged())
				continue;

			TreeItem item = new TreeItem(contentTree, SWT.NONE);

			float[] fArColor = tmpSelectionType.getColor();

			color = new Color(parentComposite.getDisplay(), (int) (fArColor[0] * 255),
					(int) (fArColor[1] * 255), (int) (fArColor[2] * 255));

			item.setText(tmpSelectionType.toString() + " ("
					+ contentSelectionManager.getNumberOfElements(tmpSelectionType) + ")");
			item.setBackground(color);
			item.setData(tmpSelectionType);

			contentTree.setExpanded(true);
		}
	}

	@Override
	public void handleSelectionUpdate(final ISelectionDelta selectionDelta,
			final boolean scrollToSelection, final String info) {
		contentSelectionManager.setDelta(selectionDelta);
		parentComposite.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				updateContentTree();
			}
		});
	}

	@Override
	public void handleSelectionCommand(IDCategory category,
			final SelectionCommand selectionCommand) {

		// nothing to do here
	}

	@Override
	public void handleRedrawView() {
		// nothing to do here
	}

	@Override
	public void handleUpdateView() {
		// nothing to do here
	}

	@Override
	public void handleClearSelections() {

	}

	/**
	 * handling method for updates about the info text displayed in the this
	 * info-area
	 * 
	 * @param info
	 *            short-info of the sender to display
	 */
	public void handleInfoAreaUpdate(final String info) {
		parentComposite.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				lblTest.setText(info);
			}
		});
	}

	/**
	 * Registers the listeners for this view to the event system. To release the
	 * allocated resources unregisterEventListeners() has to be called.
	 */
	@Override
	public void registerEventListeners() {
		selectionUpdateListener = new SelectionUpdateListener();
		selectionUpdateListener.setHandler(this);
		eventPublisher.addListener(SelectionUpdateEvent.class, selectionUpdateListener);

		virtualArrayUpdateListener = new ContentVAUpdateListener();
		virtualArrayUpdateListener.setHandler(this);
		eventPublisher.addListener(VirtualArrayUpdateEvent.class,
				virtualArrayUpdateListener);

		selectionCommandListener = new SelectionCommandListener();
		selectionCommandListener.setHandler(this);
		eventPublisher.addListener(SelectionCommandEvent.class, selectionCommandListener);

		redrawViewListener = new RedrawViewListener();
		redrawViewListener.setHandler(this);
		eventPublisher.addListener(RedrawViewEvent.class, redrawViewListener);

		clearSelectionsListener = new ClearSelectionsListener();
		clearSelectionsListener.setHandler(this);
		eventPublisher.addListener(ClearSelectionsEvent.class, clearSelectionsListener);
	}

	/**
	 * Unregisters the listeners for this view from the event system. To release
	 * the allocated resources unregisterEventListenrs() has to be called.
	 */
	@Override
	public void unregisterEventListeners() {
		if (selectionUpdateListener != null) {
			eventPublisher.removeListener(selectionUpdateListener);
			selectionUpdateListener = null;
		}
		if (virtualArrayUpdateListener != null) {
			eventPublisher.removeListener(virtualArrayUpdateListener);
			virtualArrayUpdateListener = null;
		}
		if (selectionCommandListener != null) {
			eventPublisher.removeListener(selectionCommandListener);
			selectionCommandListener = null;
		}
		if (redrawViewListener != null) {
			eventPublisher.removeListener(redrawViewListener);
			redrawViewListener = null;
		}
		if (clearSelectionsListener != null) {
			eventPublisher.removeListener(clearSelectionsListener);
			clearSelectionsListener = null;
		}
	}

	public void dispose() {
		unregisterEventListeners();
	}

	@Override
	public synchronized void queueEvent(
			final AEventListener<? extends IListenerOwner> listener, final AEvent event) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				listener.handleEvent(event);
			}
		});
	}

	@Override
	public void handleVAUpdate(ContentVADelta vaDelta, final String info) {
		initContent();
	}

	@Override
	public void replaceContentVA(int setID, String dataDomain, String vaType) {
		// nothing to do here
	}

	@Override
	public void setDataDomain(ASetBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;

		initContent();
	}

	@Override
	public ASetBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		return new SerializedSelectionBrowserView(dataDomain.getDataDomainID());
	}

	@Override
	public void initFromSerializableRepresentation(ASerializedView serializedView) {

	}
}
