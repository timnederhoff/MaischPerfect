package nl.timnederhoff.tools.maischperfect.model;

import nl.timnederhoff.tools.maischperfect.model.highcharts.PlotLine;
import nl.timnederhoff.tools.maischperfect.model.highcharts.Point;

import java.util.List;

public class TempResponse {

	private List<Point> templog;
	private List<Point> appliedModel;
	private List<PlotLine> heaterlog;
	private boolean isEnded;

	public TempResponse() {
	}

	public TempResponse(List<Point> templog, List<Point> appliedModel, List<PlotLine> heaterlog, boolean isEnded) {
		this.templog = templog;
		this.appliedModel = appliedModel;
		this.heaterlog = heaterlog;
		this.isEnded = isEnded;
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
}
