package project.strategies.delegatemas.colony;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import project.common.packages.Package;
import project.common.packages.Priority;
import rinde.sim.core.graph.Graphs;
import rinde.sim.core.graph.Point;
import rinde.sim.core.model.RoadModel;
import rinde.sim.core.model.RoadUser;

public class Path implements Iterable<PackageAgent> {
	
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
	
	public PackageAgent getLast() {
	    return packageAgents.get(packageAgents.size()-1);
	}
	
	public int length() {
	    return packageAgents.size();
	}
	
	public Path removeLast() {
	    Path newPath = new Path(this);
	    newPath.packageAgents.remove(this.getLast());
	    return newPath;
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
	
	@Override
	public String toString() {
	    
	    String string ="";
	    for (PackageAgent agent: packageAgents) {
		string += agent.getId()+"/";
	    }
	    return string;
	}

	@Override
	public Iterator<PackageAgent> iterator() {
	    return packageAgents.iterator();
	}
	
	
	public double getPheromoneBonusForPath(Point start, RoadModel model) {
	    	    
	    if (packageAgents.size() == 0) {
		return 0;
	    }
	    
	    double bonus = 0;
	    Point startPoint = start;
	    for (PackageAgent agent: packageAgents) {
		
		Point pickup = agent.getPackage().getPickupLocation();
		Point dellivery = agent.getPackage().getDeliveryLocation();
		double uselessLength = Graphs.pathLength(model.getShortestPathTo(startPoint, pickup));
		double usefullLength = Graphs.pathLength(model.getShortestPathTo(pickup, dellivery));
		startPoint = dellivery;
		
		double ratio = Math.min(usefullLength/uselessLength, 5);
		ratio /= 5; // ratio has to be in [0,1]
		bonus += ratio*agent.getPackage().getPriority();
	    }
	    
	    return bonus/packageAgents.size();
	}

	
	public boolean isSubPath(Path path) {

	    if (length() == 0 || path.length() == 0)
		return false;
	    
	    if (length() > path.length()) {
		List<PackageAgent> otherpackageAgents = path.getListPackageAgents();
		for (int i =0; i < path.length(); i++) {
		    if (!otherpackageAgents.get(i).equals(packageAgents.get(i))) {
			return false;
		    }
		}
		return true;
	    } else {
		List<PackageAgent> otherpackageAgents = path.getListPackageAgents();
		for (int i =0; i < length(); i++) {
		    if (!otherpackageAgents.get(i).equals(packageAgents.get(i))) {
			return false;
		    }
		}
		return true;
	    }
	}
}
