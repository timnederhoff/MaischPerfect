package nl.timnederhoff.tools.maischperfect.model;

import java.util.List;

public class TempResponse {

	private List<Integer[]> templog;
	private List<Integer[]> maischmodel;
	private List<Object[]> heaterlog;

	public TempResponse() {
	}

	public TempResponse(List<Integer[]> templog, List<Integer[]> maischmodel, List<Object[]> heaterlog) {
		this.templog = templog;
		this.maischmodel = maischmodel;
		this.heaterlog = heaterlog;
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
}
