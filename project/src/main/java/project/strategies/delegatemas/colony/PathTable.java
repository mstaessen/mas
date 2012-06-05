package project.strategies.delegatemas.colony;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math.random.RandomGenerator;

import rinde.sim.core.graph.Point;
import rinde.sim.core.model.RoadModel;

public class PathTable {

    private List<Path> paths;
    private List<Double> pheromones;
    private double totPheromones;

    public PathTable() {
	this.paths = new ArrayList<Path>();
	this.pheromones = new ArrayList<Double>();
    }

    public void addPath(Path newPath) {

	for (Path path : paths) {

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
	paths.add(newPath);
	pheromones.add(Settings.START_PHEROMONE_PATH);
	totPheromones += Settings.START_PHEROMONE_PATH;
    }

    public void penaltyPheromones(Path path) {
	for (int index=0; index < paths.size(); index++) {
	    Path commonPart = path.getCommonPart(paths.get(index));
	    if (commonPart.length() > 0) {
		pheromones.set(index, 3*Settings.MIN_PHEROMONE_PATH);
	    }
	}
    }
    
    public void updatePheromones(Path path, Point start, RoadModel model) {
	
	for (int index=0; index < paths.size(); index++) {
	    Path commonPart = path.getCommonPart(paths.get(index));
	    if (commonPart.length() > 0) {
		
		double pheromoneBonus = commonPart.getPheromoneBonusForPath(start, model);
		double newPheromoneLevel = Math.min(pheromones.get(index) + pheromoneBonus,Settings.MAX_PHEROMONE_PATH);
		double pheromoneAdded = newPheromoneLevel - pheromones.get(index);
		totPheromones += pheromoneAdded;
		pheromones.set(index, newPheromoneLevel);
	    }
	}
	
    }

    public void evaporate() {
	for (int i = 0; i < pheromones.size(); i++) {
	    
	    double evaporation = 0.05*Settings.MAX_HOPS_EXPLORATION_ANT*pheromones.get(i)*pheromones.get(i);
	    
	    double newPheromone = pheromones.get(i) - evaporation;
	    
	    if (newPheromone < Settings.MIN_PHEROMONE_PATH) {
		totPheromones -= pheromones.get(i);
		pheromones.remove(i);
		paths.remove(i);	
		i--;
	    } else {
		pheromones.set(i, newPheromone);
		totPheromones -= evaporation;
	    }
	}
    }

    public Path chosePath(RandomGenerator gen) {

	if (paths.size() == 0) {
	    return null;
	}

	double chance = gen.nextDouble()* totPheromones;
	int i = -1;
	while (chance >= 0) {
	    i++;
	    chance -= pheromones.get(i);
	}
	
	return paths.get(i);
    }

    public List<Path> getPaths() {
	return paths;
    }
    
    public Path getBestPath() {
	
	if (paths.size() == 0)
	    return null;
	
	double best = 0;
	int index = 0;
	for (int i=0;i<pheromones.size();i++) {
	    if (pheromones.get(i) > best) {
		best = pheromones.get(i);
		index = i;
	    }
	}
	return paths.get(index);
    }
    
    @Override
    public String toString() {
	String string = "";
	DecimalFormat df = new DecimalFormat("#.##");
	for (int i = 0; i < paths.size(); i++) {
	    try {
		string += "\n" + paths.get(i).toString()+"::"+df.format(pheromones.get(i));
	    } catch (IndexOutOfBoundsException e) {
		//
	    }
	    
	}
	return string;
    }
}
