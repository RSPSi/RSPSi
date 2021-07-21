package com.rspsi.renderer;

import com.scalified.tree.multinode.ArrayMultiTreeNode;

public abstract class SceneNode {

	protected ArrayMultiTreeNode<SceneNode> node;

	public SceneNode() {
		this.node = new ArrayMultiTreeNode<>(this);
	}

	public abstract void invalidate(boolean modelAction);

}
