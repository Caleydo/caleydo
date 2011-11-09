package org.caleydo.util.r.filter;

import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.filter.RecordFilter;
import org.caleydo.core.data.filter.RecordMetaFilter;
import org.caleydo.core.data.filter.event.RemoveRecordFilterEvent;
import org.caleydo.core.data.filter.representation.AFilterRepresentation;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.delta.RecordVADelta;
import org.caleydo.core.data.virtualarray.delta.VADeltaItem;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.view.histogram.GLHistogram;
import org.caleydo.view.histogram.RcpGLHistogramView;
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

public class FilterRepresentationPValue extends
		AFilterRepresentation<RecordVADelta, RecordFilter> {

	private final static String TITLE = "Variance Filter";

	private ATableBasedDataDomain dataDomain;
	private DataContainer dataContainer1;

	private Histogram histogram;
	private float pValue = -1;
	private float pValueMax = -1;

	public boolean create() {

		if (!super.create())
			return false;

		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {

				((Shell) parentComposite).setText(TITLE);

				GridData gridData = new GridData();
				gridData.grabExcessHorizontalSpace = true;
				gridData.horizontalAlignment = GridData.FILL;

				Composite infoComposite = new Composite(parentComposite, SWT.NULL);
				infoComposite.setLayoutData(gridData);
				infoComposite.setLayout(new GridLayout(4, false));

				Label pValueLabel = new Label(infoComposite, SWT.NONE);
				pValueLabel.setText("p-Value:");

				final Text pValueInputField = new Text(infoComposite, SWT.SINGLE);
				final Slider pValueSlider = new Slider(infoComposite, SWT.HORIZONTAL);

				if (pValue == -1) {
					pValueMax = histogram.getMax();
					pValue = pValueMax;
				}

				gridData = new GridData();
				gridData.grabExcessHorizontalSpace = true;
				gridData.horizontalAlignment = GridData.FILL;
				pValueSlider.setLayoutData(gridData);
				pValueSlider.setSelection((int) (pValue * 10000));

				pValueInputField.setEditable(true);
				pValueInputField.setText(Float.toString(pValue));
				pValueInputField.addKeyListener(new KeyAdapter() {
					@Override
					public void keyPressed(KeyEvent e) {

						String enteredValue = pValueInputField.getText();
						pValue = new Float(enteredValue);
						pValueSlider.setSelection((int) (pValue * 10000));
						isDirty = true;
					}
				});

				pValueSlider.setMinimum(0);
				pValueSlider.setMaximum((int) (pValueMax * 10000));
				pValueSlider.setIncrement(1);
				pValueSlider.setPageIncrement(5);
				pValueSlider.setSelection((int) (pValue * 10000));

				pValueSlider.addMouseListener(new MouseAdapter() {

					@Override
					public void mouseUp(MouseEvent e) {
						pValue = (float) pValueSlider.getSelection() / 10000.00f;
						pValueInputField.setText(Float.toString(pValue));
						isDirty = true;
						parentComposite.pack();
						parentComposite.layout();

						// if (reducedVA != null)
						// reducedNumberLabel.setText("# Genes: " +
						// reducedVA.size());

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

				Composite histoComposite = new Composite(parentComposite, SWT.NULL);
				histoComposite.setLayout(new FillLayout(SWT.VERTICAL));

				gridData = new GridData();
				gridData.heightHint = 300;
				gridData.widthHint = 500;
				// gridData.verticalAlignment = GridData.FILL;
				// gridData2.grabExcessVerticalSpace = true;
				histoComposite.setLayoutData(gridData);

				RcpGLHistogramView histogramView = new RcpGLHistogramView();
				histogramView.setDataDomain(dataDomain);

				histogramView.createDefaultSerializedView();
				histogramView.createPartControl(histoComposite);
				((GLHistogram) (histogramView.getGLView())).setHistogram(histogram);
				// Usually the canvas is registered to the GL2 animator in the
				// PartListener.
				// Because the GL2 histogram is no usual RCP view we have to do
				// it on our
				// own
				GeneralManager.get().getViewManager()
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

		if (filter instanceof RecordMetaFilter) {
			for (RecordFilter subFilter : ((RecordMetaFilter) filter).getFilterList()) {

				createVADelta(subFilter);
			}
		} else
			createVADelta(filter);
	}

	private void createVADelta(RecordFilter subFilter) {

		RecordVADelta recordVADelta = new RecordVADelta(dataContainer1
				.getRecordPerspective().getID(), subFilter.getDataDomain()
				.getRecordIDType());

		RecordVirtualArray recordVA = dataContainer1.getRecordPerspective()
				.getVirtualArray();

		double[] tTestResult = dataContainer1.getContainerStatistics().tTest().getOneSidedTTestResult();//((FilterRepresentationPValue) subFilter.getFilterRep())
	

		for (int recordIndex = 0; recordIndex < recordVA.size(); recordIndex++) {

			if (tTestResult != null && tTestResult[recordIndex] > pValue)
				recordVADelta.add(VADeltaItem.removeElement(recordVA.get(recordIndex)));
		}
		subFilter.setVADelta(recordVADelta);
	}

	@Override
	protected void triggerRemoveFilterEvent() {
		RemoveRecordFilterEvent filterEvent = new RemoveRecordFilterEvent();
		filterEvent.setDataDomainID(filter.getDataDomain().getDataDomainID());
		filterEvent.setFilter(filter);
		GeneralManager.get().getEventPublisher().triggerEvent(filterEvent);
	}

	@Override
	protected void applyFilter() {
		if (isDirty) {
			createVADelta();
			filter.updateFilterManager();
		}
		isDirty = false;
	}

	/**
	 * @param dataDomain
	 *            setter, see {@link #dataDomain}
	 */
	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	public void setDataContainer1(DataContainer dataContainer1) {
		this.dataContainer1 = dataContainer1;
	}
}
