package org.caleydo.view.subgraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GL2;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.datadomain.IDataSupportDefinition;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.IMultiTablePerspectiveBasedView;
import org.caleydo.core.view.ViewManager;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;
import org.caleydo.core.view.listener.AddTablePerspectivesListener;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.util.multiform.MultiFormRenderer;
import org.caleydo.core.view.opengl.layout.util.multiform.MultiFormViewSwitchingBar;
import org.caleydo.core.view.opengl.layout2.AGLElementGLView;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementAdapter;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.data.PathwayTablePerspective;
import org.caleydo.datadomain.pathway.manager.EPathwayDatabaseType;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.eclipse.swt.widgets.Composite;

public class GLSubGraph extends AGLElementGLView implements IMultiTablePerspectiveBasedView, IGLRemoteRenderingView {

	public static String VIEW_TYPE = "org.caleydo.view.subgraph";

	public static String VIEW_NAME = "SubGraph";

	private LayoutManager layoutManager;

	private MultiFormRenderer pathMultiformRenderer = new MultiFormRenderer(this, false);

	private List<TablePerspective> tablePerspectives = new ArrayList<>();

	private Set<String> remoteRenderedPathwayMultiformViewIDs;

	// private Row baseRow;

	// private Column pathwayColumn;

	private AddTablePerspectivesListener addTablePerspectivesListener;

	// private Column pathColumn;

	private String pathEventSpace;

	private GLElementContainer baseContainer;
	private GLElementContainer pathwayColumn;

	/**
	 * Constructor.
	 *
	 * @param glCanvas
	 * @param viewLabel
	 * @param viewFrustum
	 */
	public GLSubGraph(IGLCanvas glCanvas, Composite parentComposite, ViewFrustum viewFrustum) {

		super(glCanvas, parentComposite, viewFrustum, VIEW_TYPE, VIEW_NAME);
		pathEventSpace = GeneralManager.get().getEventPublisher().createUniqueEventSpace();
		baseContainer = new GLElementContainer(GLLayouts.flowHorizontal(10));
		 pathwayColumn = new GLElementContainer(GLLayouts.flowVertical(10));
		 baseContainer.add(pathwayColumn, 0.4f);
	}

	@Override
	public void init(GL2 gl) {
		// PathwayImporter importer = new GpmlFormat();
		// try {
		// Pathway pathway = importer
		// .doImport(new File(
		// "D:\\Downloads\\wikipathways_Homo_sapiens_Curation-Tutorial_gpml\\Hs_BMP_signalling_and_regulation_WP1425_44981.gpml"));
		// List<Xref> refs = pathway.getDataNodeXrefs();
		// List<PathwayElement> elements = pathway.getDataObjects();
		// for (PathwayElement element : elements) {
		// if (element.getObjectType() == ObjectType.LINE) {
		// System.out.println(pathway.getElementById(element.getStartGraphRef()).getTextLabel() + "->"
		// + pathway.getElementById(element.getEndGraphRef()).getTextLabel());
		// }
		// }
		// // for (Xref ref : refs) {
		// // System.out.println("DB: " + ref.getDataSource().getFullName() + ", id: " + ref.getId());
		// // }
		// } catch (ConverterException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		super.init(gl);
		// displayListIndex = gl.glGenLists(1);
		// detailLevel = EDetailLevel.HIGH;
		// pathMultiformRenderer = new MultiFormRenderer(this, false);
		//
		// layoutManager = new LayoutManager(viewFrustum, pixelGLConverter);
		// baseRow = new Row();
		//
		// pathwayColumn = new Column();
		// pathwayColumn.setRatioSizeX(0.3f);
		// pathwayColumn.setRatioSizeY(1);
		// pathwayColumn.setBottomUp(false);
		//
		// pathColumn = new Column();
		// pathColumn.setRatioSizeX(0.7f);
		// pathColumn.setRatioSizeY(1);
		//
		// baseRow.add(pathwayColumn);
		// baseRow.add(pathColumn);
		// layoutManager.setBaseElementLayout(baseRow);
		// layoutManager.setUseDisplayLists(true);

	}

	// @Override
	// public void initLocal(GL2 gl) {
	// init(gl);
	// }
	//
	// @Override
	// public void initRemote(final GL2 gl, final AGLView glParentView, final GLMouseListener glMouseListener) {
	//
	// // Register keyboard listener to GL2 canvas
	// glParentView.getParentComposite().getDisplay().asyncExec(new Runnable() {
	// @Override
	// public void run() {
	// glParentView.getParentComposite().addKeyListener(glKeyListener);
	// }
	// });
	//
	// this.glMouseListener = glMouseListener;
	//
	// init(gl);
	// }

