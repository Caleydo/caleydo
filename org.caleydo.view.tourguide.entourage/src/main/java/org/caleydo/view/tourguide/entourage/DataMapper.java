package org.caleydo.view.tourguide.entourage;

import org.caleydo.view.entourage.datamapping.IDataMapper;
import org.caleydo.view.tourguide.api.vis.TourGuideUtils;

public class DataMapper implements IDataMapper {

	public DataMapper() {
	}

	@Override
	public void show() {
		TourGuideUtils.showTourGuide(EntourageStratificationAdapterFactory.SECONDARY_ID);
		TourGuideUtils.showTourGuide(EntourageNonGeneticAdapterFactory.SECONDARY_ID);
		// call again to get focus
		TourGuideUtils.showTourGuide(EntourageStratificationAdapterFactory.SECONDARY_ID);
	}

	@Override
	public void hide() {
		TourGuideUtils.hideTourGuide(EntourageStratificationAdapterFactory.SECONDARY_ID);
		TourGuideUtils.hideTourGuide(EntourageNonGeneticAdapterFactory.SECONDARY_ID);
	}

}
