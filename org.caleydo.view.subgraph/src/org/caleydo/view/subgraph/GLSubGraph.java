package org.caleydo.view.subgraph;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GL2;

import org.bridgedb.Xref;
import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.datadomain.IDataSupportDefinition;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.IMultiTablePerspectiveBasedView;
import org.caleydo.core.view.ViewManager;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;
import org.caleydo.core.view.listener.AddTablePerspectivesListener;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.layout.util.BorderedAreaRenderer;
import org.caleydo.core.view.opengl.layout.util.multiform.IEmbeddedVisualizationInfo;
import org.caleydo.core.view.opengl.layout.util.multiform.MultiFormRenderer;
import org.caleydo.core.view.opengl.layout.util.multiform.MultiFormViewSwitchingBar;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.eclipse.swt.widgets.Composite;
import org.pathvisio.core.model.ConverterException;
import org.pathvisio.core.model.GpmlFormat;
import org.pathvisio.core.model.ObjectType;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.core.model.PathwayElement;
import org.pathvisio.core.model.PathwayImporter;

public class GLSubGraph extends AGLView implements IMultiTablePerspectiveBasedView, IGLRemoteRenderingView {

	public static String VIEW_TYPE = "org.caleydo.view.subgraph";

	public static String VIEW_NAME = "SubGraph";

	private LayoutManager layoutManager;

	private MultiFormRenderer multiFormRenderer;

	private List<TablePerspective> tablePerspectives = new ArrayList<>();

	private Set<String> remoteRenderedViewIDs;

	private Column baseColumn;

	private AddTablePerspectivesListener addTablePerspectivesListener;

	/**
	 * Constructor.
	 *
	 * @param glCanvas
	 * @param viewLabel
	 * @param viewFrustum
	 */
	public GLSubGraph(IGLCanvas glCanvas, Composite parentComposite, ViewFrustum viewFrustum) {

		super(glCanvas, parentComposite, viewFrustum, VIEW_TYPE, VIEW_NAME);
	}

