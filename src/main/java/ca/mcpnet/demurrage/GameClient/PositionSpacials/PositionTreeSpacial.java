package ca.mcpnet.demurrage.GameClient.PositionSpacials;

import ca.mcpnet.demurrage.GameEngine.Positions.PositionTree;

import com.jme3.scene.Node;

public class PositionTreeSpacial extends Node {
	PositionTree _pt;
	
	PositionTreeSpacial(PositionTree pt) {
		_pt = pt;
		// Default to root node as one focus
		// Find most distant child node for other focus
	}

}
