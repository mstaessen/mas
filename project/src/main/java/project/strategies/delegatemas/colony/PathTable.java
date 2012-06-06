package project.strategies.delegatemas.colony;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.math.random.RandomGenerator;

import rinde.sim.core.graph.Point;
import rinde.sim.core.model.RoadModel;

public class PathTable {

    private HashMap<Path,Double> pheromones;
    private double totPheromones;
    private double maxPheromone;

    public PathTable(double maxPheromone) {
	this.pheromones = new HashMap<Path,Double>();
	this.maxPheromone = maxPheromone;
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
	pheromones.put(newPath,Settings.START_PHEROMONE_PATH);
	totPheromones += Settings.START_PHEROMONE_PATH;
    }
    
    public void purgeFromTable(PackageAgent agent) {
	
	List<Path> toBeRemoved = new ArrayList<Path>();
	HashMap<Path,Double> toBeAdded = new HashMap<Path,Double>();
	for (Path path : pheromones.keySet()) {
	    
	    if (path.length() == 0) {
		toBeRemoved.add(path);
	    } else if (path.getFirst().equals(agent)) {
		toBeRemoved.add(path);
		Path newPath = path.getPathWithoutFirst();
		if (newPath.length() != 0 && !newPath.contains(agent)) {
		    toBeAdded.put(newPath, pheromones.get(path));
		}
	    } else {
		toBeRemoved.add(path);
	    }
	}
	for (Path p: toBeRemoved) {
	    pheromones.remove(p);
	}
	pheromones.putAll(toBeAdded);
	
    }

    public void penaltyPheromones(Path path) {
	for (Path p: pheromones.keySet()) {
	    Path commonPart = path.getCommonPart(p);
	    if (commonPart.length() > 0) {
		pheromones.put(p, Settings.START_PHEROMONE_PATH/2);
	    }
	}
    }

    public void updatePheromones(Path path, Point start, RoadModel model) {

	for (Path p: pheromones.keySet()) {
	    Path commonPart = path.getCommonPart(p);
	    if (commonPart.length() > 0) {

		double pheromoneBonus = commonPart.getPheromoneBonusForPath(start, model);
		double newPheromoneLevel = Math
			.min(pheromones.get(p) + pheromoneBonus, maxPheromone);
		double pheromoneAdded = newPheromoneLevel - pheromones.get(p);
		totPheromones += pheromoneAdded;
		pheromones.put(p, newPheromoneLevel);
	    }
	}

    }

    public void evaporate() {
	
	List<Path> toBeRemoved = new ArrayList<Path>();
	for (Path p: pheromones.keySet()) {

	    double evaporation = (pheromones.get(p))/Settings.TIMESTEPS_WAIT_BEFORE_SENDING_FEASIBILITY_ANTS;

	    double newPheromone = pheromones.get(p) - evaporation;

	    if (newPheromone <= Settings.MIN_PHEROMONE_PATH) {
		totPheromones -= pheromones.get(p);
		toBeRemoved.add(p);
	    } else {
		pheromones.put(p, newPheromone);
		totPheromones -= evaporation;
	    }
	}
	for (Path p: toBeRemoved) {
	    pheromones.remove(p);
	}
    }

    public Path chosePath(RandomGenerator gen) {

	if (pheromones.size() == 0) {
	    return null;
	}

	double chance = gen.nextDouble() * totPheromones;
	Path r = null;
	for (Path p: pheromones.keySet()) {
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
	for (Path p: pheromones.keySet()) {
	    if (pheromones.get(p) > best) {
		best = pheromones.get(p);
		r = p;
	    }
	}
	return r;
    }
    
    
    public Path getBestPath2() {

	if (pheromones.size() == 0)
	    return null;

	double best = 0;
	Path r = null;
	for (Path p: pheromones.keySet()) {
	    if (pheromones.get(p) > best) {
		best = pheromones.get(p);
		r = p;
	    }
	}
	return r;
    }

    @Override
    public String toString() {
	String string = "";
	DecimalFormat df = new DecimalFormat("#.##");
	HashMap<Path,Double> pheromones2 = new HashMap<Path,Double>();
	pheromones2.putAll(pheromones);
	for (Path p: pheromones.keySet()) {
	    try {
		string += "\n" + p.toString() + "::" + df.format(pheromones2.get(p));
	    } catch (IndexOutOfBoundsException e) {
		//
	    }

	}
	return string;
    }
    
    public Double getPheromone(Path path) {
	return pheromones.get(path);
    }
}
