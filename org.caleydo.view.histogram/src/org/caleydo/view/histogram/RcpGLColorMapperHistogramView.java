package org.caleydo.view.histogram;

import java.util.ArrayList;

import org.caleydo.core.util.mapping.color.ChooseColorMappingDialog;
import org.caleydo.core.util.mapping.color.ColorMapper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class RcpGLColorMapperHistogramView extends RcpGLHistogramView {

	private CLabel colorMappingPreview;

	private ArrayList<CLabel> labels;

	public void redrawView() {

		colorMappingPreview = new CLabel(histoComposite, SWT.SHADOW_IN);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.heightHint = 10;
		gridData.grabExcessHorizontalSpace = true;
		colorMappingPreview.setLayoutData(gridData);
		colorMappingPreview.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				ChooseColorMappingDialog dialog = new ChooseColorMappingDialog(new Shell(SWT.APPLICATION_MODAL), dataDomain);
				// dialog.setPossibleDataDomains(availableDomains);
				dialog.setBlockOnOpen(true);
				dialog.open();
				ColorMapper.createColorMappingPreview(dialog.getColorMapper(), colorMappingPreview, labels);
			}
		});

		FillLayout fillLayout = new FillLayout();
		Composite labelComposite = new Composite(histoComposite, SWT.NULL);
		labelComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		labelComposite.setLayout(fillLayout);

		labels = new ArrayList<CLabel>(3);

		int numberOfMarkerPoints = dataDomain.getColorMapper().getMarkerPoints().size();

		for (int count = 0; count < numberOfMarkerPoints; count++) {
			CLabel label = new CLabel(labelComposite, SWT.NONE);
			labels.add(label);
			if (count == numberOfMarkerPoints - 1) {
				label.setAlignment(SWT.RIGHT);
			} else if (count > 0) {
				label.setAlignment(SWT.CENTER);
			}
		}

		ColorMapper.createColorMappingPreview(dataDomain.getColorMapper(), colorMappingPreview, labels);
	}

	@Override
	public void handleUpdateView() {
		colorMappingPreview.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				ColorMapper.createColorMappingPreview(dataDomain.getColorMapper(), colorMappingPreview, labels);
			}
		});
	}
}
