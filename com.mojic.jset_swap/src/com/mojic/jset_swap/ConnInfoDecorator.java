package com.mojic.jset_swap;

import java.util.Properties;

import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

public class ConnInfoDecorator implements ILabelDecorator {
	
	PropertyManagerView view;
	
	public ConnInfoDecorator(PropertyManagerView view) {
		super();
		this.view=view;
	}

	@Override
	public void addListener(ILabelProviderListener listener) {}

	@Override
	public void dispose() {}

	@Override
	public boolean isLabelProperty(Object element, String property) {return false;}

	@Override
	public void removeListener(ILabelProviderListener listener) {}

	@Override
	public Image decorateImage(Image image, Object element) {return null;}

	@Override
	public String decorateText(String text, Object element) {
		
		if(view.isJsonValidated()==false) {
			return text+" -> JSON fajl nije validan!";
		}
		
		if(view.getConnectionsPath()==null) {
			return text+" -> Fajl sa konekcijama nije povezan";
		}
		
		if (element instanceof TreeConnection && !(element instanceof TreeISProject)) {
			ISConnection jc = view.getConnections().get(((TreeConnection) element).getName());
			return text + " -> {" + jc.getConnection() + " ; " + jc.getUsername() + " ; " + jc.getPassword() + "}";
		}

		if (element instanceof TreeISProject) {
			if (((TreeISProject) element).getProperties() == null) {
				return text + " -> Nedostaje 'JSet.properties' fajl.";
			}
			Properties prop = ((TreeISProject) element).getProperties();			
			return text + " - "+
					(((TreeISProject)element).getCurrentConnection()==null?"?":((TreeISProject)element).getCurrentConnection())+
					" -> {" + prop.getProperty("javabaza") + " ; " + prop.getProperty("uname") + " ; "
					+ prop.getProperty("pname") + "}";
		}

		return " [ PREFIX ] " + text + " [ SUFFIX ]";
	}
}
