package org.caleydo.rcp.util.info;

import java.util.Collection;
import java.util.Set;

import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.data.selection.IVirtualArrayDelta;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionDeltaItem;
import org.caleydo.core.manager.event.EMediatorType;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

public class InfoArea
//	extends WorkbenchWindowControlContribution
	implements IMediatorReceiver
{
	private ToolTip viewInfoToolTip;
	private ToolTip detailInfoToolTip;

	private Text txtViewInfo;
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

//	@Override
	public Control createControl(final Composite parent)
	{
		Font font = new Font(parent.getDisplay(), "Arial", 10, SWT.BOLD);

		Composite composite = new Composite(parent, SWT.NONE);
		parentComposite = parent;

		Group group = new Group(composite, SWT.NULL);		
		group.setLayout(new RowLayout(SWT.VERTICAL));
		group.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		
		Label lblViewInfo = new Label(group, SWT.NONE);
		lblViewInfo.setText("View Info");
		lblViewInfo.setFont(font);
		lblViewInfo.setLayoutData(new RowData(ToolBarView.TOOLBAR_WIDTH, 15));
		lblViewInfo.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		txtViewInfo = new Text(group, SWT.WRAP);
		txtViewInfo.setText("");
		txtViewInfo.setBackground(parent.getDisplay().getSystemColor(
				SWT.COLOR_WIDGET_BACKGROUND));
		txtViewInfo.setEditable(false);
		txtViewInfo.setLayoutData(new RowData(ToolBarView.TOOLBAR_WIDTH, 30));
		txtViewInfo.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_WHITE));

//		viewInfoToolTip = new ToolTip(txtViewInfo, "No info available!", this,
//				EInfoType.VIEW_INFO);
		
		new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);

		Label lblDetailInfo = new Label(group, SWT.NO_BACKGROUND);
		lblDetailInfo.setText("Selection Info");
		lblDetailInfo.setFont(font);
		lblDetailInfo.setLayoutData(new RowData(ToolBarView.TOOLBAR_WIDTH, 15));
		lblDetailInfo.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_WHITE));

//		txtDetailedInfo = new Text(group, SWT.NONE);
//		txtDetailedInfo.setText("");
//		txtDetailedInfo.setBackground(parent.getDisplay().getSystemColor(
//				SWT.COLOR_WIDGET_BACKGROUND));
//		txtDetailedInfo.setEditable(false);
//		txtDetailedInfo.setLayoutData(new RowData(200, 15));
//
//		detailInfoToolTip = new ToolTip(txtDetailedInfo, "No info available!", this,
//				EInfoType.DETAILED_INFO);

		GridData data = new GridData();
		data.widthHint = ToolBarView.TOOLBAR_WIDTH;

		selectionList = new List(group, SWT.SINGLE);
		selectionList.setLayoutData(new RowData(((int)(ToolBarView.TOOLBAR_WIDTH*1.05f)), 150));

		selectionList.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
