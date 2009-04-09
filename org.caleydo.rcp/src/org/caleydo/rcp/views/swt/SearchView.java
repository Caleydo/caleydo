package org.caleydo.rcp.views.swt;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.event.EMediatorType;
import org.caleydo.core.manager.event.IEventContainer;
import org.caleydo.core.manager.event.IMediatorReceiver;
import org.caleydo.core.manager.general.GeneralManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.ViewPart;

/**
 * Search view contains gene and pathway search.
 * 
 * @author Marc Streit
 */
public class SearchView
	extends ViewPart
	implements IMediatorReceiver { //, ISizeProvider {
	public static final String ID = "org.caleydo.rcp.views.swt.SearchView";

	public static boolean bHorizontal = false;
	
	private TreeItem pathwayTree;
	private TreeItem geneTree;
	
	private IGeneralManager generalManager;

	@Override
	public void createPartControl(Composite parent) {
		
		generalManager = GeneralManager.get();
		generalManager.getEventPublisher().addReceiver(EMediatorType.SELECTION_MEDIATOR, this);

		final Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(1, false));

//		GridLayout layout = new GridLayout(1, false);
//		layout.marginBottom =
//			layout.marginTop =
//				layout.marginLeft =
//					layout.marginRight = layout.horizontalSpacing = layout.verticalSpacing = 0;
//		layout.marginHeight = layout.marginWidth = 3;
//		composite.setLayout(layout);

		// Trigger gene/pathway search command
//		CmdViewCreateDataEntitySearcher cmd =
//			(CmdViewCreateDataEntitySearcher) GeneralManager.get().getCommandManager().createCommandByType(
//				ECommandType.CREATE_VIEW_DATA_ENTITY_SEARCHER);
//		cmd.doCommand();
//		dataEntitySearcher = cmd.getCreatedObject();

		Label entitySearchLabel = new Label(composite, SWT.NULL);
		entitySearchLabel.setText("Search query:");

		final Text searchText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		searchText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				searchText.setText("");
				// geneSearchText.pack();
			}
		});

//		searchText.addKeyListener(new KeyAdapter() {
//			@Override
//			public void keyPressed(KeyEvent event) {
//				switch (event.keyCode) {
//					case SWT.CR: {
//						boolean bFound = dataEntitySearcher.searchForEntity(searchText.getText());
//
//						if (!bFound) {
//							searchText.setText(" NOT FOUND! Try again...");
//							// geneSearchText.setForeground(geneSearchText
//							// .getDisplay().getSystemColor(SWT.COLOR_RED));
//							// geneSearchText.pack();
//						}
//					}
//				}
//			}
//		});
		
		Button searchButton = new Button(composite, SWT.PUSH);
		searchButton.setText("Search");
		
		Label resultLabel = new Label(composite, SWT.NULL);
		resultLabel.setText("Results:");
		
		Tree selectionTree = new Tree(composite, SWT.NULL);
		selectionTree.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		geneTree = new TreeItem(selectionTree, SWT.NONE);
		geneTree.setText("Genes");
		geneTree.setExpanded(true);
//		TreeItem experimentTree = new TreeItem(selectionTree, SWT.NONE);
//		experimentTree.setText("Experiments");
//		experimentTree.setExpanded(true);
//		experimentTree.setData(-1);
		pathwayTree = new TreeItem(selectionTree, SWT.NONE);
		pathwayTree.setText("Pathways");
		pathwayTree.setExpanded(false);
		pathwayTree.setData(-1);
		
		searchButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				searchForPathway(searchText.getText());
				searchForGene(searchText.getText());
			}	
		});
	}

	private void searchForPathway(String sSearchQuery) {

		// Flush old pathway results
		for (TreeItem item : pathwayTree.getItems())
			item.dispose();

		Pattern pattern = Pattern.compile(sSearchQuery, Pattern.CASE_INSENSITIVE);
		Matcher regexMatcher;

		// Search for pathways
		for (PathwayGraph pathway : generalManager.getPathwayManager().getAllItems())
		{
			regexMatcher = pattern.matcher(pathway.getTitle());
			if (regexMatcher.find())
			{
				TreeItem item = new TreeItem(pathwayTree, SWT.NULL);
				item.setText(pathway.getTitle());
			}
		}
		
		if (pathwayTree.getItemCount() > 0)
			pathwayTree.setExpanded(true);
	}

