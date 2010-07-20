package org.caleydo.rcp.util.info;

import java.util.ArrayList;
import java.util.Set;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ESelectionCommandType;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.ContentVADelta;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDeltaItem;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IIDMappingManager;
import org.caleydo.core.manager.ISetBasedDataDomain;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.datadomain.IDataDomainBasedView;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.view.ClearSelectionsEvent;
import org.caleydo.core.manager.event.view.SelectionCommandEvent;
import org.caleydo.core.manager.event.view.infoarea.InfoAreaUpdateEvent;
import org.caleydo.core.manager.event.view.storagebased.RedrawViewEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.event.view.storagebased.VirtualArrayUpdateEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.listener.ClearSelectionsListener;
import org.caleydo.core.view.opengl.canvas.listener.ContentVAUpdateListener;
import org.caleydo.core.view.opengl.canvas.listener.IContentVAUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.RedrawViewListener;
import org.caleydo.core.view.opengl.canvas.listener.SelectionCommandListener;
import org.caleydo.core.view.opengl.canvas.listener.SelectionUpdateListener;
import org.caleydo.rcp.util.info.listener.InfoAreaUpdateListener;
import org.eclipse.core.runtime.Status;
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
 * Info area that is located in the side-bar. It shows the current view and the current selection (in a tree).
 * 
 * @author Marc Streit
 * @author Alexander Lex
 * @deprecated implement as view
 */
