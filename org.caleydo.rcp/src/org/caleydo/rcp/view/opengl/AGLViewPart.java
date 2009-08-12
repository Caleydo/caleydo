package org.caleydo.rcp.view.opengl;

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
import org.caleydo.core.command.view.opengl.CmdCreateGLEventListener;
import org.caleydo.core.command.view.rcp.CmdViewCreateRcpGLCanvas;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.serialize.SerializationManager;
import org.caleydo.core.view.opengl.camera.EProjectionMode;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.rcp.Application;
import org.caleydo.rcp.view.CaleydoViewPart;
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
 */
public abstract class AGLViewPart
	extends CaleydoViewPart {

	protected Frame frameGL;
	protected GLCaleydoCanvas glCanvas;
	protected AGLEventListener glEventListener;

	/** serialized representation of the view to initialize the view itself */
	protected ASerializedView initSerializedView;
	
	/**
	 * Constructor.
	 */
	public AGLViewPart() {
		super();
	}

	protected void createGLCanvas() {
		CmdViewCreateRcpGLCanvas cmdCanvas =
			(CmdViewCreateRcpGLCanvas) GeneralManager.get().getCommandManager().createCommandByType(
				ECommandType.CREATE_VIEW_RCP_GLCANVAS);
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
	protected AGLEventListener createGLEventListener(ASerializedView serializedView, int iParentCanvasID) {

		ECommandType glViewType = serializedView.getCreationCommandType();
		IGeneralManager generalManager = GeneralManager.get();

		CmdCreateGLEventListener cmdView =
			(CmdCreateGLEventListener) generalManager.getCommandManager().createCommandByType(glViewType);
	
		IUseCase useCase;
		ISet set;
		
		if (glViewType == ECommandType.CREATE_GL_BUCKET_3D) {
			
			useCase = GeneralManager.get().getUseCase();
			set = useCase.getSet();
			
			cmdView.setAttributes(EProjectionMode.PERSPECTIVE, -1f, 1f, -1f, 1f, 1.9f, 100, set,
//			cmdView.setAttributes(EProjectionMode.PERSPECTIVE, -2f, 2f, -2f, 2f, 3.82f, 100, set,
				iParentCanvasID, 0, 0, -8, 0, 0, 0, 0);
		}
		else if (glViewType == ECommandType.CREATE_GL_GLYPH) {
			
			useCase = GeneralManager.get().getClinicalUseCase();
			set = useCase.getSet();
			
			cmdView.setAttributes(EProjectionMode.PERSPECTIVE, -1f, 1f, -1f, 1f, 2.9f, 100, set,
				iParentCanvasID, 0, 0, -8, 0, 0, 0, 0);
		}
		else {
			useCase = GeneralManager.get().getUseCase();
			set = useCase.getSet();
			
			cmdView.setAttributes(EProjectionMode.ORTHOGRAPHIC, 0, 8, 0, 8, -20, 20, set, iParentCanvasID);
		}

		cmdView.doCommand();

		AGLEventListener glView = cmdView.getCreatedObject();

		setGLData(glCanvas, glView);
		createPartControlGL();

		glView.setUseCase(useCase);
		glView.setSet(set);
				
		glView.setViewGUIID(getViewGUIID());
		glView.initFromSerializableRepresentation(serializedView);
		
		return glView;
	}

	@Override
	public void createPartControl(Composite parent) {
		parentComposite = new Composite(parent, SWT.EMBEDDED);
		// fillToolBar();
	}

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		
		String viewXml = null;
		if (memento != null) {
			viewXml = memento.getString("serialized");
		}
		if (viewXml != null) { // init view from memento
			SerializationManager serializationManager = GeneralManager.get().getSerializationManager();
			JAXBContext jaxbContext = serializationManager.getViewContext();
			Unmarshaller unmarshaller;
			try {
				unmarshaller = jaxbContext.createUnmarshaller();
			} catch (JAXBException ex) {
				throw new RuntimeException("could not create xml unmarshaller", ex);
			}
			
			StringReader xmlInputReader = new StringReader(viewXml);
			try {
				initSerializedView = (ASerializedView) unmarshaller.unmarshal(xmlInputReader);
			} catch (JAXBException ex) {
				throw new RuntimeException("could not deserialize view-xml", ex);
			}
		} else {
			initSerializedView = createDefaultSerializedView();
			
			// check if the view is within the list of stored views
			ASerializedView storedView = null;
			for (ASerializedView initView : Application.initializedStartViews) {
				if (initSerializedView.getClass().equals(initView.getClass())) {
					storedView = initView;
				}
			}
			if (storedView != null) {
				Application.initializedStartViews.remove(storedView);
				initSerializedView = storedView;
			}
		}
	}

	@Override
	public void saveState(IMemento memento) {

		SerializationManager serializationManager = GeneralManager.get().getSerializationManager();
		JAXBContext jaxbContext = serializationManager.getViewContext();
		Marshaller marshaller = null;
		try {
			marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		} catch (JAXBException ex) {
			throw new RuntimeException("could not create xml marshaller", ex);
		}

		StringWriter xmlOutputWriter = new StringWriter();
		try {
			marshaller.marshal(glEventListener.getSerializableRepresentation(), xmlOutputWriter);
			String xmlOutput = xmlOutputWriter.getBuffer().toString();
			memento.putString("serialized", xmlOutput);
		} catch (JAXBException ex) {
			ex.printStackTrace();
		}
	}

	public void setGLData(final GLCaleydoCanvas glCanvas, final AGLEventListener glEventListener) {
		this.glCanvas = glCanvas;
		this.glEventListener = glEventListener;
		this.iViewID = glEventListener.getID();
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

		GeneralManager.get().getViewGLCanvasManager().getGLEventListener(iViewID).destroy();
	}

	@Override
	public List<Integer> getAllViewIDs() {

		// FIXXXME: rcp-view id is the same as the first gl-view-id, so rcp-view-ids have to be omitted
		// List<Integer> ids = super.getAllViewIDs();

		List<Integer> ids = new ArrayList<Integer>();
		ids.addAll(this.getGLEventListener().getAllViewIDs());
		return ids;
	}

	public AGLEventListener getGLEventListener() {
		return glEventListener;
	}

	public GLCaleydoCanvas getGLCanvas() {
		return glCanvas;
	}

	/**
	 * Creates a default serialized form ({@link ASerializedView}) of the contained gl-view
	 * @return serialized form of the gl-view with default initialization
	 */
	public abstract ASerializedView createDefaultSerializedView();

	/**
	 * Returns the rcp-ID of the view
	 * @return rcp-ID of the view
	 */
	public abstract String getViewGUIID();
}
