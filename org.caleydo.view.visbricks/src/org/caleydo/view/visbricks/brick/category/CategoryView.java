package org.caleydo.view.visbricks.brick.category;

import java.util.ArrayList;

import javax.management.InvalidAttributeValueException;
import javax.media.opengl.GL2;
import javax.media.opengl.awt.GLCanvas;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.events.ClearSelectionsListener;
import org.caleydo.core.event.view.ClearSelectionsEvent;
import org.caleydo.core.event.view.tablebased.RedrawViewEvent;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.ATableBasedView;
import org.caleydo.core.view.opengl.canvas.DetailLevel;
import org.caleydo.core.view.opengl.canvas.listener.RedrawViewListener;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.eclipse.swt.widgets.Composite;

/**
 * Rendering the histogram.
 * 
 * @author Alexander Lex
 */
public class CategoryView extends ATableBasedView {

	public final static String VIEW_TYPE = "org.caleydo.view.histogram";

	boolean bUseDetailLevel = true;

	protected RedrawViewListener redrawViewListener;

	float fRenderWidth;

	private ATableBasedDataDomain dataDomain;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param label
	 * @param viewFrustum
	 */
	public CategoryView(GLCanvas glCanvas, Composite parentComposite,
			final ViewFrustum viewFrustum) {
		super(glCanvas, parentComposite, viewFrustum);

		viewType = VIEW_TYPE;

		// registerEventListeners();
	}

	@Override
	public void init(GL2 gl) {

	}

	@Override
	public void initLocal(GL2 gl) {

		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView,
			final GLMouseListener glMouseListener) {

		this.glMouseListener = glMouseListener;

		init(gl);

	}

	@Override
	public void initData() {
		super.initData();

	}

	@Override
	public void setDetailLevel(DetailLevel detailLevel) {
		if (bUseDetailLevel) {
			super.setDetailLevel(detailLevel);
			// renderStyle.setDetailLevel(detailLevel);
			if (detailLevel == DetailLevel.LOW) {
				// sideSpacing = 0;
			} else {
				// sideSpacing = SIDE_SPACING;
			}
		}

	}

	@Override
	public void displayLocal(GL2 gl) {

		if (!lazyMode)
			pickingManager.handlePicking(this, gl);

		display(gl);

	}

	@Override
	public void displayRemote(GL2 gl) {
		// if (bIsDisplayListDirtyRemote) {
		// colorMapping = ColorMappingManager.get().getColorMapping(
		// EColorMappingType.GENE_EXPRESSION);
		// buildDisplayList(gl, iGLDisplayListIndexRemote);
		// bIsDisplayListDirtyRemote = false;
		// }
		// iGLDisplayListToCall = iGLDisplayListIndexRemote;

		display(gl);

	}

	@Override
	public void display(GL2 gl) {
		System.out.println("not much");
		if (!lazyMode)
			checkForHits(gl);
	}

	@Override
	protected void handlePickingEvents(PickingType pickingType, PickingMode pickingMode,
			int externalID, Pick pick) {
		if (detailLevel == DetailLevel.VERY_LOW) {
			return;
		}
		switch (pickingType) {

		case HISTOGRAM_COLOR_LINE:

			switch (pickingMode) {
			case CLICKED:

				break;
			case MOUSE_OVER:

				break;
			default:
				return;
			}
			setDisplayListDirty();
			break;

		}
	}

	@Override
	public void handleRedrawView() {
		setDisplayListDirty();
	}

	@Override
	public void handleUpdateView() {
		setDisplayListDirty();
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();
		redrawViewListener = new RedrawViewListener();
		redrawViewListener.setHandler(this);
		eventPublisher.addListener(RedrawViewEvent.class, redrawViewListener);

	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();
		if (redrawViewListener != null) {
			eventPublisher.removeListener(redrawViewListener);
			redrawViewListener = null;
		}
		if (clearSelectionsListener != null) {
			eventPublisher.removeListener(clearSelectionsListener);
			clearSelectionsListener = null;
		}
	}

	@Override
	public int getMinPixelHeight() {
		// TODO: Calculate depending on content
		return 100;
	}

	@Override
	public int getMinPixelWidth() {
		// TODO: Calculate depending on content
		return 150;
	}

	@Override
	public int getMinPixelHeight(DetailLevel detailLevel) {
		switch (detailLevel) {
		case HIGH:
			return getMinPixelHeight();
		case MEDIUM:
			return getMinPixelHeight();
		case LOW:
			return getMinPixelHeight();
		default:
			return 50;
		}
	}

	@Override
	public int getMinPixelWidth(DetailLevel detailLevel) {
		switch (detailLevel) {
		case HIGH:
			return 100;
		case MEDIUM:
			return 100;
		case LOW:
			return 100;
		default:
			return 100;
		}
	}

	@Override
	protected ArrayList<SelectedElementRep> createElementRep(IDType idType, int id)
			throws InvalidAttributeValueException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		// TODO Auto-generated method stub
		return null;
	}

}