	// @Override
	// public void displayLocal(GL2 gl) {
	// pickingManager.handlePicking(this, gl);
	// display(gl);
	// if (busyState != EBusyState.OFF) {
	// renderBusyMode(gl);
	// }
	//
	// }
	//
	// @Override
	// public void displayRemote(GL2 gl) {
	// display(gl);
	// }
	//
	// @Override
	// public void display(GL2 gl) {
	//
	// if (isDisplayListDirty) {
	// layoutManager.updateLayout();
	// isDisplayListDirty = false;
	// }
	// layoutManager.render(gl);
	// checkForHits(gl);
	// }

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
		eventListeners.register(AddTablePerspectivesEvent.class, new AddTablePerspectivesListener().setHandler(this));
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
		if (remoteRenderedPathwayMultiformViewIDs == null) {

			PathwayDataDomain pathwayDataDomain = (PathwayDataDomain) DataDomainManager.get().getDataDomainByType(
					PathwayDataDomain.DATA_DOMAIN_TYPE);

			TablePerspective tablePerspective = tablePerspectives.get(0);

			Perspective oldRecordPerspective = tablePerspective.getRecordPerspective();
			Perspective newRecordPerspective = new Perspective(tablePerspective.getDataDomain(),
					oldRecordPerspective.getIdType());

			PerspectiveInitializationData data = new PerspectiveInitializationData();
			data.setData(oldRecordPerspective.getVirtualArray());

			newRecordPerspective.init(data);

			Perspective oldDimensionPerspective = tablePerspective.getDimensionPerspective();
			Perspective newDimensionPerspective = new Perspective(tablePerspective.getDataDomain(),
					oldDimensionPerspective.getIdType());
			data = new PerspectiveInitializationData();
			data.setData(oldDimensionPerspective.getVirtualArray());

			newDimensionPerspective.init(data);
			PathwayTablePerspective pathwayPathwayTablePerspective = new PathwayTablePerspective(
					tablePerspective.getDataDomain(), pathwayDataDomain, newRecordPerspective, newDimensionPerspective,
					PathwayManager.get().getPathwayByTitle("Glioma", EPathwayDatabaseType.KEGG));
			pathwayDataDomain.addTablePerspective(pathwayPathwayTablePerspective);

			 addPathwayMultiform(pathwayPathwayTablePerspective);

			pathwayPathwayTablePerspective = new PathwayTablePerspective(tablePerspective.getDataDomain(),
					pathwayDataDomain, newRecordPerspective, newDimensionPerspective, PathwayManager.get()
							.getPathwayByTitle("Glioma", EPathwayDatabaseType.KEGG));
			pathwayDataDomain.addTablePerspective(pathwayPathwayTablePerspective);

			 addPathwayMultiform(pathwayPathwayTablePerspective);

			Set<String> pathViewIDs = ViewManager.get().getRemotePlugInViewIDs(VIEW_TYPE, EEmbeddingID.PATH.id());

			for (String viewID : pathViewIDs) {
				pathMultiformRenderer.addPluginVisualization(viewID, getViewType(), EEmbeddingID.PATH.id(),
						tablePerspectives, pathEventSpace);
			}

			baseContainer.add(new GLElementAdapter(this, pathMultiformRenderer));
			// MultiFormViewSwitchingBar viewSwitchingBar = new MultiFormViewSwitchingBar(pathMultiformRenderer, this);
			// pathColumn.add(viewSwitchingBar);
			// ElementLayout multiformRendererLayout = new ElementLayout();
			// multiformRendererLayout.setRenderer(pathMultiformRenderer);
			// pathColumn.add(multiformRendererLayout);

			// setDisplayListDirty();
		}
	}

	private void addPathwayMultiform(PathwayTablePerspective pathwayPathwayTablePerspective) {
		remoteRenderedPathwayMultiformViewIDs = ViewManager.get().getRemotePlugInViewIDs(VIEW_TYPE,
				EEmbeddingID.PATHWAY_MULTIFORM.id());
		List<TablePerspective> pathwayTablePerspectives = new ArrayList<>(1);
		pathwayTablePerspectives.add(pathwayPathwayTablePerspective);

		// Different renderers should receive path updates from the beginning on, therefore no lazy creation.
		MultiFormRenderer renderer = new MultiFormRenderer(this, false);

		for (String viewID : remoteRenderedPathwayMultiformViewIDs) {
			renderer.addPluginVisualization(viewID, getViewType(), EEmbeddingID.PATHWAY_MULTIFORM.id(),
					pathwayTablePerspectives, pathEventSpace);
		}

		GLElementAdapter multiFormRendererAdapter = new GLElementAdapter(this, renderer);
		pathwayColumn.add(multiFormRendererAdapter);

		MultiFormViewSwitchingBar viewSwitchingBar = new MultiFormViewSwitchingBar(renderer, this);
		// baseContainer.add(new GLElementAdapter(this, viewSwitchingBar));
		//
		// ElementLayout multiformRendererLayout = new ElementLayout();
		// multiformRendererLayout.setRenderer(renderer);
		// column.add(multiformRendererLayout);
		// pathwayColumn.add(row);

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

	@Override
	protected GLElement createRoot() {

		return baseContainer;
	}

}
