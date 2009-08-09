package org.caleydo.rcp.view.opengl;

import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.genetic.GeneticUseCase;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.canvas.storagebased.SerializedParallelCoordinatesView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

public class GLParCoordsView
	extends AGLViewPart {

	public static final String ID = SerializedParallelCoordinatesView.GUI_ID;

	/**
	 * Constructor.
	 */
	public GLParCoordsView() {
		super();
	}

	@Override 
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);

		if (memento == null) {
			SerializedParallelCoordinatesView serializedView = new SerializedParallelCoordinatesView();
			initSerializedView = serializedView;
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		IUseCase usecase = GeneralManager.get().getUseCase();
		if (usecase instanceof GeneticUseCase && ((GeneticUseCase) usecase).isPathwayViewerMode()) {
			MessageBox alert = new MessageBox(new Shell(), SWT.OK);
			alert.setMessage("Cannot create parallel coordinates in pathway viewer mode!");
			alert.open();

			dispose();
			return;
		}

		createGLCanvas();
		createGLEventListener(initSerializedView, glCanvas.getID());
		
		glEventListener.setViewGUIID(ID);
	}

	@Override
	public ASerializedView createDefaultSerializedView() {
		SerializedParallelCoordinatesView serializedView = new SerializedParallelCoordinatesView();
		return serializedView;
	}

	@Override
	public String getViewGUIID() {
		return ID;
	}

}