package org.caleydo.rcp.util.info;

import org.caleydo.core.data.IUniqueObject;
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
import org.caleydo.core.manager.event.EMediatorType;
import org.caleydo.core.manager.event.IEventContainer;
import org.caleydo.core.manager.event.IMediatorReceiver;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.rcp.views.swt.HTMLBrowserView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;

/**
 * 
 * Info area that is located in the sidebar. It shows the current view and the
 * current selection (in a tree).
 * 
 * @author Marc Streit
 * 
 */
public class InfoArea
	implements IMediatorReceiver
{
	private Label lblViewInfoContent;
	
	private Tree selectionTree;
	
	private AGLEventListener updateTriggeringView;
	private Composite parentComposite;
	
	/**
	 * Constructor.
	 */
	public InfoArea()
	{
		GeneralManager.get().getEventPublisher().addReceiver(EMediatorType.SELECTION_MEDIATOR,
				this);
		GeneralManager.get().getEventPublisher().addReceiver(EMediatorType.VIEW_SELECTION,
				this);
	}

	public Control createControl(final Composite parent)
	{
		Font font = new Font(parent.getDisplay(), "Arial", 10, SWT.BOLD);

		parentComposite = parent;

		Label lblViewInfo = new Label(parent, SWT.NONE);
		lblViewInfo.setText("View Info");
		lblViewInfo.setFont(font);

		GridData data = new GridData();
		data.heightHint = 15;
		lblViewInfo.setLayoutData(data);

		lblViewInfoContent = new Label(parent, SWT.WRAP);
		lblViewInfoContent.setText("");

		data = new GridData();
		data.heightHint = 55;
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = SWT.FILL;
		lblViewInfoContent.setLayoutData(data);

		Label lblDetailInfo = new Label(parent, SWT.NO_BACKGROUND);
		lblDetailInfo.setText("Selection Info");
		lblDetailInfo.setFont(font);

		data = new GridData();
		data.heightHint = 15;
		lblDetailInfo.setLayoutData(data);

		selectionTree = new Tree(parent, SWT.BORDER);
//		selectionTree.setItemCount(2);
		selectionTree.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				super.widgetSelected(e);

				((HTMLBrowserView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.findView(HTMLBrowserView.ID)).getHTMLBrowserViewRep().setUrl("bla");
			}
		});

		data = new GridData();
		data.heightHint = 80;
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = SWT.FILL;
		selectionTree.setLayoutData(data);

		return parent;
	}

	private void handleSelectionUpdate(final IUniqueObject eventTrigger,
			final ISelectionDelta selectionDelta)
	{
		if (selectionDelta.getIDType() != EIDType.REFSEQ_MRNA_INT)
			return;

		parentComposite.getDisplay().asyncExec(new Runnable()
		{
			public void run()
			{
				if (!(eventTrigger instanceof AGLEventListener))
					return;

				lblViewInfoContent.setText(((AGLEventListener) eventTrigger).getShortInfo());

				for (SelectionDeltaItem selectionItem : selectionDelta)
				{
					if (selectionItem.getSelectionType() == ESelectionType.NORMAL
							|| selectionItem.getSelectionType() == ESelectionType.DESELECTED)
					{
						// Flush old items that become deselected/normal
						for (TreeItem tmpItem : selectionTree.getItems())
						{
							if (tmpItem.getData() == null 
									|| ((Integer)tmpItem.getData()).intValue() == selectionItem.getPrimaryID())
							{
								tmpItem.dispose();
							}
						}	
					}
					else if (selectionItem.getSelectionType() == ESelectionType.MOUSE_OVER
							|| selectionItem.getSelectionType() == ESelectionType.SELECTION)
					{
						Color color;
						float[] fArColor = null;
						
						if(selectionItem.getSelectionType() == ESelectionType.SELECTION)
							fArColor = GeneralRenderStyle.SELECTED_COLOR;
						else if (selectionItem.getSelectionType() == ESelectionType.MOUSE_OVER)
							fArColor = GeneralRenderStyle.MOUSE_OVER_COLOR;
							
						color = new Color(parentComposite.getDisplay(), (int)(fArColor[0] * 255), 
								(int)(fArColor[1] * 255), (int)(fArColor[2] * 255));
						
						String sRefSeqID = GeneralManager.get().getIDMappingManager().getID(
								EMappingType.REFSEQ_MRNA_INT_2_REFSEQ_MRNA,
								selectionItem.getPrimaryID());

						Integer iDavidID = GeneralManager.get().getIDMappingManager().getID(
								EMappingType.REFSEQ_MRNA_INT_2_DAVID,
								selectionItem.getPrimaryID());

						String sGeneSymbol = GeneralManager.get().getIDMappingManager().getID(
										EMappingType.DAVID_2_GENE_SYMBOL, iDavidID);

						boolean bIsExisting = false;
						for (TreeItem existingItem : selectionTree.getItems())
						{
							if (existingItem.getText().equals(sGeneSymbol) 
									&& ((Integer)existingItem.getData()).intValue() == selectionItem.getPrimaryID())
							{
								existingItem.setBackground(color);
								existingItem.getItem(0).setBackground(color);
								existingItem.setData("selection_type", selectionItem.getSelectionType());
								bIsExisting = true;
								break;
							}
						}
						
						if (!bIsExisting)
						{
							TreeItem item = new TreeItem(selectionTree, 0);
							item.setText(sGeneSymbol);
							item.setBackground(color);
							item.setData(selectionItem.getPrimaryID());
							item.setData("selection_type", selectionItem.getSelectionType());
							
							TreeItem subItem = new TreeItem(item, 0);
							subItem.setText(sRefSeqID);
							subItem.setBackground(color);
							item.setExpanded(true);
						}
					}
				}
			}
		});
	}

	private void handleVAUpdate(final IUniqueObject eventTrigger, final IVirtualArrayDelta delta)
	{
		if (delta.getIDType() != EIDType.REFSEQ_MRNA_INT)
			return;
		
		if (parentComposite.isDisposed())
			return;
		
		parentComposite.getDisplay().asyncExec(new Runnable()
		{
			public void run()
			{
				if (!(eventTrigger instanceof AGLEventListener))
					return;

				lblViewInfoContent.setText(((AGLEventListener) eventTrigger).getShortInfo());

				for (VADeltaItem item : delta)
				{
					if (item.getType() == EVAOperation.REMOVE_ELEMENT)
					{
						// Flush old items that become deselected/normal
						for (TreeItem tmpItem : selectionTree.getItems())
						{
							if (((Integer)tmpItem.getData()).intValue() == item.getPrimaryID())
							{
								tmpItem.dispose();
							}
						}	
					}
				}
			}
		});
	}
	
