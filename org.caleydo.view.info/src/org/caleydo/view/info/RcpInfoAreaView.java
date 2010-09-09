package org.caleydo.view.info;

import java.util.ArrayList;

import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.rcp.view.rcp.CaleydoRCPViewPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * Search view contains gene and pathway search.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class RcpInfoAreaView extends CaleydoRCPViewPart {

	public static final String VIEW_ID = "org.caleydo.view.info";

	public static boolean bHorizontal = false;

	private Composite parentComposite;

	private InfoArea infoArea;

	@Override
	public void createPartControl(Composite parent) {
		final Composite parentComposite = new Composite(parent, SWT.NULL);

		// FIXME: when view plugin reorganizatin is done
		// if (!GenomePerspective.bIsWideScreen) {
		// bHorizontal = true;
		// }

		if (bHorizontal) {
			parentComposite.setLayout(new GridLayout(10, false));
		} else {
			parentComposite.setLayout(new GridLayout(1, false));
		}

		this.parentComposite = parentComposite;

		addInfoBar();

	}

	@Override
	public void setFocus() {

	}

	private void addInfoBar() {

		Composite infoComposite = new Composite(parentComposite, SWT.NULL);
		infoComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		GridLayout layout;
		if (bHorizontal) {
			layout = new GridLayout(2, false);
		} else {
			layout = new GridLayout(1, false);
		}

		layout.marginBottom = layout.marginTop = layout.marginLeft = layout.marginRight = layout.horizontalSpacing = layout.verticalSpacing = 0;
		layout.marginHeight = layout.marginWidth = 0;

		infoComposite.setLayout(layout);
		infoArea = new InfoArea();

		// FIXME use determineDataDomain for this
		infoArea.setDataDomain((ASetBasedDataDomain) DataDomainManager.get()
				.getDataDomain("org.caleydo.datadomain.genetic"));
		
		infoArea.registerEventListeners();
		infoArea.createControl(infoComposite);
	}

	@Override
	public void dispose() {
		super.dispose();

		infoArea.dispose();
	}

	@Override
	public void createDefaultSerializedView() {
		return ;
	}

	// @Override
	// public int computePreferredSize(boolean width, int availableParallel, int
	// availablePerpendicular,
	// int preferredResult) {
	// // Set minimum size of the view
	// if (width == true)
	// return (int) SearchView.TOOLBAR_WIDTH;
	//
	// return (int) SearchView.TOOLBAR_HEIGHT;
	// }

	// @Override
	// public int getSizeFlags(boolean width) {
	// return SWT.MIN;
	// }

}
