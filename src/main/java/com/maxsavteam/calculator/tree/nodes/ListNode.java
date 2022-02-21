package com.maxsavteam.calculator.tree.nodes;

import java.util.ArrayList;
import java.util.List;

/**
 * Node which contains list of nodes
 * <p>
 * Each item is always TreeNode and contains only left son
 */
public class ListNode extends TreeNode {

	private final List<TreeNode> nodes;

	public ListNode(List<TreeNode> nodes) {
		this.nodes = nodes;
	}

	public List<TreeNode> getNodes() {
		return new ArrayList<>(nodes);
	}
}
