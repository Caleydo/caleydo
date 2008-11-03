package org.caleydo.rcp.action.view.remote;

import java.util.Collection;

import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.rcp.action.view.AToolBarAction;
import org.caleydo.rcp.util.search.SearchBox;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/**
 * Search bar in an own shell.
 * This is just a workaround for the WinXP classic style layout problem of the toolbar.
 * Currently not in use.
 *
 * @author Marc Streit
 */
public class SearchAction
extends AToolBarAction
{
	public static final String TEXT = "Search for pathway or gene";
	public static final String ICON = "resources/icons/view/remote/search.png";

	private SearchBox searchBox;
	private Text geneSearchText;
	private final SearchAction searchAction;
	
	
	/**
	 * Constructor.
	 */
	public SearchAction(int iViewID)
	{
		super(iViewID);
		
		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromURL(this.getClass()
				.getClassLoader().getResource(ICON)));
		setChecked(false);
		
		this.searchAction = this;
	}
	
	@Override
	public void run()
	{
		super.run();
			
		Shell shell = new Shell(PlatformUI.getWorkbench().getDisplay(), SWT.SHELL_TRIM);
		shell.setLayout(new GridLayout(2, false));
		shell.setText(TEXT);
		shell.setImage(new Image(shell.getDisplay(), 
				"resources/icons/view/remote/search.png"));
		shell.setActive();
		
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e)
			{
				super.shellClosed(e);
				
				searchAction.setEnabled(true);
				searchAction.setChecked(false);
			}
		});
		
		Label searchInputLabel = new Label(shell, SWT.NULL);
		searchInputLabel.setText("Pathway search:");
		searchInputLabel.pack();
		searchBox = new SearchBox(shell, SWT.BORDER);

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
		Label entitySearchLabel = new Label(shell, SWT.NULL);
		entitySearchLabel.setText("Gene search:");

		geneSearchText = new Text(shell, SWT.BORDER | SWT.SINGLE);
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
		
		this.setEnabled(false);
		
		shell.pack();
		shell.open();
	}
}
