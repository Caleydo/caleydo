package org.caleydo.util.r.filter;

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
import org.caleydo.view.histogram.RcpGLHistogramView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;

public class FilterRepresentationPValue extends
		AFilterRepresentation<ContentVADelta, ContentFilter> {

	private ISet set;

	private Histogram histogram;
	private float pValue = 1f;

	public void create() {
		super.create();

		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {

				GridData gridData = new GridData();
				gridData.grabExcessHorizontalSpace = true;
//				gridData.horizontalAlignment = GridData.FILL;

				gridData.widthHint = 400;
				gridData.heightHint = 50;
				
				Composite infoComposite = new Composite(parentComposite, SWT.NULL);
				infoComposite.setLayoutData(gridData);
				infoComposite.setLayout(new FillLayout(SWT.VERTICAL));
				
				final Slider pValueSlider = new Slider(infoComposite, SWT.HORIZONTAL);

				final Label pValueLabel = new Label(infoComposite, SWT.NULL);
				pValueLabel.setText("p-Value: " + Float.toString(pValue));
				pValueSlider.setMinimum(0);
				pValueSlider.setMaximum((int) (histogram.getMax() * 10000));
				System.out.println("Largest: " +histogram.getMax());
				pValueSlider.setIncrement(1);
				pValueSlider.setPageIncrement(5);
				pValueSlider.setSelection((int) (pValue * 10000));
				
				pValueSlider.addMouseListener(new MouseAdapter() {

					@Override
					public void mouseUp(MouseEvent e) {
						pValue = (float) pValueSlider.getSelection() / 10000.00f;
						System.out.println(pValue);
						pValueLabel.setText("p-Value: " + Float.toString(pValue));
						parentComposite.pack();
						parentComposite.layout();

						createVADelta();
						filter.updateFilterManager();

						// if (reducedVA != null)
						// reducedNumberLabel.setText("# Genes: " +
						// reducedVA.size());

					}
				});
				
				Composite histoComposite = new Composite(parentComposite, SWT.NULL);
				histoComposite.setLayout(new FillLayout(SWT.VERTICAL));
				gridData.heightHint = 300;
//				gridData.verticalAlignment = GridData.FILL;
				gridData.grabExcessVerticalSpace = true;
				histoComposite.setLayoutData(gridData);
				
				RcpGLHistogramView histogramView = new RcpGLHistogramView();
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

				Monitor primary = parentComposite.getDisplay().getPrimaryMonitor();
				Rectangle bounds = primary.getBounds();
				Rectangle rect = parentComposite.getBounds();
				int x = bounds.x + (bounds.width - rect.width) / 2;
				int y = bounds.y + (bounds.height - rect.height) / 2;
				parentComposite.setLocation(x, y);
				
				((Shell) parentComposite).open();
			}
		});
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
		ContentVirtualArray contentVA = subFilter.getDataDomain()
				.getContentFilterManager().getBaseVA();

		double[] tTestResult = ((FilterRepresentationPValue) subFilter.getFilterRep())
				.getSet().getStatisticsResult().getOneSidedTTestResult();

		for (int contentIndex = 0; contentIndex < contentVA.size(); contentIndex++) {

			if (tTestResult != null && tTestResult[contentIndex] > pValue)
				contentVADelta
						.add(VADeltaItem.removeElement(contentVA.get(contentIndex)));
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

	public void setSet(ISet set) {
		this.set = set;
	}

	public ISet getSet() {
		return set;
	}
}
