package nl.timnederhoff.tools.maischperfect.model.highcharts;

public class Point {
	long x;
	double y;

	public Point(long x, double y) {
		this.x = x;
		this.y = y;
	}

	public long getX() {
		return x;
	}

	public double getY() {
		return y;
	}
}
