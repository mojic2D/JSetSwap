package com.mojic.jset_swap;

import java.io.File;
import java.util.ArrayList;
import java.util.Properties;

import org.eclipse.core.resources.IProject;

class TreeISProject extends TreeConnection {
	private ArrayList children;
	private IProject project;
	private Properties property;
	private File propertyFile;
	private String currentConnection;

	public TreeISProject(String name, IProject project) {
		super(name);
		children = new ArrayList();
		this.project = project;
	}

	public void addChild(TreeConnection child) {
		children.add(child);
		child.setParent(this);
	}

	public void removeChild(TreeConnection child) {
		children.remove(child);
		child.setParent(null);
	}

	public TreeConnection[] getChildren() {
		return (TreeConnection[]) children.toArray(new TreeConnection[children.size()]);
	}

	public boolean hasChildren() {
		return children.size() > 0;
	}

	public IProject getProject() {
		return this.project;
	}

	public void setProperties(Properties property) {
		this.property = property;
	}

	public Properties getProperties() {
		return this.property;
	}

	public File getPropertyFile() {
		return propertyFile;
	}

	public void setPropertyFile(File propertyFile) {
		this.propertyFile = propertyFile;
	}
	
	public String getCurrentConnection() {
		//System.out.println("Ime:"+this.getName()+"; currentConn="+this.getCurrentConnection());
		return this.currentConnection;
	}
	
	public void setCurrentConnection(String currentConnection) {
		//System.out.println("Current conection="+currentConnection);
		this.currentConnection=currentConnection;
	}

}
