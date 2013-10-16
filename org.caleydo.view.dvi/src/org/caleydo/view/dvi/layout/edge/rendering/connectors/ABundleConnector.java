/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi.layout.edge.rendering.connectors;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.view.dvi.GLDataViewIntegrator;
import org.caleydo.view.dvi.GeometryUtil;
import org.caleydo.view.dvi.node.IDVINode;

public abstract class ABundleConnector
	extends ANodeConnector
{

	protected final static int BUNDLING_POINT_NODE_DISTANCE_Y = 30;
	protected final static int BOUNDING_BOX_BAND_CONNECTIONPOINT_DISTANCE_Y = 20;
	protected final static int MAX_BAND_ANCHOR_OFFSET_DISTANCE_Y_2_CP = 20;
	protected final static int MAX_BAND_ANCHOR_OFFSET_DISTANCE_Y_4_CP = 100;
	protected final static int MIN_BAND_ANCHOR_OFFSET_DISTANCE_Y_4_CP = 10;
	protected final static int TABLEPERSPECTIVE_OFFSET_Y = 7;
	protected final static int TABLEPERSPECTIVE_TO_BUNDLE_OFFSET_Y = 14;

	protected List<TablePerspective> commonTablePerspectives;
	protected int bandWidthPixels;
	protected Map<TablePerspective, Integer> bandWidthMap = new HashMap<TablePerspective, Integer>();
	protected Point2D bundlingPoint;
	protected boolean use4ControlPointsForBandBundleConnection;
	protected GLDataViewIntegrator view;

	public ABundleConnector(IDVINode node, PixelGLConverter pixelGLconverter,
			ConnectionBandRenderer connectionBandRenderer,
			List<TablePerspective> commonTablePerspectives, int minBandWidth, int maxBandWidth,
			int maxDataAmount, IDVINode otherNode, ViewFrustum viewFrustum,
			GLDataViewIntegrator view)
	{
		super(node, pixelGLconverter, connectionBandRenderer, otherNode, viewFrustum);

		this.commonTablePerspectives = commonTablePerspectives;
		this.view = view;
		calcBandWidths(minBandWidth, maxBandWidth, maxDataAmount);
	}

	protected float calcXPositionOfBundlingPoint(IDVINode node,
			List<TablePerspective> tablePerspectives)
	{
		float summedX = 0;

		for (TablePerspective tablePerspective : tablePerspectives)
		{
			Pair<Point2D, Point2D> anchorPoints = node
					.getBottomObjectAnchorPoints(tablePerspective);
			if (anchorPoints == null)
				return (float) node.getPosition().getX();
			summedX += anchorPoints.getFirst().getX() + anchorPoints.getSecond().getX();
		}

		return summedX / ((float) tablePerspectives.size() * 2.0f);
	}

	protected void calcBandWidths(int minBandWidth, int maxBandWidth, int maxDataAmount)
	{
		bandWidthPixels = 0;

		for (TablePerspective tablePerspective : commonTablePerspectives)
		{
			int width = calcDimensionGroupBandWidthPixels(tablePerspective, minBandWidth,
					maxBandWidth, maxDataAmount);
			bandWidthPixels += width;
			bandWidthMap.put(tablePerspective, width);
		}

		if (bandWidthPixels > maxBandWidth)
		{

			int diff = bandWidthPixels - maxBandWidth;

			int newBandWidth = 0;

			for (TablePerspective dimensionGroupData : commonTablePerspectives)
			{
				int width = bandWidthMap.get(dimensionGroupData);
				int newWidth = width
						- (int) Math
								.ceil(((float) width / (float) bandWidthPixels * (float) diff));
				bandWidthMap.put(dimensionGroupData, newWidth);
				newBandWidth += newWidth;
			}

			bandWidthPixels = newBandWidth;
		}
	}

	protected int calcDimensionGroupBandWidthPixels(TablePerspective tablePerspective,
			int minBandWidth, int maxBandWidth, int maxDataAmount)
	{
		// TODO: implement properly

		return minBandWidth;
	}

	public int getBandWidth()
	{
		return bandWidthPixels;
	}

	/**
	 * Returns whether the line specified by point1 and point2 intersects with a
	 * bounding box of a node.
	 * 
	 * @param point1
	 * @param point2
	 * @return
	 */
	protected boolean doesLineIntersectWithNode(Point2D point1, Point2D point2)
	{
		for(IDVINode node : view.getAllNodes()) {
			
			if(GeometryUtil.calcIntersectionPoint(point1, point2, node.getBoundingBox()) != null) {
				return true;
			}
		}
		
		return false;
	}

}
