package org.caleydo.util.r.view;

import java.util.ArrayList;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.statistics.FoldChangeSettings;
import org.caleydo.core.data.collection.set.statistics.FoldChangeSettings.FoldChangeEvaluator;
import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.manager.ISetBasedDataDomain;
import org.caleydo.core.manager.datadomain.DataDomainGraph;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.event.data.ReplaceContentVAInUseCaseEvent;
import org.caleydo.core.manager.event.data.StatisticsResultFinishedEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.ISetBasedView;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.swt.ASWTView;
import org.caleydo.core.view.swt.ISWTView;
import org.caleydo.util.r.listener.StatisticsResultFinishedEventListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;

/**
 * View shows parameters for statistics reduction.
 * 
 * @author Marc Streit
 */
public class StatisticsView extends ASWTView implements IView, ISWTView, ISetBasedView {

	public final static String VIEW_ID = "org.caleydo.view.statistics";
	private Composite composite;

	private ArrayList<ISet> setsWithPerformedStatistics;

	private StatisticsResultFinishedEventListener statisticsResultFinishedEventListener;

	private float pValueCutOff = 0.05f;

	private ContentVirtualArray reducedVA;

	private Label reducedNumberLabel;

	private ISetBasedDataDomain dataDomain;

	/**
	 * Constructor.
	 */
	public StatisticsView(final int iParentContainerId, final String sLabel) {
		super(iParentContainerId, sLabel, GeneralManager.get().getIDManager()
				.createID(EManagedObjectType.VIEW_SWT_TABULAR_DATA_VIEWER));

		this.viewType = VIEW_ID;
		registerDataDomains();

		setsWithPerformedStatistics = new ArrayList<ISet>();
	}

	@Override
	public void initViewSWTComposite(Composite parentComposite) {
		composite = new Composite(parentComposite, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = layout.marginHeight = layout.horizontalSpacing = 0;
		composite.setLayout(layout);

		initData();
		createGUI();
	}

	@Override
	public void drawView() {

	}

	public void initData() {
	}

	private void createGUI() {

		composite.layout();

		final Slider pValueSlider = new Slider(composite, SWT.HORIZONTAL);

		final Label pValueLabel = new Label(composite, SWT.NULL);
		pValueLabel.setText("p-Value: " + pValueCutOff);

		reducedNumberLabel = new Label(composite, SWT.NULL);
		reducedNumberLabel.setText("");

		pValueSlider.setMinimum(0);
		pValueSlider.setMaximum(110);
		pValueSlider.setIncrement(10);
		pValueSlider.setPageIncrement(10);
		pValueSlider.setSelection((int) (pValueCutOff * 1000));

		pValueSlider.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {
				pValueCutOff = pValueSlider.getSelection() / 1000f;
				pValueLabel.setText("p-Value: " + pValueCutOff);

				calulateReduction();

				// if (reducedVA != null)
				// reducedNumberLabel.setText("# Genes: " + reducedVA.size());

				composite.layout();
			}
		});

