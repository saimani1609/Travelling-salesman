import java.beans.FeatureDescriptor;
import java.util.ArrayList;


public class Plane {

	public static final int MAX_FUEL = 50;//gallon
	public static final int MAX_CHEMICAL = 100;//gallon
	private final int FLYING_SPEED = 100;//miles per hour
	
	private final int TIME_REFILL_FUEL = 15;//minute
	private final int TIME_REFILL_CHEM = 30;//minute
	
	private final float FUEL_BURNING_RATE = 10;//gallons per hour
	
	
	private int totalFlyingTime;
	private int totalTime;
	private float totalFlyingDistance;
	
	
	private float totalFuel;
	private float totalChem;
	private float totalFuelCost;
	private float totalChemCost;
	private float remainingFuel = 0;
	private float remainingChem = 0;
	
	public int currX;
	public int currY;
	public Point currPoint;
	
	private ArrayList<RefillingPoint> refillingPoints; 
	private ArrayList<Point> taskPoints;
	
	ArrayList<Event> events = new ArrayList<>();
	
	CropDuster cropDuster;
	
	public Plane(CropDuster cropDuster){
		super();
		this.cropDuster = cropDuster;
	}
	
	public void setTaskPoints(ArrayList<Point> task){
		taskPoints = task;
	}
	
	public void setRefillingPoints(ArrayList<RefillingPoint> points){
		refillingPoints = points;
	}
	
	public ArrayList<Event> startWork(){
		RefillingPoint home = refillingPoints.get(0);
		currPoint = home;//which is home point
		refillAt(home);
		
		cropDuster.outputToTextArea("At home base " + home + " and refill.");
		
		while(!taskPoints.isEmpty()){
			Point p = findNextPoint();
			flyToPoint(p);
			
			if(refillingPoints.contains(p)){
				//plane going to get refilled
				refillAt((RefillingPoint)p);
				cropDuster.outputToTextArea("Arrive at " + p + " and refill.");
			} else {
				//plane going to dust
				dust();
				cropDuster.outputToTextArea("Arrive at " + p + " and dust.");
			}
		}
		
		cropDuster.outputToTextArea("\n\ntotal time :" + totalTime / 60f);
		cropDuster.outputToTextArea("total flight hours :" + totalFlyingTime / 60f);
		cropDuster.outputToTextArea("total fuel and cost : " + totalFuel + " G / $" + totalFuelCost);
		cropDuster.outputToTextArea("total chemicals and cost :" + totalChem + " G / $" + totalChemCost);
		
		return events;
	}
	
	private Point findNextPoint(){
		
		if(remainingChem < 25){
			return currPoint.getNearestPointIn(refillingPoints);
		}
		
		if(!taskPoints.isEmpty()){
			Point nearest = currPoint.getNearestPointIn(taskPoints);
			
			float fuelWillRemain = remainingFuel - getFuelCostIfFlyBetween(currPoint, nearest);
			Point nearestRefillingP = nearest.getNearestPointIn(refillingPoints);

			float fuelNeeded = getFuelCostIfFlyBetween(nearest, nearestRefillingP) + FUEL_BURNING_RATE * 0.5f;
			
			if(fuelWillRemain < fuelNeeded){
				return nearestRefillingP;
			} 
			taskPoints.remove(nearest);
			return nearest;
		}
		
		return null;
	}
	
	public void addFlyingTime(int timeInMinute){
		totalFlyingTime += timeInMinute;
		addTotalTime(timeInMinute);
	}
	
	public void addTotalTime(int timeInMinute){
		totalTime += timeInMinute;
	}
	
	public void addFlyingDistance(float distance){
		totalFlyingDistance += distance;
	}
	
	private void flyToPoint(Point p){
		FlyingEvent fe = new FlyingEvent();
		fe.from = currPoint;
		fe.to = p;
		fe.fuelCost = getFuelCostIfFlyBetween(currPoint, p);
		events.add(fe);
		
		remainingFuel -= getFuelCostIfFlyBetween(currPoint, p);
		addFlyingTime((int)(Point.getDistanceBetween(currPoint, p) / FLYING_SPEED * 60));
		currPoint = p;
		
	}
	
	private float getFuelCostIfFlyBetween(Point start, Point dest){
		return Point.getDistanceBetween(start, dest) / FLYING_SPEED * FUEL_BURNING_RATE;
	}
	
	public float getRemainingFuel(){
		return remainingFuel;
	}
	
	public float getRemainingChem(){
		return remainingChem;
	}
	
	private void refillAt(RefillingPoint p){
		float fuelNeeded = MAX_FUEL - remainingFuel;
		float chemNeeded = MAX_CHEMICAL - remainingChem;
		
		RefillEvent e = new RefillEvent();
		e.where = p;
		e.fuelAdded = fuelNeeded;
		e.ChemAdded = chemNeeded;
		
		events.add(e);
		
		totalFuel += fuelNeeded;
		totalChem += chemNeeded;
		
		totalFuelCost += p.FUEL_PRICE * fuelNeeded;
		totalChemCost += p.CHEM_PRICE * chemNeeded;
		
		remainingFuel = MAX_FUEL;
		remainingChem = MAX_CHEMICAL;
		
		addTotalTime(TIME_REFILL_FUEL);
		addTotalTime(TIME_REFILL_CHEM);
	}
	
	private void dust(){
		remainingChem -= 25;
		remainingFuel -= FUEL_BURNING_RATE * 0.5f;
		addFlyingTime((int)(0.5 * 60));
		
		DustEvent e = new DustEvent();
		e.where = currPoint;
		e.fuelCost = FUEL_BURNING_RATE * 0.5f;
		e.ChemCost = 25;
		
		events.add(e);
	}
}
