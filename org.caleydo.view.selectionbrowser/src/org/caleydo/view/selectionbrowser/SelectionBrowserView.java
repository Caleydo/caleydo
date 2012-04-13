/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.selectionbrowser;

import java.util.ArrayList;
import org.caleydo.core.data.id.IDCategory;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.mapping.IDMappingManagerRegistry;
import org.caleydo.core.data.selection.DimensionSelectionManager;
import org.caleydo.core.data.selection.RecordSelectionManager;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.SelectionTypeEvent;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.events.ClearSelectionsListener;
import org.caleydo.core.data.selection.events.ISelectionCommandHandler;
import org.caleydo.core.data.selection.events.ISelectionUpdateHandler;
import org.caleydo.core.data.selection.events.SelectionCommandListener;
import org.caleydo.core.data.selection.events.SelectionUpdateListener;
import org.caleydo.core.data.virtualarray.events.IRecordVAUpdateHandler;
import org.caleydo.core.data.virtualarray.events.RecordVAUpdateEvent;
import org.caleydo.core.data.virtualarray.events.RecordVAUpdateListener;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.event.view.ClearSelectionsEvent;
import org.caleydo.core.event.view.SelectionCommandEvent;
import org.caleydo.core.event.view.tablebased.RedrawViewEvent;
import org.caleydo.core.event.view.tablebased.SelectionUpdateEvent;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.RedrawViewListener;
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
public class SelectionBrowserView extends ASWTView implements ISelectionUpdateHandler,
		IRecordVAUpdateHandler, ISelectionCommandHandler, IViewCommandHandler {

	private final static String SELECTION_TYPE_NAME_1 = "Selected by group 1";
	private final static String SELECTION_TYPE_NAME_2 = "Selected by group 2";
	private final static String SELECTION_TYPE_NAME_3 = "Selected by group 3";
	private final static String SELECTION_TYPE_NAME_4 = "Selected by group 4";

	/** Colors taken from color brewer qualitative "Set 1" with 8 colors */
	private final static float[] SELECTION_COLOR_1 = new float[] { 152f / 255, 78f / 255,
			163f / 255, 1 };
	private final static float[] SELECTION_COLOR_2 = new float[] { 1, 127f / 255, 0, 1 }; // yellow
	private final static float[] SELECTION_COLOR_3 = new float[] { 247f / 255,
			129f / 255, 191f / 255, 1 };
	// private final static float[] SELECTION_COLOR_3 = new float[] { 1, 1,
	// 51f/255, 1 };
	private final static float[] SELECTION_COLOR_4 = new float[] { 166f / 255, 86f / 255,
			40f / 255, 1 };

	GeneralManager generalManager = null;
	EventPublisher eventPublisher = null;

	RecordSelectionManager recordSelectionManager;
	DimensionSelectionManager dimensionSelectionManager;

	private Tree selectionTree;
	private TreeItem contentTree;

	private Label lblTest;
	private Button btnMerge;
	private Button btnSub;

	private SelectionUpdateListener selectionUpdateListener;
	private RecordVAUpdateListener recordVAUpdateListener;
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
		recordSelectionManager = new RecordSelectionManager(IDMappingManagerRegistry
				.get().getIDMappingManager(IDCategory.getIDCategory("SAMPLE")),
				IDType.getIDType("SAMPLE"));
		initSelectedByGroupSelectionTypes();
	}

	private void initContent() {

	}

	private void initSelectedByGroupSelectionTypes() {

		// Check if types have already been added
		for (SelectionType selectionType : recordSelectionManager.getSelectionTypes()) {
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

		// SelectionTypeEvent selectionTypeEvent = new SelectionTypeEvent(
		// selectedByGroupSelectionTypes.get(0));
		// selectionTypeEvent.setCurrent(true);
		// GeneralManager.get().getEventPublisher().triggerEvent(selectionTypeEvent);
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

			@Override
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

		SelectionDelta selectionDelta = recordSelectionManager.getDelta();
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
				recordSelectionManager.moveType(tmpSelectionType, firstSelectionType);

				SelectionTypeEvent event = new SelectionTypeEvent();
				event.addSelectionType(tmpSelectionType);
				event.setRemove(true);
				eventPublisher.triggerEvent(event);
			}
		}

		SelectionDelta selectionDelta = recordSelectionManager.getDelta();
		SelectionUpdateEvent event2 = new SelectionUpdateEvent();
		event2.setSender(this);
		event2.setSelectionDelta((SelectionDelta) selectionDelta);
		eventPublisher.triggerEvent(event2);

		lblTest.setText(tmpString + " merged into " + firstSelectionType.toString() + ".");

		updateContentTree();

	}

	private void updateContentTree() {
		ArrayList<SelectionType> sTypes = recordSelectionManager.getSelectionTypes();
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
					+ recordSelectionManager.getNumberOfElements(tmpSelectionType) + ")");
			item.setBackground(color);
			item.setData(tmpSelectionType);

			contentTree.setExpanded(true);
		}
	}

	@Override
	public void handleSelectionUpdate(final SelectionDelta selectionDelta,
			final boolean scrollToSelection, final String info) {
		if (!selectionDelta.getIDType().getIDCategory()
				.equals(recordSelectionManager.getIDType().getIDCategory()))
			return;
		recordSelectionManager.setDelta(selectionDelta);
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

		recordVAUpdateListener = new RecordVAUpdateListener();
		recordVAUpdateListener.setHandler(this);
		eventPublisher.addListener(RecordVAUpdateEvent.class, recordVAUpdateListener);

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
		if (recordVAUpdateListener != null) {
			eventPublisher.removeListener(recordVAUpdateListener);
			recordVAUpdateListener = null;
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
	public void handleRecordVAUpdate(String recordPerspectiveID) {
		initContent();
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		return new SerializedSelectionBrowserView();
	}

	@Override
	public void initFromSerializableRepresentation(ASerializedView serializedView) {

	}
}
