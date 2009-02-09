package org.caleydo.rcp.util.info;

import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.data.selection.DeltaEventContainer;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.data.selection.SelectionDeltaItem;
import org.caleydo.core.manager.event.IEventContainer;
import org.caleydo.core.manager.event.IMediatorReceiver;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.rcp.views.swt.ToolBarView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

public class InfoArea
	// extends WorkbenchWindowControlContribution
	implements IMediatorReceiver
{
	private ToolTip viewInfoToolTip;
	private ToolTip detailInfoToolTip;

	private Label lblViewInfoContent;
	private Text txtDetailedInfo;

	private AGLEventListener updateTriggeringView;
	private ISelectionDelta selectionDelta;

	private List selectionList;

	private Composite parentComposite;

	// TODO: bad hack, but how can I access this class during runtime?
	private static InfoArea infoArea;

	public InfoArea()
	{
		infoArea = this;
	}

	// @Override
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
		data.heightHint = 15;
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = SWT.FILL;
		lblViewInfoContent.setLayoutData(data);
		
		Label lblDetailInfo = new Label(parent, SWT.NO_BACKGROUND);
		lblDetailInfo.setText("Selection Info");
		lblDetailInfo.setFont(font);

		data = new GridData();
		data.heightHint = 15;
		lblDetailInfo.setLayoutData(data);
		
		selectionList = new List(parent, SWT.SINGLE);
		data = new GridData();
		data.heightHint = 150;
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = SWT.FILL;
		selectionList.setLayoutData(data);
		
		selectionList.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// String sURL = urlGenerator.createURL(eBrowserQueryType,
				// iAlDavidID.get(list
				// .getSelectionIndex()));
				//
				// browser.setUrl(sURL);
				// browser.update();
				// textURL.setText(sURL);
			}
		});

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

//				((ToolBarView) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
//						.getActivePage().findView(ToolBarView.ID))
//						.addViewSpecificToolBar(eventTrigger.getID());

//				((ToolBarView) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
//						.getActivePage().findView(ToolBarView.ID))
//						.highlightViewSpecificToolBar(eventTrigger.getID());

				int iItemsToLoad = 0;
				// SelectionItem selectionItem;

				for (SelectionDeltaItem selectionItem : selectionDelta)
				{
					if (selectionItem.getSelectionType() == ESelectionType.MOUSE_OVER
							|| selectionItem.getSelectionType() == ESelectionType.SELECTION)
					{
						// Collection<Selechttp://orf.at/tionCommand> iAlDavidID;
						if (iItemsToLoad == 0)
						{
							// String sURL =
							// urlGenerator.createURL(eBrowserQueryType,
							// selectionItem.getSelectionID());
							//
							// browser.setUrl(sURL);
							// browser.update();
							// textURL.setText(sURL);

							// iAlDavidID.clear();
							selectionList.removeAll();
						}

						String sRefSeqID = GeneralManager.get().getIDMappingManager()
								.getID(EMappingType.REFSEQ_MRNA_INT_2_REFSEQ_MRNA,
										selectionItem.getPrimaryID());

						Integer iDavidID = GeneralManager.get().getIDMappingManager()
							.getID(EMappingType.REFSEQ_MRNA_INT_2_DAVID,
								selectionItem.getPrimaryID());
						
						String sOutput = "";
						sOutput = sOutput
								+ GeneralManager.get().getIDMappingManager().getID(
										EMappingType.DAVID_2_GENE_SYMBOL,
										iDavidID);

//						sOutput = sOutput + "\n";
						sOutput = sOutput + " - " +sRefSeqID;

						// if
						// (iAlDavidID.contains(selectionItem.getSelectionID()))
						// continue;

						selectionList.add(sOutput);
						// iAlDavidID.add(selectionItem.getSelectionID());

						iItemsToLoad++;
					}

					selectionList.redraw();
					selectionList.setSelection(0);
				}
			}
		});
	}

	// @Override
	// public void handleUpdate(final IUniqueObject eventTrigger,
	// final ISelectionDelta selectionDelta,
	// Collection<SelectionCommand> colSelectionCommand, EMediatorType
	// eMediatorType)
	// {
	// if (!(eventTrigger instanceof AGLEventListener))
	// return;
	//
	// GeneralManager.get().getLogger().log(
	// Level.INFO,
	// "Update called by " + eventTrigger.getClass().getSimpleName()
	// + ", received in: " + this.getClass().getSimpleName());
	//
	// updateTriggeringView = (AGLEventListener) eventTrigger;
	//
	// if (!selectionDelta.getSelectionData().isEmpty())
	// this.selectionDelta = selectionDelta;
	//
	// txtViewInfo.getDisplay().asyncExec(new Runnable()
	// {
	// public void run()
	// {
	// txtViewInfo.setText(((AGLEventListener) eventTrigger).getShortInfo());
	//
	// String sDetailText = "";
	//
	// EIDType eIDType = selectionDelta.getIDType();
	//
	// String sGeneSymbol = "";
	//
	// Iterator<SelectionItem> iterSelectionItems =
	// selectionDelta.getSelectionData()
	// .iterator();
	//
	// SelectionItem item;
	//
	// GlyphManager gman = (GlyphManager)
	// GeneralManager.get().getGlyphManager();
	//
	// while (iterSelectionItems.hasNext())
	// {
	// item = iterSelectionItems.next();
	//
	// if (item.getSelectionType() == ESelectionType.MOUSE_OVER
	// || item.getSelectionType() == ESelectionType.SELECTION)
	// {
	// if (eIDType == EIDType.DAVID)
	// {
	//
	// Set<String> sSetRefSeqID = GeneralManager.get()
	// .getIDMappingManager().getMultiID(
	// EMappingType.DAVID_2_REFSEQ_MRNA,
	// item.getSelectionID());
	//
	// if (sSetRefSeqID == null)
	// continue;
	//							
	// sGeneSymbol = sDetailText
	// + GeneralManager.get().getIDMappingManager().getID(
	// EMappingType.DAVID_2_GENE_SYMBOL,
	// item.getSelectionID());
	//
	// sDetailText = sDetailText + sGeneSymbol + " (";
	// for (String sRefSeqID : sSetRefSeqID)
	// {
	// sDetailText = sDetailText + sRefSeqID;
	// sDetailText = sDetailText + ", ";
	// }
	//
	// // Remove last comma
	// sDetailText = sDetailText.substring(0, sDetailText.length() - 2);
	// sDetailText += ")";
	//
	// }
	// else if (eIDType == EIDType.EXPERIMENT_INDEX)
	// {
	// GlyphEntry glyph = gman.getGlyphs().get(item.getSelectionID());
	//
	// if (glyph != null)
	// sDetailText = glyph.getGlyphDescription("; ");
	// else
	// sDetailText = "glyph not found";
	// }
	// else
	// {
	// continue;
	// }
	//
	// if (iterSelectionItems.hasNext())
	// sDetailText = sDetailText + ", ";
	// }
	//
	// // Remove last comma
	// if (sDetailText.length() > 2)
	// sDetailText = sDetailText.substring(0, sDetailText.length() - 1);
	// }
	//
	// // Prevent to reset info when view info updates
	// // TODO: think about better way!
	// if (!sDetailText.isEmpty())
	// txtDetailedInfo.setText(sDetailText);
	// }
	// });
	// }

	public static InfoArea getInfoArea()
	{
		return infoArea;
	}

	protected ISelectionDelta getSelectionDelta()
	{
		return selectionDelta;
	}

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
		}

	}
}
