package org.caleydo.view.grouper;

import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.datadomain.genetic.GeneticUseCase;
import org.caleydo.rcp.view.rcp.ARcpGLViewPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class RcpGLGrouperView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLGrouperView() {
		super();
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		IUseCase usecase = GeneralManager.get().getUseCase(dataDomain);
		if (usecase != null && usecase instanceof GeneticUseCase
				&& ((GeneticUseCase) usecase).isPathwayViewerMode()) {
			MessageBox alert = new MessageBox(new Shell(), SWT.OK);
			alert.setMessage("Cannot create grouper in pathway viewer mode!");
			alert.open();

			dispose();
			return;
		}

		createGLCanvas();
		AGLView view = createGLView(initSerializedView, glCanvas.getID());
		minSizeComposite.setView(view);
	}

	@Override
	public ASerializedView createDefaultSerializedView() {
		SerializedGrouperView serializedView = new SerializedGrouperView(
				dataDomain);
		return serializedView;
	}

	@Override
	public String getViewGUIID() {
		return GLGrouper.VIEW_ID;
	}

}