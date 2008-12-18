package org.caleydo.rcp.util.search;

import java.util.Collection;

import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.manager.general.GeneralManager;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Search bar that allows searching for genes and pathways.
 * 
 * @author Marc Streit
 */
public class SearchBar
	extends ControlContribution
{
	private SearchBox searchBox;

	private Text geneSearchText;

	private final static int MAX_PATHWAY_TITLE_LENGTH = 65;
	
	/**
	 * Constructor.
	 */
	public SearchBar(String id)
	{
		super(id);
	}

	protected Control createControl(Composite parent)
	{

		Composite composite = new Composite(parent, SWT.NONE);
//		composite.setLayout(new FillLayout());
//		RowLayout rowLayout = new RowLayout();
//		rowLayout.fill = true;
////		// rowLayout.justify = true;
//		rowLayout.pack = true;
//		rowLayout.type = SWT.VERTICAL;
////		// rowLayout.wrap = false;
//		composite.setLayout(rowLayout);

		composite.setLayout(new GridLayout(4, false));
		composite.setSize(600, 20);

		Label searchInputLabel = new Label(composite, SWT.NULL);
		searchInputLabel.setText("Pathway search:");
		searchInputLabel.pack();
		searchBox = new SearchBox(composite, SWT.BORDER);

		String items[] = { "No pathways available!" };
		GridData data = new GridData();
		data.widthHint = 430;
		searchBox.setLayoutData(data);
		searchBox.setItems(items);
		searchBox.addFocusListener(new FocusAdapter()
		{
			public void focusGained(FocusEvent e)
			{
				Collection<PathwayGraph> allPathways = GeneralManager.get().getPathwayManager().getAllItems();
				String[] sArSearchItems = new String[allPathways.size()];
				int iIndex = 0;
				String sPathwayTitle = "";
				for(PathwayGraph pathway : allPathways)
				{
					sPathwayTitle = pathway.getTitle();
					
//					if (sPathwayTitle.length() > MAX_PATHWAY_TITLE_LENGTH)
//						sPathwayTitle = sPathwayTitle.substring(0, MAX_PATHWAY_TITLE_LENGTH) + "... ";
					
//					sArSearchItems[iIndex] = pathway.getType().toString() 
//						+ " - " + sPathwayTitle;
					
					sArSearchItems[iIndex] = sPathwayTitle + " ("
						+ pathway.getType().toString() + ")";
					iIndex++;
				}

				searchBox.setItems(sArSearchItems);
				searchBox.removeFocusListener(this);
			}
		});
		
		searchBox.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				String sSearchEntity = searchBox.getItem(searchBox.getSelectionIndex());
//				sSearchEntity = sSearchEntity.substring(0, sSearchEntity.indexOf(" ("));

				GeneralManager.get().getViewGLCanvasManager().getDataEntitySearcher()
						.searchForEntity(sSearchEntity);	
			}
		});

		// Gene search
		Label entitySearchLabel = new Label(composite, SWT.NULL);
		entitySearchLabel.setText("Gene search:");

		geneSearchText = new Text(composite, SWT.BORDER | SWT.SINGLE);
//		geneSearchText.setLayoutData(new GridData(GridData.FILL_BOTH));
		geneSearchText.addFocusListener(new FocusAdapter()
		{
			public void focusGained(FocusEvent e)
			{
				geneSearchText.setText("");
				geneSearchText.pack();
			}
		});
		
		geneSearchText.addKeyListener(new KeyAdapter()
		{
			public void keyPressed(KeyEvent event)
			{
				switch (event.keyCode)
				{
					case SWT.CR:
					{
						boolean bFound = GeneralManager.get().getViewGLCanvasManager()
								.getDataEntitySearcher().searchForEntity(
										geneSearchText.getText());

						if (!bFound)
						{
							geneSearchText.setText(" NOT FOUND! Try again...");
							// geneSearchText.setForeground(geneSearchText
							// .getDisplay().getSystemColor(SWT.COLOR_RED));
							geneSearchText.pack();
						}
					}
				}
			}
		});

		composite.pack();
		return composite;
	}
}
