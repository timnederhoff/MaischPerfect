package nl.timnederhoff.tools.maischperfect.model;

import java.util.List;

public class TempResponse {

	private List<Integer[]> templog;
	private List<Integer[]> maischmodel;
	private List<Object[]> heaterlog;
	private boolean isEnded;

	public TempResponse() {
	}

	public TempResponse(List<Integer[]> templog, List<Integer[]> maischmodel, List<Object[]> heaterlog, boolean isEnded) {
		this.templog = templog;
		this.maischmodel = maischmodel;
		this.heaterlog = heaterlog;
		this.isEnded = isEnded;
	}

	public List<Integer[]> getTemplog() {
		return templog;
	}

	public List<Integer[]> getMaischmodel() {
		return maischmodel;
	}

	public List<Object[]> getHeaterlog() {
		return heaterlog;
	}

	public boolean isEnded() {
		return isEnded;
	}
}
