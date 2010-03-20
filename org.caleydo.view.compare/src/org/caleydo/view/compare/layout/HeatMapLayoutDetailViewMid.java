package org.caleydo.view.compare.layout;

import gleem.linalg.Vec3f;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.view.compare.rendercommand.RenderCommandFactory;

public class HeatMapLayoutDetailViewMid extends AHeatMapLayoutDetailView {

	public HeatMapLayoutDetailViewMid(RenderCommandFactory renderCommandFactory) {
		super(renderCommandFactory);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Vec3f getCaptionLabelPosition(float textWidth) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vec3f getDetailPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EPickingType getGroupPickingType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EPickingType getHeatMapPickingType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vec3f getOverviewGroupBarPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vec3f getOverviewHeatMapPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vec3f getOverviewPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getOverviewSliderPositionX() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public Vec3f getDendrogramButtonPosition() {
		return new Vec3f(positionX + getDendrogramLineWidth(), positionY
				+ getOverviewHeight(), 0.0f);
	}

	@Override
	public Vec3f getDendrogramLinePosition() {
		return new Vec3f(positionX, positionY, 0.0f);
	}

	@Override
	public Vec3f getDendrogramPosition() {
		// TODO Auto-generated method stub
		return null;
	}
}
