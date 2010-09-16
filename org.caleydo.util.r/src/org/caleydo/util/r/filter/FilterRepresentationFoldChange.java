package org.caleydo.util.r.filter;

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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;

public class FilterRepresentationFoldChange
	extends AFilterRepresentation<ContentVADelta, ContentFilter> {

	private ISet set1;
	private ISet set2;
	
	private float foldChange = 2;

	public void create() {
		super.create();

		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {

				final Slider slider = new Slider(parentComposite, SWT.HORIZONTAL);

				final Label label = new Label(parentComposite, SWT.NULL);
				label.setText("                                                                                                        ");

				final Label foldChangeLabel = new Label(parentComposite, SWT.NULL);
				foldChangeLabel.setText("" + foldChange);

				final Button[] evaluatorCheckBox = new Button[3];

				evaluatorCheckBox[0] = new Button(parentComposite, SWT.CHECK);
				evaluatorCheckBox[0].setSelection(true);
				evaluatorCheckBox[0].setText("Less (down regulated)");

				evaluatorCheckBox[1] = new Button(parentComposite, SWT.CHECK);
				evaluatorCheckBox[1].setText("Greater (up regulated)");

				evaluatorCheckBox[2] = new Button(parentComposite, SWT.CHECK);
				evaluatorCheckBox[2].setText("Equal");

				evaluatorCheckBox[0].addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						evaluatorCheckBox[2].setSelection(false);
					}
				});

				evaluatorCheckBox[1].addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						evaluatorCheckBox[2].setSelection(false);
					}
				});

				evaluatorCheckBox[2].addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						evaluatorCheckBox[1].setSelection(false);
						evaluatorCheckBox[0].setSelection(false);
					}
				});

				slider.setMinimum(0);
				slider.setMaximum(100);
				slider.setIncrement(1);
				slider.setPageIncrement(10);
				slider.setSelection((int) (foldChange * 10));

				slider.addMouseListener(new MouseListener() {

					@Override
					public void mouseUp(MouseEvent e) {
						Double foldChangeRatio = slider.getSelection() / 10d;
						foldChangeLabel.setText("" + foldChangeRatio);

						if (evaluatorCheckBox[0].getSelection() == true) {
							FoldChangeSettings foldChangeSettings = new FoldChangeSettings(
									foldChangeRatio, FoldChangeEvaluator.GREATER);

							set1.getStatisticsResult().setFoldChangeSettings(set2,
									foldChangeSettings);
							set2.getStatisticsResult().setFoldChangeSettings(set1,
									foldChangeSettings);
						}

						if (evaluatorCheckBox[1].getSelection() == true) {
							FoldChangeSettings foldChangeSettings = new FoldChangeSettings(
									foldChangeRatio, FoldChangeEvaluator.LESS);

							set1.getStatisticsResult().setFoldChangeSettings(set2,
									foldChangeSettings);
							set2.getStatisticsResult().setFoldChangeSettings(set1,
									foldChangeSettings);
						}

						if (evaluatorCheckBox[2].getSelection() == true) {
							FoldChangeSettings foldChangeSettings = new FoldChangeSettings(
									foldChangeRatio, FoldChangeEvaluator.GREATER);

							set1.getStatisticsResult().setFoldChangeSettings(set2,
									foldChangeSettings);
							set2.getStatisticsResult().setFoldChangeSettings(set1,
									foldChangeSettings);
						}

//						int reducedNumberOfElements = set1.getStatisticsResult()
//								.getElementNumberOfFoldChangeReduction(set2);
//
//						label.setText("The fold change reduced results in a dataset of the size "
//								+ reducedNumberOfElements);
//						parentComposite.layout();
						
						createVADelta();
						filter.updateFilterManager();
					}

					@Override
					public void mouseDown(MouseEvent e) {
						// TODO Auto-generated method stub

					}

					@Override
					public void mouseDoubleClick(MouseEvent e) {
						// TODO Auto-generated method stub

					}
				});
				
				Monitor primary = parentComposite.getDisplay().getPrimaryMonitor();
				Rectangle bounds = primary.getBounds();
				Rectangle rect = parentComposite.getBounds();
				int x = bounds.x + (bounds.width - rect.width) / 2;
				int y = bounds.y + (bounds.height - rect.height) / 2;
				parentComposite.setLocation(x, y);
				parentComposite.pack();
				
				((Shell) parentComposite).open();
			}
		});
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
		}
		else
			createVADelta(filter);
	}
	
	private void createVADelta(ContentFilter subFilter) {

		ContentVADelta contentVADelta =
			new ContentVADelta(ISet.CONTENT, subFilter.getDataDomain().getContentIDType());
		ContentVirtualArray contentVA = subFilter.getDataDomain().getContentFilterManager().getBaseVA();
		
		double[] resultVector = set1.getStatisticsResult().getFoldChangeResult(set2).getFirst();
		FoldChangeSettings settings = set1.getStatisticsResult().getFoldChangeResult(set2).getSecond();

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
				case SAME:
					if (Math.abs(resultVector[contentIndex]) < foldChangeRatio)
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