	@Override
	public void init(GL2 gl) {
		PathwayImporter importer = new GpmlFormat();
		try {
			Pathway pathway = importer
					.doImport(new File(
							"D:\\Downloads\\wikipathways_Homo_sapiens_Curation-Tutorial_gpml\\Hs_BMP_signalling_and_regulation_WP1425_44981.gpml"));
			List<Xref> refs = pathway.getDataNodeXrefs();
			List<PathwayElement> elements = pathway.getDataObjects();
			for (PathwayElement element : elements) {
				if (element.getObjectType() == ObjectType.LINE) {
					System.out.println(pathway.getElementById(element.getStartGraphRef()).getTextLabel() + "->"
							+ pathway.getElementById(element.getEndGraphRef()).getTextLabel());
				}
			}
			// for (Xref ref : refs) {
			// System.out.println("DB: " + ref.getDataSource().getFullName() + ", id: " + ref.getId());
			// }
		} catch (ConverterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		displayListIndex = gl.glGenLists(1);
		detailLevel = EDetailLevel.HIGH;
		multiFormRenderer = new MultiFormRenderer(this, true);

		layoutManager = new LayoutManager(viewFrustum, pixelGLConverter);

		baseColumn = new Column();
		baseColumn.setRatioSizeX(1);
		baseColumn.setRatioSizeY(1);
		layoutManager.setBaseElementLayout(baseColumn);

	}

	@Override
	public void initLocal(GL2 gl) {
		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView, final GLMouseListener glMouseListener) {

		// Register keyboard listener to GL2 canvas
		glParentView.getParentComposite().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				glParentView.getParentComposite().addKeyListener(glKeyListener);
			}
		});

		this.glMouseListener = glMouseListener;

		init(gl);
	}

	@Override
	public void displayLocal(GL2 gl) {
		pickingManager.handlePicking(this, gl);
		display(gl);
		if (busyState != EBusyState.OFF) {
			renderBusyMode(gl);
		}

	}

	@Override
	public void displayRemote(GL2 gl) {
		display(gl);
	}

	@Override
	public void display(GL2 gl) {

		if (isDisplayListDirty) {
			layoutManager.updateLayout();
			isDisplayListDirty = false;
		}
		layoutManager.render(gl);
		checkForHits(gl);
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedSubGraphView serializedForm = new SerializedSubGraphView();
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public String toString() {
		return "Subgraph view";
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();

		addTablePerspectivesListener = new AddTablePerspectivesListener();
		addTablePerspectivesListener.setHandler(this);
		eventPublisher.addListener(AddTablePerspectivesEvent.class, addTablePerspectivesListener);

	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

		if (addTablePerspectivesListener != null) {
			eventPublisher.removeListener(addTablePerspectivesListener);
			addTablePerspectivesListener = null;
		}
	}

	@Override
	protected void destroyViewSpecificContent(GL2 gl) {
		gl.glDeleteLists(displayListIndex, 1);
		layoutManager.destroy(gl);
	}

	@Override
	public IDataSupportDefinition getDataSupportDefinition() {
		return DataSupportDefinitions.tableBased;
	}

	@Override
	public void addTablePerspective(TablePerspective newTablePerspective) {
		if (newTablePerspective != null) {
			tablePerspectives.add(newTablePerspective);
		}
		createRemoteRenderedViews();
	}

	@Override
	public void addTablePerspectives(List<TablePerspective> newTablePerspectives) {
		if (newTablePerspectives != null) {
			for (TablePerspective tablePerspective : newTablePerspectives) {
				if (tablePerspective != null) {
					tablePerspectives.add(tablePerspective);
				}
			}
		}
		createRemoteRenderedViews();
	}

	private void createRemoteRenderedViews() {
		if (remoteRenderedViewIDs == null) {
			remoteRenderedViewIDs = ViewManager.get().getRemotePlugInViewIDs(VIEW_TYPE, "test");
			int currentRendererID = -1;
			for (String viewID : remoteRenderedViewIDs) {
				currentRendererID = multiFormRenderer.addView(viewID, "test", tablePerspectives);
			}
			ALayoutRenderer customRenderer = new BorderedAreaRenderer();
			IEmbeddedVisualizationInfo visInfo = new IEmbeddedVisualizationInfo() {

				@Override
				public EScalingEntity getPrimaryWidthScalingEntity() {
					return null;
				}

				@Override
				public EScalingEntity getPrimaryHeightScalingEntity() {
					return null;
				}
			};

			multiFormRenderer.addLayoutRenderer(customRenderer, EIconTextures.ARROW_DOWN.getFileName(), visInfo, false);

			// TextureRenderer textureRenderer = new TextureRenderer("resources/tissue_images/ebene_0.bmp",
			// textureManager);
			List<String> areaImagePaths = new ArrayList<>(2);
			areaImagePaths.add("resources/tissue_images/ebene_1.bmp");
			areaImagePaths.add("resources/tissue_images/ebene_2.bmp");
			areaImagePaths.add("resources/tissue_images/ebene_3.bmp");
			areaImagePaths.add("resources/tissue_images/ebene_4.bmp");
			TissueRenderer textureRenderer = new TissueRenderer(this, "resources/tissue_images/ebene_0.bmp",
					areaImagePaths);
			multiFormRenderer
					.addLayoutRenderer(textureRenderer, EIconTextures.ARROW_DOWN.getFileName(), visInfo, false);

			MultiFormViewSwitchingBar viewSwitchingBar = new MultiFormViewSwitchingBar(multiFormRenderer, this);
			baseColumn.add(viewSwitchingBar);
			ElementLayout multiformRendererLayout = new ElementLayout();
			multiformRendererLayout.setRenderer(multiFormRenderer);
			baseColumn.add(multiformRendererLayout);
			setDisplayListDirty();
		}
	}

	@Override
	public List<TablePerspective> getTablePerspectives() {
		return new ArrayList<>(tablePerspectives);
	}

	@Override
	public void removeTablePerspective(int tablePerspectiveID) {
		// TODO: implement
	}

	@Override
	public List<AGLView> getRemoteRenderedViews() {
		// TODO: implement
		return null;
	}

	@Override
	public boolean isDataView() {
		return true;
	}

}
