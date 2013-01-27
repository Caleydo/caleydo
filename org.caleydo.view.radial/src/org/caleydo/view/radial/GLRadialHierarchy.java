/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.radial;

import gleem.linalg.Vec2f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import javax.management.InvalidAttributeValueException;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.graph.tree.AHierarchyElement;
import org.caleydo.core.data.graph.tree.ClusterNode;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.selection.ElementConnectionInformation;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.events.SelectionCommandListener;
import org.caleydo.core.event.data.SelectionCommandEvent;
import org.caleydo.core.event.view.RedrawViewEvent;
import org.caleydo.core.id.IDType;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.clusterer.EDrawingStateType;
import org.caleydo.core.util.clusterer.EPDDrawingStrategyType;
import org.caleydo.core.util.color.mapping.UpdateColorMappingListener;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.contextmenu.item.BookmarkMenuItem;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.ATableBasedView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.listener.RedrawViewListener;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.radial.contextmenu.DetailOutsideItem;
import org.caleydo.view.radial.event.ChangeColorModeEvent;
import org.caleydo.view.radial.event.DetailOutsideEvent;
import org.caleydo.view.radial.event.GoBackInHistoryEvent;
import org.caleydo.view.radial.event.GoForthInHistoryEvent;
import org.caleydo.view.radial.event.SetMaxDisplayedHierarchyDepthEvent;
import org.caleydo.view.radial.listener.ChangeColorModeListener;
import org.caleydo.view.radial.listener.DetailOutsideListener;
import org.caleydo.view.radial.listener.GoBackInHistoryListener;
import org.caleydo.view.radial.listener.GoForthInHistoryListener;
import org.caleydo.view.radial.listener.SetMaxDisplayedHierarchyDepthListener;
import org.eclipse.swt.widgets.Composite;

/**
 * This class is responsible for rendering the radial hierarchy and receiving
 * user events and events from other views.
 *
 * @author Christian Partl
 */
public class GLRadialHierarchy extends ATableBasedView {

	public static String VIEW_TYPE = "org.caleydo.view.radial";

		public static String VIEW_NAME = "Radial Hierarchy";


	public static final int DISP_HIER_DEPTH_DEFAULT = 14;
	private static final int MIN_PIXELS_PER_DISPLAYED_LEVEL = 10;
	private static final int MIN_DISPLAY_WIDTH = 350;
	private static final int MIN_DISPLAY_HEIGHT = 300;

	private int iMaxDisplayedHierarchyDepth;
	private int iUpwardNavigationSliderID;
	private int iUpwardNavigationSliderButtonID;
	private int iUpwardNavigationSliderBodyID;

	private boolean bIsAnimationActive;
	private boolean bIsNewSelection;

	/** Tree where all partial discs are stored. */
	private Tree<PartialDisc> partialDiscTree;
	/** Hashmap for partial disc picking. */
	private HashMap<Integer, PartialDisc> hashPartialDiscs;

	/** The root element of the partial disc tree. */
	private PartialDisc pdRealRootElement;
	/** The partial disc that is currently displayed as root. */
	private PartialDisc pdCurrentRootElement;
	/**
	 * The partial disc that is currently "selected" regarding the current
	 * drawing state. For instance, in DrawingStateFullHierarchy the current
	 * root element is currently selected, while in DrawingStateDetailOutside
	 * the currently selected element is the root element of the detail view.
	 */
	private PartialDisc pdCurrentSelectedElement;

	private ADataEventManager dataEventManager;
	private DrawingController drawingController;
	private DrawingStrategyManager drawingStrategyManager;
	private NavigationHistory navigationHistory;
	private OneWaySlider upwardNavigationSlider;
	private Rectangle controlBox;

	private GoBackInHistoryListener goBackInHistoryListener;
	private GoForthInHistoryListener goForthInHistoryListener;
	private ChangeColorModeListener changeColorModeListener;
	private SetMaxDisplayedHierarchyDepthListener setMaxDisplayedHierarchyDepthListener;
	private DetailOutsideListener detailOutsideListener;
	private UpdateColorMappingListener updateViewListener;
	private SelectionCommandListener clearSelectionsListener;

	private SelectionManager selectionManager;
	boolean bUseDetailLevel = true;

	private RadialHierarchyRenderStyle renderStyle;

