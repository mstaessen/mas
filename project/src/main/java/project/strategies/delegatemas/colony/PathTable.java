package project.strategies.delegatemas.colony;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.math.random.RandomGenerator;

import rinde.sim.core.graph.Point;
import rinde.sim.core.model.RoadModel;

public class PathTable {

    private HashMap<Path, Double> pheromones;
    private double totPheromones;
    final private double maxPheromone;
    final private double minPheromone;
    final private double startPheromone;

    public PathTable(double maxPheromone, double minPheromone, double startPheromone) {
	this.pheromones = new HashMap<Path, Double>();
	this.maxPheromone = maxPheromone;
	this.minPheromone = minPheromone;
	this.startPheromone = startPheromone;
    }

    public void addPath(Path newPath) {

	if (newPath == null || newPath.length() == 0)
	    return;

	for (Path path : pheromones.keySet()) {

	    int i = 0;
	    List<PackageAgent> newNodes = newPath.getListPackageAgents();
	    List<PackageAgent> nodes = path.getListPackageAgents();

	    if (newNodes.size() == nodes.size()) {

		while (i < newNodes.size() && newNodes.get(i).equals(nodes.get(i))) {
		    i++;
		}
		if (i == newNodes.size()) { // identical path already in library
		    // do nothing
		    return;
		}
	    }
	}

	// Checked all paths for uniqueness.
	pheromones.put(newPath, startPheromone);
	totPheromones += startPheromone;
    }

    public void purgeAndCleanUpFor(PackageAgent oldA, PackageAgent newA) {

	List<Path> toBeRemoved = new ArrayList<Path>();
	HashMap<Path, Double> toBeAdded = new HashMap<Path, Double>();
	for (Path path : pheromones.keySet()) {

	    Path path2;
	    if (oldA != null) {
		path2 = path.getPathWithoutFirst();
	    } else {
		path2 = path;
	    }

	    if (path2.length() != 0 && path2.getFirst().equals(newA)) {
		toBeAdded.put(path2, pheromones.get(path));
	    }

	    toBeRemoved.add(path);
	}
	for (Path p : toBeRemoved) {
	    pheromones.remove(p);
	}
	pheromones.putAll(toBeAdded);

    }

    public void setPheromones(Path path, double amount) {
	List<Path> toBeRemoved = new ArrayList<Path>();
	for (Path p : pheromones.keySet()) {
	    Path commonPart = path.getCommonPart(p);
	    if (commonPart.length() > 0) {
		if (amount < minPheromone) {
		    toBeRemoved.add(p);
		} else {
		    pheromones.put(p, amount);
		}
	    }
	}
	for (Path p : toBeRemoved) {
	    pheromones.remove(p);
	}
    }

    public void decreasePheromones(Path path, double amount) {

	if (amount < 0) {
	    throw new IllegalArgumentException("Amount has to be positive!");
	}

	List<Path> toBeRemoved = new ArrayList<Path>();
	for (Path p : pheromones.keySet()) {
	    Path commonPart = path.getCommonPart(p);
	    if (commonPart.length() > 0) {

		double newPheromoneLevel = pheromones.get(p) - amount;
		if (newPheromoneLevel < minPheromone) {
		    toBeRemoved.add(p);
		} else {
		    totPheromones -= amount;
		    pheromones.put(p, newPheromoneLevel);
		}
	    }
	}
	for (Path p : toBeRemoved) {
	    pheromones.remove(p);
	}
    }

    public void increasePheromones(Path path, double amount) {

	if (amount < 0) {
	    throw new IllegalArgumentException("Amount has to be positive!");
	}

	for (Path p : pheromones.keySet()) {
	    Path commonPart = path.getCommonPart(p);
	    if (commonPart.length() > 0) {
		double newPheromoneLevel = Math.min(pheromones.get(p) + amount, maxPheromone);
		double pheromoneAdded = newPheromoneLevel - pheromones.get(p);
		totPheromones += pheromoneAdded;
		pheromones.put(p, newPheromoneLevel);
	    }
	}

    }

