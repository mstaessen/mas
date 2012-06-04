package project.modern.delegatemas.colony;

import java.util.ArrayList;
import java.util.List;

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
		pheromones.add(Settings.DEFAULT_INITIAL_PHEROMONE);
		totPheromones += Settings.DEFAULT_INITIAL_PHEROMONE;
	}

	
	public void addPheromone(Path path, double pheromone) {
		int index = -1;
		for (int i =0; i < paths.size(); i++) {
			if (paths.get(i).equals(path)) {
				index = i;
				break;
			}
		}
		if (index < 0) {
			System.out.println("No such path in the table, can not add pheromone.");
			return;
		}
		
		pheromones.set(index,pheromones.get(index)+pheromone);
		totPheromones += pheromone;
	}
	
	public void evaporate() {
		for(int i = 0; i < pheromones.size(); i++) {
			pheromones.set(i,pheromones.get(i)*(1-Settings.PHEROMONES_EVAPORARION_RATE));
			totPheromones -= pheromones.get(i)*Settings.PHEROMONES_EVAPORARION_RATE;
		}
	}
	
	public Path chosePath() {
		double chance = Math.random()*totPheromones;
		int i = 0;
		while (chance > 0) {
			chance -= pheromones.get(i); 
		}
		return paths.get(i);
	}

}
