/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.api.state;

import org.caleydo.view.tourguide.api.adapter.TourGuideDataModes;
import org.caleydo.view.tourguide.internal.mode.VariableDataMode;
import org.caleydo.view.tourguide.spi.adapter.ITourGuideDataMode;

import com.google.common.base.Predicate;

/**
 * @author Samuel Gratzl
 *
 */
public class RootState implements IState {
	private final String adapter;
	private final ITourGuideDataMode mode;
	private final String label;

	public RootState(String adapter, ITourGuideDataMode mode, String label) {
		this.adapter = adapter;
		this.mode = mode;
		this.label = label;
	}

	public IState getBrowseState() {
		return null;
	}

	public ITourGuideDataMode getMode() {
		return mode;
	}

	public String getAdapter() {
		return adapter;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void onEnter() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLeave() {
		// TODO Auto-generated method stub

	}

	/**
	 * @return
	 */
	public static Predicate<RootState> ARE_STRATIFICATIONS = new Predicate<RootState>() {
		@Override
		public boolean apply(RootState input) {
			return input == null ? false : TourGuideDataModes.areStratificatins(input.getMode());
		}
	};
	public static Predicate<RootState> ARE_PATHWAYS = new Predicate<RootState>() {
		@Override
		public boolean apply(RootState input) {
			return input == null ? false : TourGuideDataModes.arePathways(input.getMode());
		}
	};
	public static Predicate<RootState> ARE_INHOMOGENOUSVARIABLES = new Predicate<RootState>() {
		@Override
		public boolean apply(RootState input) {
			if (input == null)
				return false;
			return input.getMode() instanceof VariableDataMode;
		}
	};
}
