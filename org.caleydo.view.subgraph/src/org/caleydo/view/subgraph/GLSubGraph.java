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
import org.caleydo.core.view.opengl.layout2.AGLElementGLView;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementAdapter;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.util.GLElementViewSwitchingBar;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.data.PathwayTablePerspective;
import org.caleydo.datadomain.pathway.manager.EPathwayDatabaseType;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.eclipse.swt.widgets.Composite;

public class GLSubGraph extends AGLElementGLView implements IMultiTablePerspectiveBasedView, IGLRemoteRenderingView {

	public static String VIEW_TYPE = "org.caleydo.view.subgraph";

	public static String VIEW_NAME = "SubGraph";

	private LayoutManager layoutManager;

	private List<TablePerspective> tablePerspectives = new ArrayList<>();

	private Set<String> remoteRenderedPathwayMultiformViewIDs;

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
		super.init(gl);

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
			PathwayTablePerspective pathwayTablePerspective = new PathwayTablePerspective(
					tablePerspective.getDataDomain(), pathwayDataDomain, newRecordPerspective, newDimensionPerspective,
					PathwayManager.get().getPathwayByTitle("Glioma", EPathwayDatabaseType.KEGG));
			pathwayDataDomain.addTablePerspective(pathwayTablePerspective);

			List<TablePerspective> pathwayTablePerspectives = new ArrayList<>(1);
			pathwayTablePerspectives.add(pathwayTablePerspective);

			addMultiformRenderer(pathwayTablePerspectives, EEmbeddingID.PATHWAY_MULTIFORM.id(), pathwayColumn);

			pathwayTablePerspective = new PathwayTablePerspective(tablePerspective.getDataDomain(), pathwayDataDomain,
					newRecordPerspective, newDimensionPerspective, PathwayManager.get().getPathwayByTitle("Apoptosis",
							EPathwayDatabaseType.KEGG));
			pathwayDataDomain.addTablePerspective(pathwayTablePerspective);

			addMultiformRenderer(pathwayTablePerspectives, EEmbeddingID.PATHWAY_MULTIFORM.id(), pathwayColumn);

			addMultiformRenderer(tablePerspectives, EEmbeddingID.PATH.id(), baseContainer);

		}
	}

	private void addMultiformRenderer(List<TablePerspective> tablePerspectives, String embeddingID,
			GLElementContainer parent) {

		GLElementContainer container = new GLElementContainer(GLLayouts.flowVertical(6));

		remoteRenderedPathwayMultiformViewIDs = ViewManager.get().getRemotePlugInViewIDs(VIEW_TYPE, embeddingID);

		// Different renderers should receive path updates from the beginning on, therefore no lazy creation.
		MultiFormRenderer renderer = new MultiFormRenderer(this, false);

		for (String viewID : remoteRenderedPathwayMultiformViewIDs) {
			renderer.addPluginVisualization(viewID, getViewType(), embeddingID, tablePerspectives, pathEventSpace);
		}

		GLElementAdapter multiFormRendererAdapter = new GLElementAdapter(this, renderer);
		container.add(multiFormRendererAdapter);

		GLElementViewSwitchingBar viewSwitchingBar = new GLElementViewSwitchingBar(renderer);
		container.add(viewSwitchingBar);

		parent.add(container);

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
