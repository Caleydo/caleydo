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

	private float foldChange = 3;
	private float foldChangeUncertainty = 1.2f;

	Button[] evaluatorCheckBox;

	private Histogram histogram;

	public boolean create() {

		if (!super.create())
			return false;

		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {

				((Shell) parentComposite).setText(TITLE);

				Composite infoComposite = new Composite(parentComposite, SWT.NULL);
				GridData gridData = new GridData();
				infoComposite.setLayoutData(gridData);
				infoComposite.setLayout(new GridLayout(3, false));

				final Label validFoldChangeLabel = new Label(infoComposite, SWT.NULL);
				validFoldChangeLabel.setText("Valid threshold:");

				final Text validFoldChangeInputField = new Text(infoComposite, SWT.SINGLE);
				final Slider validFoldChangeSlider = new Slider(infoComposite, SWT.HORIZONTAL);

				validFoldChangeInputField.setEditable(true);
				validFoldChangeInputField.setText(Float.toString(foldChange));
				validFoldChangeInputField.addKeyListener(new KeyAdapter() {
					@Override
					public void keyPressed(KeyEvent e) {

						String enteredValue = validFoldChangeInputField.getText();

						if (enteredValue != null && !enteredValue.isEmpty()) {
							foldChange = new Float(enteredValue);
							validFoldChangeSlider.setSelection((int) (foldChange * 10));
							isDirty = true;
						}
					}
				});
				
				final Label invalidFoldChangeLabel = new Label(infoComposite, SWT.NULL);
				invalidFoldChangeLabel.setText("Invalid threshold:");

				final Text invalidFoldChangeInputField = new Text(infoComposite, SWT.SINGLE);
				final Slider invalidFoldChangeSlider = new Slider(infoComposite, SWT.HORIZONTAL);

				invalidFoldChangeInputField.setEditable(true);
				invalidFoldChangeInputField.setText(Float.toString(foldChangeUncertainty));
				invalidFoldChangeInputField.addKeyListener(new KeyAdapter() {
					@Override
					public void keyPressed(KeyEvent e) {

						String enteredValue = invalidFoldChangeInputField.getText();

						if (enteredValue != null && !enteredValue.isEmpty()) {
							foldChangeUncertainty = new Float(enteredValue);
							invalidFoldChangeSlider.setSelection((int) (foldChangeUncertainty * 10));
							isDirty = true;
						}
					}
				});

				final Button applyFilterButton = new Button(infoComposite, SWT.PUSH);
				applyFilterButton.setText("Apply");
				applyFilterButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {

						applyFilter();
					}
				});

				evaluatorCheckBox = new Button[3];

				evaluatorCheckBox[0] = new Button(parentComposite, SWT.CHECK);
				evaluatorCheckBox[0].setText("Less (down regulated)");

				evaluatorCheckBox[1] = new Button(parentComposite, SWT.CHECK);
				evaluatorCheckBox[1].setText("Greater (up regulated)");

				try {
					FoldChangeSettings settings = set1.getStatisticsResult()
							.getFoldChangeResult(set2).getSecond();
					switch (settings.getEvaluator()) {
					case GREATER:
						evaluatorCheckBox[1].setSelection(true);
						break;
					case BOTH:
						evaluatorCheckBox[1].setSelection(true);
					case LESS:
						evaluatorCheckBox[0].setSelection(true);
					}
				} catch (Exception e) // fails on filter creation
				{
					evaluatorCheckBox[0].setSelection(true);
				}

				evaluatorCheckBox[0].addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						isDirty = true;
					}
				});

				evaluatorCheckBox[1].addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						isDirty = true;
					}
				});

				validFoldChangeSlider.setMinimum(0);
				validFoldChangeSlider.setMaximum(100);
				validFoldChangeSlider.setIncrement(1);
				validFoldChangeSlider.setPageIncrement(10);
				validFoldChangeSlider.setSelection((int) (foldChange * 10));

				validFoldChangeSlider.addMouseListener(new MouseAdapter() {

					@Override
					public void mouseUp(MouseEvent e) {
						foldChange = validFoldChangeSlider.getSelection() / 10f;
						validFoldChangeInputField.setText("" + foldChange);
						isDirty = true;
						// int reducedNumberOfElements =
						// set1.getStatisticsResult()
						// .getElementNumberOfFoldChangeReduction(set2);
						//
						// label.setText("The fold change reduced results in a dataset of the size "
						// + reducedNumberOfElements);
						// parentComposite.layout();
					}
				});
				
				invalidFoldChangeSlider.setMinimum(0);
				invalidFoldChangeSlider.setMaximum(100);
				invalidFoldChangeSlider.setIncrement(1);
				invalidFoldChangeSlider.setPageIncrement(10);
				invalidFoldChangeSlider.setSelection((int) (foldChangeUncertainty * 10));

				invalidFoldChangeSlider.addMouseListener(new MouseAdapter() {

					@Override
					public void mouseUp(MouseEvent e) {
						foldChangeUncertainty = invalidFoldChangeSlider.getSelection() / 10f;
						invalidFoldChangeInputField.setText("" + foldChangeUncertainty);
						isDirty = true;
						// int reducedNumberOfElements =
						// set1.getStatisticsResult()
						// .getElementNumberOfFoldChangeReduction(set2);
						//
						// label.setText("The fold change reduced results in a dataset of the size "
						// + reducedNumberOfElements);
						// parentComposite.layout();
					}
				});
				set1.getStatisticsResult().getFoldChangeResult(set2).getFirst();

				Composite histoComposite = new Composite(parentComposite, SWT.NULL);
				histoComposite.setLayout(new FillLayout(SWT.VERTICAL));

				gridData = new GridData();
				gridData.heightHint = 300;
				gridData.widthHint = 500;
				// gridData.verticalAlignment = GridData.FILL;
				// gridData2.grabExcessVerticalSpace = true;
				histoComposite.setLayoutData(gridData);

				RcpBasicGLHistogramView histogramView = new RcpBasicGLHistogramView();
				histogramView.setDataDomain(DataDomainManager.get().getDataDomainByID(
						"org.caleydo.datadomain.genetic"));

				histogramView.createDefaultSerializedView();
				histogramView.createPartControl(histoComposite);
				((GLHistogram) (histogramView.getGLView())).setHistogram(histogram);
				// Usually the canvas is registered to the GL2 animator in the
				// PartListener.
				// Because the GL2 histogram is no usual RCP view we have to do
				// it on our
				// own
				GeneralManager.get().getViewGLCanvasManager()
						.registerGLCanvasToAnimator(histogramView.getGLCanvas());

			}
		});

		addOKCancel();

		return true;
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
		ContentVADelta contentVADeltaUncertainty = new ContentVADelta(ISet.CONTENT,
				subFilter.getDataDomain().getContentIDType());

		ContentVirtualArray contentVA = subFilter.getDataDomain()
				.getContentFilterManager().getBaseVA();

		double[] resultVector = set1.getStatisticsResult().getFoldChangeResult(set2)
				.getFirst();
		FoldChangeSettings settings = set1.getStatisticsResult()
				.getFoldChangeResult(set2).getSecond();

		double foldChangeRatio = settings.getRatio();
		double foldChangeRatioUncertainty = settings.getRatioUncertainty();

		FoldChangeEvaluator foldChangeEvaluator = settings.getEvaluator();

		// FIXME: two sided fold change should be a parallel filter
		for (Integer contentIndex = 0; contentIndex < contentVA.size(); contentIndex++) {

			double foldChangeResult = resultVector[contentIndex];
			switch (foldChangeEvaluator) {
			case LESS:
				if (foldChangeResult * -1 > foldChangeRatioUncertainty)
					continue;
				break;
			case GREATER:
				if (foldChangeResult > foldChangeRatioUncertainty)
					continue;
				break;
			case BOTH:
				if (Math.abs(foldChangeResult) > foldChangeRatioUncertainty)
					continue;
				break;
			}

			contentVADelta.add(VADeltaItem.removeElement(contentVA.get(contentIndex)));
		}

		// Evaluate uncertainty
		for (Integer contentIndex = 0; contentIndex < contentVA.size(); contentIndex++) {
			
			double foldChangeResult = resultVector[contentIndex];
			switch (foldChangeEvaluator) {
			case LESS:
				if (foldChangeResult * -1 > foldChangeRatioUncertainty
						&& foldChangeResult * -1 < foldChangeRatio)
					continue;
				break;
			case GREATER:
				if (foldChangeResult > foldChangeRatioUncertainty && foldChangeResult < foldChangeRatio)
					continue;
				break;
			case BOTH:
				if (Math.abs(foldChangeResult) > foldChangeRatioUncertainty
						&& Math.abs(foldChangeResult) < foldChangeRatio)
					continue;
				break;
			}

			contentVADeltaUncertainty
					.add(VADeltaItem.removeElement(contentVA.get(contentIndex)));

		}

		subFilter.setVADelta(contentVADelta);
		subFilter.setVADeltaUncertainty(contentVADeltaUncertainty);
	}

	@Override
	protected void triggerRemoveFilterEvent() {
		RemoveContentFilterEvent filterEvent = new RemoveContentFilterEvent();
		filterEvent.setDataDomainType(filter.getDataDomain().getDataDomainID());
		filterEvent.setFilter(filter);
		GeneralManager.get().getEventPublisher().triggerEvent(filterEvent);
	}

	@Override
	protected void applyFilter() {
		if (isDirty) {
			FoldChangeEvaluator foldChangeEvaluator = null;

			if (evaluatorCheckBox[0].getSelection() == true
					&& evaluatorCheckBox[1].getSelection() == true) {
				foldChangeEvaluator = FoldChangeEvaluator.BOTH;
			} else if (evaluatorCheckBox[0].getSelection() == true) {
				foldChangeEvaluator = FoldChangeEvaluator.LESS;
			} else if (evaluatorCheckBox[1].getSelection() == true) {
				foldChangeEvaluator = FoldChangeEvaluator.GREATER;
			}

			FoldChangeSettings foldChangeSettings = new FoldChangeSettings(foldChange,
					foldChangeUncertainty, foldChangeEvaluator);

			set1.getStatisticsResult().setFoldChangeSettings(set2, foldChangeSettings);
			set2.getStatisticsResult().setFoldChangeSettings(set1, foldChangeSettings);
			
			// FIXME: just for uncertainty paper so that the uncertainty view can access it via the main set
			ISet set = filter.getDataDomain().getSet();
			set.getStatisticsResult().setFoldChangeSettings(set1, foldChangeSettings);

			createVADelta();
			filter.updateFilterManager();
		}
		isDirty = false;
	}
}
