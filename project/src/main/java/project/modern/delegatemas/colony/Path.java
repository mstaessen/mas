package project.modern.delegatemas.colony;

import java.util.ArrayList;

public class Path {
	
	private ArrayList<PackageAgent> packageAgents;
	
	
	public Path() {
		this.packageAgents = new ArrayList<PackageAgent>();
	}
	
	public Path(PackageAgent agent) {
		this.packageAgents = new ArrayList<PackageAgent>();
		this.packageAgents.add(agent);
	}
	
	public Path(Path path, PackageAgent agent) {
		this(path);
		this.packageAgents.add(agent);
	}
	
	@SuppressWarnings("unchecked")
	public Path(Path path) {
		this.packageAgents = (ArrayList<PackageAgent>) path.getListPackageAgents().clone();
	}
	
	public ArrayList<PackageAgent> getListPackageAgents() {
		return packageAgents;
	}
	
	public boolean contains(PackageAgent agent) {
		for (PackageAgent a: packageAgents) {
			if (agent.equals(a)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean equals(Object object) {
		
		if(object instanceof Path) {
			Path otherPath = (Path) object;
			if (otherPath.getListPackageAgents().size() != packageAgents.size())
				return false;
			else {
				for (int i =0; i < packageAgents.size(); i++) {
					if (!otherPath.getListPackageAgents().get(i).equals(packageAgents.get(i))) {
						return false;
					}
				}
				return true;
			}
		} else {
			return false;
		}
	}

}
