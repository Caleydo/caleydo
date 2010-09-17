package org.caleydo.util.r.filter;

import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.statistics.FoldChangeSettings;
import org.caleydo.core.data.collection.set.statistics.FoldChangeSettings.FoldChangeEvaluator;
import org.caleydo.core.data.filter.ContentFilter;
import org.caleydo.core.data.filter.ContentMetaFilter;
import org.caleydo.core.data.filter.event.RemoveContentFilterEvent;
import org.caleydo.core.data.filter.representation.AFilterRepresentation;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.delta.ContentVADelta;
import org.caleydo.core.data.virtualarray.delta.VADeltaItem;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.view.histogram.GLHistogram;
import org.caleydo.view.histogram.RcpBasicGLHistogramView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Text;

public class FilterRepresentationFoldChange extends
		AFilterRepresentation<ContentVADelta, ContentFilter> {

	private final static String TITLE = "Fold Change Filter";

	private ISet set1;
	private ISet set2;

	private float foldChange = 2;

	Button[] evaluatorCheckBox;
	
	private Histogram histogram;

	public void create() {
		super.create();

		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {

				((Shell) parentComposite).setText(TITLE);

				Composite infoComposite = new Composite(parentComposite, SWT.NULL);
				GridData gridData = new GridData();
				infoComposite.setLayoutData(gridData);
				infoComposite.setLayout(new GridLayout(4, false));

				final Label foldChangeLabel = new Label(infoComposite, SWT.NULL);
				foldChangeLabel.setText("Fold change:");

				final Text foldChangeInputField = new Text(infoComposite, SWT.SINGLE);
				final Slider foldChangeSlider = new Slider(infoComposite, SWT.HORIZONTAL);

				// gridData.grabExcessHorizontalSpace = true;
				// gridData.horizontalAlignment = GridData.FILL;
				// pValueSlider.setLayoutData(gridData);

				foldChangeInputField.setEditable(true);
				foldChangeInputField.setText(Float.toString(foldChange));
				foldChangeInputField.addKeyListener(new KeyAdapter() {
					@Override
					public void keyPressed(KeyEvent e) {

						String enteredValue = foldChangeInputField.getText();

						if (enteredValue != null && !enteredValue.isEmpty()) {
							foldChange = new Float(enteredValue);
							foldChangeSlider.setSelection((int) (foldChange * 10));
						}
					}
				});

				final Button applyFilterButton = new Button(infoComposite, SWT.PUSH);
				applyFilterButton.setText("Apply");
				applyFilterButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {

						FoldChangeEvaluator foldChangeEvaluator = null;

						if (evaluatorCheckBox[0].getSelection() == true
								&& evaluatorCheckBox[1].getSelection() == true) {
							foldChangeEvaluator = FoldChangeEvaluator.BOTH;
						} else if (evaluatorCheckBox[0].getSelection() == true) {
							foldChangeEvaluator = FoldChangeEvaluator.LESS;
						} else if (evaluatorCheckBox[1].getSelection() == true) {
							foldChangeEvaluator = FoldChangeEvaluator.GREATER;
						}

						FoldChangeSettings foldChangeSettings = new FoldChangeSettings(
								foldChange, foldChangeEvaluator);

						set1.getStatisticsResult().setFoldChangeSettings(set2,
								foldChangeSettings);
						set2.getStatisticsResult().setFoldChangeSettings(set1,
								foldChangeSettings);

						createVADelta();
						filter.updateFilterManager();
					}
				});

				evaluatorCheckBox = new Button[3];

				evaluatorCheckBox[0] = new Button(parentComposite, SWT.CHECK);
				evaluatorCheckBox[0].setSelection(true);
				evaluatorCheckBox[0].setText("Less (down regulated)");

				evaluatorCheckBox[1] = new Button(parentComposite, SWT.CHECK);
				evaluatorCheckBox[1].setText("Greater (up regulated)");

				// evaluatorCheckBox[0].addSelectionListener(new
				// SelectionAdapter() {
				// @Override
				// public void widgetSelected(SelectionEvent e) {
				// // evaluatorCheckBox[2].setSelection(false);
				//
				// FoldChangeSettings foldChangeSettings = new
				// FoldChangeSettings(
				// foldChangeSlider.getSelection() / 10d,
				// FoldChangeEvaluator.LESS);
				//
				// set1.getStatisticsResult().setFoldChangeSettings(set2,
				// foldChangeSettings);
				// set2.getStatisticsResult().setFoldChangeSettings(set1,
				// foldChangeSettings);
				// }
				// });

				// evaluatorCheckBox[1].addSelectionListener(new
				// SelectionAdapter() {
				// @Override
				// public void widgetSelected(SelectionEvent e) {
				// // evaluatorCheckBox[2].setSelection(false);
				//
				// FoldChangeSettings foldChangeSettings = new
				// FoldChangeSettings(
				// foldChangeSlider.getSelection() / 10d,
				// FoldChangeEvaluator.GREATER);
				//
				// set1.getStatisticsResult().setFoldChangeSettings(set2,
				// foldChangeSettings);
				// set2.getStatisticsResult().setFoldChangeSettings(set1,
				// foldChangeSettings);
				// }
				// });

				foldChangeSlider.setMinimum(0);
				foldChangeSlider.setMaximum(100);
				foldChangeSlider.setIncrement(1);
				foldChangeSlider.setPageIncrement(10);
				foldChangeSlider.setSelection((int) (foldChange * 10));

				foldChangeSlider.addMouseListener(new MouseAdapter() {

					@Override
					public void mouseUp(MouseEvent e) {
						foldChange = foldChangeSlider.getSelection() / 10f;
						foldChangeInputField.setText("" + foldChange);

						// int reducedNumberOfElements =
						// set1.getStatisticsResult()
						// .getElementNumberOfFoldChangeReduction(set2);
						//
						// label.setText("The fold change reduced results in a dataset of the size "
						// + reducedNumberOfElements);
						// parentComposite.layout();
					}
				});
				
				
				set1.getStatisticsResult().getFoldChangeResult(set2)
				.getFirst();
				
				Composite histoComposite = new Composite(parentComposite, SWT.NULL);
				histoComposite.setLayout(new FillLayout(SWT.VERTICAL));

				gridData = new GridData();
				gridData.heightHint = 300;
				gridData.widthHint = 500;
				// gridData.verticalAlignment = GridData.FILL;
				// gridData2.grabExcessVerticalSpace = true;
				histoComposite.setLayoutData(gridData);

				
				RcpBasicGLHistogramView histogramView = new RcpBasicGLHistogramView();
				histogramView.setDataDomain(DataDomainManager.get().getDataDomain(
						"org.caleydo.datadomain.genetic"));

				histogramView.createDefaultSerializedView();
				histogramView.createPartControl(histoComposite);
				((GLHistogram) (histogramView.getGLView())).setHistogram(histogram);
				// Usually the canvas is registered to the GL animator in the
				// PartListener.
				// Because the GL histogram is no usual RCP view we have to do
				// it on our
				// own
				GeneralManager.get().getViewGLCanvasManager()
						.registerGLCanvasToAnimator(histogramView.getGLCanvas());
				
			}
		});
		
		


		addOKCancel();
	}
	
	public void setHistogram(Histogram histogram) {
		this.histogram = histogram;
	}

	public void setSet1(ISet set1) {
		this.set1 = set1;
	}

	public void setSet2(ISet set2) {
		this.set2 = set2;
	}

	@Override
	protected void createVADelta() {

		if (filter instanceof ContentMetaFilter) {
			for (ContentFilter subFilter : ((ContentMetaFilter) filter).getFilterList()) {

				createVADelta(subFilter);
			}
		} else
			createVADelta(filter);
	}

	private void createVADelta(ContentFilter subFilter) {

		ContentVADelta contentVADelta = new ContentVADelta(ISet.CONTENT, subFilter
				.getDataDomain().getContentIDType());
		ContentVirtualArray contentVA = subFilter.getDataDomain()
				.getContentFilterManager().getBaseVA();

		double[] resultVector = set1.getStatisticsResult().getFoldChangeResult(set2)
				.getFirst();
		FoldChangeSettings settings = set1.getStatisticsResult()
				.getFoldChangeResult(set2).getSecond();

		double foldChangeRatio = settings.getRatio();
		FoldChangeEvaluator foldChangeEvaluator = settings.getEvaluator();

		for (Integer contentIndex = 0; contentIndex < contentVA.size(); contentIndex++) {

			switch (foldChangeEvaluator) {
			case LESS:
				if (resultVector[contentIndex] * -1 > foldChangeRatio)
					continue;
				break;
			case GREATER:
				if (resultVector[contentIndex] > foldChangeRatio)
					continue;
				break;
			case BOTH:
				if (Math.abs(resultVector[contentIndex]) > foldChangeRatio)
					continue;
				break;
			}

			contentVADelta.add(VADeltaItem.removeElement(contentVA.get(contentIndex)));
		}

		subFilter.setDelta(contentVADelta);
	}

	@Override
	protected void triggerRemoveFilterEvent() {
		RemoveContentFilterEvent filterEvent = new RemoveContentFilterEvent();
		filterEvent.setDataDomainType(filter.getDataDomain().getDataDomainType());
		filterEvent.setFilter(filter);
		GeneralManager.get().getEventPublisher().triggerEvent(filterEvent);
	}
}
