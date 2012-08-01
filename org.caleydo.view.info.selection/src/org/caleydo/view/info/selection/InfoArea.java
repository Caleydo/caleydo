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
package org.caleydo.view.info.selection;

import java.util.Set;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.selection.DimensionSelectionManager;
import org.caleydo.core.data.selection.ESelectionCommandType;
import org.caleydo.core.data.selection.RecordSelectionManager;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.events.ClearSelectionsListener;
import org.caleydo.core.data.selection.events.ISelectionCommandHandler;
import org.caleydo.core.data.selection.events.ISelectionUpdateHandler;
import org.caleydo.core.data.selection.events.SelectionCommandListener;
import org.caleydo.core.data.selection.events.SelectionUpdateListener;
import org.caleydo.core.data.virtualarray.events.DimensionVADeltaEvent;
import org.caleydo.core.data.virtualarray.events.DimensionVAUpdateListener;
import org.caleydo.core.data.virtualarray.events.IDimensionVAUpdateHandler;
import org.caleydo.core.data.virtualarray.events.IRecordVAUpdateHandler;
import org.caleydo.core.data.virtualarray.events.RecordVADeltaEvent;
import org.caleydo.core.data.virtualarray.events.RecordVAUpdateListener;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.event.view.ClearSelectionsEvent;
import org.caleydo.core.event.view.SelectionCommandEvent;
import org.caleydo.core.event.view.infoarea.InfoAreaUpdateEvent;
import org.caleydo.core.event.view.tablebased.RedrawViewEvent;
import org.caleydo.core.event.view.tablebased.SelectionUpdateEvent;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.IDataDomainBasedView;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.RedrawViewListener;
import org.caleydo.view.info.selection.listener.InfoAreaUpdateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;

