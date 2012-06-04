package project.strategies.delegatemas.colony;

import project.common.trucks.Truck;
import rinde.sim.core.SimulatorAPI;
import rinde.sim.core.SimulatorUser;
import rinde.sim.core.TickListener;
import rinde.sim.core.graph.Point;
import rinde.sim.core.model.communication.CommunicationAPI;
import rinde.sim.core.model.communication.CommunicationUser;
import rinde.sim.core.model.communication.Message;

public class TruckAgent implements TickListener, SimulatorUser, CommunicationUser {

    private Truck truck;

    private SimulatorAPI simulatorAPI;
    private CommunicationAPI communicationAPI;

    public TruckAgent(Truck truck) {
	this.truck = truck;
    }

    @Override
    public void setCommunicationAPI(CommunicationAPI api) {
	this.communicationAPI = api;
    }

    @Override
    public Point getPosition() {
	return truck.getPosition();
    }

    @Override
    public double getRadius() {
	return Settings.BROADCAST_RANGE;
    }

    @Override
    public double getReliability() {
	return 1;
    }

    @Override
    public void receive(Message message) {
	// TODO Auto-generated method stub

    }

    @Override
    public void setSimulator(SimulatorAPI api) {
	this.simulatorAPI = api;
    }

    @Override
    public void tick(long currentTime, long timeStep) {
	sendExplorationAnt();
    }

    private void sendExplorationAnt() {
	
    }

    @Override
    public void afterTick(long currentTime, long timeStep) {}

}
