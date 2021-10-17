package com.maxsavteam.calculator.tree.nodes;

import java.util.ArrayList;

/**
 * Node which contains list of nodes
 *
 * Each item is always TreeNode and contains only left son
 * */
public class ListNode extends TreeNode {

	private final ArrayList<TreeNode> nodes;

	public ListNode(ArrayList<TreeNode> nodes) {
		this.nodes = nodes;
	}

	public ArrayList<TreeNode> getNodes() {
		return new ArrayList<>( nodes );
	}
}
