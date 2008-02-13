package org.geneview.core.view.swt.undoredo;

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import org.geneview.core.command.ICommand;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.ILoggerManager.LoggerType;
import org.geneview.core.view.AViewRep;
import org.geneview.core.view.IView;
import org.geneview.core.view.ViewType;

/**
 * UNDO/REDO view.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class UndoRedoViewRep 
extends AViewRep 
implements IView {

	protected Combo refUndoRedoCombo;
	
	/**
	 * Constructor. 
	 * 
	 * @param refGeneralManager
	 * @param iViewId
	 * @param iParentContainerId
	 * @param sLabel
	 */
	public UndoRedoViewRep(
			IGeneralManager refGeneralManager, 
			int iViewId, 
			int iParentContainerId, 
			String sLabel) {
		
		super(refGeneralManager, 
				iViewId, 
				iParentContainerId, 
				sLabel,
				ViewType.SWT_IMAGE_VIEWER);	
	}

	/**
	 * 
	 * @see org.geneview.core.view.IView#initView()
	 */
	protected void initViewSwtComposit(Composite swtContainer) {
		
		refSWTContainer.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		Label viewComboLabel = new Label(refSWTContainer, SWT.LEFT);
		viewComboLabel.setText("Undo/Redo:");
		viewComboLabel.setSize(300, 30);
				
		refUndoRedoCombo = new Combo(refSWTContainer, SWT.READ_ONLY);
	}

	public void drawView() {
		
//		refGeneralManager.getSingelton().logMsg(
//				this.getClass().getSimpleName() + 
//				": drawView(): Load "+sUrl, 
//				LoggerType.VERBOSE );		
	}
	
	public void setAttributes(int iWidth, int iHeight, String sImagePath) {
		
		super.setAttributes(iWidth, iHeight);
	}
	
	public void updateCommandList(Vector<ICommand> vecCommands) {
		
		refUndoRedoCombo.removeAll();
		
		Iterator<ICommand> iterCommands = vecCommands.iterator();
		
		while(iterCommands.hasNext())
		{
			//ICommand bufferCmd = iterCommands.next();
			refUndoRedoCombo.add( iterCommands.next().getInfoText() );
		}		
	}
	
	public void addCommand(final ICommand refCommand) {
	
		refSWTContainer.getDisplay().asyncExec(new Runnable() {
			public void run() {
				refUndoRedoCombo.add(refCommand.getInfoText());				
				generalManager.getSingelton().logMsg(
						"DEBUG: " + refCommand.getInfoText() + " " + refCommand.toString(),
						LoggerType.VERBOSE);
			}
		});
	}
}
