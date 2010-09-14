package org.caleydo.util.r.filter;

import org.caleydo.core.data.filter.ContentFilter;
import org.caleydo.core.data.filter.ContentMetaFilter;
import org.caleydo.core.data.filter.representation.AFilterRepresentation;
import org.caleydo.core.data.virtualarray.ContentVAType;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.delta.ContentVADelta;
import org.caleydo.core.data.virtualarray.delta.VADeltaItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Slider;

public class FilterRepresentationPValue
	extends AFilterRepresentation<ContentVAType, ContentVADelta, ContentFilter> {

	private float pValue;

	public void init() {
		super.init();

		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {

				final Slider pValueSlider = new Slider(parentComposite, SWT.HORIZONTAL);

				final Label pValueLabel = new Label(parentComposite, SWT.NULL);
				pValueLabel.setText("p-Value: " + pValue);
				pValueSlider.setMinimum(0);
				pValueSlider.setMaximum(110);
				pValueSlider.setIncrement(10);
				pValueSlider.setPageIncrement(10);
				pValueSlider.setSelection((int) (pValue * 1000));

				pValueSlider.addMouseListener(new MouseAdapter() {

					@Override
					public void mouseUp(MouseEvent e) {
						pValue = pValueSlider.getSelection() / 1000f;
						pValueLabel.setText("p-Value: " + pValue);

						createVADelta();
						filter.updateFilterManager();

						// if (reducedVA != null)
						// reducedNumberLabel.setText("# Genes: " + reducedVA.size());

						// parentComposite.layout();
					}
				});
				
				Monitor primary = parentComposite.getDisplay().getPrimaryMonitor();
				Rectangle bounds = primary.getBounds();
				Rectangle rect = parentComposite.getBounds();
				int x = bounds.x + (bounds.width - rect.width) / 2;
				int y = bounds.y + (bounds.height - rect.height) / 2;
				parentComposite.setLocation(x, y);
				parentComposite.pack();
			}
		});
	}

	@Override
	protected void createVADelta() {

		if (filter instanceof ContentMetaFilter) {
			for (ContentFilter subFilter : ((ContentMetaFilter) filter).getFilterList()) {
				
				createVADelta(subFilter);
			}
		}
		else
			createVADelta(filter);
	}
	
	private void createVADelta(ContentFilter subFilter) {

		ContentVADelta contentVADelta =
			new ContentVADelta(ContentVAType.CONTENT, subFilter.getDataDomain().getContentIDType());
		ContentVirtualArray contentVA = subFilter.getSet().getContentData(ContentVAType.CONTENT).getContentVA();

		double[] tTestResult = subFilter.getSet().getStatisticsResult().getOneSidedTTestResult();
		for (int contentIndex = 0; contentIndex < contentVA.size(); contentIndex++) {

			if (tTestResult != null && tTestResult[contentIndex] > pValue)
				contentVADelta.add(VADeltaItem.removeElement(contentVA.get(contentIndex)));
		}
		subFilter.setDelta(contentVADelta);
	}
}