		final Button button = new Button(composite, SWT.PUSH);
		button.setText("Perform Reduction");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				performReduction();
			}
		});

		final Button buttonClear = new Button(composite, SWT.PUSH);
		buttonClear.setText("Clear Statistics Results");
		buttonClear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearStatisticsResults();

				calulateReduction();
			}
		});
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedStatisticsView serializedForm = new SerializedStatisticsView();
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public void initFromSerializableRepresentation(ASerializedView ser) {
		// this implementation does not initialize anything yet
	}

	@Override
	public void registerEventListeners() {
		statisticsResultFinishedEventListener = new StatisticsResultFinishedEventListener();
		statisticsResultFinishedEventListener.setHandler(this);
		GeneralManager
				.get()
				.getEventPublisher()
				.addListener(StatisticsResultFinishedEvent.class,
						statisticsResultFinishedEventListener);
	}

	@Override
	public void unregisterEventListeners() {

		if (statisticsResultFinishedEventListener != null) {
			GeneralManager.get().getEventPublisher()
					.removeListener(statisticsResultFinishedEventListener);
			statisticsResultFinishedEventListener = null;
		}
	}

	private void calulateReduction() {

		if (setsWithPerformedStatistics == null
				|| setsWithPerformedStatistics.size() == 0)
			return;

		reducedVA = new ContentVirtualArray();

		for (int contentIndex = 0; contentIndex < setsWithPerformedStatistics.get(0)
				.getContentVA(ContentVAType.CONTENT).size(); contentIndex++) {
			boolean resultValid = true;

			for (ISet set : setsWithPerformedStatistics) {

				for (ISet foldChangeCompareSet : set.getStatisticsResult()
						.getAllFoldChangeResults().keySet()) {

					Pair<double[], FoldChangeSettings> foldChangeResult = set
							.getStatisticsResult().getFoldChangeResult(
									foldChangeCompareSet);
					FoldChangeSettings foldChangeSettings = foldChangeResult.getSecond();

					if (foldChangeSettings.getEvaluator() == FoldChangeEvaluator.GREATER) {
						if (foldChangeResult.getFirst()[contentIndex] < foldChangeSettings
								.getRatio()) {
							resultValid = false;
							continue;
						}
					} else if (foldChangeSettings.getEvaluator() == FoldChangeEvaluator.LESS) {
						if (foldChangeResult.getFirst()[contentIndex] * -1 < foldChangeSettings
								.getRatio()) {
							resultValid = false;
							continue;
						}
					} else if (foldChangeSettings.getEvaluator() == FoldChangeEvaluator.SAME) {
						if (Math.abs(foldChangeResult.getFirst()[contentIndex]) < foldChangeSettings
								.getRatio()) {
							resultValid = false;
							continue;
						}
					}
				}

				for (ISet twoSidedCompareSet : set.getStatisticsResult()
						.getAllTwoSidedTTestResults().keySet()) {

					ArrayList<Double> twoSidedTTestResult = set.getStatisticsResult()
							.getTwoSidedTTestResult(twoSidedCompareSet);

					if (twoSidedTTestResult != null
							&& twoSidedTTestResult.get(contentIndex) > pValueCutOff)
						resultValid = false;
				}

				double[] tTestResult = set.getStatisticsResult().getOneSidedTTestResult();
				if (tTestResult != null && tTestResult[contentIndex] > pValueCutOff)
					resultValid = false;

				if (!resultValid)
					continue;
				// else
				// System.out.println("Found valid gene fulfilling statistics criteria: "
				// +set +" "+contentIndex);
			}

			if (resultValid)
				reducedVA.appendUnique(contentIndex);
		}

		if (reducedVA != null) {
			reducedNumberLabel.setText("# Genes: " + reducedVA.size());
			composite.layout();
		}
	}

	private void performReduction() {

		if (reducedVA != null)
			triggerReplaceContentVAEvent(reducedVA);
	}

	private void clearStatisticsResults() {

		for (ISet set : setsWithPerformedStatistics) {
			set.getStatisticsResult().clearStatisticsResults();
		}

		calulateReduction();
	}

	public void triggerReplaceContentVAEvent(ContentVirtualArray newVA) {
		ReplaceContentVAInUseCaseEvent event = new ReplaceContentVAInUseCaseEvent(
				dataDomain.getDataDomainType(), ContentVAType.CONTENT, newVA);
		event.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(event);
	}

	public void resultFinished(ArrayList<ISet> sets) {
		setsWithPerformedStatistics.addAll(sets);

		calulateReduction();

	}

	@Override
	public void registerDataDomains() {
		ArrayList<String> dataDomainTypes = new ArrayList<String>();
		dataDomainTypes.add("org.caleydo.datadomain.genetic");
		dataDomainTypes.add("org.caleydo.datadomain.generic");
		dataDomainTypes.add("org.caleydo.datadomain.clinical");

		DataDomainManager.getInstance().getAssociationManager()
				.registerDatadomainTypeViewTypeAssociation(dataDomainTypes, viewType);
	}

	@Override
	public void setDataDomain(ISetBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	@Override
	public ISetBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	public void setSet(ISet set) {
		// TODO Auto-generated method stub

	}

}
