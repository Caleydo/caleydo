package cerberus.view;

//import cerberus.manager.GeneralManager;

public class ViewMediator 
{
//	public ViewMediator(GeneralManager refGerneralManager)
//	{
//		
//	}
	
	/**
	 * Method is called when the data changes.
	 * It triggers the onDataChangedEvent in all dependent views.
	 * Dependent views are views that operate on the same data.
	 * 
	 */
	public void updateDataSetChanged() //calls onDataSetChangedEvent
	{
		
	}

	/**
	 * Method is called when selections in views change.
	 * It triggers the onSelectionChangedEvent in all dependent views.
	 * Dependent views are (in this case) views that visualize the same selection.
	 * For example a scatterplot that shows the data that is selected in a heatmap view.
	 * Therefore when the user changes the selection in the heatmap the scatterplot 
	 * has to be updates.
	 * 
	 */
	public void updateSelectionChanged() //calls onSelectionChangedEvent
	{
		
	}
}
