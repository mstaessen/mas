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

    public Path(ArrayList<PackageAgent> agents) {
	this.packageAgents = agents;
    }

    public Path(Path path, PackageAgent agent) {
	this(path);
	this.packageAgents.add(agent);
    }
    
    public Path(PackageAgent agent, Path path) {
	this(path);
	this.packageAgents.add(0, agent);
    }

    @SuppressWarnings("unchecked")
    public Path(Path path) {
	this.packageAgents = (ArrayList<PackageAgent>) path.getListPackageAgents().clone();
    }

    public ArrayList<PackageAgent> getListPackageAgents() {
	return packageAgents;
    }

    public boolean contains(PackageAgent agent) {
	for (PackageAgent a : packageAgents) {
	    if (agent.equals(a)) {
		return true;
	    }
	}
	return false;
    }

    public PackageAgent getLast() {
	return packageAgents.get(packageAgents.size() - 1);
    }
    
    public PackageAgent getFirst() {
	return packageAgents.get(0);
    }

    public int length() {
	return packageAgents.size();
    }

    public Path getPathWithoutLast() {
	Path newPath = new Path(this);
	newPath.packageAgents.remove(this.getLast());
	return newPath;
    }
    
    public Path getPathWithoutFirst() {
	Path newPath = new Path(this);
	newPath.packageAgents.remove(this.getFirst());
	return newPath;
    }

    @Override
    public boolean equals(Object object) {

	if (object instanceof Path) {
	    Path otherPath = (Path) object;
	    if (otherPath.getListPackageAgents().size() != packageAgents.size())
		return false;
	    else {
		for (int i = 0; i < packageAgents.size(); i++) {
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

	String string = "";
	for (PackageAgent agent : packageAgents) {
	    string += agent.getId() + "/";
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
	Point prevDellivery = start;
	for (PackageAgent agent : packageAgents) {

	    Point pickup = agent.getPackage().getPickupLocation();
	    
	    double uselessLength = Graphs.pathLength(model.getShortestPathTo(prevDellivery, pickup));
	    prevDellivery = agent.getPackage().getDeliveryLocation();
	    
	    double ratio = Math.max(uselessLength/Settings.BROADCAST_RANGE, 0.1);
	    
	    double bonusTerm = 10*agent.getPackage().getPriority()/(ratio);
	    bonus += bonusTerm;
	}

	return bonus;
    }

    public Path getCommonPart(Path path) {

	if (length() == 0 || path.length() == 0)
	    return new Path();

	int n;
	if (length() > path.length()) {
	    n = path.length();
	} else {
	    n = length();
	}

	ArrayList<PackageAgent> newpackageAgents = new ArrayList<PackageAgent>();
	List<PackageAgent> otherpackageAgents = path.getListPackageAgents();
	for (int i = 0; i < n; i++) {
	    if (otherpackageAgents.get(i).equals(packageAgents.get(i))) {
		newpackageAgents.add(packageAgents.get(i));
	    } else {
		return new Path(newpackageAgents);
	    }
	}
	return new Path(newpackageAgents);
    }
}