	/**
	 * Constructor.
	 */
	public GLRadialHierarchy(IGLCanvas glCanvas, Composite parentComposite,
			ViewFrustum viewFrustum) {

		super(glCanvas, parentComposite, viewFrustum, VIEW_TYPE, VIEW_NAME);


		ArrayList<SelectionType> alSelectionTypes = new ArrayList<SelectionType>();
		alSelectionTypes.add(SelectionType.NORMAL);
		alSelectionTypes.add(SelectionType.MOUSE_OVER);
		alSelectionTypes.add(SelectionType.SELECTION);

		renderStyle = new RadialHierarchyRenderStyle(viewFrustum);
		renderStyle.setMinViewDimensions(MIN_DISPLAY_WIDTH
				+ MIN_PIXELS_PER_DISPLAYED_LEVEL * DISP_HIER_DEPTH_DEFAULT,
				MIN_DISPLAY_HEIGHT + MIN_PIXELS_PER_DISPLAYED_LEVEL
						* DISP_HIER_DEPTH_DEFAULT, this);

		hashPartialDiscs = new HashMap<Integer, PartialDisc>();
		partialDiscTree = new Tree<PartialDisc>();
		// iMaxDisplayedHierarchyDepth = DISP_HIER_DEPTH_DEFAULT;
		navigationHistory = new NavigationHistory(this, null);
		drawingController = new DrawingController(this, navigationHistory);
		drawingStrategyManager = new DrawingStrategyManager(dataDomain.getColorMapper());
		navigationHistory.setDrawingController(drawingController);
		iUpwardNavigationSliderButtonID = 0;
		iUpwardNavigationSliderID = 0;
		iUpwardNavigationSliderBodyID = 0;

		glKeyListener = new GLRadialHierarchyKeyListener(this);

		bIsAnimationActive = false;
		// bIsNewSelection = false;
	}

	@Override
	public void init(GL2 gl) {
		displayListIndex = gl.glGenLists(1);
		gl.glEnable(GL.GL_LINE_SMOOTH);
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
	}

