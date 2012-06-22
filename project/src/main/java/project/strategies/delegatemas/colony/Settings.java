package project.strategies.delegatemas.colony;

public class Settings {

    public static double BROADCAST_RANGE = 80; // grid 10x10
//     public static double BROADCAST_RANGE = 13000; // leuven

    public static int MAX_HOPS_FEASIBILITY_ANT = 2;
    public static int MAX_HOPS_EXPLORATION_ANT = 3;

    public static final double MIN_PACKAGE_PHEROMONE_PATH = 0.1;
    public static final double MAX_PACKAGE_PHEROMONE_PATH = 10;
    public static final double START_PACKAGE_PHEROMONE_PATH = 1;
    
    public static final double MIN_TRUCK_PHEROMONE_PATH = 0.1;
    public static final double MAX_TRUCK_PHEROMONE_PATH = 300;
    public static final double START_TRUCK_PHEROMONE_PATH = 0.4;
    
    public static final double EVAPORATION_RATE = 0.0333333;
    public static final double EARLY_RETURN_RATE_EXPLORATION_ANT = 0.333;
    public static final int DURATION_CLAIM = 12;
    
    public static final double INTENTION_PENALTY = 0.25;

}
