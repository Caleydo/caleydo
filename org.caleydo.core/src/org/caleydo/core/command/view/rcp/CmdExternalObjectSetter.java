package org.caleydo.core.command.view.rcp;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.EPositionModel;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GLGlyph;

/**
 * Command for setting objects in org.caleydo.core from the RCP interface.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Stefan Sauer
 */
@Deprecated
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
						glyphview.setPositionModelAxis(EPositionModel.DISPLAY_SCATTERPLOT, 0, value);
					}
					return;

				case GLYPH_CHANGE_SCATTERPLOT_AXIS_Y:
					if (object instanceof Integer) {
						int value = (Integer) object;
						glyphview.setPositionModelAxis(EPositionModel.DISPLAY_SCATTERPLOT, 1, value);
					}
					return;

				case GLYPH_CHANGE_PLUSMODEL_AXIS_X:
					if (object instanceof Integer) {
						int value = (Integer) object;
						glyphview.setPositionModelAxis(EPositionModel.DISPLAY_PLUS, 0, value);
					}
					return;

				case GLYPH_CHANGE_PLUSMODEL_AXIS_Y:
					if (object instanceof Integer) {
						int value = (Integer) object;
						glyphview.setPositionModelAxis(EPositionModel.DISPLAY_PLUS, 1, value);
					}
					return;
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
