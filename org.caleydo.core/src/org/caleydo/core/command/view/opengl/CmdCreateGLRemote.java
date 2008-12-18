package org.caleydo.core.command.view.opengl;

import java.util.ArrayList;
import org.caleydo.core.command.ECommandType;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering;

/**
 * Create single OpenGL pathway view.
 * 
 * @author Marc Streit
 */
public class CmdCreateGLRemote
	extends CmdCreateGLEventListener
{

	private ArrayList<Integer> iAlInitialContainedViewIDs;

	/**
	 * Constructor.
	 */
	public CmdCreateGLRemote(final ECommandType cmdType)
	{
		super(cmdType);

		iAlInitialContainedViewIDs = new ArrayList<Integer>();
	}

	@Override
	public void setParameterHandler(final IParameterHandler parameterHandler)
	{
		super.setParameterHandler(parameterHandler);
	}

	public void setInitialContainedViews(ArrayList<Integer> iAlInitialContainedViews)
	{
		this.iAlInitialContainedViewIDs = iAlInitialContainedViews;
	}

	@Override
	public final void doCommand()
	{
		super.doCommand();

		((GLRemoteRendering) createdObject)
				.setInitialContainedViews(iAlInitialContainedViewIDs);
	}
}
