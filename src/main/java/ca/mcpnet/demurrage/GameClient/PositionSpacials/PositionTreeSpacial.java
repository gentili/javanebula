package ca.mcpnet.demurrage.GameClient.PositionSpacials;

import ca.mcpnet.demurrage.GameEngine.TransformTree;

import com.jme3.scene.Node;

public class PositionTreeSpacial extends Node {
	TransformTree _pt;
	
	PositionTreeSpacial(TransformTree pt) {
		_pt = pt;
		// Default to root node as one focus
		// Find most distant child node for other focus
	}

}
