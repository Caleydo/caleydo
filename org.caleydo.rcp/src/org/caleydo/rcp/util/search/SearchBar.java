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
import org.eclipse.swt.layout.RowLayout;
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
		RowLayout rowLayout = new RowLayout();
		rowLayout.fill = true;
		// rowLayout.justify = true;
		// rowLayout.pack = true;
		rowLayout.type = SWT.VERTICAL;
		// rowLayout.wrap = false;
		composite.setLayout(rowLayout);

		Composite searchInputComposite = new Composite(composite, SWT.NONE);
		searchInputComposite.setLayout(new GridLayout(4, false));
		searchInputComposite.setSize(400, 20);

		Label searchInputLabel = new Label(searchInputComposite, SWT.NULL);
		searchInputLabel.setText("Pathway search:");
		searchInputLabel.pack();
		searchBox = new SearchBox(searchInputComposite, SWT.NONE);

		String items[] = { "No pathways available!                                                      " };
		searchBox.setItems(items);
		searchBox.addFocusListener(new FocusAdapter()
		{
			public void focusGained(FocusEvent e)
			{
				Collection<PathwayGraph> allPathways = GeneralManager.get().getPathwayManager().getAllItems();
				String[] sArSearchItems = new String[allPathways.size()];
				int iIndex = 0;
				for(PathwayGraph pathway : allPathways)
				{
					sArSearchItems[iIndex] = pathway.getTitle() + " ("
						+ pathway.getType().toString() + ")";
					iIndex++;
				}

				searchBox.setItems(sArSearchItems);
//				searchBox.removeFocusListener(this);
			}
		});

		searchBox.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				String sSearchEntity = searchBox.getItem(searchBox.getSelectionIndex());
				sSearchEntity = sSearchEntity.substring(0, sSearchEntity.indexOf(" ("));

				GeneralManager.get().getViewGLCanvasManager().getDataEntitySearcher()
						.searchForEntity(sSearchEntity);
			}
		});

		// Gene search
		Label entitySearchLabel = new Label(searchInputComposite, SWT.NULL);
		entitySearchLabel.setText("Gene search:");

		geneSearchText = new Text(searchInputComposite, SWT.BORDER | SWT.SINGLE);
		geneSearchText.setLayoutData(new GridData(GridData.FILL_BOTH));
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
