
public class RefillingPoint extends Point {
	
	public final int FUEL_PRICE;
	public final int CHEM_PRICE;
	
	public RefillingPoint(int x, int y, int fuelPrice, int ChemPrice) {
		super(x, y);
		FUEL_PRICE = fuelPrice;
		CHEM_PRICE = ChemPrice;
	}
	
	public void refill(Plane plane){
		plane.addTotalTime(15);//refilling fuel
		plane.addTotalTime(30);//refilling chem
		
		
	}
}
