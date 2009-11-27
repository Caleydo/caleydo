package org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters;

import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.hyperbolic.HyperbolicRenderStyle;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.EDrawAbleNodeDetailLevel;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.IDrawAbleNode;
import org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.htcalculation.HTModel;
import org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.projections.HyperbolicGlobeProjection;

public class HTLayouter
	extends ATreeLayouter
	implements ITreeLayouter {

	private HTModel model = null; // the model of the tree for the HyperTree
	private IDrawAbleNode root = null;

	private float fOriginalLineLenth = 0.0f;
	

//	public HTLayouter(IViewFrustum frustum, PickingManager pickingManager, int iViewID,HyperbolicRenderStyle renderStyle, String strInformation) {
//		super(frustum, pickingManager, iViewID, renderStyle, new DefaultProjection(1), strInformation);//new HyperbolicGlobeProjection(1));

		public HTLayouter(IViewFrustum frustum, PickingManager pickingManager, int iViewID,HyperbolicRenderStyle renderStyle, String strInformation) {
			super(frustum, pickingManager, iViewID, renderStyle, new HyperbolicGlobeProjection(1), strInformation);//new HyperbolicGlobeProjection(1));

//		IDrawAbleNode root = tree.getRoot();
		
		
	}
	
	
	@Override
	protected void renderTreeLayout() {
		updateSizeInfo();
		
		if (tree == null)
			return;
		this.root = tree.getRoot();
		root.nodeIsRoot();
		
		model = new HTModel(tree, root);
//		HTDraw drawClass = new HTDraw(model, this);
//		drawClass.refreshScreenCoordinates(fViewSpaceXAbs, fViewSpaceYAbs, fViewRadius/2.0f, fViewSpaceYAbs/2.0f);
//		drawClass.refreshScreenCoordinates(fViewSpaceXAbs/3, fViewSpaceYAbs/3, fWidth / 2.0f, fHeight / 2.0f);
//		Set<IDrawAbleNode> set = drawClass.drawNodes();
//		for(IDrawAbleNode node : set)
		{
//			drawClass.refreshScreenCoordinates(fViewSpaceXAbs/4, fViewSpaceYAbs/4, fWidth / 2.0f, fHeight / 2.0f);
//			root.setDetailLevel(EDrawAbleNodeDetailLevel.High);
//			placeNode(node, 2.4f + node.getXCoord(), 2.4f + node.getYCoord(), 0.0f, 0.1f, 0.1f);
//			if(tree.hasChildren(node)){
//				for(IDrawAbleNode child : tree.getChildren(node))
//				{
//					placeConnection(node, child);
//				}
//			}
			runThroughTreeAndCalculateScalingFactor(root, 0);
			runThroughTreeAndPlace(root, 0);
			runThroughTreeAndPlaceConnection(root, 0);
			
		}
//		IDrawAbleNode root = tree.getRoot();
//		root.setDetailLevel(EDrawAbleNodeDetailLevel.High);
//		root.place(root.getXCoord(), root.getYCoord(), 0.0f, 0.2f, 0.2f, new DefaultProjection(1));
//		root.place(2.0f, 2.0f, 0.0f, 0.2f, 0.2f, new DefaultProjection(1));
//		placeNode(root);
//		placeNode(root, root.getXCoord(), root.getYCoord(), 0.0f, 0.2f, 0.2f);
//		if(tree.hasChildren(root)){
//			for(IDrawAbleNode node : tree.getChildren(root)){
//				
//			}
//		}


	}
	private void runThroughTreeAndPlace(IDrawAbleNode node, int layer){
		layer = layer+1;
		if(layer < HyperbolicRenderStyle.MAX_DEPTH ){
		if(tree.hasChildren(node)){
			for(IDrawAbleNode child : tree.getChildren(node)){
				
				runThroughTreeAndPlace(child, layer);
			}
			
		}

		node.setDetailLevel(EDrawAbleNodeDetailLevel.High);
//		placeNode(node, node.getXCoord(), node.getYCoord(), 0.0f, 0.1f, 0.1f);
//		placeNode(node, (fWidth/2 + node.getXCoord())*fScalingFactor, (fHeight/2 + node.getYCoord())*fScalingFactor, 0.0f, 0.1f, 0.1f);
//		if(node.equals(tree.getRoot()))
//			placeNode(node,  node.getRealCoordinates().x(), node.getRealCoordinates().y(), 0.0f, 0.1f, 0.1f);
//		else	
			placeNode(node, fWidth/2 +  (node.getRealCoordinates().x() * fScalingFactor), fHeight/2 + (node.getRealCoordinates().y() * fScalingFactor), 0.0f, 0.1f, 0.1f);
			System.out.println(node.getNodeName());
			System.out.println(String.valueOf(node.getProjectedCoordinates().x())+' '+String.valueOf(node.getProjectedCoordinates().y())+' '+String.valueOf(node.getProjectedCoordinates().z()));
			System.out.println(String.valueOf(node.isVisible()));
			System.out.println();
			
//			if(tree.getParent(node) != null)
				
		
//		placeConnection(tree.getParent(node), node);

		}
	}
	
	private void runThroughTreeAndPlaceConnection(IDrawAbleNode node, int layer){
		layer = layer+1;
		if(layer < HyperbolicRenderStyle.MAX_DEPTH ){
		if(tree.hasChildren(node)){
			for(IDrawAbleNode child : tree.getChildren(node)){
				runThroughTreeAndPlaceConnection(child, layer);
			}
			
		}
		if(tree.getParent(node) != null)
		placeConnection(tree.getParent(node), node);
		}
	}
	
	private float runThroughTreeAndCalculateScalingFactor(IDrawAbleNode node, int layer){
		layer = layer+1;
		if(layer < HyperbolicRenderStyle.MAX_DEPTH ){
		if(tree.hasChildren(node)){
			for(IDrawAbleNode child : tree.getChildren(node)){
				child.setParentOfNode(node);
				runThroughTreeAndCalculateScalingFactor(child, layer);
			}
			
		}
		
		node.setDetailLevel(EDrawAbleNodeDetailLevel.High);
//		placeNode(node, node.getXCoord(), node.getYCoord(), 0.0f, 0.1f, 0.1f);
		placeNode(node, node.getRealCoordinates().x(), node.getRealCoordinates().y(), 0.0f, 0.1f, 0.1f);
//		placeNode(node, fWidth/2 + node.getRealCoordinates().x(), fHeight/2 + node.getRealCoordinates().y(), 0.0f, 0.1f, 0.1f);
		if(tree.getParent(node) != null)
			placeConnection(tree.getParent(node), node);
		float originalLineLenth = 0.0f;
//		if (layer < HyperbolicRenderStyle.MAX_DEPTH)
		{
			originalLineLenth = (float)Math.sqrt(
				Math.pow((tree.getRoot().getXCoord() - node.getRealCoordinates().x()),2) + 
				Math.pow((tree.getRoot().getYCoord() - node.getRealCoordinates().y()),2));
			if (originalLineLenth > fOriginalLineLenth){
				fOriginalLineLenth = originalLineLenth;
			
			float fNewScalingFactor = treeProjector.getProjectedLineFromCenterToBorder() / fOriginalLineLenth;
//			if (fNewScalingFactor > fScalingFactor)
				fScalingFactor = fNewScalingFactor;
		}
		}
		}
//		fScalingFactor = fScalingFactor * 0.9969f;
//		fScalingFactor = 1.0f;
		return fScalingFactor;
	}
	
//	private float updateTreeScaling(int iMaxLayer){
//		
//		tree.
//		
//		float fOriginalLineLength = calculateLineLengthFromCenterToPoint(float fXCoord, float fYCoord);
//		
//	}
	private float calculateLineLengthFromCenterToPoint(float fXCoord, float fYCoord) {
		float fDx = fWidth/2 - fXCoord;
		float fDy = fHeight/2 - fYCoord;
		float fLine = (float) Math.sqrt(Math.pow(fDx, 2) + Math.pow(fDy, 2));
		return fLine;
	}



}
