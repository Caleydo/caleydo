package org.caleydo.rcp.util.info;

import java.util.Collection;

import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.data.selection.DeltaEventContainer;
import org.caleydo.core.data.selection.ESelectionCommandType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.data.selection.IVirtualArrayDelta;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionCommandEventContainer;
import org.caleydo.core.data.selection.SelectionDeltaItem;
import org.caleydo.core.data.selection.VADeltaItem;
import org.caleydo.core.manager.IIDMappingManager;
import org.caleydo.core.manager.event.EMediatorType;
import org.caleydo.core.manager.event.IEventContainer;
import org.caleydo.core.manager.event.IMediatorReceiver;
import org.caleydo.core.manager.event.InfoAreaUpdateEventContainer;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.rcp.Application;
import org.caleydo.rcp.views.swt.ToolBarView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Info area that is located in the side-bar. It shows the current view and the current selection (in a tree).
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class InfoArea
	implements IMediatorReceiver {
	private Label lblViewInfoContent;

	private Tree selectionTree;

	private TreeItem geneTree;
	private TreeItem experimentTree;
	private TreeItem pathwayTree;

	private AGLEventListener updateTriggeringView;
	private Composite parentComposite;

	// private GlyphManager glyphManager;
	private IIDMappingManager idMappingManager;

	private String shortInfo;

	/**
	 * Constructor.
	 */
	public InfoArea() {
		GeneralManager.get().getEventPublisher().addReceiver(EMediatorType.SELECTION_MEDIATOR, this);
		GeneralManager.get().getEventPublisher().addReceiver(EMediatorType.VIEW_SELECTION, this);

		// glyphManager = GeneralManager.get().getGlyphManager();
		idMappingManager = GeneralManager.get().getIDMappingManager();
	}

	public Control createControl(final Composite parent) {

		parentComposite = parent;

		selectionTree = new Tree(parent, SWT.NULL);

		lblViewInfoContent = new Label(parent, SWT.WRAP);
		lblViewInfoContent.setAlignment(SWT.CENTER);
		lblViewInfoContent.setText("");
		
		GridData gridData = new GridData(GridData.FILL_BOTH);

		if (ToolBarView.bHorizontal) {
			gridData.minimumWidth = 150;
			gridData.widthHint = 150;
			gridData.minimumHeight = 72;
			gridData.heightHint = 72;
		}
		else {
			gridData.minimumWidth = 100;
			gridData.widthHint = 150;
			gridData.minimumHeight = 82;
			gridData.heightHint = 82;
		}

		lblViewInfoContent.setLayoutData(gridData);

		gridData = new GridData(GridData.FILL_BOTH);
		gridData.minimumHeight = 62;
		gridData.heightHint = 156;

		if (Application.bIsWindowsOS) {
			// In windows the list needs more space because of no multi line
			// support
			gridData.widthHint = 145;
			gridData.minimumWidth = 145;
		}
//		else {
//			gridData.widthHint = 145;
//			gridData.minimumWidth = 145;
//		}

		selectionTree.setLayoutData(gridData);

		// selectionTree.setItemCount(2);
//		selectionTree.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				super.widgetSelected(e);
//
//				// ((HTMLBrowserView)
//				// PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
//				// .findView(HTMLBrowserView.ID)).getHTMLBrowserViewRep().setUrl("bla");
//			}
//		});

		geneTree = new TreeItem(selectionTree, SWT.NONE);
		geneTree.setText("Genes");
		geneTree.setExpanded(true);
		geneTree.setData(-1);
		experimentTree = new TreeItem(selectionTree, SWT.NONE);
		experimentTree.setText("Experiments");
		experimentTree.setExpanded(true);
		experimentTree.setData(-1);
//		pathwayTree = new TreeItem(selectionTree, SWT.NONE);
//		pathwayTree.setText("Pathways");
//		pathwayTree.setExpanded(false);
//		pathwayTree.setData(-1);

		return parent;
	}
	
	private void handleSelectionUpdate(final IUniqueObject eventTrigger, final ISelectionDelta selectionDelta) {
		parentComposite.getDisplay().asyncExec(new Runnable() {
			public void run() {
				
				if (eventTrigger instanceof AGLEventListener) {
					lblViewInfoContent.setText(((AGLEventListener) eventTrigger).getShortInfo());
				}
				
				if (selectionDelta.getIDType() == EIDType.REFSEQ_MRNA_INT) {
					
					for (SelectionDeltaItem selectionItem : selectionDelta) {
						
						// Flush old genes from this selection type
						for (TreeItem item : geneTree.getItems())
						{
							if (item.getData("selection_type") == selectionItem.getSelectionType()
								|| ((Integer)item.getData()) == selectionItem.getPrimaryID()) {
			
								item.dispose();
							}
						}
						
//						if (selectionItem.getSelectionType() == ESelectionType.NORMAL
//							|| selectionItem.getSelectionType() == ESelectionType.DESELECTED) {
//							// Flush old items that become deselected/normal
//							for (TreeItem tmpItem : selectionTree.getItems()) {
//								if (tmpItem.getData() == null
//									|| ((Integer) tmpItem.getData()).intValue() == selectionItem
//										.getPrimaryID()) {
//									tmpItem.dispose();
//								}
//							}
//						}
						if (selectionItem.getSelectionType() == ESelectionType.MOUSE_OVER
							|| selectionItem.getSelectionType() == ESelectionType.SELECTION) {
							
							Color color;
							float[] fArColor = null;

							if (selectionItem.getSelectionType() == ESelectionType.SELECTION) {
								fArColor = GeneralRenderStyle.SELECTED_COLOR;
							}
							else if (selectionItem.getSelectionType() == ESelectionType.MOUSE_OVER) {
								fArColor = GeneralRenderStyle.MOUSE_OVER_COLOR;
							}

							color = new Color(parentComposite.getDisplay(), (int) (fArColor[0] * 255),
									(int) (fArColor[1] * 255), (int) (fArColor[2] * 255));

							String sRefSeqID =
								idMappingManager.getID(EMappingType.REFSEQ_MRNA_INT_2_REFSEQ_MRNA,
									selectionItem.getPrimaryID());

							Integer iDavidID =
								idMappingManager.getID(EMappingType.REFSEQ_MRNA_INT_2_DAVID, selectionItem
									.getPrimaryID());

							String sGeneSymbol =
								idMappingManager.getID(EMappingType.DAVID_2_GENE_SYMBOL, iDavidID);

							if (sGeneSymbol == null) {
								sGeneSymbol = "Unknown";
							}

//							boolean bIsExisting = false;
//							for (TreeItem existingItem : selectionTree.getItems()) {
//								if (existingItem.getText().equals(sGeneSymbol)
//									&& ((Integer) existingItem.getData()).intValue() == selectionItem
//										.getPrimaryID()) {
//									existingItem.setBackground(color);
//									existingItem.getItem(0).setBackground(color);
//									existingItem.setData("selection_type", selectionItem.getSelectionType());
//									bIsExisting = true;
//									break;
//								}
//							}

//							if (!bIsExisting) {
								TreeItem item = new TreeItem(geneTree, SWT.NONE);
								if (ToolBarView.bHorizontal || Application.bIsWindowsOS) {
									item.setText(sGeneSymbol + " - " + sRefSeqID);
								}
								else {
									item.setText(sGeneSymbol + "\n" + sRefSeqID);
								}
								item.setBackground(color);
								item.setData(selectionItem.getPrimaryID());
								item.setData("selection_type", selectionItem.getSelectionType());

//								TreeItem subItem = new TreeItem(item, SWT.NONE);
//								subItem.setText(sRefSeqID);
//								subItem.setBackground(color);
								geneTree.setExpanded(true);
//							}
						}
					}
				}
				else if (selectionDelta.getIDType() == EIDType.EXPERIMENT_INDEX) {
					lblViewInfoContent.setText(((AGLEventListener) eventTrigger).getShortInfo());

					for (SelectionDeltaItem selectionItem : selectionDelta) {
						
						if (selectionItem.getSelectionType() == ESelectionType.MOUSE_OVER
							|| selectionItem.getSelectionType() == ESelectionType.SELECTION) {
	
							// Flush old experiments from this selection type
							for (TreeItem item : experimentTree.getItems())
							{
								if (item.getData("selection_type") == selectionItem.getSelectionType()
									|| ((Integer)item.getData()) == selectionItem.getPrimaryID()) {
									item.dispose();
								}
							}
							
							Color color;
							float[] fArColor = null;

							if (selectionItem.getSelectionType() == ESelectionType.SELECTION) {
								fArColor = GeneralRenderStyle.SELECTED_COLOR;
							}
							else if (selectionItem.getSelectionType() == ESelectionType.MOUSE_OVER) {
								fArColor = GeneralRenderStyle.MOUSE_OVER_COLOR;
							}

							color = new Color(parentComposite.getDisplay(), (int) (fArColor[0] * 255),
									(int) (fArColor[1] * 255), (int) (fArColor[2] * 255));

							// Retrieve current set 
							// FIXME: This solution is not robust if new data are loaded -> REDESIGN
							ISet geneExpressionSet = null;
							Collection<ISet> sets = GeneralManager.get().getSetManager().getAllItems();
							int iSetCount = 0;
							for (ISet set : sets) {
								if (set.getSetType() == ESetType.GENE_EXPRESSION_DATA) {
									iSetCount++;
									geneExpressionSet = set;
								}
							}
							
							TreeItem item = new TreeItem(experimentTree, SWT.NONE);
							item.setText(geneExpressionSet.get(selectionItem.getPrimaryID()).getLabel());
							item.setData(selectionItem.getPrimaryID());
//							item.setData("mapping_type", EMappingType.EXPERIMENT_2_EXPERIMENT_INDEX.toString());
							item.setData("selection_type", selectionItem.getSelectionType());
							item.setBackground(color);
							
							experimentTree.setExpanded(true);
							
							// addGlyphInfo(selectionItem, item);
						}
					}
				}
			}
		});
	}

	// private void addGlyphInfo(SelectionDeltaItem selectionItem, TreeItem item) {
	// GlyphEntry entry = glyphManager.getGlyphs().get(selectionItem.getPrimaryID());
	//
	// if (entry == null)
	// return;
	//
	// for (int i = 0; i < entry.getNumberOfParameters(); ++i) {
	// String info =
	// glyphManager.getGlyphAttributeInfoStringWithInternalColumnNumber(i, entry.getParameter(i));
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

	private void handleVAUpdate(final IUniqueObject eventTrigger, final IVirtualArrayDelta delta) {
		if (delta.getIDType() != EIDType.REFSEQ_MRNA_INT)
			return;

		if (parentComposite.isDisposed())
			return;

		parentComposite.getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (!(eventTrigger instanceof AGLEventListener))
					return;

				lblViewInfoContent.setText(((AGLEventListener) eventTrigger).getShortInfo());

//				for (VADeltaItem item : delta) {
//					if (item.getType() == EVAOperation.REMOVE_ELEMENT) {
//						// Flush old items that become deselected/normal
//						for (TreeItem tmpItem : selectionTree.getItems()) {
//							if (((Integer) tmpItem.getData()).intValue() == item.getPrimaryID()) {
//								tmpItem.dispose();
//							}
//						}
//					}
//				}
			}
		});
	}

	// protected ISelectionDelta getSelectionDelta()
	// {
	// return selectionDelta;
	// }

	protected AGLEventListener getUpdateTriggeringView() {
		return updateTriggeringView;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void handleExternalEvent(IUniqueObject eventTrigger, IEventContainer eventContainer,
		EMediatorType eMediatorType) {
		switch (eventContainer.getEventType()) {
			case SELECTION_UPDATE:
				DeltaEventContainer<ISelectionDelta> selectionDeltaEventContainer =
					(DeltaEventContainer<ISelectionDelta>) eventContainer;
				handleSelectionUpdate(eventTrigger, selectionDeltaEventContainer.getSelectionDelta());
				break;
			case VA_UPDATE:
				DeltaEventContainer<IVirtualArrayDelta> vaDeltaEventContainer =
					(DeltaEventContainer<IVirtualArrayDelta>) eventContainer;
				handleVAUpdate(eventTrigger, vaDeltaEventContainer.getSelectionDelta());
				break;
			case TRIGGER_SELECTION_COMMAND:
				final SelectionCommandEventContainer commandEventContainer =
					(SelectionCommandEventContainer) eventContainer;
				switch (commandEventContainer.getIDType()) {
					case DAVID:
					case REFSEQ_MRNA_INT:
					case EXPRESSION_INDEX:

						if (parentComposite.isDisposed())
							return;

						parentComposite.getDisplay().asyncExec(new Runnable() {
							public void run() {
								ESelectionCommandType cmdType;
								for (SelectionCommand cmd : commandEventContainer.getSelectionCommands()) {
									cmdType = cmd.getSelectionCommandType();
									if (cmdType == ESelectionCommandType.RESET
										|| cmdType == ESelectionCommandType.CLEAR_ALL) {
										selectionTree.removeAll();
										break;
									}
									else if (cmdType == ESelectionCommandType.CLEAR) {
										// Flush old items that become
										// deselected/normal
										for (TreeItem tmpItem : selectionTree.getItems()) {
											if (tmpItem.getData("selection_type") == cmd.getSelectionType()) {
												tmpItem.dispose();
											}
										}
									}
								}
							}
						});

						break;
					case EXPERIMENT_INDEX:

						break;
				}
				break;
			case INFO_AREA_UPDATE:
				InfoAreaUpdateEventContainer infoEventContainer =
					(InfoAreaUpdateEventContainer) eventContainer;
				AGLEventListener view =
					GeneralManager.get().getViewGLCanvasManager().getGLEventListener(
						infoEventContainer.getViewID());

				shortInfo = view.getShortInfo();
				parentComposite.getDisplay().asyncExec(new Runnable() {
					public void run() {
						lblViewInfoContent.setText(shortInfo);
					}
				});
				break;
		}
	}
}
