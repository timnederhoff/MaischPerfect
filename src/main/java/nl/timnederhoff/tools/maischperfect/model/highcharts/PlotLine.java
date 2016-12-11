package nl.timnederhoff.tools.maischperfect.model.highcharts;

public class PlotLine {

	private String color;
	private String dashStyle;
	private long value;
	private int width;
	private Label label;

	public PlotLine() {
	}

	public PlotLine(String color, String dashStyle, long value, int width, Label label) {
		this.color = color;
		this.dashStyle = dashStyle;
		this.value = value;
		this.width = width;
		this.label = label;
	}

	public String getColor() {
		return color;
	}

	public String getDashStyle() {
		return dashStyle;
	}

	public long getValue() {
		return value;
	}

	public int getWidth() {
		return width;
	}

	public Label getLabel() {
		return label;
	}
}
