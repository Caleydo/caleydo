package org.caleydo.core.command.view.rcp;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;
import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.EIconIDs;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GLGlyph;
import org.caleydo.core.view.opengl.canvas.storagebased.GLHierarchicalHeatMap;

/**
 * Command for setting objects in org.caleydo.core from the RCP interface.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Stefan Sauer
 */
public class CmdExternalObjectSetter
	extends ACmdExternalAttributes {

	private Object object;

	private EExternalObjectSetterType externalSetterType;

	private int iViewId;

	/**
	 * Constructor.
	 */
	public CmdExternalObjectSetter(final ECommandType cmdType) {
		super(cmdType);
		object = null;
	}

	@Override
	public void doCommand() {

		commandManager.runDoCommand(this);

		Object viewObject = generalManager.getViewGLCanvasManager().getGLEventListener(iViewId);

		if (viewObject instanceof GLGlyph) {
			GLGlyph glyphview = (GLGlyph) viewObject;
			switch (externalSetterType) {
				case GLYPH_SELECTIONBRUSH:
					if (object instanceof Integer) {
						int size = (Integer) object;
						glyphview.setSelectionBrush(size);
					}
					return;

				case GLYPH_CHANGEPERSONALNAME:
					if (object instanceof String) {
						String name = (String) object;
						glyphview.setPersonalName(name);
					}
					return;

				case GLYPH_CHANGE_SCATTERPLOT_AXIS_X:
					if (object instanceof Integer) {
						int value = (Integer) object;
						glyphview.setPositionModelAxis(EIconIDs.DISPLAY_SCATTERPLOT, 0, value);
					}
					return;

				case GLYPH_CHANGE_SCATTERPLOT_AXIS_Y:
					if (object instanceof Integer) {
						int value = (Integer) object;
						glyphview.setPositionModelAxis(EIconIDs.DISPLAY_SCATTERPLOT, 1, value);
					}
					return;

				case GLYPH_CHANGE_PLUSMODEL_AXIS_X:
					if (object instanceof Integer) {
						int value = (Integer) object;
						glyphview.setPositionModelAxis(EIconIDs.DISPLAY_PLUS, 0, value);
					}
					return;

				case GLYPH_CHANGE_PLUSMODEL_AXIS_Y:
					if (object instanceof Integer) {
						int value = (Integer) object;
						glyphview.setPositionModelAxis(EIconIDs.DISPLAY_PLUS, 1, value);
					}
					return;
			}

		}
		if (viewObject instanceof GLHierarchicalHeatMap) {
			GLHierarchicalHeatMap glHHeatMap = (GLHierarchicalHeatMap) viewObject;
			switch (externalSetterType) {
				case STORAGEBASED_START_CLUSTERING:
					if (object instanceof ClusterState) {
						ClusterState clusterState = (ClusterState) object;
						glHHeatMap.startClustering(clusterState);
					}
					break;
			}
		}

	}

	@Override
	public void undoCommand() {
		commandManager.runUndoCommand(this);
	}

	public void setAttributes(final int iViewId, final Object object,
		final EExternalObjectSetterType externalSetterType) {
		this.object = object;
		this.externalSetterType = externalSetterType;
		this.iViewId = iViewId;
	}
}
