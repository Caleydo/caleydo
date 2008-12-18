package org.caleydo.rcp.util.info;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionItem;
import org.caleydo.core.manager.event.mediator.EMediatorType;
import org.caleydo.core.manager.event.mediator.IMediatorReceiver;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.glyph.GlyphManager;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GlyphEntry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

public class InfoArea
	extends WorkbenchWindowControlContribution
	implements IMediatorReceiver
{
	private ToolTip viewInfoToolTip;
	private ToolTip detailInfoToolTip;

	private Text txtViewInfo;
	private Text txtDetailedInfo;

	private AGLEventListener updateTriggeringView;
	private ISelectionDelta selectionDelta;

	// TODO: bad hack, but how can I access this class during runtime?
	private static InfoArea infoArea;

	public InfoArea()
	{
		infoArea = this;
	}

	@Override
	protected Control createControl(final Composite parent)
	{
		Font font = new Font(parent.getDisplay(), "Arial", 10, SWT.BOLD);

		Composite composite = new Composite(parent, SWT.NONE);

		RowLayout rowLayout = new RowLayout();
		rowLayout.fill = false;
		rowLayout.justify = false;
		rowLayout.pack = true;
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.wrap = false;
		composite.setLayout(rowLayout);

		Label lblViewInfo = new Label(composite, SWT.CENTER);
		lblViewInfo.setText("View Info:");
		lblViewInfo.setFont(font);
		lblViewInfo.setLayoutData(new RowData(80, 15));

		txtViewInfo = new Text(composite, SWT.NONE);
		txtViewInfo.setText("");
		txtViewInfo.setBackground(parent.getDisplay().getSystemColor(
				SWT.COLOR_WIDGET_BACKGROUND));
		txtViewInfo.setEditable(false);
		txtViewInfo.setLayoutData(new RowData(400, 15));

		viewInfoToolTip = new ToolTip(txtViewInfo, "No info available!", this,
				EInfoType.VIEW_INFO);

		new Label(composite, SWT.SEPARATOR | SWT.VERTICAL);

		Label lblDetailInfo = new Label(composite, SWT.NO_BACKGROUND);
		lblDetailInfo.setText("Detail Info:");
		lblDetailInfo.setFont(font);
		lblDetailInfo.setLayoutData(new RowData(80, 15));

		txtDetailedInfo = new Text(composite, SWT.NONE);
		txtDetailedInfo.setText("");
		txtDetailedInfo.setBackground(parent.getDisplay().getSystemColor(
				SWT.COLOR_WIDGET_BACKGROUND));
		txtDetailedInfo.setEditable(false);
		txtDetailedInfo.setLayoutData(new RowData(400, 15));

		detailInfoToolTip = new ToolTip(txtDetailedInfo, "No info available!", this,
				EInfoType.DETAILED_INFO);

		return composite;
	}

	@Override
	public void handleUpdate(final IUniqueObject eventTrigger, final ISelectionDelta selectionDelta, Collection<SelectionCommand> colSelectionCommand, EMediatorType eMediatorType)
	{
		if (!(eventTrigger instanceof AGLEventListener))
			return;

		GeneralManager.get().getLogger().log(
				Level.INFO,
				"Update called by " + eventTrigger.getClass().getSimpleName()
						+ ", received in: " + this.getClass().getSimpleName());

		updateTriggeringView = (AGLEventListener) eventTrigger;
		
		if (!selectionDelta.getSelectionData().isEmpty())
			this.selectionDelta = selectionDelta;

		txtViewInfo.getDisplay().asyncExec(new Runnable()
		{
			public void run()
			{
				txtViewInfo.setText(((AGLEventListener) eventTrigger).getShortInfo());

				String sDetailText = "";

				EIDType eIDType = selectionDelta.getIDType();

				String sGeneSymbol = "";
				
				Iterator<SelectionItem> iterSelectionItems 
					= selectionDelta.getSelectionData().iterator();

				SelectionItem item;

				GlyphManager gman = (GlyphManager) GeneralManager.get().getGlyphManager();

				while (iterSelectionItems.hasNext())
				{
					item = iterSelectionItems.next();

					if (item.getSelectionType() == ESelectionType.MOUSE_OVER
							|| item.getSelectionType() == ESelectionType.SELECTION)
					{
						if (eIDType == EIDType.DAVID)
						{

							Set<String> sSetRefSeqID = GeneralManager.get().getIDMappingManager()
								.getMultiID(EMappingType.DAVID_2_REFSEQ_MRNA, item.getSelectionID());
							
							sGeneSymbol = sDetailText + GeneralManager.get().getIDMappingManager().getID(
									EMappingType.DAVID_2_GENE_SYMBOL, item.getSelectionID());
	
							sDetailText = sDetailText + sGeneSymbol + " ("; 
							for (String sRefSeqID : sSetRefSeqID) 
							{
								sDetailText = sDetailText + sRefSeqID;
								sDetailText = sDetailText + ", ";
							}
							
							// Remove last comma
							sDetailText = sDetailText.substring(0, sDetailText.length() -2);						
							sDetailText += ")";

						}
						else if (eIDType == EIDType.EXPERIMENT_INDEX)
						{
							GlyphEntry glyph = gman.getGlyphs().get(item.getSelectionID());

							if (glyph != null)
								sDetailText = glyph.getGlyphDescription("; ");
							else
								sDetailText = "glyph not found";
						}
						else
						{
							continue;
						}

						if (iterSelectionItems.hasNext())
							sDetailText = sDetailText + ", ";
					}
					
					// Remove last comma
					if (sDetailText.length() > 2)
						sDetailText = sDetailText.substring(0, sDetailText.length() -1);
				}
			
				// Prevent to reset info when view info updates
				// TODO: think about better way!
				if (!sDetailText.isEmpty())
					txtDetailedInfo.setText(sDetailText);
			}
		});
	}

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
}