	@Override
	public void initLocal(GL2 gl) {

		// Register keyboard listener to GL2 canvas
		parentComposite.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				parentComposite.addKeyListener(glKeyListener);
			}
		});

		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView,
			final GLMouseListener glMouseListener) {

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

	/**
	 * Initializes a new hierarchy of partial discs according to a tree of
	 * cluster nodes.
	 *
	 * @param tree
	 *            Tree of cluster nodes which is used to build the partial disc
	 *            tree.
	 */
	/**
	 * Initializes a new hierarchy of partial discs according to a tree of
	 * cluster nodes and other parameters.
	 *
	 * @param <E>
	 *            Concrete Type of IHierarchyData
	 * @param tree
	 *            Tree of hierarchy data objects which is used to build the
	 *            partial disc tree.
	 * @param idType
	 *            IDType of the hierarchy data objects.
	 * @param dataEventManager
	 *            Concrete DataEventManager that is responsible for handling and
	 *            triggering data specific events.
	 * @param alColorModes
	 *            List of drawing strategies that shall be used as color modes.
	 */
	public <E extends AHierarchyElement<E>> void initHierarchy(Tree<E> tree, E heRoot,
			ADataEventManager dataEventManager,
			ArrayList<EPDDrawingStrategyType> alColorModes) {

		hashPartialDiscs.clear();
		selectionManager = new SelectionManager(tree.getNodeIDType());
		partialDiscTree = new Tree<PartialDisc>();
		navigationHistory.reset();
		drawingController.setDrawingState(EDrawingStateType.DRAWING_STATE_FULL_HIERARCHY);
		LabelManager.get().clearLabels();
		drawingStrategyManager.init(pickingManager, uniqueID, alColorModes);

		PartialDisc pdRoot = new PartialDisc(partialDiscTree, heRoot,
				drawingStrategyManager.getDefaultDrawingStrategy());
		partialDiscTree.setRootNode(pdRoot);
		partialDiscTree.setLeafIDType(tree.getLeaveIDType());
		partialDiscTree.setNodeIDType(tree.getNodeIDType());
		hashPartialDiscs.put(heRoot.getID(), pdRoot);
		// selectionManager.initialAdd(heRoot.getID());
		buildTree(tree, heRoot, pdRoot);
		pdRoot.calculateLargestChildren();
		iMaxDisplayedHierarchyDepth = DISP_HIER_DEPTH_DEFAULT;

		this.dataEventManager = dataEventManager;
		this.dataEventManager.registerEventListeners();

		pdCurrentRootElement = pdRoot;
		pdCurrentSelectedElement = pdRoot;
		pdRealRootElement = pdRoot;

		navigationHistory.addNewHistoryEntry(drawingController.getCurrentDrawingState(),
				pdCurrentRootElement, pdCurrentSelectedElement,
				iMaxDisplayedHierarchyDepth);

		selectionManager.addToType(SelectionType.SELECTION,
				pdCurrentRootElement.getElementID());

		controlBox = new Rectangle(0, 0, 0.3f, 0.2f);
		upwardNavigationSlider = new OneWaySlider(new Vec2f(controlBox.getMinX() + 0.1f,
				controlBox.getMinY() + 0.1f), 0.2f, 1f,
				pdRealRootElement.getHierarchyLevel(), 1, 0,
				pdRealRootElement.getDepth() - 1);
		upwardNavigationSlider.setMinSize(80);

	}

	/**
	 * Recursively builds the partial disc tree according to a hierarchy data
	 * tree. Note that the root element of the partial disc tree has to be set
	 * separately.
	 *
	 * @param tree
	 *            Tree of cluster nodes which is used to build the partial disc
	 *            tree.
	 * @param hierarchyElement
	 *            Current parent hierarchy element whose children are used for
	 *            the creation of the children of partialDisc. Initially this
	 *            variable should be the root element of the hierarchy element
	 *            tree.
	 * @param partialDisc
	 *            Current parent partial disc whose children will be created.
	 *            Initially this variable should be the root element of the
	 *            partial disc tree.
	 */
	private <E extends AHierarchyElement<E>> void buildTree(Tree<E> tree,
			E hierarchyElement, PartialDisc partialDisc) {

		ArrayList<E> alChildNodes = tree.getChildren(hierarchyElement);
		ArrayList<PartialDisc> alChildDiscs = new ArrayList<PartialDisc>();

		if (alChildNodes != null) {
			for (E heChild : alChildNodes) {
				PartialDisc pdCurrentChildDisc = new PartialDisc(partialDiscTree,
						heChild, drawingStrategyManager.getDefaultDrawingStrategy());
				try {
					alChildDiscs.add(pdCurrentChildDisc);
					partialDiscTree.addChild(partialDisc, pdCurrentChildDisc);
					hashPartialDiscs.put(heChild.getID(), pdCurrentChildDisc);
					// selectionManager.initialAdd(heChild.getID());
					buildTree(tree, heChild, pdCurrentChildDisc);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	// TODO: Remove when really not needed any more.
	// private void initTestHierarchy() {
	//
	// //Tree<ClusterNode> tree = new Tree<ClusterNode>();
	// TreePorter treePorter = new TreePorter();
	//
	// // "data/clustering/experiment_tree_nonbinar.xml"
	// try {
	// tree = treePorter.importTree("data/clustering/hcc_5000.xml");
	// }
	// catch (FileNotFoundException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// catch (JAXBException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// // initHierarchy(tree);
	// }

	@Override
	public void setDetailLevel(EDetailLevel detailLevel) {
		if (bUseDetailLevel) {
			super.setDetailLevel(detailLevel);
			// renderStyle.setDetailLevel(detailLevel);
		}

	}

	@Override
	public void displayLocal(GL2 gl) {

		if (!lazyMode)
			pickingManager.handlePicking(this, gl);

		display(gl);

		if (!lazyMode)
			checkForHits(gl);

		if (busyState != EBusyState.OFF) {
			renderBusyMode(gl);
		}
	}

	@Override
	public void displayRemote(GL2 gl) {

		display(gl);

		// glMouseListener.resetEvents();
	}

	@Override
	public void display(GL2 gl) {

		if (isDisplayListDirty && !bIsAnimationActive) {
			buildDisplayList(gl, displayListIndex);
			isDisplayListDirty = false;
		}

		if (pdRealRootElement != null && pdCurrentRootElement != null) {

			if (upwardNavigationSlider.handleDragging(gl, glMouseListener)) {
				updateHierarchyAccordingToNavigationSlider();
			}
			// clipToFrustum(gl);
			//
			if (bIsAnimationActive) {
				float fXCenter = viewFrustum.getWidth() / 2;
				float fYCenter = viewFrustum.getHeight() / 2;

				gl.glLoadIdentity();
				upwardNavigationSlider.draw(gl, pickingManager, textureManager, uniqueID,
						iUpwardNavigationSliderID, iUpwardNavigationSliderButtonID,
						iUpwardNavigationSliderBodyID);

				float fCurrentSliderWidth = upwardNavigationSlider.getScaledWidth(gl);
				float fCurrentSliderHeight = upwardNavigationSlider.getScaledHeight(gl);

				controlBox.setRectangle(0, 0, fCurrentSliderWidth * 2,
						fCurrentSliderHeight + fCurrentSliderWidth);
				LabelManager.get().setControlBox(controlBox);
				drawingController.draw(fXCenter, fYCenter, gl, new GLU());
			} else
				gl.glCallList(displayListIndex);
		} else {
			renderSymbol(gl, EIconTextures.RADIAL_SYMBOL, 0.5f);
		}
		if (!lazyMode)
			checkForHits(gl);
	}

	/**
	 * Builds the display list for a given display list index.
	 *
	 * @param gl
	 *            Instance of GL2.
	 * @param iGLDisplayListIndex
	 *            Index of the display list.
	 */
	private void buildDisplayList(final GL2 gl, int iGLDisplayListIndex) {
		if (pdRealRootElement != null && pdCurrentRootElement != null) {

			gl.glNewList(iGLDisplayListIndex, GL2.GL_COMPILE);

			float fXCenter = viewFrustum.getWidth() / 2;
			float fYCenter = viewFrustum.getHeight() / 2;

			gl.glLoadIdentity();

			upwardNavigationSlider.draw(gl, pickingManager, textureManager, uniqueID,
					iUpwardNavigationSliderID, iUpwardNavigationSliderButtonID,
					iUpwardNavigationSliderBodyID);

			float fCurrentSliderWidth = upwardNavigationSlider.getScaledWidth(gl);
			float fCurrentSliderHeight = upwardNavigationSlider.getScaledHeight(gl);

			controlBox.setRectangle(0, 0, fCurrentSliderWidth * 2, fCurrentSliderHeight
					+ fCurrentSliderWidth);
			LabelManager.get().setControlBox(controlBox);
			drawingController.draw(fXCenter, fYCenter, gl, new GLU());

			gl.glEndList();
		} else {
			renderSymbol(gl, EIconTextures.RADIAL_SYMBOL, 0.5f);
		}
	}

	/**
	 * Updates the current root element according to the position of the upward
	 * navigation silder, i.e. a parent of the current root element with the
	 * hierarchy level of the slider position will be the new root element.
	 */
	private void updateHierarchyAccordingToNavigationSlider() {
		PartialDisc pdNewRootElement = pdCurrentRootElement
				.getParentWithLevel(upwardNavigationSlider.getSelectedValue());
		if (pdNewRootElement != null) {
			pdCurrentRootElement = pdNewRootElement;

			bIsNewSelection = true;

			PartialDisc pdSelectedElement = drawingController.getCurrentDrawingState()
					.getSelectedElement();
			if (pdSelectedElement != null) {
				pdCurrentSelectedElement = pdSelectedElement;
				pdSelectedElement.setCurrentStartAngle(0);
			}
			navigationHistory.addNewHistoryEntry(
					drawingController.getCurrentDrawingState(), pdCurrentRootElement,
					pdCurrentSelectedElement, iMaxDisplayedHierarchyDepth);
			setDisplayListDirty();

			setNewSelection(SelectionType.SELECTION, pdCurrentSelectedElement);
		}
	}

	@Override
	protected void handlePickingEvents(PickingType pickingType, PickingMode pickingMode,
			int externalID, Pick pick) {
		if (detailLevel == EDetailLevel.VERY_LOW) {
			return;
		}
		switch (pickingType) {

		case RAD_HIERARCHY_PDISC_SELECTION:

			PartialDisc pdPickedElement = hashPartialDiscs.get(externalID);

			switch (pickingMode) {
			case CLICKED:
				if (pdPickedElement != null)
					drawingController.handleSelection(pdPickedElement);
				break;

			case MOUSE_OVER:
				if (pdPickedElement != null)
					drawingController.handleMouseOver(pdPickedElement);
				break;

			case RIGHT_CLICKED:
				if (pdPickedElement != null) {
					// Prevent handling of non genetic data in context
					// menu
					if (!dataDomain.getDataDomainID().equals(
							"org.caleydo.datadomain.genetic"))
						break;

					if (!pdPickedElement.hasChildren()) {
						AContextMenuItem menuItem = new BookmarkMenuItem("Bookmark "
								+ dataDomain.getRecordLabel(dataDomain.getRecordIDType(),
										externalID), dataDomain.getRecordIDType(),
								externalID, dataDomain.getDataDomainID());
						contextMenuCreator.addContextMenuItem(menuItem);
					} else {
						AContextMenuItem menuItem = new DetailOutsideItem(externalID);
						contextMenuCreator.addContextMenuItem(menuItem);
					}

					break;
				}
				drawingController.handleAlternativeSelection(pdPickedElement);
				break;

			default:
				return;
			}
			break;

		case RAD_HIERARCHY_SLIDER_SELECTION:
			switch (pickingMode) {
			case CLICKED:
				if (externalID == iUpwardNavigationSliderID) {
					if (upwardNavigationSlider.handleSliderSelection(pickingType)) {
						updateHierarchyAccordingToNavigationSlider();
						setDisplayListDirty();
					}
				}
				break;

			default:
				return;
			}
			break;

		case RAD_HIERARCHY_SLIDER_BUTTON_SELECTION:
			switch (pickingMode) {
			case CLICKED:
				if (externalID == iUpwardNavigationSliderButtonID) {
					if (upwardNavigationSlider.handleSliderSelection(pickingType)) {
						updateHierarchyAccordingToNavigationSlider();
						setDisplayListDirty();
					}
				}
				break;

			default:
				return;
			}
			break;

		case RAD_HIERARCHY_SLIDER_BODY_SELECTION:
			switch (pickingMode) {
			case CLICKED:
				if (externalID == iUpwardNavigationSliderBodyID) {
					if (upwardNavigationSlider.handleSliderSelection(pickingType)) {
						updateHierarchyAccordingToNavigationSlider();
						setDisplayListDirty();
					}
				}
				break;

			default:
				return;
			}
			break;
		default:
			break;
		}
	}

	/**
	 * Go one step back in navigation history.
	 */
	public void goBackInHistory() {
		navigationHistory.goBack();
	}

	/**
	 * Go one step back in navigation history.
	 */
	public void goForthInHistory() {
		// handleMouseOver(10);
		navigationHistory.goForth();
	}

	/**
	 * Change the color mode to the next one.
	 */
	public void changeColorMode() {

		// ClusterNodeSelectionEvent event = new ClusterNodeSelectionEvent();
		// event.setClusterNumber(1073741840);
		// event.setSelectionType(SelectionType.SELECTION);
		//
		// eventPublisher.triggerEvent(event);

		drawingStrategyManager.setNextColorModeStrategyDefault();
		setDisplayListDirty();
	}

	/**
	 * Gets the real root element of the hierarchy.
	 *
	 * @return Real root element of the hierarchy.
	 */
	public PartialDisc getRealRootElement() {
		return pdRealRootElement;
	}

	/**
	 * Gets the element that will be displayed as root element.
	 *
	 * @return Element that will be displayed as root element.
	 */
	public PartialDisc getCurrentRootElement() {
		return pdCurrentRootElement;
	}

	/**
	 * Sets an element that will be displayed as root element.
	 *
	 * @param pdCurrentRootElement
	 *            New element that will be displayed as root element.
	 */
	public void setCurrentRootElement(PartialDisc pdCurrentRootElement) {
		this.pdCurrentRootElement = pdCurrentRootElement;
		upwardNavigationSlider.setSelectedValue(pdCurrentRootElement.getHierarchyLevel());
	}

	/**
	 * Gets the current selected element.
	 *
	 * @return The current selected element.
	 */
	public PartialDisc getCurrentSelectedElement() {
		return pdCurrentSelectedElement;
	}

	/**
	 * Sets the current selected element.
	 *
	 * @param pdCurrentSelectedElement
	 *            Element that will be the current selected element.
	 */
	public void setCurrentSelectedElement(PartialDisc pdCurrentSelectedElement) {
		this.pdCurrentSelectedElement = pdCurrentSelectedElement;
	}

	/**
	 * Returns whether an element has been newly selected, either by the view
	 * itself or externally.
	 *
	 * @return true if an element has been newly selected, false otherwise.
	 */
	public boolean isNewSelection() {
		return bIsNewSelection;
	}

	/**
	 * Gets the maximum displayed hierarchy depth.
	 *
	 * @return The maximum displayed hierarchy depth.
	 */
	public int getMaxDisplayedHierarchyDepth() {
		return iMaxDisplayedHierarchyDepth;
	}

	/**
	 * @return SelectionManager of the radial hierarchy view.
	 */
	public SelectionManager getSelectionManager() {
		return selectionManager;
	}

	/**
	 * @return Type of the currently active drawing state.
	 */
	public EDrawingStateType getCurrentDrawingStateType() {
		return drawingController.getCurrentDrawingState().getType();
	}

	/**
	 * Sets the maximum displayed hierarchy depth.
	 *
	 * @param iMaxDisplayedHierarchyDepth
	 */
	public void setMaxDisplayedHierarchyDepth(int iMaxDisplayedHierarchyDepth) {
		if (this.iMaxDisplayedHierarchyDepth != iMaxDisplayedHierarchyDepth) {

			renderStyle.setMinViewDimensions(MIN_DISPLAY_WIDTH
					+ MIN_PIXELS_PER_DISPLAYED_LEVEL * iMaxDisplayedHierarchyDepth,
					MIN_DISPLAY_HEIGHT + MIN_PIXELS_PER_DISPLAYED_LEVEL
							* iMaxDisplayedHierarchyDepth, this);

			bIsNewSelection = false;
			this.iMaxDisplayedHierarchyDepth = iMaxDisplayedHierarchyDepth;
			navigationHistory
					.setCurrentMaxDisplayedHierarchyDepth(iMaxDisplayedHierarchyDepth);
			setDisplayListDirty();
		}
	}

	/**
	 * Sets if an animation is currently active. If true, no display lists will
	 * be built or called.
	 *
	 * @param bIsAnimationActive
	 *            Determines if the animation is active.
	 */
	public void setAnimationActive(boolean bIsAnimationActive) {
		this.bIsAnimationActive = bIsAnimationActive;
	}

	/**
	 * Returns the partial disc with the specified ID.
	 *
	 * @param elementID
	 *            ID of the partial disc to obtain.
	 * @return Partial disc with the specified ID, null if no partial disc with
	 *         the specified ID is present.
	 */
	public PartialDisc getPartialDisc(int elementID) {
		return hashPartialDiscs.get(elementID);
	}

	/**
	 * @return The current DrawingStrategyManager of the radial hierarchy view.
	 */
	public DrawingStrategyManager getDrawingStrategyManager() {
		return drawingStrategyManager;
	}

	/**
	 * A new selection will be set in the selection manager with the specified
	 * parameters. A ClusterNodeSelectionEvent with the new selection will be
	 * triggered. If the selected element corresponds to a gene, a
	 * SelectionUpdateEvent will also be triggered.
	 *
	 * @param selectionType
	 *            Type of selection.
	 * @param pdSelected
	 *            Element that has been selected.
	 */
	public void setNewSelection(SelectionType selectionType, PartialDisc pdSelected) {

		selectionManager.clearSelections();
		selectionManager.addToType(selectionType, pdSelected.getElementID());

		dataEventManager.triggerDataSelectionEvents(selectionType, pdSelected);

		if (selectionType == SelectionType.SELECTION) {
			bIsNewSelection = true;
		} else {
			bIsNewSelection = false;
		}
	}

	public void setNewSelection(boolean bIsNewSelection) {
		this.bIsNewSelection = bIsNewSelection;
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedRadialHierarchyView serializedForm = new SerializedRadialHierarchyView(
				this);
		serializedForm.setMaxDisplayedHierarchyDepth(iMaxDisplayedHierarchyDepth);
		serializedForm.setNewSelection(bIsNewSelection);
		serializedForm.setDefaultDrawingStrategyType(drawingStrategyManager
				.getDefaultDrawingStrategy().getDrawingStrategyType());

		ADrawingState currentDrawingState = drawingController.getCurrentDrawingState();

		if (pdCurrentRootElement != null) {
			if ((currentDrawingState.getType() == EDrawingStateType.DRAWING_STATE_DETAIL_OUTSIDE)
					|| (currentDrawingState.getType() == EDrawingStateType.DRAWING_STATE_FULL_HIERARCHY)) {

				serializedForm.setDrawingStateType(currentDrawingState.getType());
				serializedForm.setRootElementID(pdCurrentRootElement.getElementID());
				serializedForm.setSelectedElementID(pdCurrentSelectedElement
						.getElementID());
				serializedForm.setRootElementStartAngle(pdCurrentRootElement
						.getCurrentStartAngle());
				serializedForm.setSelectedElementStartAngle(pdCurrentSelectedElement
						.getCurrentStartAngle());
			} else {
				HistoryEntry historyEntry = navigationHistory.getCurrentHistoryEntry();
				serializedForm.setDrawingStateType(historyEntry.getDrawingState()
						.getType());
				serializedForm.setRootElementID(historyEntry.getRootElement()
						.getElementID());
				serializedForm.setSelectedElementID(historyEntry.getSelectedElement()
						.getElementID());
				serializedForm.setRootElementStartAngle(historyEntry
						.getRootElementStartAngle());
				serializedForm.setSelectedElementStartAngle(historyEntry
						.getSelectedElementStartAngle());
			}
		}

		return serializedForm;
	}

	@Override
	public void initFromSerializableRepresentation(ASerializedView ser) {

		// FIXME: this code has been moved to setDataDomain - is this ok?
		// Tree<ClusterNode> tree =
		// dataDomain.getTable().getDimensionData(dimensionVAType)
		// .getDimensionTree();
		// // Tree<ClusterNode> tree = table.getClusteredTreeGenes();
		// if (tree != null) {
		// ArrayList<EPDDrawingStrategyType> alColorModes = new
		// ArrayList<EPDDrawingStrategyType>();
		// alColorModes.add(EPDDrawingStrategyType.EXPRESSION_COLOR);
		// alColorModes.add(EPDDrawingStrategyType.RAINBOW_COLOR);
		//
		// // initHierarchy(tree, EIDType.CLUSTER_NUMBER,
		// // new GeneClusterDataEventManager(this), alColorModes);
		// initHierarchy(tree,
		// dataDomain.getTable().getDimensionData(dimensionVAType)
		// .getDimensionTreeRoot(), new ExperimentClusterDataEventManager(this),
		// alColorModes);
		// }

		SerializedRadialHierarchyView serializedView = (SerializedRadialHierarchyView) ser;
		setupDisplay(serializedView.getDrawingStateType(),
				serializedView.getDefaultDrawingStrategyType(),
				serializedView.isNewSelection(), serializedView.getRootElementID(),
				serializedView.getSelectedElementID(),
				serializedView.getRootElementStartAngle(),
				serializedView.getSelectedElementStartAngle(),
				serializedView.getMaxDisplayedHierarchyDepth());
	}

	/**
	 * Sets up the current display of the RadialHierarchy view according to the
	 * specified parameters.
	 *
	 * @param drawingStateType
	 *            DrawingState that shall be used.
	 * @param drawingStrategyType
	 *            Default drawing strategy that shall be used.
	 * @param isNewSelection
	 *            Determines if the selected element has been newly selected.
	 * @param rootElementID
	 *            ID of current root element.
	 * @param selectedElementID
	 *            ID of selected element.
	 * @param rootElementStartAngle
	 *            Start angle of the root element.
	 * @param selectedElementStartAngle
	 *            Start angle of the selected element.
	 * @param maxDisplayedHierarchyDepth
	 *            Maximum hierarchy depth that shall be displayed.
	 */
	public void setupDisplay(EDrawingStateType drawingStateType,
			EPDDrawingStrategyType drawingStrategyType, boolean isNewSelection,
			int rootElementID, int selectedElementID, float rootElementStartAngle,
			float selectedElementStartAngle, int maxDisplayedHierarchyDepth) {

		this.iMaxDisplayedHierarchyDepth = maxDisplayedHierarchyDepth;
		bIsNewSelection = isNewSelection;
		PartialDisc pdTemp = hashPartialDiscs.get(rootElementID);
		drawingController.setDrawingState(drawingStateType);
		drawingStrategyManager.setDefaultStrategy(drawingStrategyType);

		if (pdTemp != null) {
			setCurrentRootElement(pdTemp);
			pdCurrentSelectedElement = hashPartialDiscs.get(selectedElementID);
			pdCurrentRootElement.setCurrentStartAngle(rootElementStartAngle);
			if (pdCurrentSelectedElement != null) {
				pdCurrentSelectedElement.setCurrentStartAngle(selectedElementStartAngle);
			}
		}
		setDisplayListDirty();
	}

	// @Override
	// public void handleClusterNodeSelection(ClusterNodeSelectionEvent event) {
	//
	// SelectionDelta selectionDelta = event.getSelectionDelta();
	//
	// if (selectionDelta.getIDType() == EIDType.CLUSTER_NUMBER) {
	// if (event.isSenderRadialHierarchy()) {
	// setupDisplay(event.getDrawingStateType(),
	// event.getDefaultDrawingStrategyType(), event
	// .isNewSelection(), event.getRootElementID(),
	// event.getSelectedElementID(), event
	// .getRootElementStartAngle(), event.getSelectedElementStartAngle(), event
	// .getMaxDisplayedHierarchyDepth());
	// }
	// selectionManager.clearSelections();
	// selectionManager.setDelta(selectionDelta);
	// bIsNewSelection = true;
	// setDisplayListDirty();
	// }
	// }

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();
		redrawViewListener = new RedrawViewListener();
		redrawViewListener.setHandler(this);
		eventPublisher.addListener(RedrawViewEvent.class, redrawViewListener);

		goBackInHistoryListener = new GoBackInHistoryListener();
		goBackInHistoryListener.setHandler(this);
		eventPublisher.addListener(GoBackInHistoryEvent.class, goBackInHistoryListener);

		goForthInHistoryListener = new GoForthInHistoryListener();
		goForthInHistoryListener.setHandler(this);
		eventPublisher.addListener(GoForthInHistoryEvent.class, goForthInHistoryListener);

		changeColorModeListener = new ChangeColorModeListener();
		changeColorModeListener.setHandler(this);
		eventPublisher.addListener(ChangeColorModeEvent.class, changeColorModeListener);

		setMaxDisplayedHierarchyDepthListener = new SetMaxDisplayedHierarchyDepthListener();
		setMaxDisplayedHierarchyDepthListener.setHandler(this);
		eventPublisher.addListener(SetMaxDisplayedHierarchyDepthEvent.class,
				setMaxDisplayedHierarchyDepthListener);

		detailOutsideListener = new DetailOutsideListener();
		detailOutsideListener.setHandler(this);
		eventPublisher.addListener(DetailOutsideEvent.class, detailOutsideListener);

		clearSelectionsListener = new SelectionCommandListener();
		clearSelectionsListener.setHandler(this);
		eventPublisher.addListener(SelectionCommandEvent.class, clearSelectionsListener);

		if (dataEventManager != null)
			dataEventManager.registerEventListeners();
	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();
		if (redrawViewListener != null) {
			eventPublisher.removeListener(redrawViewListener);
			redrawViewListener = null;
		}
		if (goBackInHistoryListener != null) {
			eventPublisher.removeListener(goBackInHistoryListener);
			goBackInHistoryListener = null;
		}
		if (goForthInHistoryListener != null) {
			eventPublisher.removeListener(goForthInHistoryListener);
			goForthInHistoryListener = null;
		}
		if (changeColorModeListener != null) {
			eventPublisher.removeListener(changeColorModeListener);
			changeColorModeListener = null;
		}
		if (setMaxDisplayedHierarchyDepthListener != null) {
			eventPublisher.removeListener(setMaxDisplayedHierarchyDepthListener);
			setMaxDisplayedHierarchyDepthListener = null;
		}
		if (detailOutsideListener != null) {
			eventPublisher.removeListener(detailOutsideListener);
			detailOutsideListener = null;
		}
		if (updateViewListener != null) {
			eventPublisher.removeListener(updateViewListener);
			updateViewListener = null;
		}

		if (clearSelectionsListener != null) {
			eventPublisher.removeListener(clearSelectionsListener);
			clearSelectionsListener = null;
		}

		if (dataEventManager != null)
			dataEventManager.unregisterEventListeners();
	}


	@Override
	public void handleRedrawView() {
		setDisplayListDirty();
	}

	// @Override
	// public void handleUpdateView() {
	// // Tree<ClusterNode> tree = table.getClusteredTreeGenes();
	// Tree<ClusterNode> tree =
	// tablePerspective.getDimensionPerspective().getTree();
	// if (tree != null) {
	//
	// // if (pdRealRootElement == null) {
	// if (dataEventManager != null)
	// dataEventManager.unregisterEventListeners();
	// ArrayList<EPDDrawingStrategyType> alColorModes = new
	// ArrayList<EPDDrawingStrategyType>();
	// alColorModes.add(EPDDrawingStrategyType.EXPRESSION_COLOR);
	// alColorModes.add(EPDDrawingStrategyType.RAINBOW_COLOR);
	// // initHierarchy(tree, EIDType.CLUSTER_NUMBER,
	// // new GeneClusterDataEventManager(this), alColorModes);
	// initHierarchy(tree,
	// tablePerspective.getDimensionPerspective().getTreeRoot(),
	// new ExperimentClusterDataEventManager(this), alColorModes);
	// // }
	//
	// } else {
	// hashPartialDiscs.clear();
	// navigationHistory.reset();
	// pdCurrentRootElement = null;
	// pdCurrentSelectedElement = null;
	// pdRealRootElement = null;
	// }
	// setDisplayListDirty();
	// }

	/**
	 * Handles the alternative partial disc selection triggered by pressing a
	 * key.
	 */
	public void handleKeyboardAlternativeDiscSelection() {

		Set<Integer> setSelection = selectionManager.getElements(SelectionType.SELECTION);
		PartialDisc pdCurrentMouseOverElement = null;
		int iDisplayedHierarchyDepth = Math.min(iMaxDisplayedHierarchyDepth,
				pdCurrentRootElement.getDepth());

		if ((setSelection != null)) {
			for (Integer elementID : setSelection) {
				pdCurrentMouseOverElement = hashPartialDiscs.get(elementID);
				if (pdCurrentMouseOverElement.isCurrentlyDisplayed(pdCurrentRootElement,
						iDisplayedHierarchyDepth)) {
					drawingController
							.handleAlternativeSelection(pdCurrentMouseOverElement);
					return;
				}
			}
		}

		Set<Integer> setMouseOver = selectionManager
				.getElements(SelectionType.MOUSE_OVER);
		if ((setMouseOver != null)) {
			for (Integer elementID : setMouseOver) {
				pdCurrentMouseOverElement = hashPartialDiscs.get(elementID);
				if (pdCurrentMouseOverElement.isCurrentlyDisplayed(pdCurrentRootElement,
						iDisplayedHierarchyDepth)) {
					drawingController
							.handleAlternativeSelection(pdCurrentMouseOverElement);
					return;
				}
			}
		}
	}

	public void handleAlternativeSelection(int elementID) {
		PartialDisc pdSelected = hashPartialDiscs.get(elementID);
		if (pdSelected != null) {
			drawingController.handleAlternativeSelection(pdSelected);
		}
	}

	// @Override
	// public void handleSelectionUpdate(SelectionDelta selectionDelta, boolean
	// scrollToSelection, String
	// info) {
	// if (selectionDelta.getIDType() == EIDType.EXPRESSION_INDEX) {
	// selectionManager.clearSelections();
	// Collection<SelectionDeltaItem> deltaItems = selectionDelta.getAllItems();
	//
	// for (SelectionDeltaItem item : deltaItems) {
	// // This works because the ClusterID of leaves equals the Expression index
	// of the corresponding
	// // gene.
	// selectionManager.addToType(item.getSelectionType(), item.getPrimaryID());
	// }
	//
	// bIsNewSelection = true;
	// setDisplayListDirty();
	// }
	// }

	public Rectangle getControlBox() {
		return controlBox;
	}

	public Collection<PartialDisc> getPartialDiscs() {
		return hashPartialDiscs.values();
	}

	@Override
	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	IDType getNodeIDType() {
		return partialDiscTree.getNodeIDType();
	}

	@Override
	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;

		// Tree<ClusterNode> tree =
		// dataDomain.getTable().getDimensionData(dimensionVAType)
		// .getDimensionTree();
		// // Tree<ClusterNode> tree = table.getClusteredTreeGenes();
		// if (tree != null) {
		// ArrayList<EPDDrawingStrategyType> alColorModes = new
		// ArrayList<EPDDrawingStrategyType>();
		// alColorModes.add(EPDDrawingStrategyType.EXPRESSION_COLOR);
		// alColorModes.add(EPDDrawingStrategyType.RAINBOW_COLOR);
		//
		// // initHierarchy(tree, EIDType.CLUSTER_NUMBER,
		// // new GeneClusterDataEventManager(this), alColorModes);
		// initHierarchy(tree,
		// dataDomain.getTable().getDimensionData(dimensionVAType)
		// .getDimensionTreeRoot(), new ExperimentClusterDataEventManager(this),
		// alColorModes);
		// }

		Tree<ClusterNode> tree = tablePerspective.getRecordPerspective().getTree();
		// Tree<ClusterNode> tree = table.getClusteredTreeGenes();
		if (tree != null) {
			ArrayList<EPDDrawingStrategyType> alColorModes = new ArrayList<EPDDrawingStrategyType>();
			alColorModes.add(EPDDrawingStrategyType.EXPRESSION_COLOR);
			alColorModes.add(EPDDrawingStrategyType.RAINBOW_COLOR);

			// initHierarchy(tree, EIDType.CLUSTER_NUMBER,
			// new GeneClusterDataEventManager(this), alColorModes);
			initHierarchy(tree, tree.getRoot(), new GeneClusterDataEventManager(this),
					alColorModes);
		}

		// Tree<ClusterNode> tree =
		// dataDomain.getTable().getContentData(recordVAType)
		// .getContentTree();
		// if (tree != null) {
		// // initHierarchy(tree);
		// } else {
		// // initTestHierarchy();
		// // }
		// partialDiscTree.setLeafIDType(tree.getLeaveIDType());
		// partialDiscTree.setNodeIDType(tree.getNodeIDType());

	}

	@Override
	protected ArrayList<ElementConnectionInformation> createElementConnectionInformation(
			IDType idType, int id) throws InvalidAttributeValueException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void destroyViewSpecificContent(GL2 gl) {
		gl.glDeleteLists(displayListIndex, 1);
	}
}
