package project.util;

import java.math.BigDecimal;

import rinde.sim.core.graph.Graph;
import rinde.sim.core.graph.Graphs;
import rinde.sim.core.graph.MultiAttributeEdgeData;
import rinde.sim.core.graph.Point;
import rinde.sim.core.model.RoadModel;
import rinde.sim.scenario.ConfigurationException;
import rinde.sim.serializers.DotGraphSerializer;
import rinde.sim.serializers.SelfCycleFilter;

public class AverageDistanceCalculator {

    private String map;
    private RoadModel roadModel;
    private Graph<MultiAttributeEdgeData> graph;
    private double accumulatedDistance = 0d;
    private long nbRoutes = 0;

//    private static final String MAP_DIR = "../core/files/maps/";
//    private static final String MAP = "leuven-simple.dot";
    private static final String MAP_DIR = "files/maps/";
    private static final String MAP = "grid-16x9.dot";
    private static final String MAP_URI = MAP_DIR + MAP;

    public static void main(String[] args) throws ConfigurationException {
	AverageDistanceCalculator calc = new AverageDistanceCalculator(MAP_URI);
	calc.calculate();
    }

    public AverageDistanceCalculator(String map) {
	this.map = map;
    }

    protected void loadMap() throws ConfigurationException {
	try {
	    graph = DotGraphSerializer.getMultiAttributeGraphSerializer(new SelfCycleFilter()).read(map);
	} catch (Exception e) {
	    throw new ConfigurationException("e:", e);
	}
	roadModel = new RoadModel(graph);
    }

    protected void calculate() throws ConfigurationException {
	loadMap();

	for (Point from : roadModel.getGraph().getNodes()) {
	    for (Point to : roadModel.getGraph().getNodes()) {
		if (from != to) {
		    double distance = Graphs.pathLength(roadModel.getShortestPathTo(from, to));
		    accumulatedDistance = accumulatedDistance + distance;
		    nbRoutes++;
		}
	    }
	}

	System.out.println("Total Distance: " + accumulatedDistance);
	System.out.println("Number of possible routes: " + nbRoutes);
	System.out.println("Average Distance: " + accumulatedDistance / nbRoutes);
    }

}