    public void addPheromoneBonus(Path path, Point start, List<Double> intentionValues, RoadModel model) {

	for (Path p : pheromones.keySet()) {
	    Path commonPart = path.getCommonPart(p);
	    if (commonPart.length() > 0) {

		double pheromoneBonus = commonPart.getPheromoneBonusForPath(start, intentionValues, model);
		double newPheromoneLevel = Math.min(pheromones.get(p) + pheromoneBonus, maxPheromone);
		double pheromoneAdded = newPheromoneLevel - pheromones.get(p);
		totPheromones += pheromoneAdded;
		pheromones.put(p, newPheromoneLevel);
	    }
	}

    }

    public void evaporate() {

	List<Path> toBeRemoved = new ArrayList<Path>();
	for (Path p : pheromones.keySet()) {

	    double evaporation = (pheromones.get(p) * pheromones.get(p)) * Settings.EVAPORATION_RATE;

	    double newPheromone = pheromones.get(p) - evaporation;

	    if (newPheromone <= minPheromone) {
		totPheromones -= pheromones.get(p);
		toBeRemoved.add(p);
	    } else {
		pheromones.put(p, newPheromone);
		totPheromones -= evaporation;
	    }
	}
	for (Path p : toBeRemoved) {
	    pheromones.remove(p);
	}
    }

    public Path chosePath() {

	if (pheromones.size() == 0) {
	    return null;
	}

	double chance = Math.random() * totPheromones;
	Path r = null;
	for (Path p : pheromones.keySet()) {
	    r = p;
	    chance -= pheromones.get(p);
	    if (chance < 0) {
		return p;
	    }
	}
	return r;
    }

    public Set<Path> getPaths() {
	return pheromones.keySet();
    }

    public Path getBestPath() {

	if (pheromones.size() == 0)
	    return null;

	double best = 0;
	Path r = null;
	for (Path p : pheromones.keySet()) {
	    if (pheromones.get(p) > best) {
		best = pheromones.get(p);
		r = p;
	    }
	}
	return r;
    }

    @Override
    public String toString() {
	
	TreeMap<Double, Path> tMap = new TreeMap<Double, Path>(new PheromoneComparator());
	
	for (Path p: pheromones.keySet()) {
	    tMap.put(pheromones.get(p), p);
	}	
	
	String string = "";
	DecimalFormat df = new DecimalFormat("#.##");
	for (Map.Entry<Double,Path> entry : tMap.entrySet()) {
	    try {
		string += "\n" + entry.getValue().toString() + "::" + df.format(entry.getKey());
	    } catch (IndexOutOfBoundsException e) {
		//
	    }

	}
	return string;
    }

    
    public String toString(int entries) {
	
	TreeMap<Double, Path> tMap = new TreeMap<Double, Path>(new PheromoneComparator());
	
	HashMap<Path,Double> pheromones2 = (HashMap<Path,Double>) pheromones.clone();
	for (Path p: pheromones2.keySet()) {
	    tMap.put(pheromones2.get(p), p);
	}	
	
	String string = "";
	DecimalFormat df = new DecimalFormat("#.##");
	int i = 0;
	for (Map.Entry<Double,Path> entry : tMap.entrySet()) {
	    
	    if (i == entries) {
		string += "\n ...";
		break;
	    }
	    i++;
	    
	    try {
		string += "\n" + entry.getValue().toString() + "::" + df.format(entry.getKey());
	    } catch (IndexOutOfBoundsException e) {
		//
	    }

	}
	return string;
    }
    
    
    
    
    public Double getPheromone(Path path) {
	return pheromones.get(path);
    }
    
    
    public class PheromoneComparator implements Comparator<Double> {

	@Override
	public int compare(Double d1, Double d2) {
	    return -Double.compare(d1, d2);
	}
	
    }

}