/**
 * Info area that is located in the side-bar. It shows the current view and the
 * current selection (in a tree).
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class InfoArea implements IDataDomainBasedView<ATableBasedDataDomain>,
		ISelectionUpdateHandler, IRecordVAUpdateHandler, IDimensionVAUpdateHandler,
		ISelectionCommandHandler, IViewCommandHandler {

	GeneralManager generalManager = null;
	EventPublisher eventPublisher = null;

	private Label lblViewInfoContent;

	private Tree selectionTree;

	private TreeItem contentTree;
	private TreeItem dimensionTree;

	private AGLView updateTriggeringView;
	private Composite parentComposite;

	protected SelectionUpdateListener selectionUpdateListener;
	protected RecordVAUpdateListener recordVAUpdateListener;
	protected DimensionVAUpdateListener dimensionVAUpdateListener;
	protected SelectionCommandListener selectionCommandListener;

	protected RedrawViewListener redrawViewListener;
	protected ClearSelectionsListener clearSelectionsListener;
	protected InfoAreaUpdateListener infoAreaUpdateListener;

	protected ATableBasedDataDomain dataDomain;

	RecordSelectionManager recordSelectionManager;
	DimensionSelectionManager dimensionSelectionManager;

	/**
	 * Constructor.
	 */
	public InfoArea() {

		generalManager = GeneralManager.get();
		eventPublisher = generalManager.getEventPublisher();
	}

	public Control createControl(final Composite parent) {

		recordSelectionManager = dataDomain.getRecordSelectionManager();
		dimensionSelectionManager = dataDomain.getDimensionSelectionManager();

		parentComposite = parent;

		selectionTree = new Tree(parent, SWT.NULL);

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessVerticalSpace = true;
		gridData.minimumHeight = 150;
		selectionTree.setLayoutData(gridData);

		contentTree = new TreeItem(selectionTree, SWT.NONE);
		contentTree.setExpanded(true);
		contentTree.setData(-1);

		contentTree.setText(dataDomain.getRecordDenomination(true, true));

		dimensionTree = new TreeItem(selectionTree, SWT.NONE);
		dimensionTree.setExpanded(true);
		dimensionTree.setData(-1);
		dimensionTree.setText(dataDomain.getDimensionDenomination(true, true));

		lblViewInfoContent = new Label(parent, SWT.WRAP);
		lblViewInfoContent.setText("");
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		gridData.minimumHeight = 100;
		lblViewInfoContent.setLayoutData(gridData);

		return parent;
	}

	@Override
	public void handleSelectionUpdate(SelectionDelta selectionDelta) {

		IDType recordIDType = dataDomain.getRecordIDType();
		if (selectionDelta.getIDType().getIDCategory()
				.equals(recordIDType.getIDCategory())) {
			// Check for type that can be handled

			recordSelectionManager.setDelta(selectionDelta);
			updateTree(true, recordSelectionManager, contentTree);
		} else if (selectionDelta.getIDType().getIDCategory()
				.equals(dimensionSelectionManager.getIDType().getIDCategory())) {
			dimensionSelectionManager.setDelta(selectionDelta);
			updateTree(false, dimensionSelectionManager, dimensionTree);
		}

	}

	private void updateTree(final boolean isContent,
			final SelectionManager selectionManager, final TreeItem tree) {

		if (parentComposite.isDisposed())
			return;

		parentComposite.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {

				// Flush old items from this selection type
				for (TreeItem item : tree.getItems()) {
					item.dispose();
				}

				Set<Integer> mouseOverIDs = selectionManager
						.getElements(SelectionType.MOUSE_OVER);
				createItems(isContent, tree, SelectionType.MOUSE_OVER, mouseOverIDs);

				Set<Integer> selectedIDs = selectionManager
						.getElements(SelectionType.SELECTION);
				createItems(isContent, tree, SelectionType.SELECTION, selectedIDs);

			}
		});
	}

	private void createItems(boolean isContent, TreeItem tree,
			SelectionType selectionType, Set<Integer> ids) {
		Color color;
		int[] intColor = selectionType.getIntColor();

		color = new Color(parentComposite.getDisplay(), intColor[0], intColor[1],
				intColor[2]);

		for (Integer id : ids) {
			String name;
			if (isContent)
				name = dataDomain.getRecordLabel(id);
			else
				name = dataDomain.getDimensionLabel(id);

			TreeItem item = new TreeItem(tree, SWT.NONE);

			item.setText(name);
			item.setBackground(color);
			item.setData(id);
			item.setData("selection_type", selectionType);
		}

		tree.setExpanded(true);
	}

	@Override
	public void handleSelectionCommand(IDCategory category,
			final SelectionCommand selectionCommand) {

		if (parentComposite.isDisposed())
			return;

		parentComposite.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				ESelectionCommandType cmdType;

				cmdType = selectionCommand.getSelectionCommandType();
				if (cmdType == ESelectionCommandType.RESET
						|| cmdType == ESelectionCommandType.CLEAR_ALL) {
					selectionTree.removeAll();
				} else if (cmdType == ESelectionCommandType.CLEAR) {
					// Flush old items that become
					// deselected/normal
					for (TreeItem tmpItem : selectionTree.getItems()) {
						if (tmpItem.getData("selection_type") == selectionCommand
								.getSelectionType()) {
							tmpItem.dispose();
						}
					}

				}
			}
		});
	}

	protected AGLView getUpdateTriggeringView() {
		return updateTriggeringView;
	}

	@Override
	public void handleRedrawView() {
		// nothing to do here
	}

	@Override
	public void handleClearSelections() {
		if (!contentTree.isDisposed())
			contentTree.removeAll();
		if (!dimensionTree.isDisposed())
			dimensionTree.removeAll();
		recordSelectionManager.clearSelections();
		dimensionSelectionManager.clearSelections();
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
				lblViewInfoContent.setText(info);
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
		selectionUpdateListener.setDataDomainID(dataDomain.getDataDomainID());
		eventPublisher.addListener(SelectionUpdateEvent.class, selectionUpdateListener);

		recordVAUpdateListener = new RecordVAUpdateListener();
		recordVAUpdateListener.setHandler(this);
		recordVAUpdateListener.setDataDomainID(dataDomain.getDataDomainID());
		eventPublisher.addListener(RecordVADeltaEvent.class, recordVAUpdateListener);

		dimensionVAUpdateListener = new DimensionVAUpdateListener();
		dimensionVAUpdateListener.setHandler(this);
		dimensionVAUpdateListener.setDataDomainID(dataDomain.getDataDomainID());
		eventPublisher
				.addListener(DimensionVADeltaEvent.class, dimensionVAUpdateListener);

		// replaceDimensionVAListener = new ReplaceDimensionVAListener();
		// replaceDimensionVAListener.setHandler(this);
		// replaceDimensionVAListener.setDataDomainID(dataDomain.getDataDomainID());
		// eventPublisher.addListener(ReplaceDimensionVAEvent.class,
		// replaceDimensionVAListener);

		selectionCommandListener = new SelectionCommandListener();
		selectionCommandListener.setHandler(this);
		selectionCommandListener.setDataDomainID(dataDomain.getDataDomainID());
		eventPublisher.addListener(SelectionCommandEvent.class, selectionCommandListener);

		redrawViewListener = new RedrawViewListener();
		redrawViewListener.setHandler(this);
		eventPublisher.addListener(RedrawViewEvent.class, redrawViewListener);

		clearSelectionsListener = new ClearSelectionsListener();
		clearSelectionsListener.setHandler(this);
		eventPublisher.addListener(ClearSelectionsEvent.class, clearSelectionsListener);

		infoAreaUpdateListener = new InfoAreaUpdateListener();
		infoAreaUpdateListener.setHandler(this);
		eventPublisher.addListener(InfoAreaUpdateEvent.class, infoAreaUpdateListener);
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

		if (dimensionVAUpdateListener != null) {
			eventPublisher.removeListener(dimensionVAUpdateListener);
			dimensionVAUpdateListener = null;
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
		if (infoAreaUpdateListener != null) {
			eventPublisher.removeListener(infoAreaUpdateListener);
			infoAreaUpdateListener = null;
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
		updateTree(true, recordSelectionManager, contentTree);
	}

	@Override
	public void handleDimensionVAUpdate(String dimensionPerspectiveID) {
		if (parentComposite.isDisposed())
			return;

		updateTree(false, dimensionSelectionManager, dimensionTree);
	}

	@Override
	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

}
