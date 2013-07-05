package org.caleydo.core.view;

import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.opengl.canvas.AGLView;

/**
 * Interface for creators of remotely rendered views.
 *
 * @author Christian Partl
 *
 */
public interface IRemoteViewCreator {

	/**
	 * Subclasses shall create an object of the View to be created.
	 *
	 * @param remoteRenderingView
	 *            View that remote-renders the created view.
	 * @param tablePerspectives
	 *            List of {@link TablePerspective} objects that shall be displayed in the view.
	 * @return Instance of the view.
	 */
	public AGLView createRemoteView(AGLView remoteRenderingView, List<TablePerspective> tablePerspectives);

}
