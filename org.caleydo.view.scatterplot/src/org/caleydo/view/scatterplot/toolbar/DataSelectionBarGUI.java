package org.caleydo.view.scatterplot.toolbar;

import java.util.ArrayList;

import org.caleydo.core.data.perspective.table.EStatisticsType;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.IEventBasedSelectionManagerUser;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.view.scatterplot.GLScatterplot;
import org.caleydo.view.scatterplot.dialogues.DataSelectionConfiguration;
import org.caleydo.view.scatterplot.event.ScatterplotDataSelectionEvent;
import org.caleydo.view.scatterplot.utils.EDataGenerationType;
import org.caleydo.view.scatterplot.utils.EVisualizationSpaceType;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Slider;

public class DataSelectionBarGUI extends ControlContribution
{
	private Combo visDomainCombo;
	private Combo dataDomainCombo;
	private Combo xAxisCombo;
	private Combo yAxisCombo;
	
	private Composite parentComposite;
	
	private DataSelectionConfiguration dataSelectionConf;
	
	private GLScatterplot parentView;

	public GLScatterplot getParentView() {
		return parentView;
	}

	public void setParentView(GLScatterplot parentView) {
		this.parentView = parentView;
	}

	public DataSelectionBarGUI(GLScatterplot parentView) {
		super("Data Selection Toolbar");
		
		this.parentView = parentView;
		//this.tablePerspective = tablePerspective;
		
	}

	@Override
	protected Control createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		parentComposite = composite;
		
		RowLayout rowLayout = new RowLayout();
		
		composite.setLayout(rowLayout);
		
		Label descriptionLabel1 = new Label(composite, SWT.LEFT);
		descriptionLabel1.setText("Type:");
		//descriptionLabel1.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false,false));
		
		visDomainCombo = new Combo(composite, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		visDomainCombo.setText("Type");
		visDomainCombo.setEnabled(true);
		
		Label descriptionLabel2 = new Label(composite, SWT.LEFT);
		descriptionLabel2.setText("Data:");
		//descriptionLabel2.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false,false));
		
		dataDomainCombo = new Combo(composite, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		//dataDomainCombo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false,false));
		dataDomainCombo.setText("Data");
		dataDomainCombo.setEnabled(true);
		
		
		Label descriptionLabel3 = new Label(composite, SWT.LEFT);
		descriptionLabel3.setText("X:");
		//descriptionLabel3.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false,false));
		
		xAxisCombo = new Combo(composite, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		//xAxisCombo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false,false));
		xAxisCombo.setText("X:");
		//xAxisCombo.setSize(40, composite.getSize().y);
		xAxisCombo.setLayoutData(new RowData(120, composite.getSize().y));
		xAxisCombo.setEnabled(false);
		
		Label descriptionLabel4 = new Label(composite, SWT.LEFT);
		descriptionLabel4.setText("Y:");

		//descriptionLabel4.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false,false));
		
		yAxisCombo = new Combo(composite, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		//yAxisCombo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false,false));
		yAxisCombo.setText("Y:");
		yAxisCombo.setLayoutData(new RowData(120, composite.getSize().y));
		yAxisCombo.setEnabled(false);
		
		Button button1 = new Button(composite, SWT.PUSH);
		button1.setText("Update Data");
		
		Listener listener = new Listener() {
			@Override
			public void handleEvent(Event event) {

				HandleDataSelectionFinishedSignal();
			}

		};
		button1.addListener(SWT.Selection, listener);
		//button1.setLayoutData(new RowData(50, 40));
		
		// Set the values for the combo's here
		
		for(EVisualizationSpaceType visDomain: EVisualizationSpaceType.values()){
			visDomainCombo.add(visDomain.name(), visDomain.ordinal());
			visDomainCombo.setData(visDomain.name(), visDomain);
		}
		//visDomainCombo.select(0);
		
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
			
		//dataDomainCombo.select(0);
		
		dataDomainCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				System.out.println("Selection " + dataDomainCombo.getSelectionIndex());
				setDataColumnCombos();
			}
		});
		
		//visDomainCombo.pack();
		//dataDomainCombo.pack();
		//xAxisCombo.pack();
		//yAxisCombo.pack();

		//setDataColumnCombos();
		return composite;
	}
	
	
	private void setDataColumnCombos()
	{
		// If no vis and/or data domain is selected, return!
		if(visDomainCombo.getSelectionIndex() == -1 | dataDomainCombo.getSelectionIndex() == -1)
		{
			return;
		}
		
		TablePerspective tablePerspective = parentView.getTablePerspective();
		
		xAxisCombo.removeAll();
		yAxisCombo.removeAll();
		
		// If it is a items space view
		// the recordPerspective will be used as the visual entity
		// and the dimensionPerspective will be the visualization axes
		if(visDomainCombo.getItem(visDomainCombo.getSelectionIndex()).toString().equals(EVisualizationSpaceType.ITEMS_SPACE.toString()))
		{
			if(dataDomainCombo.getItem(dataDomainCombo.getSelectionIndex()).toString().equals(EDataGenerationType.RAW_DATA.toString()))
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
			else if (dataDomainCombo.getItem(dataDomainCombo.getSelectionIndex()).toString().equals(EDataGenerationType.DERIVED_DATA.toString()))
			{
				for(EStatisticsType statType: EStatisticsType.values()){
					xAxisCombo.add(statType.name());
					yAxisCombo.add(statType.name());
				}
			}
		}
		else if (visDomainCombo.getItem(visDomainCombo.getSelectionIndex()).toString().equals(EVisualizationSpaceType.DIMENSIONS_SPACE.toString()))
		{
			if(dataDomainCombo.getItem(dataDomainCombo.getSelectionIndex()).toString().equals(EDataGenerationType.RAW_DATA.toString()))
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
			else if (dataDomainCombo.getItem(dataDomainCombo.getSelectionIndex()).toString().equals(EDataGenerationType.DERIVED_DATA.toString()))
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
		yAxisCombo.select(0);
		
		xAxisCombo.pack();
		yAxisCombo.pack();
		
		parentComposite.layout();
		
		
		
	}
	
	public void HandleDataSelectionFinishedSignal()
	{
		
		if(visDomainCombo.getSelectionIndex() == -1 | dataDomainCombo.getSelectionIndex() == -1 |
				xAxisCombo.getSelectionIndex() == -1 | yAxisCombo.getSelectionIndex() == -1	)
		{
			return;
		}
		
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

		
		GeneralManager
		.get()
		.getEventPublisher()
		.triggerEvent(new ScatterplotDataSelectionEvent(dataSelectionConf, parentView.getID()));
	}

}
