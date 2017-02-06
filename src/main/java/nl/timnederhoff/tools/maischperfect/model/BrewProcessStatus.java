package nl.timnederhoff.tools.maischperfect.model;

import nl.timnederhoff.tools.maischperfect.model.highcharts.PlotLine;
import nl.timnederhoff.tools.maischperfect.model.highcharts.Point;

import java.util.List;

public class BrewProcessStatus {

	private List<Point> templog;
	private List<Point> appliedModel;
	private List<PlotLine> heaterlog;
	private boolean isEnded;
	private double slope;

	public BrewProcessStatus(List<Point> templog, List<Point> appliedModel, List<PlotLine> heaterlog, boolean isEnded, double slope) {
		this.templog = templog;
		this.appliedModel = appliedModel;
		this.heaterlog = heaterlog;
		this.isEnded = isEnded;
		this.slope = slope;
	}

	public List<Point> getTemplog() {
		return templog;
	}

	public List<Point> getAppliedModel() {
		return appliedModel;
	}

	public List<PlotLine> getHeaterlog() {
		return heaterlog;
	}

	public boolean isEnded() {
		return isEnded;
	}

	public double getSlope() {
		return slope;
	}
}