//	private int searchForRefSeq(final String sSearchQuery) {
//		
//		Integer iRefSeqID =
//			GeneralManager.get().getIDMappingManager().getID(EMappingType.REFSEQ_MRNA_2_REFSEQ_MRNA_INT,
//				sSearchQuery.toUpperCase());
//
//		if (iRefSeqID == null)
//			return -1;
//
//		return iRefSeqID;
//	}

//	private int searchForNCBIGeneId(final String sSearchQuery) {
//		
//		Integer iNCBIGeneID = 0;
//		try {
//			iNCBIGeneID = Integer.valueOf(sSearchQuery);
//		}
//		catch (NumberFormatException nfe) {
//			return -1;
//		}
//
//		Integer iDavidID =
//			GeneralManager.get().getIDMappingManager().getID(EMappingType.ENTREZ_GENE_ID_2_DAVID, iNCBIGeneID);
//
//		if (iDavidID == null)
//			return -1;
//
//		return iDavidID;
//	}

	private void searchForGene(final String sSearchQuery) {
		
		// Flush old pathway results
		for (TreeItem item : geneTree.getItems())
			item.dispose();
		
		Pattern pattern = Pattern.compile(sSearchQuery, Pattern.CASE_INSENSITIVE);
		Matcher regexMatcher;
		ArrayList<Integer> iArDavidGeneResults = new ArrayList<Integer>();
		
		for (Object sGeneSymbol : generalManager.getIDMappingManager().getMapping(EMappingType.GENE_SYMBOL_2_DAVID).keySet())
		{
			regexMatcher = pattern.matcher((String)sGeneSymbol);
			if (regexMatcher.find())
				iArDavidGeneResults.add((Integer)generalManager.getIDMappingManager().getID(EMappingType.GENE_SYMBOL_2_DAVID, sGeneSymbol));
		}

		// Fill results in list
		for (Integer iDavidID : iArDavidGeneResults)
		{
			String sGeneSymbol = generalManager.getIDMappingManager().getID(EMappingType.DAVID_2_GENE_SYMBOL, iDavidID);
			
			if (sGeneSymbol == null)
				continue;
			
			TreeItem item = new TreeItem(geneTree, SWT.NULL);
			item.setText(sGeneSymbol);			
		}
		
		if (geneTree.getItemCount() > 0)
			geneTree.setExpanded(true);
	}
	
	@Override
	public void setFocus() {

	}

	@Override
	public void dispose() {
		super.dispose();

		GeneralManager.get().getEventPublisher().removeReceiver(EMediatorType.SELECTION_MEDIATOR, this);
	}

	// TODO: remove after commit of Werner
	public void updateSearchBar(boolean bIsVisible) {
//		pathwaySearchBox.setVisible(bIsVisible);
//		pathwaySearchLabel.setVisible(bIsVisible);
//		parentComposite.layout();
	}

	@Override
	public void handleExternalEvent(final IUniqueObject eventTrigger, IEventContainer eventContainer,
		EMediatorType eMediatorType) {

	}

//	@Override
//	public int computePreferredSize(boolean width, int availableParallel, int availablePerpendicular,
//		int preferredResult) {
//		// Set minimum size of the view
//		if (width == true)
//			return (int) SearchView.TOOLBAR_WIDTH;
//
//		return (int) SearchView.TOOLBAR_HEIGHT;
//	}

//	@Override
//	public int getSizeFlags(boolean width) {
//		return SWT.MIN;
//	}
}
