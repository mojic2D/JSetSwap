package com.mojic.jset_swap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class PropertyManagerView extends ViewPart {
	private TreeViewer viewer;
	private TreeISProject invisibleRoot;
	private Map<String, ISConnection> connections;
	private String connectionsPath;
	private boolean isJsonValidated = true;

	public String getConnectionsPath() {
		return this.connectionsPath;
	}

	public boolean isJsonValidated() {
		return this.isJsonValidated;
	}

	public void initialize() {

		Map<String, ISConnection> konekcije = new HashMap<String, ISConnection>();

		JSONParser parser = new JSONParser();
		JSONObject a;
		try {
			if (connectionsPath != null) {
				a = (JSONObject) parser.parse(new FileReader(connectionsPath));

				if (!validateJSON(a)) {
					isJsonValidated = false;
				} else {
					isJsonValidated=true;	
					
					JSONArray arr = (JSONArray) a.get("connections");

					for (Object o : arr) {
						JSONObject jsonConn = (JSONObject) o;

						konekcije.put((String) jsonConn.get("naziv"),
								new ISConnection((String)a.get("jdbc"),(String) jsonConn.get("javabaza"), (String) jsonConn.get("uname"),
										(String) jsonConn.get("pname")));
					}
				}
			}

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		this.connections = konekcije;

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject[] projects = workspace.getRoot().getProjects();
		ArrayList<TreeISProject> projectList = new ArrayList<TreeISProject>();
		for (IProject p : projects) {
			projectList.add(new TreeISProject(p.getName(), p));
		}

		try {
			for (TreeISProject tp : projectList) {

				IProject projekat = tp.getProject();
				IResource[] resursiProjekta = projekat.members();
				for (IResource ir : resursiProjekta) {
					if (ir.getName().equalsIgnoreCase("JSet.properties")) {
						Properties prope = new Properties();
						File propFile = new File(ir.getLocationURI());
						FileInputStream fis = new FileInputStream(propFile);
						prope.load(fis);
						tp.setProperties(prope);
						tp.setPropertyFile(propFile);
						fis.close();
					}
				}
				if (tp.getPropertyFile() != null) {
					for (Map.Entry<String, ISConnection> e : konekcije.entrySet()) {
						TreeConnection obj = new TreeConnection(e.getKey());
						tp.addChild(obj);
					}
				}
			}
		} catch (Exception e) {
		}
		invisibleRoot = new TreeISProject("", null);
		for (TreeISProject tp : projectList) {
			if (tp.getPropertyFile() != null) {
				invisibleRoot.addChild(tp);
			}
		}

	}

	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ViewContentProvider(this));
		viewer.setLabelProvider(new DecoratingLabelProvider(new ViewLabelProvider(), new ConnInfoDecorator(this)));
		viewer.setInput(getViewSite());

		hookContextMenu();
		hookDoubleCLickAction();
	}

	private void hookDoubleCLickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				
				ISelection selection = event.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();

				if (obj instanceof TreeISProject) {					
					return;					
				} else {

					TreeConnection tempObj = (TreeConnection) obj;

					ISConnection connec = connections.get(tempObj.getName());
					TreeISProject parent = tempObj.getParent();
					parent.setCurrentConnection(tempObj.getName());				
					PropertiesConfiguration conf;
					try {
						conf = new PropertiesConfiguration(parent.getPropertyFile());
						conf.setProperty("uname", connec.getUsername());
						conf.setProperty("pname", connec.getPassword());
						conf.setProperty("javabaza", "jdbc\\mysql\\//"+connec.getConnection());
						
						
//						Properties ppp=new Properties();
//						ppp.load(new FileInputStream(parent.getPropertyFile()));
//						System.out.println(ppp.get("javabaza"));
						
						
						
//						String jbaza=conf.getString("javabaza");
//						System.out.println("jbaza="+jbaza);
//						String jbaza1=jbaza.substring(0, jbaza.lastIndexOf("/"));
//						System.out.println("jbaza1="+jbaza1);
						//conf.setProperty("javabaza", jbaza1+connec.getConnection());
						
						conf.save();
						
						
						
						
						Properties prop=new Properties();
						prop.load(new FileInputStream(parent.getPropertyFile()));
						parent.setProperties(prop);
						
					} catch (ConfigurationException | IOException e1) {
						e1.printStackTrace();
					}
					viewer.refresh();
				}

			}
		});
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);

		Action refresh = new Action() {
			public void run() {
				initialize();
				viewer.refresh();
			}
		};
		refresh.setText("Refresh");
		menuMgr.add(refresh);

		Action findConnFile = new Action() {
			public void run() {
				FileDialog dialog = new FileDialog(new Shell());
				connectionsPath = dialog.open();
				initialize();
				viewer.refresh();
			}
		};
		findConnFile.setText("Connect File");
		menuMgr.add(findConnFile);
	}
	
	private boolean validateJSON(JSONObject json) {
		if (!json.containsKey("connections")||!json.containsKey("jdbc")) {
			return false;
		}

		JSONArray array = (JSONArray) json.get("connections");
		for (Object object : array) {
			JSONObject jsonConn = (JSONObject) object;
			if (!jsonConn.containsKey("naziv")) {
				return false;
			}
			if (!jsonConn.containsKey("javabaza")) {
				return false;
			}
			if (!jsonConn.containsKey("uname")) {
				return false;
			}
			if (!jsonConn.containsKey("pname")) {
				return false;
			}
		}
		return true;
	}	
	
	@Override
	public void saveState(IMemento memento) {
		memento.putString("connPath", connectionsPath);
		super.saveState(memento);
	}
	
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		if(memento!=null) {
			this.connectionsPath = memento.getString("connPath");
		}
	}
	
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
	public TreeISProject getInvisibleRoot() {
		return this.invisibleRoot;
	}
	
	public Map<String, ISConnection> getConnections() {
		return connections;
	}

}
