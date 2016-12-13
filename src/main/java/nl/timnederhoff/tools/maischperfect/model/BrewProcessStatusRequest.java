package nl.timnederhoff.tools.maischperfect.model;

public class BrewProcessStatusRequest {

	private Integer fromPointTemp;
	private Integer fromPointMaisch;
	private Integer fromPointHeater;

	public BrewProcessStatusRequest() {
	}

	public BrewProcessStatusRequest(Integer fromPointTemp, Integer fromPointMaisch, Integer fromPointHeater) {
		this.fromPointTemp = fromPointTemp;
		this.fromPointMaisch = fromPointMaisch;
		this.fromPointHeater = fromPointHeater;
	}

	public Integer getFromPointTemp() {
		return fromPointTemp;
	}

	public Integer getFromPointMaisch() {
		return fromPointMaisch;
	}

	public Integer getFromPointHeater() {
		return fromPointHeater;
	}
}