public class InfoArea
	implements IDataDomainBasedView<ISetBasedDataDomain>, ISelectionUpdateHandler, IContentVAUpdateHandler,
	ISelectionCommandHandler, IViewCommandHandler {

	private static String viewType = "org.caleydo.view.infoarea";

	IGeneralManager generalManager = null;
	IEventPublisher eventPublisher = null;

	private Label lblViewInfoContent;

	private Tree selectionTree;

	private TreeItem contentTree;
	private TreeItem experimentTree;
	// private TreeItem pathwayTree;

	private AGLView updateTriggeringView;
	private Composite parentComposite;

	// private GlyphManager glyphManager;
	private IIDMappingManager idMappingManager;

	// private String shortInfo;

	protected SelectionUpdateListener selectionUpdateListener;
	protected ContentVAUpdateListener virtualArrayUpdateListener;
	protected SelectionCommandListener selectionCommandListener;

	protected RedrawViewListener redrawViewListener;
	protected ClearSelectionsListener clearSelectionsListener;
	protected InfoAreaUpdateListener infoAreaUpdateListener;

	protected ISetBasedDataDomain dataDomain;

	/**
	 * Constructor.
	 */
	public InfoArea() {

		registerDataDomains();
		generalManager = GeneralManager.get();
		eventPublisher = generalManager.getEventPublisher();

		// glyphManager = GeneralManager.get().getGlyphManager();
		idMappingManager = generalManager.getIDMappingManager();

		registerEventListeners();
	}

	// FIXME this should go into the activator
	@Deprecated
	private void registerDataDomains() {
		ArrayList<String> dataDomainTypes = new ArrayList<String>();
		dataDomainTypes.add("org.caleydo.datadomain.genetic");
		dataDomainTypes.add("org.caleydo.datadomain.generic");
		dataDomainTypes.add("org.caleydo.datadomain.clinical");

		DataDomainManager.getInstance().getAssociationManager()
			.registerDatadomainTypeViewTypeAssociation(dataDomainTypes, viewType);
	}

	public Control createControl(final Composite parent) {

		parentComposite = parent;

		selectionTree = new Tree(parent, SWT.NULL);

		lblViewInfoContent = new Label(parent, SWT.WRAP);
		lblViewInfoContent.setAlignment(SWT.CENTER);
		lblViewInfoContent.setText("");

		GridData gridData = new GridData(GridData.FILL_BOTH);

		// FIXME: horizontal toolbar style support
		// if (ToolBarView.bHorizontal) {
		// gridData.minimumWidth = 150;
		// gridData.widthHint = 150;
		// gridData.minimumHeight = 72;
		// gridData.heightHint = 72;
		// } else {
		gridData.minimumWidth = 100;
		gridData.widthHint = 100;
		gridData.minimumHeight = 82;
		gridData.heightHint = 82;
		// }

		lblViewInfoContent.setLayoutData(gridData);

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

		// selectionTree.setItemCount(2);
		// selectionTree.addSelectionListener(new SelectionAdapter() {
		//
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// super.widgetSelected(e);
		//
		// // ((HTMLBrowserView)
		// //
		// PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
		// //
		// .findView(HTMLBrowserView.ID)).getHTMLBrowserViewRep().setUrl("bla");
		// }
		// });

		contentTree = new TreeItem(selectionTree, SWT.NONE);
		contentTree.setExpanded(true);
		contentTree.setData(-1);
		contentTree.setText(dataDomain.getContentLabel(true, true));

		experimentTree = new TreeItem(selectionTree, SWT.NONE);
		experimentTree.setExpanded(true);
		experimentTree.setData(-1);
		experimentTree.setText("Experiments");

		// pathwayTree = new TreeItem(selectionTree, SWT.NONE);
		// pathwayTree.setText("Pathways");
		// pathwayTree.setExpanded(false);
		// pathwayTree.setData(-1);

		return parent;
	}

	@Override
	public void handleSelectionUpdate(final ISelectionDelta selectionDelta, final boolean scrollToSelection,
		final String info) {

		parentComposite.getDisplay().asyncExec(new Runnable() {
			public void run() {

				if (info != null) {
					lblViewInfoContent.setText(info);
				}

				if (selectionDelta.getIDType() == EIDType.EXPRESSION_INDEX) {

					for (SelectionDeltaItem selectionItem : selectionDelta) {

						// Flush old genes from this selection type
						for (TreeItem item : contentTree.getItems()) {
							if (item.getData("selection_type") == selectionItem.getSelectionType()
								|| ((Integer) item.getData()) == selectionItem.getPrimaryID()) {

								item.dispose();
							}
						}

						// if (selectionItem.getSelectionType() ==
						// SelectionType.NORMAL
						// || selectionItem.getSelectionType() ==
						// SelectionType.DESELECTED) {
						// // Flush old items that become deselected/normal
						// for (TreeItem tmpItem : selectionTree.getItems()) {
						// if (tmpItem.getData() == null
						// || ((Integer) tmpItem.getData()).intValue() ==
						// selectionItem
						// .getPrimaryID()) {
						// tmpItem.dispose();
						// }
						// }
						// }
						if (selectionItem.getSelectionType() == SelectionType.MOUSE_OVER
							|| selectionItem.getSelectionType() == SelectionType.SELECTION) {

							Color color;
							float[] fArColor = null;

							if (selectionItem.getSelectionType() == SelectionType.SELECTION) {
								fArColor = SelectionType.SELECTION.getColor();
							}
							else if (selectionItem.getSelectionType() == SelectionType.MOUSE_OVER) {
								fArColor = SelectionType.MOUSE_OVER.getColor();
							}

							color =
								new Color(parentComposite.getDisplay(), (int) (fArColor[0] * 255),
									(int) (fArColor[1] * 255), (int) (fArColor[2] * 255));

							String sContentName = "";
							if (dataDomain.getDataDomainType() == "org.caleydo.datadomain.genetic") {

								int iExpressionIndex = selectionItem.getPrimaryID();

								// FIXME: Due to new mapping system, a mapping
								// involving expression index can
								// return a Set of
								// values, depending on the IDType that has been
								// specified when loading
								// expression data.
								// Possibly a different handling of the Set is
								// required.
								Set<String> setRefSeqIDs =
									idMappingManager.getIDAsSet(EIDType.EXPRESSION_INDEX,
										EIDType.REFSEQ_MRNA, iExpressionIndex);
								String sRefSeqID = null;
								if ((setRefSeqIDs != null && !setRefSeqIDs.isEmpty())) {
									sRefSeqID = (String) setRefSeqIDs.toArray()[0];
								}

								// FIXME: Due to new mapping system, a mapping
								// involving expression index can
								// return a Set of
								// values, depending on the IDType that has been
								// specified when loading
								// expression data.
								// Possibly a different handling of the Set is
								// required.
								Set<String> setGeneSymbols =
									idMappingManager.getIDAsSet(EIDType.EXPRESSION_INDEX,
										EIDType.GENE_SYMBOL, iExpressionIndex);

								if ((setGeneSymbols != null && !setGeneSymbols.isEmpty())) {
									sContentName = (String) setGeneSymbols.toArray()[0];
								}

								// FIXME horizontal toolbar style support
								// if (ToolBarView.bHorizontal ||
								// Application.bIsWindowsOS) {
								// FIXME: when view plugin reorganization is
								// done
								// if (Application.bIsWindowsOS) {
								// sContentName = sContentName + " - " +
								// sRefSeqID;
								// }
								// else {
								sContentName = sContentName + "\n" + sRefSeqID;
								// }
							}
							else {
								sContentName =
									idMappingManager.getID(EIDType.EXPRESSION_INDEX, EIDType.UNSPECIFIED,
										selectionItem.getPrimaryID());
							}

							if (sContentName == null) {
								sContentName = "Unknown";
							}

							TreeItem item = new TreeItem(contentTree, SWT.NONE);

							item.setText(sContentName);
							item.setBackground(color);
							item.setData(selectionItem.getPrimaryID());
							item.setData("selection_type", selectionItem.getSelectionType());

							contentTree.setExpanded(true);
						}
					}
				}
				else if (selectionDelta.getIDType() == EIDType.EXPERIMENT_INDEX) {
					if (info != null) {
						lblViewInfoContent.setText(info);
					}

					for (SelectionDeltaItem selectionItem : selectionDelta) {

						if (selectionItem.getSelectionType() == SelectionType.MOUSE_OVER
							|| selectionItem.getSelectionType() == SelectionType.SELECTION) {

							// Flush old experiments from this selection type
							for (TreeItem item : experimentTree.getItems()) {
								if (item.getData() == null
									|| item.getData("selection_type") == selectionItem.getSelectionType()
									|| ((Integer) item.getData()) == selectionItem.getPrimaryID()) {
									item.dispose();

								}
							}

							Color color;
							float[] fArColor = null;

							if (selectionItem.getSelectionType() == SelectionType.SELECTION) {
								fArColor = SelectionType.SELECTION.getColor();
							}
							else if (selectionItem.getSelectionType() == SelectionType.MOUSE_OVER) {
								fArColor = SelectionType.MOUSE_OVER.getColor();
							}

							color =
								new Color(parentComposite.getDisplay(), (int) (fArColor[0] * 255),
									(int) (fArColor[1] * 255), (int) (fArColor[2] * 255));

							// Retrieve current set
							// FIXME: This solution is not robust if new data
							// are loaded -> REDESIGN

							ISet set = dataDomain.getSet();
							TreeItem item = new TreeItem(experimentTree, SWT.NONE);

							try {
								IStorage storage = set.get(selectionItem.getPrimaryID());
								if (storage != null)
									item.setText(storage.getLabel());
								else {
									generalManager.getLogger().log(
										new Status(Status.WARNING, "org.caleydo.rcp",
											"Info Area couldn't find the correct storage for ID: "
												+ selectionItem.getPrimaryID() + ". Set was: " + set));
								}
							}
							catch (IndexOutOfBoundsException e) {
								item.setText("ERROR");
							}
							item.setData(selectionItem.getPrimaryID());
							// item.setData("mapping_type",
							// EMappingType.EXPERIMENT_2_EXPERIMENT_INDEX.toString());
							item.setData("selection_type", selectionItem.getSelectionType());
							item.setBackground(color);

							experimentTree.setExpanded(true);
						}
						// addGlyphInfo(selectionItem, item);

					}
				}
			}
		});
	}

	// private void addGlyphInfo(SelectionDeltaItem selectionItem, TreeItem
	// item) {
	// GlyphEntry entry =
	// glyphManager.getGlyphs().get(selectionItem.getPrimaryID());
	//
	// if (entry == null)
	// return;
	//
	// for (int i = 0; i < entry.getNumberOfParameters(); ++i) {
	// String info =
	// glyphManager.getGlyphAttributeInfoStringWithInternalColumnNumber(i,
	// entry.getParameter(i));
	//
	// TreeItem subitem = new TreeItem(item, SWT.NONE);
	// subitem.setText(info);
	// }
	//
	// for (String key : entry.getStringParameterColumnNames()) {
	// String info = key + ": " + entry.getStringParameter(key);
	// TreeItem subitem = new TreeItem(item, SWT.NONE);
	// subitem.setText(info);
	// }
	// }

	@Override
	public void handleSelectionCommand(EIDCategory category, final SelectionCommand selectionCommand) {

		if (parentComposite.isDisposed())
			return;

		parentComposite.getDisplay().asyncExec(new Runnable() {
			public void run() {
				ESelectionCommandType cmdType;

				cmdType = selectionCommand.getSelectionCommandType();
				if (cmdType == ESelectionCommandType.RESET || cmdType == ESelectionCommandType.CLEAR_ALL) {
					selectionTree.removeAll();
				}
				else if (cmdType == ESelectionCommandType.CLEAR) {
					// Flush old items that become
					// deselected/normal
					for (TreeItem tmpItem : selectionTree.getItems()) {
						if (tmpItem.getData("selection_type") == selectionCommand.getSelectionType()) {
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
	public void handleUpdateView() {
		// nothing to do here
	}

	@Override
	public void handleClearSelections() {
		contentTree.removeAll();
		experimentTree.removeAll();
	}

	/**
	 * handling method for updates about the info text displayed in the this info-area
	 * 
	 * @param info
	 *            short-info of the sender to display
	 */
	public void handleInfoAreaUpdate(final String info) {
		parentComposite.getDisplay().asyncExec(new Runnable() {
			public void run() {
				lblViewInfoContent.setText(info);
			}
		});
	}

	/**
	 * Registers the listeners for this view to the event system. To release the allocated resources
	 * unregisterEventListeners() has to be called.
	 */
	public void registerEventListeners() {
		selectionUpdateListener = new SelectionUpdateListener();
		selectionUpdateListener.setDataDomainType("org.caleydo.datadomain.genetic");
		selectionUpdateListener.setHandler(this);
		eventPublisher.addListener(SelectionUpdateEvent.class, selectionUpdateListener);

		virtualArrayUpdateListener = new ContentVAUpdateListener();
		virtualArrayUpdateListener.setHandler(this);
		eventPublisher.addListener(VirtualArrayUpdateEvent.class, virtualArrayUpdateListener);

		selectionCommandListener = new SelectionCommandListener();
		selectionCommandListener.setHandler(this);
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
	 * Unregisters the listeners for this view from the event system. To release the allocated resources
	 * unregisterEventListenrs() has to be called.
	 */
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
		if (infoAreaUpdateListener != null) {
			eventPublisher.removeListener(infoAreaUpdateListener);
			infoAreaUpdateListener = null;
		}
	}

	public void dispose() {
		unregisterEventListeners();
	}

	@Override
	public synchronized void queueEvent(final AEventListener<? extends IListenerOwner> listener,
		final AEvent event) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				listener.handleEvent(event);
			}
		});
	}

	@Override
	public void handleContentVAUpdate(ContentVADelta vaDelta, final String info) {
		if (vaDelta.getIDType() != EIDType.REFSEQ_MRNA_INT)
			return;

		if (parentComposite.isDisposed())
			return;

		if (info != null) {
			parentComposite.getDisplay().asyncExec(new Runnable() {
				public void run() {
					lblViewInfoContent.setText(info);

					// for (VADeltaItem item : delta) {
					// if (item.getType() == EVAOperation.REMOVE_ELEMENT) {
					// // Flush old items that become deselected/normal
					// for (TreeItem tmpItem : selectionTree.getItems()) {
					// if (((Integer) tmpItem.getData()).intValue() ==
					// item.getPrimaryID()) {
					// tmpItem.dispose();
					// }
					// }
					// }
					// }
				}
			});
		}
	}

	@Override
	public void replaceContentVA(int setID, String dataDomain, ContentVAType vaType) {
		// TODO Auto-generated method stub
	}

	@Override
	public ISetBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	public void setDataDomain(ISetBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

}
