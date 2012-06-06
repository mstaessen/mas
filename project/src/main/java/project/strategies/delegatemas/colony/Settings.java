package project.strategies.delegatemas.colony;

public class Settings {

    public static double BROADCAST_RANGE = 60; // grid 10x10
    // public static final double BROADCAST_RANGE = 10000; // leuven

    public static final int TIMESTEPS_WAIT_BEFORE_SENDING_FEASIBILITY_ANTS = 50;
    public static int MAX_HOPS_FEASIBILITY_ANT = 1;
    public static int MAX_HOPS_EXPLORATION_ANT = 3;

    public static final double MIN_PHEROMONE_PATH = 0.1;
    public static final double MAX_PHEROMONE_PATH = 10;
    public static final double START_PHEROMONE_PATH = 1;

    public static final double DEFAULT_INITIAL_PHEROMONE = 0.2;
    public static final double PHEROMONES_EVAPORARION_RATE = 0.05;

}
