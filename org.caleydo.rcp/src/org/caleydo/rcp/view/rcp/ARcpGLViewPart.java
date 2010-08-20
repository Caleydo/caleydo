package org.caleydo.rcp.view.rcp;

import java.awt.Frame;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.view.CmdCreateView;
import org.caleydo.core.command.view.CmdViewCreateRcpGLCanvas;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.core.manager.datadomain.IDataDomainBasedView;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.serialize.SerializationManager;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.opengl.camera.EProjectionMode;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

/**
 * Shared object for all Caleydo RCP OpenGL views.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Werner Puff
 * @author Alexander Lex
 */
public abstract class ARcpGLViewPart
	extends CaleydoRCPViewPart {

	protected Frame frameGL;
	protected GLCaleydoCanvas glCanvas;
	protected MinimumSizeComposite minSizeComposite;

	/**
	 * Constructor.
	 */
	public ARcpGLViewPart() {
		super();
	}

	protected void createGLCanvas() {
		CmdViewCreateRcpGLCanvas cmdCanvas =
			(CmdViewCreateRcpGLCanvas) GeneralManager.get().getCommandManager()
				.createCommandByType(ECommandType.CREATE_VIEW_RCP_GLCANVAS);
		cmdCanvas.setAttributes(-1, false, false, false);
		cmdCanvas.doCommand();

		glCanvas = cmdCanvas.getCreatedObject();
		glCanvas.setParentComposite(parentComposite);
	}

	/**
	 * This class creates the GL event listener contained in a RCP view for a RCP view.
	 * 
	 * @param glViewType
	 *            The type of view. See {@link ECommandType}
	 * @param iParentCanvasID
	 *            the id of canvas where you want to render
	 * @return the ID of the view
	 */
	protected AGLView createGLView(ASerializedView serializedView, int iParentCanvasID) {

		String viewType = serializedView.getViewType();

		GeneralManager generalManager = GeneralManager.get();

		CmdCreateView cmdView =
			(CmdCreateView) generalManager.getCommandManager().createCommandByType(
				ECommandType.CREATE_GL_VIEW);
		cmdView.setViewID(viewType);
		if (viewType.equals("org.caleydo.view.bucket") || viewType.equals("org.caleydo.view.dataflipper")) {

			cmdView.setAttributes(EProjectionMode.PERSPECTIVE, -1f, 1f, -1f, 1f, 1.9f, 100, iParentCanvasID,
				0, 0, -8, 0, 0, 0, 0);

			// cmdView.setAttributes(EProjectionMode.PERSPECTIVE, -2f, 2f, -2f,
			// 2f, 3.82f, 100, set,

		}
		else if (viewType.equals("org.caleydo.view.glyph")) {

			cmdView.setAttributes(EProjectionMode.PERSPECTIVE, -1f, 1f, -1f, 1f, 2.9f, 100, iParentCanvasID,
				0, 0, -8, 0, 0, 0, 0);
		}
		else {
			cmdView.setAttributes(EProjectionMode.ORTHOGRAPHIC, 0, 8, 0, 8, -20, 20, iParentCanvasID);
		}

		String dataDomainType = determineDataDomain(serializedView);
		cmdView.setDataDomainType(dataDomainType);
		cmdView.doCommand();

		AGLView glView = cmdView.getCreatedObject();

		setGLData(glCanvas, glView);
		createPartControlGL();

		if (glView instanceof IDataDomainBasedView<?>) {
			dataDomainType = determineDataDomain(serializedView);

			((IDataDomainBasedView<IDataDomain>) glView).setDataDomain(DataDomainManager.getInstance()
				.getDataDomain(dataDomainType));
		}
		// glView.setViewID(getViewGUIID());
		glView.initFromSerializableRepresentation(serializedView);

		return glView;
	}

	@Override
	public void createPartControl(Composite parent) {
		minSizeComposite = new MinimumSizeComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		// fillToolBar();
		parentComposite = new Composite(minSizeComposite, SWT.EMBEDDED);
		minSizeComposite.setContent(parentComposite);
		minSizeComposite.setMinSize(0, 0);
		minSizeComposite.setExpandHorizontal(true);
		minSizeComposite.setExpandVertical(true);
	}

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);

		String viewXml = null;
		if (memento != null) {
			viewXml = memento.getString("serialized");
		}
		if (viewXml != null) { // init view from memento
			JAXBContext jaxbContext = viewContext;
			Unmarshaller unmarshaller;
			try {
				unmarshaller = jaxbContext.createUnmarshaller();
			}
			catch (JAXBException ex) {
				throw new RuntimeException("could not create xml unmarshaller", ex);
			}

			StringReader xmlInputReader = new StringReader(viewXml);
			try {
				initSerializedView = (ASerializedView) unmarshaller.unmarshal(xmlInputReader);
			}
			catch (JAXBException ex) {
				throw new RuntimeException("could not deserialize view-xml", ex);
			}
		}
		else {
			initSerializedView = createDefaultSerializedView();

			// // check if the view is within the list of stored views
			// ASerializedView storedView = null;
			// for (String initView : Application.initializedStartViews) {
			// if (initSerializedView.getViewType().equals(initView)) {
			// storedView = initView;
			// }
			// }
			// if (storedView != null) {
			// Application.initializedStartViews.remove(storedView);
			// initSerializedView = storedView;
			// }
		}
	}

	@Override
	public void saveState(IMemento memento) {

		JAXBContext jaxbContext = viewContext;
		Marshaller marshaller = null;
		try {
			marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		}
		catch (JAXBException ex) {
			throw new RuntimeException("could not create xml marshaller", ex);
		}

		StringWriter xmlOutputWriter = new StringWriter();
		try {
			marshaller.marshal(((AGLView) view).getSerializableRepresentation(), xmlOutputWriter);
			String xmlOutput = xmlOutputWriter.getBuffer().toString();
			memento.putString("serialized", xmlOutput);
		}
		catch (JAXBException ex) {
			ex.printStackTrace();
		}
	}

	public void setGLData(final GLCaleydoCanvas glCanvas, final AGLView glView) {
		this.glCanvas = glCanvas;
		this.view = glView;
	}

	public void createPartControlGL() {
		if (frameGL == null) {
			frameGL = SWT_AWT.new_Frame(parentComposite);
		}

		frameGL.add(glCanvas);
	}

	@Override
	public void setFocus() {
		// final IToolBarManager toolBarManager =
		// getViewSite().getActionBars().getToolBarManager();
		// toolBarManager.add(ActionFactory.QUIT.create(this.getViewSite().getWorkbenchWindow()));
		// toolBarManager.update(true);
	}

	@Override
	public void dispose() {
		super.dispose();
		getGLView().destroy();
	}

	@Override
	public List<IView> getAllViews() {

		// FIXXXME: rcp-view id is the same as the first gl-view-id, so
		// rcp-view-ids have to be omitted
		// List<Integer> ids = super.getAllViewIDs();

		List<IView> views = new ArrayList<IView>();
		views.add(getGLView());
		if (getGLView() instanceof IGLRemoteRenderingView) {
			for (AGLView view : ((IGLRemoteRenderingView) getGLView()).getRemoteRenderedViews()) {
				views.add(view);
			}
		}

		return views;
	}

	public AGLView getGLView() {
		return (AGLView) view;
	}

	public GLCaleydoCanvas getGLCanvas() {
		return glCanvas;
	}

	/**
	 * Creates a default serialized form ({@link ASerializedView}) of the contained gl-view
	 * 
	 * @return serialized form of the gl-view with default initialization
	 */
	public abstract ASerializedView createDefaultSerializedView();

	/**
	 * Returns the rcp-ID of the view
	 * 
	 * @return rcp-ID of the view
	 */
	public abstract String getViewGUIID();
}
