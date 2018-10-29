package Model;

public class Ship {

    public String name;
    public float tonnage;
    public float maxVelocity;
    public float fuelAmount;
    public float fuelConsumptionRate;

    public Ship(String name, float tonnage, float maxVelocity, float fuelAmount, float fuelConsumptionRate) {
        this.name = name;
        this.tonnage = tonnage;
        this.maxVelocity = maxVelocity;
        this.fuelAmount = fuelAmount;
        this.fuelConsumptionRate = fuelConsumptionRate;
    }
}
