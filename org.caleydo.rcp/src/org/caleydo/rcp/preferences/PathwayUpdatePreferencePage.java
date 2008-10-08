package org.caleydo.rcp.preferences;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.rcp.wizard.firststart.FetchPathwayDataPage;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

/**
 * Preference page for updating pathway data
 * 
 * @author Marc Streit
 */
public class PathwayUpdatePreferencePage
	extends PreferencePage
	implements IWorkbenchPreferencePage
{
	public PathwayUpdatePreferencePage()
	{
		super();
		setPreferenceStore(GeneralManager.get().getPreferenceStore());
		setDescription("Preferences for updating pathway databases.");
	}

	@Override
	public boolean performOk()
	{

		boolean bReturn = super.performOk();

//		Collection<AGLEventListener> eventListeners = GeneralManager.get()
//				.getViewGLCanvasManager().getAllGLEventListeners();
//		for (AGLEventListener eventListener : eventListeners)
//		{
//			if (eventListener instanceof GLParallelCoordinates)
//			{
//				GLParallelCoordinates parCoords = (GLParallelCoordinates) eventListener;
//				// if(!heatMap.isRenderedRemote())
//				// {
//				parCoords.setNumberOfSamplesToShow(numRandomSamplesFE.getIntValue());
//				// }
//			}
//		}

		return bReturn;
	}

	@Override
	protected Control createContents(Composite parent)
	{
		this.setValid(false);
		Composite composite = new Composite(parent, SWT.NULL);
		
		RowLayout layout = new RowLayout(SWT.VERTICAL);
		layout.wrap = true;
		layout.fill = true;
		layout.justify = true;
		layout.center = true;
		composite.setLayout(layout);
		
		Label lastUpdateLabel = new Label(composite, SWT.NULL);
		lastUpdateLabel.setText("Last complete update: " +GeneralManager.get()
				.getPreferenceStore().getString("lastPathwayDataUpdate"));
		
		return FetchPathwayDataPage.createContent(composite, this);
	}
	
	@Override
	protected void performApply()
	{	
		 MessageBox messageBox = new MessageBox(this.getShell(), SWT.OK);
		 messageBox.setText("Pathway Update Notification");
		 messageBox.setMessage("You have updated your pathway data. " +
		 		"The system needs to restart in order to load the new data.");
		 messageBox.open();
	
		 PlatformUI.getWorkbench().restart();
		 
		 super.performApply();
	}
	
	@Override
	public boolean performCancel()
	{
		super.performCancel();
		
		 MessageBox messageBox = new MessageBox(this.getShell(), SWT.OK);
		 messageBox.setText("Pathway Update Notification");
		 messageBox.setMessage("You have cancelled the pathway update. " +
		 		"Please try again and wait until the update process has finished!");
//		 		"The system restores the old pathway data fetched on" +GeneralManager.get()
//				.getPreferenceStore().getString("lastPathwayDataUpdate"));
		 messageBox.open();
		
		 return true;
	}
	
	@Override
	public void init(IWorkbench workbench)
	{		
	}
}