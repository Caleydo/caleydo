package org.caleydo.view.tourguide.entourage;

import org.caleydo.view.entourage.datamapping.IDataMapper;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.api.vis.TourGuideUtils;

public class DataMapper implements IDataMapper {

	public DataMapper() {
	}

	@Override
	public void show() {
		TourGuideUtils.showTourGuide(EDataDomainQueryMode.OTHER);
		TourGuideUtils.showTourGuide(EDataDomainQueryMode.STRATIFICATIONS);
	}

	@Override
	public void hide() {
		TourGuideUtils.hideTourGuide(EDataDomainQueryMode.OTHER);
		TourGuideUtils.hideTourGuide(EDataDomainQueryMode.STRATIFICATIONS);
	}

}
