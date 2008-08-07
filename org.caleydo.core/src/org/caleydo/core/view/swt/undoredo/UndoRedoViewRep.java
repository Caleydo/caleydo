package org.caleydo.core.view.swt.undoredo;

import java.util.Iterator;
import java.util.Vector;
import org.caleydo.core.command.ICommand;
import org.caleydo.core.manager.IGeneralManager;
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
	public UndoRedoViewRep( int iViewID,
			int iParentContainerId, String sLabel)
	{
		super(iViewID, iParentContainerId, sLabel, ViewType.SWT_IMAGE_VIEWER);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.AView#initViewSwtComposit(org.eclipse.swt.widgets.Composite)
	 */
	protected void initViewSwtComposit(Composite swtContainer)
	{

		swtContainer.setLayout(new RowLayout(SWT.HORIZONTAL));

		Label viewComboLabel = new Label(swtContainer, SWT.LEFT);
		viewComboLabel.setText("Undo/Redo:");
		viewComboLabel.setSize(300, 30);

		undoRedoCombo = new Combo(swtContainer, SWT.READ_ONLY);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.IView#drawView()
	 */
	public void drawView()
	{
	}

	public void setAttributes(int iWidth, int iHeight, String sImagePath)
	{

		super.setAttributes(iWidth, iHeight);
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
