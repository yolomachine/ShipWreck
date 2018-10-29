package Model;

public class Ship {

    public String name;
    public double tonnage;
    public double maxVelocity;
    public double fuelAmount;
    public double fuelConsumptionRate;

    public Ship(String name, double tonnage, double maxVelocity, double fuelAmount, double fuelConsumptionRate) {
        this.name = name;
        this.tonnage = tonnage;
        this.maxVelocity = maxVelocity;
        this.fuelAmount = fuelAmount;
        this.fuelConsumptionRate = fuelConsumptionRate;
    }
}
