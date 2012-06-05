package project.experiments;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import project.common.controller.AbstractController;
import project.common.listeners.Report;
import rinde.sim.scenario.ConfigurationException;
import rinde.sim.scenario.Scenario;

public abstract class Experiment {

    protected List<Report> reports = new ArrayList<Report>();

    protected abstract AbstractController createController(Scenario scenario);

    protected abstract Scenario createScenario();

    protected FileWriter writer;

    private int runs;

    public Experiment(String reportFile) throws IOException {
	writer = new FileWriter(new File(reportFile + ".csv"));
    }

    public void receiveReport(Report report) {
	reports.add(report);
	runs--;

	if (runs == 0) {
	    try {
		showResults();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }

    private void showResults() throws IOException {
	writeHeader();
	int id = 0;
	for (Report report : reports) {
	    writeLine(id++, report);
	}
	writeTotalAverage();

	writer.flush();
	writer.close();
    }

    private void writeTotalAverage() throws IOException {
	writer.write("Total Average");
	writer.write(";");

	double avgDeliveries = 0;
	double avgPULateness = 0;
	double avgDLLateness = 0;
	double avgCompTime = 0;
	double avgDistance = 0;
	for (Report report : reports) {
	    avgDeliveries += report.getDeliveredPackages();
	    avgPULateness += report.getAvgPickupLateness();
	    avgDLLateness += report.getAvgDeliveryLateness();
	    avgCompTime += report.getAvgCompletionTime();
	    // avgDistance += report.getAvgDistance();
	}
	writer.write(avgDeliveries / reports.size() + "");
	writer.write(";");
	writer.write(avgPULateness / reports.size() + "");
	writer.write(";");
	writer.write(avgDLLateness / reports.size() + "");
	writer.write(";");
	writer.write(avgCompTime / reports.size() + "");
	writer.write(";");
	writer.write(avgDistance / reports.size() + "");
	writer.write("\n");
    }

    private void writeLine(int runId, Report report) throws IOException {
	writer.write(runId + "");
	writer.write(";");
	writer.write(report.getDeliveredPackages() + "");
	writer.write(";");
	writer.write(report.getAvgPickupLateness() + "");
	writer.write(";");
	writer.write(report.getAvgDeliveryLateness() + "");
	writer.write(";");
	writer.write(report.getAvgCompletionTime() + "");
	writer.write(";");
	writer.write("TODO! AVG Distance Travelled");
	writer.write("\n");
    }

    private void writeHeader() throws IOException {
	writer.write("Run");
	writer.write(";");
	writer.write("Delivered Packages");
	writer.write(";");
	writer.write("AVG Pickup Lateness");
	writer.write(";");
	writer.write("AVG Delivery Lateness");
	writer.write(";");
	writer.write("AVG Completion Time");
	writer.write(";");
	writer.write("AVG Distance Travelled");
	writer.write("\n");
    }

    public void run(int seed) {
	AbstractController controller = createController(createScenario());
	try {
	    controller.start(seed);
	} catch (ConfigurationException e) {
	    e.printStackTrace();
	}
    }

    public void runMultiple(int times) {
	this.runs = times;
	if (times < 1) {
	    throw new IllegalArgumentException("You have to run it at least one time.");
	}

	for (int i = 0; i < times; i++) {
	    run(7 * i);
	}
    }
}