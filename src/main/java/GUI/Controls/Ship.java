package GUI.Controls;

import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;

public class Ship extends InteractiveNode {
    private double displacement;
    private double serviceSpeed;
    private double fuelAmount;
    private double fuelConsumptionRate;

    public Ship(String name, double displacement, double serviceSpeed, double fuelAmount, double fuelConsumptionRate) {
        super(name, Type.Ship);
        this.displacement = displacement;
        this.serviceSpeed = serviceSpeed;
        this.fuelAmount = fuelAmount;
        this.fuelConsumptionRate = fuelConsumptionRate;
    }

    public Ship(int id, String name, double displacement, double serviceSpeed, double fuelAmount, double fuelConsumptionRate) {
        this(name, displacement, serviceSpeed, fuelAmount, fuelConsumptionRate);
        this.id = id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDisplacement(double displacement) {
        this.displacement = displacement;
    }

    public void setServiceSpeed(double serviceSpeed) {
        this.serviceSpeed = serviceSpeed;
    }

    public void setFuelAmount(double fuelAmount) {
        this.fuelAmount = fuelAmount;
    }

    public void setFuelConsumptionRate(double fuelConsumptionRate) {
        this.fuelConsumptionRate = fuelConsumptionRate;
    }

    public int getId() {
        return id;
    }

    public double getDisplacement() {
        return displacement;
    }

    public double getServiceSpeed() {
        return serviceSpeed;
    }

    public double getFuelAmount() {
        return fuelAmount;
    }

    public double getFuelConsumptionRate() {
        return fuelConsumptionRate;
    }

    @Override
    public Tooltip getTooltip() {
        String tooltipText = String.format(
                "Tonnage: %.2f; Max velocity: %.2f; Fuel amount: %.2f",
                displacement, serviceSpeed, fuelAmount
        );
        Tooltip tooltip = new Tooltip(tooltipText);
        tooltip.setFont(new Font(24));
        return tooltip;
    }
}
