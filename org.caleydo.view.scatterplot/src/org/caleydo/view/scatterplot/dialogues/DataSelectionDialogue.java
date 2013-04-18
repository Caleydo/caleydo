/**
 * 
 */
package org.caleydo.view.scatterplot.dialogues;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.perspective.table.EStatisticsType;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.view.scatterplot.utils.EDataGenerationType;
import org.caleydo.view.scatterplot.utils.EVisualizationSpaceType;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * @author cturkay
 *
 */
public class DataSelectionDialogue extends TitleAreaDialog {

	
	private TablePerspective tablePerspective;
	
	private DataSelectionConfiguration dataSelectionConf;

	

	private Composite parent;

	private Table candidateCompoundsTable;
		
	private Combo visDomainCombo;
	private Combo dataDomainCombo;
	private Combo xAxisCombo;
	private Combo yAxisCombo;

	public DataSelectionDialogue(Shell parentShell, TablePerspective tablePerspective) {
		super(parentShell);
		this.tablePerspective = tablePerspective;
	}
	
	public DataSelectionDialogue(Shell parentShell) {
		super(parentShell);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Select Data for Scatterplot");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		this.parent = parent;

		parent.setLayout(new GridLayout());

		GridData data = new GridData();
		GridLayout layout = new GridLayout(1, true);

		parent.setLayout(layout);
		
		Label descriptionLabel1 = new Label(parent, SWT.NONE);
		descriptionLabel1.setText("Choose visualization type");
		descriptionLabel1.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false,false));
		
		visDomainCombo = new Combo(parent, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		visDomainCombo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false,false));
		visDomainCombo.setText("Choose visualization type");
		visDomainCombo.setEnabled(true);
		
		Label descriptionLabel2 = new Label(parent, SWT.NONE);
		descriptionLabel2.setText("Choose data domain");
		descriptionLabel2.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false,false));
		
		dataDomainCombo = new Combo(parent, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		dataDomainCombo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false,false));
		dataDomainCombo.setText("Choose data domain");
		dataDomainCombo.setEnabled(true);
		
		
		Label descriptionLabel3 = new Label(parent, SWT.NONE);
		descriptionLabel3.setText("X-axis");
		descriptionLabel3.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false,false));
		
		xAxisCombo = new Combo(parent, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		xAxisCombo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false,false));
		xAxisCombo.setText("X-axis");
		xAxisCombo.setEnabled(false);
		
		Label descriptionLabel4 = new Label(parent, SWT.NONE);
		descriptionLabel4.setText("Y-axis");
		descriptionLabel4.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false,false));
		
		yAxisCombo = new Combo(parent, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		yAxisCombo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false,false));
		yAxisCombo.setText("Y-axis");
		yAxisCombo.setEnabled(false);
		
		// Set the values for the combo's here
		
		for(EVisualizationSpaceType visDomain: EVisualizationSpaceType.values()){
			visDomainCombo.add(visDomain.name(), visDomain.ordinal());
			visDomainCombo.setData(visDomain.name(), visDomain);
		}
		visDomainCombo.select(0);
		
		visDomainCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				System.out.println("Selection " + visDomainCombo.getSelectionIndex());
				setDataColumnCombos();
			}
		});
		
		for(EDataGenerationType dataGenDomain: EDataGenerationType.values()){
			dataDomainCombo.add(dataGenDomain.name(), dataGenDomain.ordinal());
			dataDomainCombo.setData(dataGenDomain.name(), dataGenDomain);
		}
			
		dataDomainCombo.select(0);
		
		dataDomainCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				System.out.println("Selection " + dataDomainCombo.getSelectionIndex());
				setDataColumnCombos();
			}
		});
		
		visDomainCombo.pack();
		dataDomainCombo.pack();
		xAxisCombo.pack();
		yAxisCombo.pack();

		setDataColumnCombos();

		return parent;
	}
	
	/**
	 * This function populates the two combos with
	 * the possible data columns. The fields are based on the selections of the 
	 * visualization and data domain combos
	 */
	private void setDataColumnCombos()
	{
		xAxisCombo.removeAll();
		yAxisCombo.removeAll();
		
		// If it is a items space view
		// the recordPerspective will be used as the visual entity
		// and the dimensionPerspective will be the visualization axes
		if(visDomainCombo.getItem(visDomainCombo.getSelectionIndex()) == EVisualizationSpaceType.ITEMS_SPACE.toString())
		{
			if(dataDomainCombo.getItem(dataDomainCombo.getSelectionIndex()) == EDataGenerationType.RAW_DATA.toString())
			{
				IDType chosenVisEntityIDType = tablePerspective.getDimensionPerspective().getIdType();
				for (Integer id : tablePerspective.getDimensionPerspective().getVirtualArray()) {
					String label = IDMappingManagerRegistry.get().getIDMappingManager(chosenVisEntityIDType)
						.getID(chosenVisEntityIDType, chosenVisEntityIDType.getIDCategory().getHumanReadableIDType(), id);
					xAxisCombo.add(label);
					xAxisCombo.setData(label, id);
					
					yAxisCombo.add(label);
					yAxisCombo.setData(label, id);

				}
			}
			// Use derived data, such as statistics
			else if (dataDomainCombo.getItem(dataDomainCombo.getSelectionIndex()) == EDataGenerationType.DERIVED_DATA.toString())
			{
				for(EStatisticsType statType: EStatisticsType.values()){
					xAxisCombo.add(statType.name());
					yAxisCombo.add(statType.name());
				}
			}
		}
		else if (visDomainCombo.getItem(visDomainCombo.getSelectionIndex()) == EVisualizationSpaceType.DIMENSIONS_SPACE.toString())
		{
			if(dataDomainCombo.getItem(dataDomainCombo.getSelectionIndex()) == EDataGenerationType.RAW_DATA.toString())
			{
				IDType chosenVisEntityIDType = tablePerspective.getRecordPerspective().getIdType();
				for (Integer id : tablePerspective.getRecordPerspective().getVirtualArray()) {
					String label = IDMappingManagerRegistry.get().getIDMappingManager(chosenVisEntityIDType)
						.getID(chosenVisEntityIDType, chosenVisEntityIDType.getIDCategory().getHumanReadableIDType(), id);
					xAxisCombo.add(label);
					xAxisCombo.setData(label, id);
					
					yAxisCombo.add(label);
					yAxisCombo.setData(label, id);
	
				}
			}
			// Use derived data, such as statistics
			else if (dataDomainCombo.getItem(dataDomainCombo.getSelectionIndex()) == EDataGenerationType.DERIVED_DATA.toString())
			{
				for(EStatisticsType statType: EStatisticsType.values()){
					xAxisCombo.add(statType.name());
					yAxisCombo.add(statType.name());
				}
			}
		}
		
		xAxisCombo.setEnabled(true);
		yAxisCombo.setEnabled(true);
		
		xAxisCombo.select(0);
		yAxisCombo.select(1);
		
		xAxisCombo.pack();
		yAxisCombo.pack();
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected void okPressed() {

		
		
		dataSelectionConf = new DataSelectionConfiguration();
		
		// Set the selected Axis IDs to pass to the view
		ArrayList<Integer> axisIDs = new ArrayList<>();
		 
		axisIDs.add((Integer) xAxisCombo.getData(xAxisCombo.getItem(xAxisCombo.getSelectionIndex())));
		axisIDs.add((Integer) yAxisCombo.getData(yAxisCombo.getItem(yAxisCombo.getSelectionIndex())));
		
		dataSelectionConf.setAxisIDs(axisIDs);
		
		// Set the selected labels to pass to the view
		ArrayList<String> axisLabels = new ArrayList<>();
		 
		axisLabels.add( xAxisCombo.getItem(xAxisCombo.getSelectionIndex()));
		axisLabels.add( yAxisCombo.getItem(yAxisCombo.getSelectionIndex()));
		
		dataSelectionConf.setAxisLabels(axisLabels);
		
		// Set the selected vis domain
		dataSelectionConf.setVisSpaceType((EVisualizationSpaceType) visDomainCombo.getData(visDomainCombo.getItem(visDomainCombo.getSelectionIndex())));
		
		// Set the selected data generation domain, e.g., raw or derived
		dataSelectionConf.setDataResourceType((EDataGenerationType) dataDomainCombo.getData(dataDomainCombo.getItem(dataDomainCombo.getSelectionIndex())));		

		super.okPressed();

	}
	
	/**
	 * @return the dataSelectionConf
	 */
	public DataSelectionConfiguration getDataSelectionConf() {
		return dataSelectionConf;
	}
	

	/**
	 * @return the tablePerspective
	 */
	public TablePerspective getTablePerspective() {
		return tablePerspective;
	}

	/**
	 * @param tablePerspective the tablePerspective to set
	 */
	public void setTablePerspective(TablePerspective tablePerspective) {
		this.tablePerspective = tablePerspective;
	}

}