//				String sURL = urlGenerator.createURL(eBrowserQueryType, iAlDavidID.get(list
//						.getSelectionIndex()));
//
//				browser.setUrl(sURL);
//				browser.update();
//				textURL.setText(sURL);
			}
		});
		
		group.pack();
		
		return composite;
	}

	@Override
	public void handleSelectionUpdate(final IUniqueObject eventTrigger, final ISelectionDelta selectionDelta,
			Collection<SelectionCommand> colSelectionCommand, EMediatorType eMediatorType)
	{
		if (selectionDelta.getIDType() != EIDType.DAVID)
			return;
		
		parentComposite.getDisplay().asyncExec(new Runnable()
		{
			public void run()
			{
				txtViewInfo.setText(((AGLEventListener) eventTrigger).getShortInfo());
				
				((ToolBarView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(
						ToolBarView.ID)).addViewSpecificToolBar(eventTrigger.getID());
				
				((ToolBarView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(
						ToolBarView.ID)).highlightViewSpecificToolBar(eventTrigger.getID());
				
				int iItemsToLoad = 0;
				// SelectionItem selectionItem;
				
				for (SelectionDeltaItem selectionItem : selectionDelta)
				{
					if (selectionItem.getSelectionType() == ESelectionType.MOUSE_OVER
							|| selectionItem.getSelectionType() == ESelectionType.SELECTION)
					{
				//				Collection<SelectionCommand> iAlDavidID;
						if (iItemsToLoad == 0)
						{
				//						String sURL = urlGenerator.createURL(eBrowserQueryType,
				//								selectionItem.getSelectionID());
				//
				//						browser.setUrl(sURL);
				//						browser.update();
				//						textURL.setText(sURL);
				
				//					iAlDavidID.clear();
							selectionList.removeAll();
						}
				
						Set<String> sSetRefSeqID = GeneralManager.get().getIDMappingManager()
								.getMultiID(EMappingType.DAVID_2_REFSEQ_MRNA,
										selectionItem.getPrimaryID());
				
						String sOutput = "";
						sOutput = sOutput
								+ GeneralManager.get().getIDMappingManager().getID(
										EMappingType.DAVID_2_GENE_SYMBOL,
										selectionItem.getPrimaryID());
				
						for (String sRefSeqID : sSetRefSeqID)
						{
							sOutput = sOutput + "\n";
							sOutput = sOutput + sRefSeqID;
						}
				
				//				if (iAlDavidID.contains(selectionItem.getSelectionID()))
				//					continue;
				
						selectionList.add(sOutput);
				//					iAlDavidID.add(selectionItem.getSelectionID());
				
						iItemsToLoad++;
					}
				
					selectionList.redraw();
					selectionList.setSelection(0);
				}
			}			
		});
	}
	
//	@Override
//	public void handleUpdate(final IUniqueObject eventTrigger,
//			final ISelectionDelta selectionDelta,
//			Collection<SelectionCommand> colSelectionCommand, EMediatorType eMediatorType)
//	{
//		if (!(eventTrigger instanceof AGLEventListener))
//			return;
//
//		GeneralManager.get().getLogger().log(
//				Level.INFO,
//				"Update called by " + eventTrigger.getClass().getSimpleName()
//						+ ", received in: " + this.getClass().getSimpleName());
//
//		updateTriggeringView = (AGLEventListener) eventTrigger;
//
//		if (!selectionDelta.getSelectionData().isEmpty())
//			this.selectionDelta = selectionDelta;
//
//		txtViewInfo.getDisplay().asyncExec(new Runnable()
//		{
//			public void run()
//			{
//				txtViewInfo.setText(((AGLEventListener) eventTrigger).getShortInfo());
//
//				String sDetailText = "";
//
//				EIDType eIDType = selectionDelta.getIDType();
//
//				String sGeneSymbol = "";
//
//				Iterator<SelectionItem> iterSelectionItems = selectionDelta.getSelectionData()
//						.iterator();
//
//				SelectionItem item;
//
//				GlyphManager gman = (GlyphManager) GeneralManager.get().getGlyphManager();
//
//				while (iterSelectionItems.hasNext())
//				{
//					item = iterSelectionItems.next();
//
//					if (item.getSelectionType() == ESelectionType.MOUSE_OVER
//							|| item.getSelectionType() == ESelectionType.SELECTION)
//					{
//						if (eIDType == EIDType.DAVID)
//						{
//
//							Set<String> sSetRefSeqID = GeneralManager.get()
//									.getIDMappingManager().getMultiID(
//											EMappingType.DAVID_2_REFSEQ_MRNA,
//											item.getSelectionID());
//
//							if (sSetRefSeqID == null)
//								continue;
//							
//							sGeneSymbol = sDetailText
//									+ GeneralManager.get().getIDMappingManager().getID(
//											EMappingType.DAVID_2_GENE_SYMBOL,
//											item.getSelectionID());
//
//							sDetailText = sDetailText + sGeneSymbol + " (";
//							for (String sRefSeqID : sSetRefSeqID)
//							{
//								sDetailText = sDetailText + sRefSeqID;
//								sDetailText = sDetailText + ", ";
//							}
//
//							// Remove last comma
//							sDetailText = sDetailText.substring(0, sDetailText.length() - 2);
//							sDetailText += ")";
//
//						}
//						else if (eIDType == EIDType.EXPERIMENT_INDEX)
//						{
//							GlyphEntry glyph = gman.getGlyphs().get(item.getSelectionID());
//
//							if (glyph != null)
//								sDetailText = glyph.getGlyphDescription("; ");
//							else
//								sDetailText = "glyph not found";
//						}
//						else
//						{
//							continue;
//						}
//
//						if (iterSelectionItems.hasNext())
//							sDetailText = sDetailText + ", ";
//					}
//
//					// Remove last comma
//					if (sDetailText.length() > 2)
//						sDetailText = sDetailText.substring(0, sDetailText.length() - 1);
//				}
//
//				// Prevent to reset info when view info updates
//				// TODO: think about better way!
//				if (!sDetailText.isEmpty())
//					txtDetailedInfo.setText(sDetailText);
//			}
//		});
//	}

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
	public void handleVAUpdate(EMediatorType mediatorType, IUniqueObject eventTrigger,
			IVirtualArrayDelta delta, Collection<SelectionCommand> colSelectionCommand)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleExternalEvent(IUniqueObject eventTrigger, IEventContainer eventContainer)
	{
		// TODO Auto-generated method stub
		
	}
}
