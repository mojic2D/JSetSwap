package com.mojic.jset_swap;

import org.eclipse.core.runtime.IAdaptable;

class TreeConnection implements IAdaptable {
	private String name;
	private TreeISProject parent;

	public TreeConnection(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setParent(TreeISProject parent) {
		this.parent = parent;
	}

	public TreeISProject getParent() {
		return parent;
	}

	public String toString() {
		return getName();
	}

	public Object getAdapter(Class key) {
		return null;
	}

	
}
