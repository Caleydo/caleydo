package org.caleydo.core.manager.usecase;

import java.util.ArrayList;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering;

/**
 * Abstract use case class that implements data and view management.
 * 
 * @author Marc Streit
 *
 */
public abstract class AUseCase 
implements IUseCase {
	
	protected ArrayList<IView> alView;
	
	/**
	 * This mode determines whether the user can load and work with gene expression data or otherwise if an
	 * not further specified data set is loaded. In the case of the unspecified data set some specialized gene
	 * expression features are not available.
	 */
	protected EUseCaseMode eUseCaseMode = EUseCaseMode.UNSPECIFIED_DATA;

	public AUseCase() {
		alView = new ArrayList<IView>();
	}
	
	/**
	 * The set which is currently loaded and used inside the views for this use case.
	 */
	protected ISet set;
	
	@Override
	public EUseCaseMode getUseCaseMode() {
		return eUseCaseMode;
	}
	
	@Override
	public ISet getSet() {
		return set;
	}
	
	@Override
	public void setSet(ISet set) {
		
		// TODO: check if set corresponds to use case mode
		
		this.set = set;
		
		// TODO: Destroy old set + storages etc.
	}

	@Override
	public void updateSetInViews() {
		
		GLRemoteRendering glRemoteRenderingView = null;
		
		// Update set in the views
		for (IView view : alView) {
			view.setSet(set);
			
			if (view instanceof GLRemoteRendering) {
				glRemoteRenderingView = (GLRemoteRendering) view;
			}
		}
		
		// When new data is set, the bucket will be cleared because the internal heatmap and parcoords cannot
		// be updated in the context mode.
		if (glRemoteRenderingView != null)
			glRemoteRenderingView.clearAll();
	}
	
	@Override
	public void addView(IView view) {
		
		if (alView.contains(view))
			return;
		
		alView.add(view);
	}
	
	@Override
	public void removeView(IView view) {
	
		alView.remove(view);
	}
}
