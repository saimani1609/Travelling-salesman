import java.util.ArrayList;


public class Point {
	public int x;
	public int y;
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public static float getDistanceBetween(Point a, Point b){
		return (float) Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
	}
	
	public Point getNearestPointIn(ArrayList<? extends Point> points){
		float nearest = Float.MAX_VALUE;
		Point nearestP = null;
		for(Point p : points){
			float tmp = getDistanceBetween(this, p);
			if(tmp < nearest){
				nearestP = p;
				nearest = tmp;
			}
		}
		return nearestP;
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
	
	@Override
	protected Point clone() {
		return new Point(x, y);
	}
}