//	protected ISelectionDelta getSelectionDelta()
//	{
//		return selectionDelta;
//	}

	protected AGLEventListener getUpdateTriggeringView()
	{
		return updateTriggeringView;
	}

	@Override
	public void handleExternalEvent(IUniqueObject eventTrigger, IEventContainer eventContainer)
	{
		switch (eventContainer.getEventType())
		{
			case SELECTION_UPDATE:
				DeltaEventContainer<ISelectionDelta> selectionDeltaEventContainer = (DeltaEventContainer<ISelectionDelta>) eventContainer;
				handleSelectionUpdate(eventTrigger, selectionDeltaEventContainer
						.getSelectionDelta());
				break;
			case VA_UPDATE:
				DeltaEventContainer<IVirtualArrayDelta> vaDeltaEventContainer = (DeltaEventContainer<IVirtualArrayDelta>) eventContainer;
				handleVAUpdate(eventTrigger, vaDeltaEventContainer.getSelectionDelta());
				break;
			case TRIGGER_SELECTION_COMMAND:
				final SelectionCommandEventContainer commandEventContainer = (SelectionCommandEventContainer) eventContainer;
				switch (commandEventContainer.getIDType())
				{
					case DAVID:
					case REFSEQ_MRNA_INT:
					case EXPRESSION_INDEX:
						
						if (parentComposite.isDisposed())
							return;
						
						parentComposite.getDisplay().asyncExec(new Runnable()
						{
							public void run()
							{
								ESelectionCommandType cmdType;
								for (SelectionCommand cmd : commandEventContainer.getSelectionCommands())
								{
									cmdType = cmd.getSelectionCommandType();
									if(cmdType == ESelectionCommandType.RESET 
											|| cmdType == ESelectionCommandType.CLEAR_ALL)
									{
										selectionTree.removeAll();
										break;
									}	
									else if (cmdType == ESelectionCommandType.CLEAR)
									{
										// Flush old items that become deselected/normal
										for (TreeItem tmpItem : selectionTree.getItems())
										{
											if( tmpItem.getData("selection_type") == cmd.getSelectionType())
												tmpItem.dispose();
										}	
									}
								}
							}
						});
						
						break;
					case EXPERIMENT_INDEX:
//						storageSelectionManager.executeSelectionCommands(commandEventContainer
//								.getSelectionCommands());
//						break;
				}
		}
	}
}
