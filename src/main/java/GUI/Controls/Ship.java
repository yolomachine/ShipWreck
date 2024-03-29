package GUI.Controls;

import GUI.Controls.InteractiveNode;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;

public class Ship extends InteractiveNode {
    private double tonnage;
    private double maxVelocity;
    private double fuelAmount;
    private double fuelConsumptionRate;

    public Ship(String name, double tonnage, double maxVelocity, double fuelAmount, double fuelConsumptionRate) {
        super(name, Type.Ship);
        this.tonnage = tonnage;
        this.maxVelocity = maxVelocity;
        this.fuelAmount = fuelAmount;
        this.fuelConsumptionRate = fuelConsumptionRate;
    }

    public Ship(int id, String name, double tonnage, double maxVelocity, double fuelAmount, double fuelConsumptionRate) {
        this(name, tonnage, maxVelocity, fuelAmount, fuelConsumptionRate);
        this.id = id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTonnage(double tonnage) {
        this.tonnage = tonnage;
    }

    public void setMaxVelocity(double maxVelocity) {
        this.maxVelocity = maxVelocity;
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

    public double getTonnage() {
        return tonnage;
    }

    public double getMaxVelocity() {
        return maxVelocity;
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
                tonnage, maxVelocity, fuelAmount
        );
        Tooltip tooltip = new Tooltip(tooltipText);
        tooltip.setFont(new Font(24));
        return tooltip;
    }
}
