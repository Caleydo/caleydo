package org.caleydo.view.filterpipeline.representation;

import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.collection.ISet;
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

public class FilterRepresentationSNR extends
		AFilterRepresentation<ContentVADelta, ContentFilter> {

	private final static String TITLE = "Signal-To-Noice Ratio Filter";
	
	private ISet set;

	private Histogram histogram;
	
	private float invalidThreshold = 2;
	private float invalidThresholdMax = 5;
	
	private float validThreshold = 3;
	private float validThresholdMax = 5;
	
	public boolean create() {
		
		if( !super.create() )
			return false;
		
		// Calculate it with defaults in order to have some sort of result at the beginning
		set.calculateNormalizedAverageUncertainty(invalidThreshold, validThreshold);

		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				
				((Shell)parentComposite).setText(TITLE);
				
				GridData gridData = new GridData();
				gridData.grabExcessHorizontalSpace = true;
				gridData.horizontalAlignment = GridData.FILL;

				Composite infoComposite = new Composite(parentComposite, SWT.NULL);
				infoComposite.setLayoutData(gridData);
				infoComposite.setLayout(new GridLayout(4, false));

				Label invalidThresholdLabel = new Label(infoComposite, SWT.NONE);
				invalidThresholdLabel.setText("Invalid threshold:");

				final Text invalidThresholdInputField = new Text(infoComposite, SWT.SINGLE);
				final Slider invalidThresholdSlider = new Slider(infoComposite, SWT.HORIZONTAL);
				
				if (invalidThreshold == -1) {
					invalidThresholdMax = histogram.getMax();
					invalidThreshold = invalidThresholdMax;
				}

				gridData = new GridData();
				gridData.grabExcessHorizontalSpace = true;
				gridData.horizontalAlignment = GridData.FILL;
				invalidThresholdSlider.setLayoutData(gridData);
				invalidThresholdSlider.setSelection((int) (invalidThreshold * 10000));

				invalidThresholdInputField.setEditable(true);
				invalidThresholdInputField.setText(Float.toString(invalidThreshold));
				invalidThresholdInputField.addKeyListener(new KeyAdapter() {
					@Override
					public void keyPressed(KeyEvent e) {

						String enteredValue = invalidThresholdInputField.getText();
						invalidThreshold = new Float(enteredValue);
						invalidThresholdSlider.setSelection((int) (invalidThreshold * 10000));
						isDirty = true;
					}
				});

				invalidThresholdSlider.setMinimum(0);
				invalidThresholdSlider.setMaximum((int) (invalidThresholdMax * 10000));
				invalidThresholdSlider.setIncrement(1);
				invalidThresholdSlider.setPageIncrement(5);
				invalidThresholdSlider.setSelection((int) (invalidThreshold * 10000));

				invalidThresholdSlider.addMouseListener(new MouseAdapter() {

					@Override
					public void mouseUp(MouseEvent e) {
						invalidThreshold = (float) invalidThresholdSlider.getSelection() / 10000.00f;
						invalidThresholdInputField.setText(Float.toString(invalidThreshold));
						isDirty = true;
						parentComposite.pack();
						parentComposite.layout();

						// if (reducedVA != null)
						// reducedNumberLabel.setText("# Genes: " +
						// reducedVA.size());

					}
				});

				//-----------
				
				Label validThresholdLabel = new Label(infoComposite, SWT.NONE);
				validThresholdLabel.setText("Valid threshold:");

				final Text validThresholdInputField = new Text(infoComposite, SWT.SINGLE);
				final Slider validThresholdSlider = new Slider(infoComposite, SWT.HORIZONTAL);

				if (validThreshold == -1) {
					validThresholdMax = histogram.getMax();
					validThreshold = validThresholdMax;
				}

				gridData = new GridData();
				gridData.grabExcessHorizontalSpace = true;
				gridData.horizontalAlignment = GridData.FILL;
				validThresholdSlider.setLayoutData(gridData);
				validThresholdSlider.setSelection((int) (validThreshold * 10000));

				validThresholdInputField.setEditable(true);
				validThresholdInputField.setText(Float.toString(validThreshold));
				validThresholdInputField.addKeyListener(new KeyAdapter() {
					@Override
					public void keyPressed(KeyEvent e) {

						String enteredValue = validThresholdInputField.getText();
						validThreshold = new Float(enteredValue);
						validThresholdSlider.setSelection((int) (validThreshold * 10000));
						isDirty = true;
					}
				});

				validThresholdSlider.setMinimum(0);
				validThresholdSlider.setMaximum((int) (validThresholdMax * 10000));
				validThresholdSlider.setIncrement(1);
				validThresholdSlider.setPageIncrement(5);
				validThresholdSlider.setSelection((int) (validThreshold * 10000));

				validThresholdSlider.addMouseListener(new MouseAdapter() {

					@Override
					public void mouseUp(MouseEvent e) {
						validThreshold = (float) validThresholdSlider.getSelection() / 10000.00f;
						validThresholdInputField.setText(Float.toString(validThreshold));
						isDirty = true;
						parentComposite.pack();
						parentComposite.layout();

						// if (reducedVA != null)
						// reducedNumberLabel.setText("# Genes: " +
						// reducedVA.size());

					}
				});
				
				//-------------
					
				final Button applyFilterButton = new Button(infoComposite, SWT.PUSH);
				applyFilterButton.setText("Apply");
				applyFilterButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						applyFilter();
					}
				});

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

		ContentVADelta uncertaintyContentVADelta = new ContentVADelta(ISet.CONTENT, subFilter
				.getDataDomain().getContentIDType());
		
		ContentVirtualArray contentVA = subFilter.getDataDomain()
				.getContentFilterManager().getBaseVA();

		float[] rawUncertainty = ((FilterRepresentationSNR) subFilter.getFilterRep())
				.getSet().getRawUncertainty();
		
		for (int contentIndex = 0; contentIndex < contentVA.size(); contentIndex++) {

			float value = rawUncertainty[contentIndex];
			if (value <= validThreshold)
				contentVADelta
						.add(VADeltaItem.removeElement(contentVA.get(contentIndex)));
			
			if (value > invalidThreshold && value < validThreshold)
				uncertaintyContentVADelta
				.add(VADeltaItem.append(contentVA.get(contentIndex)));				
		}
		
		subFilter.setVADelta(contentVADelta);
		subFilter.setVADeltaUncertainty(uncertaintyContentVADelta);
	}
	

	@Override
	protected void triggerRemoveFilterEvent() {
		RemoveContentFilterEvent filterEvent = new RemoveContentFilterEvent();
		filterEvent.setDataDomainType(filter.getDataDomain().getDataDomainType());
		filterEvent.setFilter(filter);
		GeneralManager.get().getEventPublisher().triggerEvent(filterEvent);
	}

	public void setSet(ISet set) {
		this.set = set;
	}

	public ISet getSet() {
		return set;
	}
	
	@Override
	protected void applyFilter() {
		if (isDirty)
		{
			createVADelta();
			set.calculateNormalizedAverageUncertainty(invalidThreshold, validThreshold);
			filter.updateFilterManager();
		}
		isDirty = false;
	}
}
