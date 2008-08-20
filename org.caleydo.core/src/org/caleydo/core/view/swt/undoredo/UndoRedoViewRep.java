package org.caleydo.core.view.swt.undoredo;

import java.util.Iterator;
import java.util.Vector;
import org.caleydo.core.command.ICommand;
import org.caleydo.core.view.AView;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.ViewType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * UNDO/REDO view.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class UndoRedoViewRep
	extends AView
	implements IView
{

	protected Combo undoRedoCombo;

	/**
	 * Constructor.

	 */
	public UndoRedoViewRep(int iParentContainerId, String sLabel)
	{
		super(iParentContainerId, sLabel, ViewType.SWT_IMAGE_VIEWER);
	}

	@Override
	protected void initViewSwtComposite(Composite swtContainer)
	{

		swtContainer.setLayout(new RowLayout(SWT.HORIZONTAL));

		Label viewComboLabel = new Label(swtContainer, SWT.LEFT);
		viewComboLabel.setText("Undo/Redo:");
		viewComboLabel.setSize(300, 30);

		undoRedoCombo = new Combo(swtContainer, SWT.READ_ONLY);
	}

	@Override
	public void drawView()
	{
	}

	public void setAttributes(String sImagePath)
	{
	}

	public void updateCommandList(Vector<ICommand> vecCommands)
	{

		undoRedoCombo.removeAll();

		Iterator<ICommand> iterCommands = vecCommands.iterator();

		while (iterCommands.hasNext())
		{
			// ICommand bufferCmd = iterCommands.next();
			undoRedoCombo.add(iterCommands.next().getInfoText());
		}
	}

	public void addCommand(final ICommand command)
	{

		swtContainer.getDisplay().asyncExec(new Runnable()
		{

			public void run()
			{

				undoRedoCombo.add(command.getInfoText());
				// generalManager.logMsg(
				// "DEBUG: " + command.getInfoText() + " " + command.toString(),
				// LoggerType.VERBOSE);
			}
		});
	}
}
