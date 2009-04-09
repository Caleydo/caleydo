package org.caleydo.rcp.views.swt;

import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.manager.event.EMediatorType;
import org.caleydo.core.manager.event.IEventContainer;
import org.caleydo.core.manager.event.IMediatorReceiver;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.rcp.perspective.GenomePerspective;
import org.caleydo.rcp.util.info.InfoArea;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * Search view contains gene and pathway search.
 * 
 * @author Marc Streit
 */
public class SelectionInfoView
	extends ViewPart
	implements IMediatorReceiver { //, ISizeProvider {
	public static final String ID = "org.caleydo.rcp.views.swt.SelectionInfoView";

	public static boolean bHorizontal = false;

	private Composite parentComposite;

	@Override
	public void createPartControl(Composite parent) {
		GeneralManager.get().getEventPublisher().addReceiver(EMediatorType.SELECTION_MEDIATOR, this);

		final Composite parentComposite = new Composite(parent, SWT.NULL);

		if (!GenomePerspective.bIsWideScreen) {
			bHorizontal = true;
		}

		if (bHorizontal) {
			parentComposite.setLayout(new GridLayout(10, false));
		}
		else {
			parentComposite.setLayout(new GridLayout(1, false));
		}

		this.parentComposite = parentComposite;

		addInfoBar();
	}

	@Override
	public void setFocus() {

	}

	@Override
	public void dispose() {
		super.dispose();

		GeneralManager.get().getEventPublisher().removeReceiver(EMediatorType.SELECTION_MEDIATOR, this);
	}

	private void addInfoBar() {

		Composite infoComposite = new Composite(parentComposite, SWT.NULL);
		infoComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		GridLayout layout;
		if (bHorizontal) {
			layout = new GridLayout(2, false);
		}
		else {
			layout = new GridLayout(1, false);
		}

		layout.marginBottom =
			layout.marginTop =
				layout.marginLeft =
					layout.marginRight = layout.horizontalSpacing = layout.verticalSpacing = 0;
		layout.marginHeight = layout.marginWidth = 0;

		infoComposite.setLayout(layout);
		InfoArea infoArea = new InfoArea();
		infoArea.createControl(infoComposite);
	}

	@Override
	public void handleExternalEvent(final IUniqueObject eventTrigger, IEventContainer eventContainer,
		EMediatorType eMediatorType) {

	}

//	@Override
//	public int computePreferredSize(boolean width, int availableParallel, int availablePerpendicular,
//		int preferredResult) {
//		// Set minimum size of the view
//		if (width == true)
//			return (int) SearchView.TOOLBAR_WIDTH;
//
//		return (int) SearchView.TOOLBAR_HEIGHT;
//	}

//	@Override
//	public int getSizeFlags(boolean width) {
//		return SWT.MIN;
//	}
}
