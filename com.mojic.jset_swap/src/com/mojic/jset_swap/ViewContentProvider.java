package com.mojic.jset_swap;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ViewContentProvider implements ITreeContentProvider {

	PropertyManagerView view;

	public ViewContentProvider(PropertyManagerView view) {
		this.view = view;
	}

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}

	public void dispose() {
	}

	public Object[] getElements(Object parent) {
		if (parent.equals(view.getViewSite())) {
			if (view.getInvisibleRoot() == null) {
				view.initialize();
			}
			return getChildren(view.getInvisibleRoot());
		}
		return getChildren(parent);
	}

	public Object getParent(Object child) {
		if (child instanceof TreeConnection) {
			return ((TreeConnection) child).getParent();
		}
		return null;
	}

	public Object[] getChildren(Object parent) {
		if (parent instanceof TreeISProject) {
			return ((TreeISProject) parent).getChildren();
		}
		return new Object[0];
	}

	public boolean hasChildren(Object parent) {
		if (parent instanceof TreeISProject)
			return ((TreeISProject) parent).hasChildren();
		return false;
	}
}
